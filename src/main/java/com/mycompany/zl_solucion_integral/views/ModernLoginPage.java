package com.mycompany.zl_solucion_integral.views;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIMessages;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import javax.swing.*;
import java.awt.*;

public class ModernLoginPage extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JCheckBox chkRemember;
    private NeonButton btnLogin;
    private JPanel mainPanel;
    private RoundedPanel brandPanel;

    public ModernLoginPage() {
        setTitle("ERP+ Business - Inicio de sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            setIconImage(new ImageIcon(getClass().getResource("/icons/app_icon.png")).getImage());
        } catch (Exception ignored) {}
        // Tamaño mínimo para evitar compresión de la tarjeta de login.
        setMinimumSize(new Dimension(850, 680));
        setSize(1180, 750);
        setLocationRelativeTo(null);
        setUndecorated(false);

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ThemeConstants.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        brandPanel = createBrandPanel();
        RoundedPanel loginCard = createLoginCard();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1.0;

        // Panel de marca ocupa todo el espacio restante a la izquierda
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 24);
        mainPanel.add(brandPanel, gbc);

        // Tarjeta de login fija a la derecha con un ancho garantizado de 420px
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(loginCard, gbc);

        add(mainPanel);

        // Accesibilidad: Enter avanza al siguiente campo, Enter en contraseña
        // ingresa, el botón por defecto responde a Enter y el foco inicia en usuario.
        txtUsuario.addActionListener(e -> txtPassword.requestFocusInWindow());
        txtPassword.addActionListener(e -> performLogin());
        getRootPane().setDefaultButton(btnLogin);
        SwingUtilities.invokeLater(() -> txtUsuario.requestFocusInWindow());
    }

    private RoundedPanel createBrandPanel() {
        RoundedPanel panel = new RoundedPanel(32, ThemeConstants.BRAND_BACKGROUND);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(36, 36, 36, 36));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Header de marca con Logo Oficial ERP+
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        logoRow.setOpaque(false);
        logoRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        try {
            ImageIcon rawIcon = new ImageIcon(getClass().getResource("/icons/app_icon.png"));
            Image scaledImg = rawIcon.getImage().getScaledInstance(54, 54, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(scaledImg));
            logoRow.add(lblLogo);
        } catch (Exception ignored) {}

        JLabel lblTag = new JLabel("ERP+ BUSINESS");
        lblTag.setForeground(ThemeConstants.NEON_PURPLE);
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoRow.add(lblTag);

        JLabel lblSubtitle = new JLabel("Sistema de gestión para tu negocio");
        lblSubtitle.setForeground(ThemeConstants.BRAND_TEXT_PRIMARY);
        lblSubtitle.setFont(ThemeConstants.FONT_SUBTITLE.deriveFont(Font.BOLD));
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubtitle.setBorder(BorderFactory.createEmptyBorder(12, 0, 14, 0));

        JLabel lblDescription = new JLabel("<html><body style='width: 360px;'>Plataforma integral de gestión empresarial: controla ventas en POS, inventario en tiempo real, recaudo de cartera, abastecimiento de proveedores y análisis financiero con cifrado local.</body></html>");
        lblDescription.setForeground(ThemeConstants.BRAND_TEXT_SECONDARY);
        lblDescription.setFont(ThemeConstants.FONT_BODY);
        lblDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel badges = new JPanel();
        badges.setOpaque(false);
        badges.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        badges.setAlignmentX(Component.LEFT_ALIGNMENT);
        badges.add(createBrandBadge("Ventas & POS", "icons/sales.svg", ThemeConstants.BRAND_TEXT_PRIMARY, ThemeConstants.NEON_CYAN));
        badges.add(createBrandBadge("Inventario", "icons/products.svg", ThemeConstants.BRAND_TEXT_PRIMARY, ThemeConstants.NEON_CYAN));
        badges.add(createBrandBadge("Cartera & Cobro", "icons/cart-shopping.svg", ThemeConstants.BRAND_TEXT_PRIMARY, ThemeConstants.NEON_CYAN));
        badges.add(createBrandBadge("Compras & Prov.", "icons/suppliers.svg", ThemeConstants.BRAND_TEXT_PRIMARY, ThemeConstants.NEON_CYAN));
        badges.add(createBrandBadge("Clientes", "icons/clients.svg", ThemeConstants.BRAND_TEXT_PRIMARY, ThemeConstants.NEON_CYAN));
        badges.add(createBrandBadge("Personal & Nómina", "icons/staff.svg", ThemeConstants.BRAND_TEXT_PRIMARY, ThemeConstants.NEON_CYAN));
        badges.add(createBrandBadge("Reportes BI", "icons/reports.svg", ThemeConstants.BRAND_TEXT_PRIMARY, ThemeConstants.NEON_CYAN));
        badges.add(createBrandBadge("Ajustes & BD", "icons/settings.svg", ThemeConstants.BRAND_TEXT_PRIMARY, ThemeConstants.NEON_CYAN));

        JPanel telemetryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        telemetryRow.setOpaque(false);
        telemetryRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel pillOffline = new JLabel(" ● Base de Datos SQLite WAL ");
        pillOffline.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        pillOffline.setForeground(ThemeConstants.NEON_GREEN);
        pillOffline.setBackground(ThemeConstants.withAlpha(ThemeConstants.NEON_GREEN, 30));
        pillOffline.setOpaque(true);
        pillOffline.setBorder(BorderFactory.createLineBorder(ThemeConstants.withAlpha(ThemeConstants.NEON_GREEN, 90), 1));
        
        JLabel pillLic = new JLabel(" ● Motor ERP+ v2.1.0 ");
        pillLic.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        pillLic.setForeground(ThemeConstants.NEON_PURPLE);
        pillLic.setBackground(ThemeConstants.withAlpha(ThemeConstants.NEON_PURPLE, 30));
        pillLic.setOpaque(true);
        pillLic.setBorder(BorderFactory.createLineBorder(ThemeConstants.withAlpha(ThemeConstants.NEON_PURPLE, 90), 1));

        telemetryRow.add(pillOffline);
        telemetryRow.add(pillLic);

        JLabel lblFooter = new JLabel("Acceso seguro • Encriptación de credenciales • Respaldo local");
        lblFooter.setForeground(ThemeConstants.BRAND_TEXT_SECONDARY);
        lblFooter.setFont(ThemeConstants.FONT_SMALL);
        lblFooter.setIconTextGap(6);
        try {
            FlatSVGIcon lockIcon = new FlatSVGIcon("icons/shield-heart.svg", 14, 14);
            lockIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_CYAN));
            lblFooter.setIcon(lockIcon);
        } catch (Exception ignored) {}
        lblFooter.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        lblFooter.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(logoRow);
        content.add(lblSubtitle);
        content.add(lblDescription);
        content.add(Box.createVerticalStrut(14));
        content.add(badges);
        content.add(Box.createVerticalStrut(12));
        content.add(telemetryRow);
        content.add(lblFooter);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private RoundedPanel createLoginCard() {
        RoundedPanel card = new RoundedPanel(32, ThemeConstants.SIDEBAR_BACKGROUND);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(36, 36, 36, 36));
        card.setPreferredSize(new Dimension(420, 0));
        card.setMinimumSize(new Dimension(420, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblWelcome = new JLabel(" Iniciar sesión");
        lblWelcome.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        try {
            FlatSVGIcon iconWelcome = new FlatSVGIcon("icons/user.svg", 26, 26);
            iconWelcome.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_PURPLE));
            lblWelcome.setIcon(iconWelcome);
        } catch (Exception ignored) {}
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(lblWelcome, gbc);

        JLabel lblInfo = new JLabel("Accede a tu panel de administración");
        lblInfo.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblInfo.setFont(ThemeConstants.FONT_BODY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        card.add(lblInfo, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(createLabel("USUARIO O CORREO ELECTRÓNICO"), gbc);

        txtUsuario = createTextField("Primer nombre o correo registrado");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(txtUsuario, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(createLabel("CONTRASEÑA"), gbc);

        txtPassword = createPasswordField("Ingrese su contraseña");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(txtPassword, gbc);

        JPanel metaRow = new JPanel(new BorderLayout(10, 0));
        metaRow.setOpaque(false);
        chkRemember = new JCheckBox("Recordarme");
        chkRemember.setOpaque(false);
        chkRemember.setForeground(ThemeConstants.TEXT_SECONDARY);
        chkRemember.setFocusPainted(false);
        chkRemember.setFont(ThemeConstants.FONT_SMALL);

        // Cargar usuario recordado si existe
        String usuarioGuardado = com.mycompany.zl_solucion_integral.config.SelecionRuta.cargarUsuarioRecordado();
        if (usuarioGuardado != null && !usuarioGuardado.trim().isEmpty()) {
            txtUsuario.setText(usuarioGuardado.trim());
            chkRemember.setSelected(true);
        }

        metaRow.add(chkRemember, BorderLayout.WEST);

        JLabel forgot = new JLabel("¿Olvidaste tu contraseña?");
        forgot.setForeground(ThemeConstants.NEON_BLUE);
        forgot.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        forgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                forgot.setForeground(ThemeConstants.NEON_PURPLE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                forgot.setForeground(ThemeConstants.NEON_BLUE);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new com.mycompany.zl_solucion_integral.views.components.organisms.PasswordRecoveryDialog(ModernLoginPage.this).setVisible(true);
            }
        });
        metaRow.add(forgot, BorderLayout.EAST);

        gbc.gridy = 6;
        gbc.insets = new Insets(4, 0, 24, 0);
        card.add(metaRow, gbc);

        NeonButton btnLogin = new NeonButton("INGRESAR");
        btnLogin.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnLogin.setPreferredSize(new Dimension(0, 50));
        btnLogin.addActionListener(e -> performLogin());
        this.btnLogin = btnLogin;
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(btnLogin, gbc);

        JLabel lblFooter = new JLabel("Versión 2.1.0 • Seguridad y control empresarial");
        lblFooter.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setFont(ThemeConstants.FONT_SMALL);
        gbc.gridy = 8;
        gbc.insets = new Insets(16, 0, 4, 0);
        card.add(lblFooter, gbc);

        JLabel lblPoweredBy = new JLabel("Powered by: ChopCode Solutions");
        lblPoweredBy.setForeground(ThemeConstants.NEON_BLUE);
        lblPoweredBy.setHorizontalAlignment(SwingConstants.CENTER);
        lblPoweredBy.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        lblPoweredBy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblPoweredBy.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                lblPoweredBy.setForeground(ThemeConstants.NEON_PURPLE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                lblPoweredBy.setForeground(ThemeConstants.NEON_BLUE);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI("https://portafolio-brandon-daza.web.app/"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        gbc.gridy = 9;
        gbc.insets = new Insets(4, 0, 0, 0);
        card.add(lblPoweredBy, gbc);

        return card;
    }

    private JLabel createBrandBadge(String text, String iconPath, Color textColor, Color iconColor) {
        JLabel badge = new JLabel(" " + text);
        badge.setForeground(textColor);
        badge.setBackground(ThemeConstants.withAlpha(ThemeConstants.NEON_BLUE, 35));
        badge.setOpaque(true);
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                FlatSVGIcon icon = new FlatSVGIcon(iconPath, 14, 14);
                icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, iconColor));
                badge.setIcon(icon);
            } catch (Exception ignored) {}
        }
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeConstants.withAlpha(ThemeConstants.NEON_BLUE, 90), 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        badge.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        return badge;
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

        FlatSVGIcon userIcon = new FlatSVGIcon("icons/user.svg", 18, 18);
        userIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> ThemeConstants.TEXT_SECONDARY));
        JLabel icon = new JLabel(userIcon);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        f.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, icon);

        styleInput(f);
        return f;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);

        FlatSVGIcon lockIcon = new FlatSVGIcon("icons/lock.svg", 18, 18);
        lockIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> ThemeConstants.TEXT_SECONDARY));
        JLabel icon = new JLabel(lockIcon);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        f.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, icon);

        styleInput(f);
        return f;
    }
    
    private void styleInput(JTextField f) {
        f.setBackground(ThemeConstants.INPUT_BACKGROUND);
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_PURPLE);
        f.setFont(ThemeConstants.FONT_BODY);
        
        // Agregar botón para mostrar contraseña
        if (f instanceof JPasswordField) {
            f.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        }
        
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
    }
    
    private void performLogin() {
        // Evitar envíos duplicados mientras se procesa la validación.
        if (btnLogin != null && !btnLogin.isEnabled()) {
            return;
        }

        UsuarioController controller = new UsuarioController();
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        // Guardar o borrar la preferencia del usuario recordado
        if (chkRemember != null && chkRemember.isSelected()) {
            com.mycompany.zl_solucion_integral.config.SelecionRuta.guardarUsuarioRecordado(user);
        } else {
            com.mycompany.zl_solucion_integral.config.SelecionRuta.guardarUsuarioRecordado(null);
        }
        
        habilitarFormulario(false);
        try {
            // Intentar login como Administrador (Rol 1)
            if (controller.validarCredencialesAdmin(user, pass)) {
                com.mycompany.zl_solucion_integral.models.Sesion.setRolLogueado("1");
                this.dispose();
                new MainTemplate("1").setVisible(true);
            } 
            // Intentar login como Vendedor (Rol != 1)
            else if (controller.validarCredencialesUsuarioRegular(user, pass)) {
                com.mycompany.zl_solucion_integral.models.Sesion.setRolLogueado("0");
                this.dispose();
                new MainTemplate("0").setVisible(true);
            }
            else {
                JOptionPane.showMessageDialog(this, UIMessages.MSG_CREDENCIALES_INVALIDAS,
                        UIMessages.TITULO_ERROR, JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            habilitarFormulario(true);
        }
    }

    /**
     * Feedback de carga: bloquea el formulario y muestra estado "Ingresando…"
     * mientras se validan las credenciales. Es un cambio puramente visual.
     */
    private void habilitarFormulario(boolean habilitar) {
        if (btnLogin != null) {
            btnLogin.setEnabled(habilitar);
            btnLogin.setText(habilitar ? "INGRESAR" : UIMessages.TEXTO_PROCESANDO_INGRESO);
        }
        if (txtUsuario != null) {
            txtUsuario.setEnabled(habilitar);
        }
        if (txtPassword != null) {
            txtPassword.setEnabled(habilitar);
        }
    }
}
