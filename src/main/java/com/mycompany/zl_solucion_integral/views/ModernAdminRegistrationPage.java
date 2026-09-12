package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.config.Validaciones;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.models.Usuario;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

public class ModernAdminRegistrationPage extends JFrame {
    private JTextField txtNombre;
    private JTextField txtTel;
    private JTextField txtEmail;
    private JPasswordField txtContraseña;
    
    private Validaciones valid = new Validaciones();
    private UsuarioController usuarioCtrl = new UsuarioController();

    public ModernAdminRegistrationPage() {
        setTitle("ERP+ Business - Registro Inicial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ThemeConstants.BACKGROUND);
        
        RoundedPanel regCard = new RoundedPanel(30, ThemeConstants.SIDEBAR_BACKGROUND);
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
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        regCard.add(txtNombre, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        regCard.add(createLabel("TELÉFONO"), gbc);
        txtTel = createTextField("Ej: 3001234567");
        gbc.gridy = 5;
        regCard.add(txtTel, gbc);
        gbc.gridy = 6;
        gbc.insets = new Insets(2, 0, 15, 0);
        regCard.add(createHelperLabel("Debe tener 10 dígitos numéricos"), gbc);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 5, 0);
        regCard.add(createLabel("CORREO ELECTRÓNICO"), gbc);
        txtEmail = createTextField("usuario@ejemplo.com");
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 15, 0);
        regCard.add(txtEmail, gbc);
        
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 5, 0);
        regCard.add(createLabel("CONTRASEÑA"), gbc);
        txtContraseña = createPasswordField("Mínimo 6 caracteres");
        gbc.gridy = 10;
        regCard.add(txtContraseña, gbc);
        gbc.gridy = 11;
        gbc.insets = new Insets(2, 0, 30, 0);
        regCard.add(createHelperLabel("Usa una contraseña segura que no olvides"), gbc);
        
        // Register Button
        NeonButton btnReg = new NeonButton("REGISTRAR ADMINISTRADOR");
        btnReg.setNeonColor(ThemeConstants.NEON_BLUE);
        btnReg.setPreferredSize(new Dimension(0, 50));
        btnReg.addActionListener(e -> registerAdmin());
        gbc.gridy = 12;
        regCard.add(btnReg, gbc);
        
        mainPanel.add(regCard);
        add(mainPanel);
    }
    
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(ThemeConstants.TEXT_SECONDARY);
        l.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        return l;
    }

    private JLabel createHelperLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(ThemeConstants.NEON_BLUE.getRed(), ThemeConstants.NEON_BLUE.getGreen(), ThemeConstants.NEON_BLUE.getBlue(), 180));
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
        f.setBackground(new Color(15, 23, 42));
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_BLUE);
        f.setFont(ThemeConstants.FONT_BODY);
        
        // Agregar botón de ver contraseña si es un JPasswordField
        if (f instanceof JPasswordField) {
            f.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        }
        
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }
    
    private void registerAdmin() {
        String nombre = txtNombre.getText().trim();
        String numTel = txtTel.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = new String(txtContraseña.getPassword()).trim();
        
        if (nombre.isEmpty() || numTel.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!Validaciones.validarEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email no válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!valid.validarTelefono(numTel)) {
            JOptionPane.showMessageDialog(this, "El teléfono debe tener 10 dígitos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Usuario admin = new Usuario();
        admin.setNombre(nombre);
        admin.setTelefono(numTel);
        admin.setEmail(email);
        admin.setContraseña(pass);
        admin.setRol("1");
        
        usuarioCtrl.agregarUsuario(admin);
        
        if (usuarioCtrl.existeAdministrador()) {
            JOptionPane.showMessageDialog(this, "Administrador registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new ModernLoginPage().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
