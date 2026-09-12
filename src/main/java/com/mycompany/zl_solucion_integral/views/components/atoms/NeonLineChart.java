package com.mycompany.zl_solucion_integral.views.components.atoms;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        int left = 48;
        int right = 18;
        int top = 42;
        int bottom = 28;
        int chartWidth = Math.max(1, width - left - right);
        int chartHeight = Math.max(1, height - top - bottom);

        // Draw Title
        g2.setColor(ThemeConstants.TEXT_SECONDARY);
        g2.setFont(ThemeConstants.FONT_SMALL);
        g2.drawString(title, left, 20);

        if (data == null || data.size() < 2 || data.stream().allMatch(value -> value == null || value <= 0)) {
            FlatSVGIcon emptyIcon = new FlatSVGIcon("icons/sales.svg", 22, 22);
            emptyIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_BLUE));
            emptyIcon.paintIcon(this, g2, left, height / 2 - 28);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.setFont(ThemeConstants.FONT_BODY);
            g2.drawString("Aún no hay ventas registradas", left + 32, height / 2 - 10);
            g2.dispose();
            return;
        }

        double max = data.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        if (max <= 0) max = 1.0;

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        numberFormat.setMaximumFractionDigits(0);

        g2.setFont(ThemeConstants.FONT_SMALL);
        for (int step = 0; step <= 3; step++) {
            int y = top + (chartHeight * step / 3);
            g2.setColor(new Color(148, 163, 184, 35));
            g2.drawLine(left, y, left + chartWidth, y);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.drawString(numberFormat.format(max - (max * step / 3)), 4, y + 4);
        }

        // Draw Line
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Gradient for line
        GradientPaint gradient = new GradientPaint(0, 0, ThemeConstants.NEON_PURPLE, width, 0, ThemeConstants.NEON_BLUE);
        g2.setPaint(gradient);

        Path2D.Double path = new Path2D.Double();
        double xInc = (double) chartWidth / (data.size() - 1);
        int[] pointX = new int[data.size()];
        int[] pointY = new int[data.size()];
        
        for (int i = 0; i < data.size(); i++) {
            double x = left + (i * xInc);
            double y = (top + chartHeight) - (data.get(i) / max * chartHeight);
            pointX[i] = (int) x;
            pointY[i] = (int) y;
            
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            
        }
        
        g2.draw(path);

        for (int i = 0; i < pointX.length; i++) {
            g2.fillOval(pointX[i] - 4, pointY[i] - 4, 8, 8);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.setFont(ThemeConstants.FONT_SMALL);
            g2.drawString("D" + (i + 1), pointX[i] - 7, height - 8);
            g2.setPaint(gradient);
        }

        // Draw area under line (semi-transparent)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
        path.lineTo(left + chartWidth, top + chartHeight);
        path.lineTo(left, top + chartHeight);
        path.closePath();
        g2.fill(path);

        g2.dispose();
    }
}
