package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.config.Listener;
import com.mycompany.zl_solucion_integral.config.PantallaCarga;
import com.mycompany.zl_solucion_integral.config.SelecionRuta;

import com.mycompany.zl_solucion_integral.config.UtilVentanas;
import com.mycompany.zl_solucion_integral.controllers.VentasController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author Dazac
 */
public class FormInfoVentas extends javax.swing.JFrame {

    FormMainWindow formManagerWindow = new FormMainWindow();

    VentasController ventasCtrl = new VentasController();
    Listener listenerTb = new Listener();

    public FormInfoVentas() {
        initComponents();
        setTitle("Informe Ventas");
        UtilVentanas.aplicarPantallaCompleta(this);
        ventasCtrl.MostrarVentas(tbVentasInformes);
        contarVentasUnicas();
        calcularPrecioTotalVentas();
        // Crear los campos de texto a llenar con los datos de la fila seleccionada
        JTextField[] camposTexto = {txtFechaInicio, txtFecha, txtPagoConfirmado};

        // Definir los índices de las columnas que quieres mostrar en los campos de texto
        int[] columnas = {10, 10, 8};

        // Inicializar el método para obtener los datos del producto seleccionado
        listenerTb.agregarListenerTabla(tbVentasInformes, camposTexto, columnas);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbVentasInformes = new javax.swing.JTable();
        btnMenuInformes = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        btnSalir = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtFechaInicio = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtFechaFinalizacion = new javax.swing.JTextField();
        btnFiltrar01 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtFecha = new javax.swing.JTextField();
        btnFiltrar02 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        textNumVentas = new javax.swing.JLabel();
        textPrecioTotalVentas = new javax.swing.JLabel();
        btnReiniciarFiltros = new javax.swing.JButton();
        btnExportarExcel = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        txtPagoConfirmado = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnCambiarValor = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Console", 2, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Informe de ventas");

        tbVentasInformes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tbVentasInformes);

        btnMenuInformes.setBackground(new java.awt.Color(153, 255, 255));
        btnMenuInformes.setFont(new java.awt.Font("Candara", 3, 12)); // NOI18N
        btnMenuInformes.setForeground(new java.awt.Color(0, 0, 0));
        btnMenuInformes.setText("Menu principal");
        btnMenuInformes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuInformesActionPerformed(evt);
            }
        });

        btnSalir.setBackground(new java.awt.Color(153, 255, 255));
        btnSalir.setFont(new java.awt.Font("Candara", 3, 12)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(0, 0, 0));
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtrar por rango de fechas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Fecha de Inicio:");

        txtFechaInicio.setBackground(new java.awt.Color(204, 204, 204));
        txtFechaInicio.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        txtFechaInicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFechaInicioActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Fecha Finalizacion:");

        txtFechaFinalizacion.setBackground(new java.awt.Color(204, 204, 204));
        txtFechaFinalizacion.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        txtFechaFinalizacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFechaFinalizacionActionPerformed(evt);
            }
        });

        btnFiltrar01.setBackground(new java.awt.Color(153, 204, 255));
        btnFiltrar01.setFont(new java.awt.Font("Candara", 3, 12)); // NOI18N
        btnFiltrar01.setForeground(new java.awt.Color(0, 0, 0));
        btnFiltrar01.setText("Filtrar");
        btnFiltrar01.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFiltrar01.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar01ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFechaFinalizacion, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                            .addComponent(txtFechaInicio)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFiltrar01)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3))
                    .addComponent(txtFechaFinalizacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFiltrar01, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtrar por una fecha", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Fecha:");

        txtFecha.setBackground(new java.awt.Color(204, 204, 204));
        txtFecha.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N

        btnFiltrar02.setBackground(new java.awt.Color(153, 204, 255));
        btnFiltrar02.setFont(new java.awt.Font("Candara", 3, 12)); // NOI18N
        btnFiltrar02.setForeground(new java.awt.Color(0, 0, 0));
        btnFiltrar02.setText("Filtrar");
        btnFiltrar02.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFiltrar02.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrar02ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(29, 29, 29)
                        .addComponent(txtFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnFiltrar02)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFiltrar02)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Informacion de registros", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));

        textNumVentas.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        textNumVentas.setForeground(new java.awt.Color(255, 255, 255));
        textNumVentas.setText("N° Total de ventas:");

        textPrecioTotalVentas.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        textPrecioTotalVentas.setForeground(new java.awt.Color(255, 255, 255));
        textPrecioTotalVentas.setText("Precio total de ventas:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textPrecioTotalVentas)
                    .addComponent(textNumVentas))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textNumVentas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textPrecioTotalVentas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnReiniciarFiltros.setBackground(new java.awt.Color(153, 204, 255));
        btnReiniciarFiltros.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnReiniciarFiltros.setForeground(new java.awt.Color(0, 0, 0));
        btnReiniciarFiltros.setText("Reiniciar filtros");
        btnReiniciarFiltros.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReiniciarFiltros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReiniciarFiltrosActionPerformed(evt);
            }
        });

        btnExportarExcel.setBackground(new java.awt.Color(153, 204, 255));
        btnExportarExcel.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnExportarExcel.setForeground(new java.awt.Color(0, 0, 0));
        btnExportarExcel.setText("Exportar Excel");
        btnExportarExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarExcelActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Confirmacion de pago", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        txtPagoConfirmado.setBackground(new java.awt.Color(204, 204, 204));
        txtPagoConfirmado.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        txtPagoConfirmado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPagoConfirmadoActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Cambie el valor si es necesario:");

        btnCambiarValor.setBackground(new java.awt.Color(153, 204, 255));
        btnCambiarValor.setFont(new java.awt.Font("Candara", 3, 12)); // NOI18N
        btnCambiarValor.setForeground(new java.awt.Color(0, 0, 0));
        btnCambiarValor.setText("Cambiar valor");
        btnCambiarValor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCambiarValor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarValorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnCambiarValor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPagoConfirmado)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPagoConfirmado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCambiarValor, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 949, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnReiniciarFiltros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnExportarExcel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(30, 30, 30))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnMenuInformes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSalir)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnReiniciarFiltros)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnExportarExcel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMenuInformes)
                    .addComponent(btnSalir))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMenuInformesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuInformesActionPerformed
        this.setVisible(false);
        if (formManagerWindow == null) {
            formManagerWindow = new FormMainWindow();
        }
        formManagerWindow.setVisible(true);
    }//GEN-LAST:event_btnMenuInformesActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnSalirActionPerformed

    private void txtFechaInicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaInicioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaInicioActionPerformed

    private void txtFechaFinalizacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaFinalizacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaFinalizacionActionPerformed

    private void btnFiltrar01ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar01ActionPerformed
        String fechaInicio = txtFechaInicio.getText();
        String fechaFinalizacion = txtFechaFinalizacion.getText();

        // validar q los campos no esten vacios 
        if (fechaInicio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La fecha de inicio no debe ser vacia.");
        }
        if (fechaFinalizacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La fecha de finalización no debe ser vacía.\nSe pone la fecha actual en caso de estar vacía la fecha de finalización.");

            // Obtener la fecha formateada
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Ajusta el formato según lo necesites
            String fechaActual = now.format(formatter);

            txtFechaFinalizacion.setText(fechaActual);
            return;
        }
        ventasCtrl.mostrarFechasDefinidas(tbVentasInformes, fechaInicio, fechaFinalizacion);
        contarVentasUnicas();
        calcularPrecioTotalVentas();

    }//GEN-LAST:event_btnFiltrar01ActionPerformed

    public void contarVentasUnicas() {
        // Obtiene el modelo de la tabla
        javax.swing.table.TableModel modelo = tbVentasInformes.getModel();

        // Usamos un Set para almacenar los Id únicos
        java.util.Set<String> ventasUnicas = new java.util.HashSet<>();

        // Recorremos todas las filas de la tabla y agregamos los Id únicos al Set
        for (int i = 0; i < modelo.getRowCount(); i++) {
            String idVenta = modelo.getValueAt(i, 0).toString(); // Supongamos que el Id está en la columna 0
            ventasUnicas.add(idVenta);
        }

        // El tamaño del Set representa el número de ventas únicas
        int totalVentasUnicas = ventasUnicas.size();

        // Muestra el número total de ventas únicas en el campo correspondiente
        textNumVentas.setText("Nª Total de ventas: " + String.valueOf(totalVentasUnicas));
    }

    // Metodo para sumar la columna 9 precio total para mostrarlo
    private void calcularPrecioTotalVentas() {
        // Obtiene el modelo de la tabla
        javax.swing.table.TableModel modelo = tbVentasInformes.getModel();

        // Variable para almacenar la suma total
        double sumaTotal = 0.0;

        // Recorremos todas las filas de la tabla
        for (int i = 0; i < modelo.getRowCount(); i++) {
            // Obtenemos el valor de la columna 12 (índice 11 porque las columnas empiezan en 0)
            String valorColumna = modelo.getValueAt(i, 11).toString();

            // Convertimos el valor a double y lo sumamos (manejo de excepciones por seguridad)
            try {
                double precio = Double.parseDouble(valorColumna);
                sumaTotal += precio;
            } catch (NumberFormatException e) {
                System.out.println("Error al convertir a número: " + valorColumna);
            }
        }

        // Muestra la suma total en el campo correspondiente
        textPrecioTotalVentas.setText("Precio total de ventas: " +String.format("%.2f", sumaTotal));
    }


    private void btnFiltrar02ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrar02ActionPerformed
        String fecha = txtFecha.getText();

        ventasCtrl.mostrarVentasPorDia(tbVentasInformes, fecha);
        contarVentasUnicas();
        calcularPrecioTotalVentas();
    }//GEN-LAST:event_btnFiltrar02ActionPerformed

    private void btnReiniciarFiltrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReiniciarFiltrosActionPerformed
        ventasCtrl.MostrarVentas(tbVentasInformes);
        // Limpiando los campos de texto
        txtFechaInicio.setText("");
        txtFechaFinalizacion.setText("");
        txtFecha.setText("");
        txtPagoConfirmado.setText("");
        // Contar la ventas y el total de las ventas
        contarVentasUnicas();
        calcularPrecioTotalVentas();

    }//GEN-LAST:event_btnReiniciarFiltrosActionPerformed

    private void btnExportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarExcelActionPerformed
        // Obtener la ruta de exportación utilizando la clase SeleccionRuta
        String rutaExcel = SelecionRuta.obtenerRuta("Exportar archivo Excel", "InformeVentas.xlsx", ".xlsx");

        if (rutaExcel != null) {
            // Crear una pantalla de carga
            PantallaCarga pantallaCarga = new PantallaCarga((JFrame) SwingUtilities.getWindowAncestor(this));

            // Crear un SwingWorker para ejecutar la exportación en segundo plano
            SwingWorker<Void, Void> exportacionWorker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    pantallaCarga.mostrar(); // Mostrar la pantalla de carga
                    pantallaCarga.setMensaje("Exportando datos visibles de la tabla...");

                    // Exportar los datos visibles de la tabla a un archivo Excel
                    ventasCtrl.exportarDatosTablaAExcel(tbVentasInformes, rutaExcel);

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
    }//GEN-LAST:event_btnExportarExcelActionPerformed

    private void btnCambiarValorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarValorActionPerformed
        String pagoConfirmado = txtPagoConfirmado.getText();
        int idSelecionado = ventasCtrl.obtenerIdVentaSeleccionado(tbVentasInformes);
        ventasCtrl.actualizarPagoConfirmado(idSelecionado, pagoConfirmado);
        ventasCtrl.MostrarVentas(tbVentasInformes);
    }//GEN-LAST:event_btnCambiarValorActionPerformed

    private void txtPagoConfirmadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPagoConfirmadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPagoConfirmadoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormInfoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormInfoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormInfoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormInfoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormInfoVentas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCambiarValor;
    private javax.swing.JButton btnExportarExcel;
    private javax.swing.JButton btnFiltrar01;
    private javax.swing.JButton btnFiltrar02;
    private javax.swing.JButton btnMenuInformes;
    private javax.swing.JButton btnReiniciarFiltros;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable tbVentasInformes;
    private javax.swing.JLabel textNumVentas;
    private javax.swing.JLabel textPrecioTotalVentas;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtFechaFinalizacion;
    private javax.swing.JTextField txtFechaInicio;
    private javax.swing.JTextField txtPagoConfirmado;
    // End of variables declaration//GEN-END:variables
}
