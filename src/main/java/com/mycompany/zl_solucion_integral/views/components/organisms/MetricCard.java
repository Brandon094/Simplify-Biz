package com.mycompany.zl_solucion_integral.views.components.organisms;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import javax.swing.*;
import java.awt.*;

public class MetricCard extends RoundedPanel {
    public MetricCard(String title, String value, String trend, Color trendColor, String icon) {
        super(20, ThemeConstants.CARD_BACKGROUND);
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header con Título e Icono
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblTitle.setFont(ThemeConstants.FONT_SMALL);
        header.add(lblTitle, BorderLayout.WEST);

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setForeground(trendColor);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        header.add(lblIcon, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblValue.setFont(ThemeConstants.FONT_TITLE);
        add(lblValue, BorderLayout.CENTER);
        
        JLabel lblTrend = new JLabel(trend);
        lblTrend.setForeground(trendColor);
        lblTrend.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        add(lblTrend, BorderLayout.SOUTH);
    }
}
