package com.mycompany.zl_solucion_integral.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelSQLiteManagerTest {

    @TempDir
    Path tempFolder;

    @BeforeEach
    void setUp() {
        String dbUrl = "jdbc:sqlite:" + tempFolder.resolve("excel_test.db").toAbsolutePath();
        GestorConexion.getInstancia().reiniciarParaPruebas(dbUrl);
        new ConexionDB().inicializarBaseDeDatos();
    }

    @AfterEach
    void tearDown() {
        GestorConexion.getInstancia().cerrar();
    }

    @Test
    void testGenerarPlantillaYLeerCabeceras() throws Exception {
        File plantilla = tempFolder.resolve("plantilla_test.xlsx").toFile();
        ResultadoOperacion resGen = ExcelSQLiteManager.generarPlantillaModelo(plantilla);
        assertTrue(resGen.esExito(), "Debe generar la plantilla Excel modelo");
        assertTrue(plantilla.exists(), "El archivo de plantilla debe existir");

        List<String> cabeceras = ExcelSQLiteManager.leerCabeceras(plantilla);
        assertEquals(6, cabeceras.size(), "Debe tener 6 columnas en la cabecera");
        assertEquals("CÓDIGO SKU", cabeceras.get(0));
        assertEquals("NOMBRE DEL PRODUCTO", cabeceras.get(1));
    }

    @Test
    void testImportarConMapeoExcel() throws Exception {
        File plantilla = tempFolder.resolve("import_test.xlsx").toFile();
        ExcelSQLiteManager.generarPlantillaModelo(plantilla);

        Map<String, Integer> mapeo = new HashMap<>();
        mapeo.put("codigo", 0);
        mapeo.put("nombre", 1);
        mapeo.put("precio", 2);
        mapeo.put("precio_costo", 3);
        mapeo.put("cantidad", 4);
        mapeo.put("categoria", 5);

        ResultadoOperacion resImp = ExcelSQLiteManager.importarConMapeo(plantilla, mapeo, "GENERAL");
        assertTrue(resImp.esExito(), "La importación debe ser exitosa");

        // Re-importar el mismo archivo debe actualizar (Upsert) en lugar de duplicar
        ResultadoOperacion resUpsert = ExcelSQLiteManager.importarConMapeo(plantilla, mapeo, "GENERAL");
        assertTrue(resUpsert.esExito());
        assertTrue(resUpsert.getMensaje().contains("actualizados"));
    }
}
