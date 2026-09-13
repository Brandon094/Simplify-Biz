package com.mycompany.zl_solucion_integral.views;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.controllers.ProductoController;
import com.mycompany.zl_solucion_integral.controllers.VentasController;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
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
    private int totalOrdenes;
    private int productosUnicos;
    private int stockCritico;
    private Object[][] ventasRecientes;
    private List<Double> ventas7Dias;
    private Map<String, Double> distribucionCategorias;

    // Referencias para el reflow adaptable.
    private JPanel metricsPanel;
    private JPanel tablesRow;
    private JPanel chartsRow;
    private final java.util.List<MetricCard> metricCards = new java.util.ArrayList<>();

    public DashboardPage() {
        setOpaque(false);
        setLayout(new BorderLayout(18, 18));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Header (microcopy de contexto)
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Resumen general del negocio");
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea subtitle = UIUtils.createWrappingLabel(
                "Visión rápida del rendimiento y la actividad de tu negocio",
                ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitle);

        // Cabecera fija; las métricas se añaden después de la carga asíncrona.
        northWrap.setOpaque(false);
        northWrap.add(headerPanel, BorderLayout.NORTH);
        add(northWrap, BorderLayout.NORTH);

        // Estado de carga: skeleton mientras los datos llegan de SQLite.
        JPanel loadingWrap = new JPanel(new BorderLayout());
        loadingWrap.setOpaque(false);
        loadingWrap.add(new ShimmerSkeleton(), BorderLayout.CENTER);
        add(loadingWrap, BorderLayout.CENTER);

        // Carga asíncrona: las consultas a SQLite corren fuera del EDT (Tarea 6 Fase 1).
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                totalVentas = ventasCtrl.obtenerVentasTotales();
                totalOrdenes = ventasCtrl.contarRegistros("Todas");
                productosUnicos = productoCtrl.contarRegistros("Todas");
                stockCritico = productoCtrl.obtenerCantidadStockCritico(5);
                ventasRecientes = ventasCtrl.obtenerUltimasVentas(5);
                ventas7Dias = ventasCtrl.obtenerVentasUltimos7Dias();
                distribucionCategorias = productoCtrl.obtenerDistribucionCategorias();
                return null;
            }

            @Override
            protected void done() {
                remove(loadingWrap);
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
        MetricCard cardOrdenes = new MetricCard("Total Órdenes", String.valueOf(totalOrdenes), "Órdenes registradas", ThemeConstants.NEON_BLUE, "icons/dashboard.svg");
        MetricCard cardProductos = new MetricCard("Productos Únicos", String.valueOf(productosUnicos), "En catálogo", ThemeConstants.NEON_PURPLE, "icons/products.svg");
        MetricCard cardStock = new MetricCard("Stock Crítico", stockCritico + " ítems", "Menos de 5 unidades", ThemeConstants.NEON_RED, "icons/reports.svg");
        metricCards.clear();
        metricCards.add(cardVentas);
        metricCards.add(cardOrdenes);
        metricCards.add(cardProductos);
        metricCards.add(cardStock);
        for (MetricCard card : metricCards) {
            metricsPanel.add(card);
        }

        northWrap.add(metricsPanel, BorderLayout.CENTER);

        // Contenedor Central
        JPanel mainContainer = new JPanel(new GridLayout(2, 1, 0, 14));
        mainContainer.setOpaque(false);

        // Fila 1: Tablas
        JPanel tablaVentasCard = createActivityTable("Últimas Ventas Realizadas", ventasRecientes, new String[]{"Fecha", "Cliente", "Producto", "Total"});
        Object[][] lowStock = obtenerResumenInventario();
        JPanel tablaEstadoCard = createActivityTable("Estado operativo del sistema", lowStock, new String[]{"Área", "Servicio", "Estado", "Resultado"});
        tablesRow = new JPanel(new GridLayout(1, 2, 18, 0));
        tablesRow.setOpaque(false);
        tablesRow.add(tablaVentasCard);
        tablesRow.add(tablaEstadoCard);
        mainContainer.add(tablesRow);

        // Fila 2: Gráficos
        RoundedPanel lineChartCard = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        lineChartCard.setLayout(new BorderLayout());
        lineChartCard.add(new NeonLineChart("Ventas Últimos 7 Días ($)", ventas7Dias), BorderLayout.CENTER);

        RoundedPanel pieChartCard = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        pieChartCard.setLayout(new BorderLayout());
        pieChartCard.add(new NeonPieChart("Distribución de Inventario", distribucionCategorias), BorderLayout.CENTER);

        chartsRow = new JPanel(new GridLayout(1, 2, 18, 0));
        chartsRow.setOpaque(false);
        chartsRow.add(lineChartCard);
        chartsRow.add(pieChartCard);

        mainContainer.add(chartsRow);
        add(mainContainer, BorderLayout.CENTER);

        // Reflow adaptable: en móvil las métricas, tablas y gráficos se apilan.
        LayoutResponsive.listen(this, bp -> {
            LayoutResponsive.reflowColumnas(metricsPanel,
                    new java.util.ArrayList<>(metricCards), 4, 14, bp);
            LayoutResponsive.reflowColumnas(tablesRow,
                    LayoutResponsive.list(tablaVentasCard, tablaEstadoCard), 2, 18, bp);
            LayoutResponsive.reflowColumnas(chartsRow,
                    LayoutResponsive.list(lineChartCard, pieChartCard), 2, 18, bp);
        });
    }

    private Object[][] obtenerResumenInventario() {
        // Podríamos traer los productos con menos stock directamente
        return new Object[][]{
            {"Revisión de Stock", "Sistema", "Auto", "OK"},
            {"Base de Datos", "SQLite", "Conectada", "En Línea"},
            {"Sincronización", "General", "100%", "Completada"}
        };
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
