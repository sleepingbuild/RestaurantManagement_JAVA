package com.restaurant.view.panels;

import com.restaurant.controller.BookingController;
import com.restaurant.util.AppColors;
import com.restaurant.util.UIHelper;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class BookingPanel extends JPanel {

    private JTextField txtName, txtPhone, txtEmail, txtGuests;
    private JSpinner spinnerDateTime;
    private JTextArea txtRequest;
    private BookingController bookingController;

    public BookingPanel() {
        bookingController = new BookingController();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // Thêm 2 cột đỏ hai bên (full chiều cao)
        JPanel leftRed = new JPanel();
        leftRed.setBackground(new Color(200,40,40));
        leftRed.setPreferredSize(new Dimension(80, 0)); // Chiều rộng 80px
        add(leftRed, BorderLayout.WEST);

        JPanel rightRed = new JPanel();
        rightRed.setBackground(new Color(200,40,40));
        rightRed.setPreferredSize(new Dimension(80, 0));
        add(rightRed, BorderLayout.EAST);

        // Panel trung tâm chứa nội dung chính (căn giữa)
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(245, 245, 250));
        JPanel formPanel = createFormPanel();
        centerWrapper.add(formPanel);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 30", "[right][grow]", "[]15[]15[]15[]15[]15[]15[]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // Khởi tạo các thành phần
        txtName = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);
        txtGuests = new JTextField(5);
        txtRequest = new JTextArea(3, 20);
        txtRequest.setLineWrap(true);
        txtRequest.setWrapStyleWord(true);
        txtRequest.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        spinnerDateTime = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerDateTime, "dd/MM/yyyy HH:mm");
        spinnerDateTime.setEditor(dateEditor);
        spinnerDateTime.setValue(new Date());

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel lblName = new JLabel(" Họ tên:");
        lblName.setFont(labelFont);
        panel.add(lblName, "cell 0 0");
        txtName.setFont(fieldFont);
        panel.add(txtName, "cell 1 0, growx");

        JLabel lblPhone = new JLabel(" SĐT:");
        lblPhone.setFont(labelFont);
        panel.add(lblPhone, "cell 0 1");
        txtPhone.setFont(fieldFont);
        panel.add(txtPhone, "cell 1 1, growx");

        JLabel lblEmail = new JLabel(" Email:");
        lblEmail.setFont(labelFont);
        panel.add(lblEmail, "cell 0 2");
        txtEmail.setFont(fieldFont);
        panel.add(txtEmail, "cell 1 2, growx");

        JLabel lblDateTime = new JLabel(" Ngày & giờ:");
        lblDateTime.setFont(labelFont);
        panel.add(lblDateTime, "cell 0 3");
        panel.add(spinnerDateTime, "cell 1 3, growx");

        JLabel lblGuests = new JLabel(" Số khách:");
        lblGuests.setFont(labelFont);
        panel.add(lblGuests, "cell 0 4");
        txtGuests.setFont(fieldFont);
        panel.add(txtGuests, "cell 1 4, growx");

        JLabel lblRequest = new JLabel(" Yêu cầu:");
        lblRequest.setFont(labelFont);
        panel.add(lblRequest, "cell 0 5");
        JScrollPane scrollRequest = new JScrollPane(txtRequest);
        scrollRequest.setPreferredSize(new Dimension(200, 80));
        panel.add(scrollRequest, "cell 1 5, growx");

        JButton btnBook = new JButton("ĐẶT BÀN NGAY");
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnBook.setBackground(AppColors.PRIMARY);
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBook.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        UIHelper.applyHoverEffect(btnBook, AppColors.PRIMARY, new Color(100, 160, 210));

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.setBackground(Color.WHITE);
        buttonWrapper.add(btnBook);
        panel.add(buttonWrapper, "cell 0 6 2 1, center, gaptop 20");

        btnBook.addActionListener(e -> bookTable());

        return panel;
    }

    private void bookTable() {
        try {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            Date bookingTime = (Date) spinnerDateTime.getValue();
            int guests;
            try {
                guests = Integer.parseInt(txtGuests.getText().trim());
                if (guests <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số khách phải là số nguyên dương!");
                return;
            }
            String request = txtRequest.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên và số điện thoại!");
                return;
            }

            if (bookingTime.before(new Date())) {
                JOptionPane.showMessageDialog(this, "Ngày giờ đặt bàn không được ở quá khứ!");
                return;
            }

            boolean success = bookingController.createBooking(name, phone, email, bookingTime, guests, request);
            if (success) {
                JOptionPane.showMessageDialog(this, "Đặt bàn thành công! Chúng tôi sẽ liên hệ sớm.");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Đặt bàn thất bại! Vui lòng thử lại.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        spinnerDateTime.setValue(new Date());
        txtGuests.setText("");
        txtRequest.setText("");
    }
}