package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.VentasController;
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

    public ReportsPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header (microcopy de contexto)
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Centro de reportes", createIcon("icons/reports.svg", ThemeConstants.NEON_PURPLE, 24, 24), SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Filtra tus ventas por rango de fechas y exporta la información que necesitas");
        subtitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        subtitle.setFont(ThemeConstants.FONT_SMALL);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        // Contenido Principal
        JPanel mainContent = new JPanel(new BorderLayout(25, 25));
        mainContent.setOpaque(false);

        // Filtros (Arriba)
        mainContent.add(createFilterPanel(), BorderLayout.NORTH);

        // Tabla de Resultados (Centro)
        mainContent.add(createTablePanel(), BorderLayout.CENTER);

        // Acciones de Exportación (Abajo)
        mainContent.add(createExportPanel(), BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);
        
        refreshData();
    }

    private JPanel createFilterPanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        
        p.add(createLabel("FECHA INICIO"));
        txtStartDate = createTextField("DD/MM/YYYY");
        setupFieldIcon(txtStartDate, "icons/calendar.svg");
        txtStartDate.setPreferredSize(new Dimension(150, 35));
        p.add(txtStartDate);

        p.add(createLabel("FECHA FIN"));
        txtEndDate = createTextField("DD/MM/YYYY");
        setupFieldIcon(txtEndDate, "icons/calendar.svg");
        txtEndDate.setPreferredSize(new Dimension(150, 35));
        p.add(txtEndDate);

        NeonButton btnFilter = new NeonButton("Filtrar datos");
        btnFilter.setNeonColor(ThemeConstants.NEON_BLUE);
        btnFilter.setIcon(createIcon("icons/search.svg", ThemeConstants.NEON_BLUE, 17, 17));
        btnFilter.setIconTextGap(8);
        btnFilter.setPreferredSize(new Dimension(180, 35));
        btnFilter.setToolTipText("Filtra las ventas entre la fecha de inicio y fin (formato DD/MM/YYYY)");
        btnFilter.addActionListener(e -> applyFilter());
        p.add(btnFilter);

        return p;
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
        tbReports.setRowHeight(35);
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
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        p.setOpaque(false);

        NeonButton btnExcel = new NeonButton("Exportar a Excel");
        btnExcel.setNeonColor(ThemeConstants.NEON_GREEN);
        btnExcel.setIcon(createIcon("icons/excel.svg", ThemeConstants.NEON_GREEN, 17, 17));
        btnExcel.setIconTextGap(8);
        btnExcel.setPreferredSize(new Dimension(200, 45));
        btnExcel.addActionListener(e -> exportToExcel());
        btnExcel.setToolTipText("Exporta el reporte actual a un archivo de Excel");
        
        NeonButton btnPDF = new NeonButton("Generar PDF");
        btnPDF.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnPDF.setIcon(createIcon("icons/pdf.svg", ThemeConstants.NEON_PURPLE, 17, 17));
        btnPDF.setIconTextGap(8);
        btnPDF.setPreferredSize(new Dimension(200, 45));
        btnPDF.setToolTipText("Genera un PDF con las ventas del periodo filtrado");
        
        p.add(btnPDF);
        p.add(btnExcel);
        
        return p;
    }

    private void applyFilter() {
        String start = txtStartDate.getText().trim();
        String end = txtEndDate.getText().trim();
        if (start.isEmpty() || end.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese ambas fechas en formato DD/MM/YYYY");
            return;
        }
        ventasCtrl.mostrarFechasDefinidas(tbReports, start, end);
        estilizarTabla();
    }

    private void exportToExcel() {
        // Aquí llamaríamos a la lógica de ExcelSQLiteManager adaptada
        JOptionPane.showMessageDialog(this, "Exportando reporte de ventas...");
    }

    private void refreshData() {
        ventasCtrl.MostrarVentas(tbReports);
        estilizarTabla();
    }

    private void estilizarTabla() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(168, 85, 247, 40));
                } else {
                    c.setBackground(row % 2 == 0
                            ? ThemeConstants.CARD_BACKGROUND
                            : ThemeConstants.TABLE_ZEBRA);
                    c.setForeground(ThemeConstants.TEXT_SECONDARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        for (int i = 0; i < tbReports.getColumnCount(); i++) {
            tbReports.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JTableHeader header = tbReports.getTableHeader();
        header.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        header.setForeground(ThemeConstants.TEXT_SECONDARY);
        header.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        header.setPreferredSize(new Dimension(0, 32));
        header.setReorderingAllowed(false);

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
