package com.mycompany.zl_solucion_integral.views.components.atoms;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NeonButton extends JButton {
    private Color neonColor = ThemeConstants.NEON_BLUE;
    private boolean isHovered = false;

    public NeonButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(ThemeConstants.TEXT_PRIMARY);
        setFont(ThemeConstants.FONT_BODY);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Objetivo táctil mínimo (uso cómodo en pantallas pequeñas).
        setPreferredSize(new Dimension(0, ThemeConstants.TOUCH_TARGET_MIN));
        setMinimumSize(new Dimension(0, ThemeConstants.TOUCH_TARGET_MIN));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public void setNeonColor(Color color) {
        this.neonColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Fondo: al estar enfocado por teclado se resalta ligeramente.
        if (isHovered) {
            g2.setColor(neonColor.darker().darker());
        } else {
            g2.setColor(ThemeConstants.CARD_BACKGROUND);
        }
        g2.fillRoundRect(0, 0, width, height, 15, 15);

        // Border Neon
        g2.setColor(isHovered ? neonColor : neonColor.darker());
        g2.setStroke(new BasicStroke(isHovered ? 2f : 1f));
        g2.drawRoundRect(1, 1, width - 2, height - 2, 15, 15);

        // Anillo de foco para navegación por teclado (accesibilidad).
        if (hasFocus()) {
            g2.setColor(neonColor.brighter());
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{4f, 3f}, 0));
            g2.drawRoundRect(3, 3, width - 7, height - 7, 13, 13);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
