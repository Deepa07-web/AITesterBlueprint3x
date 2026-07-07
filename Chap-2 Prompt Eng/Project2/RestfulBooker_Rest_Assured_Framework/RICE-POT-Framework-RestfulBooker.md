# RICE-POT Framework Design — Restful-Booker API Automation

**Tech Stack:** Java, Rest Assured, TestNG, Cucumber BDD, Maven, Jenkins/GitLab CI
**Target API:** https://restful-booker.herokuapp.com
**Author:** Deepa | Senior QA Automation Engineer
**Purpose:** Design reference for Claude Code to scaffold a keyword-driven API automation framework

---

## 1. Overview

This framework automates the Restful-Booker API using the RICE-POT methodology — a design philosophy ensuring the framework is **Reusable, Isolated, Centralized, Efficient, Portable, Orchestrated, and Thorough**. It uses a **keyword-driven approach** on top of Rest Assured so that test scenarios can be composed from reusable action keywords rather than repeated raw HTTP code.

---

## 2. API Scope (from Restful-Booker documentation)

| Module | Endpoint | Method | Purpose |
|---|---|---|---|
| Auth | `/auth` | POST | Generate auth token |
| Booking | `/booking` | GET | Get all booking IDs (supports filters: firstname, lastname, checkin, checkout) |
| Booking | `/booking/:id` | GET | Get a specific booking (JSON/XML) |
| Booking | `/booking` | POST | Create a new booking |
| Booking | `/booking/:id` | PUT | Full update (requires token/basic auth) |
| Booking | `/booking/:id` | PATCH | Partial update (requires token/basic auth) |
| Booking | `/booking/:id` | DELETE | Delete booking (requires token/basic auth) |
| Ping | `/ping` | GET | Health check |

---

## 3. RICE-POT Principle Mapping

### R — Reusable
- Common **request builder / RequestSpecification** factory (base URI, headers, content type) built once, reused across all calls.
- **POJO models** for `Booking`, `BookingDates`, `AuthRequest`, `AuthResponse` used for both serialization (POST/PUT/PATCH bodies) and deserialization (response parsing).
- **Keyword library** (e.g. `createBooking()`, `getBookingById()`, `updateBooking()`, `deleteBooking()`, `authenticate()`) callable from both TestNG tests and Cucumber step definitions — no duplicated HTTP logic.
- Reusable **assertion utilities** (status code check, schema validation, response time check).

### I — Isolated
- Every test creates its **own booking data** (unique firstname/lastname/dates via test data generator) rather than depending on a fixed booking ID from another test.
- No test depends on execution order; each test does its own setup (create booking) and teardown (delete booking) where relevant.
- Auth token fetched fresh per test class/suite (not hardcoded), avoiding token-expiry flakiness.

### C — Centralized
- **Config file** (`config.properties` or `application.yml`) holding base URI, environment name, default credentials, timeouts.
- **Constants class** for endpoint paths (`/auth`, `/booking`, `/booking/{id}`, `/ping`).
- **Base Test class** centralizing RequestSpecification/ResponseSpecification setup, logging, and reporting hooks.
- **Centralized test data factory** for generating valid/invalid Booking payloads.

### E — Efficient
- **TestNG parallel execution** configured via `testng.xml` (parallel="methods"/"classes", thread-count).
- **DataProviders** for data-driven negative/boundary testing instead of duplicated test methods.
- Reuse of a single auth token across a test class where valid, instead of re-authenticating per test.
- Minimal, targeted assertions (avoid unnecessary full-payload comparisons where a field-level check suffices).

### P — Portable
- **Environment-agnostic base URI** — driven by config/Maven profile (e.g. `-Denv=qa`), not hardcoded.
- No machine-specific file paths; all resources (test data, schemas) referenced relative to project root/classpath.
- Framework runnable identically from local machine, VS Code, and CI/CD agents.

### O — Orchestrated
- **TestNG suite XML** grouping tests by module (Auth, Booking-CRUD, Booking-Negative, Ping) with defined execution groups.
- **Cucumber Runner** (if BDD layer used) tagging scenarios (`@auth`, `@booking`, `@smoke`, `@regression`).
- **CI/CD pipeline** (Jenkins/GitLab CI) stage definitions: build → smoke suite → full regression → report publish.
- **Extent/Allure reporting** integrated into the run for orchestrated visibility of results.

### T — Thorough
Coverage across positive, negative, and boundary scenarios per endpoint:

- **Auth:** valid login → token returned; invalid username/password → no token; missing fields.
- **GetBookingIds:** all IDs returned; filter by firstname/lastname; filter by checkin/checkout; filter with no matches.
- **GetBooking:** valid ID → correct schema/fields; invalid/non-existent ID → 404; XML vs JSON Accept header handling.
- **CreateBooking:** valid payload (JSON, XML, URL-encoded) → 200 + bookingid; missing required fields; invalid data types; boundary values (price = 0, very large price, past/invalid dates).
- **UpdateBooking (PUT):** valid full update with token; valid full update with Basic auth; unauthorized (no token) → 403/401; non-existent ID.
- **PartialUpdateBooking (PATCH):** valid partial update; unauthorized attempt; invalid field values.
- **DeleteBooking:** valid delete with token; valid delete with Basic auth; unauthorized delete attempt; delete of already-deleted/non-existent ID.
- **Ping:** health check returns 201.
- **Schema validation** on all GET/POST/PUT/PATCH responses.
- **Negative/security checks:** SQL-injection-style payloads in string fields, oversized payloads, wrong content-type headers.

---

## 4. Proposed Folder Structure

```
restful-booker-rest-assured-framework/
├── src/
│   ├── main/java/
│   │   ├── config/           # Config loader (env, base URI, credentials)
│   │   ├── constants/        # Endpoint paths, headers
│   │   ├── models/           # POJOs: Booking, BookingDates, AuthRequest, AuthResponse
│   │   ├── keywords/         # Reusable action methods (createBooking, authenticate, etc.)
│   │   └── utils/            # RequestSpec factory, response validators, test data generator
│   └── test/java/
│       ├── base/             # BaseTest (setup/teardown, spec builders)
│       ├── tests/            # TestNG test classes (Auth, Booking CRUD, Negative, Ping)
│       └── stepdefinitions/  # Cucumber step defs (if BDD layer included)
│   └── test/resources/
│       ├── features/         # .feature files (if Cucumber used)
│       ├── testdata/         # JSON test data files
│       └── config/           # config.properties per environment
├── testng.xml
├── pom.xml
└── README.md
```

---

## 5. What This Document Includes (for your reference)

- ✅ Full endpoint inventory extracted from the Restful-Booker API docs
- ✅ RICE-POT principle-by-principle mapping to concrete framework decisions
- ✅ Suggested folder/package structure for a keyword-driven Rest Assured framework
- ✅ Thoroughness checklist of positive/negative/boundary scenarios per endpoint

## What's NOT included yet (let me know if you want these added)
- ❌ Actual POJO class code
- ❌ Actual Rest Assured keyword method implementations
- ❌ pom.xml dependency list
- ❌ testng.xml content
- ❌ Sample .feature files / step definitions
- ❌ CI/CD pipeline YAML

---

*This document is intended to be placed in the project root or `docs/` folder and referenced by Claude Code when scaffolding the framework.*
