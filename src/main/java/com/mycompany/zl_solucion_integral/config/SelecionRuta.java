package com.mycompany.zl_solucion_integral.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
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
            // db.path es la carpeta de datos; el archivo efectivo es db.db
            return carpetaSeleccionada.getAbsolutePath();
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
    /**
     * Devuelve la ruta por defecto protegida en la carpeta de datos de la aplicación del usuario
     * (%APPDATA%\ERPPlusBusiness en Windows / ~/.config/ERPPlusBusiness en Linux/Mac).
     */
    public static String obtenerRutaProtegidaPredeterminada() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name", "").toLowerCase();
        File folder;

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isEmpty()) {
                folder = new File(appData, "ERPPlusBusiness");
            } else {
                folder = new File(userHome, "AppData/Roaming/ERPPlusBusiness");
            }
        } else if (os.contains("mac")) {
            folder = new File(userHome, "Library/Application Support/ERPPlusBusiness");
        } else {
            // Linux/Unix por defecto
            folder = new File(userHome, ".config/ERPPlusBusiness");
        }

        if (!folder.exists()) {
            folder.mkdirs();
        }
        return new File(folder, "db.db").getAbsolutePath();
    }

    // Cargar la ruta desde el archivo properties; si no existe, usar la ruta protegida por defecto.
    public static String cargarRutaBaseDatos() {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
            String rutaGuardada = props.getProperty("db.path");
            if (rutaGuardada != null && !rutaGuardada.trim().isEmpty()) {
                return rutaGuardada.trim();
            }
        } catch (IOException e) {
            // Archivo de configuración aún no existe
        }
        return obtenerRutaProtegidaPredeterminada();
    }

    /**
     * Carga la preferencia de tema (oscuro/claro) desde config.properties.
     *
     * @return true si se prefiere tema oscuro, false para tema claro, o null si
     *         no hay preferencia guardada.
     */
    public static Boolean cargarPreferenciaTema() {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
            String valor = props.getProperty("theme.dark");
            if (valor == null) {
                return null;
            }
            return Boolean.parseBoolean(valor);
        } catch (IOException e) {
            return null; // Archivo no disponible aún; se usa el valor por defecto
        }
    }

    /**
     * Guarda la preferencia de tema en config.properties conservando las demás
     * propiedades existentes.
     *
     * @param dark true para tema oscuro, false para tema claro.
     */
    public static void guardarPreferenciaTema(boolean dark) {
        Properties props = new Properties();
        // Conservar las propiedades existentes (por ejemplo db.path).
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            // Si no existe, se crea desde cero en el bloque de escritura.
        }

        try (FileOutputStream output = new FileOutputStream("config.properties")) {
            props.setProperty("theme.dark", String.valueOf(dark));
            props.store(output, "Configuración de la aplicación");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga el usuario recordado si existe la clave remember.user en config.properties.
     */
    public static String cargarUsuarioRecordado() {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
            return props.getProperty("remember.user");
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Guarda o borra el usuario recordado en config.properties.
     * Si usuario es null o vacío, remueve la clave.
     */
    public static void guardarUsuarioRecordado(String usuario) {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            // Ignorar si no existe aún
        }

        try (FileOutputStream output = new FileOutputStream("config.properties")) {
            if (usuario != null && !usuario.trim().isEmpty()) {
                props.setProperty("remember.user", usuario.trim());
            } else {
                props.remove("remember.user");
            }
            props.store(output, "Configuración de la aplicación");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga la preferencia de colapso del sidebar horizontal (contraído/expandido).
     *
     * @return true si el sidebar estaba colapsado (64px), false si estaba expandido (260px).
     */
    public static boolean cargarPreferenciaSidebarColapsado() {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
            String valor = props.getProperty("sidebar.collapsed");
            if (valor != null) {
                return Boolean.parseBoolean(valor);
            }
        } catch (IOException e) {
            // No existe archivo aún
        }
        return false;
    }

    /**
     * Guarda la preferencia del estado de colapso del sidebar en config.properties.
     *
     * @param colapsado true para contraído, false para expandido.
     */
    public static void guardarPreferenciaSidebarColapsado(boolean colapsado) {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            // Crear nuevo si no existe
        }

        try (FileOutputStream output = new FileOutputStream("config.properties")) {
            props.setProperty("sidebar.collapsed", String.valueOf(colapsado));
            props.store(output, "Configuración de la aplicación");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Evalúa si el usuario ya vio la guía interactiva de bienvenida de primer uso.
     *
     * @return true si ya fue mostrada/vista previamente, false si es la primera vez.
     */
    public static boolean cargarPrimerUsoVisto() {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
            String valor = props.getProperty("welcome.seen");
            if (valor != null) {
                return Boolean.parseBoolean(valor);
            }
        } catch (IOException e) {
            // No existe archivo aún
        }
        return false;
    }

    /**
     * Guarda el estado de la guía de bienvenida de primer uso en config.properties.
     *
     * @param visto true para no volver a mostrar automáticamente en el inicio.
     */
    public static void guardarPrimerUsoVisto(boolean visto) {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            // Crear si no existe
        }

        try (FileOutputStream output = new FileOutputStream("config.properties")) {
            props.setProperty("welcome.seen", String.valueOf(visto));
            props.store(output, "Configuración de la aplicación");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
