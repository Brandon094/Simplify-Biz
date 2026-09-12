package com.mycompany.zl_solucion_integral.views.components;

/**
 * Mensajes de validación de formularios, centralizados (principio DRY).
 *
 * Pertenece a la capa de vistas: los controladores usan sus propios mensajes
 * y aquí solo viven los textos que la UI muestra al usuario, para que todos
 * los formularios se comuniquen con la misma voz.
 */
public final class UIMessages {

    /** Título estándar para diálogos de error de validación. */
    public static final String TITULO_ERROR = "Error";

    /** Título estándar para diálogos de operación exitosa. */
    public static final String TITULO_EXITO = "Éxito";

    /** Mensaje cuando falta información en el formulario. */
    public static final String MSG_CAMPOS_OBLIGATORIOS = "Todos los campos son obligatorios";

    /** Mensaje cuando el correo no cumple el formato esperado. */
    public static final String MSG_EMAIL_INVALIDO = "Email no válido";

    /** Mensaje cuando el teléfono no cumple el formato esperado. */
    public static final String MSG_TELEFONO_INVALIDO = "El teléfono debe tener 10 dígitos";

    /** Mensaje cuando las credenciales no coinciden con ningún usuario. */
    public static final String MSG_CREDENCIALES_INVALIDAS = "Credenciales incorrectas";

    /** Texto del botón de ingreso mientras se validan las credenciales. */
    public static final String TEXTO_PROCESANDO_INGRESO = "Ingresando…";

    /** Texto del botón de registro mientras se crea la cuenta. */
    public static final String TEXTO_PROCESANDO_REGISTRO = "Registrando…";

    private UIMessages() {
        // Clase de utilidades: no instanciable.
    }
}