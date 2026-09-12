package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.config.Seguridad;
import com.mycompany.zl_solucion_integral.config.Validaciones;
import com.mycompany.zl_solucion_integral.models.Usuario;
import com.mycompany.zl_solucion_integral.views.components.UIMessages;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Operaciones de usuarios. No muestra diálogos Swing: devuelve
 * {@link ResultadoOperacion} para que la vista informe al usuario.
 */
public class UsuarioController {

    private static final String COLUMNAS_LISTADO = "id, nombre, email, telefono, rol";
    private final Logger logger = Logger.getLogger(UsuarioController.class.getName());

    private Connection conn() {
        return GestorConexion.getInstancia().obtenerConexion();
    }

    public ResultadoOperacion agregarUsuario(final Usuario usuario) {
        if (validarExistenciaUsuario(usuario.getNombre())) {
            return ResultadoOperacion.error(UIMessages.MSG_USUARIO_DUPLICADO_NOMBRE);
        }
        if (validarExistenciaPorCorreo(usuario.getEmail())) {
            return ResultadoOperacion.error(UIMessages.MSG_USUARIO_DUPLICADO_CORREO);
        }

        final String sql = "INSERT INTO usuarios (nombre, telefono, email, contraseña, rol) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            String contraseñaEncriptada = Seguridad.encriptarContraseña(usuario.getContraseña());
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getTelefono());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, contraseñaEncriptada);
            pstmt.setString(5, usuario.getRol());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return ResultadoOperacion.ok(UIMessages.MSG_USUARIO_REGISTRADO);
            }
            return ResultadoOperacion.error(UIMessages.MSG_USUARIO_NO_REGISTRADO);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al registrar el usuario", e);
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique")) {
                return ResultadoOperacion.error(UIMessages.MSG_USUARIO_DUPLICADO_CONTACTO);
            }
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al registrar el usuario", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }
    }

    /**
     * Actualiza un usuario. Si {@code contraseña} está vacía o nula, conserva
     * la clave existente.
     */
    public ResultadoOperacion modificarUsuario(final String nombre, final String telefono, final String email,
            final String rol, final String contraseña, final int idUsuario) {
        boolean actualizarClave = contraseña != null && !contraseña.trim().isEmpty();
        final String sql = actualizarClave
                ? "UPDATE usuarios SET nombre = ?, telefono = ?, email = ?, rol = ?, contraseña = ? WHERE id = ?"
                : "UPDATE usuarios SET nombre = ?, telefono = ?, email = ?, rol = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, telefono);
            pstmt.setString(3, email);
            pstmt.setString(4, rol);
            if (actualizarClave) {
                pstmt.setString(5, Seguridad.encriptarContraseña(contraseña));
                pstmt.setInt(6, idUsuario);
            } else {
                pstmt.setInt(5, idUsuario);
            }
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return ResultadoOperacion.ok(UIMessages.MSG_USUARIO_MODIFICADO);
            }
            return ResultadoOperacion.error(UIMessages.MSG_USUARIO_NO_MODIFICADO);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al modificar el usuario", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }
    }

    public ResultadoOperacion eliminarUsuario(int idUsuario) {
        if (idUsuario == -1) {
            return ResultadoOperacion.error(UIMessages.MSG_SELECCIONE_USUARIO);
        }
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return ResultadoOperacion.ok(UIMessages.MSG_USUARIO_ELIMINADO);
            }
            return ResultadoOperacion.error(UIMessages.MSG_USUARIO_NO_ELIMINADO);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al eliminar el usuario", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }
    }

    public int obtenerIdUsuarioSeleccionado(final JTable tabla) {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            return -1;
        }
        Integer id = Validaciones.parseEntero(tabla.getValueAt(filaSeleccionada, 0).toString());
        return id == null ? -1 : id;
    }

    public void mostrarUsuarios(final JTable tablaUsuarios) {
        final DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Id");
        modelo.addColumn("Usuario");
        modelo.addColumn("Email");
        modelo.addColumn("# Tel");
        modelo.addColumn("Rol");
        tablaUsuarios.setModel(modelo);
        ajustarColumnasUsuarios(tablaUsuarios);

        final String sql = "SELECT " + COLUMNAS_LISTADO + " FROM usuarios";
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getString("rol")
                });
            }
            tablaUsuarios.setModel(modelo);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al mostrar usuarios", e);
        }
    }

    public void mostrarUsuariosPorRol(JTable tablaUsuarios, String rolSeleccionado) {
        DefaultTableModel modelo = (DefaultTableModel) tablaUsuarios.getModel();
        asegurarColumnasSinPassword(modelo);
        modelo.setRowCount(0);

        String query = "SELECT " + COLUMNAS_LISTADO + " FROM usuarios";
        boolean filtrarPorRol = !"Todas".equals(rolSeleccionado);
        if (filtrarPorRol) {
            query += " WHERE rol = ?";
        }

        try (PreparedStatement stmt = conn().prepareStatement(query)) {
            if (filtrarPorRol) {
                String rolLimpio = rolSeleccionado.contains(":")
                        ? rolSeleccionado.split(":")[0].trim()
                        : rolSeleccionado.trim();
                Integer rolNumerico = Validaciones.parseEntero(rolLimpio);
                if (rolNumerico == null) {
                    return;
                }
                stmt.setInt(1, rolNumerico);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getInt("rol")
                });
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al filtrar usuarios por rol", e);
        }
    }

    private void asegurarColumnasSinPassword(DefaultTableModel modelo) {
        if (modelo.getColumnCount() != 5) {
            modelo.setColumnCount(5);
            modelo.setColumnIdentifiers(new Object[]{"Id", "Usuario", "Email", "Teléfono", "Rol"});
        }
    }

    private void ajustarColumnasUsuarios(JTable tablaUsuarios) {
        if (tablaUsuarios.getColumnCount() < 5) return;
        tablaUsuarios.getColumnModel().getColumn(0).setMinWidth(45);
        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaUsuarios.getColumnModel().getColumn(0).setMaxWidth(70);
        tablaUsuarios.getColumnModel().getColumn(2).setMinWidth(200);
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(220);
        tablaUsuarios.getColumnModel().getColumn(2).setMaxWidth(250);
        tablaUsuarios.getColumnModel().getColumn(4).setMinWidth(45);
        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(50);
        tablaUsuarios.getColumnModel().getColumn(4).setMaxWidth(50);
    }

    public boolean validarCredencialesUsuarioRegular(final String usuario, final String contraseña) {
        final String sql = "SELECT contraseña FROM usuarios WHERE LOWER(nombre) = LOWER(?) AND rol != 1";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setString(1, usuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Seguridad.validarContraseña(contraseña, rs.getString("contraseña"));
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al validar credenciales del usuario regular", e);
        }
        return false;
    }

    public boolean validarCredencialesAdmin(final String usuario, final String contraseña) {
        final String sql = "SELECT contraseña FROM usuarios WHERE LOWER(nombre) = LOWER(?) AND rol = 1";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setString(1, usuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Seguridad.validarContraseña(contraseña, rs.getString("contraseña"));
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al validar credenciales del administrador", e);
        }
        return false;
    }

    public boolean existeAdministrador() {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE rol = 1";
        try (PreparedStatement pst = conn().prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al validar existencia de administrador", e);
            return false;
        }
    }

    public boolean validarExistenciaUsuario(final String nombreUsuario) {
        final String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE nombre = ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setString(1, nombreUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt("total") > 0;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al validar existencia del usuario", e);
            return false;
        }
    }

    public boolean validarExistenciaPorCorreo(final String email) {
        final String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE email = ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt("total") > 0;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al validar existencia del correo", e);
            return false;
        }
    }

    /**
     * Valida que coincidan el usuario o correo con el teléfono registrado para recuperación.
     */
    public boolean validarDatosRecuperacion(final String usuarioOEmail, final String telefono) {
        if (usuarioOEmail == null || usuarioOEmail.trim().isEmpty() || telefono == null || telefono.trim().isEmpty()) {
            return false;
        }
        final String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE (LOWER(nombre) = LOWER(?) OR LOWER(email) = LOWER(?)) AND telefono = ?";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            String idLimpio = usuarioOEmail.trim();
            pstmt.setString(1, idLimpio);
            pstmt.setString(2, idLimpio);
            pstmt.setString(3, telefono.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt("total") > 0;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al validar datos de recuperación", e);
            return false;
        }
    }

    /**
     * Restablece la contraseña de un usuario según su usuario o email.
     */
    public ResultadoOperacion restablecerContraseña(final String usuarioOEmail, final String nuevaContraseña) {
        if (nuevaContraseña == null || nuevaContraseña.trim().length() < 4) {
            return ResultadoOperacion.error("La contraseña debe tener al menos 4 caracteres.");
        }
        final String sql = "UPDATE usuarios SET contraseña = ? WHERE LOWER(nombre) = LOWER(?) OR LOWER(email) = LOWER(?)";
        try (PreparedStatement pstmt = conn().prepareStatement(sql)) {
            String idLimpio = usuarioOEmail.trim();
            pstmt.setString(1, Seguridad.encriptarContraseña(nuevaContraseña.trim()));
            pstmt.setString(2, idLimpio);
            pstmt.setString(3, idLimpio);
            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                return ResultadoOperacion.ok("Contraseña restablecida exitosamente.");
            }
            return ResultadoOperacion.error("No se pudo restablecer la contraseña. Usuario no encontrado.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al restablecer la contraseña", e);
            return ResultadoOperacion.error(UIMessages.MSG_ERROR_BD);
        }
    }
}
