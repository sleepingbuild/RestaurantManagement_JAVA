package com.restaurant.view.dialogs;

import com.restaurant.model.Dish;
import com.restaurant.util.ImageUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class DishDetailDialog extends JDialog {
    public DishDetailDialog(Frame owner, Dish dish) {
        super(owner, "Chi tiết món ăn", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow][]"));

        // Ảnh món ăn
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (dish.getImagePath() != null && !dish.getImagePath().isEmpty()) {
            ImageIcon icon = ImageUtils.loadImage(dish.getImagePath(), 250, 200);
            if (icon != null) {
                imageLabel.setIcon(icon);
            } else {
                imageLabel.setText("Không có ảnh");
            }
        } else {
            imageLabel.setText("Không có ảnh");
        }
        add(imageLabel, "cell 0 0, center");

        // Tên món
        JLabel nameLabel = new JLabel(dish.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(nameLabel, "cell 0 1, center");

        // Thông tin chi tiết
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText("Giá: " + dish.getPrice() + " VND\n\n" +
                "Mô tả:\n" + (dish.getDescription() != null ? dish.getDescription() : "Không có mô tả"));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, "cell 0 2, grow");

        // Nút đóng
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnClose.addActionListener(e -> dispose());
        add(btnClose, "cell 0 3, center, gaptop 10");
    }
}