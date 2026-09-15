package com.mycompany.zl_solucion_integral.views.components.molecules;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Componente acordeón colapsable/expandible para el Sidebar.
 * Muestra un encabezado con badge estilizado, icono SVG, indicador visual
 * y guarda la preferencia del usuario mediante java.util.prefs.Preferences.
 */
public class SidebarSection extends JPanel {
    private final String sectionId;
    private final JLabel lblTitle;
    private final JLabel lblArrow;
    private final JLabel lblIcon;
    private final JLabel lblBadge;
    private final JPanel headerPanel;
    private final JPanel contentPanel;
    private final JPanel leftGroup;
    private boolean expanded = true;
    private boolean isHovered = false;
    private final List<SidebarItem> items = new ArrayList<>();
    private static final Preferences prefs = Preferences.userNodeForPackage(SidebarSection.class);

    public SidebarSection(String title, String sectionId, String iconPath) {
        this.sectionId = sectionId;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));

        // Cargar preferencia del usuario (por defecto abierta: true)
        this.expanded = prefs.getBoolean("sidebar_section_" + sectionId, true);

        // Header neumórfico clicable con fondo redondeado
        headerPanel = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isHovered) {
                    g2.setColor(ThemeConstants.isDark() ? new Color(168, 85, 247, 30) : new Color(124, 58, 237, 25));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(ThemeConstants.NEON_PURPLE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                } else {
                    g2.setColor(ThemeConstants.isDark() ? new Color(255, 255, 255, 6) : new Color(0, 0, 0, 8));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Icono de sección
        if (iconPath != null && !iconPath.isEmpty()) {
            FlatSVGIcon svgIcon = new FlatSVGIcon(iconPath, 14, 14);
            svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_PURPLE));
            lblIcon = new JLabel(svgIcon);
        } else {
            FlatSVGIcon svgIcon = new FlatSVGIcon("icons/bolt.svg", 14, 14);
            svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_PURPLE));
            lblIcon = new JLabel(svgIcon);
        }

        leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftGroup.setOpaque(false);
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
        leftGroup.add(lblIcon);

        lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblTitle.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD, 10f));
        leftGroup.add(lblTitle);

        headerPanel.add(leftGroup, BorderLayout.CENTER);

        // Contenedor derecho (Badge + Flecha)
        JPanel rightGroup = new JPanel();
        rightGroup.setLayout(new BoxLayout(rightGroup, BoxLayout.X_AXIS));
        rightGroup.setOpaque(false);

        lblBadge = new JLabel(" 0 ");
        lblBadge.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 9f));
        lblBadge.setForeground(ThemeConstants.NEON_PURPLE);
        lblBadge.setOpaque(false);
        rightGroup.add(lblBadge);
        rightGroup.add(Box.createHorizontalStrut(4));

        lblArrow = new JLabel(expanded ? "▲" : "▼");
        lblArrow.setForeground(ThemeConstants.NEON_PURPLE);
        lblArrow.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 9f));
        rightGroup.add(lblArrow);

        headerPanel.add(rightGroup, BorderLayout.EAST);

        // Eventos Hover y Clic
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                headerPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                headerPanel.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                toggle();
            }
        });

        // Contenedor de ítems
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        contentPanel.setVisible(expanded);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void addItem(SidebarItem item) {
        items.add(item);
        contentPanel.add(item);
        lblBadge.setText(items.size() + " ops");
    }

    public List<SidebarItem> getItems() {
        return items;
    }

    public void toggle() {
        expanded = !expanded;
        contentPanel.setVisible(expanded);
        lblArrow.setText(expanded ? "▲" : "▼");
        prefs.putBoolean("sidebar_section_" + sectionId, expanded);
        revalidate();
        repaint();
        if (getParent() != null) {
            getParent().revalidate();
            getParent().repaint();
        }
    }

    public void refreshTheme() {
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblArrow.setForeground(ThemeConstants.NEON_PURPLE);
        lblBadge.setForeground(ThemeConstants.NEON_PURPLE);
        for (SidebarItem item : items) {
            item.refresh();
        }
        headerPanel.repaint();
    }

    public void setCollapsed(boolean collapsed) {
        lblTitle.setVisible(!collapsed);
        lblBadge.setVisible(!collapsed);
        lblArrow.setVisible(!collapsed);
        if (collapsed) {
            headerPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            leftGroup.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        } else {
            headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            leftGroup.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
        }
        contentPanel.setVisible(expanded);
        for (SidebarItem item : items) {
            item.setCollapsed(collapsed);
        }
        revalidate();
        repaint();
    }
}
