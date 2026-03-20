package com.restaurant.view.admin;

import com.restaurant.controller.AuthController;
import com.restaurant.util.SessionManager;
import com.restaurant.view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdminFrame extends JFrame {
    private AuthController authController;

    public AdminFrame() {
        // Kiểm tra đăng nhập trước khi cho phép mở
        if (!SessionManager.isAdminLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Bạn cần đăng nhập để truy cập khu vực quản trị!");
            dispose();
            return;
        }

        this.authController = new AuthController();
        setTitle("Quản trị nhà hàng");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(new Color(240, 240, 240));

        tabbedPane.addTab("Đặt bàn", new BookingsPanel());
        tabbedPane.addTab("Hóa đơn", new OrdersPanel());
        tabbedPane.addTab("Doanh thu", new RevenuePanel());
        tabbedPane.addTab("Món đã bán", new SoldDishesPanel());
        tabbedPane.addTab("Danh sách món", new DishListPanel());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setBackground(new Color(200, 70, 70));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(tabbedPane, BorderLayout.CENTER);
        topPanel.add(btnLogout, BorderLayout.SOUTH);

        add(topPanel);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            authController.logout();
            dispose();
            new MainFrame().setVisible(true);
        }
    }
}