package com.restaurant.view.panels;

import com.restaurant.controller.CartController;
import com.restaurant.controller.OrderController;
import com.restaurant.model.OrderItem;
import com.restaurant.util.AppColors;
import com.restaurant.util.UIHelper;
import com.restaurant.view.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class BillPanel extends JPanel {
    private MainFrame mainFrame;
    private CartController cartController;
    private OrderController orderController;

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private String lastCustomerName = "";
    private String lastPhone = "";
    private String lastEmail = "";

    public BillPanel(MainFrame mainFrame, CartController cartController) {
        this.mainFrame = mainFrame;
        this.cartController = cartController;
        this.orderController = new OrderController();

        setLayout(new BorderLayout());
        setBackground(AppColors.BG_LIGHT);

        JLabel title = new JLabel(" Hóa đơn của bạn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        createTable();
        createBottom();

        refreshBill();
    }

    private void createTable() {
        String[] columns = {"ID", "Tên món", "Số lượng", "Đơn giá", "Thành tiền", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);
        UIHelper.styleTable(table);

        // Căn phải cho cột giá
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Đơn giá
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Thành tiền

        // Căn giữa cho cột trạng thái
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scroll = new JScrollPane(table);
        UIHelper.smoothScroll(scroll);
        add(scroll, BorderLayout.CENTER);
    }

    private void createBottom() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(AppColors.BG_LIGHT);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        totalLabel = new JLabel("Tổng cộng (chưa thanh toán): 0 VNĐ");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(new Color(200, 70, 70));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.setBackground(AppColors.PRIMARY);
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> refreshBill());

        JButton btnCheckout = new JButton("Thanh toán");
        btnCheckout.setBackground(new Color(46, 204, 113));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckout.addActionListener(e -> checkout());

        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnCheckout);

        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(buttonPanel, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);
    }

    public void refreshBill() {
        tableModel.setRowCount(0);
        List<OrderItem> items = cartController.getCartItems();
        BigDecimal totalUnpaid = BigDecimal.ZERO;

        for (OrderItem item : items) {
            BigDecimal subtotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            String dishName = (item.getDish() != null) ? item.getDish().getName() : "Món #" + item.getDishId();
            String status = item.isPaid() ? "Đã thanh toán" : "Chưa thanh toán";
            tableModel.addRow(new Object[]{
                    item.getDishId(),
                    dishName,
                    item.getQuantity(),
                    String.format("%,.0f", item.getPrice()),
                    String.format("%,.0f", subtotal),
                    status
            });
            if (!item.isPaid()) {
                totalUnpaid = totalUnpaid.add(subtotal);
            }
        }
        totalLabel.setText("Tổng cộng (chưa thanh toán): " + String.format("%,.0f", totalUnpaid) + " VNĐ");
    }

    private void checkout() {
        List<OrderItem> unpaidItems = cartController.getUnpaidItems();
        if (unpaidItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có món nào cần thanh toán!");
            return;
        }

        String name, phone, email;
        if (lastCustomerName.isEmpty()) {
            // Lần đầu thanh toán, hiện dialog nhập
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

            name = txtName.getText().trim();
            phone = txtPhone.getText().trim();
            email = txtEmail.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên và số điện thoại!");
                return;
            }

            lastCustomerName = name;
            lastPhone = phone;
            lastEmail = email;
        } else {
            // Đã có thông tin, dùng lại
            name = lastCustomerName;
            phone = lastPhone;
            email = lastEmail;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Thanh toán với thông tin: " + name + " - " + phone + "?\nNhấn OK để tiếp tục.",
                    "Xác nhận", JOptionPane.OK_CANCEL_OPTION);
            if (confirm != JOptionPane.OK_OPTION) return;
        }

        // Tạo hóa đơn chỉ với các mục unpaid
        boolean success = orderController.checkoutWithoutClearingCart(cartController, name, phone, email);
        if (success) {
            // Đánh dấu tất cả món trong giỏ là đã thanh toán
            cartController.markAllAsPaid();
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            refreshBill();
            mainFrame.refreshAdminOrders(); // Cập nhật admin
        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}