package com.restfulbooker.utils;

import io.restassured.response.Response;
import org.testng.Assert;

/**
 * Reusable assertion helpers so keyword/page-object callers and test classes don't repeat
 * raw Assert.assertEquals boilerplate.
 */
public final class ResponseValidator {

    private ResponseValidator() {
    }

    public static void assertStatusCode(Response response, int expectedStatusCode) {
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Unexpected status code. Body: " + response.getBody().asPrettyString());
    }

    public static void assertResponseTimeUnder(Response response, long maxMillis) {
        long actual = response.getTime();
        Assert.assertTrue(actual <= maxMillis,
                "Response took " + actual + "ms, expected <= " + maxMillis + "ms");
    }

    public static void assertFieldPresent(Response response, String jsonPath) {
        Assert.assertNotNull(response.jsonPath().get(jsonPath),
                "Expected field '" + jsonPath + "' to be present in response: "
                        + response.getBody().asPrettyString());
    }

    public static void assertFieldAbsent(Response response, String jsonPath) {
        Assert.assertNull(response.jsonPath().get(jsonPath),
                "Expected field '" + jsonPath + "' to be absent from response: "
                        + response.getBody().asPrettyString());
    }
}
