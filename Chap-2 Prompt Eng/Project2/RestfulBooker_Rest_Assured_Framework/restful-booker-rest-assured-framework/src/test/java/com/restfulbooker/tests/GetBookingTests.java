package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.Booking;
import com.restfulbooker.utils.ResponseValidator;
import com.restfulbooker.utils.TestDataFactory;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/**
 * GET /booking/:id (Restful-booker.pdf pages 4-5). Success 200 with firstname, lastname,
 * totalprice, depositpaid, bookingdates{checkin,checkout}, additionalneeds. Accept header
 * selects application/json or application/xml.
 * Each test creates its own booking first (RICE-POT: Isolated) rather than depending on a
 * fixed booking id from another test or the live dataset.
 */
public class GetBookingTests extends BaseTest {

    @Test(groups = {"smoke", "booking"})
    public void getBooking_withValidId_returnsJsonFields() {
        Booking booking = TestDataFactory.validBooking();
        int bookingId = bookingPage.createBooking(booking).jsonPath().getInt("bookingid");

        Response response = bookingPage.getBookingById(bookingId, ContentType.JSON.toString());

        ResponseValidator.assertStatusCode(response, 200);
        ResponseValidator.assertFieldPresent(response, "firstname");
        org.testng.Assert.assertEquals(response.jsonPath().getString("firstname"), booking.getFirstname());
    }

    @Test(groups = {"booking"})
    public void getBooking_withXmlAccept_returnsXml() {
        Booking booking = TestDataFactory.validBooking();
        int bookingId = bookingPage.createBooking(booking).jsonPath().getInt("bookingid");

        Response response = bookingPage.getBookingById(bookingId, ContentType.XML.toString());

        ResponseValidator.assertStatusCode(response, 200);
        org.testng.Assert.assertTrue(response.getContentType().contains("xml"));
    }

    /**
     * Inference (low confidence): the PDF's GetBooking section documents no error case at
     * all. 404 comes from RICE-POT-Framework-RestfulBooker.md section 3 (Thorough) —
     * "invalid/non-existent ID -> 404" — which is a design reference, not the official API
     * doc. Verify against the live API before trusting this in CI.
     */
    @Test(groups = {"booking", "negative"})
    public void getBooking_withNonExistentId_returns404() {
        Response response = bookingPage.getBookingById(999_999_999);
        ResponseValidator.assertStatusCode(response, 404);
    }
}
