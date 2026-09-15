package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.models.Compra;
import com.mycompany.zl_solucion_integral.models.DetalleCompra;
import com.mycompany.zl_solucion_integral.views.components.UIMessages;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComprasController {
    private static final Logger logger = Logger.getLogger(ComprasController.class.getName());

    private Connection conn() {
        return GestorConexion.getInstancia().obtenerConexion();
    }

    /**
     * Procesa e ingresa una orden de compra atómicamente (ACID).
     * Incrementa el stock del catálogo y actualiza el costo de adquisición (precio_costo).
     */
    public ResultadoOperacion guardarEntradaCompra(Compra compra, List<DetalleCompra> detalles) {
        if (compra == null || detalles == null || detalles.isEmpty()) {
            return ResultadoOperacion.error("La entrada debe tener al menos un producto.");
        }

        String sqlInsertCompra = "INSERT INTO compras (proveedor_id, proveedor_nombre, proveedor_nit, num_factura, usuario_registro, fecha, total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlInsertDetalle = "INSERT INTO detalles_compra (compra_id, producto, codigo, cantidad, precio_costo, subtotal) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUpdateStockCosto = "UPDATE productos SET cantidad = cantidad + ?, precio_costo = ? WHERE codigo = ?";

        Connection conn = null;
        PreparedStatement psCompra = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psStock = null;
        ResultSet generatedKeys = null;

        try {
            conn = conn();
            conn.setAutoCommit(false);

            // 1. Insertar encabezado de compra
            psCompra = conn.prepareStatement(sqlInsertCompra, Statement.RETURN_GENERATED_KEYS);
            if (compra.getProveedorId() != null && compra.getProveedorId() > 0) {
                psCompra.setInt(1, compra.getProveedorId());
            } else {
                psCompra.setNull(1, Types.INTEGER);
            }
            psCompra.setString(2, compra.getProveedorNombre());
            psCompra.setString(3, compra.getProveedorNit());
            psCompra.setString(4, compra.getNumFactura());
            psCompra.setString(5, compra.getUsuarioRegistro());
            psCompra.setString(6, compra.getFecha() != null ? compra.getFecha() : java.time.LocalDate.now().toString());
            psCompra.setDouble(7, compra.getTotal());

            psCompra.executeUpdate();
            generatedKeys = psCompra.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("No se pudo obtener el ID de la compra.");
            }
            int compraId = generatedKeys.getInt(1);

            // 2. Insertar detalles y actualizar stock + costo (o crear producto si es nuevo)
            psDetalle = conn.prepareStatement(sqlInsertDetalle);
            psStock = conn.prepareStatement(sqlUpdateStockCosto);
            String sqlInsertNuevoProd = "INSERT INTO productos (producto, codigo, precio, precio_costo, cantidad, categoria) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psInsertNuevo = conn.prepareStatement(sqlInsertNuevoProd);

            for (DetalleCompra det : detalles) {
                // Detalle
                psDetalle.setInt(1, compraId);
                psDetalle.setString(2, det.getProducto());
                psDetalle.setString(3, det.getCodigo());
                psDetalle.setInt(4, det.getCantidad());
                psDetalle.setDouble(5, det.getPrecioCosto());
                psDetalle.setDouble(6, det.getSubtotal());
                psDetalle.executeUpdate();

                // Incremento de existencias y actualización del costo
                psStock.setInt(1, det.getCantidad());
                psStock.setDouble(2, det.getPrecioCosto());
                psStock.setString(3, det.getCodigo());
                if (psStock.executeUpdate() == 0) {
                    // Si no existía el producto en la BD, crearlo automáticamente de forma transparente
                    double precioVentaSugerido = det.getPrecioCosto() * 1.30; // Margen base 30%
                    psInsertNuevo.setString(1, det.getProducto());
                    psInsertNuevo.setString(2, det.getCodigo());
                    psInsertNuevo.setDouble(3, precioVentaSugerido);
                    psInsertNuevo.setDouble(4, det.getPrecioCosto());
                    psInsertNuevo.setInt(5, det.getCantidad());
                    psInsertNuevo.setString(6, "General");
                    psInsertNuevo.executeUpdate();
                }
            }
            if (psInsertNuevo != null) psInsertNuevo.close();

            conn.commit();
            return ResultadoOperacion.ok("Entrada de inventario registrada con éxito.");

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { logger.log(Level.SEVERE, "Error al revertir compra", ex); }
            }
            logger.log(Level.SEVERE, "Error al procesar la entrada de compra", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (psCompra != null) psCompra.close();
                if (psDetalle != null) psDetalle.close();
                if (psStock != null) psStock.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error al restaurar auto-commit", ex);
            }
        }
    }

    /**
     * Muestra la bitácora de compras en la tabla especificada.
     */
    public void mostrarHistorialCompras(JTable tabla, String filtro) {
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();
        model.setRowCount(0);

        String sql = "SELECT id, proveedor_nombre, num_factura, usuario_registro, fecha, total "
                   + "FROM compras ";

        if (filtro != null && !filtro.trim().isEmpty()) {
            sql += "WHERE LOWER(proveedor_nombre) LIKE ? OR LOWER(num_factura) LIKE ? ";
        }
        sql += "ORDER BY fecha DESC, id DESC";

        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            if (filtro != null && !filtro.trim().isEmpty()) {
                String term = "%" + filtro.trim().toLowerCase() + "%";
                ps.setString(1, term);
                ps.setString(2, term);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    UIUtils.formatDate(rs.getString("fecha")),
                    rs.getString("proveedor_nombre"),
                    rs.getString("num_factura"),
                    rs.getString("usuario_registro"),
                    rs.getDouble("total")
                });
            }
            UIUtils.applyTableStyling(tabla);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar compras", e);
        }
    }

    /**
     * Muestra los productos recibidos en una compra específica.
     */
    public void mostrarDetallesCompra(JTable tabla, int compraId) {
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();
        model.setRowCount(0);

        String sql = "SELECT producto, codigo, cantidad, precio_costo, subtotal "
                   + "FROM detalles_compra WHERE compra_id = ?";

        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, compraId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("producto"),
                    rs.getString("codigo"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio_costo"),
                    rs.getDouble("subtotal")
                });
            }
            UIUtils.applyTableStyling(tabla);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar detalles de la compra " + compraId, e);
        }
    }

    /**
     * Obtiene métricas resumen KPI para el módulo de compras:
     * [0] Total Invertido en Compras ($)
     * [1] Entradas Recibidas (#)
     * [2] Proveedores Atendidos (#)
     */
    public double[] obtenerResumenCompras() {
        double[] kpis = new double[]{0.0, 0.0, 0.0};
        String sqlTotal = "SELECT SUM(total), COUNT(id), COUNT(DISTINCT proveedor_nit) FROM compras";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sqlTotal)) {
            if (rs.next()) {
                kpis[0] = rs.getDouble(1);
                kpis[1] = rs.getInt(2);
                kpis[2] = rs.getInt(3);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener resumen KPI de compras", e);
        }
        return kpis;
    }
}
