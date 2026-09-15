package com.mycompany.zl_solucion_integral.views.components.organisms;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.config.Validaciones;
import com.mycompany.zl_solucion_integral.controllers.CarteraController;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal atómico estilizado para registrar un abono a una venta a crédito.
 */
public class RegistrarAbonoDialog extends JDialog {

    private final CarteraController carteraCtrl = new CarteraController();
    private final int ventaId;
    private final double saldoPendiente;
    private final Runnable onSuccessCallback;

    private JTextField txtMonto;
    private JComboBox<String> cmbMetodoPago;
    private JTextField txtObservacion;

    public RegistrarAbonoDialog(Window owner, int ventaId, String cliente, double totalVenta, double saldoPendiente, Runnable onSuccessCallback) {
        super(owner, "Registrar Abono — Venta #" + ventaId, ModalityType.APPLICATION_MODAL);
        this.ventaId = ventaId;
        this.saldoPendiente = saldoPendiente;
        this.onSuccessCallback = onSuccessCallback;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBackground(ThemeConstants.CARD_BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Ingresar recaudo de abono");
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_SUBTITLE);

        JLabel info = new JLabel(String.format("Cliente: %s | Saldo Deuda: %s", cliente, UIUtils.formatCurrency(saldoPendiente)));
        info.setForeground(ThemeConstants.NEON_BLUE);
        info.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(info);

        content.add(headerPanel, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.gridx = 0; gbc.weightx = 1.0;

        int row = 0;

        // Monto
        gbc.gridy = row++;
        formPanel.add(createLabel("MONTO DEL ABONO ($)"), gbc);

        txtMonto = createTextField(String.format("%.0f", saldoPendiente));
        txtMonto.setPreferredSize(new Dimension(320, ThemeConstants.TOUCH_TARGET_MIN));
        gbc.gridy = row++;
        formPanel.add(txtMonto, gbc);

        // Método de pago
        gbc.gridy = row++;
        formPanel.add(createLabel("MÉTODO DE PAGO"), gbc);

        cmbMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Transferencia"});
        cmbMetodoPago.setPreferredSize(new Dimension(320, ThemeConstants.TOUCH_TARGET_MIN));
        cmbMetodoPago.setBackground(ThemeConstants.INPUT_BACKGROUND);
        cmbMetodoPago.setForeground(ThemeConstants.TEXT_PRIMARY);
        gbc.gridy = row++;
        formPanel.add(cmbMetodoPago, gbc);

        // Observaciones
        gbc.gridy = row++;
        formPanel.add(createLabel("OBSERVACIÓN / COMPROBANTE (OPCIONAL)"), gbc);

        txtObservacion = createTextField("ej. Comprobante Nequi #12345");
        txtObservacion.setPreferredSize(new Dimension(320, ThemeConstants.TOUCH_TARGET_MIN));
        gbc.gridy = row++;
        formPanel.add(txtObservacion, gbc);

        content.add(formPanel, BorderLayout.CENTER);

        // Botones de acción
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        FlatSVGIcon cancelIcon = new FlatSVGIcon("icons/xmark.svg", 16, 16);
        cancelIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.TEXT_SECONDARY));

        NeonButton btnCancelar = new NeonButton("Cancelar");
        btnCancelar.setIcon(cancelIcon);
        btnCancelar.setIconTextGap(6);
        btnCancelar.setPreferredSize(new Dimension(120, ThemeConstants.TOUCH_TARGET_MIN));
        btnCancelar.addActionListener(e -> dispose());

        FlatSVGIcon confirmIcon = new FlatSVGIcon("icons/check-double.svg", 16, 16);
        confirmIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_GREEN));

        NeonButton btnConfirmar = new NeonButton("Confirmar Abono");
        btnConfirmar.setNeonColor(ThemeConstants.NEON_GREEN);
        btnConfirmar.setIcon(confirmIcon);
        btnConfirmar.setIconTextGap(6);
        btnConfirmar.setPreferredSize(new Dimension(180, ThemeConstants.TOUCH_TARGET_MIN));
        btnConfirmar.addActionListener(e -> procesarAbono());

        actions.add(btnCancelar);
        actions.add(btnConfirmar);

        content.add(actions, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setLocationRelativeTo(owner);
    }

    private void procesarAbono() {
        Double monto = Validaciones.parseDecimalNoNegativo(txtMonto.getText());
        if (monto == null || monto <= 0) {
            UIUtils.showError(this, "Ingrese un monto válido y mayor a cero.");
            return;
        }

        if (monto > saldoPendiente + 0.01) {
            UIUtils.showError(this, String.format("El abono (%s) supera el saldo pendiente (%s).",
                    UIUtils.formatCurrency(monto), UIUtils.formatCurrency(saldoPendiente)));
            return;
        }

        String metodo = (String) cmbMetodoPago.getSelectedItem();
        String obs = txtObservacion.getText();

        ResultadoOperacion res = carteraCtrl.registrarAbono(ventaId, monto, metodo, obs);
        if (res.esExito()) {
            UIUtils.showSuccess(this, res.getMensaje());
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            dispose();
        } else {
            UIUtils.showError(this, res.getMensaje());
        }
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(ThemeConstants.TEXT_SECONDARY);
        l.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 11f));
        return l;
    }

    private JTextField createTextField(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.setBackground(ThemeConstants.INPUT_BACKGROUND);
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_GREEN);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return f;
    }
}
