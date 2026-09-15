package com.mycompany.zl_solucion_integral.views.components.organisms;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;

import javax.swing.*;
import java.awt.*;

public class PasswordRecoveryDialog extends JDialog {

    private final UsuarioController usuarioController;
    private final CardLayout cardLayout;
    private final JPanel cardsContainer;

    // Componentes Paso 1: Verificación
    private JTextField txtUsuarioOEmail;
    private JTextField txtTelefono;

    // Componentes Paso 2: Nueva Contraseña
    private JPasswordField txtNuevaPass;
    private JPasswordField txtConfirmPass;
    private String usuarioVerificado;

    public PasswordRecoveryDialog(Window owner) {
        super(owner, "Recuperación de contraseña", ModalityType.APPLICATION_MODAL);
        this.usuarioController = new UsuarioController();
        setSize(480, 480);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeConstants.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        cardLayout = new CardLayout();
        cardsContainer = new JPanel(cardLayout);
        cardsContainer.setOpaque(false);

        cardsContainer.add(createStep1Panel(), "STEP1");
        cardsContainer.add(createStep2Panel(), "STEP2");

        mainPanel.add(cardsContainer, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createStep1Panel() {
        RoundedPanel panel = new RoundedPanel(24, ThemeConstants.SIDEBAR_BACKGROUND);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("Verificar identidad");
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Ingresa tu usuario/correo y tu teléfono registrado.");
        lblSub.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblSub.setFont(ThemeConstants.FONT_BODY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(lblSub, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(createLabel("USUARIO O CORREO"), gbc);

        txtUsuarioOEmail = createTextField("Ej. admin@empresa.com");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 16, 0);
        panel.add(txtUsuarioOEmail, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(createLabel("TELÉFONO REGISTRADO"), gbc);

        txtTelefono = createTextField("Ej. 3001234567");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 24, 0);
        panel.add(txtTelefono, gbc);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);

        FlatSVGIcon cancelIcon = new FlatSVGIcon("icons/xmark.svg", 16, 16);
        cancelIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.TEXT_SECONDARY));

        NeonButton btnCancelar = new NeonButton("Cancelar");
        btnCancelar.setIcon(cancelIcon);
        btnCancelar.setIconTextGap(6);
        btnCancelar.addActionListener(e -> dispose());

        NeonButton btnVerificar = new NeonButton("VERIFICAR");
        btnVerificar.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnVerificar.setPreferredSize(new Dimension(0, 42));
        btnVerificar.addActionListener(e -> procesarVerificacion());

        btnRow.add(btnCancelar);
        btnRow.add(btnVerificar);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(btnRow, gbc);

        return panel;
    }

    private JPanel createStep2Panel() {
        RoundedPanel panel = new RoundedPanel(24, ThemeConstants.SIDEBAR_BACKGROUND);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("Nueva contraseña");
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Ingresa tu nueva clave de acceso.");
        lblSub.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblSub.setFont(ThemeConstants.FONT_BODY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(lblSub, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(createLabel("NUEVA CONTRASEÑA"), gbc);

        txtNuevaPass = createPasswordField("Mínimo 4 caracteres");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 16, 0);
        panel.add(txtNuevaPass, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(createLabel("CONFIRMAR CONTRASEÑA"), gbc);

        txtConfirmPass = createPasswordField("Repita la contraseña");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 24, 0);
        panel.add(txtConfirmPass, gbc);

        NeonButton btnRestablecer = new NeonButton("CAMBIAR CONTRASEÑA");
        btnRestablecer.setNeonColor(ThemeConstants.NEON_GREEN);
        btnRestablecer.setPreferredSize(new Dimension(0, 44));
        btnRestablecer.addActionListener(e -> procesarRestablecimiento());

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(btnRestablecer, gbc);

        return panel;
    }

    private void procesarVerificacion() {
        String idUser = txtUsuarioOEmail.getText().trim();
        String tel = txtTelefono.getText().trim();

        if (idUser.isEmpty() || tel.isEmpty()) {
            UIUtils.showError(this, "Por favor complete todos los campos.");
            return;
        }

        if (usuarioController.validarDatosRecuperacion(idUser, tel)) {
            this.usuarioVerificado = idUser;
            cardLayout.show(cardsContainer, "STEP2");
        } else {
            UIUtils.showError(this, "Los datos ingresados no coinciden con ningún usuario registrado.");
        }
    }

    private void procesarRestablecimiento() {
        String pass1 = new String(txtNuevaPass.getPassword()).trim();
        String pass2 = new String(txtConfirmPass.getPassword()).trim();

        if (pass1.isEmpty() || pass2.isEmpty()) {
            UIUtils.showError(this, "Por favor ingrese y confirme su nueva contraseña.");
            return;
        }

        if (!pass1.equals(pass2)) {
            UIUtils.showError(this, "Las contraseñas no coinciden.");
            return;
        }

        ResultadoOperacion res = usuarioController.restablecerContraseña(usuarioVerificado, pass1);
        if (res.esExito()) {
            UIUtils.showSuccess(this, "¡Contraseña actualizada con éxito! Ya puedes ingresar con tu nueva clave.");
            dispose();
        } else {
            UIUtils.showError(this, res.getMensaje());
        }
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(ThemeConstants.TEXT_SECONDARY);
        l.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        return l;
    }

    private JTextField createTextField(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.setBackground(ThemeConstants.INPUT_BACKGROUND);
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_PURPLE);
        f.setFont(ThemeConstants.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return f;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        f.setBackground(ThemeConstants.INPUT_BACKGROUND);
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_PURPLE);
        f.setFont(ThemeConstants.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return f;
    }
}
