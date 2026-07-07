package com.restfulbooker.constants;

/**
 * Endpoint paths as documented in Restful-booker.pdf (apidoc, generated 2025-06-11).
 */
public final class Endpoints {

    public static final String AUTH = "/auth";
    public static final String BOOKING = "/booking";
    public static final String BOOKING_BY_ID = "/booking/{id}";
    public static final String PING = "/ping";

    public static final String COOKIE_TOKEN_NAME = "token";

    private Endpoints() {
    }
}
