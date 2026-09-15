package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.config.Validaciones;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.models.Usuario;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIMessages;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;

public class ModernAdminRegistrationPage extends JFrame {
    private JTextField txtNombre;
    private JTextField txtTel;
    private JTextField txtEmail;
    private JPasswordField txtContraseña;
    private NeonButton btnReg;

    private Validaciones valid = new Validaciones();
    private UsuarioController usuarioCtrl = new UsuarioController();
    private JPanel mainPanel;
    private RoundedPanel regCard;

    public ModernAdminRegistrationPage() {
        setTitle("ERP+ Business - Registro Inicial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Tamaño mínimo pequeño (móvil) para permitir la tarjeta fluida.
        setMinimumSize(new Dimension(360, 640));
        setSize(900, 720);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ThemeConstants.BACKGROUND);

        regCard = new RoundedPanel(30, ThemeConstants.SIDEBAR_BACKGROUND);
        regCard.setPreferredSize(new Dimension(500, 650));
        regCard.setLayout(new GridBagLayout());
        regCard.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title
        JLabel lblTitle = new JLabel("REGISTRO INICIAL", SwingConstants.CENTER);
        lblTitle.setForeground(ThemeConstants.NEON_PURPLE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        regCard.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Configura la cuenta principal del negocio", SwingConstants.CENTER);
        lblSub.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblSub.setFont(ThemeConstants.FONT_SMALL);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        regCard.add(lblSub, gbc);

        // Inputs
        gbc.insets = new Insets(0, 0, 5, 0);

        gbc.gridy = 2;
        regCard.add(createLabel("NOMBRE COMPLETO"), gbc);
        txtNombre = createTextField("Ej: Juan Pérez");
        setupFieldIcon(txtNombre, "icons/user.svg");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        regCard.add(txtNombre, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        regCard.add(createLabel("TELÉFONO"), gbc);
        txtTel = createTextField("Ej: 3001234567");
        setupFieldIcon(txtTel, "icons/phone.svg");
        gbc.gridy = 5;
        regCard.add(txtTel, gbc);
        gbc.gridy = 6;
        gbc.insets = new Insets(2, 0, 15, 0);
        regCard.add(createHelperLabel("Debe tener 10 dígitos numéricos"), gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 5, 0);
        regCard.add(createLabel("CORREO ELECTRÓNICO"), gbc);
        txtEmail = createTextField("usuario@ejemplo.com");
        setupFieldIcon(txtEmail, "icons/email.svg");
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 15, 0);
        regCard.add(txtEmail, gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 5, 0);
        regCard.add(createLabel("CONTRASEÑA"), gbc);
        txtContraseña = createPasswordField("Mínimo 6 caracteres");
        setupFieldIcon(txtContraseña, "icons/lock.svg");
        gbc.gridy = 10;
        regCard.add(txtContraseña, gbc);
        gbc.gridy = 11;
        gbc.insets = new Insets(2, 0, 30, 0);
        regCard.add(createHelperLabel("Usa una contraseña segura que no olvides"), gbc);

        // Register Button
        NeonButton btnReg = new NeonButton("REGISTRAR ADMINISTRADOR");
        btnReg.setNeonColor(ThemeConstants.NEON_BLUE);
        FlatSVGIcon btnIcon = new FlatSVGIcon("icons/user-plus.svg", 18, 18);
        btnIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_BLUE));
        btnReg.setIcon(btnIcon);
        btnReg.setIconTextGap(8);
        btnReg.setPreferredSize(new Dimension(0, 50));
        btnReg.addActionListener(e -> registerAdmin());
        this.btnReg = btnReg;
        gbc.gridy = 12;
        regCard.add(btnReg, gbc);

        mainPanel.add(regCard);
        add(mainPanel);

        // Accesibilidad: Enter avanza al siguiente campo, Enter en contraseña
        // registra, botón por defecto y foco inicial en el nombre.
        txtNombre.addActionListener(e -> txtTel.requestFocusInWindow());
        txtTel.addActionListener(e -> txtEmail.requestFocusInWindow());
        txtEmail.addActionListener(e -> txtContraseña.requestFocusInWindow());
        txtContraseña.addActionListener(e -> registerAdmin());
        getRootPane().setDefaultButton(btnReg);
        SwingUtilities.invokeLater(() -> txtNombre.requestFocusInWindow());

        // Adaptabilidad: en móvil la tarjeta ocupa el ancho disponible.
        LayoutResponsive.listen(this, this::aplicarBreakpoint);
    }

    /** Ajusta el ancho fijo de la tarjeta según el breakpoint vigente. */
    private void aplicarBreakpoint(LayoutResponsive.Breakpoint bp) {
        boolean movil = LayoutResponsive.esColumnaUnica(bp);
        regCard.setPreferredSize(movil ? null : new Dimension(500, 650));
        regCard.setBorder(BorderFactory.createEmptyBorder(
                movil ? 24 : 40, movil ? 20 : 40, movil ? 24 : 40, movil ? 20 : 40));
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(ThemeConstants.TEXT_SECONDARY);
        l.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        return l;
    }

    private JLabel createHelperLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(ThemeConstants.withAlpha(ThemeConstants.NEON_BLUE, 180));
        l.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.ITALIC));
        return l;
    }

    private JTextField createTextField(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        styleInput(f);
        return f;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        styleInput(f);
        return f;
    }

    private void styleInput(JTextField f) {
        f.setBackground(ThemeConstants.INPUT_BACKGROUND);
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_BLUE);
        f.setFont(ThemeConstants.FONT_BODY);

        // Agregar botón de ver contraseña si es un JPasswordField
        if (f instanceof JPasswordField) {
            f.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        }

        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private void setupFieldIcon(JTextField field, String iconPath) {
        FlatSVGIcon icon = new FlatSVGIcon(iconPath, 18, 18);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> ThemeConstants.TEXT_SECONDARY));
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        field.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, lblIcon);
    }

    private void registerAdmin() {
        // Evitar envíos duplicados mientras se procesa el registro.
        if (btnReg != null && !btnReg.isEnabled()) {
            return;
        }

        String nombre = txtNombre.getText().trim();
        String numTel = txtTel.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = new String(txtContraseña.getPassword()).trim();

        if (!Validaciones.validarNoVacio(nombre, numTel, email, pass)) {
            UIUtils.showError(this, UIMessages.MSG_CAMPOS_OBLIGATORIOS);
            return;
        }

        if (!Validaciones.validarEmail(email)) {
            UIUtils.showError(this, UIMessages.MSG_EMAIL_INVALIDO);
            return;
        }

        if (!valid.validarTelefono(numTel)) {
            UIUtils.showError(this, UIMessages.MSG_TELEFONO_INVALIDO);
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre(nombre);
        admin.setTelefono(numTel);
        admin.setEmail(email);
        admin.setContraseña(pass);
        admin.setRol("1");

        habilitarFormulario(false);
        try {
            usuarioCtrl.agregarUsuario(admin);
        } finally {
            habilitarFormulario(true);
        }

        // (La adaptabilidad se registra en el constructor; ver aplicarBreakpoint).

        if (usuarioCtrl.existeAdministrador()) {
            UIUtils.showSuccess(this, UIMessages.MSG_ADMIN_REGISTRADO);
            this.dispose();
            new ModernLoginPage().setVisible(true);
        } else {
            UIUtils.showError(this, UIMessages.MSG_ERROR_REGISTRO);
        }
    }

    /**
     * Feedback de carga: bloquea el formulario y muestra estado "Registrando…"
     * mientras se guarda la cuenta. Es un cambio puramente visual.
     */
    private void habilitarFormulario(boolean habilitar) {
        if (btnReg != null) {
            btnReg.setEnabled(habilitar);
            btnReg.setText(habilitar ? "REGISTRAR ADMINISTRADOR" : UIMessages.TEXTO_PROCESANDO_REGISTRO);
        }
        if (txtNombre != null) txtNombre.setEnabled(habilitar);
        if (txtTel != null) txtTel.setEnabled(habilitar);
        if (txtEmail != null) txtEmail.setEnabled(habilitar);
        if (txtContraseña != null) txtContraseña.setEnabled(habilitar);
    }
}
