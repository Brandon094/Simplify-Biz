package com.mycompany.zl_solucion_integral.views.components.organisms;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.controllers.CarteraController;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Diálogo modal para consultar el historial completo de abonos recibidos para una venta.
 */
public class HistorialAbonosDialog extends JDialog {

    private final CarteraController carteraCtrl = new CarteraController();

    public HistorialAbonosDialog(Window owner, int ventaId, String cliente, double totalVenta) {
        super(owner, "Historial de Abonos — Venta #" + ventaId, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(550, 400));

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBackground(ThemeConstants.CARD_BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Historial de Recaudos");
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_SUBTITLE);

        JLabel info = new JLabel(String.format("Cliente: %s | Total Venta: %s", cliente, UIUtils.formatCurrency(totalVenta)));
        info.setForeground(ThemeConstants.NEON_BLUE);
        info.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(info);

        content.add(headerPanel, BorderLayout.NORTH);

        // Tabla de Abonos
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Id");
        model.addColumn("Fecha");
        model.addColumn("Monto ($)");
        model.addColumn("Método de Pago");
        model.addColumn("Observación");

        List<Object[]> historial = carteraCtrl.obtenerHistorialAbonos(ventaId);
        double totalAbonado = 0.0;
        for (Object[] fila : historial) {
            model.addRow(fila);
            if (fila[2] instanceof Number) {
                totalAbonado += ((Number) fila[2]).doubleValue();
            }
        }

        JTable table = new JTable(model);
        UIUtils.applyTableStyling(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        content.add(scroll, BorderLayout.CENTER);

        // Footer con total recaudado y botón de cerrar
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel lblTotalRecaudado = new JLabel(String.format("Total Acumulado Abonado: %s", UIUtils.formatCurrency(totalAbonado)));
        lblTotalRecaudado.setForeground(ThemeConstants.NEON_GREEN);
        lblTotalRecaudado.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD));

        FlatSVGIcon closeIcon = new FlatSVGIcon("icons/xmark.svg", 16, 16);
        closeIcon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.TEXT_SECONDARY));

        NeonButton btnCerrar = new NeonButton("Cerrar");
        btnCerrar.setIcon(closeIcon);
        btnCerrar.setIconTextGap(6);
        btnCerrar.setPreferredSize(new Dimension(110, ThemeConstants.TOUCH_TARGET_MIN));
        btnCerrar.addActionListener(e -> dispose());

        footer.add(lblTotalRecaudado, BorderLayout.WEST);
        footer.add(btnCerrar, BorderLayout.EAST);

        content.add(footer, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setLocationRelativeTo(owner);
    }
}
