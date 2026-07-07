package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.utils.ResponseValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertTrue;

/**
 * GET /booking with optional firstname/lastname/checkin/checkout filters
 * (Restful-booker.pdf pages 2-3). Success 200, body is an array of { "bookingid": Number }.
 */
public class GetBookingIdsTests extends BaseTest {

    @Test(groups = {"smoke", "booking"})
    public void getAllBookingIds_returns200AndArray() {
        Response response = bookingPage.getBookingIds(null);

        ResponseValidator.assertStatusCode(response, 200);
        List<Integer> ids = response.jsonPath().getList("bookingid");
        assertTrue(ids != null, "Expected a bookingid array in the response");
    }

    @Test(groups = {"booking"})
    public void getBookingIds_filteredByFirstAndLastName_returns200() {
        Response response = bookingPage.getBookingIds(Map.of("firstname", "sally", "lastname", "brown"));

        ResponseValidator.assertStatusCode(response, 200);
    }

    @Test(groups = {"booking"})
    public void getBookingIds_filteredByCheckinCheckout_returns200() {
        Response response = bookingPage.getBookingIds(Map.of(
                "checkin", "2014-03-13", "checkout", "2014-05-21"));

        ResponseValidator.assertStatusCode(response, 200);
    }
}
