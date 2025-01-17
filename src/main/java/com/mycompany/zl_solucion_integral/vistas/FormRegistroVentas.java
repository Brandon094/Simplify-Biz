package com.mycompany.zl_solucion_integral.vistas;

import com.mycompany.zl_solucion_integral.config.EnvioCotizacion;
import com.mycompany.zl_solucion_integral.config.PantallaCarga;
import com.mycompany.zl_solucion_integral.config.SelecionRuta;
import com.mycompany.zl_solucion_integral.config.UtilVentanas;
import com.mycompany.zl_solucion_integral.controllers.ProductoController;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.controllers.VentasController;
import com.mycompany.zl_solucion_integral.models.Producto;
import com.mycompany.zl_solucion_integral.models.Sesion;
import com.mycompany.zl_solucion_integral.models.Usuario;
import com.mycompany.zl_solucion_integral.models.Venta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * La clase {registroVentas} representa la interfaz gráfica para el registro,
 * modificación y eliminación de ventas en la aplicación {zl_solucion_integral}.
 * Esta ventana permite a los usuarios ingresar detalles de ventas, visualizar
 * ventas existentes en una tabla y gestionar las ventas mediante las opciones
 * proporcionadas.
 *
 * <p>
 * Dependiendo del rol del usuario (administrador o no), se muestra u oculta el
 * botón para regresar al menú principal.
 * </p>
 *
 * @autor Dazac
 */
public class FormRegistroVentas extends javax.swing.JFrame {

    // Instancia de la clase sesion
    private Sesion sesion;
    Usuario cliente = new Usuario();
    Venta venta = new Venta();   
    VentasController ventasCtrl = new VentasController(cliente, sesion);
    UsuarioController usuarioCtrl = new UsuarioController();
    ProductoController productoCtrl = new ProductoController();
    EnvioCotizacion envioCotizacion = new EnvioCotizacion();
    private int cantidadDisponibleEnMemoria;

    // Define el ArrayList de ventas a nivel de la clase
    private List<Venta> ventasCotizadas = new ArrayList<>();
    private List<Producto> productosVendidos = new ArrayList<>();

    // Inicialización del modelo
    private DefaultListModel<String> modeloLista = new DefaultListModel<>();

    // Obtener el usuario logueado
    String vendedor = sesion.getUsuarioLogueado();

    /**
     * Constructor de la clase {registroVentas}.
     *
     * <p>
     * Inicializa los componentes gráficos y configura la visibilidad del botón
     * {btnMenuPrincipal} basado en el rol del usuario.
     * </p>
     *
     * @param esAdmin Un booleano que indica si el usuario es administrador.
     */
    public FormRegistroVentas(boolean esAdmin) {
        initComponents();
        setTitle("Registro de ventas");
        this.venta = new Venta();
        listProCotizados.setModel(modeloLista); // Asignar modelo al JList
        UtilVentanas.aplicarPantallaCompleta(this);
        ventasCtrl.MostrarVentas(tbVentas); // Mostrar las ventas 
        btnMenuPrincipal.setVisible(esAdmin); // Ocultar el boton inicalmente
        tbVentas.setVisible(esAdmin); // Ocultar la tabla inicalmente
        textVendedor.setText("Vendedor: " + vendedor);               
    }

    // Método para obtener los datos del formulario
    private Venta obtenerDatosFormulario() {
        try {
            // Obtener los datos del formulario
            // Datos de producto
            String codigo = txtCodigo.getText().trim();
            String productoName = txtProducto.getText().trim();
            String cantidadStr = txtCantidad.getText().trim();
            String descuentoStr = txtDescuento.getText().trim();
            // Datos cliente
            String clienteName = txtCliente.getText().trim();
            String noCcCliente = txtNoCc.getText().trim();
            String telefonoCliente = txtTelefonoCliente.getText().trim();
            String correoCliente = txtCorreoCliente.getText().trim();
            String NIT = txtNit.getText().trim();
            String DIR = txtDir.getText().trim();
            // Los datos de contraseña y rol 
            String contraseña = " ";
            String rol = "2";

            // Verificar si se seleccionó efectivo o crédito
            String metodoPago = "Efectivo"; // Valor por defecto
            if (checkCredito.isSelected()) {
                metodoPago = "Crédito";  // Si se selecciona el JCheckBox de crédito
            } else if (checkEfectivo.isSelected()) {
                metodoPago = "Efectivo";  // Si se selecciona el JCheckBox de efectivo
            }

            // Validar datos
            if (!validarProducto(codigo, productoName, cantidadStr) || !validarCliente(clienteName, noCcCliente, telefonoCliente, correoCliente, NIT, DIR)) {
                return null;
            }

            // Buscar producto
            Producto productoEncontrado = !codigo.isEmpty() ? productoCtrl.buscarProductoPorCodigo(codigo)
                    : productoCtrl.buscarProductoPorNombre(productoName);

            if (productoEncontrado == null) {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // Validar cantidad solicitada contra la cantidad disponible en memoria (no la base de datos)
            int cantidadSolicitada = Integer.parseInt(cantidadStr);

            cantidadDisponibleEnMemoria = productoEncontrado.getCantidad();
            if (cantidadSolicitada > cantidadDisponibleEnMemoria) {
                JOptionPane.showMessageDialog(this, "La cantidad solicitada supera la cantidad disponible en inventario (" + cantidadDisponibleEnMemoria + ").", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            // Acctualizar la cantidad disponible en memoria
            cantidadDisponibleEnMemoria = cantidadDisponibleEnMemoria - cantidadSolicitada;

            // Calcular total            
            double descuento = descuentoStr.isEmpty() ? 0.0 : Double.parseDouble(descuentoStr);
            double precio = productoEncontrado.getPrecio();
            double total = precio * cantidadSolicitada - (precio * cantidadSolicitada * descuento / 100);

            // Configurar cliente
            cliente.setNombre(clienteName);
            cliente.setNoCc(noCcCliente);
            cliente.setTelefono(telefonoCliente);
            cliente.setEmail(correoCliente);
            cliente.setNIT(NIT);
            cliente.setDIR(DIR);
            cliente.setRol(rol);
            // crear una contraseña 
            contraseña = clienteName.replaceAll("\\s+", "") + noCcCliente;
            // Asigna la contraseña creada
            cliente.setContraseña(contraseña);

            // Inicializar venta si es null
            if (venta == null) {
                venta = new Venta();
            }
            venta.setCliente(cliente); // Asegúrate de asignar el cliente a la venta

            // Cantidad solicitada
            productoEncontrado.setCantidadSolicitada(cantidadSolicitada);

            // Asignar la forma de pago a la venta
            venta.setMetodoPago(metodoPago);
            // Retornar venta
            return new Venta(productoEncontrado.getCodigo(), productoEncontrado, productoEncontrado.getCantidadSolicitada(), LocalDate.now(), vendedor, venta.getCliente(), total, descuento, cantidadDisponibleEnMemoria, metodoPago);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private boolean validarCliente(String clienteName, String noCcCliente, String telefonoCliente, String correoCliente, String NIT, String DIR) {
        if (clienteName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (noCcCliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el número de cédula del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (telefonoCliente.isEmpty() && correoCliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el teléfono o el correo electrónico.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!telefonoCliente.isEmpty() && !telefonoCliente.matches("\\d{7,10}")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe contener entre 7 y 10 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!correoCliente.isEmpty() && !correoCliente.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "El correo electrónico no tiene un formato válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (NIT.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El NIT no debe estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (DIR.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La Dirección debe ser llenada.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validar que se haya seleccionado uno de los métodos de pago
        if (!checkCredito.isSelected() && !checkEfectivo.isSelected()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un método de pago (Efectivo o Crédito).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean validarProducto(String codigo, String productoName, String cantidadStr) {
        if (codigo.isEmpty() && productoName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el código o el nombre del producto.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar la cantidad.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // Método para validar campos antes de procesar la venta
    public boolean validarCampos() {
        if (ventasCotizadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe realizar una cotización antes de registrar la venta.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        btnMenuPrincipal = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        txtNameCliente = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        btnAgregarAlCarrito = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtProducto = new javax.swing.JTextField();
        txtNoCc = new javax.swing.JTextField();
        txtNoCcCliente = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtDescuento = new javax.swing.JTextField();
        txtNoTel = new javax.swing.JLabel();
        txtCorreoCl = new javax.swing.JLabel();
        txtTelefonoCliente = new javax.swing.JTextField();
        txtCorreoCliente = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtNit = new javax.swing.JTextField();
        txtDir = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        checkEfectivo = new javax.swing.JCheckBox();
        checkCredito = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        textNombre = new javax.swing.JLabel();
        textCedula = new javax.swing.JLabel();
        textTelefono = new javax.swing.JLabel();
        textCorreo = new javax.swing.JLabel();
        textNIT = new javax.swing.JLabel();
        textDIR = new javax.swing.JLabel();
        textMetodoPago = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        textVendedor = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listProCotizados = new javax.swing.JList<>();
        btnEliminar = new javax.swing.JButton();
        textValorTotal = new javax.swing.JLabel();
        btnCotizar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnGuardarVenta = new javax.swing.JButton();
        btnLimpiarCarrito = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbVentas = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Console", 0, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Registro de Ventas");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        btnCerrar.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        btnCerrar.setText("Cerrar secion");
        btnCerrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        btnMenuPrincipal.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        btnMenuPrincipal.setText("Menu Principal");
        btnMenuPrincipal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMenuPrincipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuPrincipalActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("INFORMACION DEL PRODUCTO"));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Ingrese la cantidad:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Ingrese el codigo: ");

        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        txtNameCliente.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNameCliente.setText("Nombre cliente:");

        txtCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClienteActionPerformed(evt);
            }
        });

        btnAgregarAlCarrito.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnAgregarAlCarrito.setText("Agregar al carrito");
        btnAgregarAlCarrito.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregarAlCarrito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarAlCarritoActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Ingrese el producto:");

        txtNoCcCliente.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNoCcCliente.setText("Numero de cedula:");

        jLabel8.setText("INFORMACION DEL CLIENTE");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Ingrese el descuento: ");

        txtNoTel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNoTel.setText("Numero de telefono:");

        txtCorreoCl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCorreoCl.setText("Correo electronico:");

        txtTelefonoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefonoClienteActionPerformed(evt);
            }
        });

        jLabel7.setText("NIT:");

        jLabel9.setText("DIR:");

        jLabel11.setText("Metodo de pago:");

        checkEfectivo.setText("Efectivo");
        checkEfectivo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        checkCredito.setText("Credito");
        checkCredito.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNameCliente)
                                    .addComponent(txtNoCcCliente))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                    .addComponent(txtNoCc)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel10))
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCantidad, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                                    .addComponent(txtDescuento)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel3))
                                .addGap(21, 21, 21)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtProducto)
                                    .addComponent(txtCodigo)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNoTel)
                                    .addComponent(txtCorreoCl)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtNit, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(16, 16, 16)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtDir))
                                    .addComponent(txtTelefonoCliente)
                                    .addComponent(txtCorreoCliente)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addGap(18, 18, 18)
                                        .addComponent(checkCredito)
                                        .addGap(18, 18, 18)
                                        .addComponent(checkEfectivo)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(btnAgregarAlCarrito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNameCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNoCc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNoCcCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNoTel)
                    .addComponent(txtTelefonoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCorreoCl)
                    .addComponent(txtCorreoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(txtNit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(checkCredito)
                    .addComponent(checkEfectivo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAgregarAlCarrito))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("CARRITO DE COMPRAS"));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Informacion de cliente"));
        jPanel2.setToolTipText("");

        textNombre.setText("Nombre:");

        textCedula.setText("N° Cedula:");

        textTelefono.setText("N° Telefono:");

        textCorreo.setText("Email: ");

        textNIT.setText("NIT:");

        textDIR.setText("DIR:");

        textMetodoPago.setText("Metodo de pago: ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textNombre)
                    .addComponent(textCedula)
                    .addComponent(textTelefono)
                    .addComponent(textCorreo)
                    .addComponent(textNIT)
                    .addComponent(textDIR)
                    .addComponent(textMetodoPago))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textNombre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textCedula)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textTelefono)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textCorreo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textNIT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textDIR)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textMetodoPago))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Informacion de vendedor"));

        textVendedor.setText("Vendedor:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textVendedor)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(textVendedor)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Informacion de producto"));

        listProCotizados.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listProCotizados);

        btnEliminar.setText("Eliminar");
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        textValorTotal.setText("Total:");

        btnCotizar.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        btnCotizar.setText("Cotizacion");
        btnCotizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCotizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCotizarActionPerformed(evt);
            }
        });

        jLabel2.setText("Enviar cotizacion:");

        jLabel5.setText("Eliminar del carrito:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(textValorTotal)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCotizar, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(textValorTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCotizar)
                        .addGap(2, 2, 2)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminar)))
                .addContainerGap())
        );

        btnGuardarVenta.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnGuardarVenta.setText("Confirmar Venta");
        btnGuardarVenta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarVentaActionPerformed(evt);
            }
        });

        btnLimpiarCarrito.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnLimpiarCarrito.setText("Limpiar carrito de compras");
        btnLimpiarCarrito.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLimpiarCarrito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarCarritoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnLimpiarCarrito, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardarVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardarVenta)
                    .addComponent(btnLimpiarCarrito))
                .addContainerGap())
        );

        tbVentas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tbVentas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnMenuPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(19, 19, 19))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMenuPrincipal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrar)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        // ocultar la interfaz actual
        this.setVisible(false);

        // Instanciar la clase de logIn
        FormLogIn log_In = new FormLogIn();
        // Mostrar la interfaz de logIn
        log_In.setVisible(true);
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnMenuPrincipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuPrincipalActionPerformed
        // Ocultar la interfaz de registro ventas
        this.setVisible(false);
        // abrir la nueva interfaz
        FormMainWindow dbMag = new FormMainWindow();
        dbMag.setVisible(true);
    }//GEN-LAST:event_btnMenuPrincipalActionPerformed

    // Actualizar el JList y total
    private void mostrarInformacionCotizacion(List<Venta> ventas) {
        modeloLista.clear();
        textNombre.setText("Nombre: " + cliente.getNombre());
        textCedula.setText("N° Cedula: " + cliente.getNoCc());
        textTelefono.setText("N° telefono: " + cliente.getTelefono());
        textCorreo.setText("Email: " + cliente.getEmail());
        textNIT.setText("NIT: " + cliente.getNIT());
        textDIR.setText("DIR: " + cliente.getDIR());
        textMetodoPago.setText("Metodo de pago: " + venta.getMetodoPago());

        for (Venta venta : ventas) {
            modeloLista.addElement(
                    "<html><hr>Producto: " + venta.getProducto().getProducto() + "<br>"
                    + "Código: " + venta.getProducto().getCodigo() + "<br>"
                    + "Cantidad: " + venta.getCantidad() + "<br>"
                    + "Precio: " + venta.getTotal() + "<hr></html>"
            );
        }
        actualizarPrecioTotal();
    }

    // Metodo para limpiar las cajas de texto
    private void limpiarCajas() {
        txtCodigo.setText("");
        txtProducto.setText("");
        txtCantidad.setText("");
        txtDescuento.setText("");
    }

    // Metodo para crear una cotizacion
    private void btnCotizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCotizarActionPerformed
        if (cliente == null || ventasCtrl == null || envioCotizacion == null) {
            JOptionPane.showMessageDialog(this, "Faltan datos para realizar la cotización.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar si el carrito de compras está vacío
        if (ventasCotizadas == null || ventasCotizadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito de compras está vacío. No se puede generar una cotización.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String telefono = cliente.getTelefono();
        String correo = cliente.getEmail();
        String numeroCotizacion = ventasCtrl.obtenerYActualizarNumeroCotizacion();

        // Guardar el número de cotización en la base de datos
        ventasCtrl.guardarNumeroCotizacionEnBaseDeDatos(numeroCotizacion);

        // Crear la pantalla de carga
        final PantallaCarga pantallaCarga = new PantallaCarga(this);  // Hacerla final
        pantallaCarga.setMensaje("Generando cotización, por favor espere...");

        // Mostrar la pantalla de carga en el hilo de la interfaz
        SwingUtilities.invokeLater(() -> pantallaCarga.mostrar());

        // Crear un hilo para generar la cotización
        new Thread(() -> {
            try {// Paso 1: Abrir la plantilla
                SwingUtilities.invokeLater(() -> pantallaCarga.setProgreso(15)); // Progreso: 15%
                // Aquí iría la lógica para abrir la plantilla (si es necesario)
                // Seleccionar plantilla y ruta de guardado
                String rutaPlantilla = SelecionRuta.obtenerRuta("Seleccionar Plantilla de Cotización", "Plantilla_Cotizacion.xlsx", ".xlsx");
                if (rutaPlantilla == null) {
                    JOptionPane.showMessageDialog(this, "No se seleccionó ninguna plantilla. Operación cancelada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String rutaArchivo = SelecionRuta.obtenerRuta("Guardar Cotización", "cotizacion_cliente_" + cliente.getNoCc() + ".xlsx", ".xlsx");
                if (rutaArchivo == null) {
                    JOptionPane.showMessageDialog(this, "No se seleccionó ninguna ubicación para guardar el archivo. Operación cancelada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Paso 2: Iniciar el proceso de generación (por ejemplo, insertar datos en la plantilla)
                SwingUtilities.invokeLater(() -> pantallaCarga.setProgreso(30)); // Progreso: 30%                
                // Llamada real para generar el archivo de cotización
                boolean resultado = ventasCtrl.generarArchivoCotizacionConPlantilla(rutaPlantilla, rutaArchivo, numeroCotizacion, ventasCotizadas);
                if (!resultado) {
                    JOptionPane.showMessageDialog(this, "Error al generar la cotización.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Paso 3: Escribir el archivo de cotización
                SwingUtilities.invokeLater(() -> pantallaCarga.setProgreso(90)); // Progreso: 90%              

                JOptionPane.showMessageDialog(this, "Cotización generada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Cuadro de diálogo para elegir el método de envío (WhatsApp o Correo)
                String[] opcionesEnvio = {"WhatsApp", "Correo Electrónico"};
                int opcionSeleccionada = JOptionPane.showOptionDialog(this,
                        "Seleccione el método de envío de la cotización:",
                        "Seleccionar Método de Envío",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, opcionesEnvio, opcionesEnvio[0]);

                // Verificar la opción seleccionada y proceder con el envío
                if (opcionSeleccionada == 0) {
                    // Enviar por WhatsApp
                    if (telefono != null && !telefono.isEmpty()) {
                        envioCotizacion.enviarPorWhatsApp(telefono, rutaArchivo);
                    } else {
                        JOptionPane.showMessageDialog(this, "El número de teléfono del cliente no está disponible.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (opcionSeleccionada == 1) {
                    // Enviar por Correo
                    if (correo != null && !correo.isEmpty()) {
                        envioCotizacion.enviarPorCorreo(correo, rutaArchivo);
                    } else {
                        JOptionPane.showMessageDialog(this, "El correo electrónico del cliente no está disponible.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No se seleccionó ningún método de envío.", "Información", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Cerrar la pantalla de carga al finalizar el proceso
                SwingUtilities.invokeLater(() -> pantallaCarga.cerrar());
            }
        }).start();
    }//GEN-LAST:event_btnCotizarActionPerformed

    // Metodo para Guardar la venta en la base de datos
    private void btnGuardarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarVentaActionPerformed
        // Validar si hay productos cotizados
        if (ventasCotizadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe agregar mínimo 1 producto al carrito antes de registrar la venta.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar campos del cliente
        if (cliente.getNombre().isEmpty() || cliente.getNoCc().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar los datos del cliente antes de guardar la venta.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar que hay suficiente stock para cada producto en el carrito
        for (Venta v : ventasCotizadas) {
            Producto producto = v.getProducto();
            int cantidadSolicitada = v.getCantidad();
            int cantidadDisponible = producto.getCantidad();

            if (cantidadSolicitada > cantidadDisponible) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente para el producto: " + producto.getProducto()
                        + ". Disponible: " + cantidadDisponible + ", Solicitado: " + cantidadSolicitada,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return; // Detener el proceso si hay productos con stock insuficiente
            }
        }

        // Intentar guardar la venta
        try {
            // Invocar el método del controlador para guardar la venta
            ventasCtrl.guardarVenta(venta, productosVendidos, tbVentas);
            usuarioCtrl.agregarUsuario(cliente);
            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(this, "Venta registrada con éxito.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Limpiar el carrito y las cajas de texto
            ventasCotizadas.clear();
            productosVendidos.clear();
            modeloLista.clear();
            limpiarCajas();
            actualizarPrecioTotal();

            // Actualizar la tabla de ventas
            ventasCtrl.MostrarVentas(tbVentas);

        } catch (Exception e) {
            // Mostrar mensaje de error
            JOptionPane.showMessageDialog(this, "Ocurrió un error al registrar la venta: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarVentaActionPerformed
    // Metodo para eliminar un producto del carrito de compras
    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        int selectedIndex = listProCotizados.getSelectedIndex();
        if (selectedIndex != -1) {
            ventasCotizadas.remove(selectedIndex);
            modeloLista.remove(selectedIndex);
            actualizarPrecioTotal();
            JOptionPane.showMessageDialog(this, "Producto eliminado del carrito.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLimpiarCarritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarCarritoActionPerformed
        // Limpiar la lista de productos cotizados
        ventasCotizadas.clear(); // Vacía el ArrayList de ventas cotizadas
        modeloLista.clear(); // Limpia el modelo asociado al JList

        // Opcional: Limpiar también los campos de información del cliente y el total
        textNombre.setText("Nombre: ");
        textCedula.setText("N° Cédula: ");
        textTelefono.setText("N° Teléfono: ");
        textCorreo.setText("Email: ");
        textValorTotal.setText("Precio Total: 0.0");

        JOptionPane.showMessageDialog(this, "La lista de productos cotizados ha sido limpiada.",
                "Lista Limpiada", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnLimpiarCarritoActionPerformed

    private void txtTelefonoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefonoClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefonoClienteActionPerformed

    // Metodo para agregar los productos al carrito de compras 
    private void btnAgregarAlCarritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarAlCarritoActionPerformed
        venta = obtenerDatosFormulario();

        if (venta != null) {
            Producto producto = venta.getProducto();
            int cantidadSolicitada = venta.getCantidad();
            int cantidadDisponible = producto.getCantidad();

            // Validar si la cantidad solicitada es mayor que la cantidad disponible en el inventario
            if (cantidadSolicitada > cantidadDisponible) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente para el producto: " + producto.getProducto()
                        + ". Disponible: " + cantidadDisponible + ", Solicitado: " + cantidadSolicitada,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return; // No continuar si la cantidad solicitada es mayor que la disponible
            }

            // Buscar si el producto ya está en el carrito
            boolean productoExistente = false;
            for (Venta v : ventasCotizadas) {
                if (v.getProducto().getCodigo().equals(producto.getCodigo())) {
                    // Producto ya existe en el carrito, actualizar cantidad y total
                    v.setCantidad(v.getCantidad() + cantidadSolicitada);
                    v.setTotal(v.getCantidad() * v.getProducto().getPrecio()); // Actualizar total
                    productoExistente = true;
                    break;
                }
            }

            // Si el producto no estaba en el carrito, agregarlo
            if (!productoExistente) {
                ventasCotizadas.add(venta);
                productosVendidos.add(producto); // Aquí ya tiene la cantidad ajustada
            }

            // Actualizar la información de la cotización en el JList
            mostrarInformacionCotizacion(ventasCotizadas);

            JOptionPane.showMessageDialog(this, "Producto agregado al carrito con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCajas();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos requeridos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
//GEN-LAST:event_btnAgregarAlCarritoActionPerformed

    private void txtClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClienteActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed
    // Metodo para actualizar el precio total de la venta
    private void actualizarPrecioTotal() {
        double total = ventasCotizadas.stream().mapToDouble(Venta::getTotal).sum();
        textValorTotal.setText("Precio Total: " + String.format("%.2f", total));
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormRegistroVentas(true).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarAlCarrito;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnCotizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardarVenta;
    private javax.swing.JButton btnLimpiarCarrito;
    private javax.swing.JButton btnMenuPrincipal;
    private javax.swing.JCheckBox checkCredito;
    private javax.swing.JCheckBox checkEfectivo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JList<String> listProCotizados;
    private javax.swing.JTable tbVentas;
    private javax.swing.JLabel textCedula;
    private javax.swing.JLabel textCorreo;
    private javax.swing.JLabel textDIR;
    private javax.swing.JLabel textMetodoPago;
    private javax.swing.JLabel textNIT;
    private javax.swing.JLabel textNombre;
    private javax.swing.JLabel textTelefono;
    private javax.swing.JLabel textValorTotal;
    private javax.swing.JLabel textVendedor;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JLabel txtCorreoCl;
    private javax.swing.JTextField txtCorreoCliente;
    private javax.swing.JTextField txtDescuento;
    private javax.swing.JTextField txtDir;
    private javax.swing.JLabel txtNameCliente;
    private javax.swing.JTextField txtNit;
    private javax.swing.JTextField txtNoCc;
    private javax.swing.JLabel txtNoCcCliente;
    private javax.swing.JLabel txtNoTel;
    private javax.swing.JTextField txtProducto;
    private javax.swing.JTextField txtTelefonoCliente;
    // End of variables declaration//GEN-END:variables
}
