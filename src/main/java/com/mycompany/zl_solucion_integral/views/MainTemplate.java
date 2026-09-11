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
        setTitle("Vortex ERP - " + (role.equals("1") ? "Panel Administrativo" : "Punto de Venta"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Configuración para Pantalla Completa
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizado al abrir
        setMinimumSize(new Dimension(1280, 800)); // Tamaño mínimo para no romper el layout
        setLocationRelativeTo(null); // Centrado inicial si no está maximizado
        
        getContentPane().setBackground(ThemeConstants.BACKGROUND);
        setLayout(new BorderLayout());

        sidebar = new ModernSidebar();
        add(sidebar, BorderLayout.WEST);

        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);
        add(contentArea, BorderLayout.CENTER);

        // Header superior
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);
        topHeader.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));
        
        String roleName = role.equals("1") ? "Administrador" : "Vendedor";
        JLabel lblUser = new JLabel(com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado() + " (" + roleName + ") 👤", SwingConstants.RIGHT);
        lblUser.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblUser.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        topHeader.add(lblUser, BorderLayout.EAST);
        
        contentArea.add(topHeader, BorderLayout.NORTH);
        
        pageContainer = new JPanel(new BorderLayout());
        pageContainer.setOpaque(false);
        contentArea.add(pageContainer, BorderLayout.CENTER);

        initNavigation();
        
        // Página inicial según rol
        if (role.equals("1")) {
            showPage(pageContainer, new DashboardPage());
        } else {
            showPage(pageContainer, new SalesPage());
        }
    }

    private void initNavigation() {
        if (userRole.equals("1")) {
            sidebar.addItem("Dashboard", "📊", () -> showPage(pageContainer, new DashboardPage()));
            sidebar.addItem("Ventas", "💰", () -> showPage(pageContainer, new SalesPage()));
            sidebar.addItem("Inventario", "📦", () -> showPage(pageContainer, new ProductPage()));
            sidebar.addItem("Clientes", "👥", () -> showPage(pageContainer, new ClientsPage()));
            sidebar.addItem("Vendedores", "👷", () -> showPage(pageContainer, new SellersPage()));
            sidebar.addItem("Proveedores", "🏭", () -> showPage(pageContainer, new ProvidersPage()));
            sidebar.addItem("Reportes", "📈", () -> showPage(pageContainer, new ReportsPage()));
            sidebar.addItem("Configuración", "⚙️", () -> showPage(pageContainer, new ConfigPage()));
        } else {
            // El vendedor SOLO ve ventas e inventario
            sidebar.addItem("Ventas (POS)", "💰", () -> showPage(pageContainer, new SalesPage()));
            sidebar.addItem("Inventario", "📦", () -> showPage(pageContainer, new ProductPage()));
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

    private JPanel createPlaceholder(String title) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(title);
        l.setFont(ThemeConstants.FONT_TITLE);
        l.setForeground(ThemeConstants.TEXT_SECONDARY);
        p.add(l);
        return p;
    }

    public void showPage(JPanel container, JPanel page) {
        container.removeAll();
        container.add(page, BorderLayout.CENTER);
        container.revalidate();
        container.repaint();
    }
}
