package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.config.SelecionRuta;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigPage extends JPanel {
    private JTextField txtDbPath;

    public ConfigPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel title = new JLabel("Configuración del sistema", createIcon("icons/settings.svg", ThemeConstants.NEON_PURPLE, 24, 24), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        add(title, BorderLayout.NORTH);

        // Contenido Principal
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTH;

        // Sección Base de Datos
        gbc.gridy = 0;
        mainContent.add(createDbConfigPanel(), gbc);

        // Sección Información del Sistema
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 0, 0, 0);
        mainContent.add(createInfoPanel(), gbc);
        
        // Espaciador para empujar todo hacia arriba
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        mainContent.add(Box.createVerticalGlue(), gbc);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createDbConfigPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout(15, 15));
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel lblTitle = new JLabel("Base de datos", createIcon("icons/database.svg", ThemeConstants.NEON_BLUE, 20, 20), SwingConstants.LEFT);
        lblTitle.setForeground(ThemeConstants.NEON_BLUE);
        lblTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        p.add(lblTitle, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);

        txtDbPath = new JTextField(SelecionRuta.cargarRutaBaseDatos());
        txtDbPath.setEditable(false);
        txtDbPath.setBackground(new Color(15, 23, 42));
        txtDbPath.setForeground(ThemeConstants.TEXT_SECONDARY);
        
        JLabel icon = new JLabel(createIcon("icons/database.svg", ThemeConstants.TEXT_SECONDARY, 17, 17));
        icon.setForeground(ThemeConstants.TEXT_SECONDARY);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        txtDbPath.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, icon);

        txtDbPath.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        content.add(txtDbPath, BorderLayout.CENTER);

        NeonButton btnChange = new NeonButton("Cambiar ruta");
        btnChange.setNeonColor(ThemeConstants.NEON_BLUE);
        btnChange.setIcon(createIcon("icons/settings.svg", ThemeConstants.NEON_BLUE, 17, 17));
        btnChange.setIconTextGap(8);
        btnChange.setPreferredSize(new Dimension(150, 40));
        btnChange.addActionListener(e -> changeDbPath());
        content.add(btnChange, BorderLayout.EAST);

        p.add(content, BorderLayout.CENTER);

        JLabel lblHint = new JLabel("Respalde db.sqlite antes de mover la ruta de almacenamiento.");
        lblHint.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblHint.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.ITALIC));
        p.add(lblHint, BorderLayout.SOUTH);

        return p;
    }

    private JPanel createInfoPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new GridLayout(4, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        p.add(createDataRow("Versión del software", "1.2.0", "icons/version-code.svg", ThemeConstants.NEON_PURPLE));
        p.add(createDataRow("Motor de base de datos", "SQLite 3.46", "icons/database.svg", ThemeConstants.NEON_BLUE));
        p.add(createDataRow("Licencia", "Pago único (ERP+ Business)", "icons/license.svg", ThemeConstants.NEON_GREEN));
        p.add(createDeveloperRow());

        return p;
    }

    private JPanel createDataRow(String label, String value, String iconPath, Color iconColor) {
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(ThemeConstants.NEON_PURPLE);
        valueLabel.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD));
        return createDataRow(label, valueLabel, iconPath, iconColor);
    }

    private JPanel createDataRow(String label, JLabel valueLabel, String iconPath, Color iconColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel icon = new JLabel(createIcon(iconPath, iconColor, 18, 18));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblLabel.setFont(ThemeConstants.FONT_BODY);
        
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        labelPanel.add(icon, BorderLayout.WEST);
        labelPanel.add(lblLabel, BorderLayout.CENTER);
        
        row.add(labelPanel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private JPanel createDeveloperRow() {
        JLabel link = new JLabel("<html><a href=''>ChopCode Solutions</a></html>");
        link.setForeground(ThemeConstants.NEON_CYAN);
        link.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.setToolTipText("Abrir el portafolio de ChopCode Solutions");
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(java.net.URI.create("https://portafolio-brandon-daza.web.app/"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ConfigPage.this,
                            "No se pudo abrir el portafolio web.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return createDataRow("Desarrollador", link, "icons/developer.svg", ThemeConstants.NEON_CYAN);
    }

    private FlatSVGIcon createIcon(String path, Color color, int width, int height) {
        FlatSVGIcon icon = new FlatSVGIcon(path, width, height);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, color));
        return icon;
    }

    private void changeDbPath() {
        String newPath = SelecionRuta.selecionarRutaDB();
        if (newPath != null) {
            guardarRutaEnConfig(newPath);
            txtDbPath.setText(newPath);
            JOptionPane.showMessageDialog(this, "Configuración guardada. Reinicie la aplicación para aplicar cambios.");
        }
    }

    private void guardarRutaEnConfig(String ruta) {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            System.out.println("Archivo de configuración no encontrado. Creando uno nuevo...");
        }

        try (FileOutputStream output = new FileOutputStream("config.properties")) {
            props.setProperty("db.path", ruta);
            props.store(output, "Configuración de la base de datos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
