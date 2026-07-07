package com.restfulbooker.utils;

import com.restfulbooker.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Builds RequestSpecification instances once and reuses them across all keyword/page-object
 * calls so base URI, content type, and logging config live in a single place.
 */
public final class RequestSpecFactory {

    private RequestSpecFactory() {
    }

    public static RequestSpecification baseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getBaseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(LogDetail.URI)
                .build();
    }

    public static RequestSpecification specWithCookie(String cookieToken) {
        return new RequestSpecBuilder()
                .addRequestSpecification(baseSpec())
                .addCookie("token", cookieToken)
                .build();
    }

    public static RequestSpecification specWithBasicAuth(String username, String password) {
        return new RequestSpecBuilder()
                .addRequestSpecification(baseSpec())
                .setAuth(RestAssured.preemptive().basic(username, password))
                .build();
    }
}
