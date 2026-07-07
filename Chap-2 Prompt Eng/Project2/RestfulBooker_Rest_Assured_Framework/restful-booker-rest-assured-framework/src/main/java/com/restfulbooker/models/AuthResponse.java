package com.restfulbooker.models;

/**
 * Success 200 response body of POST /auth per Restful-booker.pdf: { "token": String }
 */
public class AuthResponse {

    private String token;

    public AuthResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
