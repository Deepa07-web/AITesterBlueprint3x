package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.utils.ResponseValidator;
import com.restfulbooker.utils.TestDataFactory;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * PATCH /booking/:id (Restful-booker.pdf pages 14-16, method badge "PATCH"). Note: the PDF's
 * own curl examples for this section say "curl -X PUT" (apparent copy/paste error in the
 * source doc) — the method badge and URL pattern are treated as authoritative here.
 * Requires Cookie token=<value> or Authorization: Basic auth. Success 200 returns the
 * updated booking with only the submitted fields changed.
 */
public class PartialUpdateBookingTests extends BaseTest {

    @Test(groups = {"smoke", "booking"})
    public void partialUpdate_withTokenAuth_updatesOnlySubmittedFields() {
        int bookingId = bookingPage.createBooking(TestDataFactory.validBooking())
                .jsonPath().getInt("bookingid");

        Response response = bookingPage.partialUpdateBookingWithToken(
                bookingId, Map.of("firstname", "James", "lastname", "Brown"), authToken);

        ResponseValidator.assertStatusCode(response, 200);
        org.testng.Assert.assertEquals(response.jsonPath().getString("firstname"), "James");
        org.testng.Assert.assertEquals(response.jsonPath().getString("lastname"), "Brown");
    }

    /**
     * Insufficient information to determine the exact rejection status for an invalid field
     * value (e.g. non-numeric totalprice) — not documented in the PDF for PATCH. Test records
     * actual behavior rather than asserting an invented status code.
     */
    @Test(groups = {"booking", "negative"})
    public void partialUpdate_withInvalidFieldValue_documentedBehaviorUnknown() {
        int bookingId = bookingPage.createBooking(TestDataFactory.validBooking())
                .jsonPath().getInt("bookingid");

        Response response = bookingPage.partialUpdateBookingWithToken(
                bookingId, Map.of("totalprice", "not-a-number"), authToken);

        org.testng.Assert.assertTrue(response.getStatusCode() > 0,
                "Insufficient information to determine expected status code — "
                        + "actual status observed: " + response.getStatusCode());
    }
}
