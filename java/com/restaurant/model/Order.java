package com.restaurant.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private String customerName;
    private String phone;
    private String email;
    private Date orderTime;
    private BigDecimal totalAmount;
    private String status; // "pending", "paid"
    private List<OrderItem> items;

    public Order() {}

    public Order(int id, String customerName, String phone, String email, Date orderTime, BigDecimal totalAmount, String status) {
        this.id = id;
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.orderTime = orderTime;
        this.totalAmount = totalAmount;
        this.status = status;
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
    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }


}