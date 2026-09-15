package com.mycompany.zl_solucion_integral.views;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.mycompany.zl_solucion_integral.views.components.dialogs.HistorialComprasClienteDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClientsPage extends JPanel {
    private final UsuarioController usuarioCtrl = new UsuarioController();
    private JTable tbClientes;
    private JScrollPane clientsScroll;
    private NeonButton btnHistorial;

    public ClientsPage() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        add(createTablePanel(), BorderLayout.CENTER);
        refreshData();
    }

    private JPanel createTablePanel() {
        RoundedPanel panel = new RoundedPanel(20, ThemeConstants.CARD_BACKGROUND);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 8, 12, 8));

        JLabel tableTitle = new JLabel("Clientes registrados", createIcon("icons/clients.svg", ThemeConstants.NEON_PURPLE, 20, 20), SwingConstants.LEFT);
        tableTitle.setIconTextGap(8);
        tableTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        tableTitle.setFont(ThemeConstants.FONT_SUBTITLE);

        btnHistorial = new NeonButton("Ver Historial de Compras");
        btnHistorial.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnHistorial.setIcon(createIcon("icons/history.svg", ThemeConstants.NEON_PURPLE, 16, 16));
        btnHistorial.setPreferredSize(new Dimension(210, 36));
        btnHistorial.setToolTipText("Ver todas las ventas y productos comprados por el cliente seleccionado");
        btnHistorial.addActionListener(e -> abrirHistorialCliente());

        topBar.add(tableTitle, BorderLayout.WEST);
        topBar.add(btnHistorial, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        tbClientes = new JTable();
        tbClientes.setModel(new DefaultTableModel(
            new String[]{"Id", "Usuario", "Email", "Teléfono", "Rol"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        });
        tbClientes.setBackground(ThemeConstants.CARD_BACKGROUND);
        tbClientes.setForeground(ThemeConstants.TEXT_PRIMARY);
        tbClientes.setRowHeight(ThemeConstants.TABLE_ROW_HEIGHT);
        tbClientes.setShowGrid(false);
        tbClientes.setFillsViewportHeight(true);
        tbClientes.setFont(ThemeConstants.FONT_SMALL);
        tbClientes.setSelectionBackground(ThemeConstants.SELECTION_PURPLE);
        tbClientes.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);

        // Listener de doble clic
        tbClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tbClientes.getSelectedRow() != -1) {
                    abrirHistorialCliente();
                }
            }
        });

        clientsScroll = new JScrollPane(tbClientes);
        clientsScroll.setOpaque(false);
        clientsScroll.getViewport().setOpaque(false);
        clientsScroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(clientsScroll, BorderLayout.CENTER);
        return panel;
    }

    private void abrirHistorialCliente() {
        int selectedRow = tbClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor selecciona un cliente de la tabla para consultar su historial.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener datos de la fila del modelo
        DefaultTableModel model = (DefaultTableModel) tbClientes.getModel();
        String nombreCliente = (String) model.getValueAt(selectedRow, 1);
        String ccCliente = (String) model.getValueAt(selectedRow, 3); // Teléfono/CC en columna 3 o búsqueda

        Window window = SwingUtilities.getWindowAncestor(this);
        HistorialComprasClienteDialog dialog = new HistorialComprasClienteDialog(window, nombreCliente, ccCliente);
        dialog.setVisible(true);
    }

    private void refreshData() {
        usuarioCtrl.mostrarUsuariosPorRol(tbClientes, "2");
        estilizarTabla();
    }

    private void estilizarTabla() {
        UIUtils.applyTableStyling(tbClientes);
        UIUtils.hideColumn(tbClientes, "Rol");
        actualizarEstadoVacio();
    }

    private void actualizarEstadoVacio() {
        if (tbClientes.getRowCount() == 0) {
            clientsScroll.setViewportView(createEmptyState("icons/clients.svg"));
            if (btnHistorial != null) btnHistorial.setEnabled(false);
        } else {
            clientsScroll.setViewportView(tbClientes);
            if (btnHistorial != null) btnHistorial.setEnabled(true);
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

