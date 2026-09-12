package com.mycompany.zl_solucion_integral;

import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.config.DatabaseInitializer;
import com.mycompany.zl_solucion_integral.config.GestorConexion;
import com.mycompany.zl_solucion_integral.config.SelecionRuta;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.views.ModernLoginPage;
import com.mycompany.zl_solucion_integral.views.ModernAdminRegistrationPage;
import com.mycompany.zl_solucion_integral.views.components.UIUtils;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Clase principal que inicia la aplicación.
 *
 * La clase `Main` es el punto de entrada de la aplicación. Su método `main` es
 * el que se ejecuta al iniciar el programa. Esta clase se encarga de
 * inicializar la base de datos y de mostrar el formulario de inicio de sesión
 * de la aplicación.
 *
 * @author ChopCode Solutions
 */
public class Main {
    /**
     * Método principal que inicia la aplicación.
     *
     * @param args Argumentos de la línea de comandos (no utilizados en este
     * caso).
     */
    public static void main(String[] args) {
        // Cargar la preferencia de tema guardada (oscuro por defecto si no existe).
        Boolean temaGuardado = SelecionRuta.cargarPreferenciaTema();
        ThemeConstants.setDark(temaGuardado == null || temaGuardado);

        // Inicializar Look and Feel Moderno según el tema por defecto (oscuro).
        aplicarTema();
        UIUtils.configureGlobalStyles();
        
        // Primero, se intenta inicializar la base de datos.
        if (inicializarBaseDatos()) {
            // Si la base de datos se inicializa correctamente, se inicia la aplicación.
            iniciarAplicacion();
        }
    }

    /**
     * Aplica el tema gráfico (oscuro o claro) según {@link ThemeConstants}.
     * Usa FlatLaf para que el render de nativos (tablas, scrollbars, etc.)
     * se ajuste al tema y luego re-aplica los estilos globales de la UI.
     *
     * Al cambiar el tema, las vistas se construyen de nuevo leyendo las
     * constantes de color, por lo que no es necesario actualizar componentes
     * ya creados aquí.
     */
    public static void aplicarTema() {
        if (ThemeConstants.isDark()) {
            com.formdev.flatlaf.themes.FlatMacDarkLaf darkLaf = new com.formdev.flatlaf.themes.FlatMacDarkLaf();
            darkLaf.install();
        } else {
            com.formdev.flatlaf.themes.FlatMacLightLaf lightLaf = new com.formdev.flatlaf.themes.FlatMacLightLaf();
            lightLaf.install();
        }
        UIUtils.configureGlobalStyles();
    }

    /**
     * Método para inicializar la base de datos.
     *
     * @return true si la base de datos se inicializa correctamente, false en
     * caso contrario.
     */
    private static boolean inicializarBaseDatos() {
        // Se obtiene la ruta de la base de datos protegida por defecto o la configurada previamente
        String ruta = SelecionRuta.cargarRutaBaseDatos();
        if (ruta == null || ruta.trim().isEmpty()) {
            ruta = SelecionRuta.obtenerRutaProtegidaPredeterminada();
        }

        // Guardar la ruta en config.properties si no estaba registrada
        guardarRutaEnConfig(ruta);

        try {
            GestorConexion.getInstancia().inicializar(ruta);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> GestorConexion.getInstancia().cerrar()));
            DatabaseInitializer dbInit = new DatabaseInitializer(new ConexionDB());
            dbInit.inicializarTablas();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Ocurrió un error al intentar inicializar la base de datos.\n"
                    + "Detalles técnicos: " + e.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Método para guardar la ruta seleccionada en el archivo de configuración.
     *
     * @param ruta Ruta seleccionada para la base de datos.
     */
    private static void guardarRutaEnConfig(String ruta) {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            System.out.println("Archivo de configuración no encontrado. Creando uno nuevo...");
        }

        try (FileOutputStream output = new FileOutputStream("config.properties")) {
            props.setProperty("db.path", ruta);
            props.store(output, "Configuración de la base de datos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para iniciar la aplicación.
     *
     * Este método verifica si ya existe un administrador registrado en la base
     * de datos. Si no existe, se muestra un formulario para registrar un nuevo
     * administrador. Si ya existe, se muestra el formulario de inicio de
     * sesión.
     */
    private static void iniciarAplicacion() {
        // Se crea una instancia del controlador de usuarios.
        UsuarioController usuarioCtrl = new UsuarioController();
        // Se verifica si ya existe un administrador en la base de datos.
        boolean existeAdmin = usuarioCtrl.existeAdministrador();

        if (!existeAdmin) {
            // Se muestra el formulario de registro de administrador.
            ModernAdminRegistrationPage registro = new ModernAdminRegistrationPage();
            registro.setVisible(true);
            // Si no existe un administrador, se muestra un mensaje de bienvenida y se solicita la creación de uno.
            JOptionPane.showMessageDialog(null,
                    "Bienvenido a ERP+ Business.\n\n"
                    + "Para comenzar a usar la aplicación, primero debes configurar la cuenta principal del negocio.\n\n"
                    + "Completa el siguiente formulario para crear el administrador inicial.",
                    "Configuración Inicial", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Si ya existe un administrador, se muestra el formulario de inicio de sesión.
            ModernLoginPage loginForm = new ModernLoginPage();
            loginForm.setVisible(true);
        }
    }
}
