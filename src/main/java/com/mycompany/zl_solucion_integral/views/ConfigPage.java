package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.config.LicenciaManager;
import com.mycompany.zl_solucion_integral.config.SelecionRuta;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.mycompany.zl_solucion_integral.views.components.dialogs.LicenciaDialog;
import com.mycompany.zl_solucion_integral.views.components.dialogs.ManualUsuarioDialog;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Vista principal de Configuración del Sistema de ERP+ Business.
 * Presenta el almacenamiento de BD, la gestión de licencias criptográficas,
 * acceso directo al Manual de Usuario y la telemetría del sistema con estética Neón Cyberpunk.
 */
public class ConfigPage extends JPanel {

    private JTextField txtDbPath;

    public ConfigPage() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));

        // Panel Principal dentro de ScrollPane para navegación fluida
        JPanel scrollContent = new JPanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(new EmptyBorder(10, 20, 20, 20));

        // 1. Tarjeta: Base de Datos
        JPanel dbCard = createDbConfigPanel();
        dbCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(dbCard);
        scrollContent.add(Box.createVerticalStrut(18));

        // 2. Tarjeta: Licencia y Activación
        JPanel licenseCard = createLicensePanel();
        licenseCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(licenseCard);
        scrollContent.add(Box.createVerticalStrut(18));

        // 3. Tarjeta: Manual de Usuario & Centro de Ayuda
        JPanel manualCard = createManualHelpPanel();
        manualCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(manualCard);
        scrollContent.add(Box.createVerticalStrut(18));

        // 4. Tarjeta: Información del Sistema
        JPanel infoCard = createInfoPanel();
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(infoCard);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createDbConfigPanel() {
        RoundedPanel p = new RoundedPanel(18, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout(0, 14));
        p.setBorder(new EmptyBorder(20, 22, 20, 22));

        // Header Tarjeta
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Base de Datos Local", createIcon("icons/database.svg", ThemeConstants.NEON_BLUE, 20, 20), SwingConstants.LEFT);
        lblTitle.setForeground(ThemeConstants.NEON_BLUE);
        lblTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        lblTitle.setIconTextGap(10);

        JLabel badgeStatus = createBadge("SQLITE (WAL)", ThemeConstants.NEON_BLUE);

        cardHeader.add(lblTitle, BorderLayout.WEST);
        cardHeader.add(badgeStatus, BorderLayout.EAST);
        p.add(cardHeader, BorderLayout.NORTH);

        // Body Tarjeta (Input + Botón)
        JPanel contentRow = new JPanel(new BorderLayout(14, 0));
        contentRow.setOpaque(false);

        txtDbPath = new JTextField(SelecionRuta.cargarRutaBaseDatos());
        txtDbPath.setEditable(false);
        txtDbPath.setPreferredSize(new Dimension(0, 42));
        txtDbPath.setBackground(ThemeConstants.INPUT_BACKGROUND);
        txtDbPath.setForeground(ThemeConstants.TEXT_PRIMARY);
        txtDbPath.setFont(ThemeConstants.FONT_BODY);
        txtDbPath.setCaretColor(ThemeConstants.NEON_BLUE);

        JLabel icon = new JLabel(createIcon("icons/folder.svg", ThemeConstants.TEXT_SECONDARY, 16, 16));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 8));
        txtDbPath.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, icon);

        txtDbPath.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        contentRow.add(txtDbPath, BorderLayout.CENTER);

        NeonButton btnCambiarRuta = new NeonButton("Cambiar Carpeta");
        btnCambiarRuta.setNeonColor(ThemeConstants.NEON_BLUE);
        btnCambiarRuta.setIcon(createIcon("icons/folder.svg", ThemeConstants.NEON_BLUE, 16, 16));
        btnCambiarRuta.setIconTextGap(8);
        btnCambiarRuta.setPreferredSize(new Dimension(170, 42));
        btnCambiarRuta.addActionListener(e -> changeDbPath());
        btnCambiarRuta.setToolTipText("Selecciona una nueva carpeta de almacenamiento para la base de datos.");
        contentRow.add(btnCambiarRuta, BorderLayout.EAST);

        p.add(contentRow, BorderLayout.CENTER);

        // Footer Hint
        JLabel lblHint = new JLabel("💡 Nota: Respalda db.db antes de mover la ruta de almacenamiento. Requiere reiniciar la aplicación.");
        lblHint.setFont(ThemeConstants.FONT_SMALL);
        lblHint.setForeground(ThemeConstants.TEXT_SECONDARY);
        p.add(lblHint, BorderLayout.SOUTH);

        return p;
    }

    private JPanel createLicensePanel() {
        RoundedPanel p = new RoundedPanel(18, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout(0, 14));
        p.setBorder(new EmptyBorder(20, 22, 20, 22));

        // Header Tarjeta
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Licencia & Activación de Software", createIcon("icons/license.svg", ThemeConstants.NEON_GREEN, 20, 20), SwingConstants.LEFT);
        lblTitle.setForeground(ThemeConstants.NEON_GREEN);
        lblTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        lblTitle.setIconTextGap(10);

        LicenciaManager.InfoLicencia info = LicenciaManager.obtenerInfoLicencia();
        boolean isPro = info.getEstado() == LicenciaManager.EstadoLicencia.PRO_ACTIVA;
        JLabel badgeStatus = createBadge(isPro ? "PRO ACTIVA" : "DEMO (" + info.getDiasRestantes() + " DÍAS)", isPro ? ThemeConstants.NEON_GREEN : ThemeConstants.NEON_CYAN);

        cardHeader.add(lblTitle, BorderLayout.WEST);
        cardHeader.add(badgeStatus, BorderLayout.EAST);
        p.add(cardHeader, BorderLayout.NORTH);

        // Body Tarjeta
        JPanel contentRow = new JPanel(new BorderLayout(14, 0));
        contentRow.setOpaque(false);

        String desc = "Estado de Licencia: " + info.getEstado().getDescripcion() +
                (isPro ? " — Registrado a: " + info.getCliente() : " — Período de evaluación de 30 días.");
        JLabel lblInfo = new JLabel(desc);
        lblInfo.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblInfo.setFont(ThemeConstants.FONT_BODY);
        contentRow.add(lblInfo, BorderLayout.CENTER);

        NeonButton btnLicencia = new NeonButton("Administrar Licencia");
        btnLicencia.setNeonColor(ThemeConstants.NEON_GREEN);
        btnLicencia.setIcon(createIcon("icons/license.svg", ThemeConstants.NEON_GREEN, 16, 16));
        btnLicencia.setIconTextGap(8);
        btnLicencia.setPreferredSize(new Dimension(200, 42));
        btnLicencia.addActionListener(e -> {
            Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            LicenciaDialog dialog = new LicenciaDialog(topFrame, () -> {
                // Refresh al actualizar licencia
                removeAll();
                add(new ConfigPage(), BorderLayout.CENTER);
                revalidate();
                repaint();
            });
            dialog.setVisible(true);
        });
        contentRow.add(btnLicencia, BorderLayout.EAST);

        p.add(contentRow, BorderLayout.CENTER);
        return p;
    }

    private JPanel createManualHelpPanel() {
        RoundedPanel p = new RoundedPanel(18, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout(0, 14));
        p.setBorder(new EmptyBorder(20, 22, 20, 22));

        // Header Tarjeta
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Manual de Usuario & Centro de Ayuda", createIcon("icons/manual.svg", ThemeConstants.NEON_PURPLE, 20, 20), SwingConstants.LEFT);
        lblTitle.setForeground(ThemeConstants.NEON_PURPLE);
        lblTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        lblTitle.setIconTextGap(10);

        JLabel badgeStatus = createBadge("DOCUMENTACIÓN OFICIAL", ThemeConstants.NEON_PURPLE);

        cardHeader.add(lblTitle, BorderLayout.WEST);
        cardHeader.add(badgeStatus, BorderLayout.EAST);
        p.add(cardHeader, BorderLayout.NORTH);

        // Body Tarjeta
        JPanel contentRow = new JPanel(new BorderLayout(14, 0));
        contentRow.setOpaque(false);

        JLabel lblInfo = new JLabel("Consulta guías interactivas, preguntas frecuentes y procedimientos operativos sin salir de la app.");
        lblInfo.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblInfo.setFont(ThemeConstants.FONT_BODY);
        contentRow.add(lblInfo, BorderLayout.CENTER);

        NeonButton btnManual = new NeonButton("Abrir Manual de Usuario");
        btnManual.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnManual.setIcon(createIcon("icons/manual.svg", ThemeConstants.NEON_PURPLE, 16, 16));
        btnManual.setIconTextGap(8);
        btnManual.setPreferredSize(new Dimension(220, 42));
        btnManual.addActionListener(e -> {
            Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            ManualUsuarioDialog dialog = new ManualUsuarioDialog(topFrame);
            dialog.setVisible(true);
        });
        contentRow.add(btnManual, BorderLayout.EAST);

        p.add(contentRow, BorderLayout.CENTER);
        return p;
    }

    private JPanel createInfoPanel() {
        RoundedPanel p = new RoundedPanel(18, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout(0, 16));
        p.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel lblTitle = new JLabel("Información y Telemetría del Sistema", createIcon("icons/info.svg", ThemeConstants.NEON_CYAN, 20, 20), SwingConstants.LEFT);
        lblTitle.setForeground(ThemeConstants.NEON_CYAN);
        lblTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        lblTitle.setIconTextGap(10);
        p.add(lblTitle, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        gridPanel.setOpaque(false);

        gridPanel.add(createDataRow("Versión del Software", "2.0.0 (Enterprise Major)", "icons/version-code.svg", ThemeConstants.NEON_PURPLE));
        gridPanel.add(createDataRow("Motor de Base de Datos", "SQLite 3.46 (WAL Mode)", "icons/database.svg", ThemeConstants.NEON_BLUE));
        gridPanel.add(createDataRow("Licenciamiento Criptográfico", "RSA-2048 Hardware-Bound", "icons/license.svg", ThemeConstants.NEON_GREEN));
        gridPanel.add(createDeveloperRow());

        p.add(gridPanel, BorderLayout.CENTER);
        return p;
    }

    private JPanel createDataRow(String label, String value, String iconPath, Color iconColor) {
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(iconColor);
        valueLabel.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD));
        return createDataRow(label, valueLabel, iconPath, iconColor);
    }

    private JPanel createDataRow(String label, Component valueComp, String iconPath, Color iconColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeConstants.CARD_BORDER),
                new EmptyBorder(4, 0, 8, 0)
        ));

        JLabel icon = new JLabel(createIcon(iconPath, iconColor, 18, 18));
        icon.setBorder(new EmptyBorder(0, 0, 0, 10));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblLabel.setFont(ThemeConstants.FONT_BODY);

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        labelPanel.add(icon);
        labelPanel.add(lblLabel);

        row.add(labelPanel, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);
        return row;
    }

    private JPanel createDeveloperRow() {
        JLabel link = new JLabel("<html><u>ChopCode Solutions</u> ↗</html>");
        link.setForeground(ThemeConstants.NEON_CYAN);
        link.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD));
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.setToolTipText("Abrir el portafolio oficial de ChopCode Solutions");
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(java.net.URI.create("https://portafolio-brandon-daza.web.app/"));
                } catch (Exception ex) {
                    UIUtils.showError(ConfigPage.this, "No se pudo abrir el navegador web.");
                }
            }
        });
        return createDataRow("Desarrollador Oficial", link, "icons/developer.svg", ThemeConstants.NEON_CYAN);
    }

    private JLabel createBadge(String text, Color neonColor) {
        JLabel badge = new JLabel(text);
        badge.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        badge.setForeground(neonColor);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(neonColor, 1),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        return badge;
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
            UIUtils.showInfo(this, "Ruta Actualizada", "La configuración de almacenamiento ha sido guardada. Reinicia la aplicación para aplicar los cambios.");
        }
    }

    private void guardarRutaEnConfig(String ruta) {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            System.out.println("Creando nuevo archivo config.properties...");
        }

        try (FileOutputStream output = new FileOutputStream("config.properties")) {
            props.setProperty("db.path", ruta);
            props.store(output, "Configuración de la Base de Datos - ERP+ Business");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
