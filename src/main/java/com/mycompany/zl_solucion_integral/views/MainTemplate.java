package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.Main;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.organisms.ModernSidebar;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;

public class MainTemplate extends JFrame {
    private final ModernSidebar sidebar;
    private final JPanel pageContainer;
    private final String userRole; // "1" para Admin, "0" para Vendedor
    private LayoutResponsive.Breakpoint breakpoint = LayoutResponsive.Breakpoint.ESCRITORIO;
    private JPanel sidebarHost;
    private NeonButton btnHamburger;

    public MainTemplate(String role) {
        this.userRole = role;
        setTitle("ERP+ Business - " + (role.equals("1") ? "Panel Administrativo" : "Punto de Venta"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Tamaño mínimo pensado para portátiles 1366x768: deja "respiro visual"
        // sin recortar el contenido en resoluciones pequeñas.
        setMinimumSize(new Dimension(1120, 700));
        setLocationRelativeTo(null);

        getContentPane().setBackground(ThemeConstants.BACKGROUND);
        setLayout(new BorderLayout(20, 0));

        sidebar = new ModernSidebar();
        // Host del sidebar: permite ocultarlo por completo (drawer) en móvil.
        sidebarHost = new JPanel(new BorderLayout());
        sidebarHost.setOpaque(false);
        sidebarHost.add(sidebar, BorderLayout.CENTER);
        add(sidebarHost, BorderLayout.WEST);

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

        // Adaptabilidad: en móvil/tablet el sidebar se oculta (drawer) y se
        // controla con el botón hamburguesa del header.
        LayoutResponsive.listen(this, this::applyBreakpoint);
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

        // Botón hamburguesa (solo visible en móvil/tablet) para el drawer.
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
        titleText.add(lblTitle);
        titleText.add(Box.createVerticalStrut(6));
        titleText.add(lblSubtitle);
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

        rightContent.add(lblDate);
        rightContent.add(lblUser);

        topHeader.add(leftContent, BorderLayout.WEST);
        topHeader.add(rightContent, BorderLayout.EAST);
        return topHeader;
    }

    /**
     * Aplica el comportamiento adaptable según el breakpoint vigente.
     * En móvil el sidebar se oculta (drawer) y se muestra el botón hamburguesa;
     * en tablet/escritorio el sidebar queda fijo y visible.
     */
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

    /** Muestra u oculta el sidebar (drawer) en modo compacto. */
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

    /** En modo compacto, oculta el drawer tras navegar para dar más espacio. */
    private void autoOcultarSidebarEnCompacto() {
        if (breakpoint != LayoutResponsive.Breakpoint.ESCRITORIO) {
            setSidebarVisible(false);
        }
    }

    /** Navega a una página y, en modo compacto, cierra el drawer para dar espacio. */
    private void navegar(JPanel page) {
        showPage(pageContainer, page);
        autoOcultarSidebarEnCompacto();
    }

    private void initNavigation() {
        if (userRole.equals("1")) {
            sidebar.addItem("Resumen", "icons/dashboard.svg", () -> navegar(new DashboardPage()));
            sidebar.addItem("Ventas", "icons/sales.svg", () -> navegar(new SalesPage()));
            sidebar.addItem("Productos", "icons/products.svg", () -> navegar(new ProductPage()));
            sidebar.addItem("Clientes", "icons/clients.svg", () -> navegar(new ClientsPage()));
            sidebar.addItem("Empleados", "icons/staff.svg", () -> navegar(new SellersPage()));
            sidebar.addItem("Reportes", "icons/reports.svg", () -> navegar(new ReportsPage()));
            sidebar.addItem("Configuración", "icons/settings.svg", () -> navegar(new ConfigPage()));
        } else {
            sidebar.addItem("Ventas", "icons/sales.svg", () -> navegar(new SalesPage()));
            sidebar.addItem("Productos", "icons/products.svg", () -> navegar(new ProductPage()));
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
