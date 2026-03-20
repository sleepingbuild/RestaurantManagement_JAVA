package com.restaurant.view.panels;

import com.restaurant.controller.CartController;
import com.restaurant.controller.OrderController;
import com.restaurant.model.OrderItem;
import com.restaurant.util.AppColors;
import com.restaurant.view.MainFrame;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class CartPanel extends JPanel {
    private MainFrame mainFrame;
    private CartController cartController;
    private OrderController orderController;

    private JTable table;
    private DefaultTableModel model;
    private JLabel totalLabel;

    public CartPanel(CartController cartController, MainFrame mainFrame) {
        this.cartController = cartController;
        this.mainFrame = mainFrame;
        this.orderController = new OrderController();

        setLayout(new BorderLayout());
        setBackground(AppColors.BG_LIGHT);

        JLabel title = new JLabel("GIỎ HÀNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        createTable();
        createBottom();

        refreshCart();
    }

    private void createTable() {
        String[] columns = {"ID món", "Tên món", "Số lượng", "Đơn giá", "Thành tiền", "Xóa"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(220, 240, 255));

        // Cột xóa hiển thị nút
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);
    }

    private void createBottom() {
        JPanel bottom = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Tổng: 0 VNĐ");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnRefresh = new JButton("Cập nhật");
        btnRefresh.setBackground(AppColors.PRIMARY);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> refreshCart());

        JButton btnCheckout = new JButton("Thanh toán");
        btnCheckout.setBackground(new Color(46, 204, 113));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckout.addActionListener(e -> checkout());

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnCheckout);

        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(buttonPanel, BorderLayout.EAST);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        add(bottom, BorderLayout.SOUTH);
    }

    public void refreshCart() {
        model.setRowCount(0);
        List<OrderItem> items = cartController.getCartItems();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : items) {
            BigDecimal price = item.getPrice();
            BigDecimal subtotal = price.multiply(new BigDecimal(item.getQuantity()));
            String dishName = (item.getDish() != null) ? item.getDish().getName() : "Món #" + item.getDishId();
            model.addRow(new Object[]{
                    item.getDishId(),
                    dishName,
                    item.getQuantity(),
                    String.format("%,.0f", price),
                    String.format("%,.0f", subtotal),
                    "Xóa"
            });
            total = total.add(subtotal);
        }
        totalLabel.setText("Tổng: " + String.format("%,.0f", total) + " VNĐ");
    }

    private void checkout() {
        if (cartController.getCartItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
            return;
        }

        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Họ tên:"));
        panel.add(txtName);
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(txtPhone);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thông tin thanh toán",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên và số điện thoại!");
            return;
        }

        boolean success = orderController.checkout(cartController, name, phone, email);
        if (success) {
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            refreshCart();
            mainFrame.refreshBill();
            mainFrame.showPanel("bill");
        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Lớp hiển thị nút xóa
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Xóa");
            setBackground(new Color(200, 70, 70));
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            return this;
        }
    }

    // Lớp xử lý click vào nút xóa
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            selectedRow = row;
            button.setText("Xóa");
            button.setBackground(new Color(200, 70, 70));
            button.setForeground(Color.WHITE);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // Xác nhận trước khi xóa
                int confirm = JOptionPane.showConfirmDialog(table, "Xóa món này khỏi giỏ hàng?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    int dishId = (int) model.getValueAt(modelRow, 0);
                    cartController.removeFromCart(dishId);
                    refreshCart();
                }
            }
            isPushed = false;
            return "Xóa";
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}