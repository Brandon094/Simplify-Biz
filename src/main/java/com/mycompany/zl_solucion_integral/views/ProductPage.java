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
    private JTextField txtNombre, txtPrecio, txtPrecioCosto, txtCantidad, txtCodigo;
    private JComboBox<String> cbCategoria;
    private JPanel chartContainer;
    private JScrollPane productsScroll;
    private JPanel centerPanel;
    private JPanel formPanel;
    private JPanel rightPanel;
    private JPanel header;
    private int selectedProductId = -1;

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
        chartContainer.setPreferredSize(new Dimension(0, 190));
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

        // Autocompletado genérico (DRY) para buscar y rellenar productos existentes al digitar en el inventario
        setupAutocompletes();

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
        cbCategoria = new JComboBox<>();
        cbCategoria.setEditable(true);
        cbCategoria.setBackground(ThemeConstants.INPUT_BACKGROUND);
        cbCategoria.setForeground(ThemeConstants.TEXT_PRIMARY);
        cargarCategorias();
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 12, 0);
        p.add(cbCategoria, gbc);

        // Fila para Precio Venta, Precio Costo y Cantidad
        JPanel row = new JPanel(new GridLayout(1, 3, 8, 0));
        row.setOpaque(false);
        
        JPanel p1 = new JPanel(new BorderLayout(0, 4)); p1.setOpaque(false);
        p1.add(createLabel("P. VENTA"), BorderLayout.NORTH);
        txtPrecio = createTextField("0.00"); 
        setupFieldIcon(txtPrecio, "icons/sales.svg");
        p1.add(txtPrecio, BorderLayout.CENTER);
        p1.add(UIUtils.createHelperLabel("Ej: 2500"), BorderLayout.SOUTH);
        
        JPanel pCosto = new JPanel(new BorderLayout(0, 4)); pCosto.setOpaque(false);
        pCosto.add(createLabel("P. COSTO"), BorderLayout.NORTH);
        txtPrecioCosto = createTextField("0.00");
        setupFieldIcon(txtPrecioCosto, "icons/reports.svg");
        pCosto.add(txtPrecioCosto, BorderLayout.CENTER);
        pCosto.add(UIUtils.createHelperLabel("Ej: 1800"), BorderLayout.SOUTH);

        JPanel p2 = new JPanel(new BorderLayout(0, 4)); p2.setOpaque(false);
        p2.add(createLabel("STOCK"), BorderLayout.NORTH);
        txtCantidad = createTextField("0"); 
        setupFieldIcon(txtCantidad, "icons/dashboard.svg");
        p2.add(txtCantidad, BorderLayout.CENTER);
        p2.add(UIUtils.createHelperLabel("Ej: 50"), BorderLayout.SOUTH);
        
        row.add(p1); row.add(pCosto); row.add(p2);
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
        int viewRow = tbProductos.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = tbProductos.convertRowIndexToModel(viewRow);
            Object idVal = tbProductos.getModel().getValueAt(modelRow, 0);
            Integer idParsed = com.mycompany.zl_solucion_integral.config.Validaciones.parseEntero(idVal != null ? idVal.toString() : "");
            if (idParsed != null && idParsed != -1) {
                selectedProductId = idParsed;
                Producto p = productoCtrl.obtenerProductoPorId(selectedProductId);
                if (p != null) {
                    txtNombre.setText(p.getProducto());
                    txtCodigo.setText(p.getCodigo());
                    txtPrecio.setText(String.valueOf(p.getPrecio()));
                    txtPrecioCosto.setText(String.valueOf(p.getPrecioCosto()));
                    txtCantidad.setText(String.valueOf(p.getCantidad()));
                    cbCategoria.setSelectedItem(p.getCategoria() != null ? p.getCategoria() : "OTROS");
                }
            }
        }
    }

    private void cargarCategorias() {
        Object selected = cbCategoria.getSelectedItem();
        cbCategoria.removeAllItems();
        java.util.List<String> cats = productoCtrl.obtenerCategorias();
        for (String c : cats) {
            cbCategoria.addItem(c);
        }
        if (selected != null) {
            cbCategoria.setSelectedItem(selected);
        }
    }

    private void updateProductFromForm() {
        if (selectedProductId == -1) {
            UIUtils.showError(this, "Seleccione un producto de la tabla para actualizar.");
            return;
        }

        String name = txtNombre.getText().trim();
        String code = txtCodigo.getText().trim();
        String priceStr = txtPrecio.getText().trim();
        String costStr = txtPrecioCosto.getText().trim();
        String qtyStr = txtCantidad.getText().trim();
        Object rawCat = cbCategoria.getSelectedItem();
        String cat = rawCat != null ? rawCat.toString().trim().toUpperCase() : "GENERAL";
        if (cat.isEmpty()) cat = "GENERAL";

        if (name.isEmpty() || code.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            UIUtils.showError(this, UIMessages.MSG_CAMPOS_OBLIGATORIOS);
            return;
        }

        Double price = com.mycompany.zl_solucion_integral.config.Validaciones.parseDecimalNoNegativo(priceStr);
        Double cost = costStr.isEmpty() ? 0.0 : com.mycompany.zl_solucion_integral.config.Validaciones.parseDecimalNoNegativo(costStr);
        Integer qty = com.mycompany.zl_solucion_integral.config.Validaciones.parseEnteroPositivo(qtyStr);

        if (price == null || qty == null || cost == null) {
            UIUtils.showError(this, "Precio de venta, costo y stock deben ser valores numéricos válidos.");
            return;
        }

        productoCtrl.guardarCategoriaSiNoExiste(cat);
        Producto p = new Producto(selectedProductId, name, price, cost, qty, code, price * qty, cat);
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
        String costStr = txtPrecioCosto.getText().trim();
        String qtyStr = txtCantidad.getText().trim();
        Object rawCat = cbCategoria.getSelectedItem();
        String cat = rawCat != null ? rawCat.toString().trim().toUpperCase() : "GENERAL";
        if (cat.isEmpty()) cat = "GENERAL";

        if (name.isEmpty() || code.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            UIUtils.showError(this, UIMessages.MSG_CAMPOS_OBLIGATORIOS);
            return;
        }

        Double price = com.mycompany.zl_solucion_integral.config.Validaciones.parseDecimalNoNegativo(priceStr);
        Double cost = costStr.isEmpty() ? 0.0 : com.mycompany.zl_solucion_integral.config.Validaciones.parseDecimalNoNegativo(costStr);
        Integer qty = com.mycompany.zl_solucion_integral.config.Validaciones.parseEnteroPositivo(qtyStr);

        if (price == null || cost == null) {
            UIUtils.showError(this, "Los precios deben ser números decimales no negativos.");
            return;
        }
        if (qty == null) {
            UIUtils.showError(this, "La cantidad (stock) debe ser un número entero positivo.");
            return;
        }

        productoCtrl.guardarCategoriaSiNoExiste(cat);
        Producto p = new Producto(0, name, price, cost, qty, code, price * qty, cat);
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
        if (selectedProductId == -1) {
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
            com.mycompany.zl_solucion_integral.config.ResultadoOperacion res = productoCtrl.eliminarProducto(selectedProductId);
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
        cargarCategorias();
        updateChart();
    }

    private void clearFields() {
        txtNombre.setText("");
        txtCodigo.setText("");
        txtPrecio.setText("");
        txtPrecioCosto.setText("");
        txtCantidad.setText("");
        selectedProductId = -1;
    }

    private void estilizarTabla() {
        UIUtils.applyTableStyling(tbProductos);
        actualizarEstadoVacio();
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

    private void setupAutocompletes() {
        com.mycompany.zl_solucion_integral.views.components.AutocompletePopup.SelectionListener<Producto> onProductSelect = p -> {
            selectedProductId = p.getId();
            txtNombre.setText(p.getProducto());
            txtCodigo.setText(p.getCodigo());
            txtPrecio.setText(String.valueOf(p.getPrecio()));
            txtCantidad.setText(String.valueOf(p.getCantidad()));
            if (p.getCategoria() != null) {
                cbCategoria.setSelectedItem(p.getCategoria().toUpperCase());
            }
            txtNombre.setForeground(ThemeConstants.NEON_GREEN);
            txtCodigo.setForeground(ThemeConstants.NEON_GREEN);
        };

        // Autocompletado en el campo Nombre del Producto
        com.mycompany.zl_solucion_integral.views.components.AutocompletePopup.attach(
            txtNombre,
            query -> productoCtrl.buscarProductosSugeridos(query),
            p -> String.format("[%s] %s - $%.2f (Stock: %d)", p.getCodigo(), p.getProducto(), p.getPrecio(), p.getCantidad()),
            onProductSelect
        );

        // Autocompletado en el campo Código / SKU
        com.mycompany.zl_solucion_integral.views.components.AutocompletePopup.attach(
            txtCodigo,
            query -> productoCtrl.buscarProductosSugeridos(query),
            p -> String.format("[%s] %s - $%.2f (Stock: %d)", p.getCodigo(), p.getProducto(), p.getPrecio(), p.getCantidad()),
            onProductSelect
        );
    }
}
