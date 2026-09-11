package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.models.Sesion;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

public class ModernLoginPage extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private Sesion sesion = new Sesion();

    public ModernLoginPage() {
        setTitle("Simplify Biz - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ThemeConstants.BACKGROUND);
        
        RoundedPanel loginCard = new RoundedPanel(30, ThemeConstants.SIDEBAR_BACKGROUND);
        loginCard.setPreferredSize(new Dimension(450, 550));
        loginCard.setLayout(new GridBagLayout());
        loginCard.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        
        // Logo / Title
        JLabel lblLogo = new JLabel("VORTEX ERP", SwingConstants.CENTER);
        lblLogo.setForeground(ThemeConstants.NEON_PURPLE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        loginCard.add(lblLogo, gbc);
        
        JLabel lblSub = new JLabel("Gestión Inteligente de Negocios", SwingConstants.CENTER);
        lblSub.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblSub.setFont(ThemeConstants.FONT_SMALL);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        loginCard.add(lblSub, gbc);
        
        // Inputs
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy = 2;
        loginCard.add(createLabel("USUARIO"), gbc);
        
        txtUsuario = createTextField("Ingrese su usuario");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 25, 0);
        loginCard.add(txtUsuario, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        loginCard.add(createLabel("CONTRASEÑA"), gbc);
        
        txtPassword = createPasswordField("Ingrese su contraseña");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 45, 0);
        loginCard.add(txtPassword, gbc);
        
        // Login Button
        NeonButton btnLogin = new NeonButton("INICIAR SESIÓN");
        btnLogin.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnLogin.setPreferredSize(new Dimension(0, 50));
        btnLogin.addActionListener(e -> performLogin());
        gbc.gridy = 6;
        loginCard.add(btnLogin, gbc);
        
        mainPanel.add(loginCard);
        add(mainPanel);
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
        
        // Icono de usuario
        JLabel icon = new JLabel("👤");
        icon.setForeground(ThemeConstants.TEXT_SECONDARY);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        f.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, icon);
        
        styleInput(f);
        return f;
    }
    
    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        
        // Icono de llave/password
        JLabel icon = new JLabel("🔑");
        icon.setForeground(ThemeConstants.TEXT_SECONDARY);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        f.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, icon);
        
        styleInput(f);
        return f;
    }
    
    private void styleInput(JTextField f) {
        f.setBackground(new Color(15, 23, 42));
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_PURPLE);
        f.setFont(ThemeConstants.FONT_BODY);
        
        // Agregar botón para mostrar contraseña
        if (f instanceof JPasswordField) {
            f.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        }
        
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
    }
    
    private void performLogin() {
        UsuarioController controller = new UsuarioController();
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        
        try {
            // Intentar login como Administrador (Rol 1)
            if (controller.validarCredencialesAdmin(user, pass)) {
                com.mycompany.zl_solucion_integral.models.Sesion.setUsuarioLogueado(user);
                this.dispose();
                new MainTemplate("1").setVisible(true);
            } 
            // Intentar login como Vendedor (Rol != 1)
            else if (controller.validarCredencialesUsuarioRegular(user, pass)) {
                com.mycompany.zl_solucion_integral.models.Sesion.setUsuarioLogueado(user);
                this.dispose();
                new MainTemplate("0").setVisible(true);
            }
            else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
