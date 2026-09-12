package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.ProductoController;
import com.mycompany.zl_solucion_integral.models.Producto;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonPieChart;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.UIMessages;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Map;

public class ProductPage extends JPanel {
    private final ProductoController productoCtrl = new ProductoController();
    private JTable tbProductos;
    private JTextField txtNombre, txtPrecio, txtCantidad, txtCodigo;
    private JComboBox<String> cbCategoria;
    private JPanel chartContainer;
    private JScrollPane productsScroll;
    private JPanel centerPanel;
    private JPanel formPanel;
    private JPanel rightPanel;
    private JPanel header;

    public ProductPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header (microcopy de contexto usando UIUtils)
        header = UIUtils.createHeader("icons/products.svg", ThemeConstants.NEON_PURPLE,
                "Gestión de inventario",
                "Administra tu catálogo y mantén el control de tu stock en tiempo real");
        add(header, BorderLayout.NORTH);

        // Contenido Principal (Formulario + Tabla)
        centerPanel = new JPanel(new BorderLayout(25, 25));
        centerPanel.setOpaque(false);

        // Formulario (Izquierda) envuelto en JScrollPane para evitar desbordamiento vertical
        JScrollPane formScroll = new JScrollPane(createFormPanel());
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.setPreferredSize(new Dimension(360, 0));

        // Contenedor para Tabla y Gráfico (Derecha)
        rightPanel = new JPanel(new BorderLayout(0, 20));
        rightPanel.setOpaque(false);

        rightPanel.add(createTablePanel(), BorderLayout.CENTER);

        // Gráfico de Distribución (Abajo de la tabla, formato compacto)
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setOpaque(false);
        chartContainer.setPreferredSize(new Dimension(0, 150));
        updateChart();
        rightPanel.add(chartContainer, BorderLayout.SOUTH);

        formPanel = new JPanel(new BorderLayout());
        formPanel.setOpaque(false);
        formPanel.add(formScroll, BorderLayout.CENTER);

        centerPanel.add(formPanel, BorderLayout.WEST);
        centerPanel.add(rightPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Reflow adaptable: en móvil el formulario pasa arriba (una columna).
        LayoutResponsive.listen(this, this::aplicarBreakpoint);

        // Cargar datos iniciales
        refreshData();
    }

    /** Reorganiza formulario y tabla según el ancho (vertical en móvil). */
    private void aplicarBreakpoint(LayoutResponsive.Breakpoint bp) {
        boolean movil = LayoutResponsive.esColumnaUnica(bp);
        // En móvil el formulario ocupa todo el ancho; en escritorio se fija a 360px.
        formPanel.setPreferredSize(movil ? null : new Dimension(360, 0));
        centerPanel.removeAll();
        if (movil) {
            centerPanel.setLayout(new BorderLayout(0, 20));
            centerPanel.add(formPanel, BorderLayout.NORTH);
            centerPanel.add(rightPanel, BorderLayout.CENTER);
        } else {
            centerPanel.setLayout(new BorderLayout(25, 25));
            centerPanel.add(formPanel, BorderLayout.WEST);
            centerPanel.add(rightPanel, BorderLayout.CENTER);
        }
        centerPanel.revalidate();
        centerPanel.repaint();
        header.revalidate();
        header.repaint();
    }

    private JPanel createFormPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 16, 0);
        JLabel formTitle = new JLabel("Registrar producto");
        formTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        formTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        p.add(formTitle, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 4, 0);
        p.add(createLabel("NOMBRE DEL PRODUCTO"), gbc);
        txtNombre = createTextField("Ej: Taladro Inalámbrico 20V");
        setupFieldIcon(txtNombre, "icons/products.svg");
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 12, 0);
        p.add(txtNombre, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 4, 0);
        p.add(createLabel("CÓDIGO / SKU"), gbc);
        txtCodigo = createTextField("Ej: SKU-001");
        setupFieldIcon(txtCodigo, "icons/reports.svg");
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 12, 0);
        p.add(UIUtils.createFieldWithHelper(txtCodigo, "Ej: SKU-001 — código único del producto"), gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 4, 0);
        p.add(createLabel("CATEGORÍA"), gbc);
        cbCategoria = new JComboBox<>(new String[]{"HERRAMIENTAS", "MATERIALES", "ELECTRÓNICA", "BEBIDAS", "LIMPIEZA", "OTROS"});
        cbCategoria.setBackground(ThemeConstants.INPUT_BACKGROUND);
        cbCategoria.setForeground(ThemeConstants.TEXT_PRIMARY);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 12, 0);
        p.add(cbCategoria, gbc);

        // Fila para Precio y Cantidad
        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setOpaque(false);
        
        JPanel p1 = new JPanel(new BorderLayout(0, 4)); p1.setOpaque(false);
        p1.add(createLabel("PRECIO"), BorderLayout.NORTH);
        txtPrecio = createTextField("0.00"); 
        setupFieldIcon(txtPrecio, "icons/sales.svg");
        p1.add(txtPrecio, BorderLayout.CENTER);
        p1.add(UIUtils.createHelperLabel("Ej: 2500.00"), BorderLayout.SOUTH);
        
        JPanel p2 = new JPanel(new BorderLayout(0, 4)); p2.setOpaque(false);
        p2.add(createLabel("STOCK"), BorderLayout.NORTH);
        txtCantidad = createTextField("0"); 
        setupFieldIcon(txtCantidad, "icons/dashboard.svg");
        p2.add(txtCantidad, BorderLayout.CENTER);
        p2.add(UIUtils.createHelperLabel("Ej: 50 unidades"), BorderLayout.SOUTH);
        
        row.add(p1); row.add(p2);
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 18, 0);
        p.add(row, gbc);

        // Botones de Acción en fila horizontal compacta
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);

        NeonButton btnSave = new NeonButton("Crear producto");
        btnSave.setNeonColor(ThemeConstants.NEON_GREEN);
        btnSave.setIcon(createIcon("icons/plus.svg", ThemeConstants.NEON_GREEN, 16, 16));
        btnSave.setIconTextGap(6);
        btnSave.setPreferredSize(new Dimension(0, 42));
        btnSave.addActionListener(e -> saveProduct());
        btnSave.setToolTipText("Guarda un nuevo producto en el catálogo.");

        NeonButton btnClear = new NeonButton("Limpiar");
        btnClear.setNeonColor(ThemeConstants.NEON_BLUE);
        btnClear.setIcon(createIcon("icons/settings.svg", ThemeConstants.NEON_BLUE, 16, 16));
        btnClear.setIconTextGap(6);
        btnClear.setPreferredSize(new Dimension(0, 42));
        btnClear.addActionListener(e -> clearFields());
        btnClear.setToolTipText("Limpia el formulario para registrar un producto nuevo");

        btnRow.add(btnSave);
        btnRow.add(btnClear);

        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 0, 0);
        p.add(btnRow, gbc);

        return p;
    }

    private JPanel createTablePanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout(0, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel tableTitle = new JLabel("Productos registrados");
        tableTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        tableTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        headerPanel.add(tableTitle, BorderLayout.WEST);

        // Acciones por registro (Actualizar y Eliminar)
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionRow.setOpaque(false);

        NeonButton btnUpdate = new NeonButton("Actualizar");
        btnUpdate.setNeonColor(ThemeConstants.NEON_BLUE);
        btnUpdate.setIcon(createIcon("icons/update.svg", ThemeConstants.NEON_BLUE, 16, 16));
        btnUpdate.setIconTextGap(6);
        btnUpdate.setPreferredSize(new Dimension(115, 34));
        btnUpdate.addActionListener(e -> updateProductFromForm());
        btnUpdate.setToolTipText("Actualiza el producto seleccionado con los datos del formulario");

        NeonButton btnDelete = new NeonButton("Eliminar");
        btnDelete.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnDelete.setIcon(createIcon("icons/trash.svg", ThemeConstants.NEON_PURPLE, 16, 16));
        btnDelete.setIconTextGap(6);
        btnDelete.setPreferredSize(new Dimension(110, 34));
        btnDelete.addActionListener(e -> deleteProduct());
        btnDelete.setToolTipText("Elimina el registro seleccionado de la tabla");

        actionRow.add(btnUpdate);
        actionRow.add(btnDelete);
        headerPanel.add(actionRow, BorderLayout.EAST);

        p.add(headerPanel, BorderLayout.NORTH);

        tbProductos = new JTable();
        tbProductos.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbProductos.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbProductos.setRowHeight(ThemeConstants.TABLE_ROW_HEIGHT);
        tbProductos.setShowGrid(false);
        tbProductos.setIntercellSpacing(new Dimension(0, 0));
        tbProductos.setFillsViewportHeight(true);
        tbProductos.setFont(ThemeConstants.FONT_SMALL);
        tbProductos.setSelectionBackground(new Color(59, 130, 246, 70));
        tbProductos.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);
        
        tbProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedProductToForm();
        });

        productsScroll = new JScrollPane(tbProductos);
        productsScroll.setOpaque(false);
        productsScroll.getViewport().setOpaque(false);
        productsScroll.setBorder(BorderFactory.createEmptyBorder());
        
        p.add(productsScroll, BorderLayout.CENTER);
        return p;
    }

    private void loadSelectedProductToForm() {
        int selectedId = productoCtrl.obtenerIdProductoSeleccionado(tbProductos);
        if (selectedId != -1) {
            Producto p = productoCtrl.obtenerProductoPorId(selectedId);
            if (p != null) {
                txtNombre.setText(p.getProducto());
                txtCodigo.setText(p.getCodigo());
                txtPrecio.setText(String.valueOf(p.getPrecio()));
                txtCantidad.setText(String.valueOf(p.getCantidad()));
                cbCategoria.setSelectedItem(p.getCategoria() != null ? p.getCategoria() : "OTROS");
            }
        }
    }

    private void updateProductFromForm() {
        int selectedId = productoCtrl.obtenerIdProductoSeleccionado(tbProductos);
        if (selectedId == -1) {
            UIUtils.showError(this, "Seleccione un producto de la tabla para actualizar.");
            return;
        }

        String name = txtNombre.getText().trim();
        String code = txtCodigo.getText().trim();
        String priceStr = txtPrecio.getText().trim();
        String qtyStr = txtCantidad.getText().trim();
        String cat = (String) cbCategoria.getSelectedItem();

        if (name.isEmpty() || code.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            UIUtils.showError(this, UIMessages.MSG_CAMPOS_OBLIGATORIOS);
            return;
        }

        Double price = com.mycompany.zl_solucion_integral.config.Validaciones.parseDecimalNoNegativo(priceStr);
        Integer qty = com.mycompany.zl_solucion_integral.config.Validaciones.parseEnteroPositivo(qtyStr);

        if (price == null || qty == null) {
            UIUtils.showError(this, "Precio y stock deben ser valores numéricos válidos.");
            return;
        }

        Producto p = new Producto(selectedId, name, price, qty, code, price * qty, cat);
        com.mycompany.zl_solucion_integral.config.ResultadoOperacion res = productoCtrl.modificarProducto(p);
        if (res.esExito()) {
            UIUtils.showSuccess(this, res.getMensaje());
            refreshData();
            clearFields();
        } else {
            UIUtils.showError(this, res.getMensaje());
        }
    }

    private void updateChart() {
        chartContainer.removeAll();
        Map<String, Double> dist = productoCtrl.obtenerDistribucionCategorias();
        RoundedPanel card = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        card.setLayout(new BorderLayout());
        card.add(new NeonPieChart("Distribución por Categorías", dist), BorderLayout.CENTER);
        chartContainer.add(card, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private void saveProduct() {
        String name = txtNombre.getText().trim();
        String code = txtCodigo.getText().trim();
        String priceStr = txtPrecio.getText().trim();
        String qtyStr = txtCantidad.getText().trim();
        String cat = (String) cbCategoria.getSelectedItem();

        if (name.isEmpty() || code.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            UIUtils.showError(this, UIMessages.MSG_CAMPOS_OBLIGATORIOS);
            return;
        }

        Double price = com.mycompany.zl_solucion_integral.config.Validaciones.parseDecimalNoNegativo(priceStr);
        Integer qty = com.mycompany.zl_solucion_integral.config.Validaciones.parseEnteroPositivo(qtyStr);

        if (price == null) {
            UIUtils.showError(this, "El precio debe ser un número decimal no negativo.");
            return;
        }
        if (qty == null) {
            UIUtils.showError(this, "La cantidad (stock) debe ser un número entero positivo.");
            return;
        }

        Producto p = new Producto(name, price, qty, code, cat);
        com.mycompany.zl_solucion_integral.config.ResultadoOperacion res = productoCtrl.agregarOActualizarProductoSiExiste(p);
        
        if (res.esExito()) {
            UIUtils.showSuccess(this, res.getMensaje());
            refreshData();
            clearFields();
        } else {
            UIUtils.showError(this, res.getMensaje());
        }
    }

    private void deleteProduct() {
        int selectedId = productoCtrl.obtenerIdProductoSeleccionado(tbProductos);
        if (selectedId == -1) {
            UIUtils.showError(this, "Seleccione un producto de la tabla.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el producto seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            com.mycompany.zl_solucion_integral.config.ResultadoOperacion res = productoCtrl.eliminarProducto(selectedId);
            if (res.esExito()) {
                UIUtils.showSuccess(this, res.getMensaje());
                refreshData();
                clearFields();
            } else {
                UIUtils.showError(this, res.getMensaje());
            }
        }
    }

    private void refreshData() {
        productoCtrl.mostrarProductos(tbProductos);
        estilizarTabla();
        updateChart();
    }

    private void clearFields() {
        txtNombre.setText("");
        txtCodigo.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");
    }

    private void estilizarTabla() {
        if (tbProductos.getColumnCount() > 0
                && "Id".equals(tbProductos.getColumnName(0))) {
            tbProductos.removeColumn(tbProductos.getColumnModel().getColumn(0));
        }

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(59, 130, 246, 40));
                } else {
                    c.setBackground(row % 2 == 0
                            ? ThemeConstants.CARD_BACKGROUND
                            : ThemeConstants.TABLE_ZEBRA);
                    c.setForeground(ThemeConstants.TEXT_SECONDARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        for (int i = 0; i < tbProductos.getColumnCount(); i++) {
            tbProductos.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        int[] preferredWidths = {220, 100, 80, 105, 135};
        for (int i = 0; i < tbProductos.getColumnCount(); i++) {
            int width = preferredWidths[Math.min(i, preferredWidths.length - 1)];
            tbProductos.getColumnModel().getColumn(i).setPreferredWidth(width);
        }

        actualizarEstadoVacio();
        JTableHeader header = tbProductos.getTableHeader();
        header.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        header.setForeground(ThemeConstants.TEXT_SECONDARY);
        header.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        header.setPreferredSize(new Dimension(0, 32));
        header.setReorderingAllowed(false);
    }

    private void actualizarEstadoVacio() {
        if (tbProductos.getRowCount() == 0) {
            productsScroll.setViewportView(createEmptyState());
        } else {
            productsScroll.setViewportView(tbProductos);
        }
        productsScroll.revalidate();
        productsScroll.repaint();
    }

    private JPanel createEmptyState() {
        return UIUtils.createEmptyState(
                "icons/products.svg", ThemeConstants.NEON_CYAN,
                "Todavía no tienes productos en tu catálogo",
                "Registra tu primer producto con el formulario de la izquierda para empezar a vender.",
                "Registrar primer producto",
                () -> txtNombre.requestFocusInWindow());
    }

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(ThemeConstants.TEXT_SECONDARY);
        l.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        return l;
    }

    private void setupFieldIcon(JTextField f, String icon) {
        JLabel lbl = new JLabel(createIcon(icon, ThemeConstants.TEXT_SECONDARY, 17, 17));
        lbl.setForeground(ThemeConstants.TEXT_SECONDARY);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        f.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, lbl);
    }

    private FlatSVGIcon createIcon(String path, Color color, int width, int height) {
        FlatSVGIcon icon = new FlatSVGIcon(path, width, height);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, color));
        return icon;
    }

    private JTextField createTextField(String placeholder) {
        JTextField f = new JTextField();
        if (placeholder != null && !placeholder.isEmpty()) {
            f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        }
        f.setBackground(ThemeConstants.INPUT_BACKGROUND);
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_PURPLE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return f;
    }
}
