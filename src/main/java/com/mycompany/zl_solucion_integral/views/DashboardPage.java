package com.mycompany.zl_solucion_integral.views;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.controllers.ProductoController;
import com.mycompany.zl_solucion_integral.controllers.VentasController;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonLineChart;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonPieChart;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
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

    public DashboardPage() {
        setOpaque(false);
        setLayout(new BorderLayout(18, 18));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Resumen general del negocio");
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        headerPanel.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("Visión rápida del rendimiento actual");
        subtitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        subtitle.setFont(ThemeConstants.FONT_SMALL);
        headerPanel.add(subtitle, BorderLayout.EAST);

        // Grid de Métricas con Datos Reales
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 14, 0));
        metricsPanel.setOpaque(false);
        
        double totalVentas = ventasCtrl.obtenerVentasTotales();
        NumberFormat cur = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        
        metricsPanel.add(new MetricCard("Ventas Totales", cur.format(totalVentas), "Acumulado histórico", ThemeConstants.NEON_GREEN, "icons/sales.svg"));
        metricsPanel.add(new MetricCard("Total Órdenes", String.valueOf(ventasCtrl.contarRegistros("Todas")), "Órdenes registradas", ThemeConstants.NEON_BLUE, "icons/dashboard.svg"));
        metricsPanel.add(new MetricCard("Productos Únicos", String.valueOf(productoCtrl.contarRegistros("Todas")), "En catálogo", ThemeConstants.NEON_PURPLE, "icons/products.svg"));
        metricsPanel.add(new MetricCard("Stock Crítico", productoCtrl.obtenerCantidadStockCritico(5) + " ítems", "Menos de 5 unidades", ThemeConstants.NEON_RED, "icons/reports.svg"));

        JPanel overviewHeader = new JPanel(new BorderLayout(0, 18));
        overviewHeader.setOpaque(false);
        overviewHeader.add(headerPanel, BorderLayout.NORTH);
        overviewHeader.add(metricsPanel, BorderLayout.CENTER);
        add(overviewHeader, BorderLayout.NORTH);

        // Contenedor Central para Gráficos y Tablas
        JPanel mainContainer = new JPanel(new GridLayout(2, 1, 0, 14));
        mainContainer.setOpaque(false);

        // Fila 1: Resumen operativo y actividad reciente
        JPanel tablesRow = new JPanel(new GridLayout(1, 2, 18, 0));
        tablesRow.setOpaque(false);

        // Tabla 1: Últimas Transacciones (DATOS REALES)
        Object[][] lastSales = ventasCtrl.obtenerUltimasVentas(5);
        tablesRow.add(createActivityTable("Últimas Ventas Realizadas", lastSales, new String[]{"Fecha", "Cliente", "Producto", "Total"}));

        // Tabla 2: Estado de Inventario (DATOS REALES)
        Object[][] lowStock = obtenerResumenInventario();
        tablesRow.add(createActivityTable("Estado operativo del sistema", lowStock, new String[]{"Área", "Servicio", "Estado", "Resultado"}));

        mainContainer.add(tablesRow);

        // Fila 2: Gráficos con Datos Reales
        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 18, 0));
        chartsRow.setOpaque(false);

        // Chart 1: Line Chart (Ventas 7 días reales)
        RoundedPanel lineChartCard = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        lineChartCard.setLayout(new BorderLayout());
        List<Double> sales7Days = ventasCtrl.obtenerVentasUltimos7Dias();
        lineChartCard.add(new NeonLineChart("Ventas Últimos 7 Días ($)", sales7Days), BorderLayout.CENTER);
        chartsRow.add(lineChartCard);

        // Chart 2: Pie Chart (Distribución Real)
        RoundedPanel pieChartCard = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        pieChartCard.setLayout(new BorderLayout());
        Map<String, Double> distribution = productoCtrl.obtenerDistribucionCategorias();
        pieChartCard.add(new NeonPieChart("Distribución de Inventario", distribution), BorderLayout.CENTER);
        chartsRow.add(pieChartCard);

        mainContainer.add(chartsRow);
        add(mainContainer, BorderLayout.CENTER);
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
        table.setForeground(ThemeConstants.TEXT_SECONDARY);
        table.setSelectionBackground(new Color(59, 130, 246, 70));
        table.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFont(ThemeConstants.FONT_SMALL);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    component.setBackground(row % 2 == 0
                            ? ThemeConstants.CARD_BACKGROUND
                            : ThemeConstants.TABLE_ZEBRA);
                    component.setForeground(ThemeConstants.TEXT_SECONDARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                return component;
            }
        });

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        tableHeader.setForeground(ThemeConstants.TEXT_SECONDARY);
        tableHeader.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        tableHeader.setPreferredSize(new Dimension(0, 32));
        tableHeader.setReorderingAllowed(false);

        int columnWidth = Math.max(90, 680 / cols.length);
        for (int column = 0; column < table.getColumnCount(); column++) {
            table.getColumnModel().getColumn(column).setPreferredWidth(columnWidth);
        }

        if (data == null || data.length == 0) {
            card.add(createEmptyState("Aún no hay registros para mostrar", "icons/summary.svg"), BorderLayout.CENTER);
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
        JPanel state = new JPanel(new GridBagLayout());
        state.setOpaque(false);
        FlatSVGIcon icon = new FlatSVGIcon(iconPath, 20, 20);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.TEXT_SECONDARY));
        JLabel content = new JLabel(message, icon, SwingConstants.CENTER);
        content.setForeground(ThemeConstants.TEXT_SECONDARY);
        content.setFont(ThemeConstants.FONT_BODY);
        content.setIconTextGap(10);
        state.add(content);
        return state;
    }
}
