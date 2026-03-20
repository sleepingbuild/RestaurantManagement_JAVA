package com.restaurant.controller;

import com.restaurant.dao.UserDAO;
import com.restaurant.model.User;
import com.restaurant.util.PasswordUtils;
import com.restaurant.util.SessionManager;

public class AuthController {
    private UserDAO userDAO = new UserDAO();

    public boolean login(String username, String password) {
        System.out.println("Đang đăng nhập với username: " + username);
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            System.out.println("Không tìm thấy user trong DB");
            return false;
        }
        System.out.println("Tìm thấy user, kiểm tra password...");
        boolean check = PasswordUtils.checkPassword(password, user.getPassword());
        System.out.println("Kết quả check password: " + check);
        if (check) {
            SessionManager.setCurrentUser(user);
            return true;
        }
        return false;
    }

    public void logout() {
        SessionManager.logout();
    }

    // Tạo admin mặc định nếu chưa có
    public void createDefaultAdminIfNotExists() {
        User admin = userDAO.getUserByUsername("admin");
        if (admin == null) {
            String hashed = PasswordUtils.hashPassword("admin123");
            userDAO.createDefaultAdmin("admin", hashed);
            System.out.println("✅ Đã tạo tài khoản admin mặc định (admin/admin123)");
        } else {
            System.out.println("ℹ️ Tài khoản admin đã tồn tại.");
        }
    }
}