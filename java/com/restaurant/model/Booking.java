package com.restaurant.model;

import java.util.Date;

public class Booking {
    private int id;
    private String customerName;
    private String phone;
    private String email;
    private Date bookingDateTime;
    private int guestCount;
    private String specialRequest;
    private Date createdAt;

    public Booking() {}

    public Booking(int id, String customerName, String phone, String email, Date bookingDateTime, int guestCount, String specialRequest, Date createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.bookingDateTime = bookingDateTime;
        this.guestCount = guestCount;
        this.specialRequest = specialRequest;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getBookingDateTime() { return bookingDateTime; }
    public void setBookingDateTime(Date bookingDateTime) { this.bookingDateTime = bookingDateTime; }
    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }
    public String getSpecialRequest() { return specialRequest; }
    public void setSpecialRequest(String specialRequest) { this.specialRequest = specialRequest; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}