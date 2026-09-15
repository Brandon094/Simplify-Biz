package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.models.Proveedor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProveedorControllerTest {

    @TempDir
    Path tempFolder;

    private ProveedorController proveedorCtrl;

    @BeforeEach
    void setUp() {
        String dbUrl = "jdbc:sqlite:" + tempFolder.resolve("proveedor_test.db").toAbsolutePath();
        GestorConexion.getInstancia().reiniciarParaPruebas(dbUrl);
        new ConexionDB().inicializarBaseDeDatos();
        proveedorCtrl = new ProveedorController();
    }

    @AfterEach
    void tearDown() {
        GestorConexion.getInstancia().cerrar();
    }

    @Test
    void testGuardarYBuscarProveedorSugeridos() {
        Proveedor p = new Proveedor("Distribuidora Repuestos S.A.S", "NIT-900123", "3001234567", "ventas@distribuidora.com", "Calle 10 # 20-30");
        ResultadoOperacion resGuardar = proveedorCtrl.guardarOActualizarProveedor(p);
        assertTrue(resGuardar.esExito(), "El proveedor debe guardarse correctamente");

        List<Proveedor> sugeridos = proveedorCtrl.buscarProveedoresSugeridos("Distribuidora");
        assertFalse(sugeridos.isEmpty(), "Debe encontrar al menos un proveedor sugerido");
        assertEquals("NIT-900123", sugeridos.get(0).getNit());
        assertEquals("Distribuidora Repuestos S.A.S", sugeridos.get(0).getNombre());
    }
}
