package com.restaurant.controller;

import com.restaurant.model.Dish;
import com.restaurant.model.OrderItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartController {
    private List<OrderItem> cartItems = new ArrayList<>();

    public void addToCart(Dish dish, int quantity) {
        // Kiểm tra xem món đã có trong giỏ chưa, nếu có thì tăng số lượng
        for (OrderItem item : cartItems) {
            if (item.getDishId() == dish.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        OrderItem newItem = new OrderItem();
        newItem.setDishId(dish.getId());
        newItem.setDish(dish);
        newItem.setQuantity(quantity);
        newItem.setPrice(dish.getPrice());
        cartItems.add(newItem);
    }

    public void removeFromCart(int dishId) {
        cartItems.removeIf(item -> item.getDishId() == dishId);
    }

    public void clearCart() {
        cartItems.clear();
    }

    public List<OrderItem> getCartItems() {
        return cartItems;
    }

    public BigDecimal getTotal() {
        return cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemCount() {
        return cartItems.size();
    }
}