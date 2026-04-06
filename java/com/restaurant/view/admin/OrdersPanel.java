package com.restaurant.view.admin;

import com.restaurant.controller.AdminController;
import com.restaurant.model.Order;
import com.restaurant.util.PaginationPanel;
import com.restaurant.view.dialogs.OrderDetailDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class OrdersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private AdminController adminController;
    private PaginationPanel pagination;
    private List<Order> allOrders;

    public OrdersPanel() {
        adminController = new AdminController();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("QUẢN LÝ HÓA ĐƠN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(60, 60, 60));
        add(title, BorderLayout.NORTH);

        createTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        pagination = new PaginationPanel(15);
        pagination.setPageChangeListener(page -> {
            displayPage(page);
        });
        add(pagination, BorderLayout.SOUTH);

        loadOrders();
    }

    private void createTable() {
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Khách hàng", "SĐT", "Email", "Thời gian", "Tổng tiền", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(200, 220, 240));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);

        // Thêm mouse listener để double click xem chi tiết
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int orderId = (int) table.getValueAt(row, 0);
                        showOrderDetail(orderId);
                    }
                }
            }
        });
    }

    private void loadOrders() {
        allOrders = adminController.getAllOrders();
        pagination.updateTotalItems(allOrders.size());
        displayPage(1);
    }

    private void displayPage(int page) {
        int from = (page - 1) * pagination.getPageSize();
        int to = Math.min(from + pagination.getPageSize(), allOrders.size());
        tableModel.setRowCount(0);
        for (int i = from; i < to; i++) {
            Order o = allOrders.get(i);
            tableModel.addRow(new Object[]{
                    o.getId(),
                    o.getCustomerName(),
                    o.getPhone(),
                    o.getEmail(),
                    o.getOrderTime(),
                    String.format("%,.0f VND", o.getTotalAmount()),
                    o.getStatus()
            });
        }
    }

    // Phương thức public để refresh từ AdminFrame
    public void refreshOrders() {
        loadOrders();
    }

    private void showOrderDetail(int orderId) {
        // Tìm order từ allOrders
        Order order = allOrders.stream().filter(o -> o.getId() == orderId).findFirst().orElse(null);
        if (order != null) {
            new OrderDetailDialog((Frame) SwingUtilities.getWindowAncestor(this), order).setVisible(true);
        }
    }
}