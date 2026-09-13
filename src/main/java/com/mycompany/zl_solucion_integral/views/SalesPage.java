package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.ProductoController;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.controllers.VentasController;
import com.mycompany.zl_solucion_integral.models.Producto;
import com.mycompany.zl_solucion_integral.models.Usuario;
import com.mycompany.zl_solucion_integral.models.Venta;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SalesPage extends JPanel {
    private final ProductoController productoCtrl = new ProductoController();
    private final VentasController ventasCtrl = new VentasController();
    private final UsuarioController usuarioCtrl = new UsuarioController();

    // UI Components
    private JTextField txtSearch, txtQty, txtDiscount;
    private JTextField txtClientName, txtClientCC, txtClientTel, txtClientEmail;
    private JLabel lblClientName, lblClientCC, lblClientTel, lblClientEmail;
    private JComponent txtClientCCHelper;
    private JPanel clientFieldsContainer;
    private JComboBox<String> cbPaymentMethod;
    private JCheckBox chkGenericClient;
    private JTable tbCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;

    private List<Venta> cartItems = new ArrayList<>();
    private Producto selectedProduct = null;
    private JPanel mainGrid;
    private JPanel panelBusqueda, panelCarrito, panelCheckout;

    public SalesPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header (microcopy de contexto)
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Punto de venta", createIcon("icons/sales.svg", ThemeConstants.NEON_GREEN, 24, 24), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea subtitle = UIUtils.createWrappingLabel(
                "Registra ventas ágiles: busca productos, arma el carrito y cobra en segundos",
                ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        // Col 1: Búsqueda de Producto
        panelBusqueda = createProductSearchPanel();
        // Col 2: Carrito Visual
        panelCarrito = createCartPanel();
        // Col 3: Datos de Cliente y Pago
        panelCheckout = createCheckoutPanel();

        mainGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        mainGrid.setOpaque(false);
        mainGrid.add(panelBusqueda);
        mainGrid.add(panelCarrito);
        mainGrid.add(panelCheckout);

        add(mainGrid, BorderLayout.CENTER);

        // Reflow adaptable: en móvil (una columna) los paneles se apilan.
        LayoutResponsive.listen(this, bp -> LayoutResponsive.reflowColumnas(
                mainGrid,
                LayoutResponsive.list(panelBusqueda, panelCarrito, panelCheckout),
                3, 20, bp));

        // Aplicar estado inicial de visibilidad (ocultar campos si Consumidor Final está seleccionado)
        syncClientState();
    }

    private JPanel createProductSearchPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.SIDEBAR_BACKGROUND);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel title = new JLabel("Agregar productos", createIcon("icons/products.svg", ThemeConstants.NEON_PURPLE, 20, 20), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_SUBTITLE);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 16, 0);
        p.add(title, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 4, 0);
        p.add(createLabel("BUSCAR PRODUCTO (CÓDIGO/NOMBRE)"), gbc);
        txtSearch = createTextField("Ej: SKU-001...");
        setupFieldIcon(txtSearch, "icons/products.svg");
        txtSearch.addActionListener(e -> searchProduct());
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 14, 0);
        p.add(txtSearch, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 4, 0);
        p.add(createLabel("CANTIDAD"), gbc);
        txtQty = createTextField("1");
        setupFieldIcon(txtQty, "icons/dashboard.svg");
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 14, 0);
        p.add(txtQty, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 4, 0);
        p.add(createLabel("DESCUENTO %"), gbc);
        txtDiscount = createTextField("0");
        setupFieldIcon(txtDiscount, "icons/reports.svg");
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 20, 0);
        p.add(UIUtils.createFieldWithHelper(txtDiscount, "Deja 0 si no aplicas descuento"), gbc);

        NeonButton btnAdd = new NeonButton("Agregar al carrito");
        btnAdd.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnAdd.setIcon(createIcon("icons/plus.svg", ThemeConstants.NEON_PURPLE, 17, 17));
        btnAdd.setIconTextGap(8);
        btnAdd.setPreferredSize(new Dimension(0, 46));
        btnAdd.addActionListener(e -> addToCart());
        btnAdd.setToolTipText("Agrega el producto buscado al carrito con la cantidad y el descuento indicados");
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 0, 0);
        p.add(btnAdd, gbc);

        return p;
    }

    private JPanel createCartPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout(0, 16));
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Carrito de compras", createIcon("icons/cart-shopping.svg", ThemeConstants.NEON_CYAN, 20, 20), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_SUBTITLE);
        p.add(title, BorderLayout.NORTH);

        cartModel = new DefaultTableModel(new String[]{"Producto", "Cant.", "Total"}, 0);
        tbCart = new JTable(cartModel);
        tbCart.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbCart.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbCart.setRowHeight(ThemeConstants.TABLE_ROW_HEIGHT);
        tbCart.setFont(ThemeConstants.FONT_SMALL);
        tbCart.setShowGrid(false);
        tbCart.setFillsViewportHeight(true);
        tbCart.setSelectionBackground(new Color(6, 182, 212, 70));
        tbCart.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);

        tbCart.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return component;
            }
        });

        JTableHeader cartHeader = tbCart.getTableHeader();
        cartHeader.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        cartHeader.setForeground(ThemeConstants.TEXT_SECONDARY);
        cartHeader.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        cartHeader.setPreferredSize(new Dimension(0, 34));
        cartHeader.setReorderingAllowed(false);
        
        JScrollPane scroll = new JScrollPane(tbCart);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        p.add(scroll, BorderLayout.CENTER);

        lblTotal = new JLabel("TOTAL: $ 0.00", SwingConstants.RIGHT);
        lblTotal.setForeground(ThemeConstants.NEON_GREEN);
        lblTotal.setFont(ThemeConstants.FONT_TITLE);
        p.add(lblTotal, BorderLayout.SOUTH);

        return p;
    }

    private JPanel createCheckoutPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.SIDEBAR_BACKGROUND);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel title = new JLabel("Método de pago y cliente", createIcon("icons/clients.svg", ThemeConstants.NEON_GREEN, 20, 20), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_SUBTITLE);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 16, 0);
        p.add(title, gbc);

        // 1. Selector de Método de Pago Prominente arriba
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 4, 0);
        p.add(createLabel("MÉTODO DE PAGO"), gbc);

        cbPaymentMethod = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Crédito", "Otro"});
        cbPaymentMethod.setBackground(ThemeConstants.INPUT_BACKGROUND);
        cbPaymentMethod.setForeground(ThemeConstants.TEXT_PRIMARY);
        cbPaymentMethod.addActionListener(e -> syncClientState());
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 14, 0);
        p.add(cbPaymentMethod, gbc);

        // Checkbox Venta Rápida / Consumidor Final
        chkGenericClient = new JCheckBox("Venta a Consumidor Final (Sin datos)");
        chkGenericClient.setSelected(true);
        chkGenericClient.setOpaque(false);
        chkGenericClient.setForeground(ThemeConstants.TEXT_SECONDARY);
        chkGenericClient.setFont(ThemeConstants.FONT_SMALL);
        chkGenericClient.addActionListener(e -> syncClientState());
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 14, 0);
        p.add(chkGenericClient, gbc);

        // Panel contenedor de campos de cliente (para ocultar/mostrar limpiamente)
        clientFieldsContainer = new JPanel(new GridBagLayout());
        clientFieldsContainer.setOpaque(false);
        GridBagConstraints subGbc = new GridBagConstraints();
        subGbc.fill = GridBagConstraints.HORIZONTAL;
        subGbc.weightx = 1.0; subGbc.gridx = 0;

        subGbc.gridy = 0; subGbc.insets = new Insets(0, 0, 4, 0);
        lblClientCC = createLabel("CÉDULA / NIT (ENTER PARA BUSCAR)");
        clientFieldsContainer.add(lblClientCC, subGbc);

        txtClientCC = createTextField("Ej: 1098765432...");
        setupFieldIcon(txtClientCC, "icons/id.svg");
        txtClientCC.addActionListener(e -> searchClientByCC());
        txtClientCC.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                searchClientByCC();
            }
        });
        subGbc.gridy = 1; subGbc.insets = new Insets(0, 0, 10, 0);
        txtClientCCHelper = UIUtils.createFieldWithHelper(txtClientCC, "Presiona Enter o cambia de campo para autocompletar");
        clientFieldsContainer.add(txtClientCCHelper, subGbc);

        subGbc.gridy = 2; subGbc.insets = new Insets(0, 0, 4, 0);
        lblClientName = createLabel("NOMBRE DEL CLIENTE / RAZÓN SOCIAL");
        clientFieldsContainer.add(lblClientName, subGbc);

        txtClientName = createTextField("Nombre o negocio");
        setupFieldIcon(txtClientName, "icons/user.svg");
        subGbc.gridy = 3; subGbc.insets = new Insets(0, 0, 10, 0);
        clientFieldsContainer.add(txtClientName, subGbc);

        subGbc.gridy = 4; subGbc.insets = new Insets(0, 0, 4, 0);
        lblClientTel = createLabel("TELÉFONO (OPCIONAL)");
        clientFieldsContainer.add(lblClientTel, subGbc);

        txtClientTel = createTextField("Contacto");
        setupFieldIcon(txtClientTel, "icons/phone.svg");
        subGbc.gridy = 5; subGbc.insets = new Insets(0, 0, 10, 0);
        clientFieldsContainer.add(txtClientTel, subGbc);

        subGbc.gridy = 6; subGbc.insets = new Insets(0, 0, 4, 0);
        lblClientEmail = createLabel("CORREO ELECTRÓNICO (OPCIONAL)");
        clientFieldsContainer.add(lblClientEmail, subGbc);

        txtClientEmail = createTextField("cliente@correo.com");
        setupFieldIcon(txtClientEmail, "icons/email.svg");
        subGbc.gridy = 7; subGbc.insets = new Insets(0, 0, 14, 0);
        clientFieldsContainer.add(txtClientEmail, subGbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 0, 0);
        p.add(clientFieldsContainer, gbc);

        NeonButton btnConfirm = new NeonButton("Confirmar venta");
        btnConfirm.setNeonColor(ThemeConstants.NEON_GREEN);
        btnConfirm.setIcon(createIcon("icons/check-double.svg", ThemeConstants.NEON_GREEN, 17, 17));
        btnConfirm.setIconTextGap(8);
        btnConfirm.setPreferredSize(new Dimension(0, 48));
        btnConfirm.addActionListener(e -> finishSale());
        btnConfirm.setToolTipText("Procesa el cobro, guarda la venta y descuenta el stock");
        gbc.gridy = 5; gbc.insets = new Insets(10, 0, 0, 0);
        p.add(btnConfirm, gbc);

        // Inicializar estado por defecto (Efectivo / Consumidor Final)
        SwingUtilities.invokeLater(this::syncClientState);

        return p;
    }

    private void searchProduct() {
        String query = txtSearch.getText().trim();
        selectedProduct = productoCtrl.buscarProductoPorCodigo(query);
        if (selectedProduct == null) {
            selectedProduct = productoCtrl.buscarProductoPorNombre(query);
        }
        
        if (selectedProduct != null) {
            txtSearch.setText(selectedProduct.getProducto());
            txtSearch.setForeground(ThemeConstants.NEON_CYAN);
        } else {
            UIUtils.showError(this, "Producto no encontrado.");
        }
    }

    private void searchClientByCC() {
        if (chkGenericClient != null && chkGenericClient.isSelected()) {
            return;
        }
        String cc = txtClientCC.getText().trim();
        if (cc.isEmpty()) {
            return;
        }
        Usuario clientFound = usuarioCtrl.buscarClientePorCC(cc);
        if (clientFound != null) {
            txtClientName.setText(clientFound.getNombre() != null ? clientFound.getNombre() : "");
            txtClientTel.setText(clientFound.getTelefono() != null ? clientFound.getTelefono() : "");
            txtClientEmail.setText(clientFound.getEmail() != null ? clientFound.getEmail() : "");
            txtClientCC.setForeground(ThemeConstants.NEON_GREEN);
        } else {
            txtClientCC.setForeground(ThemeConstants.TEXT_PRIMARY);
        }
    }

    private void addToCart() {
        if (selectedProduct == null) {
            UIUtils.showError(this, "Primero busque un producto.");
            return;
        }

        String qtyText = txtQty.getText().trim();
        String discountText = txtDiscount.getText().trim();

        if (!com.mycompany.zl_solucion_integral.config.Validaciones.validarCantidad(qtyText)) {
            UIUtils.showError(this, "La cantidad debe ser un número entero positivo.");
            return;
        }
        if (!com.mycompany.zl_solucion_integral.config.Validaciones.validarDescuento(discountText)) {
            UIUtils.showError(this, "El descuento debe ser un porcentaje entre 0 y 100.");
            return;
        }

        int qty = Integer.parseInt(qtyText);
        double discount = discountText.isEmpty() ? 0.0 : Double.parseDouble(discountText);

        if (qty > selectedProduct.getCantidad()) {
            UIUtils.showError(this, "La cantidad solicitada supera el stock disponible (" + selectedProduct.getCantidad() + ").");
            return;
        }

        double subtotal = selectedProduct.getPrecio() * qty;
        double total = subtotal - (subtotal * discount / 100.0);

        Venta v = new Venta();
        v.setProducto(selectedProduct);
        v.setCantidad(qty);
        v.setTotal(total);
        v.setFecha(LocalDate.now());

        cartItems.add(v);
        cartModel.addRow(new Object[]{selectedProduct.getProducto(), qty, String.format("$ %.2f", total)});
        updateTotal();

        // Reset
        selectedProduct = null;
        txtSearch.setText("");
        txtSearch.setForeground(ThemeConstants.TEXT_PRIMARY);
        txtQty.setText("1");
        txtDiscount.setText("0");
    }

    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(Venta::getTotal).sum();
        lblTotal.setText(String.format("TOTAL: $ %.2f", total));
    }

    private void syncClientState() {
        if (cbPaymentMethod == null || chkGenericClient == null) return;

        String metodo = (String) cbPaymentMethod.getSelectedItem();
        boolean requiereCliente = "Crédito".equals(metodo) || "Transferencia".equals(metodo);

        if (requiereCliente) {
            // En Crédito y Transferencia: Se desactiva y deshabilita la opción de Consumidor Final
            chkGenericClient.setSelected(false);
            chkGenericClient.setEnabled(false);
        } else {
            // En Efectivo u Otro: Opción habilitada para el vendedor
            chkGenericClient.setEnabled(true);
        }

        boolean esConsumidorFinal = chkGenericClient.isSelected();

        // Ocultamiento/Visibilidad limpia del contenedor de campos
        if (clientFieldsContainer != null) {
            clientFieldsContainer.setVisible(!esConsumidorFinal);
        }

        // Estado de los componentes de entrada
        txtClientName.setEnabled(!esConsumidorFinal);
        txtClientCC.setEnabled(!esConsumidorFinal);
        txtClientTel.setEnabled(!esConsumidorFinal);
        txtClientEmail.setEnabled(!esConsumidorFinal);

        if (esConsumidorFinal) {
            txtClientName.setText("CONSUMIDOR FINAL");
            txtClientCC.setText("N/A");
            txtClientTel.setText("N/A");
            txtClientEmail.setText("N/A");
            txtClientCC.setForeground(ThemeConstants.TEXT_PRIMARY);
        } else {
            if ("CONSUMIDOR FINAL".equals(txtClientName.getText().trim())) {
                txtClientName.setText("");
            }
            if ("N/A".equals(txtClientCC.getText().trim())) {
                txtClientCC.setText("");
            }
            if ("N/A".equals(txtClientTel.getText().trim())) {
                txtClientTel.setText("");
            }
            if ("N/A".equals(txtClientEmail.getText().trim())) {
                txtClientEmail.setText("");
            }
        }

        if (panelCheckout != null) {
            panelCheckout.revalidate();
            panelCheckout.repaint();
        }
    }

    private void finishSale() {
        if (cartItems.isEmpty()) {
            UIUtils.showError(this, "El carrito está vacío.");
            return;
        }

        String metodoPago = (String) cbPaymentMethod.getSelectedItem();
        boolean genericClient = chkGenericClient.isSelected();

        // Validaciones específicas por método de pago
        if (("Crédito".equals(metodoPago) || "Transferencia".equals(metodoPago)) && genericClient) {
            UIUtils.showError(this, "Las ventas por " + metodoPago + " requieren obligatoriamente los datos del cliente.");
            return;
        }

        String clientName = genericClient ? "CONSUMIDOR FINAL" : txtClientName.getText().trim();
        String clientCC = genericClient ? "N/A" : txtClientCC.getText().trim();
        String clientTel = genericClient ? "N/A" : txtClientTel.getText().trim();
        String clientEmail = genericClient ? "N/A" : txtClientEmail.getText().trim();

        if (!genericClient) {
            if (clientCC.isEmpty() && clientName.isEmpty()) {
                UIUtils.showError(this, "Ingresa al menos la Cédula/NIT o el Nombre del cliente.");
                return;
            }
            if (clientName.isEmpty()) {
                clientName = "Cliente (" + clientCC + ")";
            }
            if (clientCC.isEmpty()) {
                clientCC = "S/D";
            }
        }

        List<Producto> productsToSave = new ArrayList<>();
        double currentGrandTotal = 0;
        for (Venta item : cartItems) {
            Producto p = item.getProducto();
            p.setCantidadSolicitada(item.getCantidad());
            productsToSave.add(p);
            currentGrandTotal += item.getTotal();
        }

        Venta finalVenta = new Venta();
        finalVenta.setCliente(new Usuario());
        finalVenta.getCliente().setNombre(clientName);
        finalVenta.getCliente().setNoCc(clientCC);
        finalVenta.setVendedor(com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado());
        finalVenta.setFecha(LocalDate.now());
        finalVenta.setTotal(currentGrandTotal);
        finalVenta.setMetodoPago(metodoPago);

        com.mycompany.zl_solucion_integral.config.ResultadoOperacion res = ventasCtrl.guardarVenta(finalVenta, productsToSave, new JTable());

        if (res.esExito()) {
            if (!genericClient) {
                Usuario client = finalVenta.getCliente();
                client.setTelefono(clientTel);
                client.setEmail(clientEmail.isEmpty() ? clientCC + "@simplify.biz" : clientEmail);
                client.setRol("2");
                client.setContraseña(clientCC);

                if (!usuarioCtrl.validarExistenciaUsuario(clientName)) {
                    usuarioCtrl.agregarUsuario(client);
                }
            }

            UIUtils.showSuccess(this, res.getMensaje());
            clearAll();
        } else {
            UIUtils.showError(this, res.getMensaje());
        }
    }

    private void clearAll() {
        cartItems.clear();
        cartModel.setRowCount(0);
        updateTotal();
        cbPaymentMethod.setSelectedIndex(0); // Efectivo por defecto
        chkGenericClient.setSelected(true);
        syncClientState();
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
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
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
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
