package com.mycompany.zl_solucion_integral.views.components.atoms;

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
        int padding = 30;
        int diameter = Math.min(width, height) - (padding * 2);
        int x = (width - diameter) / 2;
        int y = (height - diameter) / 2;

        // Draw Title
        g2.setColor(ThemeConstants.TEXT_PRIMARY);
        g2.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        g2.drawString(title, padding, padding);

        if (data == null || data.isEmpty()) return;

        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
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
            int ix = (width - innerDiameter) / 2;
            int iy = (height - innerDiameter) / 2;
            g2.fillOval(ix, iy, innerDiameter, innerDiameter);

            // Draw Legend
            g2.setColor(neonColors[colorIdx % neonColors.length]);
            g2.fillRect(width - 100, padding + (colorIdx * 20) + 10, 10, 10);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.setFont(ThemeConstants.FONT_SMALL);
            g2.drawString(entry.getKey(), width - 85, padding + (colorIdx * 20) + 20);

            curAngle += angle;
            colorIdx++;
        }

        g2.dispose();
    }
}
