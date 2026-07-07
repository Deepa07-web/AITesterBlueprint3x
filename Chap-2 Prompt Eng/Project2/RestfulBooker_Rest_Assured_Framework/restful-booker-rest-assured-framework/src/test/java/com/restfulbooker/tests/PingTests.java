package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.utils.ResponseValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/**
 * GET /ping. Restful-booker.pdf shows the literal example response "HTTP/1.1 201 Created"
 * for this endpoint (page 19) — 201 is a verified fact, not an inference.
 */
public class PingTests extends BaseTest {

    @Test(groups = {"smoke", "ping"})
    public void ping_returns201() {
        Response response = bookingPage.ping();
        ResponseValidator.assertStatusCode(response, 201);
    }
}
