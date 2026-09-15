package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.models.Proveedor;
import com.mycompany.zl_solucion_integral.views.components.UIMessages;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProveedorController {
    private static final Logger logger = Logger.getLogger(ProveedorController.class.getName());

    private Connection conn() {
        return GestorConexion.getInstancia().obtenerConexion();
    }

    public ResultadoOperacion guardarOActualizarProveedor(Proveedor prov) {
        if (prov.getNombre() == null || prov.getNombre().trim().isEmpty() ||
            prov.getNit() == null || prov.getNit().trim().isEmpty()) {
            return ResultadoOperacion.error("Nombre y NIT/Cédula del proveedor son obligatorios.");
        }

        String sqlCheck = "SELECT id FROM proveedores WHERE nit = ?";
        String sqlInsert = "INSERT INTO proveedores (nombre, nit, telefono, email, direccion) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE proveedores SET nombre = ?, telefono = ?, email = ?, direccion = ? WHERE id = ?";

        try (PreparedStatement psCheck = conn().prepareStatement(sqlCheck)) {
            psCheck.setString(1, prov.getNit().trim());
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                int idExistente = rs.getInt("id");
                try (PreparedStatement psUp = conn().prepareStatement(sqlUpdate)) {
                    psUp.setString(1, prov.getNombre().trim());
                    psUp.setString(2, prov.getTelefono());
                    psUp.setString(3, prov.getEmail());
                    psUp.setString(4, prov.getDireccion());
                    psUp.setInt(5, idExistente);
                    psUp.executeUpdate();
                    return ResultadoOperacion.ok("Proveedor actualizado correctamente.");
                }
            } else {
                try (PreparedStatement psIn = conn().prepareStatement(sqlInsert)) {
                    psIn.setString(1, prov.getNombre().trim());
                    psIn.setString(2, prov.getNit().trim());
                    psIn.setString(3, prov.getTelefono());
                    psIn.setString(4, prov.getEmail());
                    psIn.setString(5, prov.getDireccion());
                    psIn.executeUpdate();
                    return ResultadoOperacion.ok("Proveedor registrado correctamente.");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al guardar proveedor", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }
    }

    public void mostrarProveedores(JTable tabla) {
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();
        model.setRowCount(0);

        String sql = "SELECT id, nombre, nit, telefono, email, direccion FROM proveedores ORDER BY nombre ASC";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("nit"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion")
                });
            }
            UIUtils.applyTableStyling(tabla);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar proveedores", e);
        }
    }

    public List<Proveedor> buscarProveedoresSugeridos(String query) {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, nit, telefono, email, direccion FROM proveedores "
                   + "WHERE LOWER(nombre) LIKE ? OR LOWER(nit) LIKE ? LIMIT 10";

        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            String term = "%" + (query != null ? query.trim().toLowerCase() : "") + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Proveedor(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("nit"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar sugerencias de proveedores", e);
        }
        return lista;
    }
}
