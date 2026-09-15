package com.mycompany.zl_solucion_integral.views.dialogs;

import com.mycompany.zl_solucion_integral.config.ExcelSQLiteManager;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.mycompany.zl_solucion_integral.views.components.atoms.RoundedPanel;
import com.mycompany.zl_solucion_integral.views.components.UIMessages;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diálogo modal para importar masivamente productos desde Excel/CSV con mapeo visual de columnas.
 */
public class ImportarProductosDialog extends JDialog {

    private File archivoSeleccionado;
    private List<String> cabecerasExcel;
    private JLabel lblArchivo;
    private JComboBox<String> cbNombre, cbCodigo, cbPrecio, cbCosto, cbStock, cbCategoria;
    private JTextField txtCatDefault;
    private JTable tbPreview;
    private DefaultTableModel modelPreview;
    private Runnable onImportSuccess;

    public ImportarProductosDialog(Window owner, Runnable onImportSuccess) {
        super(owner, "Asistente de Importación Flexible de Productos (Excel/CSV)", ModalityType.APPLICATION_MODAL);
        this.onImportSuccess = onImportSuccess;

        setSize(850, 620);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBackground(ThemeConstants.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        content.add(createHeaderPanel(), BorderLayout.NORTH);

        // Body (Paso 1: Archivo + Paso 2: Mapeo + Paso 3: Vista previa)
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setOpaque(false);
        body.add(createFileAndMappingPanel(), BorderLayout.NORTH);
        body.add(createPreviewPanel(), BorderLayout.CENTER);

        content.add(body, BorderLayout.CENTER);

        // Footer Actions
        content.add(createFooterPanel(), BorderLayout.SOUTH);

        add(content);
    }

    private JPanel createHeaderPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        JLabel title = new JLabel("Asistente de Importación de Inventario", new FlatSVGIcon("icons/excel.svg", 24, 24), SwingConstants.LEFT);
        title.setFont(ThemeConstants.FONT_TITLE);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Selecciona cualquier Excel de tu proveedor y mapea las columnas según corresponda.");
        subtitle.setFont(ThemeConstants.FONT_BODY);
        subtitle.setForeground(ThemeConstants.TEXT_SECONDARY);

        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 4));
        texts.setOpaque(false);
        texts.add(title);
        texts.add(subtitle);

        p.add(texts, BorderLayout.CENTER);
        return p;
    }

    private JPanel createFileAndMappingPanel() {
        RoundedPanel p = new RoundedPanel(16, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        // Fila 1: Selección de Archivo
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel lblPass1 = new JLabel("1. Archivo Excel:");
        lblPass1.setFont(ThemeConstants.FONT_SUBTITLE);
        lblPass1.setForeground(ThemeConstants.NEON_BLUE);
        p.add(lblPass1, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        lblArchivo = new JLabel("Ningún archivo seleccionado (.xlsx, .xls, .csv)");
        lblArchivo.setForeground(ThemeConstants.TEXT_SECONDARY);
        p.add(lblArchivo, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1;
        NeonButton btnExplicar = new NeonButton("Examinar...");
        btnExplicar.setNeonColor(ThemeConstants.NEON_BLUE);
        btnExplicar.addActionListener(e -> seleccionarArchivo());
        p.add(btnExplicar, gbc);

        // Fila 2: Separador
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeConstants.CARD_BORDER);
        p.add(sep, gbc);

        // Fila 3: Mapeo de Columnas (Paso 2)
        gbc.gridy = 2; gbc.gridwidth = 4;
        JLabel lblPass2 = new JLabel("2. Vinculación / Mapeo de Columnas del Excel:");
        lblPass2.setFont(ThemeConstants.FONT_SUBTITLE);
        lblPass2.setForeground(ThemeConstants.NEON_PURPLE);
        p.add(lblPass2, gbc);

        // Grid 2x3 para Combos
        JPanel mapGrid = new JPanel(new GridLayout(3, 4, 10, 8));
        mapGrid.setOpaque(false);

        cbNombre = createCombo();
        cbCodigo = createCombo();
        cbPrecio = createCombo();
        cbCosto = createCombo();
        cbStock = createCombo();
        cbCategoria = createCombo();

        mapGrid.add(createLabel("Nombre Producto *")); mapGrid.add(cbNombre);
        mapGrid.add(createLabel("Código SKU")); mapGrid.add(cbCodigo);

        mapGrid.add(createLabel("Precio Venta ($) *")); mapGrid.add(cbPrecio);
        mapGrid.add(createLabel("Precio Costo ($)")); mapGrid.add(cbCosto);

        mapGrid.add(createLabel("Stock / Cantidad")); mapGrid.add(cbStock);
        mapGrid.add(createLabel("Categoría")); mapGrid.add(cbCategoria);

        gbc.gridy = 3; gbc.gridwidth = 4;
        p.add(mapGrid, gbc);

        // Fila Categoría por Defecto
        JPanel pCatDef = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pCatDef.setOpaque(false);
        pCatDef.add(createLabel("Categoría por Defecto (si viene vacía en Excel):"));
        txtCatDefault = new JTextField("GENERAL", 15);
        txtCatDefault.setBackground(ThemeConstants.INPUT_BACKGROUND);
        txtCatDefault.setForeground(ThemeConstants.TEXT_PRIMARY);
        pCatDef.add(txtCatDefault);

        gbc.gridy = 4; gbc.gridwidth = 4;
        p.add(pCatDef, gbc);

        return p;
    }

    private JPanel createPreviewPanel() {
        RoundedPanel p = new RoundedPanel(16, ThemeConstants.CARD_BACKGROUND);
        p.setLayout(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("3. Vista Previa de Filas a Importar (Primeras 5 filas):");
        title.setFont(ThemeConstants.FONT_SUBTITLE);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);
        p.add(title, BorderLayout.NORTH);

        modelPreview = new DefaultTableModel();
        tbPreview = new JTable(modelPreview);
        UIUtils.applyTableStyling(tbPreview);

        JScrollPane scroll = new JScrollPane(tbPreview);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel createFooterPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        p.setOpaque(false);

        NeonButton btnCancelar = new NeonButton("Cancelar");
        btnCancelar.setNeonColor(new Color(100, 110, 120));
        btnCancelar.addActionListener(e -> dispose());

        NeonButton btnImportar = new NeonButton("Procesar e Importar");
        btnImportar.setNeonColor(ThemeConstants.NEON_GREEN);
        btnImportar.addActionListener(e -> procesarImportacion());

        p.add(btnCancelar);
        p.add(btnImportar);
        return p;
    }

    private JComboBox<String> createCombo() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setBackground(ThemeConstants.INPUT_BACKGROUND);
        cb.setForeground(ThemeConstants.TEXT_PRIMARY);
        cb.addItem("-- Seleccionar Columna --");
        return cb;
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeConstants.FONT_BODY);
        l.setForeground(ThemeConstants.TEXT_SECONDARY);
        return l;
    }

    private void seleccionarArchivo() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar Lista de Productos Excel");
        fc.setFileFilter(new FileNameExtensionFilter("Archivos Excel / CSV (*.xlsx, *.xls, *.csv)", "xlsx", "xls", "csv"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            archivoSeleccionado = fc.getSelectedFile();
            lblArchivo.setText(archivoSeleccionado.getName());
            lblArchivo.setForeground(ThemeConstants.NEON_GREEN);

            try {
                cabecerasExcel = ExcelSQLiteManager.leerCabeceras(archivoSeleccionado);
                poblarCombosMapeo();
                cargarVistaPrevia();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo Excel: " + ex.getMessage(), UIMessages.TITULO_ERROR, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void poblarCombosMapeo() {
        JComboBox<?>[] combos = {cbNombre, cbCodigo, cbPrecio, cbCosto, cbStock, cbCategoria};
        for (JComboBox<?> cb : combos) {
            cb.removeAllItems();
            @SuppressWarnings("unchecked")
            JComboBox<String> cbStr = (JComboBox<String>) cb;
            cbStr.addItem("-- No Asignado --");
            for (String col : cabecerasExcel) {
                cbStr.addItem(col);
            }
        }

        // Auto-selección inteligente por coincidencia de texto
        autoSeleccionarCoincidencia(cbNombre, "nombre", "producto", "descripcion", "item");
        autoSeleccionarCoincidencia(cbCodigo, "codigo", "sku", "ref", "referencia", "id");
        autoSeleccionarCoincidencia(cbPrecio, "precio", "venta", "pvp", "valor");
        autoSeleccionarCoincidencia(cbCosto, "costo", "compra", "neto");
        autoSeleccionarCoincidencia(cbStock, "stock", "cantidad", "cant", "existencias");
        autoSeleccionarCoincidencia(cbCategoria, "categoria", "cat", "grupo");
    }

    private void autoSeleccionarCoincidencia(JComboBox<String> cb, String... palabrasClave) {
        for (int i = 1; i < cb.getItemCount(); i++) {
            String itemLower = cb.getItemAt(i).toLowerCase();
            for (String kw : palabrasClave) {
                if (itemLower.contains(kw)) {
                    cb.setSelectedIndex(i);
                    return;
                }
            }
        }
    }

    private void cargarVistaPrevia() throws Exception {
        modelPreview.setRowCount(0);
        modelPreview.setColumnIdentifiers(cabecerasExcel.toArray());

        List<Object[]> filas = ExcelSQLiteManager.leerVistaPrevia(archivoSeleccionado, 5);
        for (Object[] fila : filas) {
            modelPreview.addRow(fila);
        }
    }

    private void procesarImportacion() {
        if (archivoSeleccionado == null || !archivoSeleccionado.exists()) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un archivo Excel válido primero.", UIMessages.TITULO_ERROR, JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Integer> mapeo = new HashMap<>();
        mapeo.put("nombre", cbNombre.getSelectedIndex() - 1);
        mapeo.put("codigo", cbCodigo.getSelectedIndex() - 1);
        mapeo.put("precio", cbPrecio.getSelectedIndex() - 1);
        mapeo.put("precio_costo", cbCosto.getSelectedIndex() - 1);
        mapeo.put("cantidad", cbStock.getSelectedIndex() - 1);
        mapeo.put("categoria", cbCategoria.getSelectedIndex() - 1);

        ResultadoOperacion res = ExcelSQLiteManager.importarConMapeo(archivoSeleccionado, mapeo, txtCatDefault.getText());

        if (res.esExito()) {
            JOptionPane.showMessageDialog(this, res.getMensaje(), UIMessages.TITULO_EXITO, JOptionPane.INFORMATION_MESSAGE);
            if (onImportSuccess != null) {
                onImportSuccess.run();
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, res.getMensaje(), UIMessages.TITULO_ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }
}
