package com.restfulbooker.pages;

import com.restfulbooker.constants.Endpoints;
import com.restfulbooker.models.AuthRequest;
import com.restfulbooker.utils.RequestSpecFactory;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Page Object for the Auth module (POST /auth). One of the framework's two Page Objects
 * (the other is {@link BookingPage}) — wraps every Rest Assured call for this module so
 * tests never issue raw HTTP calls directly.
 */
public class AuthPage {

    /**
     * POST /auth. Header: Content-Type application/json (per Restful-booker.pdf).
     * Returns the raw Response so both positive (token present) and negative
     * (invalid credentials) cases can be asserted by the caller.
     */
    public Response createToken(AuthRequest authRequest) {
        return given()
                .spec(RequestSpecFactory.baseSpec())
                .body(authRequest)
                .when()
                .post(Endpoints.AUTH);
    }

    /**
     * Convenience wrapper for the common case: authenticate and return just the token string.
     * Fails fast (NPE-safe null) if the API did not return a token field.
     */
    public String createTokenAndGet(String username, String password) {
        Response response = createToken(new AuthRequest(username, password));
        return response.jsonPath().getString("token");
    }
}
