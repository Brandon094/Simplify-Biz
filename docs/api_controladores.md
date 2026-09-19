# Referencia de API & Controladores — ERP+ Business (v2.1.0)

> **Especificación Técnica de Métodos de Lógica de Negocio y Data Access Object (DAO)**  
> Todos los controladores ejecutan operaciones desacopladas de Swing y retornan el DTO inmutable `ResultadoOperacion` o estructuras POJO.

---

## 1. `VentasController` ([VentasController.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/controllers/VentasController.java))

Transacciones comerciales del punto de venta (POS), reporte de utilidad neta, comparativas de desempeño interperiodo y exportación multiformato.

### Métodos de Escritura (POS Transaccional)

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `guardarVenta` | `Venta v, List<Producto> productos, JTable t` | `ResultadoOperacion` | **Transacción ACID atómica:** Valida existencias en bodega, inserta la venta en `ventas`, inserta los renglones en `detalles_venta` reteniendo snapshot de costo y descuento, y resta atómicamente el stock con la cláusula guardiana `WHERE codigo = ? AND cantidad >= ?`. |
| `mostrarVentasPorCliente` | `JTable tabla, String ccCliente, String nombre` | `void` | Consulta y puebla la tabla con las facturas asociadas a un cliente especifico ordenadas por fecha. |
| `mostrarDetallesVenta` | `JTable tabla, int ventaId` | `void` | Carga los renglones de productos y subtotales comprados en una factura específica. |
| `exportarDatosTablaAExcel` | `JTable tabla, File archivoDestino` | `ResultadoOperacion` | Exporta la tabla activa a un libro Microsoft Excel `.xlsx` vía Apache POI con encabezado ejecutivo, paleta `#1E293B`, formato `$#,##0.00` y fila de Gran Total. |

### Métodos de Analítica & Dashboard BI

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `obtenerTotalVentasPorPeriodo` | `String periodo` | `double` | Calcula la suma bruta facturada filtrada por periodo (`Hoy`, `Esta Semana`, `Este Mes`, `Este Año`, `Histórico`). |
| `obtenerUtilidadPorPeriodo` | `String periodo` | `double` | Calcula la ganancia neta real deduciendo el COGS: `SUM(d.total - (d.precio_costo * d.cantidad))`. |
| `obtenerDesgloseMetodosPagoPorPeriodo` | `String periodo` | `Map<String, Double>` | Acumula la facturación segregada por método de pago (`Efectivo`, `Transferencia`, `Crédito`). |
| `obtenerDesgloseUtilidadNetaPorPeriodo` | `String periodo` | `double[]` | Retorna el vector de 3 posiciones: `[0] Total Facturado`, `[1] Costo Mercancía (COGS)` y `[2] Utilidad Neta Real`. |
| `obtenerDatosGraficaVentasComparativas` | `String periodo` | `Map<String, List<Double>>` | Genera un mapa con dos series síncronas (`"actual"` y `"anterior"`) para el trazado comparativo en `NeonLineChart`. |
| `obtenerEtiquetasGraficaPorPeriodo` | `String periodo` | `List<String>` | Genera dinámicamente las etiquetas adaptativas para el Eje X (Horas, Días/Meses en español, Años). |
| `obtenerCogsPorVentaIds` | `List<Integer> ventaIds` | `double` | Calcula el Costo de Mercancía Vendida (COGS) acumulado para un grupo arbitrario de IDs de ventas. |

---

## 2. `ProductoController` ([ProductoController.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/controllers/ProductoController.java))

Capa de acceso a datos y reglas de negocio para el catálogo de inventario y valoración de bodega.

### Métodos de Escritura

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `agregarOActualizarProductoSiExiste` | `Producto p` | `ResultadoOperacion` | Si el SKU existe, incrementa las existencias (`UPDATE cantidad = cantidad + ?`). Si no, realiza `INSERT` del nuevo producto y persiste la categoría. |
| `modificarProducto` | `Producto p` | `ResultadoOperacion` | Actualiza atributos comprobando previamente que no colisione el SKU con otros registros (`SELECT COUNT(*) WHERE codigo = ? AND id != ?`). |
| `eliminarProducto` | `int idProducto` | `ResultadoOperacion` | Elimina físicamente el registro por su ID primario. |
| `eliminarCantidadProducto` | `int id, int cantidadRestar` | `ResultadoOperacion` | Resta existencias. Si el stock resultante es $\le 0$, elimina el producto. |
| `guardarCategoriaSiNoExiste` | `String categoria` | `void` | Registra dinámicamente una nueva categoría en la tabla `categorias`. |

### Métodos de Lectura & Analítica

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `cargarProductosEnTabla` | `JTable tabla, String filtroNombre` | `void` | Puebla la tabla de inventario filtrando por coincidencia parcial en nombre o SKU. |
| `buscarProductosSugeridos` | `String query` | `List<Producto>` | Consulta reactiva (`LIKE %query%`) para el autocompletado del POS. |
| `obtenerInversionTotalBodega` | — | `double` | Valoración del inventario a precio de costo: $\mathbf{V_{inv}} = \sum (precio\_costo \times cantidad)$. |
| `obtenerInversionTotalAbastecimiento` | — | `double` | Acumulado histórico ingresado a través de facturas formales de abastecimiento a proveedores. |
| `obtenerCantidadProductosStockCritico` | — | `int` | Retorna el total de ítems con existencias $\le 5$ unidades. |
| `obtenerProductosStockCritico` | `int limite` | `List<Producto>` | Obtiene la lista de ítems con existencias $\le limite$ ordenados ascendentemente por cantidad. |
| `obtenerDistribucionCategorias` | — | `Map<String, Double>` | Agrupa existencias por categoría para el gráfico donut `NeonPieChart`. |
| `obtenerCategorias` | — | `List<String>` | Obtiene el listado completo de categorías registradas. |

---

## 3. `CarteraController` ([CarteraController.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/controllers/CarteraController.java))

Gobernanza financiera del módulo de cuentas por cobrar, seguimiento a deudores y recaudo de abonos.

### Métodos Principales

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `registrarAbono` | `int ventaId, double monto, String metodo, String obs` | `ResultadoOperacion` | **Transacción atómica:** Registra el pago en `abonos_cartera`. Si el saldo remanente es $\le \$0.01$, liquida automáticamente la venta actualizando `pago_confirmado = 'pagado'`. |
| `obtenerCuentasPorCobrar` | `String filtro` | `List<Map<String, Object>>` | Recupera el listado de facturas a crédito pendientes indicando cliente, cédula, fecha, total venta, acumulado abonado y saldo pendiente. |
| `obtenerHistorialAbonosVenta` | `int ventaId` | `List<Map<String, Object>>` | Obtiene la bitácora cronológica de pagos parciales aplicados a una venta a crédito. |
| `obtenerTotalCarteraPendiente` | — | `double` | Suma global del saldo pendiente por cobrar en todo el sistema. |
| `obtenerTotalRecaudadoMes` | — | `double` | Suma acumulada de abonos recibidos durante el mes actual. |

---

## 4. `ComprasController` ([ComprasController.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/controllers/ComprasController.java))

Abastecimiento de bodega, registro de facturas de compra e incremento atómico de stock.

### Métodos Principales

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `registrarCompra` | `Compra c, List<DetalleCompra> det` | `ResultadoOperacion` | **Transacción ACID atómica:** Guarda la orden en `compras`, inserta renglones en `detalles_compra`, incrementa existencias de productos existentes (`cantidad = cantidad + ?`) y crea automáticamente productos nuevos. |
| `mostrarHistorialCompras` | `JTable tabla, String filtro` | `void` | Carga el listado de entradas de almacén aplicando formato contable `UIUtils.applyTableStyling`. |
| `mostrarDetallesCompra` | `JTable tabla, int compraId` | `void` | Carga los renglones de productos recibidos en una orden de compra específica. |

---

## 5. `UsuarioController` ([UsuarioController.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/controllers/UsuarioController.java))

Gestión multirol de acceso (Administradores, Vendedores, Clientes), autenticación flexible y seguridad.

### Métodos Principales

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `validarCredencialesAdmin` | `String user, String pass` | `ResultadoOperacion` | Autentica administradores (Rol `1`). Emplea la función SQL `SUBSTR(nombre, 1, INSTR(nombre || ' ', ' ') - 1)` para comparar el primer nombre o coincidencia por email, verificando el hash SHA-256. |
| `validarCredencialesUsuarioRegular` | `String user, String pass` | `ResultadoOperacion` | Valida el acceso para empleados/vendedores regulares. |
| `registrarCliente` | `Usuario cliente` | `ResultadoOperacion` | Registra un nuevo cliente comprobando la no duplicidad de la Cédula/NIT. |
| `buscarClientesSugeridos` | `String query` | `List<Usuario>` | Consulta unificada para el motor de autocompletado del POS en tiempo real (`AutocompletePopup`). |

---

## 6. `ProveedorController` ([ProveedorController.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/controllers/ProveedorController.java))

Gestión del directorio corporativo de proveedores e integración con el módulo de compras.

### Métodos Principales

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `guardarProveedor` | `Proveedor p` | `ResultadoOperacion` | Inserta o actualiza un proveedor verificando unicidad por NIT. |
| `cargarProveedoresEnTabla` | `JTable tabla` | `void` | Carga la lista completa de proveedores registrados en la base de datos. |
| `buscarProveedoresSugeridos` | `String query` | `List<Proveedor>` | Consulta en tiempo real (`LIKE %query%`) para el desplegable emergente de abastecimiento. |

---

## 7. `ExcelSQLiteManager` ([ExcelSQLiteManager.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/config/ExcelSQLiteManager.java))

Motor de procesamiento masivo de hojas de cálculo (Excel `.xlsx` / `.xls` y CSV) parametrizable por la enumeración `ModoImportacion`.

### Enumeraciones y DTOs
- **`ModoImportacion`**: `CATALOGO` (importación directa a catálogo de productos) | `ABASTECIMIENTO` (lectura de ítems para precargar en orden de compra).
- **`ResultadoLecturaAbastecimiento`**: Encapsula la lista de `DetalleCompra` válidos, lista de advertencias (`warnings`) y conteo de filas procesadas/omitidas.

### Métodos Principales

| Método | Parámetros | Retorno | Descripción |
| :--- | :--- | :--- | :--- |
| `leerCabeceras` | `File archivo` | `List<String>` | Extrae la primera fila del archivo Excel/CSV como nombres de columna para los selectores de mapeo visual. |
| `leerVistaPrevia` | `File archivo, Map<String, Integer> mapeo, int maxFilas` | `List<Object[]>` | Lee las primeras 5 filas aplicando las transformaciones de mapeo para mostrar la vista previa en vivo. |
| `importarConMapeo` | `File archivo, Map<String, Integer> mapeo` | `ResultadoOperacion` | Procesa el archivo completo en una transacción SQLite en modo Catálogo. Realiza **Upsert** (actualiza precios y suma existencias si el SKU existe). |
| `leerItemsParaAbastecimiento` | `File archivo, Map<String, Integer> mapeo` | `ResultadoLecturaAbastecimiento` | Procesa el archivo Excel en modo Abastecimiento, transformando las filas en objetos `DetalleCompra` validados para precargar la tabla de compra. |
| `generarPlantillaModelo` | `File destino` | `ResultadoOperacion` | Genera y guarda una plantilla `.xlsx` con las columnas estándar esperadas (`codigo_barras`, `nombre`, `precio`, `costo`, `stock`, `categoria`). |

---

## 8. `Validaciones` & `ResultadoOperacion`

### Parsers Defensivos
- `Validaciones.parseEntero(String)`: Retorna `Integer` o `null` sin lanzar excepciones `NumberFormatException`.
- `Validaciones.parseDecimalNoNegativo(String)`: Acepta coma o punto decimal y retorna `Double` o `null`.
- `Validaciones.parseFecha(String)`: Valida formato estricto `dd/MM/yyyy`.
- `Validaciones.limpiarFormatoMoneda(String)`: Limpia caracteres de moneda (`$`, `,`, espacios) mediante regex `[^0-9,.-]` para parseo numérico seguro.

### DTO `ResultadoOperacion`
- `ResultadoOperacion.ok(mensaje)` / `ResultadoOperacion.error(mensaje)`
- Encapsula el estado de la operación permitiendo que Swing renderice alertas uniformes mediante `UIUtils.showSuccess` o `UIUtils.showError`.

