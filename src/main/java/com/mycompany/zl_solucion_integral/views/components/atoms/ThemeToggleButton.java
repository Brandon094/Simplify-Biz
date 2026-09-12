package com.mycompany.zl_solucion_integral.views.components.atoms;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Átomo: botón reutilizable para alternar entre tema oscuro y claro.
 *
 * Muestra el icono SVG de sol (indica pasar a tema claro) cuando el tema
 * vigente es oscuro, y el de luna (indica volver a tema oscuro) cuando el
 * tema vigente es claro. El texto "Modo claro"/"Modo oscuro" indica la acción
 * a realizar. El icono, el texto y los colores se recalculan cada vez que
 * cambia el tema mediante {@link #refresh()}.
 */
public class ThemeToggleButton extends JButton {
    private boolean hovered = false;

    public ThemeToggleButton() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFont(ThemeConstants.FONT_BODY);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 14));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });

        refresh();
    }

    /** Crea el icono SVG de tema tiñéndolo con el color secundario vigente. */
    private Icon createThemeIcon(String resourcePath) {
        FlatSVGIcon icon = new FlatSVGIcon(resourcePath, 16, 16);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter()
                .add(Color.BLACK, ThemeConstants.TEXT_SECONDARY));
        return icon;
    }

    /** Actualiza el icono, el texto y los colores según el tema vigente. */
    public void refresh() {
        if (ThemeConstants.isDark()) {
            setIcon(createThemeIcon("/icons/sun.svg"));
            setText("Modo claro");
        } else {
            setIcon(createThemeIcon("/icons/moon.svg"));
            setText("Modo oscuro");
        }
        setForeground(ThemeConstants.TEXT_SECONDARY);
        setIconTextGap(10);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        if (hovered) {
            g2.setColor(ThemeConstants.HOVER_BACKGROUND);
            g2.fillRoundRect(0, 0, width, height, 12, 12);
        }

        // Anillo de foco para navegación por teclado (accesibilidad).
        if (hasFocus()) {
            g2.setColor(ThemeConstants.NEON_PURPLE);
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(1, 1, width - 3, height - 3, 12, 12);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}