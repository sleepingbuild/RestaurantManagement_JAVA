package com.restaurant.view.panels;

import com.restaurant.controller.BookingController;
import com.restaurant.util.AppColors;
import com.restaurant.util.UIHelper;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookingPanel extends JPanel {

    private JTextField txtName, txtPhone, txtEmail, txtGuests;
    private JSpinner spinnerDateTime;
    private JTextArea txtRequest;
    private BookingController bookingController;
    private SimpleDateFormat dateTimeFormat;

    public BookingPanel() {
        bookingController = new BookingController();
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        setLayout(new BorderLayout());
        setBackground(AppColors.BG_LIGHT);

        JLabel title = new JLabel("📅 Đặt bàn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        add(title, BorderLayout.NORTH);
        createForm();
    }

    private void createForm() {
        JPanel form = new JPanel(new MigLayout("fill, insets 20", "[right][grow]", "[]10[]10[]10[]10[]10[]10[]"));
        form.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        form.setBackground(Color.WHITE);

        txtName = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);
        txtGuests = new JTextField(5);
        txtRequest = new JTextArea(3, 20);

        // Spinner chọn ngày giờ
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        spinnerDateTime = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerDateTime, "dd/MM/yyyy HH:mm");
        spinnerDateTime.setEditor(dateEditor);
        spinnerDateTime.setValue(new Date()); // mặc định là thời điểm hiện tại

        // Thêm các trường vào form
        form.add(new JLabel("Họ tên:"), "cell 0 0");
        form.add(txtName, "cell 1 0, growx");
        form.add(new JLabel("SĐT:"), "cell 0 1");
        form.add(txtPhone, "cell 1 1, growx");
        form.add(new JLabel("Email:"), "cell 0 2");
        form.add(txtEmail, "cell 1 2, growx");
        form.add(new JLabel("Ngày & giờ:"), "cell 0 3");
        form.add(spinnerDateTime, "cell 1 3, growx");
        form.add(new JLabel("Số khách:"), "cell 0 4");
        form.add(txtGuests, "cell 1 4, growx");
        form.add(new JLabel("Yêu cầu:"), "cell 0 5");
        form.add(new JScrollPane(txtRequest), "cell 1 5, growx");

        JButton btnBook = new JButton("Đặt bàn");
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        UIHelper.applyHoverEffect(btnBook, AppColors.PRIMARY, new Color(100, 160, 210));

        JPanel wrapper = new JPanel();
        wrapper.setBackground(AppColors.BG_LIGHT);
        wrapper.add(btnBook);

        btnBook.addActionListener(e -> bookTable());

        add(form, BorderLayout.CENTER);
        add(wrapper, BorderLayout.SOUTH);
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
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số khách không hợp lệ!");
                return;
            }
            String request = txtRequest.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên và số điện thoại!");
                return;
            }

            boolean success = bookingController.createBooking(
                    name, phone, email, bookingTime, guests, request
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Đặt bàn thành công!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Đặt bàn thất bại!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
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