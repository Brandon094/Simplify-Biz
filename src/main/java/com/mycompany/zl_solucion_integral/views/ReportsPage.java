package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.VentasController;
import com.mycompany.zl_solucion_integral.views.components.LayoutResponsive;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ReportsPage extends JPanel {
    private final VentasController ventasCtrl = new VentasController();
    private JTable tbReports;
    private JScrollPane reportsScroll;
    private JTextField txtStartDate, txtEndDate;
    private JPanel filterPanel, summaryPanel, exportPanel;
    private NeonButton btnFilter, btnExcel, btnPDF;
    private GridBagLayout filtroGrid;

    // Métricas del Resumen Ejecutivo
    private JLabel lblValTotalFacturado, lblValCostoCOGS, lblValGananciaNeta, lblValMetodosPago;

    public ReportsPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        // Contenido Principal
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setOpaque(false);

        // Contenedor Norte: Filtros + Tarjetas de Resumen Ejecutivo
        JPanel topContainer = new JPanel();
        topContainer.setOpaque(false);
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        filterPanel = createFilterPanel();
        summaryPanel = createExecutiveSummaryPanel();

        topContainer.add(filterPanel);
        topContainer.add(Box.createVerticalStrut(15));
        topContainer.add(summaryPanel);

        mainContent.add(topContainer, BorderLayout.NORTH);

        // Tabla de Resultados (Centro)
        mainContent.add(createTablePanel(), BorderLayout.CENTER);

        // Acciones de Exportación (Abajo)
        exportPanel = createExportPanel();
        mainContent.add(exportPanel, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);

        // Adaptabilidad: en móvil el filtro, resumen y acciones se apilan.
        LayoutResponsive.listenWidth(this, (bp, ancho) -> aplicarBreakpoint(bp));

        refreshData();
    }

    /** En móvil los filtros, métricas resumen y botones de exportar se adaptan. */
    private void aplicarBreakpoint(LayoutResponsive.Breakpoint bp) {
        boolean movil = LayoutResponsive.esColumnaUnica(bp);
        if (filterPanel != null && txtStartDate != null) {
            int anchoCampo = movil ? 150 : 150;
            txtStartDate.setPreferredSize(new Dimension(anchoCampo, ThemeConstants.TOUCH_TARGET_MIN));
            txtEndDate.setPreferredSize(new Dimension(anchoCampo, ThemeConstants.TOUCH_TARGET_MIN));
            acomodarFiltro(filterPanel, movil);
        }
        if (summaryPanel != null) {
            summaryPanel.setLayout(movil ? new GridLayout(2, 2, 10, 10) : new GridLayout(1, 4, 15, 15));
            summaryPanel.revalidate();
            summaryPanel.repaint();
        }
        if (exportPanel != null) {
            exportPanel.setLayout(movil
                    ? new GridLayout(2, 1, 0, 10)
                    : new GridLayout(1, 2, 20, 0));
            exportPanel.revalidate();
            exportPanel.repaint();
        }
        revalidate();
        repaint();
    }

    private JPanel createFilterPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        filtroGrid = new GridBagLayout();
        p.setLayout(filtroGrid);

        txtStartDate = createTextField("DD/MM/YYYY");
        setupFieldIcon(txtStartDate, "icons/calendar.svg");
        txtStartDate.setPreferredSize(new Dimension(150, ThemeConstants.TOUCH_TARGET_MIN));
        UIUtils.fluid(txtStartDate);

        txtEndDate = createTextField("DD/MM/YYYY");
        setupFieldIcon(txtEndDate, "icons/calendar.svg");
        txtEndDate.setPreferredSize(new Dimension(150, ThemeConstants.TOUCH_TARGET_MIN));
        UIUtils.fluid(txtEndDate);

        btnFilter = new NeonButton("Filtrar datos");
        btnFilter.setNeonColor(ThemeConstants.NEON_BLUE);
        btnFilter.setIcon(createIcon("icons/search.svg", ThemeConstants.NEON_BLUE, 17, 17));
        btnFilter.setIconTextGap(8);
        btnFilter.setPreferredSize(new Dimension(180, ThemeConstants.TOUCH_TARGET_MIN));
        btnFilter.setMinimumSize(new Dimension(0, ThemeConstants.TOUCH_TARGET_MIN));
        btnFilter.setToolTipText("Filtra las ventas entre la fecha de inicio y fin (formato DD/MM/YYYY)");
        btnFilter.addActionListener(e -> applyFilter());

        acomodarFiltro(p, false);
        return p;
    }

    private JPanel createExecutiveSummaryPanel() {
        JPanel container = new GridLayout(1, 4, 15, 15) != null ? new JPanel(new GridLayout(1, 4, 15, 15)) : new JPanel();
        container.setOpaque(false);

        // Card 1: Facturado
        lblValTotalFacturado = new JLabel("$0");
        container.add(createSummaryCard("TOTAL FACTURADO", lblValTotalFacturado, ThemeConstants.NEON_BLUE, "icons/sales.svg"));

        // Card 2: Costo COGS
        lblValCostoCOGS = new JLabel("$0");
        container.add(createSummaryCard("COSTO MERCANCÍA (COGS)", lblValCostoCOGS, ThemeConstants.NEON_AMBER, "icons/boxes-stacked.svg"));

        // Card 3: Utilidad Neta & Margen
        lblValGananciaNeta = new JLabel("$0 (0.0%)");
        container.add(createSummaryCard("UTILIDAD NETA & MARGEN", lblValGananciaNeta, ThemeConstants.NEON_GREEN, "icons/chart-line.svg"));

        // Card 4: Métodos de Pago
        lblValMetodosPago = new JLabel("Ef: $0 | Tr: $0 | Cr: $0");
        lblValMetodosPago.setFont(ThemeConstants.FONT_SMALL);
        container.add(createSummaryCard("MÉTODOS DE PAGO", lblValMetodosPago, ThemeConstants.NEON_PURPLE, "icons/wallet.svg"));

        return container;
    }

    private JPanel createSummaryCard(String titleText, JLabel valLabel, Color accentColor, String iconPath) {
        RoundedPanel card = new RoundedPanel(18, ThemeConstants.CARD_BACKGROUND);
        card.setLayout(new BorderLayout(10, 8));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel(titleText.toUpperCase());
        title.setForeground(ThemeConstants.TEXT_SECONDARY);
        title.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 10f));

        JLabel iconLbl = new JLabel(createIcon(iconPath, accentColor, 18, 18));
        top.add(title, BorderLayout.CENTER);
        top.add(iconLbl, BorderLayout.EAST);

        valLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        valLabel.setFont(ThemeConstants.FONT_TITLE.deriveFont(Font.BOLD, 16f));

        card.add(top, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);

        return card;
    }

    private void acomodarFiltro(JPanel p, boolean movil) {
        p.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        if (movil) {
            gbc.gridx = 0; gbc.weightx = 1.0;
            int fila = 0;
            gbc.gridy = fila++; p.add(createLabel("FECHA INICIO"), gbc);
            gbc.gridy = fila++; p.add(txtStartDate, gbc);
            gbc.gridy = fila++; p.add(createLabel("FECHA FIN"), gbc);
            gbc.gridy = fila++; p.add(txtEndDate, gbc);
            gbc.gridy = fila++; p.add(btnFilter, gbc);
        } else {
            gbc.gridy = 0;
            gbc.gridx = 0; gbc.weightx = 0; p.add(createLabel("FECHA INICIO"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0; p.add(txtStartDate, gbc);
            gbc.gridx = 2; gbc.weightx = 0; p.add(createLabel("FECHA FIN"), gbc);
            gbc.gridx = 3; gbc.weightx = 1.0; p.add(txtEndDate, gbc);
            gbc.gridx = 4; gbc.weightx = 0; p.add(btnFilter, gbc);
        }
        p.revalidate();
        p.repaint();
    }

    private JPanel createTablePanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel tableTitle = new JLabel("Ventas registradas", createIcon("icons/reports.svg", ThemeConstants.NEON_PURPLE, 20, 20), SwingConstants.LEFT);
        tableTitle.setIconTextGap(8);
        tableTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        tableTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 8, 12, 8));
        p.add(tableTitle, BorderLayout.NORTH);

        tbReports = new JTable();
        tbReports.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbReports.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbReports.setRowHeight(ThemeConstants.TABLE_ROW_HEIGHT);
        tbReports.setShowGrid(false);
        tbReports.setFillsViewportHeight(true);
        tbReports.setFont(ThemeConstants.FONT_SMALL);
        tbReports.setSelectionBackground(ThemeConstants.SELECTION_PURPLE);
        tbReports.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);

        reportsScroll = new JScrollPane(tbReports);
        reportsScroll.setOpaque(false);
        reportsScroll.getViewport().setOpaque(false);
        reportsScroll.setBorder(BorderFactory.createEmptyBorder());

        p.add(reportsScroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel createExportPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 20, 0));
        p.setOpaque(false);

        btnExcel = new NeonButton("Exportar a Excel");
        btnExcel.setNeonColor(ThemeConstants.NEON_GREEN);
        btnExcel.setIcon(createIcon("icons/excel.svg", ThemeConstants.NEON_GREEN, 17, 17));
        btnExcel.setIconTextGap(8);
        btnExcel.setMinimumSize(new Dimension(0, ThemeConstants.TOUCH_TARGET_MIN));
        btnExcel.addActionListener(e -> exportToExcel());
        btnExcel.setToolTipText("Exporta el reporte actual a un archivo de Excel con formato ejecutivo");

        btnPDF = new NeonButton("Generar PDF");
        btnPDF.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnPDF.setIcon(createIcon("icons/pdf.svg", ThemeConstants.NEON_PURPLE, 17, 17));
        btnPDF.setIconTextGap(8);
        btnPDF.setMinimumSize(new Dimension(0, ThemeConstants.TOUCH_TARGET_MIN));
        btnPDF.setToolTipText("Imprime o genera un PDF oficial con las ventas del periodo filtrado");
        btnPDF.addActionListener(e -> exportToPDF());

        p.add(btnPDF);
        p.add(btnExcel);

        return p;
    }

    private void applyFilter() {
        String start = txtStartDate.getText().trim();
        String end = txtEndDate.getText().trim();
        if (start.isEmpty() || end.isEmpty()) {
            UIUtils.showError(this, "Ingrese ambas fechas en formato DD/MM/YYYY");
            return;
        }
        ventasCtrl.mostrarFechasDefinidas(tbReports, start, end);
        estilizarTabla();
    }

    private void exportToExcel() {
        if (tbReports.getRowCount() == 0) {
            UIUtils.showWarning(this, "No hay datos de ventas en la tabla para exportar.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte en Excel");
        fileChooser.setSelectedFile(new java.io.File("Reporte_Ventas_" + System.currentTimeMillis() + ".xlsx"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".xlsx")) {
                path += ".xlsx";
            }
            try {
                ventasCtrl.exportarDatosTablaAExcel(tbReports, path);
                UIUtils.showSuccess(this, "Reporte exportado exitosamente a:\n" + path);
            } catch (Exception ex) {
                UIUtils.showError(this, "Error al exportar reporte a Excel:\n" + ex.getMessage());
            }
        }
    }

    private void exportToPDF() {
        if (tbReports.getRowCount() == 0) {
            UIUtils.showWarning(this, "No hay datos de ventas en la tabla para generar PDF.");
            return;
        }
        try {
            MessageFormat header = new MessageFormat("Reporte Ejecutivo de Ventas - Simplify-Biz");
            MessageFormat footer = new MessageFormat("Página {0}");
            boolean complete = tbReports.print(JTable.PrintMode.FIT_WIDTH, header, footer, true, null, true);
            if (complete) {
                UIUtils.showSuccess(this, "Documento impreso / exportado a PDF correctamente.");
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Error al generar PDF / impresión:\n" + ex.getMessage());
        }
    }

    private void refreshData() {
        ventasCtrl.MostrarVentas(tbReports);
        estilizarTabla();
    }

    private void estilizarTabla() {
        UIUtils.applyTableStyling(tbReports);
        actualizarResumenEjecutivo();
        actualizarEstadoVacio();
    }

    private void actualizarResumenEjecutivo() {
        if (tbReports == null || tbReports.getModel() == null || tbReports.getModel().getRowCount() == 0) {
            if (lblValTotalFacturado != null) lblValTotalFacturado.setText("$0");
            if (lblValCostoCOGS != null) lblValCostoCOGS.setText("$0");
            if (lblValGananciaNeta != null) lblValGananciaNeta.setText("$0 (0.0%)");
            if (lblValMetodosPago != null) lblValMetodosPago.setText("Ef: $0 | Tr: $0 | Cr: $0");
            return;
        }

        javax.swing.table.TableModel model = tbReports.getModel();
        double totalFacturado = 0;
        double totalEfectivo = 0, totalTransf = 0, totalCredito = 0;
        Set<Integer> uniqueVentaIds = new HashSet<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                Object idObj = model.getValueAt(i, 0);
                if (idObj != null) {
                    uniqueVentaIds.add(Integer.parseInt(idObj.toString()));
                }

                Object totalObj = model.getValueAt(i, 11);
                double rowTotal = 0.0;
                if (totalObj instanceof Number) {
                    rowTotal = ((Number) totalObj).doubleValue();
                } else if (totalObj != null) {
                    rowTotal = Double.parseDouble(totalObj.toString().replace("$", "").replace(".", "").replace(",", ".").trim());
                }
                totalFacturado += rowTotal;

                Object metodoObj = model.getValueAt(i, 7);
                String metodo = metodoObj != null ? metodoObj.toString().toLowerCase() : "";
                if (metodo.contains("efectivo")) {
                    totalEfectivo += rowTotal;
                } else if (metodo.contains("transferencia")) {
                    totalTransf += rowTotal;
                } else {
                    totalCredito += rowTotal;
                }
            } catch (Exception ex) {
                // Continuar si hay algún valor inesperado
            }
        }

        double cogs = ventasCtrl.obtenerCogsPorVentaIds(new ArrayList<>(uniqueVentaIds));
        double gananciaNeta = totalFacturado - cogs;
        double margenPct = totalFacturado > 0 ? (gananciaNeta / totalFacturado) * 100.0 : 0.0;

        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        fmt.setMaximumFractionDigits(0);

        if (lblValTotalFacturado != null) lblValTotalFacturado.setText(fmt.format(totalFacturado));
        if (lblValCostoCOGS != null) lblValCostoCOGS.setText(fmt.format(cogs));
        if (lblValGananciaNeta != null) {
            lblValGananciaNeta.setText(String.format("%s (%.1f%%)", fmt.format(gananciaNeta), margenPct));
        }
        if (lblValMetodosPago != null) {
            lblValMetodosPago.setText(String.format(
                    "<html><div style='line-height:1.25; font-size:11px; color:#F8FAFC;'>" +
                    "<b>Efectivo:</b> %s &nbsp;|&nbsp; <b>Transf:</b> %s<br>" +
                    "<span style='color:#A855F7;'><b>Crédito (Deudor):</b> %s</span>" +
                    "</div></html>",
                    fmt.format(totalEfectivo), fmt.format(totalTransf), fmt.format(totalCredito)));
        }

    }


    private void actualizarEstadoVacio() {
        if (tbReports.getRowCount() == 0) {
            boolean hasFilter = !txtStartDate.getText().trim().isEmpty()
                    || !txtEndDate.getText().trim().isEmpty();
            reportsScroll.setViewportView(createEmptyState(hasFilter));
        } else {
            reportsScroll.setViewportView(tbReports);
        }
        reportsScroll.revalidate();
        reportsScroll.repaint();
    }

    private JPanel createEmptyState(boolean hasFilter) {
        if (hasFilter) {
            return UIUtils.createEmptyState(
                    "icons/reports.svg", ThemeConstants.NEON_PURPLE,
                    "No encontramos ventas en ese periodo",
                    "Prueba con otro rango de fechas o consulta el histórico completo.",
                    "Ver todo el histórico",
                    () -> {
                        txtStartDate.setText("");
                        txtEndDate.setText("");
                        refreshData();
                    });
        }
        return UIUtils.createEmptyState(
                "icons/reports.svg", ThemeConstants.NEON_PURPLE,
                "Aún no tienes ventas registradas",
                "Las ventas que registres en el punto de venta aparecerán aquí.",
                null, null);
    }

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(ThemeConstants.TEXT_SECONDARY);
        l.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        return l;
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

    private JTextField createTextField(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.setBackground(ThemeConstants.INPUT_BACKGROUND);
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_PURPLE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return f;
    }
}
