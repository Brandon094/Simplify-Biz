package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.models.Producto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ProductoControllerTest {

    @TempDir
    Path tempFolder;

    private ProductoController controller;

    @BeforeEach
    void setUp() {
        String dbUrl = "jdbc:sqlite:" + tempFolder.resolve("productos_test.db").toAbsolutePath();
        GestorConexion.getInstancia().reiniciarParaPruebas(dbUrl);
        new ConexionDB().inicializarBaseDeDatos();
        controller = new ProductoController();
    }

    @AfterEach
    void tearDown() {
        GestorConexion.getInstancia().cerrar();
    }

    @Test
    void testAgregarProductoNuevo() {
        Producto p = new Producto("Taladro", 150000.0, 10, "SKU-001", "HERRAMIENTAS");
        ResultadoOperacion res = controller.agregarOActualizarProductoSiExiste(p);

        assertTrue(res.esExito());
        Producto guardado = controller.buscarProductoPorCodigo("SKU-001");
        assertNotNull(guardado);
        assertEquals("Taladro", guardado.getProducto());
        assertEquals(10, guardado.getCantidad());
    }

    @Test
    void testAgregarProductoExistenteSumaStock() {
        Producto p1 = new Producto("Martillo", 25000.0, 5, "SKU-002", "HERRAMIENTAS");
        controller.agregarOActualizarProductoSiExiste(p1);

        Producto p2 = new Producto("Martillo", 25000.0, 15, "SKU-002", "HERRAMIENTAS");
        ResultadoOperacion res = controller.agregarOActualizarProductoSiExiste(p2);

        assertTrue(res.esExito());
        Producto guardado = controller.buscarProductoPorCodigo("SKU-002");
        assertNotNull(guardado);
        assertEquals(20, guardado.getCantidad(), "Debe sumar el stock existente (5 + 15 = 20)");
    }

    @Test
    void testModificarProducto() {
        Producto p = new Producto("Destornillador", 12000.0, 8, "SKU-003", "HERRAMIENTAS");
        controller.agregarOActualizarProductoSiExiste(p);

        Producto guardado = controller.buscarProductoPorCodigo("SKU-003");
        guardado.setProducto("Destornillador Pro");
        guardado.setPrecio(15000.0);

        ResultadoOperacion res = controller.modificarProducto(guardado);
        assertTrue(res.esExito());

        Producto modificado = controller.buscarProductoPorCodigo("SKU-003");
        assertEquals("Destornillador Pro", modificado.getProducto());
        assertEquals(15000.0, modificado.getPrecio());
    }

    @Test
    void testEliminarProducto() {
        Producto p = new Producto("Cinta Aislante", 5000.0, 50, "SKU-004", "MATERIALES");
        controller.agregarOActualizarProductoSiExiste(p);

        Producto guardado = controller.buscarProductoPorCodigo("SKU-004");
        ResultadoOperacion res = controller.eliminarProducto(guardado.getId());

        assertTrue(res.esExito());
        assertNull(controller.buscarProductoPorCodigo("SKU-004"));
    }

    @Test
    void testEliminarCantidadProducto() {
        Producto p = new Producto("Llave Inglesa", 45000.0, 10, "SKU-005", "HERRAMIENTAS");
        controller.agregarOActualizarProductoSiExiste(p);

        Producto guardado = controller.buscarProductoPorCodigo("SKU-005");
        ResultadoOperacion res = controller.eliminarCantidadProducto(guardado.getId(), 4);

        assertTrue(res.esExito());
        Producto actualizado = controller.buscarProductoPorCodigo("SKU-005");
        assertEquals(6, actualizado.getCantidad());
    }

    @Test
    void testObtenerProductosStockCritico() {
        controller.agregarOActualizarProductoSiExiste(new Producto("Alto Stock", 1000.0, 20, "SKU-C1", "GRAL"));
        controller.agregarOActualizarProductoSiExiste(new Producto("Bajo Stock 1", 2000.0, 2, "SKU-C2", "GRAL"));
        controller.agregarOActualizarProductoSiExiste(new Producto("Bajo Stock 2", 3000.0, 0, "SKU-C3", "GRAL"));

        java.util.List<Producto> criticos = controller.obtenerProductosStockCritico(5);
        assertEquals(2, criticos.size());
        assertEquals("Bajo Stock 2", criticos.get(0).getProducto(), "Debe ordenar por cantidad ascendente (0 unidades primero)");
        assertEquals(0, criticos.get(0).getCantidad());
        assertEquals("Bajo Stock 1", criticos.get(1).getProducto());
        assertEquals(2, criticos.get(1).getCantidad());
    }

    @Test
    void testObtenerDesgloseInversionInventario() {
        Producto p1 = new Producto("Item A", 10000.0, 10, "SKU-D1", "CAT1");
        p1.setPrecioCosto(5000.0);
        controller.agregarOActualizarProductoSiExiste(p1);

        double[] desglose = controller.obtenerDesgloseInversionInventario();
        assertNotNull(desglose);
        assertEquals(3, desglose.length);
        assertEquals(50000.0, desglose[0], 0.01, "Total invertido = 10 * 5000 = 50000");
        assertEquals(0.0, desglose[1], 0.01, "Sin abastecimiento = 0");
        assertEquals(50000.0, desglose[2], 0.01, "Inversión directa = 50000");
    }
}
