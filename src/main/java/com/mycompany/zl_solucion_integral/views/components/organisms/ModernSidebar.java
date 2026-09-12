package com.mycompany.zl_solucion_integral.views.components.organisms;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.ThemeToggleButton;
import com.mycompany.zl_solucion_integral.views.components.molecules.SidebarItem;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModernSidebar extends JPanel {
    private final List<SidebarItem> items = new ArrayList<>();
    private final JPanel itemsContainer;
    private final JPanel footerContainer;
    private final ThemeToggleButton themeToggle = new ThemeToggleButton();

    public ModernSidebar() {
        setLayout(new BorderLayout(0, 18));
        setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        setPreferredSize(new Dimension(260, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(30, 22, 18, 20));

        JLabel logo = new JLabel("ERP+ BUSINESS");
        logo.setForeground(ThemeConstants.NEON_PURPLE);
        logo.setFont(ThemeConstants.FONT_TITLE);
        header.add(logo, BorderLayout.NORTH);

        JLabel slogan = new JLabel("Gestión inteligente");
        slogan.setForeground(ThemeConstants.TEXT_SECONDARY);
        slogan.setFont(ThemeConstants.FONT_SMALL);
        slogan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        header.add(slogan, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setOpaque(false);
        itemsContainer.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(itemsContainer, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);

        // Toggle de tema en el borde inferior (justo encima del cierre de sesión)
        JPanel toggleContainer = new JPanel(new BorderLayout());
        toggleContainer.setOpaque(false);
        toggleContainer.setBorder(BorderFactory.createEmptyBorder(0, 8, 4, 8));
        toggleContainer.add(themeToggle, BorderLayout.NORTH);

        footerContainer = new JPanel(new BorderLayout());
        footerContainer.setOpaque(false);
        footerContainer.setBorder(BorderFactory.createEmptyBorder(8, 8, 18, 8));

        JPanel bottomStack = new JPanel();
        bottomStack.setOpaque(false);
        bottomStack.setLayout(new BorderLayout());
        bottomStack.add(toggleContainer, BorderLayout.NORTH);
        bottomStack.add(footerContainer, BorderLayout.CENTER);
        add(bottomStack, BorderLayout.SOUTH);
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
        SidebarItem logoutItem = new SidebarItem("Cerrar Sesión", "icons/logout.svg");
        logoutItem.setOnClick(onLogout);
        footerContainer.removeAll();
        footerContainer.add(logoutItem, BorderLayout.CENTER);
        footerContainer.revalidate();
        footerContainer.repaint();
    }

    /** Asocia el manejador del toggle de tema. */
    public void setThemeToggleAction(Runnable action) {
        themeToggle.addActionListener(e -> {
            if (action != null) action.run();
            themeToggle.refresh();
        });
    }

    /** Refresca los colores del sidebar tras un cambio de tema. */
    public void applyTheme() {
        setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        for (Component c : itemsContainer.getComponents()) {
            if (c instanceof SidebarItem) {
                ((SidebarItem) c).refresh();
            }
        }
        themeToggle.refresh();
        revalidate();
        repaint();
    }

    private void clearSelection() {
        for (SidebarItem item : items) {
            item.setActive(false);
        }
    }
}
