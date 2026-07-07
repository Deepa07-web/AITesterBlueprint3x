package com.restfulbooker.models;

/**
 * Response of POST /booking per Restful-booker.pdf: { "bookingid": Number, "booking": {...} }
 */
public class CreateBookingResponse {

    private Integer bookingid;
    private Booking booking;

    public CreateBookingResponse() {
    }

    public Integer getBookingid() {
        return bookingid;
    }

    public void setBookingid(Integer bookingid) {
        this.bookingid = bookingid;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
