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

    // ---- Mensajes de usuarios ----
    /** El nombre de usuario ya está ocupado. */
    public static final String MSG_USUARIO_DUPLICADO_NOMBRE = "El usuario ya existe con este nombre.";
    /** El correo ya está registrado. */
    public static final String MSG_USUARIO_DUPLICADO_CORREO = "El usuario ya existe con este correo.";
    /** Duplicado detectado por restricción de BD (correo o teléfono). */
    public static final String MSG_USUARIO_DUPLICADO_CONTACTO = "El usuario ya existe con este correo o teléfono.";
    /** Registro de usuario correcto. */
    public static final String MSG_USUARIO_REGISTRADO = "Usuario registrado exitosamente";
    /** No se pudo registrar el usuario. */
    public static final String MSG_USUARIO_NO_REGISTRADO = "No se pudo registrar el usuario";
    /** Modificación de usuario correcta. */
    public static final String MSG_USUARIO_MODIFICADO = "Usuario modificado exitosamente";
    /** No se pudo modificar el usuario. */
    public static final String MSG_USUARIO_NO_MODIFICADO = "No se pudo modificar el usuario";
    /** Eliminación de usuario correcta. */
    public static final String MSG_USUARIO_ELIMINADO = "Usuario eliminado exitosamente";
    /** No se pudo eliminar el usuario. */
    public static final String MSG_USUARIO_NO_ELIMINADO = "No se pudo eliminar el usuario";
    /** Falta seleccionar un usuario en la tabla. */
    public static final String MSG_SELECCIONE_USUARIO = "Seleccione un usuario";
    /** El administrador inicial fue registrado correctamente. */
    public static final String MSG_ADMIN_REGISTRADO = "Administrador registrado con éxito";
    /** No se pudo completar el registro del administrador. */
    public static final String MSG_ERROR_REGISTRO = "Error al registrar";

    // ---- Mensajes de productos ----
    /** Código de producto duplicado. */
    public static final String MSG_PRODUCTO_CODIGO_DUPLICADO = "El código del producto ya está registrado en otro producto.";
    /** Producto guardado correctamente. */
    public static final String MSG_PRODUCTO_GUARDADO = "Producto guardado exitosamente.";
    /** No se pudo guardar el producto. */
    public static final String MSG_PRODUCTO_NO_GUARDADO = "No se pudo guardar el producto";
    /** Producto modificado correctamente. */
    public static final String MSG_PRODUCTO_MODIFICADO = "Producto modificado exitosamente.";
    /** No se pudo modificar el producto. */
    public static final String MSG_PRODUCTO_NO_MODIFICADO = "No se pudo modificar el producto";
    /** Producto eliminado correctamente. */
    public static final String MSG_PRODUCTO_ELIMINADO = "Producto eliminado exitosamente";
    /** No se pudo eliminar el producto. */
    public static final String MSG_PRODUCTO_NO_ELIMINADO = "No se pudo eliminar el producto";
    /** Falta seleccionar un producto en la tabla. */
    public static final String MSG_SELECCIONE_PRODUCTO = "Debe seleccionar un producto de la tabla";
    /** Stock actualizado correctamente. */
    public static final String MSG_STOCK_ACTUALIZADO = "Stock actualizado exitosamente.";
    /** Se intentó descontar más stock del disponible. */
    public static final String MSG_STOCK_INSUFICIENTE = "No se puede eliminar más cantidad de la disponible en stock.";
    /** Stock insuficiente para completar una venta. */
    public static final String MSG_STOCK_VENTA_INSUFICIENTE = "No hay stock suficiente para completar la venta.";
    /** Error genérico de persistencia, sin detalle técnico. */
    public static final String MSG_ERROR_BD = "No se pudo completar la operación. Inténtelo de nuevo.";
    /** Cantidad no numérica o no positiva. */
    public static final String MSG_CANTIDAD_INVALIDA = "La cantidad debe ser un número entero mayor que cero.";
    /** Precio inválido. */
    public static final String MSG_PRECIO_INVALIDO = "El precio debe ser un número mayor o igual a cero.";
    /** Descuento fuera de rango. */
    public static final String MSG_DESCUENTO_INVALIDO = "El descuento debe ser un número entre 0 y 100.";

    // ---- Mensajes de ventas ----
    /** Venta guardada correctamente. */
    public static final String MSG_VENTA_GUARDADA = "Venta guardada y stock actualizado exitosamente.";
    /** Venta modificada correctamente. */
    public static final String MSG_VENTA_MODIFICADA = "Venta modificada y stock actualizado exitosamente.";
    /** Estado de pago actualizado correctamente. */
    public static final String MSG_PAGO_ACTUALIZADO = "El estado de pago fue actualizado correctamente.";
    /** No se encontró la venta indicada. */
    public static final String MSG_VENTA_NO_ENCONTRADA = "No se encontró una venta con el ID especificado.";

    // ---- Mensajes de éxito genéricos ----
    /** Exportación a Excel correcta. */
    public static final String MSG_EXCEL_GENERADO = "Archivo Excel generado con éxito: ";
    /** Plantilla seleccionada inválida. */
    public static final String MSG_PLANTILLA_INVALIDA = "La plantilla seleccionada no es un archivo Excel válido.";

    // ---- Mensajes de formato/validación ----
    /** Formato de fecha esperado incorrecto. */
    public static final String MSG_FECHA_FORMATO_INVALIDO = "La fecha ingresada no tiene un formato válido (dd/MM/yyyy).";
    /** Los campos numéricos deben ser válidos. */
    public static final String MSG_NUMERO_INVALIDO = "Precio y Cantidad deben ser numéricos.";

    private UIMessages() {
        // Clase de utilidades: no instanciable.
    }
}