package com.restaurant.util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIHelper {


    public static void applyHoverEffect(JButton button, Color normal, Color hover){

        button.setBackground(normal);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){
                button.setBackground(hover);
            }

            public void mouseExited(MouseEvent e){
                button.setBackground(normal);
            }
        });
    }


    public static void applyCardHover(JPanel panel){

        panel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){
                panel.setBorder(BorderFactory.createLineBorder(new Color(70,130,180),2));
            }

            public void mouseExited(MouseEvent e){
                panel.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
            }
        });
    }


    public static void smoothScroll(JScrollPane scroll){

        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
    }

    
    public static void styleTable(JTable table){

        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI",Font.PLAIN,14));
        table.setSelectionBackground(new Color(220,240,255));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI",Font.BOLD,14));
        header.setBackground(new Color(70,130,180));
        header.setForeground(Color.WHITE);
    }
}
