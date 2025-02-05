package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.config.Seguridad;
import com.mycompany.zl_solucion_integral.config.SelecionRuta;
import com.mycompany.zl_solucion_integral.models.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase encargada de manejar las operaciones relacionadas con los usuarios en
 * la base de datos.
 *
 * La clase `UsuarioController` proporciona métodos para agregar, modificar,
 * eliminar y mostrar usuarios, así como para validar credenciales y la
 * existencia de usuarios. Utiliza una instancia de `ConexionDB` para
 * interactuar con la base de datos.
 *
 * @author Dazac
 */
public class UsuarioController {
    SelecionRuta rutaDB = new SelecionRuta();

    private final ConexionDB conexion = new ConexionDB(rutaDB.cargarRutaBaseDatos());
    private final Logger logger = Logger.getLogger(UsuarioController.class.getName());

    /**
     * Agrega un nuevo usuario a la base de datos.
     *
     * Verifica si el usuario ya existe antes de intentar agregarlo. Muestra un
     * mensaje de error si el usuario ya existe o si ocurre un problema durante
     * el registro.
     *
     * @param usuario Un objeto `Usuario` que contiene la información del nuevo
     * usuario.
     */
    public void agregarUsuario(final Usuario usuario) {
        // Validar si ya existe un usuario con ese nombre
        if (validarExistenciaUsuario(usuario.getNombre())) {
            JOptionPane.showMessageDialog(null, "El usuario ya existe con este nombre.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Sale del método para evitar duplicados
        }

        // Validar si ya existe un usuario con ese correo
        if (validarExistenciaPorCorreo(usuario.getEmail())) {
            JOptionPane.showMessageDialog(null, "El usuario ya existe con este correo.",  "Error", JOptionPane.ERROR_MESSAGE);
            return; // Sale del método para evitar duplicados
        }

        final String sql = "INSERT INTO usuarios (nombre, telefono, email, contraseña, rol) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Encriptar la contraseña antes de guardarla
            String contraseñaEncriptada = Seguridad.encriptarContraseña(usuario.getContraseña());

            // Establecer parámetros
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getTelefono());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, contraseñaEncriptada);
            pstmt.setString(5, usuario.getRol()); // Asumiendo que el rol es un número

            // Ejecutar la consulta
            int rowsAffected = pstmt.executeUpdate();

            // Mensaje al usuario
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo registrar el usuario");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "El usuario ya existe con este correo o teléfono.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la base de datos: " + e.getMessage());
            logger.log(Level.SEVERE, "Error en la base de datos", e);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
            logger.log(Level.SEVERE, "Error al registrar el usuario", e);
        }
    }

    /**
     * Modifica un usuario existente en la base de datos.
     *
     * Actualiza la información del usuario con el ID especificado. Muestra un
     * mensaje de éxito si la actualización se realiza correctamente, o un
     * mensaje de error en caso contrario.
     *
     * @param nombre Nombre del usuario.
     * @param telefono Teléfono del usuario.
     * @param email Correo del usuario.
     * @param rol Rol del usuario.
     * @param contraseña Contraseña del usuario.
     * @param idUsuario ID del usuario a modificar.
     */
    public void modificarUsuario(final String nombre, final String telefono, final String email, final String rol, final String contraseña, final int idUsuario) {
        final String sql = "UPDATE usuarios SET nombre = ?, telefono = ?, email = ?, rol = ?, contraseña = ? WHERE id = ?";

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Establecer los valores en la consulta
            pstmt.setString(1, nombre);
            pstmt.setString(2, telefono);
            pstmt.setString(3, email);
            pstmt.setString(4, rol);
            // Encriptar la contraseña antes de guardarla
            String contraseñaEncriptada = Seguridad.encriptarContraseña(contraseña);
            pstmt.setString(5, contraseñaEncriptada);
            pstmt.setInt(6, idUsuario);

            // Ejecutar la actualización
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Usuario modificado exitosamente");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo modificar el usuario",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al modificar el usuario", e);
            JOptionPane.showMessageDialog(null, "Error al modificar el usuario: "
                    + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * Solicita confirmación antes de eliminar el usuario. Muestra un mensaje de
     * éxito si la eliminación se realiza correctamente, o un mensaje de error
     * en caso contrario.
     *
     * @param idUsuario ID del usuario a eliminar.
     * @param tbUsuarios Tabla de usuarios para actualizar después de la
     * eliminación.
     */
    public void eliminarUsuario(int idUsuario, JTable tbUsuarios) {
        if (idUsuario == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un usuario",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmar la eliminación
        int confirmacion = JOptionPane.showConfirmDialog(null,
                "¿Está seguro de que desea eliminar este usuario?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);

            // Ejecutar la consulta
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Usuario eliminado exitosamente");
                mostrarUsuarios(tbUsuarios);  // Actualizar la tabla
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo eliminar el usuario",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el usuario: "
                    + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para obtener el ID del usuario seleccionado en una tabla.
     *
     * Este método devuelve el ID del usuario seleccionado en la tabla
     * (`JTable`). Si no hay una fila seleccionada, devuelve `-1` para indicar
     * que no se ha seleccionado ningún usuario.
     *
     * @param tabla La tabla (`JTable`) de donde se obtiene el usuario
     * seleccionado.
     * @return El ID del usuario seleccionado o `-1` si no se ha seleccionado
     * ninguna fila.
     */
    public int obtenerIdUsuarioSeleccionado(final JTable tabla) {
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

    /**
     * Muestra todos los usuarios en una tabla.
     *
     * Consulta la base de datos para obtener la lista de usuarios y la muestra
     * en la tabla proporcionada.
     *
     * @param tablaUsuarios Tabla donde se mostrarán los usuarios.
     */
    public void mostrarUsuarios(final JTable tablaUsuarios) {
        final DefaultTableModel modelo = new DefaultTableModel();
        final String sql = "SELECT * FROM usuarios";
        modelo.addColumn("Id");
        modelo.addColumn("Usuario");
        modelo.addColumn("Email");
        modelo.addColumn("# Tel");
        modelo.addColumn("Rol");
        modelo.addColumn("Contraseña");

        tablaUsuarios.setModel(modelo);

        // Establecer el ancho de la columna "Id"
        tablaUsuarios.getColumnModel().getColumn(0).setMinWidth(45); // Tamaño mínimo
        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(50); // Tamaño preferido
        tablaUsuarios.getColumnModel().getColumn(0).setMaxWidth(70); // Tamaño máximo
        // Establecer el ancho de la columna "Email"
        tablaUsuarios.getColumnModel().getColumn(2).setMinWidth(200); // Tamaño mínimo
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(220); // Tamaño preferido
        tablaUsuarios.getColumnModel().getColumn(2).setMaxWidth(250); // Tamaño máximo
        // Establecer el ancho de la columna "Rol"
        tablaUsuarios.getColumnModel().getColumn(4).setMinWidth(45); // Tamaño mínimo
        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(50); // Tamaño preferido
        tablaUsuarios.getColumnModel().getColumn(4).setMaxWidth(50); // Tamaño máximo        

        try (Connection conn = conexion.obtenerConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getString("rol"),
                    rs.getString("contraseña")
                });
            }
            tablaUsuarios.setModel(modelo);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al mostrar usuarios", e);
            JOptionPane.showMessageDialog(null, "Error al mostrar: " + e.toString());
        }
    }

    /**
     * Muestra los usuarios en una tabla, con o sin filtro por rol.
     *
     * @param tabla La tabla donde se mostrarán los usuarios.
     * @param rol El rol por el cual filtrar los usuarios. Si es "Todas",
     * muestra todos.
     */
    public void mostrarUsuariosPorRol(JTable tablaUsuarios, String rolSeleccionado) {
        DefaultTableModel modelo = (DefaultTableModel) tablaUsuarios.getModel();
        modelo.setRowCount(0); // Limpia la tabla antes de agregar los resultados

        String query = "SELECT * FROM usuarios";
        boolean filtrarPorRol = !"Todas".equals(rolSeleccionado);

        if (filtrarPorRol) {
            query += " WHERE rol = ?";
        }

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (filtrarPorRol) {
                // Extrae el número del rol del texto seleccionado
                int rolNumerico = Integer.parseInt(rolSeleccionado.split(":")[0].trim());
                stmt.setInt(1, rolNumerico);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getInt("rol"), // Muestra directamente el número del rol
                    rs.getString("contraseña")

                };
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar usuarios por rol: " + e.getMessage());
        }
    }

    // Validar usuario regular 
    public boolean validarCredencialesUsuarioRegular(final String usuario, final String contraseña) {
        final String sql = "SELECT contraseña FROM usuarios WHERE nombre = ? AND rol != 1";

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Obtén la contraseña encriptada de la base de datos
                    String contraseñaEncriptada = rs.getString("contraseña");
                    // Compara la contraseña ingresada con la almacenada
                    return Seguridad.validarContraseña(contraseña, contraseñaEncriptada);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al validar credenciales del usuario regular", e);
            JOptionPane.showMessageDialog(null, "Error al validar credenciales: " + e.getMessage());
        }
        return false;
    }
// Validar credenciales administrador

    public boolean validarCredencialesAdmin(final String usuario, final String contraseña) {
        String usuarioEnMinusculas = usuario.toLowerCase();
        final String sql = "SELECT contraseña FROM usuarios WHERE nombre = ? AND rol = 1";

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuarioEnMinusculas);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Obtén la contraseña encriptada de la base de datos
                    String contraseñaEncriptada = rs.getString("contraseña");
                    // Compara la contraseña ingresada con la almacenada
                    return Seguridad.validarContraseña(contraseña, contraseñaEncriptada);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al validar credenciales del administrador", e);
            JOptionPane.showMessageDialog(null, "Error al validar credenciales: " + e.getMessage());
        }
        return false;
    }

    // Validar si existe un administrador 
    public boolean existeAdministrador() {
        boolean existe = false;
        try (Connection con = new ConexionDB(rutaDB.cargarRutaBaseDatos()).obtenerConexion()) {
            String sql = "SELECT COUNT(*) FROM usuarios WHERE rol = 1"; // Verifica si hay un admin
            try (PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    existe = true; // Si hay un administrador, devuelve true
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return existe;
    }

    /**
     * Valida la existencia de un usuario.
     *
     * @param nombreUsuario Nombre del usuario a validar.
     * @return `true` si el usuario existe, `false` en caso contrario.
     */
    public boolean validarExistenciaUsuario(final String nombreUsuario) {
        final String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE nombre = ?";

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt("total") > 0;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al validar existencia del usuario", e);
            JOptionPane.showMessageDialog(null, "Error al validar existencia de usuario: "
                    + e.getMessage());
        }
        return false;
    }

    public boolean validarExistenciaPorCorreo(final String email) {
        final String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE email = ?";

        try (Connection conn = conexion.obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt("total") > 0;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al validar existencia del correo", e);
            JOptionPane.showMessageDialog(null, "Error al validar existencia de correo: " + e.getMessage());
        }
        return false;
    }
}
