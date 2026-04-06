package com.restaurant.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ImageUtils {
    private static Map<String, ImageIcon> cache = new HashMap<>();

    public static ImageIcon loadImage(String fileName, int width, int height) {
        if (fileName == null || fileName.isEmpty()) {
            return createPlaceholder(width, height);
        }
        String key = fileName + "_" + width + "_" + height;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        java.net.URL imgURL = ImageUtils.class.getResource("/images/dishes/" + fileName);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaled = new ImageIcon(img);
            cache.put(key, scaled);
            return scaled;
        } else {
            System.err.println("Không tìm thấy ảnh: " + fileName);
            ImageIcon placeholder = createPlaceholder(width, height);
            cache.put(key, placeholder);
            return placeholder;
        }
    }

    private static ImageIcon createPlaceholder(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();
        String text = "No Image";
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, x, y);
        g2.dispose();
        return new ImageIcon(img);
    }
}