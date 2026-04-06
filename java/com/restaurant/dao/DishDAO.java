package com.restaurant.dao;

import com.restaurant.model.Dish;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishDAO {
    public List<Dish> getAllDishes() {
        List<Dish> list = new ArrayList<>();
        String sql = "SELECT * FROM dishes ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractDishFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Dish> getDishesByCategory(int categoryId) {
        List<Dish> list = new ArrayList<>();
        String sql = "SELECT * FROM dishes WHERE category_id = ? ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractDishFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Dish getDishById(int id) {
        String sql = "SELECT * FROM dishes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractDishFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Dish extractDishFromResultSet(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getInt("id"));
        dish.setName(rs.getString("name"));
        dish.setCategoryId(rs.getInt("category_id"));
        dish.setPrice(rs.getBigDecimal("price"));
        dish.setDescription(rs.getString("description"));
        dish.setCreatedAt(rs.getTimestamp("created_at"));
        dish.setImagePath(rs.getString("image_path"));
        return dish;
    }
    public boolean addDish(Dish dish) {
        String sql = "INSERT INTO dishes (name, category_id, price, description, image_path, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dish.getName());
            ps.setInt(2, dish.getCategoryId());
            ps.setBigDecimal(3, dish.getPrice());
            ps.setString(4, dish.getDescription());
            ps.setString(5, dish.getImagePath());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteDish(int id) {
        String sql = "DELETE FROM dishes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}