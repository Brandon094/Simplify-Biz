package com.mycompany.zl_solucion_integral.config;

import java.awt.Color;
import javax.swing.JFrame;

/**
 *
 * @author ChopCode Solutions
 */
public class UtilVentanas {

    // Método que configura una ventana en pantalla completa
    public static void aplicarPantallaCompleta(JFrame ventana) {
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Cambiar el color de fondo
        ventana.getContentPane().setBackground(Color.GRAY);
    }
}
