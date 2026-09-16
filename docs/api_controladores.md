# Referencia de API & Controladores — ERP+ Business (v2.0.0)

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
| `obtenerDesgloseInversionInventario` | — | `double[]` | Retorna `[0] Total Invertido`, `[1] Inversión Abastecimiento (Facturas)` y `[2] Inversión Carga Directa / Catálogo`. |
| `obtenerCantidadStockCritico` | `int limite` | `int` | Retorna el conteo de ítems con existencias `≤ limite`. |
| `obtenerProductosStockCritico` | `int limite` | `List<Producto>` | Obtiene la lista de ítems con existencias `≤ limite` ordenados ascendentemente por cantidad (prioriza agotados en 0 unds). |
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
| `mostrarVentasPorCliente` | `JTable t, String cc, String nom` | `void` | Consulta y carga las facturas asociadas a un cliente por Cédula/NIT o Nombre ordenadas cronológicamente. |
| `mostrarDetallesVenta` | `JTable t, int ventaId` | `void` | Carga los productos y subtotales comprados de una factura específica en la tabla de detalle. |
| `obtenerUtilidadTotal` | — | `double` | Calcula la ganancia neta real ($): `SELECT SUM(total - (precio_costo * cantidad)) FROM detalles_venta`. |
| `obtenerVentasTotalesPorPeriodo` | `String periodo` | `double` | Calcula ventas totales por periodo ("Hoy", "Últimos 7 Días", "Este Mes", "Histórico Total"). |
| `obtenerUtilidadTotalPorPeriodo` | `String periodo` | `double` | Calcula la utilidad neta total por periodo. |
| `obtenerDesgloseMetodosPagoPorPeriodo` | `String periodo` | `Map<String, Double>` | Retorna acumulados por método de pago (`Efectivo`, `Transferencia`, `Crédito`). |
| `obtenerDesgloseUtilidadNetaPorPeriodo` | `String periodo` | `double[]` | Retorna `[0] Total Facturado`, `[1] Costo Mercancía (COGS)` y `[2] Utilidad Neta Real`. |
| `obtenerCogsPorVentaIds` | `List<Integer> ventaIds` | `double` | Calcula el Costo de Mercancía Vendida (COGS) para un listado de IDs de ventas visibles/filtrados. |
| `obtenerVentasUltimos7Dias` | — | `List<Double>` | Totales acumulados diarios de los últimos 7 días. |
| `exportarDatosTablaAExcel` | `JTable table, String path` | `void` | Exporta la tabla visible a formato Microsoft Excel `.xlsx` vía Apache POI con diseño ejecutivo corporativo, banner de título, cabeceras en azul oscuro `#1E293B`, formato `$#,##0.00` y fila de Gran Total. |


---

## 4. `CarteraController`

Gestión de cuentas por cobrar, saldos deudores y registro atómico de abonos.

### Métodos Principales

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `obtenerVentasEnCartera` | `JTable tabla, String filtro` | `void` | Carga las ventas a crédito calculando `Total Venta`, `Total Abonado` y `Saldo Pendiente`. Soporta filtro por cliente o cédula. |
| `registrarAbono` | `int ventaId, double monto, String metodo, String obs` | `ResultadoOperacion` | **Transacción atómica:** inserta el recaudo en `abonos_cartera`. Si `saldoPendiente <= 0.01`, actualiza el estado de la venta a `'pagado'`. |
| `obtenerHistorialAbonos` | `int ventaId` | `List<Object[]>` | Obtiene el historial completo de pagos parciales aplicados a una venta a crédito. |
| `obtenerResumenCartera` | — | `double[]` | Calcula los KPIs globales del módulo: `[0]` Cartera Pendiente ($), `[1]` Recaudado Mes ($), `[2]` Deudores Activos (count). |

---

## 5. `ComprasController`

Abastecimiento de bodega, registro de facturas de compra e incremento atómico de stock.

### Métodos Principales

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `guardarEntradaCompra` | `Compra c, List<DetalleCompra> det` | `ResultadoOperacion` | **Transacción ACID atómica:** guarda compra, detalles, incrementa existencias de productos existentes (`cantidad = cantidad + ?`) y **crea de forma transparente** productos nuevos que no existían previamente en el catálogo en una sola operación. |
| `mostrarHistorialCompras` | `JTable tabla, String filtro` | `void` | Carga el listado de entradas de almacén aplicando formato contable `UIUtils.applyTableStyling`. |
| `mostrarDetallesCompra` | `JTable tabla, int compraId` | `void` | Carga los renglones de productos recibidos en una orden de compra específica. |
| `obtenerResumenCompras` | — | `double[]` | Retorna los KPIs globales de compras: `[0]` Total Invertido ($), `[1]` Entradas Recibidas (#), `[2]` Proveedores Atendidos (#). |

---

## 6. `ProveedorController`

Gestión de proveedores e integración con el motor de autocompletado del módulo de compras.

### Métodos Principales

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `guardarOActualizarProveedor` | `Proveedor p` | `ResultadoOperacion` | Inserta o actualiza un proveedor verificando unicidad por NIT. |
| `mostrarProveedores` | `JTable tabla` | `void` | Carga la lista completa de proveedores registrados. |
| `buscarProveedoresSugeridos` | `String query` | `List<Proveedor>` | Consulta en tiempo real (`LIKE %query%`) para el componente `AutocompletePopup`. |

---

## 7. `ExcelSQLiteManager`

Motor de procesamiento de hojas de cálculo (Excel `.xlsx` / `.xls` y CSV) parametrizable por la enumeración `ModoImportacion` (`CATALOGO` y `ABASTECIMIENTO`).

### Enumeraciones y DTOs
- **`ModoImportacion`**: `CATALOGO` (importación directa a base de datos de productos) | `ABASTECIMIENTO` (lectura de ítems para precargar en tabla de orden de compra).
- **`ResultadoLecturaAbastecimiento`**: Encapsula la lista de `DetalleCompra` válidos generados a partir del Excel, lista de advertencias de validación (`warnings`) y contador de filas procesadas/omitidas.

### Métodos Principales

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `leerCabeceras` | `File archivo` | `List<String>` | Extrae la primera fila del archivo Excel/CSV como nombres de columna para poblar los selectores de mapeo visual. |
| `leerVistaPrevia` | `File archivo, Map<String, Integer> mapeo, int maxFilas` | `List<Object[]>` | Lee las primeras 5 filas aplicando las transformaciones de mapeo para mostrar la tabla de vista previa en vivo. |
| `importarConMapeo` | `File archivo, Map<String, Integer> mapeo` | `ResultadoOperacion` | Procesa el archivo completo en una transacción SQLite en modo Catálogo. Realiza **Upsert** (actualiza precio, costo y suma existencias si el SKU existe, o crea el producto si es nuevo). |
| `leerItemsParaAbastecimiento` | `File archivo, Map<String, Integer> mapeo` | `ResultadoLecturaAbastecimiento` | Procesa el archivo Excel en modo Abastecimiento, transformando las filas en objetos `DetalleCompra` validados (costo y cantidad) para precargar la tabla de la orden de compra antes de confirmar la factura. |
| `generarPlantillaModelo` | `File destino` | `ResultadoOperacion` | Genera y guarda una plantilla `.xlsx` con las columnas estándar esperadas por el sistema (`codigo_barras`, `nombre`, `precio`, `costo`, `stock`, `categoria`). |

---

## 8. `Validaciones` & `ResultadoOperacion`

### Parsers Defensivos
- `Validaciones.parseEntero(String)`: Retorna `Integer` o `null` sin lanzar excepción.
- `Validaciones.parseDecimalNoNegativo(String)`: Acepta coma o punto decimal y retorna `Double` o `null`.
- `Validaciones.parseFecha(String)`: Valida formato estricto `dd/MM/yyyy`.
- `Validaciones.limpiarFormatoMoneda(String)`: Limpia caracteres de moneda como `$`, `,`, espacios mediante regex `[^0-9,.-]` para parseo numérico seguro.

### DTO `ResultadoOperacion`
- `ResultadoOperacion.ok(mensaje)` / `ResultadoOperacion.error(mensaje)`
- Encapsula el estado de la operación permitiendo que Swing renderice alertas con `UIUtils.showSuccess` o `UIUtils.showError`.

