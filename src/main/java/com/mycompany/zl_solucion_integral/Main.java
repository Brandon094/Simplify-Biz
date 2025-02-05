package com.mycompany.zl_solucion_integral;

import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.config.DatabaseInitializer;
import com.mycompany.zl_solucion_integral.config.SelecionRuta;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.views.FormLogIn;
import com.mycompany.zl_solucion_integral.views.FormRegistroAdmin;
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
 * @author Dazac
 */
public class Main {
    /**
     * Método principal que inicia la aplicación.
     *
     * @param args Argumentos de la línea de comandos (no utilizados en este
     * caso).
     */
    public static void main(String[] args) {
        // Primero, se intenta inicializar la base de datos.
        if (inicializarBaseDatos()) {
            // Si la base de datos se inicializa correctamente, se inicia la aplicación.
            iniciarAplicacion();
        }
    }

    /**
     * Método para inicializar la base de datos.
     *
     * @return true si la base de datos se inicializa correctamente, false en
     * caso contrario.
     */
    private static boolean inicializarBaseDatos() {
        // Instancia de SelecionRuta para manejar la configuración de la ruta
        SelecionRuta rutaDB = new SelecionRuta();        
        // Se lee el archivo de configuración para obtener la ruta de la base de datos
        String ruta = rutaDB.cargarRutaBaseDatos();

        if (ruta == null) {
            // Si no se encuentra la ruta, le pedimos al usuario que seleccione una
            ruta = rutaDB.selecionarRutaDB(); // Se llama al método para seleccionar la ruta de la base de datos
            if (ruta == null) {
                JOptionPane.showMessageDialog(null,
                        "No se seleccionó una ruta válida. La aplicación no puede continuar.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false; // No se puede continuar si no se selecciona una ruta
            }

            // Si el usuario selecciona una ruta, la guardamos en el archivo de configuración
            guardarRutaEnConfig(ruta);
        }

        // Se crea una instancia de la conexión a la base de datos usando la ruta leída.
        ConexionDB conexion = new ConexionDB(ruta);
        try {
            // Se inicializa la base de datos y se crean las tablas necesarias.
            DatabaseInitializer dbInit = new DatabaseInitializer(conexion);
            dbInit.inicializarTablas();
            return true; // La inicialización fue exitosa.
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
            FormRegistroAdmin registro = new FormRegistroAdmin();
            registro.setVisible(true);
            // Si no existe un administrador, se muestra un mensaje de bienvenida y se solicita la creación de uno.
            JOptionPane.showMessageDialog(null,
                    "Bienvenido a Simplify Biz.\n\n"
                    + "Para comenzar a usar la aplicación, primero debes configurar una cuenta de administrador.\n\n"
                    + "Por favor, completa el siguiente formulario con tus datos.",
                    "Configuración Inicial", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Si ya existe un administrador, se muestra el formulario de inicio de sesión.
            FormLogIn loginForm = new FormLogIn();
            loginForm.setVisible(true);
        }
    }
}
