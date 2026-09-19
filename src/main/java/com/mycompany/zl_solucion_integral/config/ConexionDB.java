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
                + "contraseña TEXT NOT NULL,"
                + "no_cc TEXT);", "usuarios");

        crearTabla(conn, "CREATE TABLE IF NOT EXISTS productos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "producto TEXT NOT NULL,"
                + "precio REAL NOT NULL,"
                + "precio_costo REAL DEFAULT 0.0,"
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
                + "precio_costo REAL DEFAULT 0.0,"
                + "total REAL NOT NULL,"
                + "FOREIGN KEY (venta_id) REFERENCES ventas(id));", "detalles_venta");

        // Migración limpia para instalaciones previas que no tenían ciertas columnas
        migrarColumnaSegura(conn, "usuarios", "no_cc", "TEXT");
        migrarColumnaSegura(conn, "productos", "precio_costo", "REAL DEFAULT 0.0");
        migrarColumnaSegura(conn, "detalles_venta", "precio_costo", "REAL DEFAULT 0.0");
        migrarColumnaSegura(conn, "detalles_venta", "descuento", "REAL DEFAULT 0.0");

        crearTabla(conn, "CREATE TABLE IF NOT EXISTS configuracion ("
                + "id INTEGER PRIMARY KEY,"
                + "ultimoNumeroCotizacion TEXT);", "configuracion");

        crearTabla(conn, "CREATE TABLE IF NOT EXISTS categorias ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT UNIQUE NOT NULL);", "categorias");

        crearTabla(conn, "CREATE TABLE IF NOT EXISTS abonos_cartera ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "venta_id INTEGER NOT NULL,"
                + "monto REAL NOT NULL,"
                + "fecha DATE NOT NULL,"
                + "metodo_pago TEXT NOT NULL,"
                + "observacion TEXT,"
                + "FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE);", "abonos_cartera");

        crearTabla(conn, "CREATE TABLE IF NOT EXISTS proveedores ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "nit TEXT UNIQUE NOT NULL,"
                + "telefono TEXT,"
                + "email TEXT,"
                + "direccion TEXT);", "proveedores");

        crearTabla(conn, "CREATE TABLE IF NOT EXISTS compras ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "proveedor_id INTEGER,"
                + "proveedor_nombre TEXT NOT NULL,"
                + "proveedor_nit TEXT NOT NULL,"
                + "num_factura TEXT NOT NULL,"
                + "usuario_registro TEXT NOT NULL,"
                + "fecha DATE NOT NULL,"
                + "total REAL NOT NULL,"
                + "FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE SET NULL);", "compras");

        crearTabla(conn, "CREATE TABLE IF NOT EXISTS detalles_compra ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "compra_id INTEGER NOT NULL,"
                + "producto TEXT NOT NULL,"
                + "codigo TEXT NOT NULL,"
                + "cantidad INTEGER NOT NULL,"
                + "precio_costo REAL NOT NULL,"
                + "subtotal REAL NOT NULL,"
                + "FOREIGN KEY (compra_id) REFERENCES compras(id) ON DELETE CASCADE);", "detalles_compra");

        insertarValorInicialConfiguracion(conn);
        sincronizarCategoriasIniciales(conn);
        LOGGER.log(Level.INFO, "Tablas verificadas o creadas correctamente.");
    }

    private void sincronizarCategoriasIniciales(Connection conn) {
        String sqlDefault = "INSERT OR IGNORE INTO categorias (nombre) VALUES "
                + "('DOTACION HOMBRE'), ('DOTACION DAMA'), ('CALZADO'), ('EPP'), ('BOTIQUINES'), ('SEÑALIZACION'), "
                + "('HERRAMIENTAS'), ('MATERIALES'), ('ELECTRÓNICA'), ('BEBIDAS'), ('LIMPIEZA'), ('OTROS');";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDefault)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al insertar categorías iniciales por defecto", e);
        }

        String sqlSync = "INSERT OR IGNORE INTO categorias (nombre) "
                + "SELECT DISTINCT UPPER(TRIM(categoria)) FROM productos WHERE categoria IS NOT NULL AND TRIM(categoria) != '';";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSync)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al sincronizar categorías desde productos", e);
        }
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

    private void migrarColumnaSegura(Connection conn, String tabla, String columna, String definicion) {
        String sql = "ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + definicion;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
            LOGGER.log(Level.INFO, "Columna ''{0}'' agregada exitosamente a la tabla ''{1}''.", new Object[]{columna, tabla});
        } catch (SQLException e) {
            // Ignorar error si la columna ya existe en SQLite
        }
    }
}
