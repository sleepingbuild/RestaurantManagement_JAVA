package com.restaurant.view.admin;

import com.restaurant.controller.AdminController;
import com.restaurant.model.Order;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;

import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class RevenuePanel extends JPanel {

    private AdminController adminController;

    public RevenuePanel() {

        adminController = new AdminController();

        setLayout(new BorderLayout(15,15));
        setBackground(new Color(245,247,250));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel title = new JLabel("THỐNG KÊ DOANH THU");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(60,60,60));

        add(title, BorderLayout.NORTH);

        add(createChartPanel(), BorderLayout.CENTER);

        add(createTotalPanel(), BorderLayout.SOUTH);
    }

    private ChartPanel createChartPanel() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -5);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

        for(int i=0;i<6;i++){

            Date start = getStartOfMonth(cal.getTime());
            Date end = getEndOfMonth(cal.getTime());

            List<Order> orders = adminController.getOrdersByDateRange(start,end);

            BigDecimal total = orders.stream()
                    .filter(o -> "paid".equals(o.getStatus()))
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dataset.addValue(total.doubleValue(),"Doanh thu",sdf.format(cal.getTime()));

            cal.add(Calendar.MONTH,1);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Doanh thu 6 tháng gần nhất",
                "Tháng",
                "VND",
                dataset,
                PlotOrientation.VERTICAL,
                false,true,false
        );

        CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0,new Color(70,130,180));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(
                java.text.NumberFormat.getCurrencyInstance(
                        new java.util.Locale("vi","VN")));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.UP_45);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800,450));

        return chartPanel;
    }

    private JPanel createTotalPanel(){

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(new Color(245,247,250));

        BigDecimal totalRevenue = adminController.getTotalRevenue();

        JLabel lblTotal = new JLabel(
                "Tổng doanh thu: "
                        + String.format("%,.0f",totalRevenue)
                        + " VND"
        );

        lblTotal.setFont(new Font("Segoe UI",Font.BOLD,18));
        lblTotal.setForeground(new Color(70,130,180));

        panel.add(lblTotal);

        return panel;
    }

    private Date getStartOfMonth(Date date){

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMinimum(Calendar.DAY_OF_MONTH));

        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        return cal.getTime();
    }

    private Date getEndOfMonth(Date date){

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);

        return cal.getTime();
    }
}