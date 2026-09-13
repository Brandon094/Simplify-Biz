package com.mycompany.zl_solucion_integral.views.components.atoms;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Componente gráfico de barras neón para la comparativa financiera macro del negocio.
 * Muestra Ventas Totales, Utilidad Neta e Inversión en Bodega.
 */
public class NeonBarChart extends JPanel {
    private final String title;
    private final String[] labels = {"Ventas Totales", "Utilidad Neta", "Inversión Bodega"};
    private final double[] values = new double[3];
    private final Color[] barColors = {
        ThemeConstants.NEON_GREEN,  // Ventas Totales
        ThemeConstants.NEON_CYAN,   // Utilidad Neta
        ThemeConstants.NEON_PURPLE  // Inversión Inventario
    };
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    private int hoverIndex = -1;

    public NeonBarChart(String title, double ventasTotales, double utilidadNeta, double inversionBodega) {
        this.title = title;
        this.values[0] = Math.max(0, ventasTotales);
        this.values[1] = Math.max(0, utilidadNeta);
        this.values[2] = Math.max(0, inversionBodega);
        setOpaque(false);

        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                checkHover(e.getPoint());
            }

            @Override
            public void mouseExited(MouseEvent e) {
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
        int width = getWidth();
        int height = getHeight();
        int padding = 16;
        int topMargin = 38;
        int bottomMargin = 28;
        int chartHeight = height - topMargin - bottomMargin;
        if (chartHeight <= 0) return;

        int numBars = values.length;
        int availableWidth = width - (padding * 2) - 40; // Espacio horizontal para barras
        int barGap = 24;
        int barWidth = Math.min(80, Math.max(30, (availableWidth - (barGap * (numBars - 1))) / numBars));
        int startX = padding + 35 + (availableWidth - ((barWidth * numBars) + (barGap * (numBars - 1)))) / 2;

        int newHover = -1;
        for (int i = 0; i < numBars; i++) {
            int bx = startX + i * (barWidth + barGap);
            Rectangle rect = new Rectangle(bx, topMargin, barWidth, chartHeight + bottomMargin);
            if (rect.contains(p)) {
                newHover = i;
                break;
            }
        }

        if (newHover != hoverIndex) {
            hoverIndex = newHover;
            setCursor(hoverIndex != -1 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            repaint();
        }
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

        // 1. Título del gráfico
        g2.setColor(ThemeConstants.TEXT_PRIMARY);
        g2.setFont(ThemeConstants.FONT_SUBTITLE);
        g2.drawString(title, padding, padding + 10);

        int topMargin = 42;
        int bottomMargin = 30;
        int leftMargin = 55;
        int rightMargin = 20;

        int chartWidth = width - leftMargin - rightMargin;
        int chartHeight = height - topMargin - bottomMargin;

        if (chartWidth <= 0 || chartHeight <= 0) {
            g2.dispose();
            return;
        }

        // Encontrar valor máximo para escala
        double maxVal = 0;
        for (double v : values) {
            if (v > maxVal) maxVal = v;
        }
        if (maxVal <= 0) maxVal = 1.0;
        // Redondear maxVal hacia arriba a un múltiplo limpio
        maxVal = maxVal * 1.15;

        // 2. Líneas de cuadrícula horizontal (3 divisiones)
        g2.setFont(ThemeConstants.FONT_SMALL.deriveFont(10f));
        FontMetrics fmY = g2.getFontMetrics();

        int gridLines = 3;
        for (int i = 0; i <= gridLines; i++) {
            double gridValue = (maxVal / gridLines) * i;
            int yPos = topMargin + chartHeight - (int) ((gridValue / maxVal) * chartHeight);

            // Línea tenue
            g2.setColor(new Color(255, 255, 255, 15));
            g2.drawLine(leftMargin, yPos, leftMargin + chartWidth, yPos);

            // Texto de eje Y (Abreviado ej: $4.3M o $500K)
            String labelY = formatCompactCurrency(gridValue);
            g2.setColor(ThemeConstants.TEXT_SECONDARY);
            g2.drawString(labelY, leftMargin - fmY.stringWidth(labelY) - 6, yPos + 4);
        }

        // 3. Dibujar Barras Neón
        int numBars = values.length;
        int barGap = Math.max(16, (chartWidth - (numBars * 60)) / (numBars + 1));
        int barWidth = Math.min(75, Math.max(28, (chartWidth - (barGap * (numBars + 1))) / numBars));
        int totalBarsWidth = (numBars * barWidth) + ((numBars - 1) * barGap);
        int startX = leftMargin + (chartWidth - totalBarsWidth) / 2;

        for (int i = 0; i < numBars; i++) {
            double val = values[i];
            int bx = startX + i * (barWidth + barGap);
            int barH = (int) ((val / maxVal) * chartHeight);
            barH = Math.max(4, barH); // Al menos 4px de altura visual

            int by = topMargin + chartHeight - barH;
            Color color = barColors[i % barColors.length];
            boolean isHovered = (i == hoverIndex);

            if (isHovered) {
                // Glow neón traslúcido de fondo al pasar el ratón
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                g2.fillRoundRect(bx - 4, by - 4, barWidth + 8, barH + 4, 12, 12);
            }

            // Cuerpo de la Barra Neón
            g2.setColor(isHovered ? color.brighter() : color);
            g2.fillRoundRect(bx, by, barWidth, barH, 10, 10);

            // Borde neón brillante
            g2.setColor(isHovered ? Color.WHITE : color.brighter());
            g2.setStroke(new BasicStroke(isHovered ? 2.0f : 1.0f));
            g2.drawRoundRect(bx, by, barWidth, barH, 10, 10);

            // Texto sobre la barra (Valor formateado en Moneda)
            g2.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 10.5f));
            g2.setColor(isHovered ? ThemeConstants.TEXT_PRIMARY : ThemeConstants.TEXT_SECONDARY);
            String valStr = currencyFormatter.format(val);
            FontMetrics fmVal = g2.getFontMetrics();
            int valX = bx + (barWidth - fmVal.stringWidth(valStr)) / 2;
            int valY = Math.max(topMargin + 12, by - 6);
            g2.drawString(valStr, valX, valY);

            // Etiqueta del Eje X (Nombre del KPI)
            g2.setFont(ThemeConstants.FONT_SMALL);
            g2.setColor(isHovered ? ThemeConstants.TEXT_PRIMARY : ThemeConstants.TEXT_SECONDARY);
            String labelX = labels[i];
            FontMetrics fmX = g2.getFontMetrics();
            int labelXPos = bx + (barWidth - fmX.stringWidth(labelX)) / 2;
            g2.drawString(labelX, labelXPos, topMargin + chartHeight + 18);
        }

        g2.dispose();
    }

    private String formatCompactCurrency(double val) {
        if (val >= 1_000_000) {
            return String.format(new Locale("es", "CO"), "$%.1fM", val / 1_000_000.0);
        } else if (val >= 1_000) {
            return String.format(new Locale("es", "CO"), "$%.0fK", val / 1_000.0);
        } else {
            return String.format(new Locale("es", "CO"), "$%.0f", val);
        }
    }
}
