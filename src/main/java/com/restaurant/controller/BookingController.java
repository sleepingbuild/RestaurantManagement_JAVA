package com.restaurant.controller;

import com.restaurant.dao.BookingDAO;
import com.restaurant.model.Booking;
import java.util.Date;
import java.util.List;

public class BookingController {
    private BookingDAO bookingDAO = new BookingDAO();

    public boolean createBooking(String name, String phone, String email, Date dateTime, int guests, String request) {
        Booking booking = new Booking();
        booking.setCustomerName(name);
        booking.setPhone(phone);
        booking.setEmail(email);
        booking.setBookingDateTime(dateTime);
        booking.setGuestCount(guests);
        booking.setSpecialRequest(request);
        booking.setCreatedAt(new Date());
        return bookingDAO.addBooking(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }
}