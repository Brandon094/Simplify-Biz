package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.config.SelecionRuta;
import com.mycompany.zl_solucion_integral.config.UtilVentanas;
import javax.swing.JOptionPane;

/*
 * Esta clase representa la ventana principal para administrar la base de datos 
 * en la aplicación 'Simplify-Biz'. Ofrece opciones para registrar usuarios, 
 * productos, ventas e informes. La interfaz gráfica es generada usando Swing.
 * 
 * Al interactuar con esta ventana, los administradores pueden elegir diferentes 
 * funcionalidades relacionadas con la gestión de datos en la aplicación.
 * 
 * Controles disponibles:
 * - Registro de usuarios
 * - Registro de productos
 * - Registro de ventas
 * - Generación de informes
 * - Cerrar la aplicación
 * 
 * La funcionalidad de cada botón abre una nueva ventana que corresponde a la 
 * acción seleccionada, y la interfaz actual se oculta mientras el usuario interactúa 
 * con la nueva interfaz.
 * 
 * Autor: ChopCode Solutions
 */
public class FormMainWindow extends javax.swing.JFrame {

    private FormRegistroUsuarios formUsuarios; // Ventana de registro de usuarios
    private FormRegistroProductos formProductos; // Ventana de registro de productos
    private FormRegistroVentas formVentas; // Ventana de registro de ventas
    private FormMenuInformes formInformes; // Ventana de informes
    private FormInfoVentas formInfoVentas; // Ventana de información de ventas

    SelecionRuta ruta = new SelecionRuta(); // Utilidad para seleccionar rutas de archivos

    /**
     * Constructor de la clase que inicializa la ventana 'manager_DB'. Invoca al
     * método initComponents() para configurar los componentes gráficos.
     */
    public FormMainWindow() {
        initComponents(); // Inicializa los componentes gráficos
        setTitle("Menu administrador"); // Establece el título de la ventana
        UtilVentanas.aplicarPantallaCompleta(this); // Aplica pantalla completa
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btnSalir = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        btnRegistroProductos = new javax.swing.JButton();
        btnRegistroUsuarios = new javax.swing.JButton();
        btnRegistroVentas = new javax.swing.JButton();
        btnInformes = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        btnCerrarSecion = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Console", 2, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Administrar base de datos");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnSalir.setBackground(new java.awt.Color(153, 255, 255));
        btnSalir.setFont(new java.awt.Font("Candara", 3, 12)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(0, 0, 0));
        btnSalir.setText("Salir de la app");
        btnSalir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        btnRegistroProductos.setBackground(new java.awt.Color(153, 204, 255));
        btnRegistroProductos.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnRegistroProductos.setForeground(new java.awt.Color(0, 0, 0));
        btnRegistroProductos.setText("Registro de productos");
        btnRegistroProductos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegistroProductos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRegistroProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistroProductosActionPerformed(evt);
            }
        });

        btnRegistroUsuarios.setBackground(new java.awt.Color(153, 204, 255));
        btnRegistroUsuarios.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnRegistroUsuarios.setForeground(new java.awt.Color(0, 0, 0));
        btnRegistroUsuarios.setText("Registro de usuarios");
        btnRegistroUsuarios.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegistroUsuarios.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRegistroUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistroUsuariosActionPerformed(evt);
            }
        });

        btnRegistroVentas.setBackground(new java.awt.Color(153, 204, 255));
        btnRegistroVentas.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnRegistroVentas.setForeground(new java.awt.Color(0, 0, 0));
        btnRegistroVentas.setText("Registro de ventas");
        btnRegistroVentas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegistroVentas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRegistroVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistroVentasActionPerformed(evt);
            }
        });

        btnInformes.setBackground(new java.awt.Color(153, 204, 255));
        btnInformes.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnInformes.setForeground(new java.awt.Color(0, 0, 0));
        btnInformes.setText("Informes Ventas");
        btnInformes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnInformes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnInformes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInformesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnRegistroProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegistroUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegistroVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInformes, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(116, 116, 116))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(btnRegistroProductos)
                .addGap(18, 18, 18)
                .addComponent(btnRegistroUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRegistroVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnInformes, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(86, Short.MAX_VALUE))
        );

        btnCerrarSecion.setBackground(new java.awt.Color(153, 255, 255));
        btnCerrarSecion.setFont(new java.awt.Font("Candara", 3, 12)); // NOI18N
        btnCerrarSecion.setForeground(new java.awt.Color(0, 0, 0));
        btnCerrarSecion.setText("Cerrar sesìon");
        btnCerrarSecion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCerrarSecion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSecionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 295, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 295, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jSeparator1))
            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(btnCerrarSecion, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCerrarSecion)
                    .addComponent(btnSalir))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Método para cerrar la aplicación.
     *
     * @param evt Evento de acción generado al hacer clic en el botón "Salir".
     */

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        System.exit(0); // Cierra la applicacion
    }//GEN-LAST:event_btnSalirActionPerformed

    /**
     * Método para abrir la ventana de registro de usuarios.
     *
     * @param evt Evento de acción generado al hacer clic en el botón "Registro
     * de usuarios".
     */
    private void btnRegistroUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistroUsuariosActionPerformed
        this.setVisible(false); // Oculta la ventana actual
        if (formUsuarios == null) {
            formUsuarios = new FormRegistroUsuarios(); // Crea una nueva instancia si no existe
        }
        formUsuarios.setVisible(true); // Muestra la ventana de registro de usuarios
    }//GEN-LAST:event_btnRegistroUsuariosActionPerformed

    /**
     * Método para abrir la ventana de registro de productos.
     *
     * @param evt Evento de acción generado al hacer clic en el botón "Registro
     * de productos".
     */
    private void btnRegistroProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistroProductosActionPerformed
        this.setVisible(false); // Oculta la ventana actual
        if (formProductos == null) {
            formProductos = new FormRegistroProductos(); // Crea una nueva instancia si no existe
        }
        formProductos.setVisible(true); // Muestra la ventana de registro de productos
    }//GEN-LAST:event_btnRegistroProductosActionPerformed

    /**
     * Método para abrir la ventana de registro de ventas.
     *
     * @param evt Evento de acción generado al hacer clic en el botón "Registro
     * de ventas".
     */
    private void btnRegistroVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistroVentasActionPerformed
        this.setVisible(false); // Oculta la ventana actual
        if (formVentas == null) {
            formVentas = new FormRegistroVentas(true); // Crea una nueva instancia si no existe
        }
        formVentas.setVisible(true); // Muestra la ventana de registro de ventas
    }//GEN-LAST:event_btnRegistroVentasActionPerformed

    /**
     * Método para abrir la ventana de informes.
     *
     * @param evt Evento de acción generado al hacer clic en el botón "Informes
     * Ventas".
     */
    private void btnInformesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInformesActionPerformed
        this.setVisible(false); // Oculta la ventana actual
        if (formInfoVentas == null) {
            formInfoVentas = new FormInfoVentas(); // Crea una nueva instancia si no existe
        }
        formInfoVentas.setVisible(true); // Muestra la ventana de informes
    }//GEN-LAST:event_btnInformesActionPerformed

    /**
     * Método para cerrar la sesión actual y volver a la ventana de inicio de
     * sesión.
     *
     * @param evt Evento de acción generado al hacer clic en el botón "Cerrar
     * sesión".
     */
    private void btnCerrarSecionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSecionActionPerformed
        int confirmed = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas cerrar sesión?", "Confirmación", JOptionPane.YES_NO_OPTION);

        if (confirmed == JOptionPane.YES_OPTION) {
            this.setVisible(false); // Oculta la ventana actual
            FormLogIn log_In = new FormLogIn(); // Crea una nueva instancia de la ventana de inicio de sesión
            log_In.setVisible(true); // Muestra la ventana de inicio de sesión
        }
    }//GEN-LAST:event_btnCerrarSecionActionPerformed

    /**
     * Método principal para ejecutar la ventana principal.
     *
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormMainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSecion;
    private javax.swing.JButton btnInformes;
    private javax.swing.JButton btnRegistroProductos;
    private javax.swing.JButton btnRegistroUsuarios;
    private javax.swing.JButton btnRegistroVentas;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables
}
