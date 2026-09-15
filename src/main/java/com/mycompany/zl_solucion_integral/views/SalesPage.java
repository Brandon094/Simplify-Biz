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
    private JTextField txtCashGiven;
    private JLabel lblClientName, lblClientCC, lblClientTel, lblClientEmail;
    private JComponent txtClientCCHelper;
    private JPanel clientFieldsContainer;
    private JComboBox<String> cbPaymentMethod;
    private JCheckBox chkGenericClient;
    private JTable tbCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private JLabel lblSubtotal;
    private JLabel lblDiscountSavings;
    private JLabel lblItemCount;
    private JLabel lblChangeDue;
    private JPanel panelChangeCalculation;
    private JPanel centerCardPanel;
    private JPanel emptyCartState;

    private List<Venta> cartItems = new ArrayList<>();
    private Producto selectedProduct = null;
    private JPanel mainGrid;
    private JPanel panelBusqueda, panelCarrito, panelCheckout;

    public SalesPage() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        // Col 1: Búsqueda de Producto
        panelBusqueda = createProductSearchPanel();
        // Col 2: Carrito Visual
        panelCarrito = createCartPanel();
        // Col 3: Datos de Cliente y Pago
        panelCheckout = createCheckoutPanel();

        // Envolver panelCheckout en ScrollPane sin bordes para evitar desbordamiento vertical de botones
        JScrollPane checkoutScroll = new JScrollPane(panelCheckout);
        checkoutScroll.setOpaque(false);
        checkoutScroll.getViewport().setOpaque(false);
        checkoutScroll.setBorder(BorderFactory.createEmptyBorder());
        checkoutScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Grid proporciones: Búsqueda (28%), Carrito Ampliado (44%), Checkout (28%)
        mainGrid = new JPanel(new GridBagLayout());
        mainGrid.setOpaque(false);
        GridBagConstraints gbcGrid = new GridBagConstraints();
        gbcGrid.fill = GridBagConstraints.BOTH;
        gbcGrid.gridy = 0; gbcGrid.weighty = 1.0;

        gbcGrid.gridx = 0; gbcGrid.weightx = 0.28;
        gbcGrid.insets = new Insets(0, 0, 0, 10);
        mainGrid.add(panelBusqueda, gbcGrid);

        gbcGrid.gridx = 1; gbcGrid.weightx = 0.44;
        gbcGrid.insets = new Insets(0, 0, 0, 10);
        mainGrid.add(panelCarrito, gbcGrid);

        gbcGrid.gridx = 2; gbcGrid.weightx = 0.28;
        gbcGrid.insets = new Insets(0, 0, 0, 0);
        mainGrid.add(checkoutScroll, gbcGrid);

        add(mainGrid, BorderLayout.CENTER);

        // Reflow adaptable: en móvil (una columna) los paneles se apilan.
        LayoutResponsive.listen(this, bp -> LayoutResponsive.reflowColumnas(
                mainGrid,
                LayoutResponsive.list(panelBusqueda, panelCarrito, checkoutScroll),
                3, 15, bp));

        // Configurar autocompletados una vez que todos los componentes UI fueron instanciados
        setupAutocompletes();

        // Aplicar estado inicial de visibilidad
        SwingUtilities.invokeLater(this::syncClientState);
    }

    private JPanel createProductSearchPanel() {
        RoundedPanel p = new RoundedPanel(16, ThemeConstants.SIDEBAR_BACKGROUND);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel title = new JLabel("Agregar productos", createIcon("icons/products.svg", ThemeConstants.NEON_PURPLE, 18, 18), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_SUBTITLE);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 10, 0);
        p.add(title, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 2, 0);
        p.add(createLabel("BUSCAR PRODUCTO (CÓDIGO/NOMBRE)"), gbc);
        txtSearch = createTextField("Ej: SKU-001...");
        setupFieldIcon(txtSearch, "icons/products.svg");
        txtSearch.addActionListener(e -> searchProduct());
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        p.add(txtSearch, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 2, 0);
        p.add(createLabel("CANTIDAD"), gbc);
        txtQty = createTextField("1");
        setupFieldIcon(txtQty, "icons/dashboard.svg");
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 8, 0);
        p.add(txtQty, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 2, 0);
        p.add(createLabel("DESCUENTO %"), gbc);
        txtDiscount = createTextField("0");
        setupFieldIcon(txtDiscount, "icons/reports.svg");
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 12, 0);
        p.add(UIUtils.createFieldWithHelper(txtDiscount, "Deja 0 si no aplicas descuento"), gbc);

        NeonButton btnAdd = new NeonButton("Agregar al carrito");
        btnAdd.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnAdd.setIcon(createIcon("icons/plus.svg", ThemeConstants.NEON_PURPLE, 15, 15));
        btnAdd.setIconTextGap(6);
        btnAdd.setPreferredSize(new Dimension(0, 40));
        btnAdd.addActionListener(e -> addToCart());
        btnAdd.setToolTipText("Agrega el producto buscado al carrito con la cantidad y el descuento indicados");
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 0, 0);
        p.add(btnAdd, gbc);

        return p;
    }

    private JPanel createCartPanel() {
        RoundedPanel p = new RoundedPanel(16, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout(0, 10));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        JLabel title = new JLabel("Carrito de compras", createIcon("icons/cart-shopping.svg", ThemeConstants.NEON_CYAN, 18, 18), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_SUBTITLE);
        
        lblItemCount = new JLabel("0 ítems ");
        lblItemCount.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblItemCount.setFont(ThemeConstants.FONT_SMALL);

        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(lblItemCount, BorderLayout.EAST);
        p.add(headerRow, BorderLayout.NORTH);

        cartModel = new DefaultTableModel(new String[]{"Producto", "Cant.", "Total", "Acciones"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Solo la columna de acciones es interactiva
            }
        };

        tbCart = new JTable(cartModel);
        tbCart.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbCart.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbCart.setRowHeight(38);
        tbCart.setFont(ThemeConstants.FONT_SMALL);
        tbCart.setShowGrid(false);
        tbCart.setFillsViewportHeight(true);
        tbCart.setSelectionBackground(ThemeConstants.withAlpha(ThemeConstants.NEON_CYAN, 50));
        tbCart.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);

        // Anchos de columna optimizados
        tbCart.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbCart.getColumnModel().getColumn(1).setPreferredWidth(35);
        tbCart.getColumnModel().getColumn(2).setPreferredWidth(75);
        tbCart.getColumnModel().getColumn(3).setPreferredWidth(115);

        // Cell renderer para columnas 0, 1 y 2
        DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    component.setBackground(row % 2 == 0
                            ? ThemeConstants.CARD_BACKGROUND
                            : ThemeConstants.TABLE_ZEBRA);
                    component.setForeground(ThemeConstants.TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return component;
            }
        };
        tbCart.getColumnModel().getColumn(0).setCellRenderer(textRenderer);
        tbCart.getColumnModel().getColumn(1).setCellRenderer(textRenderer);
        tbCart.getColumnModel().getColumn(2).setCellRenderer(textRenderer);

        // Renderer y Editor para la columna 3 (Botones SVG por fila: + / - / Edit / Trash)
        CartRowActionsPanel rowActionsRenderer = new CartRowActionsPanel();

        tbCart.getColumnModel().getColumn(3).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            rowActionsRenderer.updateBackground(isSelected, row % 2 == 0);
            return rowActionsRenderer;
        });

        tbCart.getColumnModel().getColumn(3).setCellEditor(new CartCellEditor());

        JTableHeader cartHeader = tbCart.getTableHeader();
        cartHeader.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        cartHeader.setForeground(ThemeConstants.TEXT_SECONDARY);
        cartHeader.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        cartHeader.setPreferredSize(new Dimension(0, 30));
        cartHeader.setReorderingAllowed(false);
        
        JScrollPane scroll = new JScrollPane(tbCart);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        // Panel de Estado Vacío Animado/Grafico
        emptyCartState = new JPanel(new GridBagLayout());
        emptyCartState.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        
        JLabel lblEmptyIcon = new JLabel(createIcon("icons/cart-shopping.svg", ThemeConstants.withAlpha(ThemeConstants.TEXT_SECONDARY, 80), 54, 54));
        emptyCartState.add(lblEmptyIcon, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(12, 0, 4, 0);
        JLabel lblEmptyTitle = new JLabel("Tu carrito está vacío");
        lblEmptyTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblEmptyTitle.setFont(ThemeConstants.FONT_SUBTITLE.deriveFont(Font.BOLD, 15f));
        emptyCartState.add(lblEmptyTitle, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        JLabel lblEmptySub = new JLabel("<html><body style='width: 200px; text-align: center;'>Busca productos a la izquierda o digita su código SKU para armar la orden.</body></html>");
        lblEmptySub.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblEmptySub.setFont(ThemeConstants.FONT_SMALL);
        lblEmptySub.setHorizontalAlignment(SwingConstants.CENTER);
        emptyCartState.add(lblEmptySub, gbc);

        // CardLayout central para alternar entre Estado Vacío y Tabla de Carrito
        centerCardPanel = new JPanel(new CardLayout());
        centerCardPanel.setOpaque(false);
        centerCardPanel.add(emptyCartState, "EMPTY");
        centerCardPanel.add(scroll, "CART");

        p.add(centerCardPanel, BorderLayout.CENTER);

        // Footer con Gran Total de Venta, Subtotal y Desglose de Descuentos
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));

        JSeparator lineSep = new JSeparator(JSeparator.HORIZONTAL);
        lineSep.setForeground(ThemeConstants.INPUT_BORDER);
        lineSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JPanel breakdownPanel = new JPanel(new GridLayout(2, 2, 6, 2));
        breakdownPanel.setOpaque(false);
        breakdownPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 2, 6));

        JLabel lblSubtotalTag = new JLabel("Subtotal sin desc:");
        lblSubtotalTag.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblSubtotalTag.setFont(ThemeConstants.FONT_SMALL);

        lblSubtotal = new JLabel("$ 0.00", SwingConstants.RIGHT);
        lblSubtotal.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblSubtotal.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));

        JLabel lblDiscountTag = new JLabel("Ahorro / Descuento:");
        lblDiscountTag.setForeground(ThemeConstants.NEON_CYAN);
        lblDiscountTag.setFont(ThemeConstants.FONT_SMALL);

        lblDiscountSavings = new JLabel("-$ 0.00 (0%)", SwingConstants.RIGHT);
        lblDiscountSavings.setForeground(ThemeConstants.NEON_CYAN);
        lblDiscountSavings.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));

        breakdownPanel.add(lblSubtotalTag);
        breakdownPanel.add(lblSubtotal);
        breakdownPanel.add(lblDiscountTag);
        breakdownPanel.add(lblDiscountSavings);

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6));

        lblTotal = new JLabel("TOTAL: $ 0.00", SwingConstants.RIGHT);
        lblTotal.setForeground(ThemeConstants.NEON_GREEN);
        lblTotal.setFont(ThemeConstants.FONT_TITLE.deriveFont(Font.BOLD, 22f));
        
        totalRow.add(lblTotal, BorderLayout.CENTER);

        // Panel de Cálculo de Vueltas / Cambio (Monto entregado por el tendero)
        panelChangeCalculation = new JPanel(new GridLayout(2, 2, 6, 4));
        panelChangeCalculation.setOpaque(false);
        panelChangeCalculation.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConstants.INPUT_BORDER),
                BorderFactory.createEmptyBorder(6, 6, 4, 6)
        ));

        JLabel lblCashGivenTag = new JLabel("Paga con ($):");
        lblCashGivenTag.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblCashGivenTag.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));

        txtCashGiven = createTextField("Ej: 50000");
        txtCashGiven.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        setupFieldIcon(txtCashGiven, "icons/wallet.svg");
        txtCashGiven.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateChangeCalculation(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateChangeCalculation(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateChangeCalculation(); }
        });

        JLabel lblChangeDueTag = new JLabel("Vueltas:");
        lblChangeDueTag.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblChangeDueTag.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD));

        lblChangeDue = new JLabel("$ 0.00", SwingConstants.RIGHT);
        lblChangeDue.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblChangeDue.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD, 15f));

        panelChangeCalculation.add(lblCashGivenTag);
        panelChangeCalculation.add(txtCashGiven);
        panelChangeCalculation.add(lblChangeDueTag);
        panelChangeCalculation.add(lblChangeDue);

        footerPanel.add(Box.createVerticalStrut(4));
        footerPanel.add(lineSep);
        footerPanel.add(breakdownPanel);
        footerPanel.add(Box.createVerticalStrut(2));
        footerPanel.add(totalRow);
        footerPanel.add(panelChangeCalculation);

        p.add(footerPanel, BorderLayout.SOUTH);

        return p;
    }

    // Componente interno para mostrar botones con SVG minus.svg y plus.svg directamente en cada fila de la tabla
    private class CartRowActionsPanel extends JPanel {
        private final JButton btnPlus = createRowIconButton("icons/plus.svg", ThemeConstants.NEON_GREEN, "Aumentar (+1)");
        private final JButton btnMinus = createRowIconButton("icons/minus.svg", ThemeConstants.NEON_PURPLE, "Disminuir (-1)");
        private final JButton btnTrash = createRowIconButton("icons/trash.svg", ThemeConstants.NEON_RED, "Quitar ítem");

        public CartRowActionsPanel() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
            add(btnPlus);
            add(btnMinus);
            add(btnTrash);
        }

        public void updateBackground(boolean isSelected, boolean isEven) {
            if (isSelected) {
                setBackground(ThemeConstants.withAlpha(ThemeConstants.NEON_CYAN, 50));
            } else {
                setBackground(isEven ? ThemeConstants.CARD_BACKGROUND : ThemeConstants.TABLE_ZEBRA);
            }
        }

        public void setActionListeners(java.awt.event.ActionListener onPlus,
                                       java.awt.event.ActionListener onMinus,
                                       java.awt.event.ActionListener onTrash) {
            for (java.awt.event.ActionListener al : btnPlus.getActionListeners()) btnPlus.removeActionListener(al);
            for (java.awt.event.ActionListener al : btnMinus.getActionListeners()) btnMinus.removeActionListener(al);
            for (java.awt.event.ActionListener al : btnTrash.getActionListeners()) btnTrash.removeActionListener(al);

            btnPlus.addActionListener(onPlus);
            btnMinus.addActionListener(onMinus);
            btnTrash.addActionListener(onTrash);
        }

        private JButton createRowIconButton(String iconPath, Color accentColor, String tooltip) {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(26, 26));
            btn.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
            btn.setFocusPainted(false);
            btn.setToolTipText(tooltip);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createLineBorder(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 140), 1, true));
            btn.setIcon(createIcon(iconPath, accentColor, 13, 13));

            // Efecto Hover neumórfico con cursor de manito (HAND_CURSOR)
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(accentColor);
                    btn.setIcon(createIcon(iconPath, Color.WHITE, 13, 13));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
                    btn.setIcon(createIcon(iconPath, accentColor, 13, 13));
                }
            });
            return btn;
        }
    }

    private class CartCellEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final CartRowActionsPanel rowActionsEditor = new CartRowActionsPanel();
        private int currentRow = -1;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentRow = row;
            rowActionsEditor.updateBackground(true, row % 2 == 0);
            rowActionsEditor.setActionListeners(
                    e -> { fireEditingStopped(); changeQtyAtRow(currentRow, 1); },
                    e -> { fireEditingStopped(); changeQtyAtRow(currentRow, -1); },
                    e -> { fireEditingStopped(); removeCartItemAtRow(currentRow); }
            );
            return rowActionsEditor;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private void changeQtyAtRow(int rowIndex, int delta) {
        if (rowIndex < 0 || rowIndex >= cartItems.size()) return;
        Venta item = cartItems.get(rowIndex);
        int newQty = item.getCantidad() + delta;

        if (newQty <= 0) {
            removeCartItemAtRow(rowIndex);
            return;
        }

        if (item.getProducto() != null && newQty > item.getProducto().getCantidad()) {
            UIUtils.showError(this, "La cantidad (" + newQty + ") supera el stock disponible (" + item.getProducto().getCantidad() + ").");
            return;
        }

        double unitPrice = item.getTotal() / item.getCantidad();
        item.setCantidad(newQty);
        item.setTotal(unitPrice * newQty);

        cartModel.setValueAt(newQty, rowIndex, 1);
        cartModel.setValueAt(String.format("$ %.2f", item.getTotal()), rowIndex, 2);
        updateTotal();
    }

    private void setCustomQtyAtRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= cartItems.size()) return;
        Venta item = cartItems.get(rowIndex);
        String input = JOptionPane.showInputDialog(this,
                "Ingresa la nueva cantidad para " + item.getProducto().getProducto() + ":",
                "Modificar cantidad", JOptionPane.QUESTION_MESSAGE);
        
        if (input == null || input.trim().isEmpty()) return;

        if (!com.mycompany.zl_solucion_integral.config.Validaciones.validarCantidad(input.trim())) {
            UIUtils.showError(this, "La cantidad debe ser un número entero positivo.");
            return;
        }

        int newQty = Integer.parseInt(input.trim());
        if (newQty <= 0) {
            removeCartItemAtRow(rowIndex);
            return;
        }

        if (item.getProducto() != null && newQty > item.getProducto().getCantidad()) {
            UIUtils.showError(this, "La cantidad (" + newQty + ") supera el stock disponible (" + item.getProducto().getCantidad() + ").");
            return;
        }

        double unitPrice = item.getTotal() / item.getCantidad();
        item.setCantidad(newQty);
        item.setTotal(unitPrice * newQty);

        cartModel.setValueAt(newQty, rowIndex, 1);
        cartModel.setValueAt(String.format("$ %.2f", item.getTotal()), rowIndex, 2);
        updateTotal();
    }

    private void removeCartItemAtRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= cartItems.size()) return;
        cartItems.remove(rowIndex);
        cartModel.removeRow(rowIndex);
        updateTotal();
    }

    private JButton createMiniCartButton(String text, String iconPath, Color accentColor, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 11f));
        btn.setForeground(ThemeConstants.TEXT_PRIMARY);
        btn.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        btn.setFocusPainted(false);
        btn.setToolTipText(tooltip);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 120), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        if (iconPath != null) {
            btn.setIcon(createIcon(iconPath, accentColor, 12, 12));
            btn.setIconTextGap(4);
        }
        return btn;
    }

    private void changeSelectedQty(int delta) {
        int selectedRow = tbCart.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= cartItems.size()) {
            UIUtils.showError(this, "Selecciona un producto del carrito para modificar su cantidad.");
            return;
        }
        Venta item = cartItems.get(selectedRow);
        int newQty = item.getCantidad() + delta;

        if (newQty <= 0) {
            removeSelectedCartItem();
            return;
        }

        if (item.getProducto() != null && newQty > item.getProducto().getCantidad()) {
            UIUtils.showError(this, "La cantidad (" + newQty + ") supera el stock disponible (" + item.getProducto().getCantidad() + ").");
            return;
        }

        double unitPrice = item.getTotal() / item.getCantidad();
        item.setCantidad(newQty);
        item.setTotal(unitPrice * newQty);

        cartModel.setValueAt(newQty, selectedRow, 1);
        cartModel.setValueAt(String.format("$ %.2f", item.getTotal()), selectedRow, 2);
        updateTotal();
    }

    private void setCustomQtyForSelected() {
        int selectedRow = tbCart.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= cartItems.size()) {
            UIUtils.showError(this, "Selecciona un producto del carrito para editar su cantidad.");
            return;
        }
        Venta item = cartItems.get(selectedRow);
        String input = JOptionPane.showInputDialog(this,
                "Ingresa la nueva cantidad para " + item.getProducto().getProducto() + ":",
                "Modificar cantidad", JOptionPane.QUESTION_MESSAGE);
        
        if (input == null || input.trim().isEmpty()) return;

        if (!com.mycompany.zl_solucion_integral.config.Validaciones.validarCantidad(input.trim())) {
            UIUtils.showError(this, "La cantidad debe ser un número entero positivo.");
            return;
        }

        int newQty = Integer.parseInt(input.trim());
        if (newQty <= 0) {
            removeSelectedCartItem();
            return;
        }

        if (item.getProducto() != null && newQty > item.getProducto().getCantidad()) {
            UIUtils.showError(this, "La cantidad (" + newQty + ") supera el stock disponible (" + item.getProducto().getCantidad() + ").");
            return;
        }

        double unitPrice = item.getTotal() / item.getCantidad();
        item.setCantidad(newQty);
        item.setTotal(unitPrice * newQty);

        cartModel.setValueAt(newQty, selectedRow, 1);
        cartModel.setValueAt(String.format("$ %.2f", item.getTotal()), selectedRow, 2);
        updateTotal();
    }

    private void removeSelectedCartItem() {
        int selectedRow = tbCart.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= cartItems.size()) {
            UIUtils.showError(this, "Selecciona un producto del carrito para quitar.");
            return;
        }
        cartItems.remove(selectedRow);
        cartModel.removeRow(selectedRow);
        updateTotal();
    }

    private JPanel createCheckoutPanel() {
        RoundedPanel p = new RoundedPanel(16, ThemeConstants.SIDEBAR_BACKGROUND);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel title = new JLabel("Método de pago y cliente", createIcon("icons/clients.svg", ThemeConstants.NEON_GREEN, 18, 18), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_SUBTITLE);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 10, 0);
        p.add(title, gbc);

        // 1. Selector de Método de Pago Prominente arriba
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 2, 0);
        p.add(createLabel("MÉTODO DE PAGO"), gbc);

        cbPaymentMethod = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Crédito"});
        cbPaymentMethod.setBackground(ThemeConstants.INPUT_BACKGROUND);
        cbPaymentMethod.setForeground(ThemeConstants.TEXT_PRIMARY);
        cbPaymentMethod.addActionListener(e -> syncClientState());
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        p.add(cbPaymentMethod, gbc);

        // Checkbox Venta Rápida / Consumidor Final
        chkGenericClient = new JCheckBox("Venta a Consumidor Final (Sin datos)");
        chkGenericClient.setSelected(true);
        chkGenericClient.setOpaque(false);
        chkGenericClient.setForeground(ThemeConstants.TEXT_SECONDARY);
        chkGenericClient.setFont(ThemeConstants.FONT_SMALL);
        chkGenericClient.addActionListener(e -> syncClientState());
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 8, 0);
        p.add(chkGenericClient, gbc);

        // Panel contenedor de campos de cliente (para ocultar/mostrar limpiamente)
        clientFieldsContainer = new JPanel(new GridBagLayout());
        clientFieldsContainer.setOpaque(false);
        GridBagConstraints subGbc = new GridBagConstraints();
        subGbc.fill = GridBagConstraints.HORIZONTAL;
        subGbc.weightx = 1.0; subGbc.gridx = 0;

        subGbc.gridy = 0; subGbc.insets = new Insets(0, 0, 2, 0);
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
        subGbc.gridy = 1; subGbc.insets = new Insets(0, 0, 6, 0);
        txtClientCCHelper = UIUtils.createFieldWithHelper(txtClientCC, "Presiona Enter o cambia de campo para autocompletar");
        clientFieldsContainer.add(txtClientCCHelper, subGbc);

        subGbc.gridy = 2; subGbc.insets = new Insets(0, 0, 2, 0);
        lblClientName = createLabel("NOMBRE DEL CLIENTE / RAZÓN SOCIAL");
        clientFieldsContainer.add(lblClientName, subGbc);

        txtClientName = createTextField("Nombre o negocio");
        setupFieldIcon(txtClientName, "icons/user.svg");
        subGbc.gridy = 3; subGbc.insets = new Insets(0, 0, 6, 0);
        clientFieldsContainer.add(txtClientName, subGbc);

        subGbc.gridy = 4; subGbc.insets = new Insets(0, 0, 2, 0);
        lblClientTel = createLabel("TELÉFONO (OPCIONAL)");
        clientFieldsContainer.add(lblClientTel, subGbc);

        txtClientTel = createTextField("Contacto");
        setupFieldIcon(txtClientTel, "icons/phone.svg");
        subGbc.gridy = 5; subGbc.insets = new Insets(0, 0, 6, 0);
        clientFieldsContainer.add(txtClientTel, subGbc);

        subGbc.gridy = 6; subGbc.insets = new Insets(0, 0, 2, 0);
        lblClientEmail = createLabel("CORREO ELECTRÓNICO (OPCIONAL)");
        clientFieldsContainer.add(lblClientEmail, subGbc);

        txtClientEmail = createTextField("cliente@correo.com");
        setupFieldIcon(txtClientEmail, "icons/email.svg");
        subGbc.gridy = 7; subGbc.insets = new Insets(0, 0, 8, 0);
        clientFieldsContainer.add(txtClientEmail, subGbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 0, 0);
        clientFieldsContainer.setVisible(false);
        p.add(clientFieldsContainer, gbc);

        NeonButton btnConfirm = new NeonButton("Confirmar venta");
        btnConfirm.setNeonColor(ThemeConstants.NEON_GREEN);
        btnConfirm.setIcon(createIcon("icons/check-double.svg", ThemeConstants.NEON_GREEN, 15, 15));
        btnConfirm.setIconTextGap(6);
        btnConfirm.setPreferredSize(new Dimension(0, 40));
        btnConfirm.addActionListener(e -> finishSale());
        btnConfirm.setToolTipText("Procesa el cobro, guarda la venta y descuenta el stock");
        gbc.gridy = 5; gbc.insets = new Insets(6, 0, 0, 0);
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

        Producto prodToCart = new Producto();
        prodToCart.setId(selectedProduct.getId());
        prodToCart.setProducto(selectedProduct.getProducto());
        prodToCart.setPrecio(selectedProduct.getPrecio());
        prodToCart.setPrecioCosto(selectedProduct.getPrecioCosto());
        prodToCart.setCantidad(selectedProduct.getCantidad());
        prodToCart.setCodigo(selectedProduct.getCodigo());
        prodToCart.setCategoria(selectedProduct.getCategoria());
        prodToCart.setDescuento(discount);
        prodToCart.setPrecioCalculado(total);

        Venta v = new Venta();
        v.setProducto(prodToCart);
        v.setCantidad(qty);
        v.setDescuento(discount);
        v.setTotal(total);
        v.setFecha(LocalDate.now());

        cartItems.add(v);
        cartModel.addRow(new Object[]{selectedProduct.getProducto(), qty, String.format("$ %.2f", total), ""});
        updateTotal();

        // Reset
        selectedProduct = null;
        txtSearch.setText("");
        txtSearch.setForeground(ThemeConstants.TEXT_PRIMARY);
        txtQty.setText("1");
        txtDiscount.setText("0");
    }

    private void updateTotal() {
        double grandTotal = cartItems.stream().mapToDouble(Venta::getTotal).sum();
        double grandSubtotal = cartItems.stream().mapToDouble(v -> v.getProducto() != null ? (v.getProducto().getPrecio() * v.getCantidad()) : v.getTotal()).sum();
        double savings = grandSubtotal - grandTotal;
        double effectiveDiscountPct = grandSubtotal > 0 ? (savings / grandSubtotal * 100.0) : 0.0;

        int totalUnits = cartItems.stream().mapToInt(Venta::getCantidad).sum();
        
        lblTotal.setText(String.format("TOTAL: $ %.2f", grandTotal));
        if (lblSubtotal != null) {
            lblSubtotal.setText(String.format("$ %.2f", grandSubtotal));
        }
        if (lblDiscountSavings != null) {
            if (savings > 0.01) {
                lblDiscountSavings.setText(String.format("-$ %.2f (%.1f%%)", savings, effectiveDiscountPct));
            } else {
                lblDiscountSavings.setText("-$ 0.00 (0%)");
            }
        }
        if (lblItemCount != null) {
            lblItemCount.setText(totalUnits == 1 ? "1 ítem " : totalUnits + " ítems ");
        }

        if (centerCardPanel != null) {
            CardLayout cl = (CardLayout) centerCardPanel.getLayout();
            if (cartItems.isEmpty()) {
                cl.show(centerCardPanel, "EMPTY");
            } else {
                cl.show(centerCardPanel, "CART");
            }
        }

        updateChangeCalculation();
    }

    private void updateChangeCalculation() {
        if (lblChangeDue == null || txtCashGiven == null) return;

        double grandTotal = cartItems.stream().mapToDouble(Venta::getTotal).sum();
        String rawInput = txtCashGiven.getText().trim();

        if (rawInput.isEmpty() || grandTotal <= 0) {
            lblChangeDue.setText("$ 0.00");
            lblChangeDue.setForeground(ThemeConstants.TEXT_SECONDARY);
            return;
        }

        // Parseo numérico defensivo ignorando símbolos de moneda o comas
        String cleaned = rawInput.replaceAll("[^0-9.,]", "").replace(",", ".");
        try {
            double cashGiven = Double.parseDouble(cleaned);
            double change = cashGiven - grandTotal;

            if (change >= 0) {
                lblChangeDue.setText(String.format("$ %.2f", change));
                lblChangeDue.setForeground(ThemeConstants.NEON_GREEN);
            } else {
                lblChangeDue.setText(String.format("Falta: $ %.2f", Math.abs(change)));
                lblChangeDue.setForeground(ThemeConstants.NEON_RED);
            }
        } catch (NumberFormatException e) {
            lblChangeDue.setText("Monto inválido");
            lblChangeDue.setForeground(ThemeConstants.NEON_RED);
        }
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
        if (mainGrid != null) {
            mainGrid.revalidate();
            mainGrid.repaint();
        }
        revalidate();
        repaint();
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

    private void setupAutocompletes() {
        // Autocompletado DRY para Productos
        com.mycompany.zl_solucion_integral.views.components.AutocompletePopup.attach(
            txtSearch,
            query -> productoCtrl.buscarProductosSugeridos(query),
            p -> String.format("[%s] %s - $%.2f (Stock: %d)", p.getCodigo(), p.getProducto(), p.getPrecio(), p.getCantidad()),
            p -> {
                selectedProduct = p;
                txtSearch.setText(p.getProducto());
                txtSearch.setForeground(ThemeConstants.NEON_CYAN);
                txtQty.requestFocusInWindow();
                txtQty.selectAll();
            }
        );

        // Autocompletado DRY para Clientes (por Cédula/NIT, Nombre, Teléfono o Correo)
        com.mycompany.zl_solucion_integral.views.components.AutocompletePopup.attach(
            txtClientCC,
            query -> usuarioCtrl.buscarClientesSugeridos(query),
            c -> String.format("[%s] %s %s",
                c.getNoCc() != null && !c.getNoCc().isEmpty() ? c.getNoCc() : "S/D",
                c.getNombre() != null ? c.getNombre() : "",
                c.getTelefono() != null && !c.getTelefono().isEmpty() ? "(" + c.getTelefono() + ")" : ""),
            c -> {
                txtClientCC.setText(c.getNoCc() != null ? c.getNoCc() : "");
                txtClientName.setText(c.getNombre() != null ? c.getNombre() : "");
                txtClientTel.setText(c.getTelefono() != null ? c.getTelefono() : "");
                txtClientEmail.setText(c.getEmail() != null ? c.getEmail() : "");
                txtClientCC.setForeground(ThemeConstants.NEON_GREEN);
            }
        );
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
