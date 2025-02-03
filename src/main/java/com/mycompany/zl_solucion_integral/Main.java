package com.mycompany.zl_solucion_integral;

import com.mycompany.zl_solucion_integral.config.ConexionDB;
import com.mycompany.zl_solucion_integral.config.DatabaseInitializer;
import com.mycompany.zl_solucion_integral.controllers.UsuarioController;
import com.mycompany.zl_solucion_integral.views.FormLogIn;
import com.mycompany.zl_solucion_integral.views.FormRegistroAdmin;
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
     * @param args Argumentos de la línea de comandos (no utilizados en este caso).
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
     * @return true si la base de datos se inicializa correctamente, false en caso contrario.
     */
    private static boolean inicializarBaseDatos() {
        // Se crea una instancia de la conexión a la base de datos.
        ConexionDB conexion = new ConexionDB();
        try {
            // Se inicializa la base de datos y se crean las tablas necesarias.
            DatabaseInitializer dbInit = new DatabaseInitializer(conexion);
            dbInit.inicializarTablas();
            return true; // La inicialización fue exitosa.
        } catch (Exception e) {
            // Si ocurre un error, se muestra un mensaje de error y se imprime la traza.
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al inicializar la base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false; // La inicialización falló.
        }
    }

    /**
     * Método para iniciar la aplicación.
     *
     * Este método verifica si ya existe un administrador registrado en la base de datos.
     * Si no existe, se muestra un formulario para registrar un nuevo administrador.
     * Si ya existe, se muestra el formulario de inicio de sesión.
     */
    private static void iniciarAplicacion() {
        // Se crea una instancia del controlador de usuarios.
        UsuarioController usuarioCtrl = new UsuarioController();
        // Se verifica si ya existe un administrador en la base de datos.
        boolean existeAdmin = usuarioCtrl.existeAdministrador();

        if (!existeAdmin) {
            // Si no existe un administrador, se muestra un mensaje de bienvenida y se solicita la creación de uno.
            JOptionPane.showMessageDialog(null,
                    "Bienvenido a Simplify Biz.\n\n" +
                    "Para comenzar a usar la aplicación, primero debes configurar una cuenta de administrador.\n\n" +
                    "Por favor, completa el siguiente formulario con tus datos.",
                    "Configuración Inicial", JOptionPane.INFORMATION_MESSAGE);

            // Se muestra el formulario de registro de administrador.
            FormRegistroAdmin registro = new FormRegistroAdmin();
            registro.setVisible(true);
        } else {
            // Si ya existe un administrador, se muestra el formulario de inicio de sesión.
            FormLogIn loginForm = new FormLogIn();
            loginForm.setVisible(true);
        }
    }
}