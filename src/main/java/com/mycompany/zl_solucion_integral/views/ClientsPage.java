package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.models.Usuario;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ClientsPage extends JPanel {
    private final UsuarioController usuarioCtrl = new UsuarioController();
    private JTable tbClientes;
    private JTextField txtNombre, txtTelefono, txtEmail, txtCC, txtPassword;
    private int selectedClientId = -1;

    public ClientsPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel title = new JLabel("👥 Gestión de Clientes y Fidelización");
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        add(title, BorderLayout.NORTH);

        // Contenido Principal
        JPanel mainContent = new JPanel(new BorderLayout(25, 0));
        mainContent.setOpaque(false);

        // Formulario (Izquierda)
        mainContent.add(createFormPanel(), BorderLayout.WEST);

        // Tabla (Derecha)
        mainContent.add(createTablePanel(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

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

        gbc.gridy = 0; p.add(createLabel("👤 NOMBRE COMPLETO"), gbc);
        txtNombre = createTextField("Ej: Juan Pérez");
        setupFieldIcon(txtNombre, "👤");
        gbc.gridy = 1; p.add(txtNombre, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("🆔 CÉDULA / NIT"), gbc);
        txtCC = createTextField("Documento de identidad");
        setupFieldIcon(txtCC, "🆔");
        gbc.gridy = 3; p.add(txtCC, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("📞 TELÉFONO"), gbc);
        txtTelefono = createTextField("300...");
        setupFieldIcon(txtTelefono, "📞");
        gbc.gridy = 5; p.add(txtTelefono, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("📧 CORREO ELECTRÓNICO"), gbc);
        txtEmail = createTextField("cliente@email.com");
        setupFieldIcon(txtEmail, "📧");
        gbc.gridy = 7; p.add(txtEmail, gbc);

        gbc.gridy = 8; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("🔑 CONTRASEÑA (OPCIONAL)"), gbc);
        txtPassword = createTextField("Autogenerada si está vacío");
        setupFieldIcon(txtPassword, "🔑");
        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 25, 0);
        p.add(txtPassword, gbc);

        // Botones
        NeonButton btnSave = new NeonButton("GUARDAR CLIENTE ✅");
        btnSave.setNeonColor(ThemeConstants.NEON_GREEN);
        btnSave.addActionListener(e -> saveClient());
        gbc.gridy = 10; gbc.insets = new Insets(0, 0, 10, 0);
        p.add(btnSave, gbc);

        NeonButton btnUpdate = new NeonButton("ACTUALIZAR DATOS 🔄");
        btnUpdate.setNeonColor(ThemeConstants.NEON_BLUE);
        btnUpdate.addActionListener(e -> updateClient());
        gbc.gridy = 11;
        p.add(btnUpdate, gbc);

        return p;
    }

    private JPanel createTablePanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        tbClientes = new JTable();
        tbClientes.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbClientes.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbClientes.setRowHeight(35);
        tbClientes.setShowGrid(false);
        
        tbClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedClient();
            }
        });

        JScrollPane scroll = new JScrollPane(tbClientes);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void saveClient() {
        String nombre = txtNombre.getText().trim();
        String tel = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        String cc = txtCC.getText().trim();
        String pass = txtPassword.getText().trim();

        if (nombre.isEmpty() || cc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Cédula son obligatorios.");
            return;
        }

        if (pass.isEmpty()) pass = cc; // Pass por defecto es la CC

        Usuario u = new Usuario(0, nombre, tel, email, pass, "2");
        usuarioCtrl.agregarUsuario(u);
        refreshData();
        clearFields();
    }

    private void updateClient() {
        if (selectedClientId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.");
            return;
        }
        String nombre = txtNombre.getText().trim();
        String tel = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = txtPassword.getText().trim();
        if (pass.isEmpty()) pass = "no_change"; // Simplificación para UX

        usuarioCtrl.modificarUsuario(nombre, tel, email, "2", pass, selectedClientId);
        refreshData();
        clearFields();
    }

    private void loadSelectedClient() {
        int row = tbClientes.getSelectedRow();
        if (row != -1) {
            selectedClientId = Integer.parseInt(tbClientes.getValueAt(row, 0).toString());
            txtNombre.setText(tbClientes.getValueAt(row, 1).toString());
            txtEmail.setText(tbClientes.getValueAt(row, 2).toString());
            txtTelefono.setText(tbClientes.getValueAt(row, 3).toString());
            // CC y Pass no siempre están en la tabla por seguridad/espacio, pero el ID es suficiente
        }
    }

    private void refreshData() {
        usuarioCtrl.mostrarUsuariosPorRol(tbClientes, "2");
        estilizarTabla();
    }

    private void clearFields() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtCC.setText("");
        txtPassword.setText("");
        selectedClientId = -1;
        tbClientes.clearSelection();
    }

    private void estilizarTabla() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(168, 85, 247, 40));
                } else {
                    c.setBackground(ThemeConstants.CARD_BACKGROUND);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        for (int i = 0; i < tbClientes.getColumnCount(); i++) {
            tbClientes.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(ThemeConstants.TEXT_SECONDARY);
        l.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        return l;
    }

    private void setupFieldIcon(JTextField f, String icon) {
        JLabel lbl = new JLabel(icon);
        lbl.setForeground(ThemeConstants.TEXT_SECONDARY);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        f.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, lbl);
    }

    private JTextField createTextField(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.setBackground(new Color(15, 23, 42));
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_PURPLE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return f;
    }
}
