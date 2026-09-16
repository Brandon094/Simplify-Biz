package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.models.Producto;
import com.mycompany.zl_solucion_integral.models.Usuario;
import com.mycompany.zl_solucion_integral.models.Venta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.JTable;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VentasControllerTest {

    @TempDir
    Path tempFolder;

    private VentasController ventasCtrl;
    private ProductoController productoCtrl;

    @BeforeEach
    void setUp() {
        String dbUrl = "jdbc:sqlite:" + tempFolder.resolve("ventas_test.db").toAbsolutePath();
        GestorConexion.getInstancia().reiniciarParaPruebas(dbUrl);
        new ConexionDB().inicializarBaseDeDatos();
        ventasCtrl = new VentasController();
        productoCtrl = new ProductoController();
    }

    @AfterEach
    void tearDown() {
        GestorConexion.getInstancia().cerrar();
    }

    @Test
    void testGuardarVentaExitosoYDescuentoStock() {
        // Crear producto con stock 10
        Producto prod = new Producto("Casco de Seguridad", 80000.0, 10, "SKU-CASCO", "EPP");
        productoCtrl.agregarOActualizarProductoSiExiste(prod);

        // Preparar venta de 3 unidades
        Venta venta = new Venta();
        Usuario cliente = new Usuario();
        cliente.setNombre("Cliente Prueba");
        cliente.setNoCc("12345678");
        venta.setCliente(cliente);
        venta.setVendedor("Admin");
        venta.setFecha(LocalDate.now());
        venta.setTotal(240000.0);
        venta.setMetodoPago("Efectivo");

        List<Producto> vendidos = new ArrayList<>();
        Producto itemVendido = productoCtrl.buscarProductoPorCodigo("SKU-CASCO");
        itemVendido.setCantidadSolicitada(3);
        vendidos.add(itemVendido);

        ResultadoOperacion res = ventasCtrl.guardarVenta(venta, vendidos, null);
        assertTrue(res.esExito(), "La venta debe procesarse con éxito");

        // Verificar que el stock bajó de 10 a 7
        Producto actualizado = productoCtrl.buscarProductoPorCodigo("SKU-CASCO");
        assertEquals(7, actualizado.getCantidad(), "El stock debe actualizarse a 7");
    }

    @Test
    void testRollbackVentaStockInsuficiente() {
        Producto prod = new Producto("Guantes Nitrilo", 15000.0, 2, "SKU-GUANTES", "EPP");
        productoCtrl.agregarOActualizarProductoSiExiste(prod);

        Venta venta = new Venta();
        Usuario cliente = new Usuario();
        cliente.setNombre("Cliente Prueba");
        cliente.setNoCc("12345678");
        venta.setCliente(cliente);
        venta.setVendedor("Admin");
        venta.setFecha(LocalDate.now());
        venta.setTotal(75000.0);
        venta.setMetodoPago("Efectivo");

        List<Producto> vendidos = new ArrayList<>();
        Producto itemVendido = productoCtrl.buscarProductoPorCodigo("SKU-GUANTES");
        itemVendido.setCantidadSolicitada(5); // Solicita 5 pero solo hay 2
        vendidos.add(itemVendido);

        ResultadoOperacion res = ventasCtrl.guardarVenta(venta, vendidos, null);
        assertFalse(res.esExito(), "La venta debe fallar por stock insuficiente");

        // Verificar que el stock permaneció intacto (2)
        Producto sinCambios = productoCtrl.buscarProductoPorCodigo("SKU-GUANTES");
        assertEquals(2, sinCambios.getCantidad(), "El stock debe mantenerse intacto en 2");
    }

    @Test
    void testFiltroVentasPorFechas() {
        Producto prod = new Producto("Bota Dielectrica", 120000.0, 20, "SKU-BOTA", "CALZADO");
        productoCtrl.agregarOActualizarProductoSiExiste(prod);

        Venta venta = new Venta();
        Usuario cliente = new Usuario();
        cliente.setNombre("Cliente Rango");
        cliente.setNoCc("99999");
        venta.setCliente(cliente);
        venta.setVendedor("Admin");
        venta.setFecha(LocalDate.now());
        venta.setTotal(120000.0);
        venta.setMetodoPago("Efectivo");

        List<Producto> vendidos = new ArrayList<>();
        Producto item = productoCtrl.buscarProductoPorCodigo("SKU-BOTA");
        item.setCantidadSolicitada(1);
        vendidos.add(item);

        ventasCtrl.guardarVenta(venta, vendidos, null);

        JTable tabla = new JTable();
        String hoy = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now());
        ResultadoOperacion res = ventasCtrl.mostrarFechasDefinidas(tabla, hoy, hoy);

        assertTrue(res.esExito());
        assertEquals(1, tabla.getModel().getRowCount(), "Debe encontrar la venta realizada hoy");
    }

    @Test
    void testObtenerDesgloseMetodosPagoYUtilidadPorPeriodo() {
        Producto prod = new Producto("Overol Térmico", 100000.0, 10, "SKU-OVEROL", "ROPA");
        prod.setPrecioCosto(50000.0);
        productoCtrl.agregarOActualizarProductoSiExiste(prod);

        Venta venta = new Venta();
        Usuario cliente = new Usuario();
        cliente.setNombre("Cliente Crédito");
        cliente.setNoCc("77777");
        venta.setCliente(cliente);
        venta.setVendedor("Admin");
        venta.setFecha(LocalDate.now());
        venta.setTotal(200000.0);
        venta.setMetodoPago("Crédito");

        List<Producto> vendidos = new ArrayList<>();
        Producto item = productoCtrl.buscarProductoPorCodigo("SKU-OVEROL");
        item.setCantidadSolicitada(2);
        vendidos.add(item);

        ventasCtrl.guardarVenta(venta, vendidos, null);

        java.util.Map<String, Double> desglosePagos = ventasCtrl.obtenerDesgloseMetodosPagoPorPeriodo("Histórico Total");
        assertNotNull(desglosePagos);
        assertEquals(200000.0, desglosePagos.getOrDefault("Crédito", 0.0), 0.01);

        double[] desgloseUtilidad = ventasCtrl.obtenerDesgloseUtilidadNetaPorPeriodo("Histórico Total");
        assertNotNull(desgloseUtilidad);
        assertEquals(200000.0, desgloseUtilidad[0], 0.01, "Facturado = 200.000");
        assertEquals(100000.0, desgloseUtilidad[1], 0.01, "COGS = 2 * 50.000 = 100.000");
        assertEquals(100000.0, desgloseUtilidad[2], 0.01, "Utilidad neta = 100.000");
    }
}
