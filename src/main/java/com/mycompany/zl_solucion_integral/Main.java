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

    public static void main(String[] args) {
        if (inicializarBaseDatos()) {
            iniciarAplicacion();
        }
    }

    private static boolean inicializarBaseDatos() {
        ConexionDB conexion = new ConexionDB();
        try {
            DatabaseInitializer dbInit = new DatabaseInitializer(conexion);
            dbInit.inicializarTablas();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al inicializar la base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static void iniciarAplicacion() {
        UsuarioController usuarioCtrl = new UsuarioController();
        // Se asume que existeAdministrador() devuelve true si ya existe un administrador.
        boolean existeAdmin = usuarioCtrl.existeAdministrador();

        if (!existeAdmin) { // Si no existe administrador, mostrar el formulario de registro.
            JOptionPane.showMessageDialog(null,
                    "Bienvenido a Simplify Biz.\n\n" +
                    "Para comenzar a usar la aplicación, primero debes configurar una cuenta de administrador.\n\n" +
                    "Por favor, completa el siguiente formulario con tus datos.",
                    "Configuración Inicial", JOptionPane.INFORMATION_MESSAGE);

            // Mostrar la ventana de registro de administrador.
            FormRegistroAdmin registro = new FormRegistroAdmin();
            registro.setVisible(true);
        } else {
            // Si ya existe un administrador, mostrar la ventana de inicio de sesión.
            FormLogIn loginForm = new FormLogIn();
            loginForm.setVisible(true);
        }
    }
}
