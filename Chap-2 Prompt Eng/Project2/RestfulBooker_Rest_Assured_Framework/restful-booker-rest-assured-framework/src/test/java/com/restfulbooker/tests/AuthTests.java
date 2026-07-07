package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.models.AuthRequest;
import com.restfulbooker.utils.ResponseValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/**
 * POST /auth. Restful-booker.pdf (pages 1-2) documents: request body {username, password},
 * default credentials admin/password123, success 200 with { "token": String }.
 * The PDF does NOT document a failure response schema/status for invalid credentials, so
 * that case is asserted only on the documented contract (no "token" field) per the
 * anti-hallucination rule against inventing error codes.
 */
public class AuthTests extends BaseTest {

    @Test(groups = {"smoke", "auth"})
    public void createToken_withValidCredentials_returnsToken() {
        AuthRequest request = new AuthRequest(
                ConfigManager.getDefaultUsername(), ConfigManager.getDefaultPassword());
        Response response = authPage.createToken(request);

        ResponseValidator.assertStatusCode(response, 200);
        ResponseValidator.assertFieldPresent(response, "token");
    }

    /**
     * Insufficient information to determine the exact status code Restful-booker returns for
     * invalid credentials — the PDF has no documented negative example for /auth. Asserting
     * only the one thing the documented schema guarantees: no token is issued.
     */
    @Test(groups = {"auth", "negative"})
    public void createToken_withInvalidCredentials_returnsNoToken() {
        AuthRequest request = new AuthRequest("invalid_user", "invalid_password");
        Response response = authPage.createToken(request);

        ResponseValidator.assertFieldAbsent(response, "token");
    }
}
