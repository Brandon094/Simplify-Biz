package com.mycompany.zl_solucion_integral.tools;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

/**
 * Herramienta administrativa para la generación de licencias comerciales RSA-2048.
 * SOLO PARA USO DEL DESARROLLADOR/ADMINISTRADOR.
 */
public class GeneradorLicenciaAdmin {

    // Clave privada RSA-2048 en formato PKCS#8 DER Base64 (Mantenida segura por el Administrador)
    private static final String CLAVE_PRIVADA_RSA_BASE64 =
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC3q+39JK0sd/tE" +
            "iRDw757yJfkXRk4DE1zAAflM+w9e/odzeHZH0VeJCXVtflS5dg7FGfrpKMG0xJZX" +
            "wo1xZskMDytRlFRKM2rHa6Mc/3sntTdO0shVTKpfVPovc5OsUOv8InS05uOI1jdB" +
            "sbGL2NXATR7guT7gxZPfMakTrZwJ9C3Y3WSUey107DTYto2NpnY8fIIBvu2WE9OP" +
            "3BCS/F5Q4TUm3slQ6wZZ63QaFCayvNpYke5k/2w3uvg8pvPlk3SUsFljo4DWAZUv" +
            "rlodrTgH6YSqHJFPUQMt33t7Qx/KQYAZwYS06g5Z+huqKA8NScwhtKN105IGSPnP" +
            "WB+6F3UzAgMBAAECggEATGTH62rmVmEfEv35nGTmFOyDYtFcoD3c7YXEpeakmYu/" +
            "VeFMPQnO4VIcU+rgHXWDpipsqK7JhsDfkWf7waeRRqFGkLtjpasmJgTYhraiiD0A" +
            "4JmeNpBZKqajGrp1OQ49YRUJurZv/BmxSPM6GCMboMzS44BZLU8wdnxmyScECEUf" +
            "6w8j+w/tXnRmkxi4W/tiSXWAtglojP75s3Id96LWGl33ur6RLHPOLcWntJ8tCpe4" +
            "Dg+0fQFMrDY56y2mus7nfhxmVtXEZLFsckqz9vvQseIah1wjC1lMYyLCEjkEm+lC" +
            "F1Z4LzOQT+k8QWaBMZBBFSXTJ6CuqlzE3PgY4i8f1QKBgQD5SpV8HchwBN2B028A" +
            "2fX2qyy4H8c6msyd5D2H/S/BNynhqvd/+PMyuR+qN9SiCqzeFf1DmrLTdRRKHYGP" +
            "jfpHm0ktnQ3ZBhFbpaRUDY+HW9xvV/PTeUKl/fX4vTfBrTO/bNzN1021VLinVCMj" +
            "ZEzzuDdYPmVvlwaMqRAKh4wVzwKBgQC8nUdW6Ukas6fG39nw6afiO3EeH+nI42Zd" +
            "RBaenr3sSv0BhGGaX7Ysd4bAXOJhaEz31+ngWS4Gh2QpxZxksw6Np9L9yHPX/v3p" +
            "YocF5GQS039hJOKzPOeVgk2RBvDe881xoV6QorzEbWrGmHBJ6vzGB2xu4HJyen9C" +
            "E9xCdk8nXQKBgQCQnSWuhJB7+zaMU503PzmiSJZ+kWC/rIE6rubK9qM3UMro8Ib3" +
            "I/Hg4OdvlUeyYML7aoIrL5jBdk+41vkLz+76jyiaX1tjJFP5eFOltuAmQ7HSnZEC" +
            "gnuZU4PXMd0ga1tzlrFYb6fSoz0jRHnF1C+XyqvAcyHg5BmWi5SUpyEqnQKBgFRU" +
            "pe7wAyWTKfK7Z4BVmCCInzzu2+Jb+gq9RLfLFfOuW4zwGYuiO5uKHY+od/dBYRh3" +
            "SzRp6zA0HM6ochJ8FIe1f5rEozbx5akRynkR8sSQF6XhjhLBqnGofvHW/p9QtoyV" +
            "r+hPOsNAgLsmsR37mOUZsJuqcsFIj9mFc7ermhxpAoGBAM5+NEwKs6wj99xWu7ap" +
            "/jm3VfnplYvuNh3nQNMD7jBXkEnxA/q3wCXx/93MCtLaieVR4fHWfe5Jt0rWd3NH" +
            "zLR7f2i7H1vMc3fRipANEcLrZNCfoBcorpW/cz5Ut+dihOYYpAMUZIMWoa86kEUH" +
            "xzVBxHQPGYBABDNd26ttYYGk";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=================================================");
        System.out.println("   GENERADOR DE LICENCIAS COMERCIALES - ERP+     ");
        System.out.println("=================================================");

        try {
            System.out.print("Nombre del Cliente / Empresa: ");
            String cliente = scanner.nextLine().trim();

            System.out.print("Hardware ID del Cliente (ej. 5F8B-9E12-4C3D-00E5): ");
            String hwId = scanner.nextLine().trim();

            System.out.print("Fecha de Expiración (YYYY-MM-DD) [Presiona Enter para 'De Por Vida' 2099-12-31]: ");
            String fechaExp = scanner.nextLine().trim();
            if (fechaExp.isEmpty()) {
                fechaExp = "2099-12-31";
            }

            String tokenLicencia = generarTokenLicencia(cliente, hwId, fechaExp);

            System.out.println("\n-------------------------------------------------");
            System.out.println("¡LICENCIA GENERADA CON ÉXITO!");
            System.out.println("-------------------------------------------------");
            System.out.println("Cliente:     " + cliente);
            System.out.println("Hardware ID: " + hwId);
            System.out.println("Expiración:  " + fechaExp);
            System.out.println("\nCLAVE / TOKEN DE ACTIVACIÓN (Entregar al cliente):");
            System.out.println(tokenLicencia);
            System.out.println("-------------------------------------------------\n");

        } catch (Exception e) {
            System.err.println("Error al generar la licencia: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String generarTokenLicencia(String cliente, String hwId, String fechaExp) throws Exception {
        String payloadJson = String.format("{\"cliente\":\"%s\",\"hardwareId\":\"%s\",\"expiracion\":\"%s\"}",
                cliente, hwId, fechaExp);

        byte[] privateKeyBytes = Base64.getDecoder().decode(CLAVE_PRIVADA_RSA_BASE64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(keySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(payloadJson.getBytes(StandardCharsets.UTF_8));
        byte[] firmaBytes = signature.sign();

        String firmaBase64 = Base64.getEncoder().encodeToString(firmaBytes);
        String rawToken = payloadJson + "|" + firmaBase64;

        return Base64.getEncoder().encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));
    }
}
