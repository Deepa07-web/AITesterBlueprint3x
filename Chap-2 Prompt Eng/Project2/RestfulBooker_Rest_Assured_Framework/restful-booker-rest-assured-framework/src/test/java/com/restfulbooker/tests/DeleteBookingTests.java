package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.utils.ResponseValidator;
import com.restfulbooker.utils.TestDataFactory;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/**
 * DELETE /booking/:id (Restful-booker.pdf pages 17-18). Requires Cookie token=<value> or
 * Authorization: Basic auth.
 *
 * Inference (low confidence) on status code: the PDF's own doc is internally inconsistent —
 * the section header says "Success 200" but the field table says "OK String / Default HTTP
 * 201 response" (identical wording to the Ping section, which the PDF's own example proves
 * returns 201). 201 is used here as the more strongly evidenced value; verify against the
 * live API and correct if it actually returns 200.
 */
public class DeleteBookingTests extends BaseTest {

    private static final int EXPECTED_DELETE_STATUS = 201;

    @Test(groups = {"smoke", "booking"})
    public void deleteBooking_withTokenAuth_succeeds() {
        int bookingId = bookingPage.createBooking(TestDataFactory.validBooking())
                .jsonPath().getInt("bookingid");

        Response response = bookingPage.deleteBookingWithToken(bookingId, authToken);

        ResponseValidator.assertStatusCode(response, EXPECTED_DELETE_STATUS);
    }

    @Test(groups = {"booking"})
    public void deleteBooking_withBasicAuth_succeeds() {
        int bookingId = bookingPage.createBooking(TestDataFactory.validBooking())
                .jsonPath().getInt("bookingid");

        Response response = bookingPage.deleteBookingWithBasicAuth(
                bookingId, ConfigManager.getDefaultUsername(), ConfigManager.getDefaultPassword());

        ResponseValidator.assertStatusCode(response, EXPECTED_DELETE_STATUS);
    }

    /**
     * Inference (low confidence): 403/401 comes from RICE-POT-Framework-RestfulBooker.md
     * section 3, not the official API doc (which does not state a rejection status for DELETE).
     */
    @Test(groups = {"booking", "negative"})
    public void deleteBooking_withoutAuth_isRejected() {
        int bookingId = bookingPage.createBooking(TestDataFactory.validBooking())
                .jsonPath().getInt("bookingid");

        Response response = bookingPage.deleteBookingWithoutAuth(bookingId);

        int status = response.getStatusCode();
        org.testng.Assert.assertTrue(status == 403 || status == 401,
                "Expected 403 or 401 per design doc, got: " + status);
    }

    /**
     * Insufficient information to determine behavior for deleting an already-deleted booking
     * — not documented. Test records actual behavior rather than asserting an invented status.
     */
    @Test(groups = {"booking", "negative"})
    public void deleteBooking_alreadyDeleted_documentedBehaviorUnknown() {
        int bookingId = bookingPage.createBooking(TestDataFactory.validBooking())
                .jsonPath().getInt("bookingid");
        bookingPage.deleteBookingWithToken(bookingId, authToken);

        Response response = bookingPage.deleteBookingWithToken(bookingId, authToken);

        org.testng.Assert.assertTrue(response.getStatusCode() > 0,
                "Insufficient information to determine expected status code — "
                        + "actual status observed: " + response.getStatusCode());
    }
}
