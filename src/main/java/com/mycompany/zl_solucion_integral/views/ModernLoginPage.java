package com.mycompany.zl_solucion_integral.views;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.models.Sesion;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIMessages;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import javax.swing.*;
import java.awt.*;

public class ModernLoginPage extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private NeonButton btnLogin;
    private Sesion sesion = new Sesion();
    private JPanel mainPanel;
    private JSplitPane splitPane;
    private RoundedPanel brandPanel;

    public ModernLoginPage() {
        setTitle("ERP+ Business - Inicio de sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Tamaño mínimo pequeño (móvil) para permitir reflow a una columna.
        setMinimumSize(new Dimension(360, 640));
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setUndecorated(false);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeConstants.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        brandPanel = createBrandPanel();
        RoundedPanel loginCard = createLoginCard();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, brandPanel, loginCard);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);
        splitPane.setResizeWeight(0.52);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);

        // Accesibilidad: Enter avanza al siguiente campo, Enter en contraseña
        // ingresa, el botón por defecto responde a Enter y el foco inicia en usuario.
        txtUsuario.addActionListener(e -> txtPassword.requestFocusInWindow());
        txtPassword.addActionListener(e -> performLogin());
        getRootPane().setDefaultButton(btnLogin);
        SwingUtilities.invokeLater(() -> txtUsuario.requestFocusInWindow());

        // Adaptabilidad: en móvil la marca y el formulario se apilan en vertical.
        LayoutResponsive.listen(this, this::aplicarBreakpoint);
    }

    /** Reorganiza la pantalla de login según el ancho (horizontal o vertical). */
    private void aplicarBreakpoint(LayoutResponsive.Breakpoint bp) {
        boolean movil = LayoutResponsive.esColumnaUnica(bp);
        int pad = movil ? 16 : 80;
        int vPad = movil ? 16 : 50;
        mainPanel.setBorder(BorderFactory.createEmptyBorder(vPad, pad, vPad, pad));

        splitPane.setOrientation(movil ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(movil ? 0.35 : 0.52);
        brandPanel.setVisible(true);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private RoundedPanel createBrandPanel() {
        RoundedPanel panel = new RoundedPanel(32, ThemeConstants.BRAND_BACKGROUND);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(42, 42, 42, 42));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel lblTag = new JLabel("ERP+ BUSINESS");
        lblTag.setForeground(ThemeConstants.NEON_PURPLE);
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Sistema de gestión para tu negocio");
        lblSubtitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblSubtitle.setFont(ThemeConstants.FONT_SUBTITLE.deriveFont(Font.BOLD));
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubtitle.setBorder(BorderFactory.createEmptyBorder(12, 0, 18, 0));

        JLabel lblDescription = new JLabel("<html><body style='width: 320px;'>Gestiona inventario, ventas, clientes, proveedores y reportes desde una sola plataforma.</body></html>");
        lblDescription.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblDescription.setFont(ThemeConstants.FONT_BODY);
        lblDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel badges = new JPanel();
        badges.setOpaque(false);
        badges.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        badges.setAlignmentX(Component.LEFT_ALIGNMENT);
        badges.add(createBadge("Inventario"));
        badges.add(createBadge("Ventas"));
        badges.add(createBadge("Reportes"));
        badges.add(createBadge("Clientes"));

        JLabel lblFooter = new JLabel("Acceso seguro • Datos locales • Diseño moderno");
        lblFooter.setForeground(new Color(148, 163, 184));
        lblFooter.setFont(ThemeConstants.FONT_SMALL);
        lblFooter.setBorder(BorderFactory.createEmptyBorder(28, 0, 0, 0));
        lblFooter.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(lblTag);
        content.add(lblSubtitle);
        content.add(lblDescription);
        content.add(Box.createVerticalStrut(20));
        content.add(badges);
        content.add(lblFooter);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private RoundedPanel createLoginCard() {
        RoundedPanel card = new RoundedPanel(32, ThemeConstants.SIDEBAR_BACKGROUND);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(36, 36, 36, 36));
        card.setPreferredSize(new Dimension(440, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblWelcome = new JLabel("Iniciar sesión");
        lblWelcome.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
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
        card.add(createLabel("USUARIO"), gbc);

        txtUsuario = createTextField("Ingrese su usuario");
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
        JCheckBox remember = new JCheckBox("Recordarme");
        remember.setOpaque(false);
        remember.setForeground(ThemeConstants.TEXT_SECONDARY);
        remember.setFocusPainted(false);
        remember.setFont(ThemeConstants.FONT_SMALL);

        // Cargar usuario recordado si existe
        String usuarioGuardado = com.mycompany.zl_solucion_integral.config.SelecionRuta.cargarUsuarioRecordado();
        if (usuarioGuardado != null && !usuarioGuardado.trim().isEmpty()) {
            txtUsuario.setText(usuarioGuardado.trim());
            remember.setSelected(true);
        }

        metaRow.add(remember, BorderLayout.WEST);

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
        btnLogin.addActionListener(e -> {
            if (remember.isSelected()) {
                com.mycompany.zl_solucion_integral.config.SelecionRuta.guardarUsuarioRecordado(txtUsuario.getText());
            } else {
                com.mycompany.zl_solucion_integral.config.SelecionRuta.guardarUsuarioRecordado(null);
            }
            performLogin();
        });
        this.btnLogin = btnLogin;
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(btnLogin, gbc);

        JLabel lblFooter = new JLabel("Versión 1.0 • Seguridad y control empresarial");
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

    private JLabel createBadge(String text) {
        JLabel badge = new JLabel(text);
        badge.setForeground(ThemeConstants.TEXT_PRIMARY);
        badge.setBackground(new Color(59, 130, 246, 40));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(59, 130, 246, 90), 1),
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
        
        habilitarFormulario(false);
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
