package com.mycompany.zl_solucion_integral.views.components.atoms;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class NeonPieChart extends JPanel {
    private final Map<String, Double> data;
    private final String title;
    private final Color[] neonColors = {
        ThemeConstants.NEON_PURPLE,
        ThemeConstants.NEON_BLUE,
        ThemeConstants.NEON_CYAN,
        ThemeConstants.NEON_GREEN,
        ThemeConstants.NEON_RED
    };

    public NeonPieChart(String title, Map<String, Double> data) {
        this.title = title;
        this.data = data;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 24;

        // Draw Title
        g2.setColor(ThemeConstants.TEXT_PRIMARY);
        g2.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        g2.drawString(title, padding, padding);

        if (data == null || data.isEmpty()) {
            FlatSVGIcon emptyIcon = new FlatSVGIcon("icons/products.svg", 22, 22);
            emptyIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_CYAN));
            emptyIcon.paintIcon(this, g2, padding, height / 2 - 28);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.setFont(ThemeConstants.FONT_BODY);
            g2.drawString("Aún no hay inventario registrado", padding + 32, height / 2 - 10);
            g2.dispose();
            return;
        }

        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total <= 0) {
            FlatSVGIcon emptyIcon = new FlatSVGIcon("icons/products.svg", 22, 22);
            emptyIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_CYAN));
            emptyIcon.paintIcon(this, g2, padding, height / 2 - 28);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.setFont(ThemeConstants.FONT_BODY);
            g2.drawString("Aún no hay inventario registrado", padding + 32, height / 2 - 10);
            g2.dispose();
            return;
        }

        int legendX = Math.max(width / 2 + 20, width - 135);
        int chartAreaWidth = Math.max(1, legendX - padding - 18);
        int diameter = Math.min(chartAreaWidth, Math.max(1, height - 58));
        int x = padding + (chartAreaWidth - diameter) / 2;
        int y = 38 + Math.max(0, (height - 58 - diameter) / 2);
        double curAngle = 0;
        int colorIdx = 0;

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double angle = (entry.getValue() / total) * 360;
            
            // Draw slice
            g2.setColor(neonColors[colorIdx % neonColors.length]);
            g2.fillArc(x, y, diameter, diameter, (int)curAngle, (int)angle);
            
            // Draw "Donut" hole to make it modern
            g2.setColor(ThemeConstants.CARD_BACKGROUND);
            int innerDiameter = (int)(diameter * 0.7);
            int ix = x + (diameter - innerDiameter) / 2;
            int iy = y + (diameter - innerDiameter) / 2;
            g2.fillOval(ix, iy, innerDiameter, innerDiameter);

            // Draw Legend
            g2.setColor(neonColors[colorIdx % neonColors.length]);
            int legendY = 46 + (colorIdx * 22);
            g2.fillRoundRect(legendX, legendY, 10, 10, 3, 3);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.setFont(ThemeConstants.FONT_SMALL);
            String label = entry.getKey();
            if (label.length() > 14) label = label.substring(0, 13) + "...";
            g2.drawString(label, legendX + 17, legendY + 9);

            curAngle += angle;
            colorIdx++;
        }

        g2.dispose();
    }
}
