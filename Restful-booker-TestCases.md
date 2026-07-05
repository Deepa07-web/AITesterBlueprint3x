# Restful-Booker API — Test Cases (Jira Format)

**Base URL:** `https://restful-booker.herokuapp.com`
**Executed on:** 2026-07-04
**Executed by:** QA (automated curl execution against live sandbox)
**Endpoints covered:** Auth, Booking (GET/GET-list/POST/PUT/PATCH/DELETE), Ping

> All "Actual Result" fields below were captured from real HTTP calls against the live API during this session — not assumed. 4 defects were found; see **Defect Summary** at the bottom.

---

## Module: Auth

### TC-001 — Create Token with valid credentials (Happy Path)
- **Priority:** High
- **Type:** Functional
- **Preconditions:** None
- **Test Data:** `{"username":"admin","password":"password123"}`
- **Steps to Reproduce:**
  1. Send `POST /auth` with header `Content-Type: application/json`
  2. Body: `{"username":"admin","password":"password123"}`
- **Expected Result:** HTTP 200, response body contains a `token` field with a non-empty string value.
- **Actual Result:** HTTP 200 OK — `{"token":"3fd47fd3cd032d3"}`
- **Status:** ✅ Pass

### TC-002 — Create Token with invalid password (Negative)
- **Priority:** High
- **Type:** Negative
- **Preconditions:** None
- **Test Data:** `{"username":"admin","password":"wrongpass"}`
- **Steps to Reproduce:**
  1. Send `POST /auth` with header `Content-Type: application/json`
  2. Body: `{"username":"admin","password":"wrongpass"}`
- **Expected Result:** Request rejected — ideally HTTP 401/400 with an error reason.
- **Actual Result:** HTTP **200 OK** — `{"reason":"Bad credentials"}` (success status code returned for a failed auth attempt)
- **Status:** ⚠️ Pass (functionally correct) / **Design concern** — 200 status code on auth failure is non-standard; see Defect Summary D1.

### TC-003 — Create Token with missing password field (Negative)
- **Priority:** Medium
- **Type:** Negative
- **Preconditions:** None
- **Test Data:** `{"username":"admin"}`
- **Steps to Reproduce:**
  1. Send `POST /auth` with header `Content-Type: application/json`
  2. Body: `{"username":"admin"}` (no `password` key)
- **Expected Result:** HTTP 400 Bad Request, indicating a missing required field.
- **Actual Result:** HTTP **200 OK** — `{"reason":"Bad credentials"}`
- **Status:** ⚠️ Pass (no crash) / Same design concern as TC-002.

### TC-004 — Create Token with empty JSON body (Edge Case)
- **Priority:** Low
- **Type:** Edge
- **Preconditions:** None
- **Test Data:** `{}`
- **Steps to Reproduce:**
  1. Send `POST /auth` with header `Content-Type: application/json`
  2. Body: `{}`
- **Expected Result:** HTTP 400 Bad Request for empty payload.
- **Actual Result:** HTTP **200 OK** — `{"reason":"Bad credentials"}`
- **Status:** ⚠️ Pass (no crash) / Same design concern as TC-002.

---

## Module: Booking — GetBookingIds

### TC-005 — Get all booking IDs (Happy Path)
- **Priority:** High
- **Type:** Functional
- **Preconditions:** None
- **Steps to Reproduce:**
  1. Send `GET /booking`
- **Expected Result:** HTTP 200, JSON array of objects each containing a `bookingid` number.
- **Actual Result:** HTTP 200 OK — JSON array returned (15,088 bytes, hundreds of `{"bookingid": N}` entries confirmed).
- **Status:** ✅ Pass

### TC-006 — Filter by a name that does not exist (Edge Case)
- **Priority:** Medium
- **Type:** Edge
- **Preconditions:** None
- **Test Data:** `firstname=NoSuchPerson12345&lastname=NoSuchLast`
- **Steps to Reproduce:**
  1. Send `GET /booking?firstname=NoSuchPerson12345&lastname=NoSuchLast`
- **Expected Result:** HTTP 200, empty array `[]` (no matching bookings, no error).
- **Actual Result:** HTTP 200 OK — `[]`
- **Status:** ✅ Pass

### TC-007 — Filter by checkin/checkout date range (Happy Path)
- **Priority:** Medium
- **Type:** Functional
- **Preconditions:** At least one booking exists with matching dates (booking created in TC-012 with checkin 2026-08-01 / checkout 2026-08-05).
- **Steps to Reproduce:**
  1. Send `GET /booking?checkin=2026-08-01&checkout=2026-08-05`
- **Expected Result:** HTTP 200, array containing the `bookingid` of matching booking(s).
- **Actual Result:** HTTP 200 OK — `[{"bookingid":1402}]`
- **Status:** ✅ Pass

---

## Module: Booking — GetBooking

### TC-008 — Get booking by valid ID (Happy Path)
- **Priority:** High
- **Type:** Functional
- **Preconditions:** Booking ID exists (e.g., booking created in TC-012, id `1309`, prior to deletion).
- **Steps to Reproduce:**
  1. Send `GET /booking/1309`
- **Expected Result:** HTTP 200, full booking object (`firstname`, `lastname`, `totalprice`, `depositpaid`, `bookingdates`, `additionalneeds`).
- **Actual Result:** HTTP 200 OK — `{"bookingid":1309,"booking":{"firstname":"QA","lastname":"Tester",...}}` matched created data.
- **Status:** ✅ Pass

### TC-009 — Get booking with non-existent ID (Negative)
- **Priority:** High
- **Type:** Negative
- **Test Data:** `id=999999`
- **Steps to Reproduce:**
  1. Send `GET /booking/999999`
- **Expected Result:** HTTP 404 Not Found.
- **Actual Result:** HTTP 404 Not Found — body `Not Found`
- **Status:** ✅ Pass

### TC-010 — Get booking with non-numeric ID (Negative)
- **Priority:** Medium
- **Type:** Negative
- **Test Data:** `id=abc`
- **Steps to Reproduce:**
  1. Send `GET /booking/abc`
- **Expected Result:** HTTP 400 Bad Request (invalid id format) or 404.
- **Actual Result:** HTTP 404 Not Found — body `Not Found` (treated as unresolvable id rather than a format error).
- **Status:** ✅ Pass (acceptable, no crash)

### TC-011 — Get booking with `Accept: application/xml` header (Happy Path / Edge)
- **Priority:** Low
- **Type:** Functional
- **Preconditions:** Booking id exists (`1402`).
- **Steps to Reproduce:**
  1. Send `GET /booking/1402` with header `Accept: application/xml`
- **Expected Result:** HTTP 200, response body as well-formed XML `<booking>` document.
- **Actual Result:** HTTP 200 OK, `Content-Type: text/html` (not `application/xml`), body is valid XML:
  ```xml
  <?xml version='1.0'?>
  <booking>
      <firstname>QA</firstname>
      <lastname>Tester</lastname>
      <totalprice>NaN</totalprice>
      ...
  </booking>
  ```
- **Status:** ⚠️ Pass (content correct) / **Defect** — `Content-Type` header returned is `text/html`, not `application/xml` as documented; also `totalprice` serialized as `NaN` (see D3, carried over from TC-014's bad data).

---

## Module: Booking — CreateBooking

### TC-012 — Create booking with valid JSON payload (Happy Path)
- **Priority:** High
- **Type:** Functional
- **Test Data:**
  ```json
  {
    "firstname":"QA","lastname":"Tester","totalprice":150,
    "depositpaid":true,
    "bookingdates":{"checkin":"2026-08-01","checkout":"2026-08-05"},
    "additionalneeds":"Breakfast"
  }
  ```
- **Steps to Reproduce:**
  1. Send `POST /booking` with header `Content-Type: application/json` and the payload above.
- **Expected Result:** HTTP 200, response contains a new `bookingid` and echoes the submitted `booking` object.
- **Actual Result:** HTTP 200 OK — `{"bookingid":1309,"booking":{"firstname":"QA","lastname":"Tester","totalprice":150,"depositpaid":true,"bookingdates":{"checkin":"2026-08-01","checkout":"2026-08-05"},"additionalneeds":"Breakfast"}}`
- **Status:** ✅ Pass

### TC-013 — Create booking with missing required fields (Negative)
- **Priority:** High
- **Type:** Negative
- **Test Data:** `{"firstname":"QA"}`
- **Steps to Reproduce:**
  1. Send `POST /booking` with header `Content-Type: application/json`
  2. Body: `{"firstname":"QA"}` (missing `lastname`, `totalprice`, `depositpaid`, `bookingdates`)
- **Expected Result:** HTTP 400 Bad Request, with validation message naming the missing fields.
- **Actual Result:** HTTP **500 Internal Server Error** — body `Internal Server Error`
- **Status:** ❌ **Fail** — server error instead of graceful validation. See Defect Summary D2.

### TC-014 — Create booking with invalid `totalprice` type and checkout date before checkin date (Edge Case)
- **Priority:** Medium
- **Type:** Edge
- **Test Data:**
  ```json
  {
    "firstname":"QA","lastname":"Tester","totalprice":"not-a-number",
    "depositpaid":true,
    "bookingdates":{"checkin":"2026-08-05","checkout":"2026-08-01"},
    "additionalneeds":"Breakfast"
  }
  ```
- **Steps to Reproduce:**
  1. Send `POST /booking` with header `Content-Type: application/json` and the payload above (note: `totalprice` is a string, and `checkout` is earlier than `checkin`).
- **Expected Result:** HTTP 400 Bad Request — reject non-numeric `totalprice` and/or reject `checkout` earlier than `checkin`.
- **Actual Result:** HTTP **200 OK** — booking created (`bookingid":1402`), `totalprice` silently coerced to `null` (`NaN` when rendered as XML in TC-011), and the invalid date order was accepted without complaint.
- **Status:** ❌ **Fail** — no type or business-rule validation. See Defect Summary D3.

---

## Module: Booking — UpdateBooking (PUT)

### TC-015 — Update booking without auth token (Negative)
- **Priority:** High
- **Type:** Security/Negative
- **Preconditions:** Booking id `1309` exists.
- **Steps to Reproduce:**
  1. Send `PUT /booking/1309` with header `Content-Type: application/json`, **no** `Cookie`/`Authorization` header, and a valid body.
- **Expected Result:** HTTP 403 Forbidden (auth required).
- **Actual Result:** HTTP 403 Forbidden — body `Forbidden`
- **Status:** ✅ Pass

### TC-016 — Update booking with valid token (Happy Path)
- **Priority:** High
- **Type:** Functional
- **Preconditions:** Valid token obtained from TC-001 (`3fd47fd3cd032d3`); booking id `1309` exists.
- **Test Data:**
  ```json
  {
    "firstname":"QAUpdated","lastname":"Tester","totalprice":200,
    "depositpaid":false,
    "bookingdates":{"checkin":"2026-09-01","checkout":"2026-09-10"},
    "additionalneeds":"Lunch"
  }
  ```
- **Steps to Reproduce:**
  1. Send `PUT /booking/1309` with headers `Content-Type: application/json`, `Accept: application/json`, `Cookie: token=3fd47fd3cd032d3`, and the payload above.
- **Expected Result:** HTTP 200, response body reflects all updated fields.
- **Actual Result:** HTTP 200 OK — `{"firstname":"QAUpdated","lastname":"Tester","totalprice":200,"depositpaid":false,"bookingdates":{"checkin":"2026-09-01","checkout":"2026-09-10"},"additionalneeds":"Lunch"}`
- **Status:** ✅ Pass

### TC-017 — Update a non-existent booking ID (Negative)
- **Priority:** Medium
- **Type:** Negative
- **Preconditions:** Valid token; id `999999` does not exist.
- **Steps to Reproduce:**
  1. Send `PUT /booking/999999` with a valid token and a valid body.
- **Expected Result:** HTTP 404 Not Found.
- **Actual Result:** HTTP **405 Method Not Allowed** — body `Method Not Allowed`
- **Status:** ⚠️ **Fail (wrong status code)** — returns 405 instead of 404 for a non-existent resource. See Defect Summary D4.

---

## Module: Booking — PartialUpdateBooking (PATCH)

### TC-018 — Partial update with valid token (Happy Path)
- **Priority:** Medium
- **Type:** Functional
- **Preconditions:** Valid token; booking id `1309` exists (post TC-016 update).
- **Test Data:** `{"firstname":"Patched"}`
- **Steps to Reproduce:**
  1. Send `PATCH /booking/1309` with headers `Content-Type: application/json`, `Cookie: token=3fd47fd3cd032d3`
  2. Body: `{"firstname":"Patched"}`
- **Expected Result:** HTTP 200, only `firstname` changes; all other fields remain as previously set.
- **Actual Result:** HTTP 200 OK — `{"firstname":"Patched","lastname":"Tester","totalprice":200,"depositpaid":false,"bookingdates":{"checkin":"2026-09-01","checkout":"2026-09-10"},"additionalneeds":"Lunch"}` — only `firstname` changed, rest preserved.
- **Status:** ✅ Pass

---

## Module: Booking — DeleteBooking

### TC-019 — Delete booking without auth (Negative)
- **Priority:** High
- **Type:** Security/Negative
- **Preconditions:** Booking id `1309` exists.
- **Steps to Reproduce:**
  1. Send `DELETE /booking/1309` with no `Cookie`/`Authorization` header.
- **Expected Result:** HTTP 403 Forbidden.
- **Actual Result:** HTTP 403 Forbidden — body `Forbidden`
- **Status:** ✅ Pass

### TC-020 — Delete booking with valid token (Happy Path)
- **Priority:** High
- **Type:** Functional
- **Preconditions:** Valid token; booking id `1309` exists.
- **Steps to Reproduce:**
  1. Send `DELETE /booking/1309` with header `Cookie: token=3fd47fd3cd032d3`
- **Expected Result:** HTTP 201 Created (per API doc's documented success code for this endpoint).
- **Actual Result:** HTTP 201 Created — body `Created`
- **Status:** ✅ Pass

### TC-021 — Delete an already-deleted booking ID (Edge Case)
- **Priority:** Medium
- **Type:** Edge
- **Preconditions:** Booking id `1309` was just deleted in TC-020.
- **Steps to Reproduce:**
  1. Repeat `DELETE /booking/1309` with the same valid token.
- **Expected Result:** HTTP 404 Not Found (resource no longer exists).
- **Actual Result:** HTTP **405 Method Not Allowed** — body `Method Not Allowed`
- **Status:** ⚠️ **Fail (wrong status code)** — same D4 pattern as TC-017.

### TC-022 — Delete booking with invalid/garbage auth token (Negative)
- **Priority:** Medium
- **Type:** Security/Negative
- **Preconditions:** Booking id `1402` exists.
- **Steps to Reproduce:**
  1. Send `DELETE /booking/1402` with header `Cookie: token=invalid-garbage-token`
- **Expected Result:** HTTP 403 Forbidden, distinguishing an invalid token from a missing one (ideally via message or logging).
- **Actual Result:** HTTP 403 Forbidden — body `Forbidden` (identical response to the no-token case in TC-019; cannot distinguish "missing" from "invalid" token from the response alone).
- **Status:** ✅ Pass (secure — request correctly rejected) / Minor observability gap, not a functional defect.

### TC-023 — Get booking after deletion confirms removal (Edge Case)
- **Priority:** Low
- **Type:** Regression/Edge
- **Preconditions:** Booking id `1309` was deleted in TC-020.
- **Steps to Reproduce:**
  1. Send `GET /booking/1309`
- **Expected Result:** HTTP 404 Not Found.
- **Actual Result:** HTTP 404 Not Found — body `Not Found`
- **Status:** ✅ Pass

---

## Module: Ping

### TC-024 — Health check (Happy Path)
- **Priority:** Low
- **Type:** Functional/Smoke
- **Steps to Reproduce:**
  1. Send `GET /ping`
- **Expected Result:** HTTP 201 Created (per documented default response for this endpoint).
- **Actual Result:** HTTP 201 Created — body `Created`
- **Status:** ✅ Pass

---

## Defect Summary

| ID | Endpoint | Summary | Severity |
|----|----------|---------|----------|
| D1 | `POST /auth` | Failed authentication (wrong password, missing field, empty body) returns **HTTP 200** with `{"reason":"Bad credentials"}` instead of 400/401. Clients that check status codes only will treat auth failures as success. | Medium |
| D2 | `POST /booking` | Submitting a payload with missing required fields (e.g. only `firstname`) causes **HTTP 500 Internal Server Error** instead of a 400 validation error. | High |
| D3 | `POST /booking` | No type validation on `totalprice` (string accepted, silently coerced to `null`/`NaN`) and no business-rule validation that `checkout` must be after `checkin`. Booking created successfully with corrupted data. | High |
| D4 | `PUT /booking/:id`, `DELETE /booking/:id` | Operating on a non-existent or already-deleted booking id returns **HTTP 405 Method Not Allowed** instead of 404 Not Found, misleading clients into thinking the HTTP verb itself is unsupported. | Medium |

## Coverage Notes
- All 8 documented endpoints exercised: Auth-CreateToken, Booking-GetBookingIds, Booking-GetBooking, Booking-CreateBooking, Booking-UpdateBooking, Booking-PartialUpdateBooking, Booking-DeleteBooking, Ping-HealthCheck.
- Not covered in this pass (recommend follow-up): XML/urlencoded request bodies for CreateBooking/UpdateBooking, Basic-auth variant (vs Cookie) for PUT/DELETE, pagination/large-payload limits on GetBookingIds, concurrent update/delete race conditions.
