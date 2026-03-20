package com.restaurant.view.panels;

import com.restaurant.controller.OrderController;
import com.restaurant.dao.OrderItemDAO;
import com.restaurant.dao.DishDAO;
import com.restaurant.model.*;
import com.restaurant.util.AppColors;
import com.restaurant.util.PaginationPanel;
import com.restaurant.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

public class BillPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private OrderController orderController;
    private OrderItemDAO orderItemDAO;
    private DishDAO dishDAO;
    private JTextField txtSearch;
    private List<Order> allOrders;        // tất cả từ DB
    private List<Order> currentOrders;    // sau lọc
    private PaginationPanel pagination;

    public BillPanel() {
        orderController = new OrderController();
        orderItemDAO = new OrderItemDAO();
        dishDAO = new DishDAO();

        setLayout(new BorderLayout());
        setBackground(AppColors.BG_LIGHT);

        JLabel title = new JLabel("🧾 Quản lý hóa đơn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(40, 40, 40));

        // North compound panel
        JPanel northCompound = new JPanel(new BorderLayout());
        northCompound.setBackground(AppColors.BG_LIGHT);
        northCompound.add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnSearch = new JButton("🔍");
        btnSearch.addActionListener(e -> filterOrders());
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        northCompound.add(searchPanel, BorderLayout.SOUTH);
        add(northCompound, BorderLayout.NORTH);

        createTable();

        JScrollPane scrollPane = new JScrollPane(table);
        UIHelper.smoothScroll(scrollPane);
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 20, 20, 20),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)
        ));
        card.add(scrollPane, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        pagination = new PaginationPanel(15);
        pagination.setPageChangeListener(page -> {
            displayPage(page);
        });
        add(pagination, BorderLayout.SOUTH);

        refreshBills();
    }

    private void createTable() {
        tableModel = new DefaultTableModel(
                new String[]{"Mã ĐH", "Khách hàng", "SĐT", "Thời gian", "Tổng tiền", "Trạng thái"}, 0
        ) {
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
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        UIHelper.styleTable(table);

        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                String status = value.toString().toLowerCase();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setForeground(status.equals("paid") ? new Color(46, 204, 113) : Color.RED);
                return label;
            }
        });

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        table.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) table.setRowSelectionInterval(row, row);
            }
        });

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

    public void refreshBills() {
        allOrders = orderController.getAllOrders();
        currentOrders = allOrders;
        pagination.updateTotalItems(currentOrders.size());
        displayPage(1);
    }

    private void filterOrders() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            currentOrders = allOrders;
        } else {
            currentOrders = allOrders.stream()
                    .filter(o -> o.getCustomerName().toLowerCase().contains(keyword) ||
                            o.getPhone().contains(keyword))
                    .collect(Collectors.toList());
        }
        pagination.updateTotalItems(currentOrders.size());
        displayPage(1);
    }

    private void displayPage(int page) {
        int from = (page - 1) * pagination.getPageSize();
        int to = Math.min(from + pagination.getPageSize(), currentOrders.size());
        tableModel.setRowCount(0);
        for (int i = from; i < to; i++) {
            Order o = currentOrders.get(i);
            tableModel.addRow(new Object[]{
                    o.getId(),
                    o.getCustomerName(),
                    o.getPhone(),
                    o.getOrderTime(),
                    String.format("%,.0f VNĐ", o.getTotalAmount()),
                    o.getStatus()
            });
        }
    }

    private void showOrderDetail(int orderId) {
        List<OrderItem> items = orderItemDAO.getItemsByOrderId(orderId);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dialog.setTitle("Chi tiết hóa đơn #" + orderId);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Món", "SL", "Giá", "Thành tiền"}, 0
        );
        JTable detailTable = new JTable(model);
        detailTable.setRowHeight(30);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        UIHelper.styleTable(detailTable);

        for (OrderItem item : items) {
            Dish dish = dishDAO.getDishById(item.getDishId());
            String name = (dish != null) ? dish.getName() : "?";
            double total = item.getPrice().doubleValue() * item.getQuantity();
            model.addRow(new Object[]{
                    name,
                    item.getQuantity(),
                    String.format("%,.0f", item.getPrice()),
                    String.format("%,.0f", total)
            });
        }

        JScrollPane scroll = new JScrollPane(detailTable);
        UIHelper.smoothScroll(scroll);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}