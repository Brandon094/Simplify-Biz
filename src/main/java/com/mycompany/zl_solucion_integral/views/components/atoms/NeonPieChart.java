package com.mycompany.zl_solucion_integral.views.components.atoms;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class NeonPieChart extends JPanel {
    private final Map<String, Double> data;
    private final String title;
    private final String iconPath;
    private FlatSVGIcon svgIcon;
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

    private int hoverIndex = -1;

    public NeonPieChart(String title, Map<String, Double> data) {
        this(title, "icons/chart-pie.svg", data);
    }

    public NeonPieChart(String title, String iconPath, Map<String, Double> data) {
        this.title = title;
        this.iconPath = iconPath;
        this.data = data;
        setOpaque(false);
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                svgIcon = new FlatSVGIcon(iconPath, 18, 18);
                svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_PURPLE));
            } catch (Exception e) {
                svgIcon = null;
            }
        }

        java.awt.event.MouseAdapter adapter = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                checkHover(e.getPoint());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (hoverIndex != -1) {
                    hoverIndex = -1;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                }
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    private void checkHover(Point p) {
        if (data == null || data.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int padding = 16;
        int topMargin = 38;
        int chartAreaHeight = Math.max(10, height - topMargin - padding);
        int diameter = Math.min(Math.max(10, width / 3), chartAreaHeight);
        int x = padding + 5;
        int y = topMargin + (chartAreaHeight - diameter) / 2;
        int cx = x + diameter / 2;
        int cy = y + diameter / 2;

        int legendX = x + diameter + 20;
        int legendWidth = Math.max(50, width - legendX - padding);

        Map<String, Double> sortedData = getSortedData();
        double total = sortedData.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total <= 0) return;

        int newHover = -1;
        int numItems = sortedData.size();
        int stepY = numItems > 5 ? 19 : 22;
        int startLegendY = topMargin + Math.max(0, (chartAreaHeight - (numItems * stepY)) / 2);

        // 1. Verificar si está sobre la Leyenda
        for (int i = 0; i < numItems; i++) {
            int itemY = startLegendY + (i * stepY);
            Rectangle rect = new Rectangle(legendX - 4, itemY, legendWidth + 8, stepY);
            if (rect.contains(p)) {
                newHover = i;
                break;
            }
        }

        // 2. Verificar si está sobre las rebanadas del Donut
        if (newHover == -1) {
            double dist = p.distance(cx, cy);
            double outerR = (diameter / 2.0) + 12;
            double innerR = (diameter * 0.68 / 2.0);

            if (dist >= innerR && dist <= outerR) {
                double dx = p.x - cx;
                double dy = cy - p.y; // Y invertido para plano cartesiano
                double angleDeg = Math.toDegrees(Math.atan2(dy, dx));
                if (angleDeg < 0) angleDeg += 360;

                double curAngle = 0;
                int idx = 0;
                for (Map.Entry<String, Double> entry : sortedData.entrySet()) {
                    double sliceAngle = (entry.getValue() / total) * 360;
                    double endAngle = curAngle + sliceAngle;

                    if (angleDeg >= curAngle && angleDeg <= endAngle) {
                        newHover = idx;
                        break;
                    }
                    curAngle += sliceAngle;
                    idx++;
                }
            }
        }

        if (newHover != hoverIndex) {
            hoverIndex = newHover;
            setCursor(hoverIndex != -1 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            repaint();
        }
    }

    private Map<String, Double> getSortedData() {
        Map<String, Double> sortedData = new java.util.LinkedHashMap<>();
        if (data == null || data.isEmpty()) return sortedData;

        java.util.List<Map.Entry<String, Double>> entries = new java.util.ArrayList<>(data.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        if (entries.size() > 5) {
            for (int i = 0; i < 5; i++) {
                sortedData.put(entries.get(i).getKey(), entries.get(i).getValue());
            }
            double otrosSum = 0;
            for (int i = 5; i < entries.size(); i++) {
                otrosSum += entries.get(i).getValue();
            }
            if (otrosSum > 0) {
                sortedData.put("OTROS", otrosSum);
            }
        } else {
            for (Map.Entry<String, Double> entry : entries) {
                sortedData.put(entry.getKey(), entry.getValue());
            }
        }
        return sortedData;
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
        int titleX = padding;
        if (svgIcon != null) {
            svgIcon.paintIcon(this, g2, padding, 6);
            titleX = padding + 26;
        }
        g2.setColor(ThemeConstants.TEXT_PRIMARY);
        g2.setFont(ThemeConstants.FONT_SUBTITLE.deriveFont(Font.BOLD, 14f));
        g2.drawString(title, titleX, 20);

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

        Map<String, Double> sortedData = getSortedData();
        double total = sortedData.values().stream().mapToDouble(Double::doubleValue).sum();
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

        int numItems = sortedData.size();
        int stepY = numItems > 5 ? 19 : 22;
        int startLegendY = topMargin + Math.max(0, (chartAreaHeight - (numItems * stepY)) / 2);

        // 1. Dibujar rebanadas del Donut Chart
        double curAngle = 0;
        int colorIdx = 0;

        Point hoveredArcEdge = null;
        Color hoveredColor = null;

        for (Map.Entry<String, Double> entry : sortedData.entrySet()) {
            double angle = (entry.getValue() / total) * 360;
            Color sliceColor = neonColors[colorIdx % neonColors.length];
            g2.setColor(sliceColor);

            boolean isHovered = (colorIdx == hoverIndex);
            int drawX = x;
            int drawY = y;

            double midAngleRad = Math.toRadians(curAngle + angle / 2.0);
            if (isHovered) {
                int shift = 7;
                drawX += (int) (Math.cos(midAngleRad) * shift);
                drawY -= (int) (Math.sin(midAngleRad) * shift); // Y invertido

                // Guardar punto para la línea conectora neón
                int edgeR = (diameter / 2) + shift;
                int edgeX = (x + diameter / 2) + (int) (Math.cos(midAngleRad) * edgeR);
                int edgeY = (y + diameter / 2) - (int) (Math.sin(midAngleRad) * edgeR);
                hoveredArcEdge = new Point(edgeX, edgeY);
                hoveredColor = sliceColor;
            }

            g2.fillArc(drawX, drawY, diameter, diameter, (int) Math.round(curAngle), (int) Math.ceil(angle));
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
        g2.setFont(ThemeConstants.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();

        Point targetLegendPoint = null;

        for (Map.Entry<String, Double> entry : sortedData.entrySet()) {
            Color color = neonColors[colorIdx % neonColors.length];
            int currentY = startLegendY + (colorIdx * stepY);
            boolean isHovered = (colorIdx == hoverIndex);

            if (isHovered) {
                // Fondo sutil para el elemento seleccionado en la leyenda
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(legendX - 4, currentY, legendWidth + 8, stepY - 2, 6, 6);
                targetLegendPoint = new Point(legendX - 4, currentY + (stepY / 2));
            }

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

            g2.setColor(isHovered ? ThemeConstants.TEXT_PRIMARY : ThemeConstants.TEXT_SECONDARY);
            if (isHovered) {
                g2.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
            } else {
                g2.setFont(ThemeConstants.FONT_SMALL);
            }
            g2.drawString(textToDraw, legendX + 16, currentY + 11);
            colorIdx++;
        }

        // 4. Dibujar Línea Neón Conectora si hay ítem en Hover
        if (hoveredArcEdge != null && targetLegendPoint != null && hoveredColor != null) {
            g2.setColor(hoveredColor);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int p1x = hoveredArcEdge.x;
            int p1y = hoveredArcEdge.y;
            int p2x = targetLegendPoint.x;
            int p2y = targetLegendPoint.y;

            // Línea con curva Bézier suave
            int ctrlX = (p1x + p2x) / 2;
            java.awt.geom.Path2D path = new java.awt.geom.Path2D.Double();
            path.moveTo(p1x, p1y);
            path.curveTo(ctrlX, p1y, ctrlX, p2y, p2x, p2y);
            g2.draw(path);

            // Pequeña bolita neón en los extremos
            g2.fillOval(p1x - 3, p1y - 3, 6, 6);
            g2.fillOval(p2x - 3, p2y - 3, 6, 6);
        }

        g2.dispose();
    }
}
