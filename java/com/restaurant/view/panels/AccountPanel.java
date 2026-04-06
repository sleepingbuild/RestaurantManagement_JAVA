package com.restaurant.view.panels;

import com.restaurant.controller.AuthController;
import com.restaurant.view.MainFrame;
import com.restaurant.view.dialogs.LoginDialog;

import javax.swing.*;
import java.awt.*;

public class AccountPanel extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;

    public AccountPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.authController = new AuthController();
        setLayout(new GridBagLayout());
        setBackground(new Color(248, 248, 252));

        JButton btnLogin = new JButton("Đăng nhập Admin");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        btnLogin.addActionListener(e -> {
            LoginDialog loginDialog = new LoginDialog(mainFrame, authController);
            loginDialog.setVisible(true);
        });

        add(btnLogin);
    }
}