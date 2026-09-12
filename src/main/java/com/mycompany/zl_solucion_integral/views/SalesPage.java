package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.ProductoController;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.controllers.VentasController;
import com.mycompany.zl_solucion_integral.models.Producto;
import com.mycompany.zl_solucion_integral.models.Usuario;
import com.mycompany.zl_solucion_integral.models.Venta;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
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
    private JComboBox<String> cbPaymentMethod;
    private JCheckBox chkGenericClient;
    private JTable tbCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    
    private List<Venta> cartItems = new ArrayList<>();
    private Producto selectedProduct = null;

    public SalesPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel title = new JLabel("Punto de venta", createIcon("icons/sales.svg", ThemeConstants.NEON_GREEN, 24, 24), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        add(title, BorderLayout.NORTH);

        JPanel mainGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        mainGrid.setOpaque(false);

        // Col 1: Búsqueda de Producto
        mainGrid.add(createProductSearchPanel());

        // Col 2: Carrito Visual
        mainGrid.add(createCartPanel());

        // Col 3: Datos de Cliente y Pago
        mainGrid.add(createCheckoutPanel());

        add(mainGrid, BorderLayout.CENTER);
    }

    private JPanel createProductSearchPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.SIDEBAR_BACKGROUND);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.gridx = 0; gbc.insets = new Insets(0,0,10,0);

        gbc.gridy = 0; p.add(createLabel("BUSCAR PRODUCTO (CÓDIGO/NOMBRE)"), gbc);
        txtSearch = createTextField("Ej: SKU-001...");
        setupFieldIcon(txtSearch, "icons/products.svg");
        txtSearch.addActionListener(e -> searchProduct());
        gbc.gridy = 1; p.add(txtSearch, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(20,0,5,0);
        p.add(createLabel("CANTIDAD"), gbc);
        txtQty = createTextField("1");
        setupFieldIcon(txtQty, "icons/dashboard.svg");
        gbc.gridy = 3; gbc.insets = new Insets(0,0,10,0);
        p.add(txtQty, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(10,0,5,0);
        p.add(createLabel("DESCUENTO %"), gbc);
        txtDiscount = createTextField("0");
        setupFieldIcon(txtDiscount, "icons/reports.svg");
        gbc.gridy = 5; gbc.insets = new Insets(0,0,30,0);
        p.add(txtDiscount, gbc);

        NeonButton btnAdd = new NeonButton("Agregar al carrito");
        btnAdd.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnAdd.setIcon(createIcon("icons/sales.svg", ThemeConstants.NEON_PURPLE, 17, 17));
        btnAdd.setIconTextGap(8);
        btnAdd.addActionListener(e -> addToCart());
        gbc.gridy = 6; p.add(btnAdd, gbc);

        return p;
    }

    private JPanel createCartPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Carrito de compras", createIcon("icons/sales.svg", ThemeConstants.NEON_CYAN, 20, 20), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_SUBTITLE);
        p.add(title, BorderLayout.NORTH);

        cartModel = new DefaultTableModel(new String[]{"Producto", "Cant.", "Total"}, 0);
        tbCart = new JTable(cartModel);
        tbCart.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbCart.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbCart.setRowHeight(30);
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
        cartHeader.setPreferredSize(new Dimension(0, 32));
        cartHeader.setReorderingAllowed(false);
        
        JScrollPane scroll = new JScrollPane(tbCart);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
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
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.gridx = 0; gbc.insets = new Insets(0,0,10,0);

        gbc.gridy = 0; p.add(createLabel("NOMBRE DEL CLIENTE"), gbc);
        txtClientName = createTextField("Nombre completo");
        setupFieldIcon(txtClientName, "icons/user.svg");
        gbc.gridy = 1; p.add(txtClientName, gbc);

        gbc.gridy = 2; p.add(createLabel("CÉDULA / NIT"), gbc);
        txtClientCC = createTextField("Documento");
        setupFieldIcon(txtClientCC, "icons/reports.svg");
        gbc.gridy = 3; p.add(txtClientCC, gbc);

        gbc.gridy = 4; p.add(createLabel("TELÉFONO"), gbc);
        txtClientTel = createTextField("Contacto");
        setupFieldIcon(txtClientTel, "icons/user.svg");
        gbc.gridy = 5; p.add(txtClientTel, gbc);

        gbc.gridy = 6; p.add(createLabel("CORREO ELECTRÓNICO"), gbc);
        txtClientEmail = createTextField("cliente@correo.com");
        setupFieldIcon(txtClientEmail, "icons/settings.svg");
        gbc.gridy = 7; p.add(txtClientEmail, gbc);

        gbc.gridy = 8; p.add(createLabel("MÉTODO DE PAGO"), gbc);
        cbPaymentMethod = new JComboBox<>(new String[]{"Efectivo", "Crédito", "Transferencia", "Otro"});
        cbPaymentMethod.setBackground(ThemeConstants.INPUT_BACKGROUND);
        cbPaymentMethod.setForeground(ThemeConstants.TEXT_PRIMARY);
        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 10, 0);
        p.add(cbPaymentMethod, gbc);

        chkGenericClient = new JCheckBox("Cliente no desea suministrar datos");
        chkGenericClient.setOpaque(false);
        chkGenericClient.setForeground(ThemeConstants.TEXT_SECONDARY);
        chkGenericClient.setFont(ThemeConstants.FONT_SMALL);
        chkGenericClient.addActionListener(e -> toggleGenericClient());
        gbc.gridy = 10; gbc.insets = new Insets(12, 0, 10, 0);
        p.add(chkGenericClient, gbc);

        gbc.gridy = 11; gbc.insets = new Insets(20, 0, 10, 0);
        NeonButton btnConfirm = new NeonButton("Confirmar venta");
        btnConfirm.setNeonColor(ThemeConstants.NEON_GREEN);
        btnConfirm.setIcon(createIcon("icons/sales.svg", ThemeConstants.NEON_GREEN, 17, 17));
        btnConfirm.setIconTextGap(8);
        btnConfirm.addActionListener(e -> finishSale());
        p.add(btnConfirm, gbc);

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
            JOptionPane.showMessageDialog(this, "Producto no encontrado.");
        }
    }

    private void addToCart() {
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Primero busque un producto.");
            return;
        }
        try {
            int qty = Integer.parseInt(txtQty.getText());
            double discount = Double.parseDouble(txtDiscount.getText());
            
            double subtotal = selectedProduct.getPrecio() * qty;
            double total = subtotal - (subtotal * discount / 100);
            
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datos numéricos inválidos.");
        }
    }

    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(Venta::getTotal).sum();
        lblTotal.setText(String.format("TOTAL: $ %.2f", total));
    }

    private void finishSale() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            return;
        }

        boolean genericClient = chkGenericClient.isSelected();
        String clientName = genericClient ? "CONSUMIDOR FINAL" : txtClientName.getText().trim();
        String clientCC = genericClient ? "N/A" : txtClientCC.getText().trim();
        String clientTel = genericClient ? "N/A" : txtClientTel.getText().trim();
        String clientEmail = genericClient ? "N/A" : txtClientEmail.getText().trim();

        if (!genericClient && (clientName.isEmpty() || clientCC.isEmpty())) {
            JOptionPane.showMessageDialog(this, "Nombre y Cédula del cliente son obligatorios.");
            return;
        }

        try {
            // 1. Preparar Cliente
            Usuario client = new Usuario();
            client.setNombre(clientName);
            client.setNoCc(clientCC);
            client.setTelefono(clientTel);
            client.setEmail(clientEmail.isEmpty() ? clientCC + "@simplify.biz" : clientEmail);

            // 2. Preparar Lista de Productos Vendidos
            List<Producto> productsToSave = new ArrayList<>();
            double currentGrandTotal = 0;
            for (Venta item : cartItems) {
                Producto p = item.getProducto();
                p.setCantidadSolicitada(item.getCantidad());
                productsToSave.add(p);
                currentGrandTotal += item.getTotal();
            }

            // 3. Preparar Venta Header
            Venta finalVenta = new Venta();
            finalVenta.setCliente(client);
            finalVenta.setVendedor(com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado());
            finalVenta.setFecha(LocalDate.now());
            finalVenta.setTotal(currentGrandTotal);
            finalVenta.setMetodoPago((String) cbPaymentMethod.getSelectedItem()); 

            // 4. Llamar al Controller para persistir en SQLite
            ventasCtrl.guardarVenta(finalVenta, productsToSave, new JTable()); 
            
            // 5. Registrar el cliente solo cuando entregó sus datos.
            if (!genericClient) {
                client.setRol("2");
                client.setContraseña(clientCC);

                if (!usuarioCtrl.validarExistenciaUsuario(clientName)) {
                    usuarioCtrl.agregarUsuario(client);
                }
            }

            JOptionPane.showMessageDialog(this, "¡Venta procesada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearAll();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al procesar la venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearAll() {
        cartItems.clear();
        cartModel.setRowCount(0);
        updateTotal();
        txtClientName.setText("");
        txtClientCC.setText("");
        txtClientTel.setText("");
        txtClientEmail.setText("");
        chkGenericClient.setSelected(false);
        toggleGenericClient();
    }

    private void toggleGenericClient() {
        boolean genericClient = chkGenericClient.isSelected();
        txtClientName.setEnabled(!genericClient);
        txtClientCC.setEnabled(!genericClient);
        txtClientTel.setEnabled(!genericClient);
        txtClientEmail.setEnabled(!genericClient);

        if (genericClient) {
            txtClientName.setText("CONSUMIDOR FINAL");
            txtClientCC.setText("N/A");
            txtClientTel.setText("N/A");
            txtClientEmail.setText("N/A");
        } else {
            txtClientName.setText("");
            txtClientCC.setText("");
            txtClientTel.setText("");
            txtClientEmail.setText("");
        }
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
