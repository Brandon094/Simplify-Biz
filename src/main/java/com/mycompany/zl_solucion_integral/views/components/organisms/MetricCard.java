package com.mycompany.zl_solucion_integral.views.components.organisms;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import javax.swing.*;
import java.awt.*;

public class MetricCard extends RoundedPanel {
    public MetricCard(String title, String value, String trend, Color trendColor, String icon) {
        super(20, ThemeConstants.CARD_BACKGROUND);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        
        // Header con Título e Icono
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblTitle.setFont(ThemeConstants.FONT_SMALL);
        header.add(lblTitle, BorderLayout.WEST);

        JPanel iconBadge = new JPanel(new GridBagLayout());
        iconBadge.setOpaque(false);

        FlatSVGIcon metricIcon = new FlatSVGIcon(icon, 18, 18);
        metricIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, trendColor));
        JLabel lblIcon = new JLabel(metricIcon);
        lblIcon.setForeground(trendColor);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        iconBadge.add(lblIcon);

        header.add(iconBadge, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblValue.setFont(ThemeConstants.FONT_TITLE.deriveFont(Font.BOLD, 22));
        add(lblValue, BorderLayout.CENTER);
        
        JLabel lblTrend = new JLabel(trend);
        lblTrend.setForeground(trendColor);
        lblTrend.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        add(lblTrend, BorderLayout.SOUTH);
    }
}
