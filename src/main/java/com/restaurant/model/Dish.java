package com.restaurant.model;

import java.math.BigDecimal;
import java.util.Date;

public class Dish {
    private int id;
    private String name;
    private int categoryId;
    private BigDecimal price;
    private String description;
    private Date createdAt;
    private String imagePath;

    public Dish() {}

    public Dish(int id, String name, int categoryId, BigDecimal price, String description, Date createdAt) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.description = description;
        this.createdAt = createdAt;

    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        return name + " - " + price + " VND";
    }
}