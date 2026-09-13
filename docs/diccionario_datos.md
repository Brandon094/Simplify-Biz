# Diccionario de Datos — ERP+ Business

> Descripción funcional detallada de cada tabla y campo de la base de datos. Para las definiciones DDL, consulta el [Esquema de Base de Datos](esquema_bd.md).

---

## 1. Tabla `usuarios`

Almacena todos los usuarios del sistema: administradores, vendedores/empleados y clientes.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | Identificador único autoincremental. Clave primaria. |
| `nombre` | `TEXT` | No | Nombre completo del usuario. Se usa para login (comparación case-insensitive). |
| `telefono` | `TEXT` | No | Número de contacto (7–10 dígitos). |
| `email` | `TEXT` | No | Correo electrónico. Formato validado con regex. Se verifica duplicados al registrar. |
| `rol` | `INTEGER` | No | Rol funcional del usuario (ver tabla de roles abajo). |
| `contraseña` | `TEXT` | No | Contraseña encriptada mediante `Seguridad.encriptarContraseña()`. **Nunca se muestra** en interfaces de consulta. |

### Valores de Rol

| Código | Rol | Descripción |
| :--- | :--- | :--- |
| `0` | Vendedor / Empleado | Acceso limitado a Ventas y Productos. Se gestiona desde la pantalla de Empleados. |
| `1` | Administrador | Acceso completo a todos los módulos. Se crea en el registro inicial o manualmente. |
| `2` | Cliente | Datos de referencia para ventas. Se crea automáticamente cuando un cliente proporciona sus datos durante una venta. No tiene acceso al sistema. |

---

## 2. Tabla `productos`

Catálogo de productos disponibles para la venta con control de inventario.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | Identificador único autoincremental. Clave primaria. |
| `producto` | `TEXT` | No | Nombre descriptivo del producto. |
| `precio` | `REAL` | No | Precio unitario de venta al público. |
| `cantidad` | `INTEGER` | No | Stock disponible actualmente. Se descuenta al confirmar ventas y se suma al registrar productos con código existente. |
| `codigo` | `TEXT` | No | Código SKU o identificador único del producto. Se usa para búsquedas y control de duplicados. |
| `categoria` | `TEXT` | Sí | Categoría del producto. Se vincula dinámicamente con la tabla `categorias`. Si es `NULL` o vacío, se muestra como "Sin Categoría" en los gráficos. |

---

## 3. Tabla `ventas`

Encabezado de cada transacción de venta registrada.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | Identificador único de la venta. Clave primaria autoincremental. |
| `cliente` | `TEXT` | No | Nombre del cliente al momento de la venta. Se almacena como snapshot textual (no es FK). En venta mostrador: `CONSUMIDOR FINAL`. |
| `cc_cliente` | `TEXT` | No | Cédula de ciudadanía, NIT o identificación del cliente. En venta mostrador: `N/A`. |
| `vendedor` | `TEXT` | No | Nombre del usuario que realizó la venta (tomado de la sesión activa). |
| `fecha` | `DATE` | No | Fecha de la transacción. Almacenada como `java.sql.Date`. |
| `total` | `REAL` | No | Monto total de la venta (suma de todos los detalles con descuentos aplicados). |
| `metodo_pago` | `TEXT` | No | Método de pago seleccionado: `Efectivo`, `Crédito`, `Transferencia` u otro. |
| `pago_confirmado` | `TEXT` | Sí | Estado del pago. Para ventas a crédito se marca como `deudor`. Cadena vacía para pagos confirmados al momento. |

---

## 4. Tabla `detalles_venta`

Líneas de detalle de cada venta. Relación N:1 con `ventas`.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | Identificador único del detalle. Clave primaria autoincremental. |
| `venta_id` | `INTEGER` | No | **Clave foránea** → `ventas.id`. Identifica la venta a la que pertenece este detalle. |
| `producto` | `TEXT` | No | Nombre del producto al momento de la venta (snapshot textual). |
| `cantidad` | `INTEGER` | No | Unidades vendidas de este producto. |
| `codigo` | `TEXT` | No | Código/SKU del producto al momento de la venta. |
| `precio` | `REAL` | No | Precio unitario al que se vendió el producto. |
| `total` | `REAL` | No | Subtotal del detalle: `precio × cantidad`. |

---

## 5. Tabla `configuracion`

Configuración interna de la aplicación. Contiene un único registro (`id = 1`).

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | Clave primaria. Siempre `1` (registro único). |
| `ultimoNumeroCotizacion` | `TEXT` | Sí | Último número de cotización generado. Formato: `YYYYMMDD-XXX` (ej: `20260912-001`). Se incrementa automáticamente y se reinicia al cambiar la fecha. |

---

## 6. Convenciones y Reglas de Negocio

### 6.1 Datos Históricos

Las tablas `ventas` y `detalles_venta` almacenan datos como **texto plano** (snapshot):
- Los nombres de cliente, vendedor, producto y código se guardan tal como eran al momento de la transacción.
- No dependen de claves foráneas hacia `usuarios` ni `productos`.
- Esto garantiza que el historial de ventas se preserva aunque se modifiquen o eliminen usuarios y productos posteriormente.

### 6.2 Venta Mostrador

- Cliente: `CONSUMIDOR FINAL`
- CC: `N/A`
- **No** se crea un registro ficticio en la tabla `usuarios`.

### 6.3 Seguridad de Contraseñas

- La columna `contraseña` **nunca** se incluye en consultas de listado (constante `COLUMNAS_LISTADO = "id, nombre, email, telefono, rol"`).
- Las vistas no muestran la columna de contraseña en ninguna tabla.
- Las contraseñas se almacenan encriptadas y se validan mediante `Seguridad.validarContraseña()`.

### 6.4 Integridad de Stock

- El stock se descuenta dentro de una transacción SQL atómica al confirmar una venta.
- La condición `WHERE cantidad >= ?` en el `UPDATE` previene que el stock sea negativo.
- Si falla cualquier actualización, toda la transacción se revierte con `rollback`.

### 6.5 Categorías Dinámicas

- Tabla `categorias` (`id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT UNIQUE NOT NULL`).
- El sistema aprende y registra automáticamente en SQLite cualquier nueva categoría ingresada en el inventario.
- Semilla inicial: `DOTACION HOMBRE`, `DOTACION DAMA`, `CALZADO`, `EPP`, `BOTIQUINES`, `SEÑALIZACION`, `HERRAMIENTAS`, `MATERIALES`, `ELECTRÓNICA`, `BEBIDAS`, `LIMPIEZA`, `OTROS`.

### 6.6 Cotizaciones

- El número de cotización sigue el formato `YYYYMMDD-XXX`.
- Se incrementa automáticamente con cada cotización generada.
- Se reinicia a `001` al cambiar la fecha del sistema.
