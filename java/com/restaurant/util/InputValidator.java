package com.restaurant.util;

import javax.swing.*;
import java.awt.*;

public class InputValidator {
    public static boolean validateTextField(JTextField field, String message) {
        if (field.getText().trim().isEmpty()) {
            field.setBackground(Color.RED);
            field.setToolTipText(message);
            return false;
        } else {
            field.setBackground(Color.WHITE);
            field.setToolTipText(null);
            return true;
        }
    }

    public static boolean validateEmail(JTextField field) {
        String email = field.getText().trim();
        if (email.isEmpty()) return false; // có thể cho phép email trống? tùy yêu cầu
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            field.setBackground(Color.RED);
            field.setToolTipText("Email không hợp lệ");
            return false;
        } else {
            field.setBackground(Color.WHITE);
            field.setToolTipText(null);
            return true;
        }
    }

    public static boolean validatePhone(JTextField field) {
        String phone = field.getText().trim();
        if (phone.isEmpty()) return false;
        String phoneRegex = "^[0-9]{10,11}$";
        if (!phone.matches(phoneRegex)) {
            field.setBackground(Color.RED);
            field.setToolTipText("Số điện thoại phải 10-11 số");
            return false;
        } else {
            field.setBackground(Color.WHITE);
            field.setToolTipText(null);
            return true;
        }
    }
}