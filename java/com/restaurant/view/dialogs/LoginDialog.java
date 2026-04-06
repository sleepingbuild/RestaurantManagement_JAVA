package com.restaurant.view.dialogs;

import com.restaurant.controller.AuthController;
import com.restaurant.util.AppColors;
import com.restaurant.view.MainFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private AuthController authController;
    private boolean succeeded;

    public LoginDialog(MainFrame mainFrame, AuthController authController) {
        super(mainFrame, "Đăng nhập Admin", true);
        this.authController = authController;
        setSize(400, 250);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 20", "[right][grow]", "[]10[]20[]"));
        panel.setBackground(AppColors.WHITE);

        JLabel title = new JLabel("ĐĂNG NHẬP ADMIN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(AppColors.PRIMARY);
        panel.add(title, "span 2, center, wrap, gapbottom 20");

        panel.add(new JLabel("Tên đăng nhập:"), "cell 0 1");
        txtUsername = new JTextField(15);
        panel.add(txtUsername, "cell 1 1, growx");

        panel.add(new JLabel("Mật khẩu:"), "cell 0 2");
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, "cell 1 2, growx");

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(AppColors.PRIMARY);
        btnLogin.setForeground(AppColors.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btnLogin, "span 2, center, gaptop 10");

        btnLogin.addActionListener(e -> login());

        add(panel);
    }

    private void login() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        if (authController.login(username, password)) {
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
            succeeded = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            succeeded = false;
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}