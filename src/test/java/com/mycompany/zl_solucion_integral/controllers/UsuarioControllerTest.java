package com.mycompany.zl_solucion_integral.controllers;

import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.ResultadoOperacion;
import com.mycompany.zl_solucion_integral.models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioControllerTest {

    @TempDir
    Path tempFolder;

    private UsuarioController controller;

    @BeforeEach
    void setUp() {
        String dbUrl = "jdbc:sqlite:" + tempFolder.resolve("usuarios_test.db").toAbsolutePath();
        GestorConexion.getInstancia().reiniciarParaPruebas(dbUrl);
        new ConexionDB().inicializarBaseDeDatos();
        controller = new UsuarioController();
    }

    @AfterEach
    void tearDown() {
        GestorConexion.getInstancia().cerrar();
    }

    @Test
    void testAgregarUsuarioExitoso() {
        Usuario user = new Usuario(0, "Vendedor Uno", "3001234567", "vendedor1@test.com", "clave123", "0");
        ResultadoOperacion res = controller.agregarUsuario(user);

        assertTrue(res.esExito(), "Debe agregar el usuario exitosamente");
        assertTrue(controller.validarExistenciaUsuario("Vendedor Uno"));
        assertTrue(controller.validarExistenciaPorCorreo("vendedor1@test.com"));
    }

    @Test
    void testPrevencionDuplicadoPorNombreYCorreo() {
        Usuario u1 = new Usuario(0, "Carlos Perez", "3001112233", "carlos@test.com", "pass123", "0");
        controller.agregarUsuario(u1);

        Usuario uDuplicadoNombre = new Usuario(0, "Carlos Perez", "3009998877", "otro@test.com", "pass123", "0");
        ResultadoOperacion resNombre = controller.agregarUsuario(uDuplicadoNombre);
        assertFalse(resNombre.esExito(), "No debe permitir duplicados por nombre");

        Usuario uDuplicadoCorreo = new Usuario(0, "Otro Nombre", "3009998877", "carlos@test.com", "pass123", "0");
        ResultadoOperacion resCorreo = controller.agregarUsuario(uDuplicadoCorreo);
        assertFalse(resCorreo.esExito(), "No debe permitir duplicados por correo");
    }

    @Test
    void testValidarCredencialesAdminYRegular() {
        Usuario admin = new Usuario(0, "AdminGeneral", "3000000000", "admin@test.com", "adminPass", "1");
        Usuario vendedor = new Usuario(0, "JuanVendedor", "3001112222", "juan@test.com", "juanPass", "0");

        controller.agregarUsuario(admin);
        controller.agregarUsuario(vendedor);

        assertTrue(controller.existeAdministrador());
        assertTrue(controller.validarCredencialesAdmin("AdminGeneral", "adminPass"));
        assertFalse(controller.validarCredencialesAdmin("AdminGeneral", "claveErronea"));

        assertTrue(controller.validarCredencialesUsuarioRegular("JuanVendedor", "juanPass"));
        assertFalse(controller.validarCredencialesUsuarioRegular("JuanVendedor", "claveErronea"));
    }

    @Test
    void testValidarCredencialesPorPrimerNombreYCorreo() {
        Usuario adminCompleto = new Usuario(0, "Brandon Daza", "3009990000", "brandon@empresa.com", "miClaveSuperSegura", "1");
        controller.agregarUsuario(adminCompleto);

        // 1. Acceso por primer nombre ("Brandon")
        assertTrue(controller.validarCredencialesAdmin("Brandon", "miClaveSuperSegura"), "Debe permitir login ingresando solo el primer nombre");
        assertEquals("Brandon Daza", com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado());

        // 2. Acceso por correo electrónico ("brandon@empresa.com")
        assertTrue(controller.validarCredencialesAdmin("brandon@empresa.com", "miClaveSuperSegura"), "Debe permitir login ingresando el correo electrónico");
        assertEquals("Brandon Daza", com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado());

        // 3. Acceso por nombre completo ("Brandon Daza")
        assertTrue(controller.validarCredencialesAdmin("Brandon Daza", "miClaveSuperSegura"), "Debe permitir login ingresando el nombre completo");
        assertEquals("Brandon Daza", com.mycompany.zl_solucion_integral.models.Sesion.getUsuarioLogueado());

        // 4. Rechazo con clave errónea
        assertFalse(controller.validarCredencialesAdmin("Brandon", "claveIncorrecta"));
    }

    @Test
    void testListadoSinColumnaContraseña() {
        Usuario vendedor = new Usuario(0, "Pedro", "3004445555", "pedro@test.com", "secret", "0");
        controller.agregarUsuario(vendedor);

        JTable tabla = new JTable();
        controller.mostrarUsuariosPorRol(tabla, "0");

        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        assertEquals(5, modelo.getColumnCount(), "Debe tener exactamente 5 columnas (Id, Usuario, Email, Teléfono, Rol)");

        for (int i = 0; i < modelo.getColumnCount(); i++) {
            assertNotEquals("Contraseña", modelo.getColumnName(i), "La columna Contraseña no debe existir en el modelo");
        }
    }

    @Test
    void testModificarUsuarioConservandoContraseña() {
        Usuario u = new Usuario(0, "Maria", "3007778888", "maria@test.com", "claveOriginal", "0");
        controller.agregarUsuario(u);

        // Modificar con contraseña vacía (debe conservar claveOriginal)
        ResultadoOperacion res = controller.modificarUsuario("Maria Actualizada", "3007778889", "maria@test.com", "0", "", 1);
        assertTrue(res.esExito());

        assertTrue(controller.validarCredencialesUsuarioRegular("Maria Actualizada", "claveOriginal"));
    }

    @Test
    void testValidarDatosRecuperacionYRestablecimiento() {
        Usuario u = new Usuario(0, "Lucia", "3008889999", "lucia@test.com", "claveAntigua", "0");
        controller.agregarUsuario(u);

        // Validación correcta
        assertTrue(controller.validarDatosRecuperacion("Lucia", "3008889999"));
        assertTrue(controller.validarDatosRecuperacion("lucia@test.com", "3008889999"));

        // Validación errónea
        assertFalse(controller.validarDatosRecuperacion("Lucia", "3000000000"));
        assertFalse(controller.validarDatosRecuperacion("OtroUsuario", "3008889999"));

        // Restablecimiento de contraseña
        ResultadoOperacion res = controller.restablecerContraseña("Lucia", "nuevaClave123");
        assertTrue(res.esExito());

        // Verificar que la clave antigua ya no sirva y la nueva sí
        assertFalse(controller.validarCredencialesUsuarioRegular("Lucia", "claveAntigua"));
        assertTrue(controller.validarCredencialesUsuarioRegular("Lucia", "nuevaClave123"));
    }
}
