package com.mycompany.zl_solucion_integral.views.components.dialogs;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.controllers.VentasController;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.mycompany.zl_solucion_integral.views.components.organisms.MetricCard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HistorialComprasClienteDialog extends JDialog {

    private final VentasController ventasCtrl = new VentasController();
    private final String nombreCliente;
    private final String ccCliente;

    private JTable tbVentas;
    private JTable tbDetalles;
    private JPanel metricsContainer;

    public HistorialComprasClienteDialog(Window owner, String nombreCliente, String ccCliente) {
        super(owner, "Historial de Compras — " + nombreCliente, ModalityType.APPLICATION_MODAL);
        this.nombreCliente = nombreCliente;
        this.ccCliente = ccCliente;

        setSize(920, 680);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(ThemeConstants.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Center Content (KPIs + Split Master-Detail)
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);

        metricsContainer = new JPanel(new BorderLayout());
        metricsContainer.setOpaque(false);
        centerPanel.add(metricsContainer, BorderLayout.NORTH);
        centerPanel.add(createSplitTablePanel(), BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer Actions
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel);

        // Cargar datos
        cargarDatos();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        FlatSVGIcon icon = new FlatSVGIcon("icons/history.svg", 24, 24);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_PURPLE));

        JLabel lblTitle = new JLabel("Historial Comercial", icon, SwingConstants.LEFT);
        lblTitle.setFont(ThemeConstants.FONT_TITLE);
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);

        String ccInfo = (ccCliente != null && !ccCliente.trim().isEmpty() && !"N/A".equalsIgnoreCase(ccCliente))
                ? " (Cédula/NIT: " + ccCliente + ")" : "";
        JTextArea lblSubtitle = UIUtils.createWrappingLabel(
                "Cliente: " + nombreCliente + ccInfo,
                ThemeConstants.FONT_BODY, ThemeConstants.TEXT_SECONDARY);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblSubtitle, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMetricsPanel(double sumaTotal, int totalCompras, double promedio) {
        JPanel grid = new JPanel(new GridLayout(1, 3, 15, 0));
        grid.setOpaque(false);
        grid.setPreferredSize(new Dimension(0, 100));

        MetricCard cardTotalComprado = new MetricCard("Total Comprado", UIUtils.formatCurrency(sumaTotal),
                "Acumulado histórico", ThemeConstants.NEON_GREEN, "icons/wallet.svg");
        MetricCard cardNumCompras = new MetricCard("Compras Realizadas", totalCompras + (totalCompras == 1 ? " factura" : " facturas"),
                "Total órdenes", ThemeConstants.NEON_BLUE, "icons/sales.svg");
        MetricCard cardPromedioCompra = new MetricCard("Ticket Promedio", UIUtils.formatCurrency(promedio),
                "Promedio por compra", ThemeConstants.NEON_CYAN, "icons/cart-shopping.svg");

        grid.add(cardTotalComprado);
        grid.add(cardNumCompras);
        grid.add(cardPromedioCompra);

        return grid;
    }

    private FlatSVGIcon createIcon(String path, Color color, int w, int h) {
        FlatSVGIcon icon = new FlatSVGIcon(path, w, h);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, color));
        return icon;
    }

    private JPanel createSplitTablePanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setResizeWeight(0.55);
        splitPane.setDividerSize(8);

        // Tabla Superior: Ventas (Master)
        RoundedPanel pnlVentas = new RoundedPanel(16, ThemeConstants.CARD_BACKGROUND);
        pnlVentas.setLayout(new BorderLayout(5, 5));
        pnlVentas.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel lblVentasTitle = new JLabel("Facturas y Ventas del Cliente", createIcon("icons/sales.svg", ThemeConstants.NEON_PURPLE, 18, 18), SwingConstants.LEFT);
        lblVentasTitle.setIconTextGap(8);
        lblVentasTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        lblVentasTitle.setForeground(ThemeConstants.TEXT_PRIMARY);

        tbVentas = new JTable(new DefaultTableModel(
                new String[]{"Folio", "Fecha", "Vendedor", "Método Pago", "Estado", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        });
        tbVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scVentas = new JScrollPane(tbVentas);
        scVentas.setOpaque(false);
        scVentas.getViewport().setOpaque(false);

        pnlVentas.add(lblVentasTitle, BorderLayout.NORTH);
        pnlVentas.add(scVentas, BorderLayout.CENTER);

        // Tabla Inferior: Detalles de Venta (Detail)
        RoundedPanel pnlDetalles = new RoundedPanel(16, ThemeConstants.CARD_BACKGROUND);
        pnlDetalles.setLayout(new BorderLayout(5, 5));
        pnlDetalles.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel lblDetallesTitle = new JLabel("Productos de la Factura Seleccionada", createIcon("icons/products.svg", ThemeConstants.NEON_PURPLE, 18, 18), SwingConstants.LEFT);
        lblDetallesTitle.setIconTextGap(8);
        lblDetallesTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        lblDetallesTitle.setForeground(ThemeConstants.TEXT_PRIMARY);

        tbDetalles = new JTable(new DefaultTableModel(
                new String[]{"Producto", "Código/SKU", "Cantidad", "Precio Unit.", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        });
        JScrollPane scDetalles = new JScrollPane(tbDetalles);
        scDetalles.setOpaque(false);
        scDetalles.getViewport().setOpaque(false);

        pnlDetalles.add(lblDetallesTitle, BorderLayout.NORTH);
        pnlDetalles.add(scDetalles, BorderLayout.CENTER);

        splitPane.setTopComponent(pnlVentas);
        splitPane.setBottomComponent(pnlDetalles);

        // Listener de selección en tabla de ventas para actualizar detalles
        tbVentas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tbVentas.getSelectedRow();
                if (row != -1) {
                    int ventaId = (Integer) tbVentas.getModel().getValueAt(row, 0);
                    ventasCtrl.mostrarDetallesVenta(tbDetalles, ventaId);
                    UIUtils.applyTableStyling(tbDetalles);
                } else {
                    ((DefaultTableModel) tbDetalles.getModel()).setRowCount(0);
                }
            }
        });

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(splitPane, BorderLayout.CENTER);
        return container;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        NeonButton btnCerrar = new NeonButton("Cerrar");
        btnCerrar.setIcon(createIcon("icons/xmark.svg", ThemeConstants.TEXT_SECONDARY, 16, 16));
        btnCerrar.setIconTextGap(6);
        btnCerrar.setPreferredSize(new Dimension(120, 38));
        btnCerrar.addActionListener(e -> dispose());

        panel.add(btnCerrar);
        return panel;
    }

    private void cargarDatos() {
        ventasCtrl.mostrarVentasPorCliente(tbVentas, ccCliente, nombreCliente);
        UIUtils.applyTableStyling(tbVentas);

        // Calcular resumen KPI
        DefaultTableModel model = (DefaultTableModel) tbVentas.getModel();
        int totalCompras = model.getRowCount();
        double sumaTotal = 0.0;

        for (int i = 0; i < totalCompras; i++) {
            Object val = model.getValueAt(i, 5); // Columna 5: Total
            if (val instanceof Number) {
                sumaTotal += ((Number) val).doubleValue();
            }
        }

        double promedio = totalCompras > 0 ? sumaTotal / totalCompras : 0.0;

        metricsContainer.removeAll();
        metricsContainer.add(createMetricsPanel(sumaTotal, totalCompras, promedio), BorderLayout.CENTER);
        metricsContainer.revalidate();
        metricsContainer.repaint();

        // Seleccionar la primera venta automáticamente si existe
        if (totalCompras > 0) {
            tbVentas.setRowSelectionInterval(0, 0);
        }
    }
}

