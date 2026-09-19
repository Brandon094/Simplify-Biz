package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.CarteraController;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.mycompany.zl_solucion_integral.views.components.organisms.HistorialAbonosDialog;
import com.mycompany.zl_solucion_integral.views.components.organisms.RegistrarAbonoDialog;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;

/**
 * Módulo de Cartera y Cuentas por Cobrar (CxC).
 * Permite gestionar deudores, visualizar saldos pendientes, registrar abonos y consultar historial de pagos.
 */
public class CarteraPage extends JPanel {

    private final CarteraController carteraCtrl = new CarteraController();

    private JTable tbCartera;
    private JScrollPane scrollTable;
    private JTextField txtSearch;
    private JPanel summaryPanel, actionPanel;
    private NeonButton btnAbonar, btnHistorial, btnSearch;

    // Métricas
    private JLabel lblValCarteraPendiente, lblValRecaudadoMes, lblValDeudoresActivos;

    public CarteraPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        // Contenido Principal
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setOpaque(false);

        // Contenedor Norte: Tarjetas KPI + Barra de Búsqueda
        JPanel topContainer = new JPanel();
        topContainer.setOpaque(false);
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        summaryPanel = createSummaryPanel();
        JPanel searchPanel = createSearchPanel();

        topContainer.add(summaryPanel);
        topContainer.add(Box.createVerticalStrut(15));
        topContainer.add(searchPanel);

        mainContent.add(topContainer, BorderLayout.NORTH);

        // Centro: Tabla de Cartera
        mainContent.add(createTablePanel(), BorderLayout.CENTER);

        // Sur: Botones de Acción (Abonar / Historial)
        actionPanel = createActionPanel();
        mainContent.add(actionPanel, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);

        // Breakpoint Responsive
        LayoutResponsive.listenWidth(this, (bp, ancho) -> aplicarBreakpoint(bp));

        refreshData();
    }

    private void aplicarBreakpoint(LayoutResponsive.Breakpoint bp) {
        boolean movil = LayoutResponsive.esColumnaUnica(bp);
        if (summaryPanel != null) {
            summaryPanel.setLayout(movil ? new GridLayout(3, 1, 10, 10) : new GridLayout(1, 3, 15, 15));
            summaryPanel.revalidate();
            summaryPanel.repaint();
        }
        if (actionPanel != null) {
            actionPanel.setLayout(movil ? new GridLayout(2, 1, 0, 10) : new GridLayout(1, 2, 20, 0));
            actionPanel.revalidate();
            actionPanel.repaint();
        }
        revalidate();
        repaint();
    }

    private JPanel createSummaryPanel() {
        JPanel container = new JPanel(new GridLayout(1, 3, 15, 15));
        container.setOpaque(false);

        lblValCarteraPendiente = new JLabel("$0");
        container.add(createSummaryCard("TOTAL CARTERA PENDIENTE", lblValCarteraPendiente, ThemeConstants.NEON_AMBER, "icons/wallet.svg"));

        lblValRecaudadoMes = new JLabel("$0");
        container.add(createSummaryCard("RECAUDADO EN ABONOS (MES)", lblValRecaudadoMes, ThemeConstants.NEON_GREEN, "icons/check-double.svg"));

        lblValDeudoresActivos = new JLabel("0 Clientes");
        container.add(createSummaryCard("DEUDORES ACTIVOS", lblValDeudoresActivos, ThemeConstants.NEON_BLUE, "icons/clients.svg"));

        return container;
    }

    private JPanel createSummaryCard(String titleText, JLabel valLabel, Color accentColor, String iconPath) {
        RoundedPanel card = new RoundedPanel(18, ThemeConstants.CARD_BACKGROUND);
        card.setLayout(new BorderLayout(8, 4));
        card.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel(titleText.toUpperCase());
        title.setForeground(ThemeConstants.TEXT_SECONDARY);
        title.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 10f));

        JLabel iconLbl = new JLabel(createIcon(iconPath, accentColor, 20, 20));
        top.add(title, BorderLayout.CENTER);
        top.add(iconLbl, BorderLayout.EAST);

        valLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        valLabel.setFont(ThemeConstants.FONT_TITLE.deriveFont(Font.BOLD, 18f));

        card.add(top, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSearchPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Filtrar por Nombre de Cliente o Cédula / NIT...");
        txtSearch.setBackground(ThemeConstants.INPUT_BACKGROUND);
        txtSearch.setForeground(ThemeConstants.TEXT_PRIMARY);
        txtSearch.setCaretColor(ThemeConstants.NEON_PURPLE);
        txtSearch.setPreferredSize(new Dimension(300, ThemeConstants.TOUCH_TARGET_MIN));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        setupFieldIcon(txtSearch, "icons/search.svg");
        UIUtils.fluid(txtSearch);

        btnSearch = new NeonButton("Filtrar cartera");
        btnSearch.setNeonColor(ThemeConstants.NEON_BLUE);
        btnSearch.setIcon(createIcon("icons/search.svg", ThemeConstants.NEON_BLUE, 17, 17));
        btnSearch.setIconTextGap(8);
        btnSearch.setPreferredSize(new Dimension(160, ThemeConstants.TOUCH_TARGET_MIN));
        btnSearch.addActionListener(e -> applyFilter());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridy = 0;

        gbc.gridx = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(txtSearch, gbc);

        gbc.gridx = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        p.add(btnSearch, gbc);

        return p;
    }

    private JPanel createTablePanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel tableTitle = new JLabel(" Ventas a crédito y saldos de cartera");
        tableTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        tableTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        try {
            FlatSVGIcon iconTable = new FlatSVGIcon("icons/wallet.svg", 18, 18);
            iconTable.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, ThemeConstants.NEON_PURPLE));
            tableTitle.setIcon(iconTable);
        } catch (Exception ignored) {}
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 8, 12, 8));
        p.add(tableTitle, BorderLayout.NORTH);

        tbCartera = new JTable();
        tbCartera.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        scrollTable = new JScrollPane(tbCartera);
        scrollTable.setOpaque(false);
        scrollTable.getViewport().setOpaque(false);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());

        p.add(scrollTable, BorderLayout.CENTER);
        return p;
    }

    private JPanel createActionPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 20, 0));
        p.setOpaque(false);

        btnAbonar = new NeonButton("Registrar Abono");
        btnAbonar.setNeonColor(ThemeConstants.NEON_GREEN);
        btnAbonar.setIcon(createIcon("icons/plus.svg", ThemeConstants.NEON_GREEN, 17, 17));
        btnAbonar.setIconTextGap(8);
        btnAbonar.addActionListener(e -> abrirDialogoAbonar());
        btnAbonar.setToolTipText("Registra un abono en dinero a la venta a crédito seleccionada");

        btnHistorial = new NeonButton("Ver Historial de Abonos");
        btnHistorial.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnHistorial.setIcon(createIcon("icons/history.svg", ThemeConstants.NEON_PURPLE, 17, 17));
        btnHistorial.setIconTextGap(8);
        btnHistorial.addActionListener(e -> abrirDialogoHistorial());
        btnHistorial.setToolTipText("Consulta la lista detallada de abonos recibidos para la venta seleccionada");

        p.add(btnAbonar);
        p.add(btnHistorial);

        return p;
    }

    private void applyFilter() {
        String query = txtSearch.getText().trim();
        carteraCtrl.obtenerVentasEnCartera(tbCartera, query);
        UIUtils.applyTableStyling(tbCartera);
        actualizarEstadoVacio();
    }

    private void refreshData() {
        carteraCtrl.obtenerVentasEnCartera(tbCartera, "");
        UIUtils.applyTableStyling(tbCartera);
        actualizarMétricas();
        actualizarEstadoVacio();
    }

    private void actualizarMétricas() {
        double[] res = carteraCtrl.obtenerResumenCartera();

        if (lblValCarteraPendiente != null) {
            lblValCarteraPendiente.setText(UIUtils.formatCompactCurrency(res[0]));
            lblValCarteraPendiente.setToolTipText("Valor total en cartera: " + UIUtils.formatCurrency(res[0]));
        }
        if (lblValRecaudadoMes != null) {
            lblValRecaudadoMes.setText(UIUtils.formatCompactCurrency(res[1]));
            lblValRecaudadoMes.setToolTipText("Recaudado este mes: " + UIUtils.formatCurrency(res[1]));
        }
        if (lblValDeudoresActivos != null) {
            lblValDeudoresActivos.setText(String.format("%.0f Clientes", res[2]));
        }
    }

    private void abrirDialogoAbonar() {
        int selectedRow = tbCartera.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showWarning(this, "Seleccione una venta a crédito de la tabla para registrar un abono.");
            return;
        }

        javax.swing.table.TableModel model = tbCartera.getModel();
        int ventaId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
        String cliente = model.getValueAt(selectedRow, 1).toString();
        double totalVenta = Double.parseDouble(model.getValueAt(selectedRow, 5).toString());
        double saldoPendiente = Double.parseDouble(model.getValueAt(selectedRow, 7).toString());

        if (saldoPendiente <= 0.01) {
            UIUtils.showInfo(this, "Deuda Saldada", "Esta venta ya se encuentra pagada al 100%. No requiere más abonos.");
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        RegistrarAbonoDialog dialog = new RegistrarAbonoDialog(parentWindow, ventaId, cliente, totalVenta, saldoPendiente, this::refreshData);
        dialog.setVisible(true);
    }

    private void abrirDialogoHistorial() {
        int selectedRow = tbCartera.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showWarning(this, "Seleccione una venta a crédito de la tabla para ver su historial de abonos.");
            return;
        }

        javax.swing.table.TableModel model = tbCartera.getModel();
        int ventaId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
        String cliente = model.getValueAt(selectedRow, 1).toString();
        double totalVenta = Double.parseDouble(model.getValueAt(selectedRow, 5).toString());

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        HistorialAbonosDialog dialog = new HistorialAbonosDialog(parentWindow, ventaId, cliente, totalVenta);
        dialog.setVisible(true);
    }

    private void actualizarEstadoVacio() {
        if (tbCartera.getModel() == null || tbCartera.getModel().getRowCount() == 0) {
            boolean hasFilter = !txtSearch.getText().trim().isEmpty();
            scrollTable.setViewportView(createEmptyState(hasFilter));
        } else {
            scrollTable.setViewportView(tbCartera);
        }
        scrollTable.revalidate();
        scrollTable.repaint();
    }

    private JPanel createEmptyState(boolean hasFilter) {
        if (hasFilter) {
            return UIUtils.createEmptyState(
                    "icons/reports.svg", ThemeConstants.NEON_PURPLE,
                    "No encontramos ventas a crédito con ese filtro",
                    "Prueba buscando por otro cliente o limpia la casilla de búsqueda.",
                    "Limpiar filtro",
                    () -> {
                        txtSearch.setText("");
                        refreshData();
                    });
        }
        return UIUtils.createEmptyState(
                "icons/check-double.svg", ThemeConstants.NEON_GREEN,
                "¡Excelente! No tienes cartera pendiente",
                "Todas tus ventas a crédito están al día o no has registrado fiados aún.",
                null, null);
    }

    private void setupFieldIcon(JTextField f, String icon) {
        JLabel lbl = new JLabel(createIcon(icon, ThemeConstants.TEXT_SECONDARY, 17, 17));
        lbl.setForeground(ThemeConstants.TEXT_SECONDARY);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        f.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, lbl);
    }

    private FlatSVGIcon createIcon(String path, Color color, int width, int height) {
        FlatSVGIcon icon = new FlatSVGIcon(path, width, height);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, color));
        return icon;
    }
}
