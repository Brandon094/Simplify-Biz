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
        ThemeConstants.NEON_PURPLE, // #A855F7
        ThemeConstants.NEON_BLUE,   // #3B82F6
        ThemeConstants.NEON_CYAN,   // #06B6D4
        ThemeConstants.NEON_GREEN,  // #22C55E
        new Color(249, 115, 22),   // Naranja Neón (#F97316)
        new Color(236, 72, 153),   // Rosa / Fucsia Neón (#EC4899)
        new Color(245, 158, 11),   // Ámbar (#F59E0B)
        new Color(99, 102, 241),   // Índigo (#6366F1)
        new Color(16, 185, 129),   // Esmeralda (#10B981)
        ThemeConstants.NEON_RED    // Rojo Neón (#EF4444)
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
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 16;

        // Título del gráfico
        g2.setColor(ThemeConstants.TEXT_PRIMARY);
        g2.setFont(ThemeConstants.FONT_SUBTITLE);
        g2.drawString(title, padding, padding + 10);

        if (data == null || data.isEmpty()) {
            FlatSVGIcon emptyIcon = new FlatSVGIcon("icons/products.svg", 22, 22);
            emptyIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_CYAN));
            emptyIcon.paintIcon(this, g2, padding, height / 2 - 10);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.setFont(ThemeConstants.FONT_BODY);
            g2.drawString("Aún no hay inventario registrado", padding + 32, height / 2 + 6);
            g2.dispose();
            return;
        }

        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total <= 0) {
            FlatSVGIcon emptyIcon = new FlatSVGIcon("icons/products.svg", 22, 22);
            emptyIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_CYAN));
            emptyIcon.paintIcon(this, g2, padding, height / 2 - 10);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.setFont(ThemeConstants.FONT_BODY);
            g2.drawString("Aún no hay inventario registrado", padding + 32, height / 2 + 6);
            g2.dispose();
            return;
        }

        // Posicionamiento responsivo del Donut y Leyenda
        int topMargin = 38;
        int chartAreaHeight = Math.max(10, height - topMargin - padding);
        int diameter = Math.min(Math.max(10, width / 3), chartAreaHeight);
        int x = padding + 5;
        int y = topMargin + (chartAreaHeight - diameter) / 2;

        int legendX = x + diameter + 20;
        int legendWidth = Math.max(50, width - legendX - padding);

        // 1. Dibujar rebanadas del Donut Chart
        double curAngle = 0;
        int colorIdx = 0;

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double angle = (entry.getValue() / total) * 360;
            g2.setColor(neonColors[colorIdx % neonColors.length]);
            g2.fillArc(x, y, diameter, diameter, (int) Math.round(curAngle), (int) Math.ceil(angle));
            curAngle += angle;
            colorIdx++;
        }

        // 2. Agujero central para efecto Donut
        g2.setColor(ThemeConstants.CARD_BACKGROUND);
        int innerDiameter = (int) (diameter * 0.68);
        int ix = x + (diameter - innerDiameter) / 2;
        int iy = y + (diameter - innerDiameter) / 2;
        g2.fillOval(ix, iy, innerDiameter, innerDiameter);

        // Texto central con el total de unidades
        g2.setColor(ThemeConstants.TEXT_PRIMARY);
        g2.setFont(ThemeConstants.FONT_TITLE.deriveFont(Font.BOLD, 15f));
        String totalStr = String.valueOf((int) total);
        FontMetrics fmTotal = g2.getFontMetrics();
        g2.drawString(totalStr, ix + (innerDiameter - fmTotal.stringWidth(totalStr)) / 2, iy + (innerDiameter / 2) + 2);
        
        g2.setFont(ThemeConstants.FONT_SMALL.deriveFont(10f));
        g2.setColor(ThemeConstants.TEXT_SECONDARY);
        String labelUnits = "unid.";
        FontMetrics fmUnits = g2.getFontMetrics();
        g2.drawString(labelUnits, ix + (innerDiameter - fmUnits.stringWidth(labelUnits)) / 2, iy + (innerDiameter / 2) + 14);

        // 3. Leyenda lateral con colores y cantidades
        colorIdx = 0;
        int numItems = data.size();
        int stepY = numItems > 6 ? 19 : 23;
        int startLegendY = topMargin + Math.max(0, (chartAreaHeight - (numItems * stepY)) / 2);

        g2.setFont(ThemeConstants.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            Color color = neonColors[colorIdx % neonColors.length];
            int currentY = startLegendY + (colorIdx * stepY);

            // Cuadrito de color
            g2.setColor(color);
            g2.fillRoundRect(legendX, currentY + 2, 10, 10, 3, 3);

            // Texto de leyenda (Categoría + unidades)
            int val = entry.getValue().intValue();
            String fullText = entry.getKey() + " (" + val + ")";
            
            // Recorte elegante ajustado al ancho disponible
            String textToDraw = fullText;
            if (fm.stringWidth(textToDraw) > legendWidth - 16) {
                String catName = entry.getKey();
                while (catName.length() > 3 && fm.stringWidth(catName + "... (" + val + ")") > legendWidth - 16) {
                    catName = catName.substring(0, catName.length() - 1);
                }
                textToDraw = catName + "... (" + val + ")";
            }

            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.drawString(textToDraw, legendX + 16, currentY + 11);
            colorIdx++;
        }

        g2.dispose();
    }
}
