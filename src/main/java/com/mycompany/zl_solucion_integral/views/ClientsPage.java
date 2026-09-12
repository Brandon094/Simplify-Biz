package com.mycompany.zl_solucion_integral.views;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ClientsPage extends JPanel {
    private final UsuarioController usuarioCtrl = new UsuarioController();
    private JTable tbClientes;
    private JScrollPane clientsScroll;

    public ClientsPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header (microcopy de contexto)
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Consulta de clientes",
                createIcon("icons/clients.svg", ThemeConstants.NEON_PURPLE, 24, 24),
                SwingConstants.LEFT);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        title.setFont(ThemeConstants.FONT_TITLE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Consulta la información de contacto de todos tus clientes registrados");
        subtitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        subtitle.setFont(ThemeConstants.FONT_SMALL);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        add(createTablePanel(), BorderLayout.CENTER);
        refreshData();
    }

    private JPanel createTablePanel() {
        RoundedPanel panel = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel tableTitle = new JLabel("Clientes registrados");
        tableTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        tableTitle.setFont(ThemeConstants.FONT_SUBTITLE);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 8, 12, 8));
        panel.add(tableTitle, BorderLayout.NORTH);

        tbClientes = new JTable();
        tbClientes.setModel(new DefaultTableModel(
            new String[]{"Id", "Usuario", "Email", "Teléfono", "Rol", "Contraseña"}, 0));
        tbClientes.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbClientes.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbClientes.setRowHeight(35);
        tbClientes.setShowGrid(false);
        tbClientes.setFillsViewportHeight(true);
        tbClientes.setFont(ThemeConstants.FONT_SMALL);
        tbClientes.setSelectionBackground(new Color(168, 85, 247, 70));
        tbClientes.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);

        clientsScroll = new JScrollPane(tbClientes);
        clientsScroll.setOpaque(false);
        clientsScroll.getViewport().setOpaque(false);
        clientsScroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(clientsScroll, BorderLayout.CENTER);
        return panel;
    }

    private void refreshData() {
        usuarioCtrl.mostrarUsuariosPorRol(tbClientes, "2");
        estilizarTabla();
    }

    private void estilizarTabla() {
        ocultarColumnasInternas();

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    component.setBackground(row % 2 == 0
                            ? ThemeConstants.CARD_BACKGROUND
                            : ThemeConstants.TABLE_ZEBRA);
                    component.setForeground(ThemeConstants.TEXT_SECONDARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return component;
            }
        };

        for (int column = 0; column < tbClientes.getColumnCount(); column++) {
            tbClientes.getColumnModel().getColumn(column).setCellRenderer(renderer);
        }

        JTableHeader header = tbClientes.getTableHeader();
        header.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        header.setForeground(ThemeConstants.TEXT_SECONDARY);
        header.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
        header.setPreferredSize(new Dimension(0, 32));
        header.setReorderingAllowed(false);
        actualizarEstadoVacio();
    }

    private void ocultarColumnasInternas() {
        String[] hiddenColumns = {"Id", "Rol", "Contraseña"};
        for (String columnName : hiddenColumns) {
            for (int column = 0; column < tbClientes.getColumnCount(); column++) {
                if (columnName.equals(tbClientes.getColumnName(column))) {
                    tbClientes.removeColumn(tbClientes.getColumnModel().getColumn(column));
                    break;
                }
            }
        }
    }

    private void actualizarEstadoVacio() {
        if (tbClientes.getRowCount() == 0) {
            clientsScroll.setViewportView(createEmptyState("icons/clients.svg"));
        } else {
            clientsScroll.setViewportView(tbClientes);
        }
        clientsScroll.revalidate();
        clientsScroll.repaint();
    }

    private JPanel createEmptyState(String iconPath) {
        return UIUtils.createEmptyState(
                iconPath, ThemeConstants.NEON_PURPLE,
                "Todavía no tienes clientes registrados",
                "Los clientes se crean automáticamente al registrar una venta con sus datos.",
                null, null);
    }

    private FlatSVGIcon createIcon(String path, Color color, int width, int height) {
        FlatSVGIcon icon = new FlatSVGIcon(path, width, height);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, color));
        return icon;
    }
}
