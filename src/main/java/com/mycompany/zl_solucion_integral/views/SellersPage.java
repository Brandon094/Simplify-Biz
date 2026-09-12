package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.models.Usuario;
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

public class SellersPage extends JPanel {
    private final UsuarioController usuarioCtrl = new UsuarioController();
    private JTable tbSellers;
    private JScrollPane sellersScroll;
    private JTextField txtName, txtTel, txtEmail, txtPassword;
    private int selectedSellerId = -1;

    public SellersPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header (microcopy de contexto)
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Gestión de empleados", createIcon("icons/staff.svg", ThemeConstants.NEON_PURPLE, 24, 24), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Registra y administra el personal con acceso al sistema");
        subtitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        subtitle.setFont(ThemeConstants.FONT_SMALL);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        // Contenido Principal
        JPanel mainContent = new JPanel(new BorderLayout(25, 0));
        mainContent.setOpaque(false);

        // Formulario (Izquierda)
        mainContent.add(createFormPanel(), BorderLayout.WEST);

        // Tabla (Derecha)
        mainContent.add(createTablePanel(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

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

        JLabel formTitle = new JLabel("Registrar empleado");
        formTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        formTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        gbc.gridy = 0; p.add(formTitle, gbc);

        gbc.gridy = 1; p.add(createLabel("NOMBRE DEL EMPLEADO"), gbc);
        txtName = createTextField("Nombre y Apellido");
        setupFieldIcon(txtName, "icons/user.svg");
        gbc.gridy = 2; p.add(txtName, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("TELÉFONO / CELULAR"), gbc);
        txtTel = createTextField("Ej: 3001234567");
        setupFieldIcon(txtTel, "icons/user.svg");
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 5, 0);
        p.add(UIUtils.createFieldWithHelper(txtTel, "Debe tener 10 dígitos numéricos"), gbc);

        gbc.gridy = 5; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("CORREO ELECTRÓNICO"), gbc);
        txtEmail = createTextField("vendedor@chopcode.com");
        setupFieldIcon(txtEmail, "icons/settings.svg");
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 5, 0);
        p.add(txtEmail, gbc);

        gbc.gridy = 7; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("CONTRASEÑA DE ACCESO"), gbc);
        txtPassword = createTextField("Defina una clave segura");
        setupFieldIcon(txtPassword, "icons/lock.svg");
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 20, 0);
        p.add(UIUtils.createFieldWithHelper(txtPassword, "La usará el empleado para iniciar sesión"), gbc);

        // Botones
        NeonButton btnSave = new NeonButton("Registrar empleado");
        btnSave.setNeonColor(ThemeConstants.NEON_GREEN);
        btnSave.setIcon(createIcon("icons/staff.svg", ThemeConstants.NEON_GREEN, 17, 17));
        btnSave.setIconTextGap(8);
        btnSave.addActionListener(e -> saveSeller());
        btnSave.setToolTipText("Registra un nuevo empleado con acceso al sistema");
        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 10, 0);
        p.add(btnSave, gbc);

        NeonButton btnUpdate = new NeonButton("Actualizar datos");
        btnUpdate.setNeonColor(ThemeConstants.NEON_BLUE);
        btnUpdate.setIcon(createIcon("icons/settings.svg", ThemeConstants.NEON_BLUE, 17, 17));
        btnUpdate.setIconTextGap(8);
        btnUpdate.addActionListener(e -> updateSeller());
        btnUpdate.setToolTipText("Actualiza los datos del empleado seleccionado en la tabla");
        gbc.gridy = 10;
        p.add(btnUpdate, gbc);

        return p;
    }

    private JPanel createTablePanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel tableTitle = new JLabel("Empleados registrados");
        tableTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        tableTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 8, 12, 8));
        p.add(tableTitle, BorderLayout.NORTH);

        tbSellers = new JTable();
        tbSellers.setModel(new DefaultTableModel(
            new String[]{"Id", "Usuario", "Email", "Teléfono", "Rol", "Contraseña"}, 0));
        tbSellers.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbSellers.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbSellers.setRowHeight(35);
        tbSellers.setShowGrid(false);
        tbSellers.setFillsViewportHeight(true);
        tbSellers.setFont(ThemeConstants.FONT_SMALL);
        tbSellers.setSelectionBackground(new Color(34, 197, 94, 70));
        tbSellers.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);
        
        tbSellers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedSeller();
        });

        sellersScroll = new JScrollPane(tbSellers);
        sellersScroll.setOpaque(false);
        sellersScroll.getViewport().setOpaque(false);
        sellersScroll.setBorder(BorderFactory.createEmptyBorder());
        p.add(sellersScroll, BorderLayout.CENTER);
        return p;
    }

    private void saveSeller() {
        String name = txtName.getText().trim();
        String tel = txtTel.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = txtPassword.getText().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, Email y Contraseña son obligatorios.");
            return;
        }

        Usuario u = new Usuario(0, name, tel, email, pass, "0"); // Rol 0 = Vendedor
        usuarioCtrl.agregarUsuario(u);
        refreshData();
        clearFields();
    }

    private void updateSeller() {
        if (selectedSellerId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un vendedor de la tabla.");
            return;
        }
        usuarioCtrl.modificarUsuario(txtName.getText(), txtTel.getText(), txtEmail.getText(), "0", txtPassword.getText(), selectedSellerId);
        refreshData();
        clearFields();
    }

    private void loadSelectedSeller() {
        int row = tbSellers.getSelectedRow();
        if (row != -1) {
            selectedSellerId = Integer.parseInt(tbSellers.getValueAt(row, 0).toString());
            txtName.setText(tbSellers.getValueAt(row, 1).toString());
            txtEmail.setText(tbSellers.getValueAt(row, 2).toString());
            txtTel.setText(tbSellers.getValueAt(row, 3).toString());
            txtPassword.setText(""); // Por seguridad no cargamos la clave
        }
    }

    private void refreshData() {
        usuarioCtrl.mostrarUsuariosPorRol(tbSellers, "0");
        estilizarTabla();
    }

    private void clearFields() {
        txtName.setText("");
        txtTel.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        selectedSellerId = -1;
    }

    private void estilizarTabla() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(34, 197, 94, 40)); // Verde neón suave
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
        for (int i = 0; i < tbSellers.getColumnCount(); i++) {
            tbSellers.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JTableHeader header = tbSellers.getTableHeader();
        header.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        header.setForeground(ThemeConstants.TEXT_SECONDARY);
        header.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        header.setPreferredSize(new Dimension(0, 32));
        header.setReorderingAllowed(false);
        actualizarEstadoVacio();
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

    private void actualizarEstadoVacio() {
        if (tbSellers.getRowCount() == 0) {
            sellersScroll.setViewportView(createEmptyState());
        } else {
            sellersScroll.setViewportView(tbSellers);
        }
        sellersScroll.revalidate();
        sellersScroll.repaint();
    }

    private JPanel createEmptyState() {
        return UIUtils.createEmptyState(
                "icons/staff.svg", ThemeConstants.NEON_GREEN,
                "Aún no tienes empleados registrados",
                "Completa el formulario de la izquierda para dar acceso al sistema a tu personal.",
                "Registrar primer empleado",
                () -> txtName.requestFocusInWindow());
    }

    private JTextField createTextField(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.setBackground(ThemeConstants.INPUT_BACKGROUND);
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_GREEN);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return f;
    }
}
