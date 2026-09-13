package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.config.SelecionRuta;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
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
    private JPanel contentDb;
    private NeonButton btnCambiarRuta;
    /** Filas "etiqueta / valor" del panel de informacion, para reflow en movil. */
    private final java.util.List<JPanel> filasDatos = new java.util.ArrayList<>();

    public ConfigPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header (microcopy de contexto)
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JPanel titleRow = UIUtils.createWrappingTitleRow("icons/settings.svg",
                ThemeConstants.NEON_PURPLE, 24, "Configuración del sistema",
                ThemeConstants.FONT_TITLE, ThemeConstants.TEXT_PRIMARY);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea subtitle = UIUtils.createWrappingLabel(
                "Ajusta el almacenamiento y conoce los detalles técnicos de tu instalación",
                ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(titleRow);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

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

        // Adaptabilidad: en movil las filas "etiqueta / valor" se apilan.
        LayoutResponsive.listen(this, bp -> {
            boolean movil = LayoutResponsive.esColumnaUnica(bp);
            for (JPanel row : filasDatos) {
                aplicarFilaResponsive(row, movil);
            }
            if (contentDb != null) {
                contentDb.setLayout(movil
                        ? new BorderLayout(0, 10)
                        : new BorderLayout(10, 10));
                contentDb.removeAll();
                if (movil) {
                    contentDb.add(txtDbPath, BorderLayout.NORTH);
                    contentDb.add(btnCambiarRuta, BorderLayout.CENTER);
                } else {
                    contentDb.add(txtDbPath, BorderLayout.CENTER);
                    contentDb.add(btnCambiarRuta, BorderLayout.EAST);
                }
                contentDb.revalidate();
                contentDb.repaint();
            }
            revalidate();
            repaint();
        });
    }

    /** En movil, el valor de una fila pasa debajo de su etiqueta. */
    private void aplicarFilaResponsive(JPanel row, boolean movil) {
        if (row.getComponentCount() < 2) {
            return;
        }
        Component labelPanel = row.getComponent(0);
        Component valueComp = row.getComponent(1);
        row.removeAll();
        row.setLayout(movil ? new GridLayout(2, 1, 0, 2) : new BorderLayout());
        row.add(labelPanel, movil ? null : BorderLayout.WEST);
        row.add(valueComp, movil ? null : BorderLayout.EAST);
        row.revalidate();
        row.repaint();
    }

    private JPanel createDbConfigPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout(15, 15));
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel lblTitle = new JLabel("Base de datos", createIcon("icons/database.svg", ThemeConstants.NEON_BLUE, 20, 20), SwingConstants.LEFT);
        lblTitle.setForeground(ThemeConstants.NEON_BLUE);
        lblTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        p.add(lblTitle, BorderLayout.NORTH);

        contentDb = new JPanel(new BorderLayout(10, 10));
        contentDb.setOpaque(false);
        JPanel content = contentDb;

        txtDbPath = new JTextField(SelecionRuta.cargarRutaBaseDatos());
        txtDbPath.setEditable(false);
        txtDbPath.setBackground(ThemeConstants.INPUT_BACKGROUND);
        txtDbPath.setForeground(ThemeConstants.TEXT_SECONDARY);

        JLabel icon = new JLabel(createIcon("icons/database.svg", ThemeConstants.TEXT_SECONDARY, 17, 17));
        icon.setForeground(ThemeConstants.TEXT_SECONDARY);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        txtDbPath.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, icon);

        txtDbPath.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        content.add(txtDbPath, BorderLayout.CENTER);

        btnCambiarRuta = new NeonButton("Cambiar ruta");
        btnCambiarRuta.setNeonColor(ThemeConstants.NEON_BLUE);
        btnCambiarRuta.setIcon(createIcon("icons/settings.svg", ThemeConstants.NEON_BLUE, 17, 17));
        btnCambiarRuta.setIconTextGap(8);
        btnCambiarRuta.setPreferredSize(new Dimension(150, ThemeConstants.TOUCH_TARGET_MIN));
        btnCambiarRuta.setMinimumSize(new Dimension(0, ThemeConstants.TOUCH_TARGET_MIN));
        btnCambiarRuta.addActionListener(e -> changeDbPath());
        btnCambiarRuta.setToolTipText("Selecciona una nueva carpeta donde se guardará la base de datos. Requiere reiniciar la aplicación.");
        content.add(btnCambiarRuta, BorderLayout.EAST);

        p.add(content, BorderLayout.CENTER);

        JTextArea lblHint = UIUtils.createWrappingLabel(
                "Respalde db.db antes de mover la ruta de almacenamiento.",
                ThemeConstants.FONT_SMALL.deriveFont(Font.ITALIC), ThemeConstants.TEXT_SECONDARY);
        p.add(lblHint, BorderLayout.SOUTH);

        return p;
    }

    private JPanel createInfoPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new GridLayout(4, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        p.add(createDataRow("Versión del software", "1.3.0", "icons/version-code.svg", ThemeConstants.NEON_PURPLE));
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
        filasDatos.add(row);

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
            UIUtils.showInfo(this, "Guardado", "Configuración guardada. Reinicie la aplicación para aplicar cambios.");
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
