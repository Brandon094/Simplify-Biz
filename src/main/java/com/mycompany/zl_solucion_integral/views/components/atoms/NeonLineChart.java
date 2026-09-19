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
    private List<Double> comparativeData = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private final String title;
    private final String iconPath;

    public NeonLineChart(String title, List<Double> data) {
        this(title, "icons/chart-line.svg", data, null, null);
    }

    public NeonLineChart(String title, String iconPath, List<Double> data) {
        this(title, iconPath, data, null, null);
    }

    public NeonLineChart(String title, String iconPath, List<Double> data, List<String> labels) {
        this(title, iconPath, data, labels, null);
    }

    public NeonLineChart(String title, String iconPath, List<Double> data, List<String> labels, List<Double> comparativeData) {
        this.title = title;
        this.iconPath = iconPath;
        this.data = data;
        this.labels = labels;
        this.comparativeData = comparativeData;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int left = 55;
        int right = 18;
        int top = 42;
        int bottom = 28;
        int chartWidth = Math.max(1, width - left - right);
        int chartHeight = Math.max(1, height - top - bottom);

        // Draw Title with SVG Icon (Align left at 16px padding)
        int padding = 16;
        int titleX = padding;
        if (iconPath != null) {
            FlatSVGIcon icon = new FlatSVGIcon(iconPath, 18, 18);
            icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_PURPLE));
            icon.paintIcon(this, g2, padding, 6);
            titleX = padding + 26;
        }

        g2.setColor(ThemeConstants.TEXT_PRIMARY);
        g2.setFont(ThemeConstants.FONT_SUBTITLE.deriveFont(Font.BOLD, 14f));
        g2.drawString(title, titleX, 20);

        // Draw Comparison Percentage Badge near the title if comparative data exists
        boolean tieneComparativa = comparativeData != null && comparativeData.size() >= 2 
                && comparativeData.stream().anyMatch(v -> v != null && v > 0);

        if (tieneComparativa && data != null && !data.isEmpty()) {
            double sumActual = data.stream().filter(v -> v != null).mapToDouble(Double::doubleValue).sum();
            double sumAnt = comparativeData.stream().filter(v -> v != null).mapToDouble(Double::doubleValue).sum();

            if (sumAnt > 0) {
                double pctCambio = ((sumActual - sumAnt) / sumAnt) * 100.0;
                String badgeText = String.format("%s%.1f%% vs per. anterior", (pctCambio >= 0 ? "+" : ""), pctCambio);
                
                int titleWidth = g2.getFontMetrics().stringWidth(title);
                int badgeX = titleX + titleWidth + 12;
                int badgeY = 6;
                int badgeW = g2.getFontMetrics().stringWidth(badgeText) + 12;
                int badgeH = 18;

                Color colorBgBadge = pctCambio >= 0 ? ThemeConstants.withAlpha(ThemeConstants.NEON_GREEN, 35) : ThemeConstants.withAlpha(ThemeConstants.NEON_RED, 35);
                Color colorFgBadge = pctCambio >= 0 ? ThemeConstants.NEON_GREEN : ThemeConstants.NEON_RED;

                g2.setColor(colorBgBadge);
                g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 10, 10);
                g2.setColor(colorFgBadge);
                g2.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 10.5f));
                g2.drawString(badgeText, badgeX + 6, badgeY + 13);
            }
        }

        boolean tieneAlMenosUnaVenta = data != null && data.stream().anyMatch(value -> value != null && value > 0);
        if (data == null || data.size() < 2 || !tieneAlMenosUnaVenta) {
            FlatSVGIcon emptyIcon = new FlatSVGIcon("icons/sales.svg", 22, 22);
            emptyIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_BLUE));
            emptyIcon.paintIcon(this, g2, left, height / 2 - 28);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.setFont(ThemeConstants.FONT_BODY);
            g2.drawString("Aún no hay suficiente historial para este período", left + 32, height / 2 - 10);
            g2.dispose();
            return;
        }

        double maxData = data.stream().filter(v -> v != null).mapToDouble(Double::doubleValue).max().orElse(1.0);
        double maxComp = (comparativeData != null && !comparativeData.isEmpty()) 
                ? comparativeData.stream().filter(v -> v != null).mapToDouble(Double::doubleValue).max().orElse(0.0) 
                : 0.0;
        double max = Math.max(maxData, maxComp);
        if (max <= 0) max = 1.0;

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        numberFormat.setMaximumFractionDigits(0);

        g2.setFont(ThemeConstants.FONT_SMALL);
        for (int step = 0; step <= 3; step++) {
            int y = top + (chartHeight * step / 3);
            g2.setColor(ThemeConstants.GRID_LINE);
            g2.drawLine(left, y, left + chartWidth, y);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.drawString(numberFormat.format(max - (max * step / 3)), 4, y + 4);
        }

        if (tieneComparativa) {
            Stroke dashed = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{4, 4}, 0);
            g2.setStroke(dashed);
            g2.setColor(ThemeConstants.withAlpha(ThemeConstants.TEXT_SECONDARY, 140));

            Path2D.Double compPath = new Path2D.Double();
            double cInc = (double) chartWidth / (comparativeData.size() - 1);
            for (int i = 0; i < comparativeData.size(); i++) {
                double val = comparativeData.get(i) != null ? comparativeData.get(i) : 0.0;
                double cx = left + (i * cInc);
                double cy = (top + chartHeight) - (val / max * chartHeight);
                if (i == 0) compPath.moveTo(cx, cy);
                else compPath.lineTo(cx, cy);
            }
            g2.draw(compPath);
        }

        // Draw Primary Line
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Gradient for main line
        GradientPaint gradient = new GradientPaint(0, 0, ThemeConstants.NEON_PURPLE, width, 0, ThemeConstants.NEON_BLUE);
        g2.setPaint(gradient);

        Path2D.Double path = new Path2D.Double();
        double xInc = (double) chartWidth / (data.size() - 1);
        int[] pointX = new int[data.size()];
        int[] pointY = new int[data.size()];
        
        for (int i = 0; i < data.size(); i++) {
            double val = data.get(i) != null ? data.get(i) : 0.0;
            double x = left + (i * xInc);
            double y = (top + chartHeight) - (val / max * chartHeight);
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
            g2.setPaint(gradient);
            g2.fillOval(pointX[i] - 4, pointY[i] - 4, 8, 8);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.setFont(ThemeConstants.FONT_SMALL.deriveFont(10.5f));
            String lblX = (labels != null && i < labels.size() && labels.get(i) != null) ? labels.get(i) : "";
            if (!lblX.isEmpty()) {
                int lblWidth = g2.getFontMetrics().stringWidth(lblX);
                g2.drawString(lblX, pointX[i] - (lblWidth / 2), height - 8);
            }
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
