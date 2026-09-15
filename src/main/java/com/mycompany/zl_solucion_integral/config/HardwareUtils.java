package com.mycompany.zl_solucion_integral.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

/**
 * Utilidad defensiva para la generación del Hardware ID (Fingerprint)
 * único de la máquina local. Combina identificadores del procesador, sistema
 * operativo y placa base en un hash SHA-256 formateado.
 */
public class HardwareUtils {

    private static String cachedHardwareId = null;

    /**
     * Obtiene el Hardware ID único formateado de la máquina actual.
     * Retorna una cadena de 16 caracteres en bloques de 4: XXXX-XXXX-XXXX-XXXX.
     */
    public static synchronized String getHardwareId() {
        if (cachedHardwareId != null) {
            return cachedHardwareId;
        }

        StringBuilder rawId = new StringBuilder();
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH);

        try {
            if (os.contains("win")) {
                rawId.append(ejecutarComando("wmic csproduct get uuid"));
                rawId.append(ejecutarComando("wmic bios get serialnumber"));
            } else if (os.contains("mac")) {
                rawId.append(ejecutarComando("ioreg -rd1 -c IOPlatformExpertDevice"));
            } else {
                // Linux / Unix
                String machineId = leerArchivoLinux("/etc/machine-id");
                if (machineId.isEmpty()) {
                    machineId = leerArchivoLinux("/var/lib/dbus/machine-id");
                }
                if (machineId.isEmpty()) {
                    machineId = ejecutarComando("hostname");
                }
                rawId.append(machineId);
            }
        } catch (Exception e) {
            // Fallback en caso de restricciones de permisos de ejecución
            rawId.append(System.getProperty("user.name", "unknown"))
                 .append(System.getProperty("os.name", "unknown"))
                 .append(System.getProperty("os.arch", "unknown"))
                 .append(Runtime.getRuntime().availableProcessors());
        }

        // Si la lectura falló por completo, asegurar un fallback no vacío
        if (rawId.toString().trim().isEmpty()) {
            rawId.append(System.getProperty("os.arch", "x86_64")).append("-ERPPLUS-FALLBACK");
        }

        cachedHardwareId = generarHashSha256Formateado(rawId.toString());
        return cachedHardwareId;
    }

    private static String ejecutarComando(String comando) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(comando);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line.trim());
                }
            }
            process.waitFor();
        } catch (Exception ignored) {
        }
        return output.toString();
    }

    private static String leerArchivoLinux(String ruta) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(ruta);
            if (java.nio.file.Files.exists(path)) {
                return new String(java.nio.file.Files.readAllBytes(path), StandardCharsets.UTF_8).trim();
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private static String generarHashSha256Formateado(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String fullHex = hexString.toString().toUpperCase(Locale.ENGLISH);
            // Formatear los primeros 16 caracteres en bloques de 4: XXXX-XXXX-XXXX-XXXX
            return fullHex.substring(0, 4) + "-" +
                   fullHex.substring(4, 8) + "-" +
                   fullHex.substring(8, 12) + "-" +
                   fullHex.substring(12, 16);
        } catch (Exception e) {
            return "ERP1-9999-AAAA-BBBB";
        }
    }
}
