package com.restaurant.model;

import java.math.BigDecimal;

public class OrderItem {
    private int id;
    private int orderId;
    private int dishId;
    private int quantity;
    private BigDecimal price; // giá tại thời điểm đặt
    private Dish dish; // để hiển thị

    public OrderItem() {}

    public OrderItem(int id, int orderId, int dishId, int quantity, BigDecimal price) {
        this.id = id;
        this.orderId = orderId;
        this.dishId = dishId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getDishId() { return dishId; }
    public void setDishId(int dishId) { this.dishId = dishId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }
}