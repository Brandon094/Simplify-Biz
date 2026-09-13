# Referencia de API & Controladores — ERP+ Business (v1.3.0)

> **Especificación Técnica de Métodos de Lógica de Negocio y Data Access Object (DAO)**  
> Todos los controladores ejecutan operaciones desacopladas de Swing y retornan `ResultadoOperacion` inmutable.

---

## 1. `ProductoController`

Capa de acceso a datos y reglas de negocio para el catálogo de inventario.

### Métodos de Escritura

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `agregarOActualizarProductoSiExiste` | `Producto p` | `ResultadoOperacion` | Si el SKU existe, incrementa el stock (`UPDATE`). Si no, realiza `INSERT` y persiste la categoría. |
| `modificarProducto` | `Producto p` | `ResultadoOperacion` | Actualiza todos los campos por `id`. Valida colisión de SKU con otros registros. |
| `eliminarProducto` | `int id` | `ResultadoOperacion` | Elimina físicamente el registro por ID. |
| `eliminarCantidadProducto` | `int id, int cant` | `ResultadoOperacion` | Resta existencias. Si el stock resultante es 0, elimina el registro. |
| `guardarCategoriaSiNoExiste` | `String cat` | `void` | Inserta dinámicamente una nueva categoría en la tabla `categorias`. |

### Métodos de Lectura & Analítica

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `mostrarProductos` | `JTable tabla` | `void` | Carga el catálogo completo aplicando `UIUtils.applyTableStyling`. |
| `buscarProductosSugeridos` | `String query` | `List<Producto>` | Consulta optimizada (`LIKE %query%`) para el autocompletado en tiempo real. |
| `obtenerInversionTotalInventario` | — | `double` | Calcula la inversión total a costo: `SELECT SUM(precio_costo * cantidad) FROM productos`. |
| `obtenerCantidadStockCritico` | `int limite` | `int` | Retorna el conteo de ítems con existencias `≤ limite`. |
| `obtenerDistribucionCategorias` | — | `Map<String, Double>` | Mapeo de categorías y volumen total de unidades para el gráfico donut. |
| `obtenerCategorias` | — | `List<String>` | Obtiene el listado de categorías almacenadas en la base de datos. |

---

## 2. `UsuarioController`

Gestión multirol de acceso (Administradores, Vendedores, Clientes) y seguridad.

### Métodos de Escritura & Seguridad

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `agregarUsuario` | `Usuario u` | `ResultadoOperacion` | Registra usuario verificando email/usuario único y aplicando hash SHA-256 a la clave. |
| `modificarUsuario` | `..., String pass, int id` | `ResultadoOperacion` | Actualiza datos. Si `pass` está vacía/nula, preserva la contraseña actual. |
| `validarCredencialesAdmin` | `String user, pass` | `boolean` | Autentica administradores (Rol `1`) mediante hash SHA-256. |
| `restablecerContraseña` | `String email, newPass` | `ResultadoOperacion` | Encripta y actualiza la clave en proceso de recuperación. |

### Métodos de Búsqueda & Autocompletado

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `buscarClientePorCC` | `String cc` | `Usuario` | Busca un cliente por Cédula/NIT en `usuarios` y en historial de `ventas`. |
| `buscarClientesSugeridos` | `String query` | `List<Usuario>` | Consulta unificada `UNION` para el motor de autocompletado del POS. |

---

## 3. `VentasController`

Transacciones comerciales, historial, reportes y cotizaciones.

### Métodos Principales

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `guardarVenta` | `Venta v, List<Producto> cart, JTable t` | `ResultadoOperacion` | **Transacción ACID atómica:** inserta venta, detalles con snapshot de costo y descuenta stock en SQLite. |
| `obtenerUtilidadTotal` | — | `double` | Calcula la ganancia neta real ($): `SELECT SUM(total - (precio_costo * cantidad)) FROM detalles_venta`. |
| `obtenerVentasUltimos7Dias` | — | `List<Double>` | Totales acumulados diarios de los últimos 7 días. |
| `exportarDatosTablaAExcel` | `JTable table, String path` | `void` | Exporta la tabla visible a formato Microsoft Excel `.xlsx` vía Apache POI. |

---

## 4. `Validaciones` & `ResultadoOperacion`

### Parsers Defensivos
- `Validaciones.parseEntero(String)`: Retorna `Integer` o `null` sin lanzar excepción.
- `Validaciones.parseDecimalNoNegativo(String)`: Acepta coma o punto decimal y retorna `Double` o `null`.
- `Validaciones.parseFecha(String)`: Valida formato estricto `dd/MM/yyyy`.

### DTO `ResultadoOperacion`
- `ResultadoOperacion.ok(mensaje)` / `ResultadoOperacion.error(mensaje)`
- Encapsula el estado de la operación permitiendo que Swing renderice alertas con `UIUtils.showSuccess` o `UIUtils.showError`.
