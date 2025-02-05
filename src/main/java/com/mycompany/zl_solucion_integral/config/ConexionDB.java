package com.mycompany.zl_solucion_integral.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Clase para manejar la conexión con la base de datos SQLite.
 *
 * Esta clase proporciona métodos para conectar, cerrar la conexión y crear las
 * tablas necesarias si no existen.
 *
 * @author Dazac
 */
public class ConexionDB {

    private final String rutaDB;

    // Constructor que recibe la ruta de la base de datos
    public ConexionDB(String rutaDB) {
        // Verificar y crear el directorio si no existe
        File directorio = new File(rutaDB);
        if (!directorio.exists()) {
            directorio.mkdirs();  // Crea el directorio si no existe
        }
        this.rutaDB = "jdbc:sqlite:" + rutaDB + "/db.db";
        System.out.println("Ruta de la base de datos: " + this.rutaDB);
    }

    /**
     * Obtiene una conexión con la base de datos SQLite.
     *
     * @return un objeto Connection o null si hay un error.
     */
    public Connection obtenerConexion() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(rutaDB);
            System.out.println("Conexión establecida correctamente.");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC de SQLite no encontrado.");
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos:\n" + e.getMessage());
        }
        return null;
    }

    /**
     * Cierra una conexión de manera segura.
     *
     * @param conn la conexión a cerrar.
     */
    public void cerrarConexion(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Ejecuta una consulta SQL para crear una tabla si no existe.
     *
     * @param sql la consulta SQL de creación de tabla.
     */
    private void crearTabla(String sql, String nombreTabla) {
        Connection conn = obtenerConexion();
        if (conn == null) {
            System.err.println("No se pudo establecer la conexión a la base de datos.");
            return;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
            System.out.println("Tabla '" + nombreTabla + "' verificada o creada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla '" + nombreTabla + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea todas las tablas necesarias en la base de datos.
     */
    public void inicializarBaseDeDatos() {
        try {
            // Intentar crear la tabla "usuarios"
            crearTabla("CREATE TABLE IF NOT EXISTS usuarios ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombre TEXT NOT NULL,"
                    + "telefono TEXT NOT NULL,"
                    + "email TEXT NOT NULL,"
                    + "rol INTEGER NOT NULL,"
                    + "contraseña TEXT NOT NULL);", "usuarios");

            // Intentar crear la tabla "productos"
            crearTabla("CREATE TABLE IF NOT EXISTS productos ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "producto TEXT NOT NULL,"
                    + "precio REAL NOT NULL,"
                    + "cantidad INTEGER NOT NULL,"
                    + "codigo TEXT NOT NULL,"
                    + "categoria TEXT);", "productos");

            // Intentar crear la tabla "ventas"
            crearTabla("CREATE TABLE IF NOT EXISTS ventas ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "cliente TEXT NOT NULL,"
                    + "cc_cliente TEXT NOT NULL,"
                    + "vendedor TEXT NOT NULL,"
                    + "fecha DATE NOT NULL,"
                    + "total REAL NOT NULL,"
                    + "metodo_pago TEXT NOT NULL,"
                    + "pago_confirmado TEXT);", "ventas");

            // Intentar crear la tabla "detalles_venta"
            crearTabla("CREATE TABLE IF NOT EXISTS detalles_venta ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "venta_id INTEGER NOT NULL,"
                    + "producto TEXT NOT NULL,"
                    + "cantidad INTEGER NOT NULL,"
                    + "codigo TEXT NOT NULL,"
                    + "precio REAL NOT NULL,"
                    + "total REAL NOT NULL,"
                    + "FOREIGN KEY (venta_id) REFERENCES ventas(id));", "detalles_venta");

            // Intentar crear la tabla "configuracion"
            crearTabla("CREATE TABLE IF NOT EXISTS configuracion ("
                    + "id INTEGER PRIMARY KEY,"
                    + "ultimoNumeroCotizacion TEXT);", "configuracion");

            // Si las tablas se crean correctamente, se imprime un mensaje de éxito
            System.out.println("Las tablas se crearon correctamente.");

            // Llamamos al método para insertar valor inicial en la tabla de configuración
            insertarValorInicialConfiguracion();

        } catch (Exception e) {
            // Si ocurre un error, se imprime el mensaje de error
            System.err.println("Error al crear las tablas: " + e.getMessage());
        }
    }

    /**
     * Inserta un valor inicial en la tabla 'configuracion' si no existe.
     */
    private void insertarValorInicialConfiguracion() {
        String sql = "INSERT OR IGNORE INTO configuracion (id, ultimoNumeroCotizacion) VALUES (1, '20241212-000');";
        try (Connection conn = obtenerConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn != null) {
                pstmt.executeUpdate();
                System.out.println("Valor inicial establecido en 'configuracion'.");
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar valor inicial en 'configuracion': " + e.getMessage());
            e.printStackTrace();
        }
    }
}
