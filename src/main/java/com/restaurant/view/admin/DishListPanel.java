package com.restaurant.view.admin;

import com.restaurant.controller.AdminController;
import com.restaurant.dao.CategoryDAO;
import com.restaurant.model.Category;
import com.restaurant.model.Dish;
import com.restaurant.util.PaginationPanel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class DishListPanel extends JPanel {

    private AdminController adminController;
    private CategoryDAO categoryDAO;

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<CategoryItem> categoryCombo;
    private PaginationPanel pagination;
    private List<Dish> allDishes;          // Danh sách gốc (tất cả món)
    private List<Dish> filteredDishes;     // Danh sách sau khi lọc theo loại
    private int currentPage = 1;
    private String sortColumn = "id";      // Cột đang sắp xếp
    private boolean sortAscending = true;  // Sắp xếp tăng dần hay giảm dần

    private static class CategoryItem {
        private int id;
        private String name;
        public CategoryItem(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        @Override public String toString() { return name; }
    }

    public DishListPanel() {
        adminController = new AdminController();
        categoryDAO = new CategoryDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        pagination = new PaginationPanel(15);
        pagination.setPageChangeListener(page -> {
            currentPage = page;
            displayCurrentPage();
        });
        add(pagination, BorderLayout.SOUTH);

        loadAllDishes();
        filterTable(); // sẽ cập nhật filteredDishes và phân trang
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("QUẢN LÝ MÓN ĂN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(60, 60, 60));

        panel.add(title);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(new JLabel("Loại:"));

        categoryCombo = new JComboBox<>();
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadCategoriesToCombo();
        categoryCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                filterTable();
            }
        });
        panel.add(categoryCombo);

        JButton btnAdd = new JButton(" Thêm món");
        btnAdd.setBackground(new Color(0, 150, 136));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> showAddDishDialog());
        panel.add(btnAdd);

        JButton btnDelete = new JButton("Xóa món");
        btnDelete.setBackground(new Color(200, 70, 70));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setFocusPainted(false);
        btnDelete.addActionListener(e -> deleteSelectedDish());
        panel.add(btnDelete);

        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Tên món", "Loại", "Giá", "Ngày thêm"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionBackground(new Color(200, 220, 240));
        table.setGridColor(new Color(230, 230, 230));
        table.setAutoCreateRowSorter(false); // tắt sorter tự động

        // Xử lý sắp xếp khi click header
        JTableHeader header = table.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = header.columnAtPoint(e.getPoint());
                if (col != -1) {
                    String columnName = table.getColumnName(col);
                    // Nếu click vào cùng cột thì đảo chiều, ngược lại đặt cột mới và sắp xếp tăng dần
                    if (columnName.equals(sortColumn)) {
                        sortAscending = !sortAscending;
                    } else {
                        sortColumn = columnName;
                        sortAscending = true;
                    }
                    sortAndRefresh();
                }
            }
        });

        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof BigDecimal) {
                    setText(String.format("%,.0f VND", ((BigDecimal) value).doubleValue()));
                }
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private void loadCategoriesToCombo() {
        categoryCombo.addItem(new CategoryItem(-1, "Tất cả"));
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category cat : categories) {
            categoryCombo.addItem(new CategoryItem(cat.getId(), cat.getName()));
        }
    }

    private void loadAllDishes() {
        allDishes = adminController.getAllDishes();
    }

    private void filterTable() {
        CategoryItem selected = (CategoryItem) categoryCombo.getSelectedItem();
        int selectedId = selected != null ? selected.getId() : -1;

        if (selectedId == -1) {
            filteredDishes = new ArrayList<>(allDishes);
        } else {
            filteredDishes = allDishes.stream()
                    .filter(d -> d.getCategoryId() == selectedId)
                    .collect(Collectors.toList());
        }
        sortFilteredDishes(); // sắp xếp theo tiêu chí hiện tại
        pagination.updateTotalItems(filteredDishes.size());
        currentPage = 1;
        displayCurrentPage();
    }

    private void sortAndRefresh() {
        sortFilteredDishes();
        pagination.updateTotalItems(filteredDishes.size());
        currentPage = 1;
        displayCurrentPage();
    }

    private void sortFilteredDishes() {
        Comparator<Dish> comparator = null;
        switch (sortColumn) {
            case "ID":
                comparator = Comparator.comparingInt(Dish::getId);
                break;
            case "Tên món":
                comparator = Comparator.comparing(Dish::getName);
                break;
            case "Loại":
                // cần lấy tên loại để so sánh, nhưng ta không có sẵn, có thể dùng categoryId tạm, hoặc lấy từ CategoryDAO
                comparator = Comparator.comparingInt(Dish::getCategoryId);
                break;
            case "Giá":
                comparator = Comparator.comparing(Dish::getPrice);
                break;
            case "Ngày thêm":
                comparator = Comparator.comparing(Dish::getCreatedAt);
                break;
            default:
                comparator = Comparator.comparingInt(Dish::getId);
        }
        if (!sortAscending) {
            comparator = comparator.reversed();
        }
        filteredDishes.sort(comparator);
    }

    private void displayCurrentPage() {
        tableModel.setRowCount(0);
        int pageSize = pagination.getPageSize();
        int from = (currentPage - 1) * pageSize;
        int to = Math.min(from + pageSize, filteredDishes.size());
        for (int i = from; i < to; i++) {
            Dish d = filteredDishes.get(i);
            String categoryName = getCategoryName(d.getCategoryId());
            tableModel.addRow(new Object[]{
                    d.getId(),
                    d.getName(),
                    categoryName,
                    d.getPrice(),
                    d.getCreatedAt()
            });
        }
    }

    private String getCategoryName(int categoryId) {
        for (int i = 1; i < categoryCombo.getItemCount(); i++) {
            CategoryItem item = categoryCombo.getItemAt(i);
            if (item.getId() == categoryId) {
                return item.toString();
            }
        }
        return "Không xác định";
    }

    private void deleteSelectedDish() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần xóa!");
            return;
        }
        // Lấy ID từ model (dòng thực tế trong filteredDishes)
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int dishId = (int) tableModel.getValueAt(modelRow, 0);
        String dishName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa món \"" + dishName + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = adminController.deleteDish(dishId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa món thành công!");
                loadAllDishes();
                filterTable(); // tải lại và cập nhật
            } else {
                JOptionPane.showMessageDialog(this, "Xóa món thất bại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Dialog thêm món (giữ nguyên)
    private void showAddDishDialog() {
        // ... (giữ nguyên code dialog thêm món)
        // Code dialog thêm món như cũ (đã có)
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm món mới", true);
        dialog.setSize(450, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Tên món
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên món:*"), gbc);
        JTextField txtName = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtName, gbc);

        // Loại
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Loại:*"), gbc);
        JComboBox<CategoryItem> comboCategory = new JComboBox<>();
        for (Category cat : categoryDAO.getAllCategories()) {
            comboCategory.addItem(new CategoryItem(cat.getId(), cat.getName()));
        }
        gbc.gridx = 1;
        formPanel.add(comboCategory, gbc);

        // Giá
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Giá (VNĐ):*"), gbc);
        JTextField txtPrice = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtPrice, gbc);

        // Mô tả
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        JTextArea txtDesc = new JTextArea(3, 20);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        formPanel.add(scrollDesc, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;

        // Ảnh
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Tên file ảnh:"), gbc);
        JTextField txtImage = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtImage, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSave = new JButton("Lưu");
        btnSave.setBackground(new Color(0, 150, 136));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setFocusPainted(false);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên món!");
                return;
            }
            CategoryItem selectedCat = (CategoryItem) comboCategory.getSelectedItem();
            if (selectedCat == null) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn loại món!");
                return;
            }
            int catId = selectedCat.getId();

            BigDecimal price;
            try {
                price = new BigDecimal(txtPrice.getText().trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Giá phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá không hợp lệ!");
                return;
            }

            String desc = txtDesc.getText().trim();
            String imagePath = txtImage.getText().trim();

            boolean success = adminController.addDish(name, catId, price, desc, imagePath);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Thêm món thành công!");
                dialog.dispose();
                loadAllDishes();
                filterTable(); // cập nhật lại danh sách và hiển thị
            } else {
                JOptionPane.showMessageDialog(dialog, "Thêm món thất bại! Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}