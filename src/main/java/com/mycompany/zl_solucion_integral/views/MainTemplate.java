package com.mycompany.zl_solucion_integral.views;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.Main;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.organisms.ModernSidebar;
import javax.swing.*;
import java.awt.*;

public class MainTemplate extends JFrame {
    private final ModernSidebar sidebar;
    private final JPanel pageContainer;
    private final String userRole; // "1" para Admin, "0" para Vendedor
    private LayoutResponsive.Breakpoint breakpoint = LayoutResponsive.Breakpoint.ESCRITORIO;
    private JPanel sidebarHost;
    private NeonButton btnHamburger;

    private JLabel lblHeaderTitle;
    private JLabel lblHeaderSubtitle;
    private FlatSVGIcon headerSvgIcon;

    public MainTemplate(String role) {
        this.userRole = role;
        setTitle("ERP+ Business - " + (role.equals("1") ? "Panel Administrativo" : "Punto de Venta"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            setIconImage(new ImageIcon(getClass().getResource("/icons/app_icon.png")).getImage());
        } catch (Exception ignored) {}

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1120, 700));
        setLocationRelativeTo(null);

        getContentPane().setBackground(ThemeConstants.BACKGROUND);
        setLayout(new BorderLayout(20, 0));

        sidebar = new ModernSidebar();
        sidebarHost = new JPanel(new BorderLayout());
        sidebarHost.setOpaque(false);
        sidebarHost.add(sidebar, BorderLayout.CENTER);
        add(sidebarHost, BorderLayout.WEST);

        JPanel contentArea = new JPanel(new BorderLayout(0, 14));
        contentArea.setOpaque(false);
        contentArea.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 18));
        add(contentArea, BorderLayout.CENTER);

        JPanel topHeader = createTopHeader(role);
        contentArea.add(topHeader, BorderLayout.NORTH);

        pageContainer = new JPanel(new BorderLayout());
        pageContainer.setOpaque(false);
        pageContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        contentArea.add(pageContainer, BorderLayout.CENTER);

        initNavigation();

        if (role.equals("1")) {
            navegar(new DashboardPage(), "Resumen general del negocio", "Visión rápida del rendimiento, utilidad neta e inversión en inventario", "icons/dashboard.svg", ThemeConstants.NEON_PURPLE);
        } else {
            navegar(new SalesPage(), "Punto de venta", "Registra ventas ágiles: busca productos, arma el carrito y cobra en segundos", "icons/cart-shopping.svg", ThemeConstants.NEON_GREEN);
        }

        LayoutResponsive.listen(this, this::applyBreakpoint);

        // Si es la primera vez que el usuario abre la app, mostrar la guía interactiva automáticamente
        SwingUtilities.invokeLater(() -> {
            if (!com.mycompany.zl_solucion_integral.config.SelecionRuta.cargarPrimerUsoVisto()) {
                com.mycompany.zl_solucion_integral.config.SelecionRuta.guardarPrimerUsoVisto(true);
                new com.mycompany.zl_solucion_integral.views.components.dialogs.ManualUsuarioDialog(this, role).setVisible(true);
            }
        });
    }

    private JPanel createTopHeader(String role) {
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);
        topHeader.setBorder(BorderFactory.createEmptyBorder(10, 18, 4, 16));

        JPanel leftContent = new JPanel();
        leftContent.setOpaque(false);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));

        headerSvgIcon = new FlatSVGIcon("icons/dashboard.svg", 22, 22);
        headerSvgIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_PURPLE));

        lblHeaderTitle = new JLabel(role.equals("1") ? "Resumen general del negocio" : "Punto de venta", headerSvgIcon, SwingConstants.LEFT);
        lblHeaderTitle.setIconTextGap(10);
        lblHeaderTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblHeaderTitle.setFont(ThemeConstants.FONT_TITLE);
        lblHeaderTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblHeaderSubtitle = new JLabel("Bienvenido/a, " + com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado());
        lblHeaderSubtitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblHeaderSubtitle.setFont(ThemeConstants.FONT_SMALL);
        lblHeaderSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnHamburger = new NeonButton("");
        btnHamburger.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnHamburger.setIcon(createIcon("icons/dashboard.svg"));
        btnHamburger.setToolTipText("Mostrar u ocultar el menú de navegación");
        btnHamburger.setPreferredSize(new Dimension(46, 46));
        btnHamburger.setVisible(false);
        btnHamburger.addActionListener(e -> toggleSidebar());

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.add(btnHamburger);
        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.add(lblHeaderTitle);
        titleText.add(Box.createVerticalStrut(4));
        titleText.add(lblHeaderSubtitle);
        titleRow.add(titleText);

        leftContent.add(titleRow);

        JPanel rightContent = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        rightContent.setOpaque(false);

        JLabel lblDate = new JLabel(java.time.LocalDate.now().toString());
        lblDate.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblDate.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        lblDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblUser = new JLabel(com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado(),
                createIcon("icons/user.svg"), SwingConstants.LEFT);
        lblUser.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblUser.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        lblUser.setIconTextGap(8);
        lblUser.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        NeonButton btnHelp = new NeonButton("Manual");
        btnHelp.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnHelp.setIcon(createIcon("icons/manual.svg"));
        btnHelp.setToolTipText("Abrir el Manual de Usuario y Centro de Ayuda");
        btnHelp.setPreferredSize(new Dimension(105, 36));
        btnHelp.addActionListener(e -> {
            com.mycompany.zl_solucion_integral.views.components.dialogs.ManualUsuarioDialog dialog =
                    new com.mycompany.zl_solucion_integral.views.components.dialogs.ManualUsuarioDialog(this, this.userRole);
            dialog.setVisible(true);
        });

        rightContent.add(lblDate);
        rightContent.add(lblUser);
        rightContent.add(btnHelp);

        topHeader.add(leftContent, BorderLayout.WEST);
        topHeader.add(rightContent, BorderLayout.EAST);
        return topHeader;
    }

    private void applyBreakpoint(LayoutResponsive.Breakpoint bp) {
        this.breakpoint = bp;
        boolean compacto = bp != LayoutResponsive.Breakpoint.ESCRITORIO;

        if (btnHamburger != null) {
            btnHamburger.setVisible(compacto);
        }
        setSidebarVisible(!compacto);
        revalidate();
        repaint();
    }

    private void toggleSidebar() {
        setSidebarVisible(!sidebarHost.isVisible());
    }

    private void setSidebarVisible(boolean visible) {
        if (sidebarHost != null) {
            sidebarHost.setVisible(visible);
            revalidate();
            repaint();
        }
    }

    private void autoOcultarSidebarEnCompacto() {
        if (breakpoint != LayoutResponsive.Breakpoint.ESCRITORIO) {
            setSidebarVisible(false);
        }
    }

    private void navegar(JPanel page, String titulo, String subtitulo, String iconPath, Color accentColor) {
        if (lblHeaderTitle != null && titulo != null) {
            lblHeaderTitle.setText(titulo);
            if (iconPath != null) {
                FlatSVGIcon newIcon = new FlatSVGIcon(iconPath, 22, 22);
                Color iconColor = accentColor != null ? accentColor : ThemeConstants.NEON_PURPLE;
                newIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, iconColor));
                lblHeaderTitle.setIcon(newIcon);
            }
        }
        if (lblHeaderSubtitle != null && subtitulo != null) {
            lblHeaderSubtitle.setText(subtitulo);
        }
        showPage(pageContainer, page);
        autoOcultarSidebarEnCompacto();
    }

    private void initNavigation() {
        if (userRole.equals("1")) {
            sidebar.addRootItem("Resumen", "icons/dashboard.svg", () -> navegar(new DashboardPage(), "Resumen general del negocio", "Visión rápida del rendimiento, utilidad neta e inversión en inventario", "icons/dashboard.svg", ThemeConstants.NEON_PURPLE));

            sidebar.addSection("Operaciones", "operaciones", "icons/bolt.svg");
            sidebar.addItem("Ventas", "icons/cart-shopping.svg", () -> navegar(new SalesPage(), "Punto de venta", "Registra ventas ágiles: busca productos, arma el carrito y cobra en segundos", "icons/cart-shopping.svg", ThemeConstants.NEON_CYAN));
            sidebar.addItem("Cartera", "icons/wallet.svg", () -> navegar(new CarteraPage(), "Cartera de clientes", "Control de ventas a crédito, seguimiento de deudores y registro de abonos", "icons/wallet.svg", ThemeConstants.NEON_PURPLE));
            sidebar.endSection();

            sidebar.addSection("INVENTARIO Y ABASTECIMIENTO", "inv", "icons/boxes-stacked.svg");
            sidebar.addItem("Inventario", "icons/boxes-stacked.svg", () -> navegar(new ProductPage(), "Gestión de inventario", "Administra productos, precios de costo, existencias en bodega y categorías", "icons/boxes-stacked.svg", ThemeConstants.NEON_GREEN));
            sidebar.addItem("Abastecimiento", "icons/suppliers.svg", () -> navegar(new ComprasPage(), "Abastecimiento & Proveedores", "Registra entradas de almacén y actualización automática de stock de mercancía", "icons/suppliers.svg", ThemeConstants.NEON_CYAN));
            sidebar.endSection();

            sidebar.addSection("GESTIÓN", "gestion", "icons/settings.svg");
            sidebar.addItem("Clientes", "icons/clients.svg", () -> navegar(new ClientsPage(), "Directorio de clientes", "Gestión de clientes, información de contacto e historial de compras facturadas", "icons/clients.svg", ThemeConstants.NEON_PURPLE));
            sidebar.addItem("Empleados", "icons/staff.svg", () -> navegar(new SellersPage(), "Gestión de empleados", "Administración de usuarios vendedores y permisos de acceso a la plataforma", "icons/staff.svg", ThemeConstants.NEON_CYAN));
            sidebar.addItem("Reportes", "icons/reports.svg", () -> navegar(new ReportsPage(), "Centro de reportes", "Informes ejecutivos de ventas, utilidades, balance contable y exportación Excel/PDF", "icons/reports.svg", ThemeConstants.NEON_GREEN));
            sidebar.addItem("Configuración", "icons/settings.svg", () -> navegar(new ConfigPage(), "Configuración del sistema", "Ajustes de base de datos local SQLite, estado de licencia y preferencia de tema", "icons/settings.svg", ThemeConstants.NEON_PURPLE));
            sidebar.endSection();
        } else {
            sidebar.addSection("OPERACIONES", "ops", "icons/sales.svg");
            sidebar.addItem("Ventas", "icons/cart-shopping.svg", () -> navegar(new SalesPage(), "Punto de venta", "Registra ventas ágiles: busca productos, arma el carrito y cobra en segundos", "icons/cart-shopping.svg", ThemeConstants.NEON_CYAN));
            sidebar.addItem("Cartera", "icons/wallet.svg", () -> navegar(new CarteraPage(), "Cartera de clientes", "Control de ventas a crédito, seguimiento de deudores y registro de abonos", "icons/wallet.svg", ThemeConstants.NEON_PURPLE));

            sidebar.addSection("Inventario", "inventario", "icons/boxes-stacked.svg");
            sidebar.addItem("Inventario", "icons/boxes-stacked.svg", () -> navegar(new ProductPage(), "Gestión de inventario", "Consulta existencias de productos y catálogo en bodega", "icons/boxes-stacked.svg", ThemeConstants.NEON_GREEN));
            sidebar.addItem("Abastecimiento", "icons/cart-shopping.svg", () -> navegar(new ComprasPage(), "Abastecimiento & Entradas", "Registro de abastecimiento y recepciones de mercancía", "icons/suppliers.svg", ThemeConstants.NEON_CYAN));
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

        // Toggle de tema oscuro / claro (solo UI, no afecta lógica de negocio).
        sidebar.setThemeToggleAction(this::toggleTheme);
    }

    /**
     * Alterna entre tema oscuro y claro re-asignando las constantes de color.
     * Para que el cambio sea consistente en todas las vistas, se reconstruye
     * la ventana con el mismo rol. Es un cambio puramente visual.
     */
    private void toggleTheme() {
        ThemeConstants.toggleTheme();
        // Persistir la preferencia para que sobreviva entre reinicios.
        com.mycompany.zl_solucion_integral.config.SelecionRuta.guardarPreferenciaTema(ThemeConstants.isDark());
        Main.aplicarTema();
        SwingUtilities.invokeLater(() -> {
            MainTemplate fresh = new MainTemplate(userRole);
            fresh.setVisible(true);
            this.dispose();
        });
    }

    public void showPage(JPanel container, JPanel page) {
        container.removeAll();
        container.add(page, BorderLayout.CENTER);
        container.revalidate();
        container.repaint();
    }

    private com.formdev.flatlaf.extras.FlatSVGIcon createIcon(String path) {
        com.formdev.flatlaf.extras.FlatSVGIcon icon =
                new com.formdev.flatlaf.extras.FlatSVGIcon(path, 18, 18);
        icon.setColorFilter(new com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter()
                .add(java.awt.Color.BLACK, ThemeConstants.TEXT_SECONDARY));
        return icon;
    }
}
