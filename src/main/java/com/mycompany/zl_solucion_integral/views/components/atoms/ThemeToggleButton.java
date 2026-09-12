package com.mycompany.zl_solucion_integral.views.components.atoms;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Átomo: botón reutilizable para alternar entre tema oscuro y claro.
 *
 * Muestra un icono de luna (🌙) en tema oscuro y de sol (☀️) en tema claro,
 * con el texto "Modo claro"/"Modo oscuro" que indica la acción a realizar.
 * El texto y el color se recalculan cada vez que cambia el tema mediante
 * {@link #refresh()}.
 */
public class ThemeToggleButton extends JButton {
    private boolean hovered = false;

    public ThemeToggleButton() {
        setContentAreaFilled(false);
        setFocusPainted(false);
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

    /** Actualiza el texto y colores según el tema vigente. */
    public void refresh() {
        if (ThemeConstants.isDark()) {
            setText("☀️  Modo claro");
        } else {
            setText("🌙  Modo oscuro");
        }
        setForeground(ThemeConstants.TEXT_SECONDARY);
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

        g2.dispose();
        super.paintComponent(g);
    }
}