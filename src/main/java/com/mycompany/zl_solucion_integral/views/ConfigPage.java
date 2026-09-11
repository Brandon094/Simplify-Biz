package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.config.SelecionRuta;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
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
        JLabel title = new JLabel("⚙️ Configuraciones del Sistema");
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

        JLabel lblTitle = new JLabel("🗄️ BASE DE DATOS");
        lblTitle.setForeground(ThemeConstants.NEON_BLUE);
        lblTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        p.add(lblTitle, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);

        txtDbPath = new JTextField(SelecionRuta.cargarRutaBaseDatos());
        txtDbPath.setEditable(false);
        txtDbPath.setBackground(new Color(15, 23, 42));
        txtDbPath.setForeground(ThemeConstants.TEXT_SECONDARY);
        
        // Icono de base de datos
        JLabel icon = new JLabel("📁");
        icon.setForeground(ThemeConstants.TEXT_SECONDARY);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        txtDbPath.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, icon);

        txtDbPath.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        content.add(txtDbPath, BorderLayout.CENTER);

        NeonButton btnChange = new NeonButton("CAMBIAR RUTA 🔄");
        btnChange.setNeonColor(ThemeConstants.NEON_BLUE);
        btnChange.setPreferredSize(new Dimension(150, 40));
        btnChange.addActionListener(e -> changeDbPath());
        content.add(btnChange, BorderLayout.EAST);

        p.add(content, BorderLayout.CENTER);

        JLabel lblHint = new JLabel("⚠️ Asegúrese de respaldar su archivo db.sqlite antes de moverlo.");
        lblHint.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblHint.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.ITALIC));
        p.add(lblHint, BorderLayout.SOUTH);

        return p;
    }

    private JPanel createInfoPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new GridLayout(4, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        p.add(createDataRow("🚀 Versión del Software:", "1.2.0 (Neon Edition)"));
        p.add(createDataRow("💾 Motor de Base de Datos:", "SQLite 3.46"));
        p.add(createDataRow("📜 Licencia:", "Pago Único (Vortex ERP)"));
        p.add(createDataRow("👨‍💻 Desarrollador:", "ChopCode Solutions - Proyectos ERP"));

        return p;
    }

    private JPanel createDataRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblLabel.setFont(ThemeConstants.FONT_BODY);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(ThemeConstants.NEON_PURPLE);
        lblValue.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD));
        
        row.add(lblLabel, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.EAST);
        return row;
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
