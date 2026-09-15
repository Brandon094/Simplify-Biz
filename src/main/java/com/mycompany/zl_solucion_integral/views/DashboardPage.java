package com.mycompany.zl_solucion_integral.views;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.controllers.ProductoController;
import com.mycompany.zl_solucion_integral.controllers.VentasController;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonBarChart;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonLineChart;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonPieChart;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.mycompany.zl_solucion_integral.views.components.atoms.ShimmerSkeleton;
import com.mycompany.zl_solucion_integral.views.components.organisms.MetricCard;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardPage extends JPanel {
    private final VentasController ventasCtrl = new VentasController();
    private final ProductoController productoCtrl = new ProductoController();

    // Panel norte fijo: título arriba y métricas después de la carga asíncrona.
    private final JPanel northWrap = new JPanel(new BorderLayout(0, 18));
    private final JPanel headerPanel = new JPanel();

    // Datos cargados en segundo plano por el SwingWorker.
    private double totalVentas;
    private double utilidadNeta;
    private double inversionInventario;
    private int stockCritico;
    private Object[][] ventasRecientes;
    private List<Double> ventas7Dias;
    private Map<String, Double> distribucionCategorias;

    // Referencias para el reflow adaptable.
    private JPanel metricsPanel;
    private JPanel tablesRow;
    private JPanel chartsRow;
    private final java.util.List<MetricCard> metricCards = new java.util.ArrayList<>();

    private String periodoSeleccionado = "Histórico Total";
    private JComboBox<String> cbPeriodo;
    private JPanel mainContainer;

    public DashboardPage() {
        setOpaque(false);
        setLayout(new BorderLayout(18, 18));
        JPanel topHeaderRow = new JPanel(new BorderLayout(12, 0));
        topHeaderRow.setOpaque(false);

        cbPeriodo = new JComboBox<>(new String[]{"Histórico Total", "Hoy", "Últimos 7 Días", "Este Mes"});
        cbPeriodo.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        cbPeriodo.setFocusable(false);
        cbPeriodo.setSelectedItem(periodoSeleccionado);
        cbPeriodo.addActionListener(e -> {
            String nuevoPeriodo = (String) cbPeriodo.getSelectedItem();
            if (nuevoPeriodo != null && !nuevoPeriodo.equals(periodoSeleccionado)) {
                periodoSeleccionado = nuevoPeriodo;
                cargarDatosYRefrescar();
            }
        });

        JPanel filterWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filterWrap.setOpaque(false);
        JLabel lblFiltro = new JLabel("Período: ");
        lblFiltro.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblFiltro.setFont(ThemeConstants.FONT_SMALL);
        filterWrap.add(lblFiltro);
        filterWrap.add(cbPeriodo);

        topHeaderRow.add(filterWrap, BorderLayout.EAST);

        northWrap.setOpaque(false);
        northWrap.add(topHeaderRow, BorderLayout.NORTH);
        add(northWrap, BorderLayout.NORTH);

        // Estado de carga inicial
        JPanel loadingWrap = new JPanel(new BorderLayout());
        loadingWrap.setOpaque(false);
        loadingWrap.add(new ShimmerSkeleton(), BorderLayout.CENTER);
        add(loadingWrap, BorderLayout.CENTER);

        cargarDatosAsincronos(loadingWrap);
    }

    private void cargarDatosAsincronos(JPanel loadingWrap) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                totalVentas = ventasCtrl.obtenerVentasTotalesPorPeriodo(periodoSeleccionado);
                utilidadNeta = ventasCtrl.obtenerUtilidadTotalPorPeriodo(periodoSeleccionado);
                inversionInventario = productoCtrl.obtenerInversionTotalInventario();
                stockCritico = productoCtrl.obtenerCantidadStockCritico(5);
                ventasRecientes = ventasCtrl.obtenerUltimasVentas(5);
                ventas7Dias = ventasCtrl.obtenerVentasUltimos7Dias();
                distribucionCategorias = productoCtrl.obtenerDistribucionCategorias();
                return null;
            }

            @Override
            protected void done() {
                if (loadingWrap != null) {
                    remove(loadingWrap);
                }
                construirVista();
                revalidate();
                repaint();
            }
        }.execute();
    }

    private void cargarDatosYRefrescar() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                totalVentas = ventasCtrl.obtenerVentasTotalesPorPeriodo(periodoSeleccionado);
                utilidadNeta = ventasCtrl.obtenerUtilidadTotalPorPeriodo(periodoSeleccionado);
                inversionInventario = productoCtrl.obtenerInversionTotalInventario();
                stockCritico = productoCtrl.obtenerCantidadStockCritico(5);
                ventasRecientes = ventasCtrl.obtenerUltimasVentas(5);
                ventas7Dias = ventasCtrl.obtenerVentasUltimos7Dias();
                distribucionCategorias = productoCtrl.obtenerDistribucionCategorias();
                return null;
            }

            @Override
            protected void done() {
                if (mainContainer != null) {
                    remove(mainContainer);
                }
                construirVista();
                revalidate();
                repaint();
            }
        }.execute();
    }

    /** Construye métricas, tablas y gráficos con los datos ya cargados. */
    private void construirVista() {
        NumberFormat cur = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        // Grid de Métricas con hgap ampliado para evitar compresión lateral
        metricsPanel = new JPanel(new GridLayout(1, 4, 18, 0));
        metricsPanel.setOpaque(false);
        MetricCard cardVentas = new MetricCard("Ventas Totales", cur.format(totalVentas), "Acumulado histórico", ThemeConstants.NEON_GREEN, "icons/sales.svg");
        
        double porcentajeMargen = totalVentas > 0 ? (utilidadNeta / totalVentas) * 100.0 : 0.0;
        MetricCard cardUtilidad = new MetricCard("Utilidad Neta", cur.format(utilidadNeta), String.format("Margen real: %.1f%%", porcentajeMargen), ThemeConstants.NEON_CYAN, "icons/check-double.svg");
        MetricCard cardInversion = new MetricCard("Inversión Inventario", cur.format(inversionInventario), "Valor costo en bodega", ThemeConstants.NEON_PURPLE, "icons/boxes-stacked.svg");
        MetricCard cardStock = new MetricCard("Stock Crítico", stockCritico + " ítems", "Menos de 5 unidades", ThemeConstants.NEON_RED, "icons/triangle-exclamation.svg");
        
        metricCards.clear();
        metricCards.add(cardVentas);
        metricCards.add(cardUtilidad);
        metricCards.add(cardInversion);
        metricCards.add(cardStock);
        for (MetricCard card : metricCards) {
            metricsPanel.add(card);
        }

        northWrap.add(metricsPanel, BorderLayout.CENTER);

        // Contenedor Central
        mainContainer = new JPanel(new GridLayout(2, 1, 0, 14));
        mainContainer.setOpaque(false);

        // FILA 1: Gráficos de Alto Valor Visual y Ejecutivo (Arriba)
        RoundedPanel lineChartCard = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        lineChartCard.setLayout(new BorderLayout());
        lineChartCard.add(new NeonLineChart("Ventas Últimos 7 Días ($)", "icons/chart-line.svg", ventas7Dias), BorderLayout.CENTER);

        RoundedPanel pieChartCard = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        pieChartCard.setLayout(new BorderLayout());
        pieChartCard.add(new NeonPieChart("Distribución de Inventario", "icons/chart-pie.svg", distribucionCategorias), BorderLayout.CENTER);

        chartsRow = new JPanel(new GridLayout(1, 2, 18, 0));
        chartsRow.setOpaque(false);
        chartsRow.add(lineChartCard);
        chartsRow.add(pieChartCard);
        mainContainer.add(chartsRow);

        // FILA 2: Balance Financiero Comparativo y Widget de Salud del Sistema (Abajo)
        RoundedPanel barChartCard = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        barChartCard.setLayout(new BorderLayout());
        barChartCard.add(new NeonBarChart("Balance Financiero Comparativo ($)", "icons/chart-column.svg", totalVentas, utilidadNeta, inversionInventario), BorderLayout.CENTER);

        JPanel statusWidgetCard = createSystemStatusWidget();

        tablesRow = new JPanel(new GridBagLayout());
        tablesRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Gráfico de Balance Financiero ocupa el 64% del ancho
        gbc.gridx = 0; gbc.weightx = 0.64; gbc.insets = new Insets(0, 0, 0, 10);
        tablesRow.add(barChartCard, gbc);

        // Widget de Salud/Alertas ocupa el 36% del ancho
        gbc.gridx = 1; gbc.weightx = 0.36; gbc.insets = new Insets(0, 6, 0, 0);
        tablesRow.add(statusWidgetCard, gbc);

        mainContainer.add(tablesRow);
        add(mainContainer, BorderLayout.CENTER);

        // Reflow adaptable: en móvil las métricas, gráficos y tablas se apilan.
        LayoutResponsive.listen(this, bp -> {
            LayoutResponsive.reflowColumnas(metricsPanel,
                    new java.util.ArrayList<>(metricCards), 4, 14, bp);
            LayoutResponsive.reflowColumnas(chartsRow,
                    LayoutResponsive.list(lineChartCard, pieChartCard), 2, 18, bp);
            LayoutResponsive.reflowColumnas(tablesRow,
                    LayoutResponsive.list(barChartCard, statusWidgetCard), 2, 18, bp);
        });
    }

    private RoundedPanel createSystemStatusWidget() {
        RoundedPanel card = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Encabezado: Icono + Título + Badge de estado en vivo (ONLINE)
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitle = new JLabel("Salud del Sistema");
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblTitle.setFont(ThemeConstants.FONT_SUBTITLE.deriveFont(Font.BOLD, 14f));
        lblTitle.setIconTextGap(8);
        try {
            FlatSVGIcon iconSalud = new FlatSVGIcon("icons/shield-heart.svg", 18, 18);
            iconSalud.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_GREEN));
            lblTitle.setIcon(iconSalud);
        } catch (Exception ignored) {}

        JLabel lblLive = createStatusPill("● ONLINE", ThemeConstants.NEON_GREEN);
        
        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblLive, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        // Contenido: 5 filas horizontales con badges neón
        JPanel content = new JPanel(new GridLayout(5, 1, 0, 4));
        content.setOpaque(false);

        // 1. Base de Datos
        content.add(createStatusRow("Base de Datos", "icons/check-double.svg", "SQLite WAL", ThemeConstants.NEON_GREEN));

        // 2. Licencia y Estado
        com.mycompany.zl_solucion_integral.config.LicenciaManager.InfoLicencia licInfo =
                com.mycompany.zl_solucion_integral.config.LicenciaManager.obtenerInfoLicencia();
        String licBadge = licInfo.getEstado() == com.mycompany.zl_solucion_integral.config.LicenciaManager.EstadoLicencia.PRO_ACTIVA
                ? "PRO Activa"
                : "Demo (" + licInfo.getDiasRestantes() + "d)";
        Color licColor = licInfo.getEstado() == com.mycompany.zl_solucion_integral.config.LicenciaManager.EstadoLicencia.PRO_ACTIVA
                ? ThemeConstants.NEON_GREEN : ThemeConstants.NEON_BLUE;
        content.add(createStatusRow("Licencia ERP+", "icons/license.svg", licBadge, licColor));

        // 3. Stock Crítico
        String stockBadge = stockCritico > 0 ? stockCritico + " requeridos" : "Óptimo";
        Color stockColor = stockCritico > 0 ? ThemeConstants.NEON_RED : ThemeConstants.NEON_GREEN;
        content.add(createStatusRow("Stock & Reposición", "icons/reports.svg", stockBadge, stockColor));

        // 4. Sesión Activa
        String usuario = com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado() != null
                ? com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado()
                : "Administrador";
        content.add(createStatusRow("Sesión Activa", "icons/clients.svg", usuario, ThemeConstants.NEON_CYAN));

        // 5. Versión del Sistema
        content.add(createStatusRow("Motor ERP+", "icons/settings.svg", "v1.3.0 Activo", ThemeConstants.NEON_PURPLE));

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStatusRow(String labelText, String iconPath, String badgeText, Color color) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);

        FlatSVGIcon icon = new FlatSVGIcon(iconPath, 16, 16);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, color));
        JLabel lblIcon = new JLabel(icon);

        JLabel lblTitle = new JLabel(labelText);
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblTitle.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));

        left.add(lblIcon);
        left.add(lblTitle);

        JLabel badge = createStatusPill(badgeText, color);

        row.add(left, BorderLayout.WEST);
        row.add(badge, BorderLayout.EAST);
        return row;
    }

    private JLabel createStatusPill(String text, Color color) {
        JLabel pill = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo neón semitransparente (20% opacidad)
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // Borde suave
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setForeground(color);
        pill.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 10.5f));
        pill.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return pill;
    }

    private RoundedPanel createActivityTable(String title, Object[][] data, String[] cols) {
        RoundedPanel card = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        card.add(lblTitle, BorderLayout.NORTH);

        JTable table = new JTable(data, cols);
        table.setBackground(ThemeConstants.CARD_BACKGROUND);
        table.setAutoCreateRowSorter(true);

        UIUtils.applyTableStyling(table);

        if (data == null || data.length == 0) {
            FlatSVGIcon icon = new FlatSVGIcon("icons/sales.svg", 36, 36);
            icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_BLUE));
            JPanel emptyState = UIUtils.createEmptyState(
                    "Sin ventas registradas aún",
                    "Aquí verás la actividad reciente de tu negocio cuando registres tus primeras ventas.",
                    icon);
            card.add(emptyState, BorderLayout.CENTER);
            return card;
        }
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createEmptyState(String message, String iconPath) {
        return UIUtils.createEmptyState(iconPath, ThemeConstants.NEON_BLUE,
                "Sin actividad por ahora", message, null, null);
    }
}
