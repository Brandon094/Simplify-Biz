package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador para la gestión del Módulo de Cartera y Cuentas por Cobrar (CxC).
 * Aplica los principios MVC y DRY para el manejo atómico de abonos y saldos.
 */
public class CarteraController {

    private static final Logger LOGGER = Logger.getLogger(CarteraController.class.getName());

    private Connection conn() {
        return GestorConexion.getInstancia().obtenerConexion();
    }

    /**
     * Carga en la tabla especificada todas las ventas registradas a crédito o deudoras.
     */
    public void obtenerVentasEnCartera(JTable tabla, String filtroCliente) {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Id");
        modelo.addColumn("Cliente");
        modelo.addColumn("CC Cliente");
        modelo.addColumn("Vendedor");
        modelo.addColumn("Fecha");
        modelo.addColumn("Total Venta");
        modelo.addColumn("Abonado");
        modelo.addColumn("Saldo Pendiente");
        modelo.addColumn("Estado");

        tabla.setModel(modelo);

        String sql = "SELECT v.id, v.cliente, v.cc_cliente, v.vendedor, v.fecha, v.total, v.pago_confirmado, "
                + "COALESCE(SUM(a.monto), 0.0) AS total_abonado "
                + "FROM ventas v "
                + "LEFT JOIN abonos_cartera a ON v.id = a.venta_id "
                + "WHERE v.metodo_pago = 'Crédito' OR v.pago_confirmado = 'deudor' "
                + "GROUP BY v.id, v.cliente, v.cc_cliente, v.vendedor, v.fecha, v.total, v.pago_confirmado "
                + "ORDER BY v.id DESC";

        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            String filtroLower = filtroCliente != null ? filtroCliente.trim().toLowerCase() : "";

            while (rs.next()) {
                String cliente = rs.getString("cliente");
                String ccCliente = rs.getString("cc_cliente");

                if (!filtroLower.isEmpty()) {
                    boolean coincide = (cliente != null && cliente.toLowerCase().contains(filtroLower))
                            || (ccCliente != null && ccCliente.toLowerCase().contains(filtroLower));
                    if (!coincide) {
                        continue;
                    }
                }

                double totalVenta = rs.getDouble("total");
                double totalAbonado = rs.getDouble("total_abonado");
                double saldoPendiente = Math.max(0.0, totalVenta - totalAbonado);
                String estado = rs.getString("pago_confirmado");

                // Si el saldo ya se liquidó pero el estado no se actualizó, forzar visualización limpia
                if (saldoPendiente <= 0.01 && !"pagado".equalsIgnoreCase(estado)) {
                    estado = "pagado";
                }

                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    cliente,
                    ccCliente,
                    rs.getString("vendedor"),
                    UIUtils.formatDate(rs.getString("fecha")),
                    totalVenta,
                    totalAbonado,
                    saldoPendiente,
                    estado
                });
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar ventas en cartera", e);
        }
    }

    /**
     * Registra un nuevo abono para una venta a crédito de forma atómica.
     * Si el saldo restante llega a 0, actualiza el estado de la venta a 'pagado'.
     */
    public ResultadoOperacion registrarAbono(int ventaId, double monto, String metodoPago, String observacion) {
        if (monto <= 0.0) {
            return ResultadoOperacion.error("El monto del abono debe ser mayor a cero.");
        }

        Connection conn = null;
        PreparedStatement psVenta = null;
        PreparedStatement psAbono = null;
        PreparedStatement psUpdateEstado = null;

        try {
            conn = conn();
            conn.setAutoCommit(false);

            // 1. Obtener total de la venta y verificar existencia
            psVenta = conn.prepareStatement("SELECT total, pago_confirmado FROM ventas WHERE id = ?");
            psVenta.setInt(1, ventaId);
            ResultSet rsVenta = psVenta.executeQuery();
            if (!rsVenta.next()) {
                conn.rollback();
                return ResultadoOperacion.error("No se encontró la venta especificada.");
            }

            double totalVenta = rsVenta.getDouble("total");
            rsVenta.close();

            // 2. Obtener acumulado abonado a la fecha
            double totalAbonadoPrevio = 0.0;
            try (PreparedStatement psAbonosPrevios = conn.prepareStatement("SELECT COALESCE(SUM(monto), 0.0) FROM abonos_cartera WHERE venta_id = ?")) {
                psAbonosPrevios.setInt(1, ventaId);
                ResultSet rsAbonos = psAbonosPrevios.executeQuery();
                if (rsAbonos.next()) {
                    totalAbonadoPrevio = rsAbonos.getDouble(1);
                }
            }

            double saldoPendienteActual = Math.max(0.0, totalVenta - totalAbonadoPrevio);
            if (monto > saldoPendienteActual + 0.01) {
                conn.rollback();
                return ResultadoOperacion.error(String.format(
                        "El abono ingresado (%s) supera el saldo pendiente de la deuda (%s).",
                        UIUtils.formatCurrency(monto), UIUtils.formatCurrency(saldoPendienteActual)));
            }

            // 3. Insertar abono en abonos_cartera
            psAbono = conn.prepareStatement(
                    "INSERT INTO abonos_cartera (venta_id, monto, fecha, metodo_pago, observacion) VALUES (?, ?, date('now'), ?, ?)");
            psAbono.setInt(1, ventaId);
            psAbono.setDouble(2, monto);
            psAbono.setString(3, metodoPago != null ? metodoPago : "Efectivo");
            psAbono.setString(4, observacion != null ? observacion.trim() : "");
            psAbono.executeUpdate();

            // 4. Si la deuda queda saldada, actualizar estado de venta a 'pagado'
            double nuevoSaldo = saldoPendienteActual - monto;
            if (nuevoSaldo <= 0.01) {
                psUpdateEstado = conn.prepareStatement("UPDATE ventas SET pago_confirmado = 'pagado' WHERE id = ?");
                psUpdateEstado.setInt(1, ventaId);
                psUpdateEstado.executeUpdate();
            }

            conn.commit();
            return ResultadoOperacion.ok(String.format("Abono de %s registrado exitosamente.", UIUtils.formatCurrency(monto)));

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Error al revertir transacción de abono", rollbackEx);
                }
            }
            LOGGER.log(Level.SEVERE, "Error al registrar abono en cartera", e);
            return ResultadoOperacion.error("Error en la base de datos al procesar el abono.");
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error al restaurar autocommit", ex);
                }
            }
        }
    }

    /**
     * Obtiene el historial de abonos realizados para una venta específica.
     */
    public List<Object[]> obtenerHistorialAbonos(int ventaId) {
        List<Object[]> historial = new ArrayList<>();
        String sql = "SELECT id, fecha, monto, metodo_pago, observacion FROM abonos_cartera WHERE venta_id = ? ORDER BY id DESC";

        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, ventaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                historial.add(new Object[]{
                    rs.getInt("id"),
                    UIUtils.formatDate(rs.getString("fecha")),
                    rs.getDouble("monto"),
                    rs.getString("metodo_pago"),
                    rs.getString("observacion")
                });
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al consultar historial de abonos", e);
        }
        return historial;
    }

    /**
     * Calcula los KPIs financieros del módulo de Cartera:
     * index 0: Total Cartera Pendiente ($)
     * index 1: Total Recaudado en Abonos Este Mes ($)
     * index 2: Cantidad de Clientes Deudores Activos
     */
    public double[] obtenerResumenCartera() {
        double[] resumen = new double[3];

        // 1. Total Cartera Pendiente
        String sqlPendiente = "SELECT SUM(v.total - COALESCE(a.total_abono, 0.0)) "
                + "FROM ventas v "
                + "LEFT JOIN (SELECT venta_id, SUM(monto) AS total_abono FROM abonos_cartera GROUP BY venta_id) a ON v.id = a.venta_id "
                + "WHERE v.metodo_pago = 'Crédito' OR v.pago_confirmado = 'deudor'";

        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sqlPendiente)) {
            if (rs.next()) {
                resumen[0] = Math.max(0.0, rs.getDouble(1));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener total cartera pendiente", e);
        }

        // 2. Recaudado en Abonos Este Mes
        String sqlRecaudado = "SELECT SUM(monto) FROM abonos_cartera WHERE fecha >= date('now', 'start of month')";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sqlRecaudado)) {
            if (rs.next()) {
                resumen[1] = rs.getDouble(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener recaudo mensual de abonos", e);
        }

        // 3. Deudores Activos
        String sqlDeudores = "SELECT COUNT(DISTINCT v.cliente) FROM ventas v WHERE v.pago_confirmado = 'deudor'";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sqlDeudores)) {
            if (rs.next()) {
                resumen[2] = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al contar deudores activos", e);
        }

        return resumen;
    }
}
