package com.mycompany.zl_solucion_integral.vistas;

import com.mycompany.zl_solucion_integral.config.ExcelSQLiteManager;
import com.mycompany.zl_solucion_integral.config.Listener;
import com.mycompany.zl_solucion_integral.config.PantallaCarga;
import com.mycompany.zl_solucion_integral.config.SelecionRuta;
import com.mycompany.zl_solucion_integral.config.UtilVentanas;
import com.mycompany.zl_solucion_integral.controllers.ProductoController;
import com.mycompany.zl_solucion_integral.models.Producto;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Ventana para registrar y gestionar productos. Controla el registro,
 * modificación y eliminación de productos.
 *
 * @author Dazac
 */
public class FormRegistroProductos extends javax.swing.JFrame {
    // Controlador para manejar las operaciones con los productos

    ProductoController productoCtrl = new ProductoController();
    Listener listenerTb = new Listener();

    /*
    * Constructor que inicaliza la ventana y muestra los productos en la tabla
     */
    public FormRegistroProductos() {
        initComponents();
        setTitle("Registro de productos");
        UtilVentanas.aplicarPantallaCompleta(this);

        // Mostrar los datos de la tabla productos
        productoCtrl.mostrarProductos(tbProductos);
        String totalRegistros = String.valueOf(productoCtrl.contarRegistros("Todas"));
        textTotalRegistros.setText("Total registros:  " + totalRegistros);

        // Rellenar el JComboBox de categorias
        initComboBoxCategorias();

        // Crear los campos de texto a llenar con los datos de la fila seleccionada
        JTextField[] camposTexto = {txtProducto, txtCantidad, txtPrecio, txtCodigo, txtCategoria};

        // Definir los índices de las columnas que quieres mostrar en los campos de texto
        int[] columnas = {1, 3, 2, 4, 5}; // Suponiendo que las columnas son: Nombre, Cantidad, Precio, Código

        // Inicializar el método para obtener los datos del producto seleccionado
        listenerTb.agregarListenerTabla(tbProductos, camposTexto, columnas);
    }

    // Método para inicializar las categorías en el JComboBox
    private void initComboBoxCategorias() {
        ListCategoria.addItem("Todas"); // Opción para mostrar todos los productos
        ListCategoria.addItem("DOTACION HOMBRE"); // Agrega tus categorías
        ListCategoria.addItem("DOTACION DAMA"); // Agrega tus categorías
        ListCategoria.addItem("CALZADO"); // Agrega tus categorías
        ListCategoria.addItem("EPP"); // Agrega tus categorías
        ListCategoria.addItem("BOTIQUINES");
        ListCategoria.addItem("SEÑALIZACION");
    }

    // Método para obtener los datos del formulario 
    private Producto obtenerDatosFormulario() {
        // Obtener los datos del formulario
        String productoNombre = txtProducto.getText();
        String cantidadStr = txtCantidad.getText();
        String precioStr = txtPrecio.getText();
        String codigo = txtCodigo.getText();
        String categoria = txtCategoria.getText();

        // Inicializar variables para cantidad y precio
        int cantidad = 0;
        double precio = 0.0;

        try {
            // Parsear cantidad y precio
            cantidad = Integer.parseInt(cantidadStr);
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            // Mostrar un mensaje si ocurre un error en el parseo
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero y el precio un número decimal válido.");
            return null; // Retornar null si hay un error en el parseo
        }

        // Crear y retornar un objeto Producto con los datos
        return new Producto(productoNombre, precio, cantidad, codigo, categoria);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tbProductos = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        btnMenuPricipal = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        txtPrecio = new javax.swing.JTextField();
        txtCantidad = new javax.swing.JTextField();
        btnEliminar = new javax.swing.JButton();
        txtCodigo = new javax.swing.JTextField();
        btnModificar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        txtProducto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnImportarExcel = new javax.swing.JButton();
        btnExportar = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtCategoria = new javax.swing.JTextField();
        ListCategoria = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        textTotalRegistros = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tbProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "producto", "precio", "cantidad"
            }
        ));
        tbProductos.setSelectionForeground(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(tbProductos);

        jLabel1.setFont(new java.awt.Font("Lucida Console", 2, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Registro de Productos");
        jLabel1.setToolTipText("");

        btnMenuPricipal.setBackground(new java.awt.Color(153, 255, 255));
        btnMenuPricipal.setFont(new java.awt.Font("Candara", 3, 12)); // NOI18N
        btnMenuPricipal.setForeground(new java.awt.Color(0, 0, 0));
        btnMenuPricipal.setText("Menu principal");
        btnMenuPricipal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMenuPricipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuPricipalActionPerformed(evt);
            }
        });

        btnSalir.setBackground(new java.awt.Color(153, 255, 255));
        btnSalir.setFont(new java.awt.Font("Candara", 3, 12)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(0, 0, 0));
        btnSalir.setText("Salir");
        btnSalir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ingrese los datos para realizar el registro", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Candara", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        txtPrecio.setBackground(new java.awt.Color(204, 204, 204));
        txtPrecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioActionPerformed(evt);
            }
        });

        txtCantidad.setBackground(new java.awt.Color(204, 204, 204));

        btnEliminar.setBackground(new java.awt.Color(153, 204, 255));
        btnEliminar.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(0, 0, 0));
        btnEliminar.setText("Eliminar");
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        txtCodigo.setBackground(new java.awt.Color(204, 204, 204));
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        btnModificar.setBackground(new java.awt.Color(153, 204, 255));
        btnModificar.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnModificar.setForeground(new java.awt.Color(0, 0, 0));
        btnModificar.setText("Modificar");
        btnModificar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnGuardar.setBackground(new java.awt.Color(153, 204, 255));
        btnGuardar.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(0, 0, 0));
        btnGuardar.setText("Guardar ");
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        txtProducto.setBackground(new java.awt.Color(204, 204, 204));

        jLabel3.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Producto:");

        jLabel4.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Precio:");

        jLabel5.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Cantidad:");

        jLabel6.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Codigo:");

        btnImportarExcel.setBackground(new java.awt.Color(153, 204, 255));
        btnImportarExcel.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnImportarExcel.setForeground(new java.awt.Color(0, 0, 0));
        btnImportarExcel.setText("Importar Excel");
        btnImportarExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnImportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarExcelActionPerformed(evt);
            }
        });

        btnExportar.setBackground(new java.awt.Color(153, 204, 255));
        btnExportar.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnExportar.setForeground(new java.awt.Color(0, 0, 0));
        btnExportar.setText("Exportar Excel");
        btnExportar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Importa tus datos desde un archivo excel");

        jLabel7.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Exporta tus productos a un archivo excel");

        jLabel8.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Categoria:");

        txtCategoria.setBackground(new java.awt.Color(204, 204, 204));
        txtCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCategoriaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnImportarExcel, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                    .addComponent(btnExportar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnGuardar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8))
                        .addGap(35, 35, 35)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProducto, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPrecio, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCategoria, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(28, 28, 28))
            .addComponent(jSeparator1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGuardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminar)
                    .addComponent(btnModificar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(4, 4, 4)
                .addComponent(btnImportarExcel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExportar)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        ListCategoria.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        ListCategoria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ListCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ListCategoriaActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Categoria:");

        jLabel9.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Filtrar por:");

        textTotalRegistros.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        textTotalRegistros.setForeground(new java.awt.Color(0, 0, 0));
        textTotalRegistros.setText("Total registros:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator3)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMenuPricipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(textTotalRegistros)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 887, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ListCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12))))
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMenuPricipal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalir))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ListCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textTotalRegistros)
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Acción para cerrar la aplicación.
     */
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // Cerrar la aplicacion
        System.exit(0);
    }//GEN-LAST:event_btnSalirActionPerformed
    /**
     * Acción para volver al menú principal.
     */
    private void btnMenuPricipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuPricipalActionPerformed
        // cerrar la ventana actual 
        this.setVisible(false);
        // Abrir una ventana de magerDB
        FormMainWindow dbManager = new FormMainWindow();
        dbManager.setVisible(true);
    }//GEN-LAST:event_btnMenuPricipalActionPerformed

    private void txtPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioActionPerformed
    }//GEN-LAST:event_txtPrecioActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
    }//GEN-LAST:event_txtCodigoActionPerformed
    /**
     * Método para guardar un nuevo producto.
     */
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // Obtener los datos del formulario
        Producto producto = obtenerDatosFormulario();

        // Verificar si el producto es nulo (error en la conversión)
        if (producto == null) {
            return; // Ya se ha mostrado un mensaje de error en obtenerDatosFormulario()
        }

        // Validar que los campos no estén vacíos
        if (producto.getProducto().isEmpty() || producto.getCantidad() <= 0 || producto.getPrecio() <= 0 || producto.getCodigo().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar todos los datos correctamente.");
            return;
        }

        try {
            // Agregar o actualizar el producto en la base de datos
            productoCtrl.agregarOActualizarProductoSiExiste(producto);

            // Actualizar los datos de la tabla
            productoCtrl.mostrarProductos(tbProductos);

            // Limpiar los campos del formulario después de guardar
            limpiarFormulario();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el producto: " + e.getMessage());
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // Obtener el ID del producto seleccionado en la tabla
        int idProducto = productoCtrl.obtenerIdProductoSeleccionado(tbProductos);

        // Obtener información del producto para mostrar en el formulario de confirmación
        Producto producto = productoCtrl.obtenerProductoPorId(idProducto);
        String infoProducto = producto.toString();

        // Llamar al formulario de confirmación de eliminación
        FormConfEliminacion formConfEliminacion = new FormConfEliminacion(idProducto, tbProductos, infoProducto);
        formConfEliminacion.setVisible(true);
    }//GEN-LAST:event_btnEliminarActionPerformed

    // Metodo para modificar un producto selecionado
    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // Obtener los datos del formulario
        Producto producto = obtenerDatosFormulario();

        // Verificar si el producto es nulo (error en la conversión)
        if (producto == null) {
            return; // Ya se ha mostrado un mensaje de error en obtenerDatosFormulario()
        }

        // Validar que los campos no estén vacíos
        if (producto.getProducto().isEmpty() || producto.getCantidad() <= 0 || producto.getPrecio() <= 0 || producto.getCodigo().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar todos los datos correctamente.");
            return;
        }

        try {
            // Obtener el ID del producto seleccionado en la tabla
            int idProducto = productoCtrl.obtenerIdProductoSeleccionado(tbProductos);

            if (idProducto != -1) {
                producto.setId(idProducto);

                // Llamar al método del controlador para modificar el producto
                productoCtrl.modificarProducto(producto);

                // Actualizar la tabla de productos
                productoCtrl.mostrarProductos(tbProductos);

                // Limpiar los campos del formulario
                limpiarFormulario();

            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un producto para modificar.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al modificar el producto: " + e.getMessage());
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnImportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarExcelActionPerformed
        // Seleccionar la ruta del archivo Excel usando el método SeleccionRuta
        String rutaExcel = SelecionRuta.obtenerRutaAbrir("Seleccionar archivo Excel");

        // Verificar si el usuario seleccionó un archivo
        if (rutaExcel != null) {
            // Configurar nombre de la base de datos y tabla
            String nombreTabla = "productos";

            // Crear y mostrar la pantalla de carga
            PantallaCarga pantalla = new PantallaCarga(FormRegistroProductos.this);
            pantalla.setMensaje("Importando datos desde Excel...");

            // Crear tarea en segundo plano
            SwingWorker<Void, Void> tarea = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        // Llamar al método de importar Excel
                        ExcelSQLiteManager.importarExcel(rutaExcel, nombreTabla);
                    } catch (Exception e) {
                        throw new Exception("Error durante la importación: " + e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void done() {
                    pantalla.cerrar();
                    try {
                        get(); // Verifica si ocurrió alguna excepción
                        productoCtrl.mostrarProductos(tbProductos); // Actualiza la tabla de productos
                        JOptionPane.showMessageDialog(FormRegistroProductos.this, "Importación completada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(FormRegistroProductos.this,
                                e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            // Mostrar la pantalla de carga y ejecutar la tarea
            pantalla.mostrar();
            tarea.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Importación cancelada.", "Cancelado", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnImportarExcelActionPerformed

    private void btnExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarActionPerformed
        // Usar la clase SeleccionRuta para obtener la ruta de exportación
        String rutaExcel = SelecionRuta.obtenerRuta("Exportar archivo Excel", "Inventario_productos_exportado.xlsx", ".xlsx");

        if (rutaExcel != null) {
            // Crear una pantalla de carga
            PantallaCarga pantallaCarga = new PantallaCarga((JFrame) SwingUtilities.getWindowAncestor(this));

            // Crear un SwingWorker para ejecutar la exportación en segundo plano
            SwingWorker<Void, Void> exportacionWorker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    pantallaCarga.mostrar(); // Mostrar la pantalla de carga
                    pantallaCarga.setMensaje("Exportando datos...");

                    // Aquí llamamos al método de exportación
                    ExcelSQLiteManager.exportarDatosAExcel("productos", rutaExcel);

                    return null;
                }

                @Override
                protected void done() {
                    pantallaCarga.cerrar(); // Cerrar la pantalla de carga
                    JOptionPane.showMessageDialog(null, "Exportación completada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            };

            exportacionWorker.execute(); // Iniciar la tarea en segundo plano
        }
    }//GEN-LAST:event_btnExportarActionPerformed

    private void ListCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ListCategoriaActionPerformed
        // Obtener la categoría seleccionada del combo box
        String categoriaSeleccionada = (String) ListCategoria.getSelectedItem();

        // Mostrar los productos filtrados por la categoría seleccionada
        productoCtrl.mostrarProductosPorCategoria(tbProductos, categoriaSeleccionada);

        // Actualizar el total de registros
        String totalRegistros = String.valueOf(productoCtrl.contarRegistros(categoriaSeleccionada));
        textTotalRegistros.setText("Total registros: " + totalRegistros);
    }//GEN-LAST:event_ListCategoriaActionPerformed

    private void txtCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCategoriaActionPerformed
    // Metodo para Limpiar el formulario  
    private void limpiarFormulario() {
        txtProducto.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtCodigo.setText("");
        txtCategoria.setText("");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormRegistroProductos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ListCategoria;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnImportarExcel;
    private javax.swing.JButton btnMenuPricipal;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTable tbProductos;
    private javax.swing.JLabel textTotalRegistros;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCategoria;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtProducto;
    // End of variables declaration//GEN-END:variables
}
