package com.restaurant;

import com.formdev.flatlaf.FlatLightLaf;
import com.restaurant.controller.AuthController;
import com.restaurant.util.AppColors;
import com.restaurant.view.MainFrame;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Cấu hình FlatLaf và màu sắc chung
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 8);
        UIManager.put("Button.background", AppColors.PRIMARY);
        UIManager.put("Button.foreground", AppColors.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));

        // Tạo tài khoản admin mặc định nếu chưa có
        AuthController authController = new AuthController();
        authController.createDefaultAdminIfNotExists();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}