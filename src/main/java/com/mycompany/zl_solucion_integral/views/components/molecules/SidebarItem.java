package com.mycompany.zl_solucion_integral.views.components.molecules;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarItem extends JPanel {
    private final JLabel label;
    private final JLabel iconLabel;
    /** Icono SVG del ítem (null si se usó un emoji en su lugar). */
    private FlatSVGIcon svgIcon;
    private boolean active = false;
    private boolean focused = false;
    private Runnable onClick;

    public SidebarItem(String text, String iconCode) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 18, 14));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Accesibilidad: el ítem es alcanzable por teclado (Tab).
        setFocusable(true);
        setToolTipText(text);
        getAccessibleContext().setAccessibleName(text);

        if (iconCode != null && iconCode.startsWith("icons/")) {
            // Los SVG de Font Awesome no declaran fill propio, por lo que FlatLaf
            // los pinta en negro por defecto. Para poder recolorearlos segun el
            // estado/tema hay que remapear ese negro mediante un ColorFilter
            // (el foreground del JLabel NO afecta al FlatSVGIcon).
            svgIcon = new FlatSVGIcon(iconCode, 18, 18);
            svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter()
                    .add(Color.BLACK, ThemeConstants.TEXT_PRIMARY));
            iconLabel = new JLabel(svgIcon);
        } else {
            iconLabel = new JLabel(iconCode == null ? "" : iconCode);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        }

        iconLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(iconLabel);

        label = new JLabel(text);
        label.setForeground(ThemeConstants.TEXT_SECONDARY);
        label.setFont(ThemeConstants.FONT_BODY);
        add(label);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) {
                    setHover(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    setHover(false);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                if (onClick != null) onClick.run();
            }
        });

        // Navegación por teclado: Enter o Espacio activan el ítem.
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (onClick != null) onClick.run();
                }
            }
        });

        // Anillo de foco visible al navegar con teclado.
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (focused) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ThemeConstants.NEON_PURPLE);
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
            g2.dispose();
        }
    }

    public void setActive(boolean active) {
        this.active = active;
        refresh();
    }

    /**
     * Re-aplica los colores del ítem según el tema vigente. Se usa tanto al
     * cambiar el estado activo como al cambiar el tema (vía ThemeConstants).
     */
    public void refresh() {
        if (active) {
            label.setForeground(ThemeConstants.TEXT_PRIMARY);
            setIconColor(ThemeConstants.NEON_PURPLE);
            label.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD));
            setOpaque(true);
            setBackground(ThemeConstants.ACTIVE_BACKGROUND);
        } else {
            label.setForeground(ThemeConstants.TEXT_SECONDARY);
            setIconColor(ThemeConstants.TEXT_PRIMARY);
            label.setFont(ThemeConstants.FONT_BODY);
            setOpaque(false);
        }
        repaint();
    }

    private void setHover(boolean hover) {
        if (hover) {
            label.setForeground(ThemeConstants.TEXT_PRIMARY);
            setIconColor(ThemeConstants.NEON_BLUE);
            setBackground(ThemeConstants.HOVER_BACKGROUND);
            setOpaque(true);
        } else {
            label.setForeground(ThemeConstants.TEXT_SECONDARY);
            setIconColor(ThemeConstants.TEXT_PRIMARY);
            setOpaque(false);
        }
        repaint();
    }

    /**
     * Aplica el color indicado al icono SVG del ítem. El foreground de un
     * JLabel no afecta a un FlatSVGIcon, por lo que el color se fija remapeando
     * el negro propio del SVG mediante un ColorFilter.
     */
    private void setIconColor(Color color) {
        if (svgIcon != null) {
            svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, color));
        } else {
            // Iconos basados en emoji/fuente: ahi si funciona el foreground.
            iconLabel.setForeground(color);
        }
        repaint();
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
}
