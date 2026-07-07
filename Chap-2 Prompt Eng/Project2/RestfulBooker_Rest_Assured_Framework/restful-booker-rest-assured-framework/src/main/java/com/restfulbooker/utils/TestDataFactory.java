package com.restfulbooker.utils;

import com.restfulbooker.models.Booking;
import com.restfulbooker.models.BookingDates;

import java.util.UUID;

/**
 * Generates unique Booking test data per call so tests stay isolated (RICE-POT: Isolated) —
 * no test relies on a booking created by another test.
 */
public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Booking validBooking() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return new Booking(
                "Firstname-" + suffix,
                "Lastname-" + suffix,
                111,
                true,
                new BookingDates("2024-01-01", "2024-01-10"),
                "Breakfast"
        );
    }

    public static Booking validBookingWithoutAdditionalNeeds() {
        Booking booking = validBooking();
        booking.setAdditionalneeds(null);
        return booking;
    }
}
