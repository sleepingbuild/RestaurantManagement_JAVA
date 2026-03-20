package com.restaurant.view.panels;

import com.restaurant.util.UIHelper;

import javax.swing.*;
import java.awt.*;

public class IntroductionPanel extends JPanel {

    public IntroductionPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createBanner(), BorderLayout.NORTH);
        add(createInfoSection(), BorderLayout.CENTER);
    }

    private JPanel createBanner(){

        JPanel banner = new JPanel();
        banner.setPreferredSize(new Dimension(1200,200));
        banner.setBackground(new Color(192,32,32));
        banner.setLayout(new BoxLayout(banner,BoxLayout.Y_AXIS));

        JLabel title = new JLabel("BruhBruh");
        title.setFont(new Font("Segoe UI",Font.BOLD,42));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Ẩm thực Việt Nam - Đậm đà hương vị");
        subtitle.setFont(new Font("Segoe UI",Font.PLAIN,18));
        subtitle.setForeground(Color.WHITE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        banner.add(Box.createVerticalGlue());
        banner.add(title);
        banner.add(Box.createRigidArea(new Dimension(0,10)));
        banner.add(subtitle);
        banner.add(Box.createVerticalGlue());

        return banner;
    }

    private JPanel createInfoSection(){

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);

        JLabel welcome = new JLabel("Chào mừng đến với nhà hàng BruhBruh");
        welcome.setFont(new Font("Segoe UI",Font.BOLD,22));
        welcome.setHorizontalAlignment(SwingConstants.CENTER);

        container.add(welcome,BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1,3,30,30));
        cards.setBorder(BorderFactory.createEmptyBorder(40,80,40,80));
        cards.setBackground(Color.WHITE);

        cards.add(createCard(
                "Ẩm thực Việt Nam",
                "Chúng tôi chuyên phục vụ các món ăn đậm đà hương vị truyền thống."
        ));

        cards.add(createCard(
                "Nguyên liệu tươi",
                "Nguyên liệu được chọn lọc kỹ lưỡng mỗi ngày."
        ));

        cards.add(createCard(
                "Giờ mở cửa",
                "10:00 - 22:00 mỗi ngày."
        ));

        container.add(cards,BorderLayout.CENTER);

        return container;
    }

    private JPanel createCard(String title,String text){

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));

        card.setBackground(new Color(245,245,245));
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI",Font.BOLD,18));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel d = new JLabel("<html><div style='text-align:center;'>"+text+"</div></html>");
        d.setFont(new Font("Segoe UI",Font.PLAIN,14));
        d.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(t);
        card.add(Box.createRigidArea(new Dimension(0,10)));
        card.add(d);
        card.add(Box.createVerticalGlue());

        UIHelper.applyCardHover(card);
        return card;
    }
}

