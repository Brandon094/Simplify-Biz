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

public class SellersPage extends JPanel {
    private final UsuarioController usuarioCtrl = new UsuarioController();
    private JTable tbSellers;
    private JTextField txtName, txtTel, txtEmail, txtPassword;
    private int selectedSellerId = -1;

    public SellersPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel title = new JLabel("👷 Gestión de Equipo de Ventas");
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

        gbc.gridy = 0; p.add(createLabel("👤 NOMBRE DEL VENDEDOR"), gbc);
        txtName = createTextField("Nombre y Apellido");
        setupFieldIcon(txtName, "👤");
        gbc.gridy = 1; p.add(txtName, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("📞 TELÉFONO / CELULAR"), gbc);
        txtTel = createTextField("Ej: 3001234567");
        setupFieldIcon(txtTel, "📞");
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 5, 0);
        p.add(txtTel, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("📧 CORREO ELECTRÓNICO"), gbc);
        txtEmail = createTextField("vendedor@chopcode.com");
        setupFieldIcon(txtEmail, "📧");
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 5, 0);
        p.add(txtEmail, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(15, 0, 5, 0);
        p.add(createLabel("🔑 CONTRASEÑA DE ACCESO"), gbc);
        txtPassword = createTextField("Defina una clave segura");
        setupFieldIcon(txtPassword, "🔑");
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 25, 0);
        p.add(txtPassword, gbc);

        // Botones
        NeonButton btnSave = new NeonButton("REGISTRAR VENDEDOR ✅");
        btnSave.setNeonColor(ThemeConstants.NEON_GREEN);
        btnSave.addActionListener(e -> saveSeller());
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 10, 0);
        p.add(btnSave, gbc);

        NeonButton btnUpdate = new NeonButton("ACTUALIZAR DATOS 🔄");
        btnUpdate.setNeonColor(ThemeConstants.NEON_BLUE);
        btnUpdate.addActionListener(e -> updateSeller());
        gbc.gridy = 9;
        p.add(btnUpdate, gbc);

        return p;
    }

    private JPanel createTablePanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        tbSellers = new JTable();
        tbSellers.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbSellers.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbSellers.setRowHeight(35);
        tbSellers.setShowGrid(false);
        
        tbSellers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedSeller();
        });

        JScrollPane scroll = new JScrollPane(tbSellers);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        p.add(scroll, BorderLayout.CENTER);
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
                    c.setBackground(ThemeConstants.CARD_BACKGROUND);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        for (int i = 0; i < tbSellers.getColumnCount(); i++) {
            tbSellers.getColumnModel().getColumn(i).setCellRenderer(renderer);
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
        f.setCaretColor(ThemeConstants.NEON_GREEN);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return f;
    }
}
