# API de Controladores — ERP+ Business

> Referencia completa de los métodos públicos de cada controlador. Los controladores no invocan componentes Swing y retornan `ResultadoOperacion` para desacoplar la lógica de negocio de la presentación.

---

## 1. `ProductoController`

Gestión de productos: CRUD, búsqueda, stock y estadísticas.

### Métodos de Escritura

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `agregarOActualizarProductoSiExiste` | `Producto producto` | `ResultadoOperacion` | Si el código ya existe, suma la cantidad al stock. Si no, inserta un nuevo producto. |
| `modificarProducto` | `Producto producto` | `ResultadoOperacion` | Actualiza todos los campos del producto por ID. Verifica código duplicado con otro producto. |
| `eliminarProducto` | `int idProducto` | `ResultadoOperacion` | Elimina el producto por ID. Error si `id = -1`. |
| `eliminarCantidadProducto` | `int idProducto, int cantidadAEliminar` | `ResultadoOperacion` | Resta stock. Si llega a cero, elimina el producto. Error si cantidad > stock actual. |
| `actualizarCantidadProducto` | `String codigoProducto, int nuevaCantidad` | `void` | Actualiza el stock directamente por código. Lanza `RuntimeException` si falla. |

### Métodos de Lectura

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `mostrarProductos` | `JTable tablaProductos` | `void` | Carga todos los productos en la tabla con columnas: Id, Producto, Precio, Cantidad, Código, Categoría. |
| `mostrarProductosPorCategoria` | `JTable tabla, String categoria` | `void` | Filtra productos por categoría. `"Todas"` muestra todos. |
| `buscarProductoPorCodigo` | `String codigoProducto` | `Producto` | Busca por código exacto. Retorna `null` si no existe. |
| `buscarProductoPorNombre` | `String nombreProducto` | `Producto` | Busca por nombre exacto. Retorna `null` si no existe. |
| `obtenerProductoPorId` | `int id` | `Producto` | Obtiene un producto por ID. Retorna `null` si no existe. |
| `obtenerIdProductoSeleccionado` | `JTable tabla` | `int` | Retorna el ID de la fila seleccionada. `-1` si no hay selección. |
| `contarRegistros` | `String categoria` | `int` | Cuenta productos por categoría. `"Todas"` para el total. |
| `obtenerDistribucionCategorias` | — | `Map<String, Double>` | Mapa categoría → cantidad de productos. `null`/vacío → `"Sin Categoría"`. |
| `obtenerCantidadStockCritico` | `int limite` | `int` | Cuenta productos con stock ≤ límite. |

---

## 2. `UsuarioController`

Gestión de usuarios: CRUD, autenticación y validación.

### Métodos de Escritura

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `agregarUsuario` | `Usuario usuario` | `ResultadoOperacion` | Registra un nuevo usuario. Verifica duplicados por nombre y correo. Encripta la contraseña. |
| `modificarUsuario` | `String nombre, telefono, email, rol, contraseña, int idUsuario` | `ResultadoOperacion` | Actualiza los datos del usuario. Si `contraseña` está vacía o nula, conserva la existente. |
| `eliminarUsuario` | `int idUsuario` | `ResultadoOperacion` | Elimina un usuario por ID. Error si `id = -1`. |

### Métodos de Lectura

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `mostrarUsuarios` | `JTable tablaUsuarios` | `void` | Carga todos los usuarios (sin contraseña) con columnas: Id, Usuario, Email, # Tel, Rol. |
| `mostrarUsuariosPorRol` | `JTable tablaUsuarios, String rolSeleccionado` | `void` | Filtra usuarios por rol. `"Todas"` muestra todos. Excluye columna contraseña. |
| `obtenerIdUsuarioSeleccionado` | `JTable tabla` | `int` | Retorna el ID de la fila seleccionada. `-1` si no hay selección. |

### Métodos de Autenticación y Recuperación

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `validarCredencialesAdmin` | `String usuario, String contraseña` | `boolean` | Valida credenciales para rol `1` (admin). Comparación case-insensitive del nombre. |
| `validarCredencialesUsuarioRegular` | `String usuario, String contraseña` | `boolean` | Valida credenciales para roles != `1`. |
| `validarDatosRecuperacion` | `String usuarioOEmail, String telefono` | `boolean` | Verifica que coincida el usuario o correo con el teléfono registrado. |
| `restablecerContraseña` | `String usuarioOEmail, String nuevaContraseña` | `ResultadoOperacion` | Encripta y actualiza la nueva contraseña del usuario. |
| `existeAdministrador` | — | `boolean` | Verifica si existe al menos un usuario con rol `1`. |
| `validarExistenciaUsuario` | `String nombreUsuario` | `boolean` | Verifica si existe un usuario con ese nombre. |
| `validarExistenciaPorCorreo` | `String email` | `boolean` | Verifica si existe un usuario con ese correo. |

---

## 3. `VentasController`

Transacciones de venta, reportes, exportación y cotizaciones.

### Constructores

| Constructor | Descripción |
| :--- | :--- |
| `VentasController()` | Constructor vacío. Para operaciones de consulta que no requieren contexto de sesión. |
| `VentasController(Usuario cliente, Sesion sesion)` | Para operaciones que requieren datos del cliente y la sesión activa (cotizaciones, modificaciones). |

### Métodos de Escritura

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `guardarVenta` | `Venta venta, List<Producto> productosVendidos, JTable tablaVentas` | `ResultadoOperacion` | **Transacción atómica:** inserta venta + detalles, descuenta stock. Rollback automático si falla. |
| `modificarVenta` | `Venta venta, int idVenta, JTable tablaVentas` | `ResultadoOperacion` | Restaura stock anterior y aplica nuevo stock. Transacción atómica. |
| `actualizarPagoConfirmado` | `int ventaId, String pagoConfirmado` | `ResultadoOperacion` | Actualiza el estado de pago de una venta. |
| `guardarNumeroCotizacionEnBaseDeDatos` | `String numeroCotizacion` | `void` | Actualiza el número de cotización en la tabla `configuracion`. |

### Métodos de Lectura

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `MostrarVentas` | `JTable tablaVentas` | `void` | Carga todas las ventas con detalles (JOIN). 12 columnas. |
| `mostrarFechasDefinidas` | `JTable tablaVentas, String fechaInicio, String fechaFin` | `ResultadoOperacion` | Filtra ventas por rango de fechas `dd/MM/yyyy`. |
| `mostrarVentasPorDia` | `JTable tablaVentas, String fecha` | `ResultadoOperacion` | Filtra ventas de un día específico. |
| `obtenerIdVentaSeleccionado` | `JTable tabla` | `int` | ID de la fila seleccionada. `-1` si no hay selección. |
| `obtenerVentasUltimos7Dias` | — | `List<Double>` | Totales diarios de los últimos 7 días (rellena con 0.0 si hay menos de 7 días). |
| `obtenerVentasTotales` | — | `double` | Suma total de todas las ventas. |
| `contarRegistros` | `String filtro` | `int` | Cantidad total de ventas registradas. |
| `obtenerUltimasVentas` | `int limite` | `Object[][]` | Últimas N ventas para el dashboard. |
| `obtenerYActualizarNumeroCotizacion` | — | `String` | Genera el siguiente número de cotización `YYYYMMDD-XXX` y lo actualiza en BD. |

### Métodos de Exportación

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `exportarDatosTablaAExcel` | `JTable tabla, String rutaExcel` | `void` (throws IOException) | Exporta los datos visibles de una tabla a un archivo `.xlsx`. |
| `generarArchivoCotizacionConPlantilla` | `String rutaPlantilla, String rutaArchivo, String numeroCotizacion, List<Venta> ventasCotizadas` | `ResultadoOperacion` | Genera un archivo Excel de cotización basado en una plantilla. |

### Métodos Auxiliares

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `esFormatoFechaValido` | `String fecha` | `boolean` | Valida formato `dd/MM/yyyy`. Delegado a `Validaciones.parseFecha()`. |

---

## 4. `Validaciones` (Utilidad)

Clase de utilidad con métodos estáticos de validación y parsing seguro.

### Parsers Seguros

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `parseEntero` | `String valor` | `Integer` | Parsea un entero. `null` si no es número o está vacío. |
| `parseEnteroPositivo` | `String valor` | `Integer` | Parsea un entero > 0. `null` si es inválido. |
| `parseDecimalNoNegativo` | `String valor` | `Double` | Parsea un decimal ≥ 0. Acepta coma como separador. `null` si es inválido. |
| `parseFecha` | `String fecha` | `Date` | Parsea fecha `dd/MM/yyyy` estricta. `null` si es inválida. |

### Validadores

| Método | Descripción |
| :--- | :--- |
| `validarNoVacio(String...)` | Verifica que ninguno de los textos esté vacío o nulo. |
| `validarEmail(String)` | Valida formato de correo electrónico con regex. |
| `validarTelefono(String)` | Verifica 7–10 dígitos. |
| `validarRol(String)` | Verifica que sea `0` o `1`. |
| `validarCategoria(String)` | Verifica contra categorías predefinidas. |
| `validarDescuento(String)` | Verifica rango 0–100. Vacío se interpreta como 0. |
| `validarCantidad(String)` | Verifica entero positivo. |
| `validarMetodoPago(boolean, boolean)` | Verifica que al menos un método esté seleccionado. |

---

## 5. `ResultadoOperacion` (DTO)

Objeto inmutable que encapsula el resultado de una operación de negocio.

```java
// Creación
ResultadoOperacion.ok("Producto guardado exitosamente");
ResultadoOperacion.error("Error al conectar con la base de datos");

// Uso
resultado.esExito()    // boolean
resultado.getMensaje() // String
```

Los mensajes se obtienen de las constantes de `UIMessages` para consistencia.
