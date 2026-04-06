package com.restaurant.view.panels;

import com.restaurant.dao.CategoryDAO;
import com.restaurant.dao.DishDAO;
import com.restaurant.model.Category;
import com.restaurant.model.Dish;
import com.restaurant.util.AppColors;
import com.restaurant.util.ImageUtils;
import com.restaurant.view.MainFrame;
import com.restaurant.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuPanel extends JPanel {

    private MainFrame mainFrame;
    private CategoryDAO categoryDAO;
    private DishDAO dishDAO;

    private JPanel categoryPanel;
    private JPanel dishPanel;
    private JTextField txtSearch;
    private int currentCategoryId = -1;
    private int currentPage = 0;
    private int itemsPerPage = 9;
    private List<Dish> currentDishes;

    // Lưu số lượng tạm thời cho từng món (trên card)
    private Map<Integer, Integer> tempQuantities = new HashMap<>();

    public MenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        categoryDAO = new CategoryDAO();
        dishDAO = new DishDAO();

        setLayout(new BorderLayout());
        setBackground(AppColors.WHITE);

        add(createSearchPanel(), BorderLayout.NORTH);
        add(createCategorySection(), BorderLayout.WEST);
        add(createDishSection(), BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(AppColors.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        JLabel lblSearch = new JLabel("Tìm món:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnSearch = new JButton(" Tìm");
        btnSearch.setBackground(AppColors.PRIMARY);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> filterDishesByName());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        return searchPanel;
    }

    private JPanel createCategorySection() {
        categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setBackground(AppColors.BG_LIGHT);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel title = new JLabel("Danh mục");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        categoryPanel.add(title);
        categoryPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        loadCategories();

        JScrollPane scroll = new JScrollPane(categoryPanel);
        scroll.setPreferredSize(new Dimension(200, 0));
        scroll.setBorder(null);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scroll);
        return wrapper;
    }

    private JScrollPane createDishSection() {
        dishPanel = new JPanel(new GridLayout(0, 3, 25, 25));
        dishPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dishPanel.setBackground(AppColors.WHITE);

        JScrollPane scroll = new JScrollPane(dishPanel);
        scroll.setBorder(null);
        UIHelper.smoothScroll(scroll);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnPrev = new JButton("<< Trước");
        JButton btnNext = new JButton("Sau >>");
        btnPrev.addActionListener(e -> changePage(-1));
        btnNext.addActionListener(e -> changePage(1));
        bottomPanel.add(btnPrev);
        bottomPanel.add(btnNext);

        JPanel container = new JPanel(new BorderLayout());
        container.add(scroll, BorderLayout.CENTER);
        container.add(bottomPanel, BorderLayout.SOUTH);
        return new JScrollPane(container);
    }

    private void loadCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category cat : categories) {
            JButton btn = new JButton(cat.getName());
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setBackground(AppColors.PRIMARY);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            UIHelper.applyHoverEffect(btn,
                    AppColors.PRIMARY,
                    new Color(100,160,210));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.addActionListener(e -> showDishesByCategory(cat.getId()));
            categoryPanel.add(btn);
            categoryPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }

    private void showDishesByCategory(int categoryId) {
        currentCategoryId = categoryId;
        txtSearch.setText("");
        currentDishes = dishDAO.getDishesByCategory(categoryId);
        currentPage = 0;
        // Reset số lượng tạm cho các món mới
        tempQuantities.clear();
        displayCurrentPage();
    }

    private void filterDishesByName() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            if (currentCategoryId != -1) {
                showDishesByCategory(currentCategoryId);
            } else {
                currentDishes = dishDAO.getAllDishes();
                currentPage = 0;
                tempQuantities.clear();
                displayCurrentPage();
            }
            return;
        }
        List<Dish> allDishes;
        if (currentCategoryId != -1) {
            allDishes = dishDAO.getDishesByCategory(currentCategoryId);
        } else {
            allDishes = dishDAO.getAllDishes();
        }
        currentDishes = allDishes.stream()
                .filter(d -> d.getName().toLowerCase().contains(keyword))
                .toList();
        currentPage = 0;
        tempQuantities.clear();
        displayCurrentPage();
    }

    private void displayCurrentPage() {
        dishPanel.removeAll();
        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, currentDishes.size());
        for (int i = start; i < end; i++) {
            dishPanel.add(createDishCard(currentDishes.get(i)));
        }
        dishPanel.revalidate();
        dishPanel.repaint();
    }

    private void changePage(int delta) {
        int newPage = currentPage + delta;
        int maxPage = (int) Math.ceil((double) currentDishes.size() / itemsPerPage) - 1;
        if (newPage >= 0 && newPage <= maxPage) {
            currentPage = newPage;
            displayCurrentPage();
        }
    }

    private JPanel createDishCard(Dish dish) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        card.setPreferredSize(new Dimension(250, 320));

        // Ảnh
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon icon = ImageUtils.loadImage(dish.getImagePath(), 220, 140);
        if (icon == null) {
            icon = new ImageIcon(new BufferedImage(220, 140, BufferedImage.TYPE_INT_ARGB));
            Graphics2D g2 = (Graphics2D) icon.getImage().getGraphics();
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, 220, 140);
            g2.setColor(Color.GRAY);
            g2.drawString("🍽️", 100, 70);
        }
        imageLabel.setIcon(icon);
        card.add(imageLabel, BorderLayout.NORTH);

        // Thông tin
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);

        JLabel name = new JLabel(dish.getName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel price = new JLabel(String.format("%,.0f VNĐ", dish.getPrice()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 15));
        price.setForeground(new Color(200,40,40));
        price.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel chọn số lượng
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        quantityPanel.setBackground(Color.WHITE);
        JButton minusBtn = new JButton("-");
        JLabel quantityLabel = new JLabel("1");
        JButton plusBtn = new JButton("+");
        Font btnFont = new Font("Segoe UI", Font.BOLD, 14);
        minusBtn.setFont(btnFont);
        plusBtn.setFont(btnFont);
        quantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        quantityLabel.setPreferredSize(new Dimension(30, 25));
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Lấy số lượng tạm thời nếu có
        int tempQty = tempQuantities.getOrDefault(dish.getId(), 1);
        quantityLabel.setText(String.valueOf(tempQty));

        minusBtn.addActionListener(e -> {
            int qty = Integer.parseInt(quantityLabel.getText());
            if (qty > 1) {
                qty--;
                quantityLabel.setText(String.valueOf(qty));
                tempQuantities.put(dish.getId(), qty);
            }
        });
        plusBtn.addActionListener(e -> {
            int qty = Integer.parseInt(quantityLabel.getText());
            qty++;
            quantityLabel.setText(String.valueOf(qty));
            tempQuantities.put(dish.getId(), qty);
        });

        quantityPanel.add(minusBtn);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(plusBtn);

        // Nút thêm vào giỏ
        JButton orderBtn = new JButton("Thêm vào giỏ");
        orderBtn.setBackground(AppColors.PRIMARY);
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setFocusPainted(false);
        orderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        orderBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        UIHelper.applyHoverEffect(orderBtn, AppColors.PRIMARY, new Color(100,160,210));

        orderBtn.addActionListener(e -> {
            int qty = Integer.parseInt(quantityLabel.getText());
            if (qty > 0) {
                mainFrame.addToCart(dish, qty);
                JOptionPane.showMessageDialog(card, "Đã thêm " + qty + " " + dish.getName() + " vào giỏ hàng!");
            }
        });

        info.add(Box.createRigidArea(new Dimension(0,10)));
        info.add(name);
        info.add(Box.createRigidArea(new Dimension(0,5)));
        info.add(price);
        info.add(Box.createRigidArea(new Dimension(0,10)));
        info.add(quantityPanel);
        info.add(Box.createRigidArea(new Dimension(0,5)));
        info.add(orderBtn);
        info.add(Box.createRigidArea(new Dimension(0,10)));

        card.add(info, BorderLayout.CENTER);
        UIHelper.applyCardHover(card);

        return card;
    }
}