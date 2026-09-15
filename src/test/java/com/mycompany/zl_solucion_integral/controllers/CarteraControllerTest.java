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

public class CarteraControllerTest {

    @TempDir
    Path tempFolder;

    private CarteraController carteraCtrl;
    private VentasController ventasCtrl;
    private ProductoController productoCtrl;

    @BeforeEach
    void setUp() {
        String dbUrl = "jdbc:sqlite:" + tempFolder.resolve("cartera_test.db").toAbsolutePath();
        GestorConexion.getInstancia().reiniciarParaPruebas(dbUrl);
        new ConexionDB().inicializarBaseDeDatos();
        carteraCtrl = new CarteraController();
        ventasCtrl = new VentasController();
        productoCtrl = new ProductoController();
    }

    @AfterEach
    void tearDown() {
        GestorConexion.getInstancia().cerrar();
    }

    @Test
    void testRegistrarAbonoYSaldoPendiente() {
        // 1. Crear producto
        Producto prod = new Producto("Casco Moto", 100000.0, 10, "SKU-CASCO-MOTO", "ACCESORIOS");
        productoCtrl.agregarOActualizarProductoSiExiste(prod);

        // 2. Realizar venta a Crédito
        Venta venta = new Venta();
        Usuario cliente = new Usuario();
        cliente.setNombre("Cliente Credito");
        cliente.setNoCc("777777");
        venta.setCliente(cliente);
        venta.setVendedor("Vendedor1");
        venta.setFecha(LocalDate.now());
        venta.setTotal(100000.0);
        venta.setMetodoPago("Crédito");

        List<Producto> vendidos = new ArrayList<>();
        Producto item = productoCtrl.buscarProductoPorCodigo("SKU-CASCO-MOTO");
        item.setCantidadSolicitada(1);
        vendidos.add(item);

        ResultadoOperacion resVenta = ventasCtrl.guardarVenta(venta, vendidos, null);
        assertTrue(resVenta.esExito(), "La venta a crédito debe guardarse con éxito");

        // 3. Verificar que la deuda aparece en cartera
        JTable tablaCartera = new JTable();
        carteraCtrl.obtenerVentasEnCartera(tablaCartera, "");
        assertEquals(1, tablaCartera.getModel().getRowCount(), "Debe haber 1 registro de deudor");

        // Obtener ID de la venta guardada (columna 0)
        int ventaId = Integer.parseInt(tablaCartera.getModel().getValueAt(0, 0).toString());

        // 4. Registrar Abono Parcial de 40,000
        ResultadoOperacion resAbono1 = carteraCtrl.registrarAbono(ventaId, 40000.0, "Efectivo", "Primer abono");
        assertTrue(resAbono1.esExito(), "El abono parcial debe registrarse exitosamente");

        // 5. Registrar Abono Final de 60,000 para liquidar la deuda (Total 100,000)
        ResultadoOperacion resAbono2 = carteraCtrl.registrarAbono(ventaId, 60000.0, "Transferencia", "Abono final");
        assertTrue(resAbono2.esExito(), "El abono final debe liquidar la cartera");

        // 6. Verificar que la venta ahora figura como Pagada
        JTable tablaSaldada = new JTable();
        carteraCtrl.obtenerVentasEnCartera(tablaSaldada, "");
        assertEquals(1, tablaSaldada.getModel().getRowCount());
        String estado = tablaSaldada.getModel().getValueAt(0, 8).toString();
        assertEquals("pagado", estado, "El estado debe cambiar a 'pagado' tras saldar la deuda");
    }

    @Test
    void testAbonoExcedeSaldoRetornaError() {
        Producto prod = new Producto("Chaqueta", 50000.0, 5, "SKU-CHAQUETA", "ROPA");
        productoCtrl.agregarOActualizarProductoSiExiste(prod);

        Venta venta = new Venta();
        Usuario cliente = new Usuario();
        cliente.setNombre("Cliente Test");
        cliente.setNoCc("888888");
        venta.setCliente(cliente);
        venta.setVendedor("Vendedor1");
        venta.setFecha(LocalDate.now());
        venta.setTotal(50000.0);
        venta.setMetodoPago("Crédito");

        List<Producto> vendidos = new ArrayList<>();
        Producto item = productoCtrl.buscarProductoPorCodigo("SKU-CHAQUETA");
        item.setCantidadSolicitada(1);
        vendidos.add(item);

        ventasCtrl.guardarVenta(venta, vendidos, null);

        JTable tablaCartera = new JTable();
        carteraCtrl.obtenerVentasEnCartera(tablaCartera, "");
        int ventaId = Integer.parseInt(tablaCartera.getModel().getValueAt(0, 0).toString());

        // Intentar abonar 60,000 cuando la deuda es de 50,000
        ResultadoOperacion resExceso = carteraCtrl.registrarAbono(ventaId, 60000.0, "Efectivo", "Abono excesivo");
        assertFalse(resExceso.esExito(), "No debe permitir abonar más del saldo pendiente");
    }
}
