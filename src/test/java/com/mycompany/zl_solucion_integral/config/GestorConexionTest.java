package com.mycompany.zl_solucion_integral.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class GestorConexionTest {

    @TempDir
    Path tempFolder;

    @BeforeEach
    void setUp() {
        String dbUrl = "jdbc:sqlite:" + tempFolder.resolve("test.db").toAbsolutePath();
        GestorConexion.getInstancia().reiniciarParaPruebas(dbUrl);
        new ConexionDB().inicializarBaseDeDatos();
    }

    @AfterEach
    void tearDown() {
        GestorConexion.getInstancia().cerrar();
    }

    @Test
    void testObtenerConexion() throws SQLException {
        Connection conn = GestorConexion.getInstancia().obtenerConexion();
        assertNotNull(conn, "La conexión no debe ser nula");
        assertFalse(conn.isClosed(), "La conexión debe estar abierta");
    }

    @Test
    void testMismaInstanciaConexion() {
        Connection conn1 = GestorConexion.getInstancia().obtenerConexion();
        Connection conn2 = GestorConexion.getInstancia().obtenerConexion();
        assertSame(conn1, conn2, "Debe retornar la misma instancia de Connection compartida");
    }

    @Test
    void testResolverArchivoConCarpeta() {
        File carpeta = tempFolder.toFile();
        File archivo = GestorConexion.resolverArchivo(carpeta.getAbsolutePath());
        assertEquals("db.db", archivo.getName(), "Para carpetas debe componer <carpeta>/db.db");
        assertEquals(carpeta.getAbsolutePath(), archivo.getParentFile().getAbsolutePath());
    }

    @Test
    void testResolverArchivoConFicheroDb() {
        File dbFile = tempFolder.resolve("mi_base.db").toFile();
        File res = GestorConexion.resolverArchivo(dbFile.getAbsolutePath());
        assertEquals(dbFile.getAbsolutePath(), res.getAbsolutePath(), "Archivos .db se usan tal cual");
    }

    @Test
    void testResolverArchivoConFicheroSqlite() {
        File sqliteFile = tempFolder.resolve("datos.sqlite").toFile();
        File res = GestorConexion.resolverArchivo(sqliteFile.getAbsolutePath());
        assertEquals(sqliteFile.getAbsolutePath(), res.getAbsolutePath(), "Archivos .sqlite se usan tal cual");
    }

    @Test
    void testResolverArchivoConCarpetaConSufijoSqlite() {
        File carpetaSqlite = tempFolder.resolve("db.sqlite").toFile();
        carpetaSqlite.mkdirs();
        File res = GestorConexion.resolverArchivo(carpetaSqlite.getAbsolutePath());
        assertEquals("db.db", res.getName());
        assertEquals(carpetaSqlite.getAbsolutePath(), res.getParentFile().getAbsolutePath());
    }

    @Test
    void testResolverArchivoCadenaVaciaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> GestorConexion.resolverArchivo("   "));
        assertThrows(IllegalArgumentException.class, () -> GestorConexion.resolverArchivo(null));
    }
}
