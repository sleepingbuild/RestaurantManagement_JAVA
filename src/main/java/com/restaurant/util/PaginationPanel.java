package com.restaurant.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.function.Consumer;

public class PaginationPanel extends JPanel {
    private JButton btnFirst, btnPrev, btnNext, btnLast;
    private JLabel lblPageInfo;
    private int currentPage = 1;
    private int totalPages = 1;
    private int pageSize = 20;
    private Consumer<Integer> pageChangeListener;

    public PaginationPanel(int pageSize) {
        this.pageSize = pageSize;
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setBackground(Color.WHITE);

        btnFirst = new JButton("Đầu");
        btnPrev = new JButton("◀");
        btnNext = new JButton("▶");
        btnLast = new JButton("Cuối");
        lblPageInfo = new JLabel("Trang 1 / 1");

        btnFirst.addActionListener(e -> goToPage(1));
        btnPrev.addActionListener(e -> goToPage(currentPage - 1));
        btnNext.addActionListener(e -> goToPage(currentPage + 1));
        btnLast.addActionListener(e -> goToPage(totalPages));

        add(btnFirst);
        add(btnPrev);
        add(lblPageInfo);
        add(btnNext);
        add(btnLast);
    }

    public void setPageChangeListener(Consumer<Integer> listener) {
        this.pageChangeListener = listener;
    }

    public void updateTotalItems(int totalItems) {
        totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages < 1) totalPages = 1;
        currentPage = 1;
        updateButtons();
        if (pageChangeListener != null) pageChangeListener.accept(currentPage);
    }

    public void goToPage(int page) {
        if (page < 1 || page > totalPages) return;
        currentPage = page;
        updateButtons();
        if (pageChangeListener != null) pageChangeListener.accept(currentPage);
    }

    private void updateButtons() {
        btnFirst.setEnabled(currentPage > 1);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
        btnLast.setEnabled(currentPage < totalPages);
        lblPageInfo.setText("Trang " + currentPage + " / " + totalPages);
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}