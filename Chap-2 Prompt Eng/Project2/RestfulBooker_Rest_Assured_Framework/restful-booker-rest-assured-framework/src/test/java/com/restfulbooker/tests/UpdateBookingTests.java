package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.config.ConfigManager;
import com.restfulbooker.models.Booking;
import com.restfulbooker.utils.ResponseValidator;
import com.restfulbooker.utils.TestDataFactory;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/**
 * PUT /booking/:id (Restful-booker.pdf pages 10-13). Requires Cookie token=<value> or
 * Authorization: Basic auth. Success 200 returns the full updated booking.
 */
public class UpdateBookingTests extends BaseTest {

    @Test(groups = {"smoke", "booking"})
    public void updateBooking_withTokenAuth_returns200AndUpdatedFields() {
        int bookingId = bookingPage.createBooking(TestDataFactory.validBooking())
                .jsonPath().getInt("bookingid");
        Booking updated = TestDataFactory.validBooking();
        updated.setFirstname("James");

        Response response = bookingPage.updateBookingWithToken(bookingId, updated, authToken);

        ResponseValidator.assertStatusCode(response, 200);
        org.testng.Assert.assertEquals(response.jsonPath().getString("firstname"), "James");
    }

    @Test(groups = {"booking"})
    public void updateBooking_withBasicAuth_returns200() {
        int bookingId = bookingPage.createBooking(TestDataFactory.validBooking())
                .jsonPath().getInt("bookingid");
        Booking updated = TestDataFactory.validBooking();

        Response response = bookingPage.updateBookingWithBasicAuth(
                bookingId, updated, ConfigManager.getDefaultUsername(), ConfigManager.getDefaultPassword());

        ResponseValidator.assertStatusCode(response, 200);
    }

    /**
     * Inference (low confidence): PDF documents the Cookie/Authorization headers as required
     * to access PUT, but does not state the rejection status code. 403/401 comes from
     * RICE-POT-Framework-RestfulBooker.md section 3 ("unauthorized (no token) -> 403/401").
     * Verify against the live API and pin down the exact code before relying on this in CI.
     */
    @Test(groups = {"booking", "negative"})
    public void updateBooking_withoutAuth_isRejected() {
        int bookingId = bookingPage.createBooking(TestDataFactory.validBooking())
                .jsonPath().getInt("bookingid");

        Response response = bookingPage.updateBookingWithoutAuth(bookingId, TestDataFactory.validBooking());

        int status = response.getStatusCode();
        org.testng.Assert.assertTrue(status == 403 || status == 401,
                "Expected 403 or 401 per design doc, got: " + status);
    }
}
