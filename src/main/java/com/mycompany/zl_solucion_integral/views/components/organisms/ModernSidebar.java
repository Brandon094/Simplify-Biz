package com.mycompany.zl_solucion_integral.views.components.organisms;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.ThemeToggleButton;
import com.mycompany.zl_solucion_integral.views.components.molecules.SidebarItem;
import com.mycompany.zl_solucion_integral.views.components.molecules.SidebarSection;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModernSidebar extends JPanel {
    private final List<SidebarItem> items = new ArrayList<>();
    private final List<SidebarSection> sections = new ArrayList<>();
    private final JPanel itemsContainer;
    private final JPanel footerContainer;
    private final ThemeToggleButton themeToggle = new ThemeToggleButton();
    private final JLabel logo = new JLabel("ERP+ BUSINESS");
    private final JLabel slogan = new JLabel("Gestión inteligente");
    private final JButton btnToggleCollapse;
    private SidebarSection currentSection = null;
    private boolean collapsedHorizontal = false;

    public ModernSidebar() {
        setLayout(new BorderLayout(0, 18));
        setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        setPreferredSize(new Dimension(270, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 16, 12, 16));

        logo.setForeground(ThemeConstants.NEON_PURPLE);
        logo.setFont(ThemeConstants.FONT_TITLE);

        slogan.setForeground(ThemeConstants.TEXT_SECONDARY);
        slogan.setFont(ThemeConstants.FONT_SMALL);
        slogan.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setOpaque(false);
        textStack.add(logo);
        textStack.add(slogan);

        btnToggleCollapse = new JButton();
        btnToggleCollapse.setFocusable(false);
        btnToggleCollapse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleCollapse.setContentAreaFilled(false);
        btnToggleCollapse.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        btnToggleCollapse.setToolTipText("Contraer / Expandir menú lateral");
        btnToggleCollapse.setIcon(createToggleIcon("icons/arrow-left.svg"));
        btnToggleCollapse.addActionListener(e -> toggleHorizontalCollapse());

        header.add(textStack, BorderLayout.CENTER);
        header.add(btnToggleCollapse, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setOpaque(false);
        itemsContainer.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        JPanel topContentWrapper = new JPanel(new BorderLayout());
        topContentWrapper.setOpaque(false);
        topContentWrapper.add(itemsContainer, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(topContentWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // Toggle de tema en el borde inferior (justo encima del cierre de sesión)
        JPanel toggleContainer = new JPanel(new BorderLayout());
        toggleContainer.setOpaque(false);
        toggleContainer.setBorder(BorderFactory.createEmptyBorder(0, 8, 4, 8));
        toggleContainer.add(themeToggle, BorderLayout.NORTH);

        footerContainer = new JPanel(new BorderLayout());
        footerContainer.setOpaque(false);
        footerContainer.setBorder(BorderFactory.createEmptyBorder(4, 8, 14, 8));

        JPanel bottomStack = new JPanel();
        bottomStack.setOpaque(false);
        bottomStack.setLayout(new BorderLayout());
        bottomStack.add(toggleContainer, BorderLayout.NORTH);
        bottomStack.add(footerContainer, BorderLayout.CENTER);
        add(bottomStack, BorderLayout.SOUTH);

        // Cargar preferencia guardada de colapso horizontal del menú
        boolean colapsadoGuardado = com.mycompany.zl_solucion_integral.config.SelecionRuta.cargarPreferenciaSidebarColapsado();
        if (colapsadoGuardado) {
            collapsedHorizontal = true;
            setPreferredSize(new Dimension(64, 0));
            logo.setVisible(false);
            slogan.setVisible(false);
            btnToggleCollapse.setIcon(createToggleIcon("icons/arrow-right.svg"));
        }
    }

    public void addRootItem(String text, String icon, Runnable onClick) {
        SidebarItem item = new SidebarItem(text, icon);
        if (collapsedHorizontal) {
            item.setCollapsed(true);
        }
        item.setOnClick(() -> {
            clearSelection();
            item.setActive(true);
            onClick.run();
        });
        items.add(item);
        itemsContainer.add(item);
        itemsContainer.add(Box.createVerticalStrut(4));
        revalidate();
        repaint();
    }

    public void addSection(String title, String sectionId, String iconPath) {
        currentSection = new SidebarSection(title, sectionId, iconPath);
        if (collapsedHorizontal) {
            currentSection.setCollapsed(true);
        }
        sections.add(currentSection);
        itemsContainer.add(currentSection);
    }

    public void endSection() {
        currentSection = null;
    }

    public void addItem(String text, String icon, Runnable onClick) {
        SidebarItem item = new SidebarItem(text, icon);
        if (collapsedHorizontal) {
            item.setCollapsed(true);
        }
        item.setOnClick(() -> {
            clearSelection();
            item.setActive(true);
            onClick.run();
        });
        items.add(item);
        if (currentSection != null) {
            currentSection.addItem(item);
        } else {
            itemsContainer.add(item);
        }
        revalidate();
        repaint();
    }

    public void addLogoutItem(Runnable onLogout) {
        SidebarItem logoutItem = new SidebarItem("Cerrar Sesión", "icons/logout.svg");
        if (collapsedHorizontal) {
            logoutItem.setCollapsed(true);
        }
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
        for (SidebarSection sec : sections) {
            sec.refreshTheme();
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

    public void toggleHorizontalCollapse() {
        collapsedHorizontal = !collapsedHorizontal;
        com.mycompany.zl_solucion_integral.config.SelecionRuta.guardarPreferenciaSidebarColapsado(collapsedHorizontal);
        int targetWidth = collapsedHorizontal ? 64 : 260;
        setPreferredSize(new Dimension(targetWidth, getHeight()));

        logo.setVisible(!collapsedHorizontal);
        slogan.setVisible(!collapsedHorizontal);
        String iconPath = collapsedHorizontal ? "icons/arrow-right.svg" : "icons/arrow-left.svg";
        btnToggleCollapse.setIcon(createToggleIcon(iconPath));

        for (SidebarItem item : items) {
            item.setCollapsed(collapsedHorizontal);
        }
        for (SidebarSection sec : sections) {
            sec.setCollapsed(collapsedHorizontal);
        }

        revalidate();
        repaint();
        if (getParent() != null) {
            getParent().revalidate();
            getParent().repaint();
        }
    }

    private com.formdev.flatlaf.extras.FlatSVGIcon createToggleIcon(String path) {
        com.formdev.flatlaf.extras.FlatSVGIcon icon = new com.formdev.flatlaf.extras.FlatSVGIcon(path, 18, 18);
        icon.setColorFilter(new com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter()
                .add(Color.BLACK, ThemeConstants.NEON_PURPLE));
        return icon;
    }
}
