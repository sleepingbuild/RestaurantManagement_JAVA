package com.restaurant.controller;

import com.restaurant.dao.DishDAO;
import com.restaurant.dao.OrderDAO;
import com.restaurant.dao.BookingDAO;
import com.restaurant.model.Dish;
import com.restaurant.model.Order;
import com.restaurant.model.Booking;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AdminController {
    private DishDAO dishDAO = new DishDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    public BigDecimal getTotalRevenue() {
        List<Order> orders = orderDAO.getAllOrders();
        return orders.stream()
                .filter(o -> "paid".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }

    public List<Dish> getAllDishes() {
        return dishDAO.getAllDishes();
    }


    public List<Order> getOrdersByDateRange(Date start, Date end) {
        return orderDAO.getOrdersByDateRange(start, end);
    }
    public boolean addDish(String name, int categoryId, BigDecimal price, String description, String imagePath) {
        Dish dish = new Dish();
        dish.setName(name);
        dish.setCategoryId(categoryId);
        dish.setPrice(price);
        dish.setDescription(description);
        dish.setImagePath(imagePath);
        // created_at tự động lấy thời gian hiện tại trong DB
        return dishDAO.addDish(dish);
    }
    public boolean deleteDish(int dishId) {
        return dishDAO.deleteDish(dishId);
    }
}