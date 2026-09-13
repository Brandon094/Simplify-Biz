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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ReportsPage extends JPanel {
    private final VentasController ventasCtrl = new VentasController();
    private JTable tbReports;
    private JScrollPane reportsScroll;
    private JTextField txtStartDate, txtEndDate;
    private JPanel filterPanel, exportPanel;
    private NeonButton btnFilter, btnExcel, btnPDF;
    private GridBagLayout filtroGrid;

    public ReportsPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header (microcopy de contexto) con subtítulo que envuelve al ancho.
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Centro de reportes", createIcon("icons/reports.svg", ThemeConstants.NEON_PURPLE, 24, 24), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea subtitle = UIUtils.createWrappingLabel(
                "Filtra tus ventas por rango de fechas y exporta la información que necesitas",
                ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        // Contenido Principal
        JPanel mainContent = new JPanel(new BorderLayout(25, 25));
        mainContent.setOpaque(false);

        // Filtros (Arriba)
        filterPanel = createFilterPanel();
        mainContent.add(filterPanel, BorderLayout.NORTH);

        // Tabla de Resultados (Centro)
        mainContent.add(createTablePanel(), BorderLayout.CENTER);

        // Acciones de Exportación (Abajo)
        exportPanel = createExportPanel();
        mainContent.add(exportPanel, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);

        // Adaptabilidad: en móvil el filtro y las acciones se apilan.
        LayoutResponsive.listenWidth(this, (bp, ancho) -> aplicarBreakpoint(bp));

        refreshData();
    }

    /** En móvil los filtros y los botones de exportar se apilan en vertical. */
    private void aplicarBreakpoint(LayoutResponsive.Breakpoint bp) {
        boolean movil = LayoutResponsive.esColumnaUnica(bp);
        if (filterPanel != null && txtStartDate != null) {
            int anchoCampo = movil ? 150 : 150;
            txtStartDate.setPreferredSize(new Dimension(anchoCampo, ThemeConstants.TOUCH_TARGET_MIN));
            txtEndDate.setPreferredSize(new Dimension(anchoCampo, ThemeConstants.TOUCH_TARGET_MIN));
            acomodarFiltro(filterPanel, movil);
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

        // Se colocan los componentes; la posicion (horizontal/vertical) la
        // decide aplicarBreakpoint() segun el ancho.
        acomodarFiltro(p, false);
        return p;
    }

    /** Coloca los componentes del filtro en una fila (escritorio) o en columna (movil). */
    private void acomodarFiltro(JPanel p, boolean movil) {
        p.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        if (movil) {
            // Una sola columna: etiqueta, campo, etiqueta, campo, boton.
            gbc.gridx = 0; gbc.weightx = 1.0;
            int fila = 0;
            gbc.gridy = fila++; p.add(createLabel("FECHA INICIO"), gbc);
            gbc.gridy = fila++; p.add(txtStartDate, gbc);
            gbc.gridy = fila++; p.add(createLabel("FECHA FIN"), gbc);
            gbc.gridy = fila++; p.add(txtEndDate, gbc);
            gbc.gridy = fila++; p.add(btnFilter, gbc);
        } else {
            // Una fila: [label campo] [label campo] [boton].
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

        JLabel tableTitle = new JLabel("Ventas registradas");
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
        tbReports.setSelectionBackground(new Color(168, 85, 247, 70));
        tbReports.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);

        reportsScroll = new JScrollPane(tbReports);
        reportsScroll.setOpaque(false);
        reportsScroll.getViewport().setOpaque(false);
        reportsScroll.setBorder(BorderFactory.createEmptyBorder());

        p.add(reportsScroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel createExportPanel() {
        // GridBalanced de 2 botones que se reparten el ancho (2x1 en escritorio, 1x2 en movil).
        JPanel p = new JPanel(new GridLayout(1, 2, 20, 0));
        p.setOpaque(false);

        btnExcel = new NeonButton("Exportar a Excel");
        btnExcel.setNeonColor(ThemeConstants.NEON_GREEN);
        btnExcel.setIcon(createIcon("icons/excel.svg", ThemeConstants.NEON_GREEN, 17, 17));
        btnExcel.setIconTextGap(8);
        btnExcel.setMinimumSize(new Dimension(0, ThemeConstants.TOUCH_TARGET_MIN));
        btnExcel.addActionListener(e -> exportToExcel());
        btnExcel.setToolTipText("Exporta el reporte actual a un archivo de Excel");

        btnPDF = new NeonButton("Generar PDF");
        btnPDF.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnPDF.setIcon(createIcon("icons/pdf.svg", ThemeConstants.NEON_PURPLE, 17, 17));
        btnPDF.setIconTextGap(8);
        btnPDF.setMinimumSize(new Dimension(0, ThemeConstants.TOUCH_TARGET_MIN));
        btnPDF.setToolTipText("Genera un PDF con las ventas del periodo filtrado");
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
            UIUtils.showSuccess(this, "Reporte exportado exitosamente a:\n" + path);
        }
    }

    private void exportToPDF() {
        if (tbReports.getRowCount() == 0) {
            UIUtils.showWarning(this, "No hay datos de ventas en la tabla para generar PDF.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte en PDF");
        fileChooser.setSelectedFile(new java.io.File("Reporte_Ventas_" + System.currentTimeMillis() + ".pdf"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".pdf")) {
                path += ".pdf";
            }
            UIUtils.showSuccess(this, "Documento PDF generado exitosamente en:\n" + path);
        }
    }

    private void refreshData() {
        ventasCtrl.MostrarVentas(tbReports);
        estilizarTabla();
    }

    private void estilizarTabla() {
        UIUtils.applyTableStyling(tbReports);
        actualizarEstadoVacio();
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
