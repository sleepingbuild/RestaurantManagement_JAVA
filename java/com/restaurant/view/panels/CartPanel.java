package com.restaurant.view.panels;

import com.restaurant.controller.CartController;
import com.restaurant.model.OrderItem;
import com.restaurant.util.AppColors;
import com.restaurant.view.MainFrame;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CartPanel extends JPanel {
    private MainFrame mainFrame;
    private CartController cartController;

    private JTable table;
    private DefaultTableModel model;
    private JLabel totalLabel;

    public CartPanel(CartController cartController, MainFrame mainFrame) {
        this.cartController = cartController;
        this.mainFrame = mainFrame;

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

        buttonPanel.add(btnRefresh);

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
            // Kiểm tra trạng thái thanh toán
            String action = item.isPaid() ? "Đã TT" : "Xóa";
            model.addRow(new Object[]{
                    item.getDishId(),
                    dishName,
                    item.getQuantity(),
                    String.format("%,.0f", price),
                    String.format("%,.0f", subtotal),
                    action
            });
        }
        totalLabel.setText("Tổng: " + String.format("%,.0f", total) + " VNĐ");
    }

    // Inner classes ButtonRenderer và ButtonEditor giữ nguyên (như cũ)
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String action = (String) value;
            if ("Xóa".equals(action)) {
                setText("Xóa");
                setBackground(new Color(200, 70, 70));
                setForeground(Color.WHITE);
                setEnabled(true);
            } else {
                setText("Đã TT");
                setBackground(Color.LIGHT_GRAY);
                setForeground(Color.DARK_GRAY);
                setEnabled(false);
            }
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            return this;
        }
    }

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
                // Lấy action từ model tại dòng đó
                int modelRow = table.convertRowIndexToModel(selectedRow);
                String action = (String) model.getValueAt(modelRow, 5);
                if ("Xóa".equals(action)) {
                    int confirm = JOptionPane.showConfirmDialog(table, "Xóa món này khỏi giỏ hàng?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int dishId = (int) model.getValueAt(modelRow, 0);
                        cartController.removeFromCart(dishId);
                        refreshCart();
                        mainFrame.refreshBill();
                    }
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