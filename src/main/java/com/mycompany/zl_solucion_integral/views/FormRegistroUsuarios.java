package com.mycompany.zl_solucion_integral.views;

import com.mycompany.zl_solucion_integral.config.Listener;
import com.mycompany.zl_solucion_integral.config.UtilVentanas;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.models.Usuario;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/*
* Ventana para registrar y gestionar usuarios. Controla el registro,
* modifica y eliminacion de usuarios.
*
* @author Dazac
*/
public class FormRegistroUsuarios extends javax.swing.JFrame {

    // Instancia del controlador del usuario
    UsuarioController usuarioCtrl = new UsuarioController();

    Listener listenerTb = new Listener();

    // Constructor           
    public FormRegistroUsuarios() {
        initComponents();
        setTitle("Registro de usuarios");
        UtilVentanas.aplicarPantallaCompleta(this);

        // Rellenar el JComboBox de categorias
        initComboBoxRol();

        // Mostrar los datos 
        usuarioCtrl.mostrarUsuarios(tbUsuarios);

        // Crear los campos de texto a llenar con los datos de la fila seleccionada
        JTextField[] camposTexto = {txtNombre, txtTelefono, txtEmail, txtRol};

        // Definir los índices de las columnas que quieres mostrar en los campos de texto
        int[] columnas = {1, 3, 2, 4, 5}; // Suponiendo que las columnas son: Nombre, Cantidad, Precio, Código

        // Inicializar el método para obtener los datos del producto seleccionado
        listenerTb.agregarListenerTabla(tbUsuarios, camposTexto, columnas);
    }

    /**
     * Método para inicializar las opciones de rol en el JComboBox.
     */
    private void initComboBoxRol() {
        // Define un nuevo modelo con los valores deseados
        DefaultComboBoxModel<String> modeloRol = new DefaultComboBoxModel<>();
        modeloRol.addElement("Todas"); // Opción para mostrar todos
        modeloRol.addElement("0: Vendedor");
        modeloRol.addElement("1: Administrador");
        modeloRol.addElement("2: Cliente");

        // Asigna el modelo al JComboBox
        ListRol.setModel(modeloRol);
    }

    /**
     * Método para obtener los datos del formulario.
     *
     * @return Un objeto Usuario con los datos del formulario.
     */
    private Usuario obtenerDatosFormulario() {
        // Obtener los datos de los campos
        String nombre = txtNombre.getText().toLowerCase();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText().toLowerCase();
        String rol = txtRol.getText();
        String contraseña = txtContraseña.getText();

        // Crear y retornar un objeto Usuario con los datos
        return new Usuario(0, nombre, telefono, email, contraseña, rol);
    }

    /**
     * Método para limpiar los campos del formulario.
     */
    public void limpiarCampos() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtRol.setText("");
        txtContraseña.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        btnGuardarUsuario = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtContraseña = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtRol = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbUsuarios = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        btnMenuPrincipal = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        ListRol = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ingresa los datos para registrar el usuario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Candara", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Ingrese la telefono:");

        jLabel5.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Ingrese el email:");

        txtTelefono.setBackground(new java.awt.Color(204, 204, 204));
        txtTelefono.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N

        txtEmail.setBackground(new java.awt.Color(204, 204, 204));
        txtEmail.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Ingrese el nombre: ");

        txtNombre.setBackground(new java.awt.Color(204, 204, 204));
        txtNombre.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });

        btnGuardarUsuario.setBackground(new java.awt.Color(153, 204, 255));
        btnGuardarUsuario.setFont(new java.awt.Font("Candara", 3, 14)); // NOI18N
        btnGuardarUsuario.setForeground(new java.awt.Color(0, 0, 0));
        btnGuardarUsuario.setText("Guardar");
        btnGuardarUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarUsuarioActionPerformed(evt);
            }
        });

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

        jLabel2.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Ingrese la contraseña:");

        txtContraseña.setBackground(new java.awt.Color(204, 204, 204));
        txtContraseña.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        txtContraseña.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContraseñaActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Ingrese el rol:");

        txtRol.setBackground(new java.awt.Color(204, 204, 204));
        txtRol.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Ingrese 1 para rol de Administrador, 0 para rol de vendedor");

        jLabel8.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("1 o 0");

        jLabel9.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Para los clientes se les asigna una contraseña generica");

        jLabel10.setFont(new java.awt.Font("Candara", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Para el rol se asigna el 2 que referencia al cliente");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel6)
                .addGap(63, 63, 63)
                .addComponent(txtRol)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNombre, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtContraseña, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(6, 6, 6))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel7)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnGuardarUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardarUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminar)
                    .addComponent(btnModificar))
                .addGap(26, 26, 26))
        );

        tbUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbUsuarios);

        jLabel1.setFont(new java.awt.Font("Lucida Console", 2, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Registro de Usuarios");

        btnCerrar.setBackground(new java.awt.Color(153, 255, 255));
        btnCerrar.setFont(new java.awt.Font("Candara", 3, 12)); // NOI18N
        btnCerrar.setForeground(new java.awt.Color(0, 0, 0));
        btnCerrar.setText("Cerrar sesìon");
        btnCerrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        btnMenuPrincipal.setBackground(new java.awt.Color(153, 255, 255));
        btnMenuPrincipal.setFont(new java.awt.Font("Candara", 3, 12)); // NOI18N
        btnMenuPrincipal.setForeground(new java.awt.Color(0, 0, 0));
        btnMenuPrincipal.setText("Menu Principal");
        btnMenuPrincipal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMenuPrincipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuPrincipalActionPerformed(evt);
            }
        });

        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Filtrar por:");

        ListRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ListRol.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ListRol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ListRolActionPerformed(evt);
            }
        });

        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Rol");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnMenuPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ListRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(17, 17, 17))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMenuPrincipal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(ListRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

        /**
     * Acción para guardar un nuevo usuario.
     */
    private void btnGuardarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarUsuarioActionPerformed
        // Obtener los datos del formulario
        Usuario usuario = obtenerDatosFormulario();

        // Validar que los campos no estén vacíos
        if (usuario.getNombre().isEmpty() || usuario.getTelefono().isEmpty() || usuario.getEmail().isEmpty()
                || usuario.getRol().isEmpty() || usuario.getContraseña().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar todos los datos.");
            return;
        }

        // Validar formato del correo electrónico
        if (!usuario.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "El correo electrónico no tiene un formato válido.");
            return;
        }

        // Validar el rol (debe ser 1 o 0)
        try {
            int rolNumero = Integer.parseInt(usuario.getRol());
            // Verificar que el rol sea 1 (admin) o 0 (vendedor)
            if (rolNumero != 1 && rolNumero != 0) {
                JOptionPane.showMessageDialog(this, "El rol debe ser 1 para administrador o 0 para vendedor.");
                return;
            }

            // Guardar el nuevo usuario en la base de datos
            usuarioCtrl.agregarUsuario(usuario);

            // Actualizar los datos de la tabla
            usuarioCtrl.mostrarUsuarios(tbUsuarios);
            // Limpiar campos del formulario
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El rol debe ser un número válido.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el usuario: " + e.getMessage());
        }
    }//GEN-LAST:event_btnGuardarUsuarioActionPerformed

     /**
     * Acción para eliminar un usuario seleccionado.
     */
    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // Obtener el ID del usuario seleccionado en la tabla
        int idUsuario = usuarioCtrl.obtenerIdUsuarioSeleccionado(tbUsuarios);

        // Llamar al método para eliminar el usuario
        usuarioCtrl.eliminarUsuario(idUsuario, tbUsuarios);

        // Actualizar la tabla de usuarios después de la eliminación
        usuarioCtrl.mostrarUsuarios(tbUsuarios);
    }//GEN-LAST:event_btnEliminarActionPerformed

    /**
     * Acción para modificar un usuario seleccionado.
     */
    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // Obtener datos del formulario
        Usuario usuario = obtenerDatosFormulario();

        // Validar que los campos no estén vacíos
        if (usuario.getNombre().isEmpty() || usuario.getTelefono().isEmpty() || usuario.getEmail().isEmpty()
                || usuario.getRol().isEmpty() || usuario.getContraseña().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar todos los datos.");
            return;
        }

        try {
            // Obtener el ID del usuario seleccionado en la tabla
            int idUsuario = usuarioCtrl.obtenerIdUsuarioSeleccionado(tbUsuarios);

            if (idUsuario != -1) {
                // Modificar el usuario
                usuarioCtrl.modificarUsuario(usuario.getNombre(), usuario.getTelefono(), usuario.getEmail(),
                        usuario.getRol(), usuario.getContraseña(), idUsuario);

                // Actualizar la tabla de usuarios
                usuarioCtrl.mostrarUsuarios(tbUsuarios);

                // Limpiar los campos después de modificar
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario para modificar.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al modificar el usuario: " + e.getMessage());
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void txtContraseñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContraseñaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtContraseñaActionPerformed

    /**
     * Acción para cerrar la sesión y volver a la ventana de inicio de sesión.
     */
    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        // ocultar la interfaz actual
        this.setVisible(false);
        // Instanciar la clase de logIn
        FormLogIn log_In = new FormLogIn();
        // Mostrar la interfaz de logIn
        log_In.setVisible(true);
    }//GEN-LAST:event_btnCerrarActionPerformed

    /**
     * Acción para volver al menú principal.
     */
    private void btnMenuPrincipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuPrincipalActionPerformed
        // Ocultar la interfaz de registro usuarios
        this.setVisible(false);
        // abrir la nueva interfaz del menu principal
        FormMainWindow dbMag = new FormMainWindow();
        dbMag.setVisible(true);
    }//GEN-LAST:event_btnMenuPrincipalActionPerformed

    /**
     * Acción para filtrar usuarios por rol.
     */
    private void ListRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ListRolActionPerformed
        String rolSeleccionado = (String) ListRol.getSelectedItem();
        usuarioCtrl.mostrarUsuariosPorRol(tbUsuarios, rolSeleccionado);
    }//GEN-LAST:event_ListRolActionPerformed
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
            java.util.logging.Logger.getLogger(FormRegistroUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormRegistroUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormRegistroUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormRegistroUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormRegistroUsuarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ListRol;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardarUsuario;
    private javax.swing.JButton btnMenuPrincipal;
    private javax.swing.JButton btnModificar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JTable tbUsuarios;
    private javax.swing.JTextField txtContraseña;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtRol;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
