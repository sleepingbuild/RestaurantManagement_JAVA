package com.restaurant.dao;

import com.restaurant.model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    public boolean addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer_name, phone, email, booking_datetime, guest_count, special_request) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, booking.getCustomerName());
            ps.setString(2, booking.getPhone());
            ps.setString(3, booking.getEmail());
            ps.setTimestamp(4, new Timestamp(booking.getBookingDateTime().getTime()));
            ps.setInt(5, booking.getGuestCount());
            ps.setString(6, booking.getSpecialRequest());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY booking_datetime DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setCustomerName(rs.getString("customer_name"));
                b.setPhone(rs.getString("phone"));
                b.setEmail(rs.getString("email"));
                b.setBookingDateTime(rs.getTimestamp("booking_datetime"));
                b.setGuestCount(rs.getInt("guest_count"));
                b.setSpecialRequest(rs.getString("special_request"));
                b.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}