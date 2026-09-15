package com.mycompany.zl_solucion_integral.views;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.controllers.ComprasController;
import com.mycompany.zl_solucion_integral.models.Sesion;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.mycompany.zl_solucion_integral.views.components.dialogs.RegistrarCompraDialog;
import com.mycompany.zl_solucion_integral.views.components.organisms.MetricCard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ComprasPage extends JPanel {

    private final ComprasController comprasCtrl = new ComprasController();
    private final Sesion sesion;

    private JTable tbCompras;
    private JTable tbDetalles;
    private JTextField txtBuscar;
    private JPanel metricsContainer;

    public ComprasPage() {
        this(null);
    }

    public ComprasPage(Sesion sesion) {
        this.sesion = sesion;
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Center Area (Metrics + ActionBar + Split Table)
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);

        metricsContainer = new JPanel(new BorderLayout());
        metricsContainer.setOpaque(false);
        centerPanel.add(metricsContainer, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(createActionBar(), BorderLayout.NORTH);
        contentPanel.add(createSplitTablePanel(), BorderLayout.CENTER);

        centerPanel.add(contentPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createActionBar() {
        JPanel bar = new JPanel(new BorderLayout(15, 0));
        bar.setOpaque(false);

        txtBuscar = new JTextField();
        txtBuscar.setBackground(ThemeConstants.INPUT_BACKGROUND);
        txtBuscar.setForeground(ThemeConstants.TEXT_PRIMARY);
        txtBuscar.setCaretColor(ThemeConstants.TEXT_PRIMARY);
        txtBuscar.setFont(ThemeConstants.FONT_BODY);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por proveedor o # factura...");
        txtBuscar.setPreferredSize(new Dimension(320, 36));
        
        FlatSVGIcon searchIcon = new FlatSVGIcon("icons/search.svg", 16, 16);
        searchIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.TEXT_SECONDARY));
        JLabel lblSearch = new JLabel(searchIcon);
        lblSearch.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        txtBuscar.putClientProperty(com.formdev.flatlaf.FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, lblSearch);
        txtBuscar.addActionListener(e -> refreshData());

        NeonButton btnBuscar = new NeonButton("Buscar");
        btnBuscar.setNeonColor(ThemeConstants.NEON_BLUE);
        btnBuscar.setIcon(createIcon("icons/search.svg", ThemeConstants.NEON_BLUE, 16, 16));
        btnBuscar.setIconTextGap(6);
        btnBuscar.setPreferredSize(new Dimension(110, 36));
        btnBuscar.addActionListener(e -> refreshData());

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSearch.setOpaque(false);
        pnlSearch.add(txtBuscar);
        pnlSearch.add(btnBuscar);

        NeonButton btnNuevaEntrada = new NeonButton("Registrar Nueva Entrada");
        btnNuevaEntrada.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnNuevaEntrada.setIcon(createIcon("icons/plus.svg", ThemeConstants.NEON_PURPLE, 16, 16));
        btnNuevaEntrada.setIconTextGap(6);
        btnNuevaEntrada.setPreferredSize(new Dimension(220, 36));
        btnNuevaEntrada.setToolTipText("Registrar factura de proveedor e ingresar productos a bodega");
        btnNuevaEntrada.addActionListener(e -> abrirDialogoRegistrarCompra());

        bar.add(pnlSearch, BorderLayout.WEST);
        bar.add(btnNuevaEntrada, BorderLayout.EAST);
        return bar;
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

        // Tabla Master: Compras
        RoundedPanel pnlCompras = new RoundedPanel(16, ThemeConstants.CARD_BACKGROUND);
        pnlCompras.setLayout(new BorderLayout(5, 5));
        pnlCompras.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel lblComprasTitle = new JLabel("Historial de Entradas Recibidas", createIcon("icons/suppliers.svg", ThemeConstants.NEON_PURPLE, 18, 18), SwingConstants.LEFT);
        lblComprasTitle.setIconTextGap(8);
        lblComprasTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        lblComprasTitle.setForeground(ThemeConstants.TEXT_PRIMARY);

        tbCompras = new JTable(new DefaultTableModel(
                new String[]{"Folio", "Fecha", "Proveedor", "Num. Factura", "Registrado Por", "Total Compra"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        });
        tbCompras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scCompras = new JScrollPane(tbCompras);
        scCompras.setOpaque(false);
        scCompras.getViewport().setOpaque(false);

        pnlCompras.add(lblComprasTitle, BorderLayout.NORTH);
        pnlCompras.add(scCompras, BorderLayout.CENTER);

        // Tabla Detail: Productos de la Compra
        RoundedPanel pnlDetalles = new RoundedPanel(16, ThemeConstants.CARD_BACKGROUND);
        pnlDetalles.setLayout(new BorderLayout(5, 5));
        pnlDetalles.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel lblDetallesTitle = new JLabel("Productos Recibidos en la Orden Seleccionada", createIcon("icons/boxes-stacked.svg", ThemeConstants.NEON_PURPLE, 18, 18), SwingConstants.LEFT);
        lblDetallesTitle.setIconTextGap(8);
        lblDetallesTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        lblDetallesTitle.setForeground(ThemeConstants.TEXT_PRIMARY);

        tbDetalles = new JTable(new DefaultTableModel(
                new String[]{"Producto", "Código/SKU", "Cantidad Recibida", "Costo Unit. ($)", "Subtotal ($)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        });
        JScrollPane scDetalles = new JScrollPane(tbDetalles);
        scDetalles.setOpaque(false);
        scDetalles.getViewport().setOpaque(false);

        pnlDetalles.add(lblDetallesTitle, BorderLayout.NORTH);
        pnlDetalles.add(scDetalles, BorderLayout.CENTER);

        splitPane.setTopComponent(pnlCompras);
        splitPane.setBottomComponent(pnlDetalles);

        // Listener de selección en compras para recargar productos
        tbCompras.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tbCompras.getSelectedRow();
                if (row != -1) {
                    int compraId = (Integer) tbCompras.getModel().getValueAt(row, 0);
                    comprasCtrl.mostrarDetallesCompra(tbDetalles, compraId);
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

    private void abrirDialogoRegistrarCompra() {
        Window window = SwingUtilities.getWindowAncestor(this);
        RegistrarCompraDialog dialog = new RegistrarCompraDialog(window, sesion);
        dialog.setVisible(true);
        refreshData();
    }

    public void refreshData() {
        String filtro = txtBuscar != null ? txtBuscar.getText().trim() : "";
        comprasCtrl.mostrarHistorialCompras(tbCompras, filtro);

        double[] kpis = comprasCtrl.obtenerResumenCompras();
        metricsContainer.removeAll();

        JPanel grid = new JPanel(new GridLayout(1, 3, 15, 0));
        grid.setOpaque(false);
        grid.setPreferredSize(new Dimension(0, 100));

        grid.add(new MetricCard("Total Invertido", UIUtils.formatCurrency(kpis[0]), "Acumulado compras", ThemeConstants.NEON_GREEN, "icons/wallet.svg"));
        grid.add(new MetricCard("Entradas Recibidas", (int) kpis[1] + " facturas", "Ordenes registradas", ThemeConstants.NEON_BLUE, "icons/suppliers.svg"));
        grid.add(new MetricCard("Proveedores Atendidos", (int) kpis[2] + " activos", "Proveedores de insumos", ThemeConstants.NEON_PURPLE, "icons/providers.svg"));

        metricsContainer.add(grid, BorderLayout.CENTER);
        metricsContainer.revalidate();
        metricsContainer.repaint();

        if (tbCompras.getRowCount() > 0) {
            tbCompras.setRowSelectionInterval(0, 0);
        }
    }
}
