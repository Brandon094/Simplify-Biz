package com.mycompany.zl_solucion_integral.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creación de tablas SQLite. La conexión la gestiona {@link GestorConexion}.
 */
public class ConexionDB {

    private static final Logger LOGGER = Logger.getLogger(ConexionDB.class.getName());

    public Connection obtenerConexion() {
        return GestorConexion.getInstancia().obtenerConexion();
    }

    public void cerrarConexion(Connection conn) {
        // La conexión es compartida; no se cierra aquí.
    }

    public void inicializarBaseDeDatos() {
        Connection conn = obtenerConexion();
        crearTabla(conn, "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "telefono TEXT NOT NULL,"
                + "email TEXT NOT NULL,"
                + "rol INTEGER NOT NULL,"
                + "contraseña TEXT NOT NULL);", "usuarios");

        crearTabla(conn, "CREATE TABLE IF NOT EXISTS productos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "producto TEXT NOT NULL,"
                + "precio REAL NOT NULL,"
                + "cantidad INTEGER NOT NULL,"
                + "codigo TEXT NOT NULL,"
                + "categoria TEXT);", "productos");

        crearTabla(conn, "CREATE TABLE IF NOT EXISTS ventas ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "cliente TEXT NOT NULL,"
                + "cc_cliente TEXT NOT NULL,"
                + "vendedor TEXT NOT NULL,"
                + "fecha DATE NOT NULL,"
                + "total REAL NOT NULL,"
                + "metodo_pago TEXT NOT NULL,"
                + "pago_confirmado TEXT);", "ventas");

        crearTabla(conn, "CREATE TABLE IF NOT EXISTS detalles_venta ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "venta_id INTEGER NOT NULL,"
                + "producto TEXT NOT NULL,"
                + "cantidad INTEGER NOT NULL,"
                + "codigo TEXT NOT NULL,"
                + "precio REAL NOT NULL,"
                + "total REAL NOT NULL,"
                + "FOREIGN KEY (venta_id) REFERENCES ventas(id));", "detalles_venta");

        crearTabla(conn, "CREATE TABLE IF NOT EXISTS configuracion ("
                + "id INTEGER PRIMARY KEY,"
                + "ultimoNumeroCotizacion TEXT);", "configuracion");

        insertarValorInicialConfiguracion(conn);
        LOGGER.log(Level.INFO, "Tablas verificadas o creadas correctamente.");
    }

    private void crearTabla(Connection conn, String sql, String nombreTabla) {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
            LOGGER.log(Level.FINE, "Tabla ''{0}'' verificada o creada.", nombreTabla);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear la tabla '" + nombreTabla + "'", e);
        }
    }

    private void insertarValorInicialConfiguracion(Connection conn) {
        String sql = "INSERT OR IGNORE INTO configuracion (id, ultimoNumeroCotizacion) VALUES (1, '20241212-000');";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al insertar valor inicial en configuracion", e);
        }
    }
}
