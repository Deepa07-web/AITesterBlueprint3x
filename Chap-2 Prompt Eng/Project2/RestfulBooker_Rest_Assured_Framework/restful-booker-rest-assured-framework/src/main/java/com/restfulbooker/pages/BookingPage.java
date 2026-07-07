package com.restfulbooker.pages;

import com.restfulbooker.constants.Endpoints;
import com.restfulbooker.models.Booking;
import com.restfulbooker.utils.RequestSpecFactory;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Page Object for the Booking + Ping modules (GET/POST/PUT/PATCH/DELETE /booking, GET /ping).
 * One of the framework's two Page Objects (the other is {@link AuthPage}) — every Rest
 * Assured call for these endpoints lives here so test classes and any future BDD step
 * definitions share one implementation.
 */
public class BookingPage {

    /** GET /booking. Optional query params: firstname, lastname, checkin, checkout. */
    public Response getBookingIds(Map<String, String> queryParams) {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .queryParams(queryParams == null ? Map.of() : queryParams)
                .when()
                .get(Endpoints.BOOKING);
    }

    /** GET /booking/:id. Accept header controls JSON vs XML response format. */
    public Response getBookingById(int id, String acceptHeader) {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .accept(acceptHeader)
                .when()
                .get(Endpoints.BOOKING_BY_ID, id);
    }

    public Response getBookingById(int id) {
        return getBookingById(id, ContentType.JSON.toString());
    }

    /** POST /booking. Content-Type/Accept default to application/json per the base spec. */
    public Response createBooking(Booking booking) {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .body(booking)
                .when()
                .post(Endpoints.BOOKING);
    }

    /** PUT /booking/:id using the Cookie token=<value> auth alternative. */
    public Response updateBookingWithToken(int id, Booking booking, String token) {
        return given()
                .spec(RequestSpecFactory.specWithCookie(token))
                .body(booking)
                .when()
                .put(Endpoints.BOOKING_BY_ID, id);
    }

    /** PUT /booking/:id using the Authorization: Basic auth alternative. */
    public Response updateBookingWithBasicAuth(int id, Booking booking, String username, String password) {
        return given()
                .spec(RequestSpecFactory.specWithBasicAuth(username, password))
                .body(booking)
                .when()
                .put(Endpoints.BOOKING_BY_ID, id);
    }

    /** PUT /booking/:id with no auth header set — used for unauthorized-access negative tests. */
    public Response updateBookingWithoutAuth(int id, Booking booking) {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .body(booking)
                .when()
                .put(Endpoints.BOOKING_BY_ID, id);
    }

    /** PATCH /booking/:id using the Cookie token=<value> auth alternative. */
    public Response partialUpdateBookingWithToken(int id, Map<String, Object> partialFields, String token) {
        return given()
                .spec(RequestSpecFactory.specWithCookie(token))
                .body(partialFields)
                .when()
                .patch(Endpoints.BOOKING_BY_ID, id);
    }

    /** PATCH /booking/:id using the Authorization: Basic auth alternative. */
    public Response partialUpdateBookingWithBasicAuth(int id, Map<String, Object> partialFields,
                                                        String username, String password) {
        return given()
                .spec(RequestSpecFactory.specWithBasicAuth(username, password))
                .body(partialFields)
                .when()
                .patch(Endpoints.BOOKING_BY_ID, id);
    }

    /** DELETE /booking/:id using the Cookie token=<value> auth alternative. */
    public Response deleteBookingWithToken(int id, String token) {
        return given()
                .spec(RequestSpecFactory.specWithCookie(token))
                .when()
                .delete(Endpoints.BOOKING_BY_ID, id);
    }

    /** DELETE /booking/:id using the Authorization: Basic auth alternative. */
    public Response deleteBookingWithBasicAuth(int id, String username, String password) {
        return given()
                .spec(RequestSpecFactory.specWithBasicAuth(username, password))
                .when()
                .delete(Endpoints.BOOKING_BY_ID, id);
    }

    /** DELETE /booking/:id with no auth header set — used for unauthorized-access negative tests. */
    public Response deleteBookingWithoutAuth(int id) {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .when()
                .delete(Endpoints.BOOKING_BY_ID, id);
    }

    /** GET /ping — health check. */
    public Response ping() {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .when()
                .get(Endpoints.PING);
    }
}
