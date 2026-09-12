package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.organisms.ModernSidebar;
import javax.swing.*;
import java.awt.*;

public class MainTemplate extends JFrame {
    private final ModernSidebar sidebar;
    private final JPanel pageContainer;
    private final String userRole; // "1" para Admin, "0" para Vendedor

    public MainTemplate(String role) {
        this.userRole = role;
        setTitle("ERP+ Business - " + (role.equals("1") ? "Panel Administrativo" : "Punto de Venta"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1280, 800));
        setLocationRelativeTo(null);

        getContentPane().setBackground(ThemeConstants.BACKGROUND);
        setLayout(new BorderLayout(20, 0));

        sidebar = new ModernSidebar();
        add(sidebar, BorderLayout.WEST);

        JPanel contentArea = new JPanel(new BorderLayout(0, 18));
        contentArea.setOpaque(false);
        contentArea.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 18));
        add(contentArea, BorderLayout.CENTER);

        JPanel topHeader = createTopHeader(role);
        contentArea.add(topHeader, BorderLayout.NORTH);

        pageContainer = new JPanel(new BorderLayout());
        pageContainer.setOpaque(false);
        pageContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        contentArea.add(pageContainer, BorderLayout.CENTER);

        initNavigation();

        if (role.equals("1")) {
            showPage(pageContainer, new DashboardPage());
        } else {
            showPage(pageContainer, new SalesPage());
        }
    }

    private JPanel createTopHeader(String role) {
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);
        topHeader.setBorder(BorderFactory.createEmptyBorder(10, 18, 4, 16));

        JPanel leftContent = new JPanel();
        leftContent.setOpaque(false);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));

        String roleName = role.equals("1") ? "Administrador" : "Vendedor";
        JLabel lblTitle = new JLabel(role.equals("1") ? "Panel general" : "Punto de venta");
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblTitle.setFont(ThemeConstants.FONT_TITLE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Bienvenido/a, " + com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado() + " • " + roleName);
        lblSubtitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblSubtitle.setFont(ThemeConstants.FONT_SMALL);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftContent.add(lblTitle);
        leftContent.add(Box.createVerticalStrut(6));
        leftContent.add(lblSubtitle);

        JPanel rightContent = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        rightContent.setOpaque(false);

        JLabel lblDate = new JLabel(java.time.LocalDate.now().toString());
        lblDate.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblDate.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        lblDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblUser = new JLabel("👤 " + com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado());
        lblUser.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblUser.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        lblUser.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        rightContent.add(lblDate);
        rightContent.add(lblUser);

        topHeader.add(leftContent, BorderLayout.WEST);
        topHeader.add(rightContent, BorderLayout.EAST);
        return topHeader;
    }

    private void initNavigation() {
        if (userRole.equals("1")) {
            sidebar.addItem("Resumen", "icons/dashboard.svg", () -> showPage(pageContainer, new DashboardPage()));
            sidebar.addItem("Ventas", "icons/sales.svg", () -> showPage(pageContainer, new SalesPage()));
            sidebar.addItem("Productos", "icons/products.svg", () -> showPage(pageContainer, new ProductPage()));
            sidebar.addItem("Clientes", "icons/clients.svg", () -> showPage(pageContainer, new ClientsPage()));
            sidebar.addItem("Empleados", "icons/staff.svg", () -> showPage(pageContainer, new SellersPage()));
            sidebar.addItem("Reportes", "icons/reports.svg", () -> showPage(pageContainer, new ReportsPage()));
            sidebar.addItem("Configuración", "icons/settings.svg", () -> showPage(pageContainer, new ConfigPage()));
        } else {
            sidebar.addItem("Ventas", "icons/sales.svg", () -> showPage(pageContainer, new SalesPage()));
            sidebar.addItem("Productos", "icons/products.svg", () -> showPage(pageContainer, new ProductPage()));
        }

        // Ítem compartido por todos: Cerrar Sesión
        sidebar.addLogoutItem(() -> {
            int confirmed = JOptionPane.showConfirmDialog(this, 
                "¿Estás seguro de que deseas cerrar sesión?", 
                "Cerrar Sesión", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirmed == JOptionPane.YES_OPTION) {
                this.dispose();
                new ModernLoginPage().setVisible(true);
            }
        });
    }

    public void showPage(JPanel container, JPanel page) {
        container.removeAll();
        container.add(page, BorderLayout.CENTER);
        container.revalidate();
        container.repaint();
    }
}
