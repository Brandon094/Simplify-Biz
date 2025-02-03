package com.mycompany.zl_solucion_integral.config;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class SelecionRuta {

    public static String selecionarRutaDB() {
        JFileChooser chooser = new JFileChooser();
        // Solo se permiten carpetas
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Seleccione la carpeta donde se creará la base de datos");

        int resultado = chooser.showOpenDialog(null);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File carpetaSeleccionada = chooser.getSelectedFile();
            // Aquí definimos el nombre del archivo de la base de datos, por ejemplo "db.sqlite"
            String ruta = carpetaSeleccionada.getAbsolutePath() + File.separator + "db.sqlite";
            return ruta;
        } else {
            // Si el usuario cancela, podemos retornar null o una ruta por defecto.
            JOptionPane.showMessageDialog(null, "No se seleccionó ninguna carpeta. Se usará la ruta por defecto.");
            return null;
        }
    }

    // Método para seleccionar una ruta para guardar un archivo
    public static String obtenerRuta(String titulo, String nombrePredeterminado, String extension) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(titulo);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File(nombrePredeterminado));

        int seleccion = fileChooser.showSaveDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            String ruta = archivoSeleccionado.getAbsolutePath();

            // Verificar y agregar la extensión si no está incluida
            if (!ruta.toLowerCase().endsWith(extension)) {
                ruta += extension;
            }
            return ruta;
        }
        return null; // Si el usuario cancela, retorna null
    }

    // Método para seleccionar un archivo para abrir
    public static String obtenerRutaAbrir(String titulo) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(titulo);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int seleccion = fileChooser.showOpenDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null; // Si el usuario cancela, retorna null
    }
}
