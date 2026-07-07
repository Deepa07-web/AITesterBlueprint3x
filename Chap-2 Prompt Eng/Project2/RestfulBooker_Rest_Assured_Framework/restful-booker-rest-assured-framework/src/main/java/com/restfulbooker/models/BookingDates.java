package com.restfulbooker.models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Sub-object of Booking. Fields per Restful-booker.pdf: checkin, checkout (format CCYY-MM-DD).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDates {

    private String checkin;
    private String checkout;

    public BookingDates() {
    }

    public BookingDates(String checkin, String checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }
}
