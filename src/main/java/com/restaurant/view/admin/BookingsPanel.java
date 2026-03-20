package com.restaurant.view.admin;

import com.restaurant.controller.BookingController;
import com.restaurant.model.Booking;
import com.restaurant.util.PaginationPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class BookingsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private BookingController bookingController;
    private PaginationPanel pagination;
    private List<Booking> allBookings;

    public BookingsPanel() {
        bookingController = new BookingController();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("QUẢN LÝ ĐẶT BÀN");
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

        loadBookings();
    }

    private void createTable() {
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Tên KH", "SĐT", "Email", "Thời gian đặt", "Số khách", "Yêu cầu", "Ngày tạo"}, 0) {
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
    }

    private void loadBookings() {
        allBookings = bookingController.getAllBookings();
        pagination.updateTotalItems(allBookings.size());
        displayPage(1);
    }

    private void displayPage(int page) {
        int from = (page - 1) * pagination.getPageSize();
        int to = Math.min(from + pagination.getPageSize(), allBookings.size());
        tableModel.setRowCount(0);
        for (int i = from; i < to; i++) {
            Booking b = allBookings.get(i);
            tableModel.addRow(new Object[]{
                    b.getId(),
                    b.getCustomerName(),
                    b.getPhone(),
                    b.getEmail(),
                    b.getBookingDateTime(),
                    b.getGuestCount(),
                    b.getSpecialRequest(),
                    b.getCreatedAt()
            });
        }
    }
}