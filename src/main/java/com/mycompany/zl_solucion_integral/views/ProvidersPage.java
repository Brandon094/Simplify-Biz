package com.mycompany.zl_solucion_integral.views;

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

public class ProvidersPage extends JPanel {
    private JTable tbProviders;
    private JTextField txtName, txtNIT, txtContact, txtPhone, txtEmail;
    private JComboBox<String> cbCategory;
    private JPanel mainContent, formPanel, tablePanel;

    public ProvidersPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        // Contenido Principal
        mainContent = new JPanel(new BorderLayout(25, 0));
        mainContent.setOpaque(false);

        // Formulario (Izquierda)
        formPanel = createFormPanel();
        tablePanel = createTablePanel();
        mainContent.add(formPanel, BorderLayout.WEST);
        mainContent.add(tablePanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // Reflow adaptable: en móvil el formulario pasa arriba (una columna).
        LayoutResponsive.listenWidth(this, (bp, ancho) -> aplicarBreakpoint(bp, ancho));

        refreshData();
    }

    /** Reorganiza formulario y tabla según el ancho (vertical en móvil). */
    private void aplicarBreakpoint(LayoutResponsive.Breakpoint bp, int anchoDisponible) {
        boolean movil = LayoutResponsive.esColumnaUnica(bp);
        formPanel.setPreferredSize(movil ? null : new Dimension(380, 0));
        mainContent.removeAll();
        if (movil) {
            mainContent.setLayout(new BorderLayout(0, 20));
            mainContent.add(formPanel, BorderLayout.NORTH);
            mainContent.add(tablePanel, BorderLayout.CENTER);
        } else {
            mainContent.setLayout(new BorderLayout(25, 0));
            mainContent.add(formPanel, BorderLayout.WEST);
            mainContent.add(tablePanel, BorderLayout.CENTER);
        }
        mainContent.revalidate();
        mainContent.repaint();
    }

    private JPanel createFormPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setPreferredSize(new Dimension(380, 0));
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 5, 0);

        JLabel formTitle = new JLabel("Registrar proveedor");
        formTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        formTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        gbc.gridy = 0; p.add(formTitle, gbc);

        gbc.gridy = 1; p.add(createLabel("RAZÓN SOCIAL / EMPRESA"), gbc);
        txtName = createTextField("Nombre de la empresa");
        setupFieldIcon(txtName, "icons/providers.svg");
        gbc.gridy = 2; p.add(txtName, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("NIT"), gbc);
        txtNIT = createTextField("Número de identificación tributaria");
        setupFieldIcon(txtNIT, "icons/reports.svg");
        gbc.gridy = 4; p.add(txtNIT, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("PERSONA DE CONTACTO"), gbc);
        txtContact = createTextField("Nombre del asesor");
        setupFieldIcon(txtContact, "icons/user.svg");
        gbc.gridy = 6; p.add(txtContact, gbc);

        gbc.gridy = 7; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("TELÉFONO DE CONTACTO"), gbc);
        txtPhone = createTextField("Celular o fijo");
        setupFieldIcon(txtPhone, "icons/user.svg");
        gbc.gridy = 8; p.add(txtPhone, gbc);

        gbc.gridy = 9; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("CORREO ELECTRÓNICO"), gbc);
        txtEmail = createTextField("comercial@proveedor.com");
        setupFieldIcon(txtEmail, "icons/settings.svg");
        gbc.gridy = 10; gbc.insets = new Insets(0, 0, 5, 0);
        p.add(txtEmail, gbc);

        gbc.gridy = 11; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("CATEGORÍA DE SUMINISTRO"), gbc);
        cbCategory = new JComboBox<>(new String[]{"DOTACIÓN", "SEGURIDAD", "ELECTRÓNICA", "PAPELERÍA", "OTROS"});
        cbCategory.setBackground(ThemeConstants.INPUT_BACKGROUND);
        cbCategory.setForeground(ThemeConstants.TEXT_PRIMARY);
        gbc.gridy = 12; gbc.insets = new Insets(0, 0, 30, 0);
        p.add(cbCategory, gbc);

        // Botones
        NeonButton btnSave = new NeonButton("Vincular proveedor");
        btnSave.setNeonColor(ThemeConstants.NEON_CYAN);
        btnSave.setIcon(createIcon("icons/providers.svg", ThemeConstants.NEON_CYAN, 17, 17));
        btnSave.setIconTextGap(8);
        btnSave.addActionListener(e -> UIUtils.showInfo(this, "Próximamente",
                "La interfaz de proveedores está lista. La persistencia llegará en una próxima fase."));
        btnSave.setToolTipText("Vincula el proveedor al sistema (la persistencia llega en la Fase 3)");
        gbc.gridy = 13; gbc.insets = new Insets(0, 0, 10, 0);
        p.add(btnSave, gbc);

        return p;
    }

    private JPanel createTablePanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel tableTitle = new JLabel("Proveedores registrados");
        tableTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        tableTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 8, 12, 8));
        p.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Empresa", "Contacto", "Teléfono", "Email"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{
            {"1", "Suministros Globales SAS", "Carlos Vaca", "3104567890", "carlos@suministros.com"},
            {"2", "Textiles del Caribe", "Marta Ruiz", "3007654321", "ventas@textiles.co"},
            {"3", "Seguridad Total", "Jorge Luis", "3159876543", "info@seguridadtotal.com"}
        }, cols);

        tbProviders = new JTable(model);
        tbProviders.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbProviders.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbProviders.setRowHeight(ThemeConstants.TABLE_ROW_HEIGHT);
        tbProviders.setShowGrid(false);
        tbProviders.setFillsViewportHeight(true);
        tbProviders.setFont(ThemeConstants.FONT_SMALL);
        tbProviders.setSelectionBackground(ThemeConstants.SELECTION_CYAN);
        tbProviders.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);

        JScrollPane scroll = new JScrollPane(tbProviders);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void refreshData() {
        estilizarTabla();
    }

    private void estilizarTabla() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(ThemeConstants.withAlpha(ThemeConstants.NEON_CYAN, 40));
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
        for (int i = 0; i < tbProviders.getColumnCount(); i++) {
            tbProviders.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JTableHeader header = tbProviders.getTableHeader();
        header.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        header.setForeground(ThemeConstants.TEXT_SECONDARY);
        header.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        header.setPreferredSize(new Dimension(0, 32));
        header.setReorderingAllowed(false);
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
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.setBackground(ThemeConstants.INPUT_BACKGROUND);
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_CYAN);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return f;
    }
}
