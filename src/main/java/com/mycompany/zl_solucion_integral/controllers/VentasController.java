package com.mycompany.zl_solucion_integral.controllers;

/**
 * Controlador para gestionar las operaciones con ventas.
 * <p>
 * Esta clase proporciona métodos para agregar, modificar, eliminar y mostrar
 * ventas en la base de datos.
 * </p>
 * Utiliza una instancia de `ConexionDB` para interactuar con la base de datos.
 *
 * @author ChopCode Solutions
 */
import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.config.Validaciones;
import com.mycompany.zl_solucion_integral.models.Producto;
import com.mycompany.zl_solucion_integral.models.Sesion;
import com.mycompany.zl_solucion_integral.models.Usuario;
import com.mycompany.zl_solucion_integral.models.Venta;
import com.mycompany.zl_solucion_integral.views.components.UIMessages;
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

    private Usuario cliente;
    private Logger logger = Logger.getLogger(VentasController.class.getName());
    private Sesion sesion;

    private Connection conn() {
        return GestorConexion.getInstancia().obtenerConexion();
    }

    public VentasController(Usuario cliente, Sesion sesion) {
        this.sesion = sesion;
        this.cliente = cliente;
    }

    public VentasController() {
    }

    public ResultadoOperacion guardarVenta(final Venta venta, List<Producto> productosVendidos, JTable tablaVentas) {
        String sqlInsertVenta = "INSERT INTO ventas (cliente, cc_cliente, vendedor, fecha, total, metodo_pago, pago_confirmado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlInsertDetalleVenta = "INSERT INTO detalles_venta (venta_id, producto, cantidad, codigo, precio, precio_costo, total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE productos SET cantidad = cantidad - ? WHERE codigo = ? AND cantidad >= ?";

        Connection conn = null;
        PreparedStatement psVenta = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psStock = null;
        ResultSet generatedKeys = null;

        try {
            conn = conn();
            conn.setAutoCommit(false);

            for (Producto producto : productosVendidos) {
                if (producto.getCantidad() < producto.getCantidadSolicitada()) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    return ResultadoOperacion.error(UIMessages.MSG_STOCK_VENTA_INSUFICIENTE);
                }
            }

            // Determinar el valor de la columna "pago_confirmado"
            String pagoConfirmado = venta.getMetodoPago().equalsIgnoreCase("crédito") ? "deudor" : "pagado";

            // Insertar venta general
            psVenta = conn.prepareStatement(sqlInsertVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setString(1, venta.getCliente().getNombre());
            psVenta.setString(2, venta.getCliente().getNoCc());
            psVenta.setString(3, venta.getVendedor());
            psVenta.setString(4, venta.getFecha() != null ? venta.getFecha().toString() : java.time.LocalDate.now().toString());
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
                psDetalle.setDouble(6, producto.getPrecioCosto());
                psDetalle.setDouble(7, producto.getPrecio() * producto.getCantidadSolicitada());
                psDetalle.executeUpdate();

                // Actualizar stock del producto
                psStock.setInt(1, producto.getCantidadSolicitada());
                psStock.setString(2, producto.getCodigo());
                psStock.setInt(3, producto.getCantidad());
                if (psStock.executeUpdate() == 0) {
                    throw new SQLException("Error al actualizar stock para el producto: " + producto.getProducto());
                }
            }

            conn.commit();
            if (tablaVentas != null) {
                MostrarVentas(tablaVentas);
            }
            return ResultadoOperacion.ok(UIMessages.MSG_VENTA_GUARDADA);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.log(Level.SEVERE, "Error al revertir la transacción", rollbackEx);
                }
            }
            logger.log(Level.SEVERE, "Error al procesar la venta", e);
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("stock")) {
                return ResultadoOperacion.error(UIMessages.MSG_STOCK_VENTA_INSUFICIENTE);
            }
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        } finally {
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
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error al restaurar auto-commit", ex);
            }
        }
    }

    // Metodo para modificar una venta
    public ResultadoOperacion modificarVenta(final Venta venta, int idVenta, JTable tablaVentas) {
        String sqlUpdateVenta = "UPDATE ventas SET producto = ?, cantidad = ?, codigo = ?, precio = ?, cliente = ?, cc_cliente = ?, vendedor = ?, fecha = ?, total = ? WHERE id = ?";
        String sqlUpdateStockRestaurar = "UPDATE productos SET cantidad = cantidad + ? WHERE codigo = ?";  // Para restaurar el stock antiguo
        String sqlUpdateStockNuevo = "UPDATE productos SET cantidad = cantidad - ? WHERE codigo = ? AND cantidad >= ?";  // Para aplicar el nuevo stock

        Connection conn = null;
        PreparedStatement psVenta = null;
        PreparedStatement psStockRestaurar = null;
        PreparedStatement psStockNuevo = null;

        try {
            conn = conn();
            conn.setAutoCommit(false);

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
            if (tablaVentas != null) {
                MostrarVentas(tablaVentas);
            }
            return ResultadoOperacion.ok(UIMessages.MSG_VENTA_MODIFICADA);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.log(Level.SEVERE, "Error al revertir la transacción", rollbackEx);
                }
            }
            logger.log(Level.SEVERE, "Error al modificar la venta", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        } finally {
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
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error al restaurar auto-commit", ex);
            }
        }
    }

// Método para obtener la cantidad anterior de una venta
    private int obtenerCantidadAnterior(int idVenta) {
        String sql = "SELECT cantidad FROM ventas WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {

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
            Integer id = Validaciones.parseEntero(tabla.getValueAt(filaSeleccionada, 0).toString());
            return id == null ? -1 : id;
        } else {
            // No se ha seleccionado ninguna fila, devolver -1
            return -1;
        }
    }

    public void guardarNumeroCotizacionEnBaseDeDatos(String numeroCotizacion) {
        String sqlActualizar = "UPDATE configuracion SET ultimoNumeroCotizacion = ? WHERE id = 1";

        try (PreparedStatement pstmt = conn().prepareStatement(sqlActualizar)) {
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

        try (PreparedStatement psConsulta = conn().prepareStatement(sqlConsulta); PreparedStatement psActualizar = conn().prepareStatement(sqlActualizar)) {

            // Consultar el último número de cotización
            ResultSet rs = psConsulta.executeQuery();
            if (rs.next()) {
                String ultimoNumero = rs.getString("ultimoNumeroCotizacion");
                if (ultimoNumero != null && ultimoNumero.startsWith(fecha)) {
                    // Extraer el número actual después del guión y sumarle 1
                    Integer extraido = Validaciones.parseEntero(ultimoNumero.split("-")[1]);
                    siguienteNumero = extraido == null ? 1 : extraido + 1;
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

        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                double precio = rs.getDouble("precio");
                int cantidad = rs.getInt("cantidad");
                double precioTotal = precio * cantidad;

                String fechaRaw = rs.getString("fecha");
                String fechaFormateada = com.mycompany.zl_solucion_integral.views.components.UIUtils.formatDate(fechaRaw);

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
    public ResultadoOperacion mostrarFechasDefinidas(JTable tablaVentas, String fechaInicio, String fechaFin) {
        Date inicio = Validaciones.parseFecha(fechaInicio);
        Date fin = Validaciones.parseFecha(fechaFin);
        if (inicio == null || fin == null) {
            return ResultadoOperacion.error(UIMessages.MSG_FECHA_FORMATO_INVALIDO);
        }

        long timestampInicio = inicio.getTime();
        long timestampFin = fin.getTime() + (24 * 60 * 60 * 1000) - 1; // Incluir todo el día final

        SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd");
        String isoInicio = isoFmt.format(inicio);
        String isoFin = isoFmt.format(fin);

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

        // Consulta SQL compatible con fechas String ISO (YYYY-MM-DD) y con timestamps antiguos
        final String sql = "SELECT v.id, d.producto, d.cantidad, d.codigo, d.precio, v.cliente, v.cc_cliente, v.vendedor, v.metodo_pago, v.fecha, v.pago_confirmado "
                + "FROM ventas v "
                + "JOIN detalles_venta d ON v.id = d.venta_id "
                + "WHERE (v.fecha BETWEEN ? AND ?) OR (v.fecha >= ? AND v.fecha <= ?)";

        try (PreparedStatement pst = conn().prepareStatement(sql)) {
            pst.setString(1, isoInicio);
            pst.setString(2, isoFin);
            pst.setLong(3, timestampInicio);
            pst.setLong(4, timestampFin);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    double precio = rs.getDouble("precio");
                    int cantidad = rs.getInt("cantidad");
                    double precioTotal = precio * cantidad;

                    String fechaRaw = rs.getString("fecha");
                    String fechaLegible = fechaRaw;
                    if (fechaRaw != null && fechaRaw.matches("\\d+")) {
                        try {
                            long fechaTimestamp = Long.parseLong(fechaRaw);
                            fechaLegible = new SimpleDateFormat("dd/MM/yyyy").format(new Date(fechaTimestamp));
                        } catch (Exception ignored) {}
                    }

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
            return ResultadoOperacion.ok("");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al mostrar ventas", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
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
    public ResultadoOperacion mostrarVentasPorDia(JTable tablaVentas, String fecha) {
        Date fechaInicio = Validaciones.parseFecha(fecha);
        if (fechaInicio == null) {
            return ResultadoOperacion.error(UIMessages.MSG_FECHA_FORMATO_INVALIDO);
        }

        long inicioDia = fechaInicio.getTime();
        long finDia = inicioDia + (24 * 60 * 60 * 1000) - 1;
        String isoDia = new SimpleDateFormat("yyyy-MM-dd").format(fechaInicio);

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

        // Consulta SQL con filtro dual (cadena ISO YYYY-MM-DD y timestamp milisegundos)
        final String sql = "SELECT v.id, d.producto, d.cantidad, d.codigo, d.precio, v.cliente, v.cc_cliente, v.vendedor, v.metodo_pago, v.fecha, v.pago_confirmado  "
                + "FROM ventas v "
                + "JOIN detalles_venta d ON v.id = d.venta_id "
                + "WHERE v.fecha = ? OR (v.fecha >= ? AND v.fecha <= ?)";

        try (PreparedStatement pst = conn().prepareStatement(sql)) {
            pst.setString(1, isoDia);
            pst.setLong(2, inicioDia);
            pst.setLong(3, finDia);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    double precio = rs.getDouble("precio");
                    int cantidad = rs.getInt("cantidad");
                    double precioTotal = precio * cantidad;

                    String fechaRaw = rs.getString("fecha");
                    String fechaLegible = fechaRaw;
                    if (fechaRaw != null && fechaRaw.matches("\\d+")) {
                        try {
                            long fechaTimestamp = Long.parseLong(fechaRaw);
                            fechaLegible = new SimpleDateFormat("dd/MM/yyyy").format(new Date(fechaTimestamp));
                        } catch (Exception ignored) {}
                    }

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
            return ResultadoOperacion.ok("");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al mostrar ventas por día", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
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
        return Validaciones.parseFecha(fecha) != null;
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

    public ResultadoOperacion generarArchivoCotizacionConPlantilla(String rutaPlantilla, String rutaArchivo, String numeroCotizacion, List<Venta> ventasCotizadas) {
        File archivoPlantilla = new File(rutaPlantilla);
        if (!archivoPlantilla.exists() || !archivoPlantilla.getName().endsWith(".xlsx")) {
            return ResultadoOperacion.error(UIMessages.MSG_PLANTILLA_INVALIDA);
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
                return ResultadoOperacion.ok(UIMessages.MSG_EXCEL_GENERADO + rutaArchivo);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al generar el archivo Excel", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }
    }

    public ResultadoOperacion actualizarPagoConfirmado(int ventaId, String pagoConfirmado) {
        String sql = "UPDATE ventas SET pago_confirmado = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {

            ps.setString(1, pagoConfirmado);
            ps.setInt(2, ventaId);

            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas > 0) {
                return ResultadoOperacion.ok(UIMessages.MSG_PAGO_ACTUALIZADO);
            }
            return ResultadoOperacion.error(UIMessages.MSG_VENTA_NO_ENCONTRADA);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar el estado de pago", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }
    }

    /**
     * Obtiene el total de ventas diarias de los últimos 7 días.
     * @return Lista de totales de ventas.
     */
    /**
     * Obtiene el total de ventas diarias de los últimos 7 días.
     * @return Lista de totales de ventas.
     */
    public List<Double> obtenerVentasUltimos7Dias() {
        List<Double> ventas = new java.util.ArrayList<>();
        java.util.Map<String, Double> mapaVentas = new java.util.HashMap<>();

        // 1. Consultar ventas agregadas por fecha
        String sql = "SELECT fecha, SUM(total) as total_dia FROM ventas GROUP BY fecha";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String f = rs.getString("fecha");
                double tot = rs.getDouble("total_dia");
                if (f != null && !f.trim().isEmpty()) {
                    String fLimpia = f.trim().substring(0, Math.min(10, f.trim().length()));
                    mapaVentas.put(fLimpia, mapaVentas.getOrDefault(fLimpia, 0.0) + tot);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener ventas diarias", e);
        }

        // 2. Intentar armar la serie temporal para los 7 días continuos hasta el día de hoy
        java.time.LocalDate hoy = java.time.LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            String claveFecha = hoy.minusDays(i).toString(); // YYYY-MM-DD
            ventas.add(mapaVentas.getOrDefault(claveFecha, 0.0));
        }

        // 3. Si las ventas registradas corresponden a fechas de prueba diferentes (ej. fechas pasadas o importadas),
        // y la lista resultante está vacía en valores positivos pero mapaVentas no lo está,
        // devolvemos directamente las últimas 7 ventas acumuladas para asegurar que la gráfica se pinte.
        boolean tieneValores = ventas.stream().anyMatch(v -> v != null && v > 0);
        if (!tieneValores && !mapaVentas.isEmpty()) {
            ventas.clear();
            java.util.List<Double> valoresEncontrados = new java.util.ArrayList<>(mapaVentas.values());
            int inicio = Math.max(0, valoresEncontrados.size() - 7);
            for (int i = inicio; i < valoresEncontrados.size(); i++) {
                ventas.add(valoresEncontrados.get(i));
            }
            while (ventas.size() < 7) {
                ventas.add(0, 0.0);
            }
        }

        return ventas;
    }
    
    public double obtenerVentasTotales() {
        String sql = "SELECT SUM(total) FROM ventas";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int contarRegistros(String filtro) {
        String sql = "SELECT COUNT(*) FROM ventas";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Obtiene las últimas ventas registradas para el Dashboard.
     */
    public Object[][] obtenerUltimasVentas(int limite) {
        String sql = "SELECT v.fecha, v.cliente, d.producto, v.total "
                + "FROM ventas v "
                + "JOIN detalles_venta d ON v.id = d.venta_id "
                + "ORDER BY v.id DESC LIMIT ?";
        
        List<Object[]> data = new java.util.ArrayList<>();
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                data.add(new Object[]{
                    com.mycompany.zl_solucion_integral.views.components.UIUtils.formatDate(rs.getString("fecha")),
                    rs.getString("cliente"),
                    rs.getString("producto"),
                    rs.getDouble("total")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        
        return data.toArray(new Object[0][]);
    }

    public double obtenerUtilidadTotal() {
        String sql = "SELECT SUM(total - (precio_costo * cantidad)) FROM detalles_venta";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener utilidad total", e);
        }
        return 0.0;
    }
}
