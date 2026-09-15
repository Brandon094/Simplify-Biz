package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.models.Compra;
import com.mycompany.zl_solucion_integral.models.DetalleCompra;
import com.mycompany.zl_solucion_integral.models.Producto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComprasControllerTest {

    @TempDir
    Path tempFolder;

    private ComprasController comprasCtrl;
    private ProductoController productoCtrl;

    @BeforeEach
    void setUp() {
        String dbUrl = "jdbc:sqlite:" + tempFolder.resolve("compras_test.db").toAbsolutePath();
        GestorConexion.getInstancia().reiniciarParaPruebas(dbUrl);
        new ConexionDB().inicializarBaseDeDatos();
        comprasCtrl = new ComprasController();
        productoCtrl = new ProductoController();

        // Crear producto base
        Producto p = new Producto("Casco de Seguridad", 45000.0, 10, "SKU-CASCO", "EPP");
        p.setPrecioCosto(30000.0);
        productoCtrl.agregarOActualizarProductoSiExiste(p);
    }

    @AfterEach
    void tearDown() {
        GestorConexion.getInstancia().cerrar();
    }

    @Test
    void testGuardarEntradaCompraIncrementaStockYCosto() {
        Compra compra = new Compra(null, "Distribuidora Industrial S.A.S.", "900987654-3", "FAC-1001", "AdminTest", "2026-09-14", 320000.0);
        List<DetalleCompra> detalles = new ArrayList<>();
        detalles.add(new DetalleCompra("Casco de Seguridad", "SKU-CASCO", 10, 32000.0));

        ResultadoOperacion res = comprasCtrl.guardarEntradaCompra(compra, detalles);

        assertTrue(res.esExito());

        // Verificar que el stock se incrementó (10 + 10 = 20) y el costo se actualizó a 32000
        Producto prodActualizado = productoCtrl.buscarProductoPorCodigo("SKU-CASCO");
        assertNotNull(prodActualizado);
        assertEquals(20, prodActualizado.getCantidad());
        assertEquals(32000.0, prodActualizado.getPrecioCosto());
    }

    @Test
    void testResumenKPICompras() {
        Compra compra = new Compra(null, "Distribuidora Industrial S.A.S.", "900987654-3", "FAC-1002", "AdminTest", "2026-09-14", 150000.0);
        List<DetalleCompra> detalles = new ArrayList<>();
        detalles.add(new DetalleCompra("Casco de Seguridad", "SKU-CASCO", 5, 30000.0));

        comprasCtrl.guardarEntradaCompra(compra, detalles);

        double[] kpis = comprasCtrl.obtenerResumenCompras();
        assertEquals(150000.0, kpis[0]); // Total Invertido
        assertEquals(1.0, kpis[1]);       // Entradas Recibidas
        assertEquals(1.0, kpis[2]);       // Proveedores Distintos
    }

    @Test
    void testGuardarEntradaCompraAutoCreaProductoNuevo() {
        Compra compra = new Compra(null, "Proveedor Nuevo S.A.S.", "900111222-1", "FAC-2026", "AdminTest", "2026-09-15", 250000.0);
        List<DetalleCompra> detalles = new ArrayList<>();
        // Insumo que NO existe en la base de datos previa
        detalles.add(new DetalleCompra("Bujía NGK C7HSA", "NUEVO-BUJIA-99", 20, 12500.0));

        ResultadoOperacion res = comprasCtrl.guardarEntradaCompra(compra, detalles);

        assertTrue(res.esExito());

        // Verificar que el producto nuevo fue creado automáticamente en la base de datos
        Producto prodNuevo = productoCtrl.buscarProductoPorCodigo("NUEVO-BUJIA-99");
        assertNotNull(prodNuevo);
        assertEquals("Bujía NGK C7HSA", prodNuevo.getProducto());
        assertEquals(20, prodNuevo.getCantidad());
        assertEquals(12500.0, prodNuevo.getPrecioCosto());
        assertTrue(prodNuevo.getPrecio() > 12500.0); // Verifica margen de venta sugerido
    }
}
