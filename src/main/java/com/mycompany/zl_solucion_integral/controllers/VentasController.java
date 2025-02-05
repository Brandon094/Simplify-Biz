package com.mycompany.zl_solucion_integral.controllers;

/**
 * Controlador para gestionar las operaciones con ventas.
 * <p>
 * Esta clase proporciona métodos para agregar, modificar, eliminar y mostrar
 * ventas en la base de datos.
 * </p>
 * Utiliza una instancia de `ConexionDB` para interactuar con la base de datos.
 *
 * @author Dazac
 */
import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.config.SelecionRuta;
import com.mycompany.zl_solucion_integral.models.Producto;
import com.mycompany.zl_solucion_integral.models.Sesion;
import com.mycompany.zl_solucion_integral.models.Usuario;
import com.mycompany.zl_solucion_integral.models.Venta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.logging.Level;
import javax.swing.JTable;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.*;

import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import javax.swing.table.TableModel;

/* Clase encargada de manejar las operaciones 
relacionadas con las ventas en la base de datos*/
public class VentasController {

    SelecionRuta rutaDB = new SelecionRuta();
    private Usuario cliente;
    private ConexionDB conexion; // Variable global para la conexión
    private Logger logger = Logger.getLogger(VentasController.class.getName());
    private Sesion sesion;    

    // Constructor
    public VentasController(Usuario cliente, Sesion sesion) {
        this.conexion = new ConexionDB(rutaDB.cargarRutaBaseDatos()); // Instancia de la clase de conexión        
        this.sesion = sesion;
        this.cliente = cliente;
    }

    // Constructor adicional sin parámetros de Usuario y Sesion
    public VentasController() {
        this.conexion = new ConexionDB(rutaDB.cargarRutaBaseDatos()); // Instancia de la clase de conexión
    }

    public void guardarVenta(final Venta venta, List<Producto> productosVendidos, JTable tablaVentas) {
        String sqlInsertVenta = "INSERT INTO ventas (cliente, cc_cliente, vendedor, fecha, total, metodo_pago, pago_confirmado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlInsertDetalleVenta = "INSERT INTO detalles_venta (venta_id, producto, cantidad, codigo, precio, total) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE productos SET cantidad = cantidad - ? WHERE codigo = ? AND cantidad >= ?";

        Connection conn = null;
        PreparedStatement psVenta = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psStock = null;
        ResultSet generatedKeys = null;

        try {
            // Establecer conexión y preparar transacción
            conn = conexion.obtenerConexion();
            conn.setAutoCommit(false);

            // Verificar stock de productos antes de comenzar
            for (Producto producto : productosVendidos) {
                if (producto.getCantidad() < producto.getCantidadSolicitada()) {
                    throw new SQLException("Stock insuficiente para el producto: " + producto.getProducto()
                            + ". Disponible: " + producto.getCantidad()
                            + ", solicitado: " + producto.getCantidadSolicitada());
                }
            }

            // Determinar el valor de la columna "pago_confirmado"
            String pagoConfirmado = venta.getMetodoPago().equalsIgnoreCase("crédito") ? "deudor" : "";

            // Insertar venta general
            psVenta = conn.prepareStatement(sqlInsertVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setString(1, venta.getCliente().getNombre());
            psVenta.setString(2, venta.getCliente().getNoCc());
            psVenta.setString(3, venta.getVendedor());
            psVenta.setDate(4, java.sql.Date.valueOf(venta.getFecha()));
            psVenta.setDouble(5, venta.getTotal());
            psVenta.setString(6, venta.getMetodoPago());
            psVenta.setString(7, pagoConfirmado);

            psVenta.executeUpdate();
            generatedKeys = psVenta.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("No se pudo obtener el ID de la venta.");
            }
            int ventaId = generatedKeys.getInt(1);

            // Insertar detalles de venta y actualizar stock
            psDetalle = conn.prepareStatement(sqlInsertDetalleVenta);
            psStock = conn.prepareStatement(sqlUpdateStock);

            for (Producto producto : productosVendidos) {
                // Insertar en detalles_venta
                psDetalle.setInt(1, ventaId);
                psDetalle.setString(2, producto.getProducto());
                psDetalle.setInt(3, producto.getCantidadSolicitada());
                psDetalle.setString(4, producto.getCodigo());
                psDetalle.setDouble(5, producto.getPrecio());
                psDetalle.setDouble(6, producto.getPrecio() * producto.getCantidadSolicitada());
                psDetalle.executeUpdate();

                // Actualizar stock del producto
                psStock.setInt(1, producto.getCantidadSolicitada());
                psStock.setString(2, producto.getCodigo());
                psStock.setInt(3, producto.getCantidad());
                if (psStock.executeUpdate() == 0) {
                    throw new SQLException("Error al actualizar stock para el producto: " + producto.getProducto());
                }
            }

            // Confirmar transacción
            conn.commit();
            JOptionPane.showMessageDialog(null, "Venta guardada y stock actualizado exitosamente.");
            MostrarVentas(tablaVentas);

        } catch (SQLException e) {
            // Revertir transacción en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.log(Level.SEVERE, "Error al revertir la transacción", rollbackEx);
                }
            }
            logger.log(Level.SEVERE, "Error al procesar la venta", e);
            JOptionPane.showMessageDialog(null, "Error al procesar venta: " + e.getMessage());
        } finally {
            // Cerrar recursos
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (psVenta != null) {
                    psVenta.close();
                }
                if (psDetalle != null) {
                    psDetalle.close();
                }
                if (psStock != null) {
                    psStock.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conexion.cerrarConexion(conn);
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error al cerrar conexiones", ex);
            }
        }
    }

    // Metodo para modificar una venta
    public void modificarVenta(final Venta venta, int idVenta, JTable tablaVentas) {
        String sqlUpdateVenta = "UPDATE ventas SET producto = ?, cantidad = ?, codigo = ?, precio = ?, cliente = ?, cc_cliente = ?, vendedor = ?, fecha = ?, total = ? WHERE id = ?";
        String sqlUpdateStockRestaurar = "UPDATE productos SET cantidad = cantidad + ? WHERE codigo = ?";  // Para restaurar el stock antiguo
        String sqlUpdateStockNuevo = "UPDATE productos SET cantidad = cantidad - ? WHERE codigo = ? AND cantidad >= ?";  // Para aplicar el nuevo stock

        Connection conn = null;
        PreparedStatement psVenta = null;
        PreparedStatement psStockRestaurar = null;
        PreparedStatement psStockNuevo = null;

        try {
            conn = conexion.obtenerConexion();
            conn.setAutoCommit(false);  // Iniciar una transacción

            // Restaurar el stock del producto basado en la venta anterior
            psStockRestaurar = conn.prepareStatement(sqlUpdateStockRestaurar);
            psStockRestaurar.setInt(1, obtenerCantidadAnterior(idVenta));  // Cantidad anterior a restaurar
            psStockRestaurar.setString(2, venta.getCodigo());  // Código del producto
            psStockRestaurar.executeUpdate();

            // Actualizar el stock del producto con la nueva cantidad
            psStockNuevo = conn.prepareStatement(sqlUpdateStockNuevo);
            psStockNuevo.setInt(1, venta.getCantidad());  // Cantidad a restar
            psStockNuevo.setString(2, venta.getCodigo());  // Código del producto
            psStockNuevo.setInt(3, venta.getCantidad());  // Verifica que el stock actual sea suficiente

            int rowsAffected = psStockNuevo.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No hay suficiente stock disponible para la nueva cantidad.");
            }

            // Actualizar los datos de la venta
            psVenta = conn.prepareStatement(sqlUpdateVenta);
            psVenta.setString(1, venta.getProducto().getProducto());  // Producto
            psVenta.setInt(2, venta.getCantidad());  // Nueva cantidad
            psVenta.setString(3, venta.getCodigo());  // Código del producto
            psVenta.setDouble(4, venta.getProducto().getPrecio());  // Precio
            psVenta.setString(5, cliente.getNombre());  // Cliente
            psVenta.setString(6, cliente.getNoCc());  // CC Cliente
            psVenta.setString(7, sesion.getUsuarioLogueado());  // Vendedor
            psVenta.setDate(8, java.sql.Date.valueOf(venta.getFecha()));  // Fecha
            psVenta.setDouble(9, venta.getTotal());  // Total
            psVenta.setInt(10, idVenta);  // ID de la venta a modificar

            psVenta.executeUpdate();

            // Confirmar la transacción
            conn.commit();
            JOptionPane.showMessageDialog(null, "Venta modificada y stock actualizado exitosamente.");

            // Actualizar la tabla de ventas
            MostrarVentas(tablaVentas);
        } catch (SQLException e) {
            // Revertir la transacción en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.log(Level.SEVERE, "Error al revertir la transacción", rollbackEx);
                }
            }
            logger.log(Level.SEVERE, "Error al modificar la venta", e);
            JOptionPane.showMessageDialog(null, "Error al modificar venta: " + e.getMessage());
        } finally {
            // Cerrar las conexiones
            try {
                if (psVenta != null) {
                    psVenta.close();
                }
                if (psStockRestaurar != null) {
                    psStockRestaurar.close();
                }
                if (psStockNuevo != null) {
                    psStockNuevo.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);  // Restaurar el auto-commit
                }
                conexion.cerrarConexion(conn);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error al cerrar conexiones", ex);
            }
        }
    }

// Método para obtener la cantidad anterior de una venta
    private int obtenerCantidadAnterior(int idVenta) {
        String sql = "SELECT cantidad FROM ventas WHERE id = ?";
        try (Connection conn = conexion.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("cantidad");
            } else {
                throw new SQLException("No se encontró la venta con ID: " + idVenta);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener la cantidad anterior de la venta", e);
            return 0;
        }
    }

    // Metodo para obtener el id de la venta selecionada 
    public int obtenerIdVentaSeleccionado(final JTable tabla) {
        // Obtener el índice de la fila seleccionada
        int filaSeleccionada = tabla.getSelectedRow();

        // Verificar si hay una fila seleccionada
        if (filaSeleccionada != -1) {
            // Obtener el valor de la primera columna (ID del producto) y convertirlo a entero
            return Integer.parseInt(tabla.getValueAt(filaSeleccionada, 0).toString());
        } else {
            // No se ha seleccionado ninguna fila, devolver -1
            return -1;
        }
    }

    public void guardarNumeroCotizacionEnBaseDeDatos(String numeroCotizacion) {
        String sqlActualizar = "UPDATE configuracion SET ultimoNumeroCotizacion = ? WHERE id = 1";

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sqlActualizar)) {
            pstmt.setString(1, numeroCotizacion);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al guardar el número de cotización en la base de datos", e);
        }
    }

    public String obtenerYActualizarNumeroCotizacion() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd");
        String fecha = formatoFecha.format(new Date());
        int siguienteNumero = 1; // Valor predeterminado si no hay registros previos

        // Consultas SQL
        String sqlConsulta = "SELECT ultimoNumeroCotizacion FROM configuracion WHERE id = 1";
        String sqlActualizar = "UPDATE configuracion SET ultimoNumeroCotizacion = ? WHERE id = 1";

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement psConsulta = conn.prepareStatement(sqlConsulta); PreparedStatement psActualizar = conn.prepareStatement(sqlActualizar)) {

            // Consultar el último número de cotización
            ResultSet rs = psConsulta.executeQuery();
            if (rs.next()) {
                String ultimoNumero = rs.getString("ultimoNumeroCotizacion");
                if (ultimoNumero != null && ultimoNumero.startsWith(fecha)) {
                    // Extraer el número actual después del guión y sumarle 1
                    siguienteNumero = Integer.parseInt(ultimoNumero.split("-")[1]) + 1;
                } else {
                    // Si la fecha cambió, reinicia el conteo para la nueva fecha
                    siguienteNumero = 1;
                }
            }

            // Generar el nuevo número de cotización
            String nuevoNumero = fecha + "-" + String.format("%03d", siguienteNumero);

            // Actualizar en la base de datos
            psActualizar.setString(1, nuevoNumero);
            psActualizar.executeUpdate();

            // Devolver el nuevo número generado
            return nuevoNumero;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener y actualizar el número de cotización", e);
            return null; // Manejo en caso de error
        }
    }

    /**
     * Método para mostrar todos las ventas en una tabla.
     *
     * Este método realiza una consulta a la base de datos para obtener todos
     * las ventas almacenados y los muestra en una tabla gráfica (`JTable`). Se
     * actualiza la tabla con las columnas de "Id", "Producto", "Precio",
     * "Cantidad", "Código" y "Precio Total".
     *
     * @param tablaVentas La tabla (`JTable`) donde se mostrarán los productos.
     */
    public void MostrarVentas(JTable tablaVentas) {
        // Crear un nuevo modelo de tabla
        final DefaultTableModel modelo = new DefaultTableModel();

        // Definir las columnas del modelo
        modelo.addColumn("Id");
        modelo.addColumn("Producto");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Código");
        modelo.addColumn("Precio");
        modelo.addColumn("Cliente");
        modelo.addColumn("CC Cliente");
        modelo.addColumn("Metodo pago");
        modelo.addColumn("Pago confirmado");
        modelo.addColumn("Vendedor");
        modelo.addColumn("Fecha");
        modelo.addColumn("Precio Total");
        // Establecer el modelo de tabla vacío antes de cargar los datos
        tablaVentas.setModel(modelo);

        // Consulta SQL para obtener todas las ventas y sus detalles
        final String sql = "SELECT v.id, d.producto, d.cantidad, d.codigo, d.precio, v.cliente, v.cc_cliente, v.vendedor, v.metodo_pago, v.fecha, v.pago_confirmado "
                + "FROM ventas v "
                + "JOIN detalles_venta d ON v.id = d.venta_id";

        try (Connection conn = conexion.obtenerConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                double precio = rs.getDouble("precio");
                int cantidad = rs.getInt("cantidad");
                double precioTotal = precio * cantidad;

                Date fecha = rs.getDate("fecha");
                String fechaFormateada = formatearFecha(fecha);

                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("producto"),
                    cantidad,
                    rs.getString("codigo"),
                    precio,
                    rs.getString("cliente"),
                    rs.getString("cc_cliente"),
                    rs.getString("metodo_pago"),
                    rs.getString("pago_confirmado"),
                    rs.getString("vendedor"),
                    fechaFormateada,
                    precioTotal
                });
            }

            tablaVentas.setModel(modelo);

            // Ajustar el tamaño de las columnas
            tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(50);  // Id
            tablaVentas.getColumnModel().getColumn(1).setPreferredWidth(250); // Producto
            tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(80);  // Cantidad
            tablaVentas.getColumnModel().getColumn(3).setPreferredWidth(100); // Código
            tablaVentas.getColumnModel().getColumn(4).setPreferredWidth(75);  // Precio
            tablaVentas.getColumnModel().getColumn(5).setPreferredWidth(150); // Cliente
            tablaVentas.getColumnModel().getColumn(6).setPreferredWidth(100); // CC Cliente
            tablaVentas.getColumnModel().getColumn(7).setPreferredWidth(100); // metodo pago
            tablaVentas.getColumnModel().getColumn(8).setPreferredWidth(100); // pago confirmado
            tablaVentas.getColumnModel().getColumn(9).setPreferredWidth(100); // Vendedor
            tablaVentas.getColumnModel().getColumn(10).setPreferredWidth(100); // Fecha
            tablaVentas.getColumnModel().getColumn(11).setPreferredWidth(100); // Precio Total

            // Opcional: ajustar automáticamente las alturas de las filas si el contenido lo requiere
            //tablaVentas.setRowHeight(25);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al mostrar ventas", e);
            JOptionPane.showMessageDialog(null, "Error al mostrar ventas: " + e.getMessage());
        }
    }

    /**
     * Método para mostrar las ventas en una tabla filtradas por rango de fechas
     * con formato "dd/MM/yyyy".
     *
     * Este método realiza una consulta a la base de datos para obtener las
     * ventas dentro de un rango de fechas especificado y las muestra en una
     * tabla gráfica (`JTable`).
     *
     * @param tablaVentas La tabla (`JTable`) donde se mostrarán las ventas.
     * @param fechaInicio Fecha de inicio del filtro en formato "dd/MM/yyyy".
     * @param fechaFin Fecha de fin del filtro en formato "dd/MM/yyyy".
     */
    public void mostrarFechasDefinidas(JTable tablaVentas, String fechaInicio, String fechaFin) {
        // Validar que las fechas tengan el formato correcto
        if (!esFormatoFechaValido(fechaInicio)) {
            JOptionPane.showMessageDialog(null, "La fecha de inicio ingresada no tiene un formato válido (dd/MM/yyyy).");
            return;
        }
        if (!esFormatoFechaValido(fechaFin)) {
            JOptionPane.showMessageDialog(null, "La fecha de finalización ingresada no tiene un formato válido (dd/MM/yyyy).");
            return;
        }

        // Convertir las fechas a timestamp
        long timestampInicio = 0;
        long timestampFin = 0;
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            formatoFecha.setLenient(false); // Para validar fechas estrictamente
            Date inicio = formatoFecha.parse(fechaInicio);
            Date fin = formatoFecha.parse(fechaFin);

            timestampInicio = inicio.getTime();
            timestampFin = fin.getTime();
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Error al convertir las fechas: " + e.getMessage());
            return;
        }

        // Crear un nuevo modelo de tabla
        final DefaultTableModel modelo = new DefaultTableModel();

        // Definir las columnas del modelo
        modelo.addColumn("Id");
        modelo.addColumn("Producto");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Código");
        modelo.addColumn("Precio");
        modelo.addColumn("Cliente");
        modelo.addColumn("CC Cliente");
        modelo.addColumn("Metodo pago");
        modelo.addColumn("Pago confirmado");
        modelo.addColumn("Vendedor");
        modelo.addColumn("Fecha");
        modelo.addColumn("Precio Total");
        tablaVentas.setModel(modelo);

        // Consulta SQL con filtro por rango de fechas (timestamps)
        final String sql = "SELECT v.id, d.producto, d.cantidad, d.codigo, d.precio, v.cliente, v.cc_cliente, v.vendedor, v.metodo_pago, v.fecha, v.pago_confirmado "
                + "FROM ventas v "
                + "JOIN detalles_venta d ON v.id = d.venta_id "
                + "WHERE v.fecha BETWEEN ? AND ?";

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {
            // Establecer los valores del rango de fechas (timestamps) en la consulta
            pst.setLong(1, timestampInicio);
            pst.setLong(2, timestampFin);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    double precio = rs.getDouble("precio");
                    int cantidad = rs.getInt("cantidad");
                    double precioTotal = precio * cantidad;

                    // Convertir el timestamp de la base de datos a una fecha legible
                    long fechaTimestamp = rs.getLong("fecha");
                    String fechaLegible = new SimpleDateFormat("dd/MM/yyyy").format(new Date(fechaTimestamp));

                    modelo.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("producto"),
                        cantidad,
                        rs.getString("codigo"),
                        precio,
                        rs.getString("cliente"),
                        rs.getString("cc_cliente"),
                        rs.getString("metodo_pago"),
                        rs.getString("pago_confirmado"),
                        rs.getString("vendedor"),
                        fechaLegible,
                        precioTotal
                    });
                }
            }

            tablaVentas.setModel(modelo);

            // Ajustar tamaños de columnas
            tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(50);  // Id
            tablaVentas.getColumnModel().getColumn(1).setPreferredWidth(250); // Producto
            tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(50);  // Cantidad
            tablaVentas.getColumnModel().getColumn(3).setPreferredWidth(100); // Código
            tablaVentas.getColumnModel().getColumn(4).setPreferredWidth(75);  // Precio
            tablaVentas.getColumnModel().getColumn(5).setPreferredWidth(150); // Cliente
            tablaVentas.getColumnModel().getColumn(6).setPreferredWidth(100); // CC Cliente
            tablaVentas.getColumnModel().getColumn(7).setPreferredWidth(100); // Vendedor
            tablaVentas.getColumnModel().getColumn(8).setPreferredWidth(100); // metodo pago
            tablaVentas.getColumnModel().getColumn(9).setPreferredWidth(100); // pago confirmado            
            tablaVentas.getColumnModel().getColumn(10).setPreferredWidth(100); // Fecha
            tablaVentas.getColumnModel().getColumn(11).setPreferredWidth(100); // Precio Total
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al mostrar ventas", e);
            JOptionPane.showMessageDialog(null, "Error al mostrar ventas: " + e.getMessage());
        }
    }

    /**
     * Método para mostrar los registros de ventas de una fecha específica en
     * formato dd/MM/yyyy.
     *
     * @param tablaVentas La tabla (`JTable`) donde se mostrarán los registros
     * de ventas.
     * @param fecha La fecha específica para filtrar los registros (formato:
     * "dd/MM/yyyy").
     */
    public void mostrarVentasPorDia(JTable tablaVentas, String fecha) {
        // Validar que la fecha tenga el formato correcto
        if (!esFormatoFechaValido(fecha)) {
            JOptionPane.showMessageDialog(null, "La fecha ingresada no tiene un formato válido (dd/MM/yyyy).");
            return;
        }

        // Convertir la fecha a un rango de timestamp
        long inicioDia = 0;
        long finDia = 0;
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            formatoFecha.setLenient(false); // Validación estricta
            Date fechaInicio = formatoFecha.parse(fecha);

            // Obtener el inicio y el fin del día
            inicioDia = fechaInicio.getTime(); // 00:00:00
            finDia = inicioDia + (24 * 60 * 60 * 1000) - 1; // 23:59:59
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Error al convertir la fecha: " + e.getMessage());
            return;
        }

        // Crear un modelo de tabla vacío
        final DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Id");
        modelo.addColumn("Producto");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Código");
        modelo.addColumn("Precio");
        modelo.addColumn("Cliente");
        modelo.addColumn("CC Cliente");
        modelo.addColumn("Metodo pago");
        modelo.addColumn("Pago confirmado");
        modelo.addColumn("Vendedor");
        modelo.addColumn("Fecha");
        modelo.addColumn("Precio Total");
        tablaVentas.setModel(modelo);

        // Consulta SQL con filtro por rango de fechas (timestamps)
        final String sql = "SELECT v.id, d.producto, d.cantidad, d.codigo, d.precio, v.cliente, v.cc_cliente, v.vendedor, v.metodo_pago, v.fecha, v.pago_confirmado  "
                + "FROM ventas v "
                + "JOIN detalles_venta d ON v.id = d.venta_id "
                + "WHERE v.fecha BETWEEN ? AND ?";

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {
            // Establecer los valores del rango de fechas (timestamps) en la consulta
            pst.setLong(1, inicioDia);
            pst.setLong(2, finDia);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    double precio = rs.getDouble("precio");
                    int cantidad = rs.getInt("cantidad");
                    double precioTotal = precio * cantidad;

                    // Convertir el timestamp de la base de datos a una fecha legible
                    long fechaTimestamp = rs.getLong("fecha");
                    String fechaLegible = new SimpleDateFormat("dd/MM/yyyy").format(new Date(fechaTimestamp));

                    modelo.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("producto"),
                        cantidad,
                        rs.getString("codigo"),
                        precio,
                        rs.getString("cliente"),
                        rs.getString("cc_cliente"),
                        rs.getString("metodo_pago"),
                        rs.getString("pago_confirmado"),
                        rs.getString("vendedor"),
                        fechaLegible,
                        precioTotal
                    });
                }
            }

            tablaVentas.setModel(modelo);

            // Ajustar tamaños de columnas
            tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(50);  // Id
            tablaVentas.getColumnModel().getColumn(1).setPreferredWidth(250); // Producto
            tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(50);  // Cantidad
            tablaVentas.getColumnModel().getColumn(3).setPreferredWidth(100); // Código
            tablaVentas.getColumnModel().getColumn(4).setPreferredWidth(75);  // Precio
            tablaVentas.getColumnModel().getColumn(5).setPreferredWidth(150); // Cliente
            tablaVentas.getColumnModel().getColumn(6).setPreferredWidth(100); // CC Cliente
            tablaVentas.getColumnModel().getColumn(7).setPreferredWidth(100); // Vendedor
            tablaVentas.getColumnModel().getColumn(8).setPreferredWidth(100); // metodo pago
            tablaVentas.getColumnModel().getColumn(9).setPreferredWidth(100); // pago confirmado
            tablaVentas.getColumnModel().getColumn(10).setPreferredWidth(100); // Fecha
            tablaVentas.getColumnModel().getColumn(11).setPreferredWidth(100); // Precio Total

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al mostrar ventas por día", e);
            JOptionPane.showMessageDialog(null, "Error al mostrar ventas por día: " + e.getMessage());
        }
    }

    // Método para formatear la fecha
    private String formatearFecha(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  // Formato de fecha deseado
        return sdf.format(fecha);  // Retorna la fecha en formato legible
    }

    /**
     * Método para validar si una fecha tiene el formato "dd/MM/yyyy".
     *
     * @param fecha La fecha en formato String que se desea validar.
     * @return `true` si la fecha es válida, `false` en caso contrario.
     */
    public static boolean esFormatoFechaValido(String fecha) {
        if (fecha == null || fecha.isEmpty()) {
            return false; // Fecha vacía o nula no es válida
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // No permitir fechas como "31/02/2025"

        try {
            Date date = sdf.parse(fecha); // Intentar parsear la fecha
            return true;
        } catch (ParseException e) {
            return false; // La fecha no es válida
        }
    }

    public void exportarDatosTablaAExcel(JTable tabla, String rutaExcel) throws IOException {
        // Crear un libro de trabajo de Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Datos Exportados");

        // Obtener el modelo de la tabla
        TableModel model = tabla.getModel();

        // Escribir los encabezados de la tabla en la primera fila del archivo Excel
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < model.getColumnCount(); col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(model.getColumnName(col));
        }

        // Escribir los datos visibles de la tabla en las filas siguientes
        for (int row = 0; row < model.getRowCount(); row++) {
            Row excelRow = sheet.createRow(row + 1);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = excelRow.createCell(col);
                Object value = model.getValueAt(row, col);
                cell.setCellValue(value != null ? value.toString() : "");
            }
        }

        // Guardar el archivo Excel en la ruta especificada
        try (FileOutputStream fileOut = new FileOutputStream(rutaExcel)) {
            workbook.write(fileOut);
        }

        // Cerrar el libro de trabajo
        workbook.close();
    }

    public boolean generarArchivoCotizacionConPlantilla(String rutaPlantilla, String rutaArchivo, String numeroCotizacion, List<Venta> ventasCotizadas) {
        // Verificar si la plantilla es un archivo Excel válido
        File archivoPlantilla = new File(rutaPlantilla);
        if (!archivoPlantilla.exists() || !archivoPlantilla.getName().endsWith(".xlsx")) {
            JOptionPane.showMessageDialog(null, "La plantilla seleccionada no es un archivo Excel válido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;  // Retorna false si el archivo no es válido
        }

        try (InputStream inputStream = new FileInputStream(archivoPlantilla); Workbook workbook = new XSSFWorkbook(inputStream)) {

            // Procesar la hoja de Excel
            Sheet sheet = workbook.getSheetAt(0); // Primera hoja

            // Llenar la fecha (columna A a C)
            String fecha = java.time.LocalDate.now().toString(); // Fecha actual
            sheet.getRow(6).getCell(0).setCellValue("FECHA: " + fecha);

            // Llenar el número de cotización (columnas D y E)            
            sheet.getRow(6).getCell(3).setCellValue("COTIZACION N°: " + numeroCotizacion);

            // Llenar el método de pago (columna F) usando la primera venta
            if (!ventasCotizadas.isEmpty()) {
                sheet.getRow(7).getCell(5).setCellValue(ventasCotizadas.get(0).getMetodoPago());  // Usar el método de pago de la primera venta
            }

            // Llenar los datos del cliente (filas 8 y 9)
            sheet.getRow(7).getCell(0).setCellValue("CLIENTE: " + cliente.getNombre());
            sheet.getRow(8).getCell(0).setCellValue("NIT: " + cliente.getNIT());
            sheet.getRow(8).getCell(2).setCellValue("DIR: " + cliente.getDIR());
            sheet.getRow(8).getCell(5).setCellValue("TELEFONO: " + cliente.getTelefono());

            // Llenar los productos cotizados a partir de la fila 11
            int rowNum = 11;
            for (Venta venta : ventasCotizadas) {
                Row row = sheet.createRow(rowNum++);

                // Llenar la cantidad en la columna A
                row.createCell(0).setCellValue(venta.getCantidad());

                // Llenar el nombre del producto en las columnas B a D
                row.createCell(1).setCellValue(venta.getProducto().getProducto());

                // Llenar el valor unitario en la columna E
                row.createCell(4).setCellValue(venta.getProducto().getPrecio());

                // Calcular el valor total para esta venta
                double totalProducto = venta.getCantidad() * venta.getProducto().getPrecio();
                row.createCell(5).setCellValue(totalProducto);
            }

            // Guardar el archivo generado
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null, "Archivo Excel generado con éxito: " + rutaArchivo,
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                return true;  // Retorna true si la generación del archivo fue exitosa
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al generar el archivo Excel: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;  // Retorna false si ocurre un error durante la operación
        }
    }

    public void actualizarPagoConfirmado(int ventaId, String pagoConfirmado) {
        String sql = "UPDATE ventas SET pago_confirmado = ? WHERE id = ?";
        try (Connection conn = conexion.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pagoConfirmado);
            ps.setInt(2, ventaId);

            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas > 0) {
                JOptionPane.showMessageDialog(null, "El estado de pago fue actualizado correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró una venta con el ID especificado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al actualizar el estado de pago: " + e.getMessage());
        }
    }

}
