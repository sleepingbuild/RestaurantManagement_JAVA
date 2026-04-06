package com.restaurant.controller;

import com.restaurant.dao.OrderDAO;
import com.restaurant.dao.OrderItemDAO;
import com.restaurant.model.Dish;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import java.math.BigDecimal;
import java.util.List;

public class OrderController {
    private OrderDAO orderDAO = new OrderDAO();
    private OrderItemDAO orderItemDAO = new OrderItemDAO();

    // Xóa toàn bộ các field và method liên quan đến CartItem riêng
    // public static class CartItem ... (xóa)

    // Phương thức checkout mới nhận CartController
    public boolean checkout(CartController cartController, String customerName, String phone, String email) {
        List<OrderItem> cartItems = cartController.getCartItems();
        if (cartItems.isEmpty()) return false;

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : cartItems) {
            // item.getPrice() là giá tại thời điểm thêm, nhân với số lượng
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setEmail(email);
        order.setTotalAmount(total);
        order.setStatus("paid");

        int orderId = orderDAO.addOrder(order);
        if (orderId == -1) return false;

        for (OrderItem item : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrderId(orderId);
            oi.setDishId(item.getDishId());
            oi.setQuantity(item.getQuantity());
            oi.setPrice(item.getPrice());
            orderItemDAO.addOrderItem(oi);
        }

        cartController.clearCart();  // Xóa giỏ hàng sau khi thanh toán
        return true;
    }

    // Các phương thức khác giữ nguyên
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    public Order getOrderById(int id) {
        return orderDAO.getOrderById(id);
    }
    public boolean checkoutSelectedItems(CartController cartController, List<OrderItem> itemsToPay,
                                         String customerName, String phone, String email) {
        if (itemsToPay.isEmpty()) return false;

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : itemsToPay) {
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setEmail(email);
        order.setTotalAmount(total);
        order.setStatus("paid");

        int orderId = orderDAO.addOrder(order);
        if (orderId == -1) return false;

        for (OrderItem item : itemsToPay) {
            OrderItem oi = new OrderItem();
            oi.setOrderId(orderId);
            oi.setDishId(item.getDishId());
            oi.setQuantity(item.getQuantity());
            oi.setPrice(item.getPrice());
            orderItemDAO.addOrderItem(oi);
        }

        return true;
    }
    public boolean checkoutWithoutClearingCart(CartController cartController, String customerName, String phone, String email) {
        List<OrderItem> cartItems = cartController.getCartItems();
        if (cartItems.isEmpty()) return false;

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : cartItems) {
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setEmail(email);
        order.setTotalAmount(total);
        order.setStatus("paid");

        int orderId = orderDAO.addOrder(order);
        if (orderId == -1) return false;

        for (OrderItem item : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrderId(orderId);
            oi.setDishId(item.getDishId());
            oi.setQuantity(item.getQuantity());
            oi.setPrice(item.getPrice());
            orderItemDAO.addOrderItem(oi);
        }

        // KHÔNG xóa giỏ hàng
        return true;
    }
}