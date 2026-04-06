package com.restaurant.view.dialogs;

import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.dao.OrderItemDAO;
import com.restaurant.dao.DishDAO;
import com.restaurant.model.Dish;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderDetailDialog extends JDialog {
    private Order order;
    private OrderItemDAO orderItemDAO;
    private DishDAO dishDAO;

    public OrderDetailDialog(Frame owner, Order order) {
        super(owner, "Chi tiết hóa đơn #" + order.getId(), true);
        this.order = order;
        this.orderItemDAO = new OrderItemDAO();
        this.dishDAO = new DishDAO();
        setSize(600, 400);
        setLocationRelativeTo(owner);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Thông tin chung hóa đơn
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(new JLabel("Khách hàng:"));
        infoPanel.add(new JLabel(order.getCustomerName()));
        infoPanel.add(new JLabel("SĐT:"));
        infoPanel.add(new JLabel(order.getPhone()));
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(new JLabel(order.getEmail() != null ? order.getEmail() : ""));
        infoPanel.add(new JLabel("Thời gian:"));
        infoPanel.add(new JLabel(order.getOrderTime().toString()));

        // Bảng chi tiết món
        String[] columns = {"Tên món", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        List<OrderItem> items = orderItemDAO.getItemsByOrderId(order.getId());
        double total = 0;
        for (OrderItem item : items) {
            Dish dish = dishDAO.getDishById(item.getDishId());
            String dishName = dish != null ? dish.getName() : "Món #" + item.getDishId();
            double thanhTien = item.getPrice().doubleValue() * item.getQuantity();
            model.addRow(new Object[]{
                    dishName,
                    item.getQuantity(),
                    String.format("%,.0f", item.getPrice()),
                    String.format("%,.0f", thanhTien)
            });
            total += thanhTien;
        }

        // Tổng tiền
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel(String.format("Tổng cộng: %,.0f VND", total));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalPanel.add(totalLabel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(infoPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(totalPanel, BorderLayout.SOUTH);
    }
}