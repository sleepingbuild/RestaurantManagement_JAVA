package com.restaurant.view.admin;

import com.restaurant.dao.OrderItemDAO;
import com.restaurant.dao.DishDAO;
import com.restaurant.model.Dish;
import com.restaurant.model.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.*;

public class SoldDishesPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private OrderItemDAO orderItemDAO;
    private DishDAO dishDAO;

    public SoldDishesPanel(){

        setLayout(new BorderLayout(10,10));
        setBackground(new Color(245,247,250));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel title = new JLabel("MÓN BÁN CHẠY");
        title.setFont(new Font("Segoe UI",Font.BOLD,20));

        add(title,BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"Món ăn","Số lượng"},0);

        table = new JTable(tableModel);
        orderItemDAO = new OrderItemDAO();
        dishDAO = new DishDAO();

        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI",Font.PLAIN,14));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(70,130,180));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI",Font.BOLD,14));

        JScrollPane scroll = new JScrollPane(table);

        add(scroll,BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        java.util.List<OrderItem> items = orderItemDAO.getAllOrderItemsPaid();
        Map<Integer, Integer> countMap = new HashMap<>();
        for (OrderItem item : items) {
            countMap.put(item.getDishId(), countMap.getOrDefault(item.getDishId(), 0) + item.getQuantity());
        }

        java.util.List<Map.Entry<Integer, Integer>> sorted = new java.util.ArrayList<>(countMap.entrySet());
        sorted.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        for (Map.Entry<Integer, Integer> entry : sorted) {
            Dish dish = dishDAO.getDishById(entry.getKey());
            if (dish != null) {
                tableModel.addRow(new Object[]{dish.getName(), entry.getValue()});
            }
        }
    }
}