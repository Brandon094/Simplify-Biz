package com.mycompany.zl_solucion_integral.config;

import javax.swing.JOptionPane;

public class Validaciones {

    /**
     * Método para validar que un campo de texto no esté vacío.
     *
     * @param texto El texto a validar.
     * @return true si el texto no está vacío, false en caso contrario.
     */
    public static boolean validarNoVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Método para validar que un arreglo de campos de texto no esté vacío.
     *
     * @param textos Arreglo de textos a validar.
     * @return true si ninguno de los textos está vacío, false en caso
     * contrario.
     */
    public static boolean validarNoVacio(String... textos) {
        for (String texto : textos) {
            if (texto == null || texto.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Método para validar el formato de un correo electrónico.
     *
     * @param email El correo electrónico a validar.
     * @return true si el correo tiene un formato válido, false en caso
     * contrario.
     */
    public static boolean validarEmail(String email) {
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    /**
     * Método para validar que el rol sea un número válido (0 o 1).
     *
     * @param rol El rol a validar.
     * @return true si el rol es 0 o 1, false en caso contrario.
     */
    public static boolean validarRol(String rol) {
        try {
            int rolNumero = Integer.parseInt(rol);
            return rolNumero == 0 || rolNumero == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Método para validar que un número sea positivo.
     *
     * @param numero El número a validar.
     * @return true si el número es positivo, false en caso contrario.
     */
    public static boolean validarNumeroPositivo(int numero) {
        return numero > 0;
    }

    /**
     * Método para validar que un número decimal sea positivo.
     *
     * @param numero El número decimal a validar.
     * @return true si el número es positivo, false en caso contrario.
     */
    public static boolean validarNumeroPositivo(double numero) {
        return numero > 0;
    }

    /**
     * Método para validar que una categoría sea válida.
     *
     * @param categoria La categoría a validar.
     * @return true si la categoría es válida, false en caso contrario.
     */
    public static boolean validarCategoria(String categoria) {
        String[] categoriasValidas = {"DOTACION HOMBRE", "DOTACION DAMA", "CALZADO", "EPP", "BOTIQUINES", "SEÑALIZACION"};
        for (String valida : categoriasValidas) {
            if (valida.equalsIgnoreCase(categoria)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Método para convertir un texto a mayúsculas.
     *
     * @param texto El texto a convertir.
     * @return El texto en mayúsculas.
     */
    public static String convertirAMayusculas(String texto) {
        return texto != null ? texto.toUpperCase() : null;
    }

    /**
     * Método para validar que un teléfono tenga un formato válido (entre 7 y 10
     * dígitos).
     *
     * @param telefono El teléfono a validar.
     * @return true si el teléfono tiene un formato válido, false en caso
     * contrario.
     */
    public static boolean validarTelefono(String telefono) {
        return telefono != null && telefono.matches("\\d{7,10}");
    }

    /**
     * Método para validar que un NIT no esté vacío.
     *
     * @param nit El NIT a validar.
     * @return true si el NIT no está vacío, false en caso contrario.
     */
    public static boolean validarNIT(String nit) {
        return validarNoVacio(nit);
    }

    /**
     * Método para validar que una dirección no esté vacía.
     *
     * @param dir La dirección a validar.
     * @return true si la dirección no está vacía, false en caso contrario.
     */
    public static boolean validarDireccion(String dir) {
        return validarNoVacio(dir);
    }

    /**
     * Método para validar que se haya seleccionado un método de pago (efectivo
     * o crédito).
     *
     * @param esEfectivo true si se seleccionó efectivo.
     * @param esCredito true si se seleccionó crédito.
     * @return true si se seleccionó un método de pago, false en caso contrario.
     */
    public static boolean validarMetodoPago(boolean esEfectivo, boolean esCredito) {
        return esEfectivo || esCredito;
    }

    /**
     * Método para validar que un código de producto tenga un formato válido (al
     * menos 5 caracteres).
     *
     * @param codigo El código del producto a validar.
     * @return true si el código tiene un formato válido, false en caso
     * contrario.
     */
    public static boolean validarCodigoProducto(String codigo) {
        return codigo != null;
    }

    /**
     * Método para validar que un descuento sea un número válido (entre 0 y
     * 100).
     *
     * @param descuentoStr El descuento en formato de texto.
     * @return true si el descuento es válido, false en caso contrario.
     */
    public static boolean validarDescuento(String descuentoStr) {
        if (descuentoStr.isEmpty()) {
            return true; // Consideramos válido porque lo interpretamos como 0
        }
        try {
            double descuento = Double.parseDouble(descuentoStr);
            return descuento >= 0 && descuento <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Método para validar que una cantidad sea un número válido y positivo.
     *
     * @param cantidadStr La cantidad en formato de texto.
     * @return true si la cantidad es válida, false en caso contrario.
     */
    public static boolean validarCantidad(String cantidadStr) {
        try {
            int cantidad = Integer.parseInt(cantidadStr);
            return cantidad > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Validacion de cliente
    public static boolean validarCliente(String clienteName, String noCcCliente, String telefonoCliente, String correoCliente, String NIT, String DIR) {
        if (!validarNoVacio(clienteName)) {
            return false; // Nombre vacío
        }
        if (!validarNoVacio(noCcCliente)) {
            return false; // Cédula vacía
        }
        if (!validarNoVacio(telefonoCliente) && !validarNoVacio(correoCliente)) {
            return false; // Teléfono y correo vacíos
        }
        if (!validarNoVacio(telefonoCliente) && !validarTelefono(telefonoCliente)) {
            return false; // Teléfono inválido
        }
        if (!validarNoVacio(correoCliente) && !validarEmail(correoCliente)) {
            return false; // Correo inválido
        }
        if (!validarNoVacio(NIT)) {
            return false; // NIT vacío
        }
        if (!validarNoVacio(DIR)) {
            return false; // Dirección vacía
        }
        return true;
    }

    // Validacion para producto
    public boolean validarProducto(String codigo, String productoName) {
        if (!Validaciones.validarNoVacio(codigo) && !Validaciones.validarNoVacio(productoName)) {
            return false; // producto vacio
        }
        return true;
    }

}
