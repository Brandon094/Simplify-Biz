package com.mycompany.zl_solucion_integral.views.components.dialogs;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.controllers.ComprasController;
import com.mycompany.zl_solucion_integral.controllers.ProductoController;
import com.mycompany.zl_solucion_integral.controllers.ProveedorController;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.models.Compra;
import com.mycompany.zl_solucion_integral.models.DetalleCompra;
import com.mycompany.zl_solucion_integral.models.Producto;
import com.mycompany.zl_solucion_integral.models.Proveedor;
import com.mycompany.zl_solucion_integral.models.Sesion;
import com.mycompany.zl_solucion_integral.views.components.AutocompletePopup;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrarCompraDialog extends JDialog {

    private final ComprasController comprasCtrl = new ComprasController();
    private final ProveedorController provCtrl = new ProveedorController();
    private final ProductoController prodCtrl = new ProductoController();
    private final Sesion sesion;

    private JTextField txtProveedorNombre;
    private JTextField txtProveedorNit;
    private JTextField txtNumFactura;
    private JTextField txtBuscarProducto;
    private JTextField txtCantidad;
    private JTextField txtCostoUnitario;

    private JTable tbCarrito;
    private DefaultTableModel modelCarrito;
    private JLabel lblTotalCompra;

    private Proveedor proveedorSeleccionado;
    private Producto productoSeleccionado;
    private final List<DetalleCompra> listaCarrito = new ArrayList<>();

    public RegistrarCompraDialog(Window owner, Sesion sesion) {
        super(owner, "Nueva Entrada de Abastecimiento", ModalityType.APPLICATION_MODAL);
        this.sesion = sesion;

        setSize(950, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(ThemeConstants.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Center Split / Form
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);

        centerPanel.add(createFormEncabezado(), BorderLayout.NORTH);
        centerPanel.add(createCarritoPanel(), BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer Actions
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        FlatSVGIcon icon = new FlatSVGIcon("icons/suppliers.svg", 24, 24);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_PURPLE));

        JLabel lblTitle = new JLabel("Nueva Entrada de Almacén", icon, SwingConstants.LEFT);
        lblTitle.setFont(ThemeConstants.FONT_TITLE);
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);

        JTextArea lblSubtitle = UIUtils.createWrappingLabel(
                "Registra la factura de compra recibida del proveedor para actualizar stock e inversión a costo",
                ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_SECONDARY);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblSubtitle, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFormEncabezado() {
        RoundedPanel panel = new RoundedPanel(16, ThemeConstants.CARD_BACKGROUND);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Proveedor Nombre + Proveedor NIT + Num Factura
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        panel.add(createFormLabel("Proveedor (Nombre / Razón Social)"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.3;
        panel.add(createFormLabel("Cédula / NIT Proveedor"), gbc);

        gbc.gridx = 2; gbc.weightx = 0.3;
        panel.add(createFormLabel("Número Factura / Nota"), gbc);

        // Fila 1: Campos
        txtProveedorNombre = createStyledTextField("Buscar o ingresar proveedor...");
        setupFieldIcon(txtProveedorNombre, "icons/suppliers.svg");
        txtProveedorNit = createStyledTextField("Ej: 900123456-1");
        setupFieldIcon(txtProveedorNit, "icons/id.svg");
        txtNumFactura = createStyledTextField("Ej: FAC-84920");
        setupFieldIcon(txtNumFactura, "icons/reports.svg");

        // Autocompletado de Proveedor
        new AutocompletePopup<>(
                txtProveedorNombre,
                query -> provCtrl.buscarProveedoresSugeridos(query),
                prov -> prov.getNombre() + " (" + prov.getNit() + ")",
                prov -> {
                    proveedorSeleccionado = prov;
                    txtProveedorNombre.setText(prov.getNombre());
                    txtProveedorNit.setText(prov.getNit());
                }
        );

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(txtProveedorNombre, gbc);

        gbc.gridx = 1;
        panel.add(txtProveedorNit, gbc);

        gbc.gridx = 2;
        panel.add(txtNumFactura, gbc);

        // Fila 2: Agregar Productos (Buscador + Cantidad + Costo + Botón Agregar)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.4;
        panel.add(createFormLabel("Buscar Producto (SKU o Nombre)"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.2;
        panel.add(createFormLabel("Cantidad Recibida"), gbc);

        gbc.gridx = 2; gbc.weightx = 0.4;
        panel.add(createFormLabel("Costo Unitario ($)"), gbc);

        // Fila 3: Inputs de producto
        txtBuscarProducto = createStyledTextField("Escribe SKU o Nombre...");
        setupFieldIcon(txtBuscarProducto, "icons/search.svg");
        txtCantidad = createStyledTextField("Ej: 10");
        setupFieldIcon(txtCantidad, "icons/boxes-stacked.svg");
        txtCostoUnitario = createStyledTextField("Ej: 15000");
        setupFieldIcon(txtCostoUnitario, "icons/wallet.svg");

        // Autocompletado de Producto
        new AutocompletePopup<>(
                txtBuscarProducto,
                query -> prodCtrl.buscarProductosSugeridos(query),
                prod -> prod.getCodigo() + " - " + prod.getProducto() + " (Stock actual: " + prod.getCantidad() + ")",
                prod -> {
                    productoSeleccionado = prod;
                    txtBuscarProducto.setText(prod.getProducto());
                    txtCostoUnitario.setText(String.valueOf(prod.getPrecioCosto()));
                    txtCantidad.requestFocus();
                }
        );

        JPanel pnlProdInput = new JPanel(new BorderLayout(8, 0));
        pnlProdInput.setOpaque(false);
        pnlProdInput.add(txtBuscarProducto, BorderLayout.CENTER);

        JPanel pnlProdDetalle = new JPanel(new GridLayout(1, 3, 8, 0));
        pnlProdDetalle.setOpaque(false);
        pnlProdDetalle.add(txtCantidad);
        pnlProdDetalle.add(txtCostoUnitario);

        NeonButton btnAgregarItem = new NeonButton("Agregar");
        btnAgregarItem.setNeonColor(ThemeConstants.NEON_BLUE);
        btnAgregarItem.setIcon(createIcon("icons/plus.svg", ThemeConstants.NEON_BLUE, 16, 16));
        btnAgregarItem.setIconTextGap(6);
        btnAgregarItem.addActionListener(e -> agregarItemAlCarrito());
        pnlProdDetalle.add(btnAgregarItem);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(pnlProdInput, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(pnlProdDetalle, gbc);

        return panel;
    }

    private void setupFieldIcon(JTextField f, String iconPath) {
        FlatSVGIcon icon = new FlatSVGIcon(iconPath, 16, 16);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.TEXT_SECONDARY));
        JLabel lbl = new JLabel(icon);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        f.putClientProperty(com.formdev.flatlaf.FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, lbl);
    }

    private FlatSVGIcon createIcon(String path, Color color, int w, int h) {
        FlatSVGIcon icon = new FlatSVGIcon(path, w, h);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, color));
        return icon;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ThemeConstants.TEXT_SECONDARY);
        label.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setBackground(ThemeConstants.INPUT_BACKGROUND);
        field.setForeground(ThemeConstants.TEXT_PRIMARY);
        field.setCaretColor(ThemeConstants.TEXT_PRIMARY);
        field.setFont(ThemeConstants.FONT_BODY);
        field.setPreferredSize(new Dimension(0, ThemeConstants.TOUCH_TARGET_MIN));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    private JPanel createCarritoPanel() {
        RoundedPanel panel = new RoundedPanel(16, ThemeConstants.CARD_BACKGROUND);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel headerPnl = new JPanel(new BorderLayout(0, 8));
        headerPnl.setOpaque(false);

        JLabel lblTitle = new JLabel("Productos en la Orden de Compra", createIcon("icons/boxes-stacked.svg", ThemeConstants.NEON_PURPLE, 18, 18), SwingConstants.LEFT);
        lblTitle.setIconTextGap(8);
        lblTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);

        JPanel btnFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnFlow.setOpaque(false);

        NeonButton btnCargarExcel = new NeonButton("Cargar Factura desde Excel");
        btnCargarExcel.setNeonColor(ThemeConstants.NEON_GREEN);
        btnCargarExcel.setIcon(createIcon("icons/excel.svg", ThemeConstants.NEON_GREEN, 16, 16));
        btnCargarExcel.setIconTextGap(6);
        btnCargarExcel.setPreferredSize(new Dimension(230, 34));
        btnCargarExcel.setToolTipText("Carga automáticamente los ítems y cantidades de la factura enviada por tu proveedor");
        btnCargarExcel.addActionListener(e -> cargarItemsDesdeExcel());
        btnFlow.add(btnCargarExcel);

        headerPnl.add(lblTitle, BorderLayout.WEST);
        headerPnl.add(btnFlow, BorderLayout.EAST);

        modelCarrito = new DefaultTableModel(
                new String[]{"Producto", "Código/SKU", "Cantidad", "Costo Unit. ($)", "Subtotal ($)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tbCarrito = new JTable(modelCarrito);
        UIUtils.applyTableStyling(tbCarrito);
        JScrollPane scroll = new JScrollPane(tbCarrito);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(headerPnl, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        lblTotalCompra = new JLabel("Total Compra: $ 0,00");
        lblTotalCompra.setFont(ThemeConstants.FONT_TITLE);
        lblTotalCompra.setForeground(ThemeConstants.NEON_GREEN);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        NeonButton btnCancelar = new NeonButton("Cancelar");
        btnCancelar.setIcon(createIcon("icons/xmark.svg", ThemeConstants.TEXT_SECONDARY, 16, 16));
        btnCancelar.setIconTextGap(6);
        btnCancelar.setPreferredSize(new Dimension(120, 38));
        btnCancelar.addActionListener(e -> dispose());

        NeonButton btnConfirmar = new NeonButton("Confirmar Ingreso a Bodega");
        btnConfirmar.setNeonColor(ThemeConstants.NEON_GREEN);
        btnConfirmar.setIcon(createIcon("icons/check-double.svg", ThemeConstants.NEON_GREEN, 16, 16));
        btnConfirmar.setIconTextGap(6);
        btnConfirmar.setPreferredSize(new Dimension(250, 38));
        btnConfirmar.addActionListener(e -> procesarCompra());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnConfirmar);

        panel.add(lblTotalCompra, BorderLayout.WEST);
        panel.add(btnPanel, BorderLayout.EAST);
        return panel;
    }

    private void agregarItemAlCarrito() {
        String textoBuscado = txtBuscarProducto.getText().trim();
        if (textoBuscado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el nombre o código del producto.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int cant = Integer.parseInt(txtCantidad.getText().trim());
            double costo = Double.parseDouble(txtCostoUnitario.getText().trim().replace(",", "."));

            if (cant <= 0 || costo < 0) {
                JOptionPane.showMessageDialog(this, "La cantidad y el costo deben ser valores mayores a cero.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String prodNombre;
            String prodCodigo;

            if (productoSeleccionado != null) {
                prodNombre = productoSeleccionado.getProducto();
                prodCodigo = productoSeleccionado.getCodigo();
            } else {
                // Producto nuevo digitado por el usuario de forma transparente
                prodNombre = textoBuscado;
                prodCodigo = "NUEVO-" + System.currentTimeMillis() % 100000;
            }

            DetalleCompra det = new DetalleCompra(
                    prodNombre,
                    prodCodigo,
                    cant,
                    costo
            );

            listaCarrito.add(det);
            refrescarTablaCarrito();

            // Limpiar campos de producto
            productoSeleccionado = null;
            txtBuscarProducto.setText("");
            txtCantidad.setText("");
            txtCostoUnitario.setText("");
            txtBuscarProducto.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa valores numéricos válidos para cantidad y costo.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTablaCarrito() {
        modelCarrito.setRowCount(0);
        double total = 0.0;
        for (DetalleCompra d : listaCarrito) {
            modelCarrito.addRow(new Object[]{
                    d.getProducto(),
                    d.getCodigo(),
                    d.getCantidad(),
                    d.getPrecioCosto(),
                    d.getSubtotal()
            });
            total += d.getSubtotal();
        }
        UIUtils.applyTableStyling(tbCarrito);
        lblTotalCompra.setText("Total Compra: " + UIUtils.formatCurrency(total));
    }

    private void procesarCompra() {
        String provNom = txtProveedorNombre.getText().trim();
        String provNit = txtProveedorNit.getText().trim();
        String numFac = txtNumFactura.getText().trim();

        if (provNom.isEmpty() || provNit.isEmpty() || numFac.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa los datos del proveedor y número de factura.", "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (listaCarrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes agregar al menos un producto a la orden de compra.", "Carrito Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Auto-guardar proveedor en la base de datos si no existía
        Proveedor provObj = new Proveedor(provNom, provNit, "", "", "");
        provCtrl.guardarOActualizarProveedor(provObj);

        double total = listaCarrito.stream().mapToDouble(DetalleCompra::getSubtotal).sum();
        String usuarioActivo = (sesion != null && sesion.getUsuarioLogueado() != null) ? sesion.getUsuarioLogueado() : "Administrador";

        Compra compra = new Compra(
                proveedorSeleccionado != null ? proveedorSeleccionado.getId() : null,
                provNom,
                provNit,
                numFac,
                usuarioActivo,
                java.time.LocalDate.now().toString(),
                total
        );

        ResultadoOperacion res = comprasCtrl.guardarEntradaCompra(compra, listaCarrito);
        if (res.esExito()) {
            JOptionPane.showMessageDialog(this, res.getMensaje(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, res.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarItemsDesdeExcel() {
        String provNom = txtProveedorNombre.getText().trim();
        String numFac = txtNumFactura.getText().trim();

        if (provNom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa o selecciona primero el Proveedor (Nombre / Razón Social).", "Proveedor Requerido", JOptionPane.WARNING_MESSAGE);
            txtProveedorNombre.requestFocus();
            return;
        }

        if (numFac.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa el Número de Factura o Remisión de la compra.", "Factura Requerida", JOptionPane.WARNING_MESSAGE);
            txtNumFactura.requestFocus();
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        ImportarProductosDialog dlg = new ImportarProductosDialog(
            owner,
            ImportarProductosDialog.ModoImportacion.ABASTECIMIENTO,
            null,
            items -> {
                if (items != null && !items.isEmpty()) {
                    listaCarrito.addAll(items);
                    refrescarTablaCarrito();
                    JOptionPane.showMessageDialog(this,
                        "¡Se cargaron " + items.size() + " ítems desde el Excel a la Factura N° " + numFac + "!\n" +
                        "Revisa la orden y presiona 'Confirmar Ingreso a Bodega' para registrar la entrada oficialmente.",
                        "Items Cargados a Factura", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        );
        dlg.setVisible(true);
    }
}
