package com.restaurant.view;

import com.restaurant.controller.CartController;
import com.restaurant.controller.AuthController;
import com.restaurant.model.Dish;
import com.restaurant.util.AppColors;
import com.restaurant.util.SessionManager;
import com.restaurant.view.admin.AdminFrame;
import com.restaurant.view.dialogs.LoginDialog;
import com.restaurant.view.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private JButton activeButton = null;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private MenuPanel menuPanel;
    private CartPanel cartPanel;
    private BillPanel billPanel;

    private JPanel indicator;
    private int indicatorX = 0;

    private Map<String, JButton> navButtons = new HashMap<>();

    private CartController cartController;

    public MainFrame() {
        cartController = new CartController();

        setTitle("Restaurant Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Responsive: mở full màn hình
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createNavbar(), BorderLayout.NORTH);
        initComponents();

        // Đặt kích thước cửa sổ tối thiểu
        setMinimumSize(new Dimension(1000, 600));
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(AppColors.WHITE);

        IntroductionPanel introPanel = new IntroductionPanel();
        BookingPanel bookingPanel = new BookingPanel();

        menuPanel = new MenuPanel(this);
        cartPanel = new CartPanel(cartController, this);
        billPanel = new BillPanel();

        mainPanel.add(introPanel, "intro");
        mainPanel.add(menuPanel, "menu");
        mainPanel.add(bookingPanel, "booking");
        mainPanel.add(cartPanel, "cart");
        mainPanel.add(billPanel, "bill");

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(AppColors.PRIMARY);
        navbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,2,0,new Color(0,0,0,50)),
                BorderFactory.createEmptyBorder(10,20,10,20)
        ));

        // Container dùng absolute để chạy indicator
        JPanel container = new JPanel(null);
        container.setPreferredSize(new Dimension(800, 60));
        container.setOpaque(false);

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        menuPanel.setOpaque(false);
        menuPanel.setBounds(0, 0, 800, 50);

        String[] buttonNames = {"Trang chủ", "Thực đơn", "Đặt bàn", "Giỏ hàng", "Hóa đơn"};
        String[] panelIds = {"intro", "menu", "booking", "cart", "bill"};

        for (int i = 0; i < buttonNames.length; i++) {
            JButton btn = new JButton(buttonNames[i]);

            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(70, 70, 70));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);

            String panelId = panelIds[i];

            btn.addActionListener(e -> {
                showPanel(panelId);
                setActiveButton(btn);
                animateIndicator(btn);
            });

            menuPanel.add(btn);
            navButtons.put(panelId, btn);
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (btn != activeButton) {
                        btn.setBackground(new Color(100, 100, 100));
                    }
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (btn != activeButton) {
                        btn.setBackground(new Color(70, 70, 70));
                    }
                }
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(50, 50, 50));
                }
            });
        }

        // Indicator
        indicator = new JPanel();
        indicator.setBackground(new Color(255,255,255,180));
        indicator.setBounds(0, 50, 80, 3);

        container.add(menuPanel);
        container.add(indicator);

        navbar.add(container, BorderLayout.CENTER);
        navbar.add(createAdminAvatar(), BorderLayout.EAST);

        // set mặc định
        SwingUtilities.invokeLater(() -> {
            JButton btn = navButtons.get("intro");
            if (btn != null) {
                setActiveButton(btn);
                animateIndicator(btn);
            }
        });

        return navbar;
    }

    private JPanel createAdminAvatar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        JButton avatar = new JButton();
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setBorderPainted(false);
        avatar.setContentAreaFilled(false);
        avatar.setFocusPainted(false);
        avatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        avatar.setToolTipText("Quản trị viên");

        // Tạo ảnh đại diện (nếu có file, nếu không dùng chữ A)
        ImageIcon icon = null;
        java.net.URL imgURL = getClass().getResource("/images/admin.png");
        if (imgURL != null) {
            icon = new ImageIcon(imgURL);
        } else {
            // Tạo hình tròn màu chủ đạo với chữ "A"
            BufferedImage defaultImg = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = defaultImg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppColors.PRIMARY);
            g2.fillOval(0, 0, 40, 40);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();
            int x = (40 - fm.stringWidth("A")) / 2;
            int y = (40 - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString("A", x, y);
            g2.dispose();
            icon = new ImageIcon(defaultImg);
        }

        // Cắt ảnh tròn (nếu icon từ file)
        if (icon != null) {
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            BufferedImage circleBuffer = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circleBuffer.createGraphics();
            g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, 40, 40));
            g2.drawImage(img, 0, 0, 40, 40, null);
            g2.dispose();
            avatar.setIcon(new ImageIcon(circleBuffer));
        }

        avatar.addActionListener(e -> {
            if (SessionManager.isAdminLoggedIn()) {
                new AdminFrame().setVisible(true);
            } else {
                LoginDialog loginDlg = new LoginDialog(this, new AuthController());
                loginDlg.setVisible(true);
                if (loginDlg.isSucceeded()) {
                    new AdminFrame().setVisible(true);
                }
            }
        });

        panel.add(avatar);
        return panel;
    }

    public void showPanel(String name) {
        // Hiệu ứng chuyển trang (fade)
        // Đơn giản dùng Timer để thay đổi độ mờ? Thực tế CardLayout không hỗ trợ mờ, nhưng có thể thêm hiệu ứng scale nhẹ.
        // Ở đây tôi thêm hiệu ứng xuất hiện nhẹ: chỉ cần gọi cardLayout và repaint
        cardLayout.show(mainPanel, name);
        // Có thể thêm một hiệu ứng đơn giản: làm mờ background rồi hiện? Không cần phức tạp.
        // Để tạo cảm giác mượt mà, dùng Timer thay đổi độ trong suốt của panel là phức tạp. Bỏ qua.
    }

    public void addToCart(Dish dish, int quantity) {
        cartController.addToCart(dish, quantity);
        if (cartPanel != null) {
            cartPanel.refreshCart();
        }
    }

    public void refreshBill() {
        if (billPanel != null) {
            billPanel.refreshBills();
        }
    }

    public CartController getCartController() {
        return cartController;
    }

    private void setActiveButton(JButton btn) {
        if (activeButton != null) {
            activeButton.setBackground(new Color(70,70,70));
            activeButton.setForeground(Color.WHITE);
            activeButton.setBorder(BorderFactory.createEmptyBorder(10,25,10,25));
        }
        btn.setBackground(Color.WHITE);
        btn.setForeground(AppColors.PRIMARY);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE,1,true),
                BorderFactory.createEmptyBorder(10,25,10,25)
        ));
        activeButton = btn;
    }

    private void animateIndicator(JButton btn) {
        int targetX = btn.getX();
        int targetWidth = btn.getWidth();

        Timer timer = new Timer(10, null);
        timer.addActionListener(e -> {
            int currentX = indicator.getX();
            int currentWidth = indicator.getWidth();

            int dx = (targetX - currentX) / 4;
            int dw = (targetWidth - currentWidth) / 4;

            if (Math.abs(dx) < 1 && Math.abs(dw) < 1) {
                indicator.setBounds(targetX, indicator.getY(), targetWidth, 3);
                timer.stop();
            } else {
                indicator.setBounds(
                        currentX + dx,
                        indicator.getY(),
                        currentWidth + dw,
                        3
                );
            }
        });
        timer.start();
    }
}