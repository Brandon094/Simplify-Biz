package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.ProductoController;
import com.mycompany.zl_solucion_integral.models.Producto;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonPieChart;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
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

    public ProductPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel title = new JLabel("Gestión de inventario", createIcon("icons/products.svg", ThemeConstants.NEON_PURPLE, 24, 24), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        add(title, BorderLayout.NORTH);

        // Contenido Principal (Formulario + Tabla)
        JPanel centerPanel = new JPanel(new BorderLayout(25, 25));
        centerPanel.setOpaque(false);

        // Formulario (Izquierda)
        centerPanel.add(createFormPanel(), BorderLayout.WEST);

        // Contenedor para Tabla y Gráfico (Derecha)
        JPanel rightPanel = new JPanel(new BorderLayout(0, 25));
        rightPanel.setOpaque(false);
        
        rightPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        // Gráfico de Distribución (Abajo de la tabla)
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setOpaque(false);
        chartContainer.setPreferredSize(new Dimension(0, 250));
        updateChart();
        rightPanel.add(chartContainer, BorderLayout.SOUTH);

        centerPanel.add(rightPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Cargar datos iniciales
        refreshData();
    }

    private JPanel createFormPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setPreferredSize(new Dimension(350, 0));
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 5, 0);

        gbc.gridy = 0;
        JLabel formTitle = new JLabel("Registrar producto");
        formTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        formTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        p.add(formTitle, gbc);

        gbc.gridy = 1;
        p.add(createLabel("NOMBRE DEL PRODUCTO"), gbc);
        txtNombre = createTextField();
        setupFieldIcon(txtNombre, "icons/products.svg");
        gbc.gridy = 2;
        p.add(txtNombre, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("CÓDIGO / SKU"), gbc);
        txtCodigo = createTextField();
        setupFieldIcon(txtCodigo, "icons/reports.svg");
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        p.add(txtCodigo, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("CATEGORÍA"), gbc);
        cbCategoria = new JComboBox<>(new String[]{"HERRAMIENTAS", "MATERIALES", "ELECTRÓNICA", "BEBIDAS", "LIMPIEZA", "OTROS"});
        cbCategoria.setBackground(new Color(15, 23, 42));
        cbCategoria.setForeground(ThemeConstants.TEXT_PRIMARY);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 5, 0);
        p.add(cbCategoria, gbc);

        // Fila para Precio y Cantidad
        JPanel row = new JPanel(new GridLayout(1, 2, 15, 0));
        row.setOpaque(false);
        
        JPanel p1 = new JPanel(new BorderLayout(0, 5)); p1.setOpaque(false);
        p1.add(createLabel("PRECIO"), BorderLayout.NORTH);
        txtPrecio = createTextField(); 
        setupFieldIcon(txtPrecio, "icons/sales.svg");
        p1.add(txtPrecio, BorderLayout.CENTER);
        
        JPanel p2 = new JPanel(new BorderLayout(0, 5)); p2.setOpaque(false);
        p2.add(createLabel("STOCK"), BorderLayout.NORTH);
        txtCantidad = createTextField(); 
        setupFieldIcon(txtCantidad, "icons/dashboard.svg");
        p2.add(txtCantidad, BorderLayout.CENTER);
        
        row.add(p1); row.add(p2);
        gbc.gridy = 7;
        gbc.insets = new Insets(15, 0, 25, 0);
        p.add(row, gbc);

        // Botones de Acción
        NeonButton btnSave = new NeonButton("Guardar producto");
        btnSave.setNeonColor(ThemeConstants.NEON_GREEN);
        btnSave.setIcon(createIcon("icons/products.svg", ThemeConstants.NEON_GREEN, 17, 17));
        btnSave.setIconTextGap(8);
        btnSave.addActionListener(e -> saveProduct());
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 10, 0);
        p.add(btnSave, gbc);

        NeonButton btnClear = new NeonButton("Limpiar campos");
        btnClear.setNeonColor(ThemeConstants.NEON_BLUE);
        btnClear.setIcon(createIcon("icons/settings.svg", ThemeConstants.NEON_BLUE, 17, 17));
        btnClear.setIconTextGap(8);
        btnClear.addActionListener(e -> clearFields());
        gbc.gridy = 9;
        p.add(btnClear, gbc);

        return p;
    }

    private JPanel createTablePanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel tableTitle = new JLabel("Productos registrados");
        tableTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        tableTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 8, 12, 8));
        p.add(tableTitle, BorderLayout.NORTH);

        tbProductos = new JTable();
        tbProductos.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbProductos.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbProductos.setRowHeight(35);
        tbProductos.setShowGrid(false);
        tbProductos.setIntercellSpacing(new Dimension(0, 0));
        tbProductos.setFillsViewportHeight(true);
        tbProductos.setFont(ThemeConstants.FONT_SMALL);
        tbProductos.setSelectionBackground(new Color(59, 130, 246, 70));
        tbProductos.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);
        
        productsScroll = new JScrollPane(tbProductos);
        productsScroll.setOpaque(false);
        productsScroll.getViewport().setOpaque(false);
        productsScroll.setBorder(BorderFactory.createEmptyBorder());
        
        p.add(productsScroll, BorderLayout.CENTER);
        return p;
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
        try {
            String name = txtNombre.getText().trim();
            String code = txtCodigo.getText().trim();
            String priceStr = txtPrecio.getText().trim();
            String qtyStr = txtCantidad.getText().trim();
            String cat = (String) cbCategoria.getSelectedItem();

            if (name.isEmpty() || code.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
                return;
            }

            double price = Double.parseDouble(priceStr);
            int qty = Integer.parseInt(qtyStr);

            Producto p = new Producto(name, price, qty, code, cat);
            productoCtrl.agregarOActualizarProductoSiExiste(p);
            
            refreshData();
            clearFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio y Cantidad deben ser numéricos.");
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
                            : new Color(30, 41, 59, 150));
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
        JPanel state = new JPanel(new GridBagLayout());
        state.setOpaque(false);
        FlatSVGIcon icon = createIcon("icons/products.svg", ThemeConstants.NEON_CYAN, 24, 24);
        JLabel message = new JLabel("Aún no hay productos registrados", icon, SwingConstants.CENTER);
        message.setForeground(ThemeConstants.TEXT_SECONDARY);
        message.setFont(ThemeConstants.FONT_BODY);
        message.setIconTextGap(10);
        state.add(message);
        return state;
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

    private JTextField createTextField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(15, 23, 42));
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_PURPLE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return f;
    }
}
