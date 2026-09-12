package com.mycompany.zl_solucion_integral.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Conexión única a SQLite (singleton). Evita abrir múltiples conexiones, el
 * spam de logs y el error {@code database is locked}.
 * <p>
 * Los consumidores <strong>no deben cerrar</strong> la {@link Connection}
 * devuelta: solo cierran {@code Statement} y {@code ResultSet}.
 */
public final class GestorConexion {

    private static final Logger LOGGER = Logger.getLogger(GestorConexion.class.getName());
    private static final GestorConexion INSTANCIA = new GestorConexion();

    private Connection conexion;
    private String jdbcUrl;
    private boolean logInicialEmitido;

    private GestorConexion() {
    }

    public static GestorConexion getInstancia() {
        return INSTANCIA;
    }

    /**
     * Inicializa (o reabre) la conexión a partir de {@code db.path}.
     *
     * @param dbPath carpeta de datos o ruta a un archivo {@code .db}/{@code .sqlite}.
     */
    public synchronized void inicializar(String dbPath) {
        File archivo = resolverArchivo(dbPath);
        this.jdbcUrl = "jdbc:sqlite:" + archivo.getAbsolutePath();
        cerrarSilencioso();
        abrirSiEsNecesario();
    }

    /**
     * Sustituye la conexión por una URL JDBC concreta (pruebas).
     */
    public synchronized void reiniciarParaPruebas(String jdbcUrlPrueba) {
        this.jdbcUrl = jdbcUrlPrueba;
        this.logInicialEmitido = false;
        cerrarSilencioso();
        abrirSiEsNecesario();
    }

    /**
     * Resuelve el archivo SQLite efectivo.
     * <ul>
     *   <li>Si {@code dbPath} es un archivo ({@code .db}, {@code .sqlite} o existe
     *       como fichero), se usa tal cual.</li>
     *   <li>Si es una carpeta (o no existe), se usa {@code <carpeta>/db.db}.</li>
     * </ul>
     */
    public static File resolverArchivo(String dbPath) {
        if (dbPath == null || dbPath.trim().isEmpty()) {
            throw new IllegalArgumentException("db.path no puede estar vacío");
        }
        File ruta = new File(dbPath.trim());

        // Si la ruta existe y es un directorio (ej. instalaciones previas con carpeta db.sqlite), se usa <carpeta>/db.db
        if (ruta.exists() && ruta.isDirectory()) {
            return new File(ruta, "db.db");
        }

        // Si la ruta existe y es un archivo, se usa tal cual
        if (ruta.exists() && ruta.isFile()) {
            File padre = ruta.getParentFile();
            if (padre != null) {
                padre.mkdirs();
            }
            return ruta;
        }

        // Si no existe aún pero el nombre termina en .db o .sqlite, se trata como archivo
        String nombre = ruta.getName().toLowerCase();
        boolean pareceArchivo = nombre.endsWith(".db") || nombre.endsWith(".sqlite");
        if (pareceArchivo) {
            File padre = ruta.getParentFile();
            if (padre != null) {
                padre.mkdirs();
            }
            return ruta;
        }

        // Si no existe y no parece archivo, se crea el directorio y se usa <carpeta>/db.db
        if (!ruta.exists()) {
            ruta.mkdirs();
        }
        return new File(ruta, "db.db");
    }

    /**
     * Devuelve la conexión compartida. No cerrar desde los controladores.
     */
    public synchronized Connection obtenerConexion() {
        abrirSiEsNecesario();
        return conexion;
    }

    public synchronized void cerrar() {
        cerrarSilencioso();
        LOGGER.log(Level.INFO, "Conexión SQLite cerrada.");
    }

    private void abrirSiEsNecesario() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                return;
            }
            if (jdbcUrl == null) {
                String path = SelecionRuta.cargarRutaBaseDatos();
                File archivo = resolverArchivo(path);
                jdbcUrl = "jdbc:sqlite:" + archivo.getAbsolutePath();
            }
            Class.forName("org.sqlite.JDBC");
            conexion = DriverManager.getConnection(jdbcUrl);
            aplicarPragmas(conexion);
            if (!logInicialEmitido) {
                LOGGER.log(Level.INFO, "Conexión SQLite establecida: {0}", jdbcUrl);
                logInicialEmitido = true;
            } else {
                LOGGER.log(Level.FINE, "Conexión SQLite reabierta: {0}", jdbcUrl);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver JDBC de SQLite no encontrado", e);
            throw new IllegalStateException("Driver JDBC de SQLite no encontrado: " + e.getMessage(), e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar con la base de datos: " + e.getMessage(), e);
            throw new IllegalStateException("Error al conectar con la base de datos: " + e.getMessage(), e);
        }
    }

    private void aplicarPragmas(Connection conn) {
        try (Statement st = conn.createStatement()) {
            try {
                st.execute("PRAGMA journal_mode=WAL");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "No se pudo activar journal_mode=WAL: {0}", e.getMessage());
            }
            try {
                st.execute("PRAGMA busy_timeout=5000");
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "No se pudo aplicar busy_timeout: {0}", e.getMessage());
            }
            try {
                st.execute("PRAGMA foreign_keys=ON");
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "No se pudo aplicar foreign_keys: {0}", e.getMessage());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al crear statement para aplicar pragmas: {0}", e.getMessage());
        }
    }

    private void cerrarSilencioso() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.FINE, "Error al cerrar la conexión", e);
            }
            conexion = null;
        }
    }
}
