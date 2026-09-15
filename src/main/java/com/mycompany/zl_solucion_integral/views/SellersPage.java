package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.models.Usuario;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.UIMessages;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SellersPage extends JPanel {
    private final UsuarioController usuarioCtrl = new UsuarioController();
    private JTable tbSellers;
    private JScrollPane sellersScroll;
    private JTextField txtName, txtTel, txtEmail, txtPassword;
    private int selectedSellerId = -1;
    private JPanel mainContent, formPanel, tablePanel;

    public SellersPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        // Contenido Principal
        mainContent = new JPanel(new BorderLayout(25, 0));
        mainContent.setOpaque(false);

        // Formulario (Izquierda) envuelto en JScrollPane para evitar desbordamiento vertical
        JScrollPane formScroll = new JScrollPane(createFormPanel());
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.setPreferredSize(new Dimension(360, 0));

        formPanel = new JPanel(new BorderLayout());
        formPanel.setOpaque(false);
        formPanel.add(formScroll, BorderLayout.CENTER);

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
        formPanel.setPreferredSize(movil ? null : new Dimension(360, 0));
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
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 16, 0);
        JLabel formTitle = new JLabel("Registrar empleado", createIcon("icons/user-plus.svg", ThemeConstants.NEON_PURPLE, 20, 20), SwingConstants.LEFT);
        formTitle.setIconTextGap(8);
        formTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        formTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        p.add(formTitle, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 4, 0);
        p.add(createLabel("NOMBRE DEL EMPLEADO"), gbc);
        txtName = createTextField("Nombre y Apellido");
        setupFieldIcon(txtName, "icons/user.svg");
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 12, 0);
        p.add(txtName, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 4, 0);
        p.add(createLabel("TELÉFONO / CELULAR"), gbc);
        txtTel = createTextField("Ej: 3001234567");
        setupFieldIcon(txtTel, "icons/phone.svg");
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 12, 0);
        p.add(UIUtils.createFieldWithHelper(txtTel, "Debe tener 10 dígitos numéricos"), gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 4, 0);
        p.add(createLabel("CORREO ELECTRÓNICO"), gbc);
        txtEmail = createTextField("vendedor@chopcode.com");
        setupFieldIcon(txtEmail, "icons/email.svg");
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 12, 0);
        p.add(txtEmail, gbc);

        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 4, 0);
        p.add(createLabel("CONTRASEÑA DE ACCESO"), gbc);
        txtPassword = createTextField("Defina una clave segura");
        setupFieldIcon(txtPassword, "icons/lock.svg");
        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 18, 0);
        p.add(UIUtils.createFieldWithHelper(txtPassword, "La usará el empleado para iniciar sesión"), gbc);

        // Botones de Acción
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);

        NeonButton btnSave = new NeonButton("Registrar empleado");
        btnSave.setNeonColor(ThemeConstants.NEON_GREEN);
        btnSave.setIcon(createIcon("icons/user-plus.svg", ThemeConstants.NEON_GREEN, 16, 16));
        btnSave.setIconTextGap(6);
        btnSave.setPreferredSize(new Dimension(0, 42));
        btnSave.addActionListener(e -> saveSeller());
        btnSave.setToolTipText("Registra un nuevo empleado con acceso al sistema");

        NeonButton btnClear = new NeonButton("Limpiar");
        btnClear.setNeonColor(ThemeConstants.NEON_BLUE);
        btnClear.setIcon(createIcon("icons/update.svg", ThemeConstants.NEON_BLUE, 16, 16));
        btnClear.setIconTextGap(6);
        btnClear.setPreferredSize(new Dimension(0, 42));
        btnClear.addActionListener(e -> clearFields());
        btnClear.setToolTipText("Limpia el formulario");

        btnRow.add(btnSave);
        btnRow.add(btnClear);

        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 0, 0);
        p.add(btnRow, gbc);

        return p;
    }

    private JPanel createTablePanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout(0, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel tableTitle = new JLabel("Empleados registrados", createIcon("icons/staff.svg", ThemeConstants.NEON_PURPLE, 20, 20), SwingConstants.LEFT);
        tableTitle.setIconTextGap(8);
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
        btnUpdate.addActionListener(e -> updateSeller());
        btnUpdate.setToolTipText("Actualiza los datos del empleado seleccionado");

        NeonButton btnDelete = new NeonButton("Eliminar");
        btnDelete.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnDelete.setIcon(createIcon("icons/trash.svg", ThemeConstants.NEON_PURPLE, 16, 16));
        btnDelete.setIconTextGap(6);
        btnDelete.setPreferredSize(new Dimension(110, 34));
        btnDelete.addActionListener(e -> deleteSeller());
        btnDelete.setToolTipText("Elimina el empleado seleccionado tras confirmar");

        actionRow.add(btnUpdate);
        actionRow.add(btnDelete);
        headerPanel.add(actionRow, BorderLayout.EAST);

        p.add(headerPanel, BorderLayout.NORTH);

        tbSellers = new JTable();
        tbSellers.setModel(new DefaultTableModel(
            new String[]{"Id", "Usuario", "Email", "Teléfono", "Rol"}, 0));
        tbSellers.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbSellers.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbSellers.setRowHeight(ThemeConstants.TABLE_ROW_HEIGHT);
        tbSellers.setShowGrid(false);
        tbSellers.setFillsViewportHeight(true);
        tbSellers.setFont(ThemeConstants.FONT_SMALL);
        tbSellers.setSelectionBackground(ThemeConstants.SELECTION_GREEN);
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
            UIUtils.showError(this, UIMessages.MSG_CAMPOS_OBLIGATORIOS);
            return;
        }
        if (!com.mycompany.zl_solucion_integral.config.Validaciones.validarEmail(email)) {
            UIUtils.showError(this, UIMessages.MSG_EMAIL_INVALIDO);
            return;
        }
        if (!com.mycompany.zl_solucion_integral.config.Validaciones.validarTelefono(tel)) {
            UIUtils.showError(this, "El teléfono debe contener entre 7 y 10 dígitos numéricos.");
            return;
        }

        Usuario u = new Usuario(0, name, tel, email, pass, "0"); // Rol 0 = Vendedor
        com.mycompany.zl_solucion_integral.config.ResultadoOperacion res = usuarioCtrl.agregarUsuario(u);
        if (res.esExito()) {
            UIUtils.showSuccess(this, res.getMensaje());
            refreshData();
            clearFields();
        } else {
            UIUtils.showError(this, res.getMensaje());
        }
    }

    private void updateSeller() {
        if (selectedSellerId == -1) {
            UIUtils.showError(this, "Seleccione un vendedor de la tabla.");
            return;
        }
        String name = txtName.getText().trim();
        String tel = txtTel.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = txtPassword.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            UIUtils.showError(this, UIMessages.MSG_CAMPOS_OBLIGATORIOS);
            return;
        }
        if (!com.mycompany.zl_solucion_integral.config.Validaciones.validarEmail(email)) {
            UIUtils.showError(this, UIMessages.MSG_EMAIL_INVALIDO);
            return;
        }
        if (!com.mycompany.zl_solucion_integral.config.Validaciones.validarTelefono(tel)) {
            UIUtils.showError(this, "El teléfono debe contener entre 7 y 10 dígitos numéricos.");
            return;
        }

        com.mycompany.zl_solucion_integral.config.ResultadoOperacion res = usuarioCtrl.modificarUsuario(name, tel, email, "0", pass, selectedSellerId);
        if (res.esExito()) {
            UIUtils.showSuccess(this, res.getMensaje());
            refreshData();
            clearFields();
        } else {
            UIUtils.showError(this, res.getMensaje());
        }
    }

    private void deleteSeller() {
        if (selectedSellerId == -1) {
            UIUtils.showError(this, "Seleccione un vendedor de la tabla.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el empleado seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirmacion == JOptionPane.YES_OPTION) {
            com.mycompany.zl_solucion_integral.config.ResultadoOperacion res = usuarioCtrl.eliminarUsuario(selectedSellerId);
            if (res.esExito()) {
                UIUtils.showSuccess(this, res.getMensaje());
                refreshData();
                clearFields();
            } else {
                UIUtils.showError(this, res.getMensaje());
            }
        }
    }

    private void loadSelectedSeller() {
        int viewRow = tbSellers.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = tbSellers.convertRowIndexToModel(viewRow);
            Object idVal = tbSellers.getModel().getValueAt(modelRow, 0);
            Integer idParsed = com.mycompany.zl_solucion_integral.config.Validaciones.parseEntero(idVal != null ? idVal.toString() : "");
            if (idParsed != null && idParsed != -1) {
                selectedSellerId = idParsed;
                txtName.setText(tbSellers.getModel().getValueAt(modelRow, 1).toString());
                txtEmail.setText(tbSellers.getModel().getValueAt(modelRow, 2).toString());
                txtTel.setText(tbSellers.getModel().getValueAt(modelRow, 3).toString());
                txtPassword.setText(""); // Por seguridad no cargamos la clave
            }
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
        UIUtils.applyTableStyling(tbSellers);
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
