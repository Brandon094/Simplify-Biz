package com.mycompany.zl_solucion_integral.config;

// @author Dazac
public class DatabaseInitializer {

    // Instancia de la clase de conexion
    private final ConexionDB conexion;

    // Constructor
    public DatabaseInitializer(ConexionDB conexion) {
        this.conexion = conexion;
    }

    // Metodo que crea todas las tablas necesarias
    public void inicializarTablas() {
        conexion.inicializarBaseDeDatos();
    }
}
