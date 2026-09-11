package com.mycompany.zl_solucion_integral.views.components.atoms;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class NeonLineChart extends JPanel {
    private List<Double> data = new ArrayList<>();
    private String title;

    public NeonLineChart(String title, List<Double> data) {
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
        int padding = 40;
        int chartWidth = width - (padding * 2);
        int chartHeight = height - (padding * 2);

        // Draw Title
        g2.setColor(ThemeConstants.TEXT_SECONDARY);
        g2.setFont(ThemeConstants.FONT_SMALL);
        g2.drawString(title, padding, padding / 2);

        if (data == null || data.size() < 2) return;

        double max = data.stream().max(Double::compare).orElse(1.0);
        if (max == 0) max = 1.0;

        // Draw Line
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Gradient for line
        GradientPaint gradient = new GradientPaint(0, 0, ThemeConstants.NEON_PURPLE, width, 0, ThemeConstants.NEON_BLUE);
        g2.setPaint(gradient);

        Path2D.Double path = new Path2D.Double();
        double xInc = (double) chartWidth / (data.size() - 1);
        
        for (int i = 0; i < data.size(); i++) {
            double x = padding + (i * xInc);
            double y = (padding + chartHeight) - (data.get(i) / max * chartHeight);
            
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            
            // Draw points
            g2.fillOval((int)x - 4, (int)y - 4, 8, 8);
        }
        
        g2.draw(path);

        // Draw area under line (semi-transparent)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
        path.lineTo(padding + chartWidth, padding + chartHeight);
        path.lineTo(padding, padding + chartHeight);
        path.closePath();
        g2.fill(path);

        g2.dispose();
    }
}
