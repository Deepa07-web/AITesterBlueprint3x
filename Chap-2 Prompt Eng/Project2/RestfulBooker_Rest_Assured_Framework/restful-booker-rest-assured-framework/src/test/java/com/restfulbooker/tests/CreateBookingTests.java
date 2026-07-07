package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.Booking;
import com.restfulbooker.models.BookingDates;
import com.restfulbooker.utils.ResponseValidator;
import com.restfulbooker.utils.TestDataFactory;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/**
 * POST /booking (Restful-booker.pdf pages 6-9). Success 200 with
 * { "bookingid": Number, "booking": {...same fields as request...} }.
 */
public class CreateBookingTests extends BaseTest {

    @Test(groups = {"smoke", "booking"})
    public void createBooking_withValidPayload_returns200AndBookingId() {
        Booking booking = TestDataFactory.validBooking();

        Response response = bookingPage.createBooking(booking);

        ResponseValidator.assertStatusCode(response, 200);
        ResponseValidator.assertFieldPresent(response, "bookingid");
        org.testng.Assert.assertEquals(
                response.jsonPath().getString("booking.firstname"), booking.getFirstname());
        org.testng.Assert.assertEquals(
                response.jsonPath().getString("booking.additionalneeds"), booking.getAdditionalneeds());
    }

    /**
     * Boundary value per RICE-POT-Framework-RestfulBooker.md section 3 (Thorough): "price = 0".
     * The PDF does not document how totalprice is validated, so this only asserts the
     * documented success contract still holds (200 + bookingid) — it does not assume the API
     * enforces a minimum price, since that behavior is not documented.
     */
    @Test(groups = {"booking", "boundary"})
    public void createBooking_withZeroPrice_returns200() {
        Booking booking = TestDataFactory.validBooking();
        booking.setTotalprice(0);

        Response response = bookingPage.createBooking(booking);

        ResponseValidator.assertStatusCode(response, 200);
        org.testng.Assert.assertEquals(response.jsonPath().getInt("booking.totalprice"), 0);
    }

    @Test(groups = {"booking", "boundary"})
    public void createBooking_withLargePrice_returns200() {
        Booking booking = TestDataFactory.validBooking();
        booking.setTotalprice(Integer.MAX_VALUE);

        Response response = bookingPage.createBooking(booking);

        ResponseValidator.assertStatusCode(response, 200);
    }

    @Test(groups = {"booking"})
    public void createBooking_withoutAdditionalNeeds_returns200() {
        Booking booking = TestDataFactory.validBookingWithoutAdditionalNeeds();

        Response response = bookingPage.createBooking(booking);

        ResponseValidator.assertStatusCode(response, 200);
    }

    /**
     * Insufficient information to determine expected status for a request missing required
     * fields — the PDF does not document field-level validation errors for CreateBooking.
     * This test only records actual behavior (any 2xx/4xx) instead of asserting an invented
     * status code; adjust the assertion once the live API's actual behavior is confirmed.
     */
    @Test(groups = {"booking", "negative"})
    public void createBooking_withMissingFirstname_documentedBehaviorUnknown() {
        Booking booking = TestDataFactory.validBooking();
        booking.setFirstname(null);

        Response response = bookingPage.createBooking(booking);

        org.testng.Assert.assertTrue(response.getStatusCode() > 0,
                "Insufficient information to determine expected status code — "
                        + "actual status observed: " + response.getStatusCode());
    }
}
