package com.mycompany.zl_solucion_integral.views.components.organisms;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.molecules.SidebarItem;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModernSidebar extends JPanel {
    private final List<SidebarItem> items = new ArrayList<>();
    private final JPanel itemsContainer;

    public ModernSidebar() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        setPreferredSize(new Dimension(260, 0));

        // Logo area
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 40));
        header.setOpaque(false);
        JLabel logo = new JLabel("VORTEX ERP");
        logo.setForeground(ThemeConstants.NEON_PURPLE);
        logo.setFont(ThemeConstants.FONT_TITLE);
        header.add(logo);
        add(header, BorderLayout.NORTH);

        // Items Container
        itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setOpaque(false);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(itemsContainer, BorderLayout.NORTH);
        
        add(wrapper, BorderLayout.CENTER);

        // Footer Area (Cerrar Sesión)
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(footer, BorderLayout.SOUTH);
    }

    public void addItem(String text, String icon, Runnable onClick) {
        SidebarItem item = new SidebarItem(text, icon);
        item.setOnClick(() -> {
            clearSelection();
            item.setActive(true);
            onClick.run();
        });
        items.add(item);
        itemsContainer.add(item);
        revalidate();
        repaint();
    }

    public void addLogoutItem(Runnable onLogout) {
        SidebarItem logoutItem = new SidebarItem("Cerrar Sesión", "🚪");
        logoutItem.setOnClick(onLogout);
        add(logoutItem, BorderLayout.SOUTH);
    }

    private void clearSelection() {
        for (SidebarItem item : items) {
            item.setActive(false);
        }
    }
}
