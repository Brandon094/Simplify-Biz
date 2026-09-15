package com.mycompany.zl_solucion_integral.views.components.dialogs;

import com.mycompany.zl_solucion_integral.config.HardwareUtils;
import com.mycompany.zl_solucion_integral.config.LicenciaManager;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * Diálogo modal para la gestión del estado de la licencia y activación criptográfica por Hardware ID.
 */
public class LicenciaDialog extends JDialog {
    private final Runnable onLicenciaActualizada;
    private JTextField txtHwId;
    private JTextField txtClaveLicencia;
    private JLabel lblEstadoBadge;
    private JLabel lblDetalleLicencia;

    public LicenciaDialog(Frame parent, Runnable onLicenciaActualizada) {
        super(parent, "Activación & Licencia de Software", true);
        this.onLicenciaActualizada = onLicenciaActualizada;

        setSize(600, 580);
        setMinimumSize(new Dimension(560, 540));
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConstants.BACKGROUND);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(22, 24, 22, 24));
        add(root, BorderLayout.CENTER);

        // Header
        JPanel header = UIUtils.createHeader("icons/license.svg", ThemeConstants.NEON_PURPLE,
                "Activación de Licencia ERP+",
                "Gestión del estado de licencia y activación por Hardware ID.");
        root.add(header, BorderLayout.NORTH);

        // Contenido Principal
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // 1. Hardware ID Card
        JPanel cardHw = new RoundedPanel(14, ThemeConstants.CARD_BACKGROUND);
        cardHw.setLayout(new BorderLayout(10, 8));
        cardHw.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lblHwTitle = new JLabel("IDENTIFICADOR DE MÁQUINA (HARDWARE ID)");
        lblHwTitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblHwTitle.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 10f));

        String hwId = HardwareUtils.getHardwareId();
        txtHwId = new JTextField(hwId);
        txtHwId.setEditable(false);
        txtHwId.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD, 14f));
        txtHwId.setForeground(ThemeConstants.NEON_PURPLE);
        txtHwId.setBackground(ThemeConstants.INPUT_BACKGROUND);
        txtHwId.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        NeonButton btnCopiar = new NeonButton("Copiar ID");
        btnCopiar.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnCopiar.setIcon(createIcon("icons/id.svg", ThemeConstants.NEON_PURPLE, 16, 16));
        btnCopiar.setIconTextGap(6);
        btnCopiar.setPreferredSize(new Dimension(120, 36));
        btnCopiar.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(hwId), null);
            UIUtils.showInfo(this, "Copiado", "Hardware ID copiado al portapapeles:\n" + hwId);
        });

        JPanel hwCenter = new JPanel(new BorderLayout(10, 0));
        hwCenter.setOpaque(false);
        hwCenter.add(txtHwId, BorderLayout.CENTER);
        hwCenter.add(btnCopiar, BorderLayout.EAST);

        cardHw.add(lblHwTitle, BorderLayout.NORTH);
        cardHw.add(hwCenter, BorderLayout.CENTER);
        content.add(cardHw);

        content.add(Box.createVerticalStrut(14));

        // 2. Estado de Licencia Actual
        JPanel cardEstado = new RoundedPanel(14, ThemeConstants.CARD_BACKGROUND);
        cardEstado.setLayout(new BorderLayout(10, 6));
        cardEstado.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lblEstTitle = new JLabel("ESTADO DE LA LICENCIA");
        lblEstTitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblEstTitle.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 10f));

        lblEstadoBadge = new JLabel();
        lblEstadoBadge.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD, 14f));

        lblDetalleLicencia = new JLabel();
        lblDetalleLicencia.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblDetalleLicencia.setFont(ThemeConstants.FONT_SMALL);

        cardEstado.add(lblEstTitle, BorderLayout.NORTH);
        cardEstado.add(lblEstadoBadge, BorderLayout.CENTER);
        cardEstado.add(lblDetalleLicencia, BorderLayout.SOUTH);
        content.add(cardEstado);

        content.add(Box.createVerticalStrut(14));

        // 3. Campo de Entrada de Clave Token
        JPanel cardToken = new RoundedPanel(14, ThemeConstants.CARD_BACKGROUND);
        cardToken.setLayout(new BorderLayout(10, 8));
        cardToken.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lblClaveTitle = new JLabel("INGRESAR CLAVE DE ACTIVACIÓN / TOKEN");
        lblClaveTitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblClaveTitle.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 10f));

        txtClaveLicencia = new JTextField();
        txtClaveLicencia.setFont(ThemeConstants.FONT_BODY);
        txtClaveLicencia.setForeground(ThemeConstants.TEXT_PRIMARY);
        txtClaveLicencia.setBackground(ThemeConstants.INPUT_BACKGROUND);
        txtClaveLicencia.setCaretColor(ThemeConstants.NEON_GREEN);
        
        FlatSVGIcon keyIcon = new FlatSVGIcon("icons/license.svg", 16, 16);
        keyIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.TEXT_SECONDARY));
        JLabel lblKeyIcon = new JLabel(keyIcon);
        lblKeyIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        txtClaveLicencia.putClientProperty(com.formdev.flatlaf.FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, lblKeyIcon);

        txtClaveLicencia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtClaveLicencia.setToolTipText("Pega aquí tu token o clave enviada por el emisor.");

        cardToken.add(lblClaveTitle, BorderLayout.NORTH);
        cardToken.add(txtClaveLicencia, BorderLayout.CENTER);
        content.add(cardToken);

        root.add(content, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setOpaque(false);

        NeonButton btnCerrar = new NeonButton("Cerrar");
        btnCerrar.setNeonColor(ThemeConstants.TEXT_SECONDARY);
        btnCerrar.setIcon(createIcon("icons/xmark.svg", ThemeConstants.TEXT_SECONDARY, 16, 16));
        btnCerrar.setIconTextGap(6);
        btnCerrar.setPreferredSize(new Dimension(110, 40));
        btnCerrar.addActionListener(e -> dispose());

        NeonButton btnActivar = new NeonButton("Activar Licencia");
        btnActivar.setNeonColor(ThemeConstants.NEON_GREEN);
        btnActivar.setIcon(createIcon("icons/check-double.svg", ThemeConstants.NEON_GREEN, 16, 16));
        btnActivar.setIconTextGap(6);
        btnActivar.setPreferredSize(new Dimension(170, 40));
        btnActivar.addActionListener(e -> ejecutarActivacion());

        footer.add(btnCerrar);
        footer.add(btnActivar);
        root.add(footer, BorderLayout.SOUTH);

        actualizarEstadoVisual();
    }

    private void actualizarEstadoVisual() {
        LicenciaManager.InfoLicencia info = LicenciaManager.obtenerInfoLicencia();
        lblEstadoBadge.setText(info.getEstado().getDescripcion());

        if (info.getEstado() == LicenciaManager.EstadoLicencia.PRO_ACTIVA) {
            lblEstadoBadge.setForeground(ThemeConstants.NEON_GREEN);
            lblDetalleLicencia.setText("Registrado a: " + info.getCliente() + " • Vence: " + info.getFechaExpiracion());
        } else if (info.getEstado() == LicenciaManager.EstadoLicencia.DEMO_ACTIVA) {
            lblEstadoBadge.setForeground(ThemeConstants.NEON_CYAN);
            lblDetalleLicencia.setText("Uso en evaluación de prueba • Quedan " + info.getDiasRestantes() + " días.");
        } else {
            lblEstadoBadge.setForeground(ThemeConstants.NEON_RED);
            lblDetalleLicencia.setText("Se requiere activación para continuar operando el sistema.");
        }
    }

    private void ejecutarActivacion() {
        String token = txtClaveLicencia.getText().trim();
        ResultadoOperacion res = LicenciaManager.activarLicencia(token);
        if (res.esExito()) {
            UIUtils.showSuccess(this, res.getMensaje());
            actualizarEstadoVisual();
            if (onLicenciaActualizada != null) onLicenciaActualizada.run();
        } else {
            UIUtils.showError(this, res.getMensaje());
        }
    }

    private FlatSVGIcon createIcon(String path, Color color, int w, int h) {
        FlatSVGIcon icon = new FlatSVGIcon(path, w, h);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, color));
        return icon;
    }
}
