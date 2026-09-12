package com.mycompany.zl_solucion_integral.views.components.molecules;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarItem extends JPanel {
    private final JLabel label;
    private final JLabel iconLabel;
    private boolean active = false;
    private Runnable onClick;

    public SidebarItem(String text, String iconCode) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 18, 14));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (iconCode != null && iconCode.startsWith("icons/")) {
            FlatSVGIcon icon = new FlatSVGIcon(iconCode, 18, 18);
            iconLabel = new JLabel(icon);
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
                if (onClick != null) onClick.run();
            }
        });
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
            iconLabel.setForeground(ThemeConstants.NEON_PURPLE);
            label.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD));
            setOpaque(true);
            setBackground(ThemeConstants.ACTIVE_BACKGROUND);
        } else {
            label.setForeground(ThemeConstants.TEXT_SECONDARY);
            iconLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
            label.setFont(ThemeConstants.FONT_BODY);
            setOpaque(false);
        }
        repaint();
    }

    private void setHover(boolean hover) {
        if (hover) {
            label.setForeground(ThemeConstants.TEXT_PRIMARY);
            iconLabel.setForeground(ThemeConstants.NEON_BLUE);
            setBackground(ThemeConstants.HOVER_BACKGROUND);
            setOpaque(true);
        } else {
            label.setForeground(ThemeConstants.TEXT_SECONDARY);
            iconLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
            setOpaque(false);
        }
        repaint();
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
}
