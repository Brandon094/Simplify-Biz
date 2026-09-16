package com.mycompany.zl_solucion_integral.views.components.dialogs;

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

    public enum ModoImportacion {
        CATALOGO,
        ABASTECIMIENTO
    }

    private File archivoSeleccionado;
    private List<String> cabecerasExcel;
    private JLabel lblArchivo;
    private JComboBox<String> cbNombre, cbCodigo, cbPrecio, cbCosto, cbStock, cbCategoria;
    private JTextField txtCatDefault;
    private JTable tbPreview;
    private DefaultTableModel modelPreview;
    private Runnable onImportSuccess;
    private ModoImportacion modo = ModoImportacion.CATALOGO;
    private java.util.function.Consumer<List<com.mycompany.zl_solucion_integral.models.DetalleCompra>> onItemsLeidos;

    public ImportarProductosDialog(Window owner, Runnable onImportSuccess) {
        this(owner, ModoImportacion.CATALOGO, onImportSuccess, null);
    }

    public ImportarProductosDialog(Window owner, ModoImportacion modo, Runnable onImportSuccess, java.util.function.Consumer<List<com.mycompany.zl_solucion_integral.models.DetalleCompra>> onItemsLeidos) {
        super(owner, modo == ModoImportacion.ABASTECIMIENTO ? 
                "Cargar Factura desde Excel (Abastecimiento)" : 
                "Asistente de Importación Flexible de Productos (Excel/CSV)", 
                ModalityType.APPLICATION_MODAL);
        this.modo = modo;
        this.onImportSuccess = onImportSuccess;
        this.onItemsLeidos = onItemsLeidos;

        setSize(940, 690);
        setMinimumSize(new Dimension(840, 580));
        setResizable(true);
        setLocationRelativeTo(owner);
        
        // Raíz en BorderLayout
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConstants.BACKGROUND);

        // Panel Principal con márgenes
        JPanel rootPanel = new JPanel(new BorderLayout(12, 12));
        rootPanel.setOpaque(false);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Header (Fijo Arriba)
        rootPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Contenedor Central Desplazable (JScrollPane suave)
        JPanel scrollBody = new JPanel(new BorderLayout(12, 12));
        scrollBody.setOpaque(false);
        scrollBody.add(createFileAndMappingPanel(), BorderLayout.NORTH);
        scrollBody.add(createPreviewPanel(), BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(scrollBody);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        rootPanel.add(scrollPane, BorderLayout.CENTER);

        // Footer Actions (Fijo Abajo Garantizado)
        rootPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(rootPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        String tituloTexto = modo == ModoImportacion.ABASTECIMIENTO ? 
                "Importar Productos a Factura de Compra" : "Asistente de Importación de Inventario";
        String subtituloTexto = modo == ModoImportacion.ABASTECIMIENTO ?
                "Mapea las columnas del Excel de tu proveedor para cargar los ítems a la orden de compra actual." :
                "Selecciona cualquier Excel de tu proveedor y mapea las columnas según corresponda.";

        JLabel title = new JLabel(tituloTexto, createIcon("icons/excel.svg", ThemeConstants.NEON_GREEN, 24, 24), SwingConstants.LEFT);
        title.setFont(ThemeConstants.FONT_TITLE);
        title.setForeground(ThemeConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(subtituloTexto);
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
        btnExplicar.setIcon(createIcon("icons/search.svg", ThemeConstants.NEON_BLUE, 14, 14));
        btnExplicar.setIconTextGap(6);
        btnExplicar.setPreferredSize(new Dimension(130, 36));
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
        p.setLayout(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

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
        scroll.setPreferredSize(new Dimension(0, 140));

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel createFooterPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConstants.CARD_BORDER),
            BorderFactory.createEmptyBorder(12, 0, 0, 0)
        ));

        JLabel lblBadge = new JLabel("Asegúrate de mapear al menos Nombre y Precio Venta", createIcon("icons/bolt.svg", ThemeConstants.NEON_CYAN, 14, 14), SwingConstants.LEFT);
        lblBadge.setFont(ThemeConstants.FONT_BODY);
        lblBadge.setForeground(ThemeConstants.TEXT_SECONDARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);

        NeonButton btnCancelar = new NeonButton("Cancelar");
        btnCancelar.setNeonColor(ThemeConstants.TEXT_SECONDARY);
        btnCancelar.setIcon(createIcon("icons/xmark.svg", ThemeConstants.TEXT_SECONDARY, 14, 14));
        btnCancelar.setIconTextGap(6);
        btnCancelar.setPreferredSize(new Dimension(120, 38));
        btnCancelar.addActionListener(e -> dispose());

        String labelBtn = modo == ModoImportacion.ABASTECIMIENTO ? "Cargar a Factura" : "Procesar e Importar";
        NeonButton btnImportar = new NeonButton(labelBtn);
        btnImportar.setNeonColor(ThemeConstants.NEON_GREEN);
        btnImportar.setIcon(createIcon("icons/check-double.svg", ThemeConstants.NEON_GREEN, 16, 16));
        btnImportar.setIconTextGap(8);
        btnImportar.setPreferredSize(new Dimension(190, 38));
        btnImportar.addActionListener(e -> procesarImportacion());

        actions.add(btnCancelar);
        actions.add(btnImportar);

        p.add(lblBadge, BorderLayout.WEST);
        p.add(actions, BorderLayout.EAST);
        return p;
    }

    private FlatSVGIcon createIcon(String path, Color color, int width, int height) {
        FlatSVGIcon icon = new FlatSVGIcon(path, width, height);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, color));
        return icon;
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

        if (cbNombre.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debes vincular la columna del Excel que contiene el 'Nombre del Producto'.", UIMessages.TITULO_ERROR, JOptionPane.WARNING_MESSAGE);
            cbNombre.requestFocus();
            return;
        }

        if (cbPrecio.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debes vincular la columna del Excel que contiene el 'Precio de Venta'.", UIMessages.TITULO_ERROR, JOptionPane.WARNING_MESSAGE);
            cbPrecio.requestFocus();
            return;
        }

        Map<String, Integer> mapeo = new HashMap<>();
        mapeo.put("nombre", cbNombre.getSelectedIndex() - 1);
        mapeo.put("codigo", cbCodigo.getSelectedIndex() - 1);
        mapeo.put("precio", cbPrecio.getSelectedIndex() - 1);
        mapeo.put("precio_costo", cbCosto.getSelectedIndex() - 1);
        mapeo.put("cantidad", cbStock.getSelectedIndex() - 1);
        mapeo.put("categoria", cbCategoria.getSelectedIndex() - 1);

        if (modo == ModoImportacion.ABASTECIMIENTO) {
            ExcelSQLiteManager.ResultadoLecturaAbastecimiento res = 
                ExcelSQLiteManager.leerItemsParaAbastecimiento(archivoSeleccionado, mapeo, txtCatDefault.getText());

            if (res.esExito()) {
                if (onItemsLeidos != null) {
                    onItemsLeidos.accept(res.getItems());
                }
                if (onImportSuccess != null) {
                    onImportSuccess.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, res.getMensaje(), UIMessages.TITULO_ERROR, JOptionPane.ERROR_MESSAGE);
            }
        } else {
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
}
