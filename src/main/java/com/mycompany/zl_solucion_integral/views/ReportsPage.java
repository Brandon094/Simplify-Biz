package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.controllers.VentasController;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class ReportsPage extends JPanel {
    private final VentasController ventasCtrl = new VentasController();
    private JTable tbReports;
    private JTextField txtStartDate, txtEndDate;

    public ReportsPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel title = new JLabel("📈 Centro de Inteligencia y Reportes");
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        add(title, BorderLayout.NORTH);

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
        
        p.add(createLabel("📅 FECHA INICIO:"));
        txtStartDate = createTextField("DD/MM/YYYY");
        setupFieldIcon(txtStartDate, "📅");
        txtStartDate.setPreferredSize(new Dimension(150, 35));
        p.add(txtStartDate);

        p.add(createLabel("📅 FECHA FIN:"));
        txtEndDate = createTextField("DD/MM/YYYY");
        setupFieldIcon(txtEndDate, "📅");
        txtEndDate.setPreferredSize(new Dimension(150, 35));
        p.add(txtEndDate);

        NeonButton btnFilter = new NeonButton("FILTRAR DATOS 🔍");
        btnFilter.setNeonColor(ThemeConstants.NEON_BLUE);
        btnFilter.setPreferredSize(new Dimension(180, 35));
        btnFilter.addActionListener(e -> applyFilter());
        p.add(btnFilter);

        return p;
    }

    private JPanel createTablePanel() {
        RoundedPanel p = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        tbReports = new JTable();
        tbReports.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbReports.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbReports.setRowHeight(35);
        tbReports.setShowGrid(false);

        JScrollPane scroll = new JScrollPane(tbReports);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel createExportPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        p.setOpaque(false);

        NeonButton btnExcel = new NeonButton("EXPORTAR A EXCEL 📗");
        btnExcel.setNeonColor(ThemeConstants.NEON_GREEN);
        btnExcel.setPreferredSize(new Dimension(200, 45));
        btnExcel.addActionListener(e -> exportToExcel());
        
        NeonButton btnPDF = new NeonButton("GENERAR PDF 📄");
        btnPDF.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnPDF.setPreferredSize(new Dimension(200, 45));
        
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
                    c.setBackground(ThemeConstants.CARD_BACKGROUND);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        for (int i = 0; i < tbReports.getColumnCount(); i++) {
            tbReports.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(ThemeConstants.TEXT_SECONDARY);
        l.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        return l;
    }

    private void setupFieldIcon(JTextField f, String icon) {
        JLabel lbl = new JLabel(icon);
        lbl.setForeground(ThemeConstants.TEXT_SECONDARY);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        f.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, lbl);
    }

    private JTextField createTextField(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.setBackground(new Color(15, 23, 42));
        f.setForeground(ThemeConstants.TEXT_PRIMARY);
        f.setCaretColor(ThemeConstants.NEON_PURPLE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return f;
    }
}
