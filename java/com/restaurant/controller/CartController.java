package com.restaurant.controller;

import com.restaurant.model.Dish;
import com.restaurant.model.OrderItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartController {
    private List<OrderItem> cartItems = new ArrayList<>();

    // Thêm món vào giỏ (nếu món đã có và chưa paid thì tăng số lượng)
    public void addToCart(Dish dish, int quantity) {
        for (OrderItem item : cartItems) {
            if (item.getDishId() == dish.getId() && !item.isPaid()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        OrderItem newItem = new OrderItem();
        newItem.setDishId(dish.getId());
        newItem.setDish(dish);
        newItem.setQuantity(quantity);
        newItem.setPrice(dish.getPrice());
        newItem.setPaid(false);
        cartItems.add(newItem);
    }

    public List<OrderItem> getCartItems() {
        return cartItems;
    }

    // Chỉ lấy các món chưa thanh toán
    public List<OrderItem> getUnpaidItems() {
        List<OrderItem> unpaid = new ArrayList<>();
        for (OrderItem item : cartItems) {
            if (!item.isPaid()) {
                unpaid.add(item);
            }
        }
        return unpaid;
    }

    // Đánh dấu tất cả món chưa thanh toán là đã thanh toán
    public void markAllAsPaid() {
        for (OrderItem item : cartItems) {
            if (!item.isPaid()) {
                item.setPaid(true);
            }
        }
    }

    // Xóa món khỏi giỏ (chỉ xóa nếu chưa thanh toán)
    public void removeFromCart(int dishId) {
        cartItems.removeIf(item -> item.getDishId() == dishId && !item.isPaid());
    }

    // Xóa toàn bộ giỏ (chỉ dùng nếu cần reset)
    public void clearCart() {
        cartItems.clear();
    }
}