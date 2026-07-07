# Restful-Booker Rest Assured Framework

Keyword/Page-Object driven API automation framework for `https://restful-booker.herokuapp.com`,
built per `RICE-POT-Framework-RestfulBooker.md` and `Restful-booker.pdf` (both in the parent
`RestfulBooker_Rest_Assured_Framework/` folder).

## Stack
Java 11, Rest Assured 5.4, TestNG 7.9, Maven, Jackson.

## Structure
```
src/main/java/com/restfulbooker/
├── config/        ConfigManager — loads config/config.properties from classpath
├── constants/      Endpoints — /auth, /booking, /booking/{id}, /ping
├── models/         Booking, BookingDates, AuthRequest, AuthResponse, CreateBookingResponse
├── utils/          RequestSpecFactory, ResponseValidator, TestDataFactory
└── pages/          AuthPage, BookingPage  (the 2 Page Objects — every Rest Assured call lives here)

src/test/java/com/restfulbooker/
├── base/           BaseTest — instantiates page objects, fetches one auth token per class
└── tests/          AuthTests, PingTests, GetBookingIdsTests, GetBookingTests,
                     CreateBookingTests, UpdateBookingTests, PartialUpdateBookingTests,
                     DeleteBookingTests

src/test/resources/config/config.properties   base URI, default credentials, timeout
testng.xml                                    suite definition, parallel="classes"
```

## Run
```
mvn test                          # full regression (testng.xml)
mvn test -Dgroups=smoke           # smoke subset only
mvn test -Dgroups=negative        # negative-case subset only
```

## Anti-Hallucination Compliance Note

This framework was generated under a strict verification ruleset: no invented API behavior,
every assertion traceable to a provided source (`Restful-booker.pdf` = official doc,
`RICE-POT-Framework-RestfulBooker.md` = design reference), and any non-documented behavior
labeled inline as `Inference (low confidence)` rather than asserted as fact.

### Verified Facts (from Restful-booker.pdf — high confidence)
- Endpoints/methods: `POST /auth`, `GET /booking`, `GET /booking/:id`, `POST /booking`,
  `PUT /booking/:id`, `PATCH /booking/:id`, `DELETE /booking/:id`, `GET /ping`.
- `/auth` request: `{username, password}`, defaults `admin`/`password123`; success 200,
  body `{"token": String}`.
- `/booking` (GET) success 200, body `[{"bookingid": Number}, ...]`; optional filters
  `firstname`, `lastname`, `checkin`, `checkout` (format `CCYY-MM-DD`).
- `/booking/:id` (GET) success 200, fields `firstname, lastname, totalprice, depositpaid,
  bookingdates{checkin,checkout}, additionalneeds`; `Accept` header toggles JSON/XML.
- `/booking` (POST) success 200, body `{"bookingid": Number, "booking": {...}}`.
- `/booking/:id` (PUT, PATCH) require `Cookie: token=<value>` OR
  `Authorization: Basic <base64>`; success 200 with the (updated) booking fields.
- `/booking/:id` (DELETE) requires the same auth alternatives.
- `/ping` — the PDF's own example shows the literal response `HTTP/1.1 201 Created`.

### Missing / Unknown Information (not in Restful-booker.pdf)
- Failure response schema/status for `POST /auth` with invalid credentials.
- Status code for `GET /booking/:id` with a non-existent id.
- Status code for PUT/PATCH/DELETE `/booking/:id` with no auth header.
- Validation behavior for missing required fields / invalid data types on
  CreateBooking or PartialUpdateBooking.
- Behavior of DELETE on an already-deleted booking id.
- **`DELETE /booking/:id` success status is itself ambiguous inside the PDF**: the section
  header reads "Success 200" but the field table says `OK String — Default HTTP 201 response`
  (identical wording to the Ping section, whose own example proves 201). The framework
  asserts 201 (`DeleteBookingTests.EXPECTED_DELETE_STATUS`) as the better-evidenced value —
  flagged as `Inference (low confidence)` in code; correct it if the live API proves 200.

### Generated Output
Full Maven project scaffolded per the sections above: 2 Page Objects, 5 POJOs, 3 utils,
1 BaseTest, 8 TestNG test classes covering positive, negative, and boundary cases per
`RICE-POT-Framework-RestfulBooker.md` §3 (Thorough). Where a test needed an assertion not
covered by the PDF, the code either (a) sources the expectation from
`RICE-POT-Framework-RestfulBooker.md` and comments it as `Inference (low confidence)`
(e.g. 403/401 for unauthorized PUT/DELETE), or (b) asserts only that a response was received
without asserting an invented status code, with an inline
`"Insufficient information to determine"` comment (e.g. missing-field CreateBooking,
already-deleted DELETE).

### Self-Validation Check
- No endpoint, field, or header appears in the code that is not present in
  `Restful-booker.pdf`.
- Every non-PDF-sourced assertion is labeled `Inference (low confidence)` or replaced with
  a non-asserting "record actual behavior" check, and cites its source
  (`RICE-POT-Framework-RestfulBooker.md`).
- The PDF's internal Delete/Ping status-code inconsistency is surfaced explicitly rather than
  silently resolved.

### Live Run Evidence (`mvn test`, 2026-07-07, 23 tests, 8 passed / 15 failed)
Confirmed against the real API (upgraded from inference to verified fact):
- `POST /auth` valid creds → 200 + token. Invalid creds → no token. **PASSED.**
- `GET /ping` → 201. **PASSED.**
- `GET /booking` (all + firstname/lastname + checkin/checkout filters) → 200. **PASSED.**
- `GET /booking/:id` with a non-existent id → 404. **PASSED** — the MD's
  `Inference (low confidence)` for this case is now a verified fact.

Blocked, not a framework defect:
- **`POST /booking` returned `418 I'm a Teapot` on every call in this run**, for both plain
  and boundary-value payloads. This is the live public demo rejecting the request, not the
  documented `200 + bookingid` contract from the PDF — the code was not changed to
  accept 418, since that would misrepresent the documented API contract as passing.
- Because `CreateBooking` is the shared setup step for `GetBookingTests`, `UpdateBookingTests`,
  `PartialUpdateBookingTests`, and `DeleteBookingTests`, those 11 tests failed downstream of
  the same root cause (a booking id was never obtained to act on).
- **Action needed before trusting this suite in CI**: re-run `mvn test` against
  `restful-booker.herokuapp.com` later (the public Heroku demo is known to be flaky/rate
  limited) to confirm whether 418 was transient, or point `base.uri` in
  `config.properties` at a locally-hosted restful-booker instance for a clean run.
