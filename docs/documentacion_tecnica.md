# Documentación Técnica & Guía de Ingeniería — ERP+ BUSINESS (v2.0.0)

> **Documentación Técnica Empresarial / Silicon Valley Startup Standard**  
> Especificación de arquitectura, patrones de diseño de software, pipeline de datos, motor de renderizado Swing 2D y suite de pruebas.

---

## 1. Visión General del Sistema & Arquitectura High-Level

**ERP+ BUSINESS** es una plataforma de software de escritorio desacoplada de alto rendimiento desarrollada en Java 25 y Swing, respaldada por un motor de persistencia relacional SQLite con registros WAL (*Write-Ahead Logging*). La arquitectura está diseñada bajo los principios **SOLID**, **DRY** (*Don't Repeat Yourself*) y **Atomic Design**.

### 1.1 Diagrama de Arquitectura de Componentes

```text
┌───────────────────────────────────────────────────────────────────────────────────────┐
│                                CAPA DE PRESENTACIÓN (Swing)                           │
│  ┌─────────────────────────────┐  ┌─────────────────────────┐  ┌───────────────────┐  │
│  │ MainTemplate (Shell Frame)  │  │ SalesPage (POS 2.0)     │  │ DashboardPage     │  │
│  └──────────────┬──────────────┘  └────────────┬────────────┘  └─────────┬─────────┘  │
│                 │                              │                         │            │
│                 ▼                              ▼                         ▼            │
│  ┌─────────────────────────────────────────────────────────────────────────────────┐  │
│  │ ATOMIC DESIGN: NeonButton | NeonPieChart | NeonBarChart | AutocompletePopup<T>  │  │
│  └──────────────────────────────────────┬──────────────────────────────────────────┘  │
└─────────────────────────────────────────┼─────────────────────────────────────────────┘
                                          │ Transfiere DTO `ResultadoOperacion`
                                          ▼
┌───────────────────────────────────────────────────────────────────────────────────────┐
│                                 CAPA DE CONTROLADORES (MVC)                           │
│  ┌──────────────────────────┐  ┌──────────────────────────┐  ┌──────────────────────┐ │
│  │ ProductoController       │  │ VentasController         │  │ CarteraController    │ │
│  └────────────┬─────────────┘  └────────────┬─────────────┘  └──────────┬───────────┘ │
└───────────────┼─────────────────────────────┼───────────────────────────┼─────────────┘
                │                             │                           │
                └─────────────────────────────┼───────────────────────────┘
                                              ▼
┌───────────────────────────────────────────────────────────────────────────────────────┐
│                            CAPA DE INFRAESTRUCTURA & DATOS                            │
│  ┌──────────────────────────┐  ┌──────────────────────────┐  ┌──────────────────────┐ │
│  │ GestorConexion (Singleton│  │ ConexionDB (Schema/DDL)  │  │ ExcelSQLiteManager   │ │
│  └────────────┬─────────────┘  └────────────┬─────────────┘  └──────────┬───────────┘ │
└───────────────┼─────────────────────────────┼───────────────────────────┼─────────────┘
                │ JDBC / SQLite WAL           │                           │
                ▼                             ▼                           ▼
┌───────────────────────────────────────────────────────────────────────────────────────┐
│                              ALMACENAMIENTO RELACIONAL                                │
│                   Windows: %APPDATA%/ERPPlusBusiness/db.db                            │
│                   Linux:   ~/.config/ERPPlusBusiness/db.db                            │
└───────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Organización de Paquetes & Módulos

```text
com.mycompany.zl_solucion_integral/
├── config/              # Infraestructura, Singleton DB, Seguridad, Validaciones y Parsers
│   ├── ConexionDB.java            # DDL, migraciones automáticas y esquema
│   ├── GestorConexion.java        # Singleton de conexión con PRAGMAs (WAL, busy_timeout)
│   ├── SelecionRuta.java          # Gestión de rutas seguras por SO y config.properties
│   ├── Seguridad.java             # Criptografía SHA-256
│   ├── Validaciones.java          # Parsers defensivos (previene NumberFormatException)
│   ├── ResultadoOperacion.java    # DTO inmutable (éxito, mensaje, datos)
│   └── ExcelSQLiteManager.java    # Importación y exportación de datos masivos
├── tools/              # Herramientas Administrativas Privadas
│   └── GeneradorLicenciaAdmin.java # Generador y firmador de licencias criptográficas RSA-2048
├── controllers/         # Reglas de negocio y transacciones (DAOs embebidos)
│   ├── ProductoController.java    # Gestión de inventario, stock crítico e inversión
│   ├── VentasController.java      # Transacciones ACID de POS y utilidad neta
│   ├── UsuarioController.java     # Autenticación, clientes y empleados
│   ├── CarteraController.java     # Gestión de cartera, recaudo de abonos y saldos
│   ├── ComprasController.java     # Transacciones ACID de abastecimiento e incremento de stock
│   └── ProveedorController.java   # Gestión de proveedores y sugerencias de autocompletado
├── models/              # Modelos de dominio POJO
│   ├── Producto.java              # Atributos SKU, precios (venta/costo), stock y categoría
│   ├── Usuario.java               # Roles, credenciales e identificación
│   ├── Venta.java                 # Transacción compuesta con snapshot de precios
│   ├── Compra.java                # Orden de compra e ingreso a almacén
│   ├── DetalleCompra.java         # Renglón de producto recibido
│   ├── Proveedor.java             # Maestro de proveedores
│   └── Sesion.java                # Contenedor de sesión activa en la JVM
├── views/               # Interfaz gráfica moderna (FlatLaf + Cyberpunk Neon)
    ├── components/
    │   ├── atoms/                 # NeonButton, NeonLineChart, NeonPieChart, NeonBarChart, RoundedPanel
    │   ├── AutocompletePopup.java # Motor genérico de autocompletado en tiempo real
    │   ├── dialogs/               # ManualUsuarioDialog, LicenciaDialog, RegistrarAbonoDialog, HistorialAbonosDialog, HistorialComprasClienteDialog, RegistrarCompraDialog
    │   ├── molecules/             # SidebarItem, SidebarSection (Acordeón colapsable neumórfico con Preferences persistence)
    │   └── organisms/             # ModernSidebar, MetricCard
    └── [Pages]                    # DashboardPage, SalesPage, ProductPage, ComprasPage, ClientsPage, CarteraPage, ReportsPage, ConfigPage, ModernLoginPage
```

---

## 3. Patrones de Diseño Avanzados

### 3.1 Conexión Compartida Singleton (`GestorConexion`)
Para evitar los bloqueos `SQLITE_BUSY` (`database is locked`), la aplicación mantiene una única conexión abierta en la memoria de la JVM. En el momento de la inicialización, se ejecutan las siguientes pragmas de rendimiento:
- `PRAGMA journal_mode=WAL;` (Write-Ahead Logging para lectura/escritura concurrente).
- `PRAGMA busy_timeout=5000;` (Espera defensiva de 5000 ms).
- `PRAGMA foreign_keys=ON;` (Enforza integridad referencial).

### 3.2 Motor Genérico de Autocompletado & Componentes Custom Table (`SalesPage.java`)
- **`AutocompletePopup<T>`**: Implementado mediante un popup desacoplado que soporta interfaces funcionales (`SearchProvider<T>`, `DisplayFormatter<T>`, `SelectionListener<T>`).
  - **Integración Dual de Clientes:** Vinculado simultáneamente a los campos `txtClientCC` (Cédula/NIT) y `txtClientName` (Nombre/Razón Social). Al escribir en cualquiera de los dos campos, consulta de forma reactiva `usuarioCtrl.buscarClientesSugeridos(query)` desplegando sugerencias flotantes.
  - **Desacoplamiento de Eventos por Foco:** La remoción del listener `focusLost` directo en `txtClientCC` evita condiciones de carrera donde la pérdida de foco cerraba la ventana emergente antes de capturar el clic de selección en la lista.
- **`CartRowActionsPanel` & `CartCellEditor`**: Renderizador y editor celda a celda en `JTable` para la columna de acciones del carrito POS. Emplea íconos vectoriales FlatSVG (`plus.svg`, `minus.svg`, `products.svg`, `trash.svg`) con desacoplamiento de eventos vía `TableCellEditor` e intercepción atómica del estado del modelo `cartItems`.

### 3.3 Formateo Compacto Inteligente de Moneda (`UIUtils.formatCompactCurrency`)
- **Estandarización DRY & Atomic Design:** Implementación centralizada en `UIUtils` para la capa de presentación Swing. Convierte montos monetarios extensos en representaciones compactas elegantes (`$100K`, `$5.4M`, `$1.2B`) evitando desbordamientos de texto y rotura de layout en tarjetas KPI (`MetricCard`) de Dashboard, Cartera y Reportes.

### 3.4 Microcopy & Guía Contextual en Registro de Administrador (`ModernAdminRegistrationPage.java`)
- **Estandarización de Helpers:** Implementación de etiquetas de ayuda permanentes mediante `createHelperLabel()` en `ModernAdminRegistrationPage`, mejorando el feedback operacional sin recargar la interfaz y respetando los tokens de `ThemeConstants`.

### 3.5 Autenticación Flexible por Primer Nombre o Correo (`UsuarioController.java` & `ModernLoginPage.java`)
- **Extracción de Primer Token SQL:** Implementación en `validarCredencialesAdmin` y `validarCredencialesUsuarioRegular` utilizando la cláusula `LOWER(SUBSTR(nombre, 1, INSTR(nombre || ' ', ' ') - 1)) = LOWER(?)` o coincidencia por `email` y `nombre` completo.
- **Transparencia en Sesión:** Tras validar la coincidencia del primer nombre o correo y verificar el hash SHA-256 de la contraseña, el controlador recupera y registra automáticamente el **Nombre Completo Oficial** del usuario en `Sesion.setUsuarioLogueado(...)`.

### 3.4 Motor de Renderizado Gráfico 2D (`Graphics2D`)
- **`NeonLineChart` & Comparativa Interperiodo**:
  - **Serie Principal & Serie de Comparación:** Soporta renderizado dual en tiempo real. La serie actual se traza con una línea continua neón violeta y área de degradado semitransparente. La serie del periodo anterior (`ventasComparativas`) se renderiza con una línea discontinua (`Stroke` punteado) en gris/cyan tenue.
  - **Badge de Crecimiento Neón Dinámico:** Si existe serie del periodo previo, el gráfico calcula de forma automática el porcentaje global de crecimiento o decrecimiento $$\Delta \% = \left( \frac{\text{Total Actual} - \text{Total Anterior}}{\text{Total Anterior}} \right) \times 100$$ y dibuja en la esquina superior derecha un badge redondeado neón verde (`+XX.X% vs per. anterior`) o neón rosa/rojo (`-XX.X% vs per. anterior`).
  - **Eje X Inteligente Adaptativo (`VentasController.obtenerEtiquetasGraficaPorPeriodo`):**
    - `Hoy`: Horas del día (`08:00`, `12:00`, `16:00`).
    - `Esta Semana` / `Este Mes`: Días y meses en español (`15 Jul`, `16 Jul`).
    - `Este Año`: Abreviaturas de meses (`Ene`, `Feb`, `Mar`, ..., `Dic`).
    - `Histórico`: Años completos (`2024`, `2025`, `2026`).
- **`NeonPieChart`**:
  - Ordena y consolida categorías agrupando del Top 6 en adelante dentro de la etiqueta `"OTROS"`.
  - **Cálculo Polar & Hover:** Mapea el ángulo cartesiano `(Math.atan2)` respecto al centro `(cx, cy)` y detecta colisión. Desplaza la rebanada seleccionada 7px hacia afuera y traza una curva de Bézier neón (`Path2D`) apuntando al ítem activo de la leyenda.
- **`NeonBarChart`**:
  - Grafica la comparativa macro (**Ventas Totales**, **Utilidad Neta**, **Inversión en Bodega**).
  - Normalización de escala dinámica `maxVal * 1.15` y formateo compacto en eje Y (`$4.3M`, `$500K`).

---

## 4. Flujos Transaccionales & Fórmulas Financieras

### 4.1 Transacción Atómica POS (`VentasController.guardarVenta`)

```sql
BEGIN TRANSACTION;

-- 1. Insertar encabezado de venta con estado de pago
INSERT INTO ventas (cliente, cc_cliente, vendedor, fecha, total, metodo_pago, pago_confirmado)
VALUES (?, ?, ?, ?, ?, ?, ?);

-- 2. Insertar detalles reteniendo snapshot de costo y % de descuento
INSERT INTO detalles_venta (venta_id, producto, cantidad, codigo, precio, precio_costo, total, descuento)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

-- 3. Descontar inventario de forma defensiva
UPDATE productos SET cantidad = cantidad - ? 
WHERE codigo = ? AND cantidad >= ?;

COMMIT; -- O ROLLBACK en caso de excepción
```

- Si `metodoPago` es `"Crédito"`, `pago_confirmado` se establece en `'deudor'`. En `"Efectivo"` o `"Transferencia"`, en `'pagado'`.

### 4.2 Métricas del Resumen Ejecutivo en Centro de Reportes & Exportaciones

El **Centro de Reportes** (`ReportsPage.java`) incluye un módulo de inteligencia financiera en tiempo real que reevalúa automáticamente las ventas filtradas por fecha o búsqueda histórica:

1. **Total Facturado ($)**: Suma de la columna `Precio Total` de todas las ventas seleccionadas.
2. **Costo de Mercancía COGS ($)**: `SELECT SUM(cantidad * precio_costo) FROM detalles_venta WHERE venta_id IN (...)`.
3. **Utilidad Neta ($) & Margen (%)**: `Ganancia = Total Facturado - COGS`; `Margen % = (Ganancia / Total Facturado) * 100.0`.
4. **Desglose de Métodos de Pago**: Acumulado y proporción para **Efectivo**, **Transferencia** y **Crédito**.
5. **Exportación a Excel con Apache POI (`VentasController.exportarDatosTablaAExcel`)**:
   - Aplica estilos corporativos con banner de título ("ERP+ BUSINESS - Reporte Oficial de Ventas").
   - Cabeceras con relleno azul oscuro (`#1E293B`) y texto en negrita.
   - Formato numérico `$#,##0.00` en celdas de moneda y fila final de **Gran Total**.
6. **Exportación a PDF / Impresión Nativa**:
   - Invocación nativa a `JTable.print(JTable.PrintMode.FIT_WIDTH, header, footer)` que permite previsualizar e imprimir o generar un PDF vectorizado.

### 4.3 Módulo de Cartera & Recaudo de Abonos (`CarteraController.registrarAbono`)

```sql
BEGIN TRANSACTION;

-- 1. Consultar total de venta y total acumulado abonado previamente
SELECT v.total, COALESCE(SUM(a.monto), 0.0) AS abonado
FROM ventas v LEFT JOIN abonos_cartera a ON v.id = a.venta_id
WHERE v.id = ? GROUP BY v.id;

-- 2. Insertar nuevo registro en abonos_cartera
INSERT INTO abonos_cartera (venta_id, monto, fecha, metodo_pago, observacion)
VALUES (?, ?, date('now'), ?, ?);

-- 3. Si (saldoPendiente - monto) <= 0.01, saldar deuda automáticamente
UPDATE ventas SET pago_confirmado = 'pagado' WHERE id = ?;

COMMIT; -- O ROLLBACK si el monto supera el saldo pendiente o falla la conexión
```

### 4.4 Motor Dual de Importación Excel/CSV (`ExcelSQLiteManager`)

El motor de importación masiva soporta dos modos de operación parametrizados mediante la enumeración `ModoImportacion`:

1. **`ModoImportacion.CATALOGO`**:
   - Invocado desde la vista de **Inventario/Productos** (`ImportarProductosDialog`).
   - Mapea dinámicamente las columnas de la plantilla Excel (Código/SKU, Nombre, Precio Venta, Precio Costo, Stock/Cantidad, Categoría).
   - Inserta nuevos productos o actualiza los existentes en la tabla `productos` usando estrategia Upsert por SKU (incrementando el stock de forma atómica).
2. **`ModoImportacion.ABASTECIMIENTO`**:
   - Invocado desde la vista de **Registro de Compras** (`RegistrarCompraDialog`).
   - Exige la validación previa de los campos obligatorios **Proveedor** y **Número de Factura**.
   - Procesa las filas mediante `ExcelSQLiteManager.leerItemsParaAbastecimiento(...)`, retornando una estructura `ResultadoLecturaAbastecimiento` que incluye ítems válidos, advertencias y productos omitidos por costo/cantidad inválida.
   - Precarga los ítems directamente en la tabla interactiva de la factura de compra (`listaCarrito`), permitiendo la revisión, ajuste y adición visual por parte del usuario antes de presionar "Confirmar Compra", lo cual procesa formalmente el abastecimiento en base de datos (incremento de bodega y actualización de costo de compra).

---

## 5. Suite de Pruebas Automatizadas (JUnit 5)

La aplicación cuenta con **45 pruebas unitarias e integrales** que ejecutan contra bases de datos en memoria o aisladas en directorio temporal (`@TempDir`), garantizando que la suite sea **reproducible, libre de efectos secundarios y no altere la base de datos de producción**.

```bash
# Ejecución oficial de tests
mvn test
```

| Suite de Prueba | Capa | Cantidad | Descripción y Aspectos Evaluados |
| :--- | :--- | :---: | :--- |
| `VentasControllerTest` | Controller | 4 | Transacciones atómicas de POS, actualización de stock, desgloses por método de pago (Efectivo/Transferencia/Crédito) y utilidad por periodo. |
| `ComprasControllerTest` | Controller | 4 | Orden de compra atómica, incremento de stock entrante, validaciones de factura de abastecimiento y recalculación de costo unitario. |
| `ProductoControllerTest` | Controller | 7 | Creación/actualización de repuestos, cálculo de inversión total, desglose de inversión (Abastecimiento vs Catálogo Directo), productos en stock crítico y búsqueda por SKU/categoría. |
| `UsuarioControllerTest` | Controller | 5 | Autenticación con hash SHA-256, cambio de contraseña, roles (Admin/Vendedor/Cliente) y registro de clientes. |
| `CarteraControllerTest` | Controller | 6 | Recaudo de abonos parciales, saldo pendiente de cuentas por cobrar y liquidación automática de facturas a crédito. |
| `ProveedorControllerTest` | Controller | 4 | Alta, edición, eliminación y búsqueda reactiva de proveedores sugeridos por NIT o nombre. |
| `ConexionDBTest` | Config | 3 | Verificación de esquema DDL, migraciones de columna seguras (`migrarColumnaSegura`) e inicialización SQLite. |
| `ExcelSQLiteManagerTest` | Config | 2 | Generación de plantilla modelo `.xlsx`, lectura de cabeceras, importación en modo Catálogo y lectura en modo Abastecimiento con `ResultadoLecturaAbastecimiento`. |
| Total | Complete | **45** | **Suite 100% verde (BUILD SUCCESS)** |

---

## 6. Construcción, Pruebas y Despliegue

```bash
# Compilar fuentes Java
mvn clean compile

# Ejecutar suite completa de 45 pruebas unitarias (JUnit 5)
mvn test

# Empaquetar artefacto JAR ejecutable (Shaded Fat-JAR)
mvn clean package

# Ejecución en producción
java -jar dist/ERP-Plus-Business-2.0.0.jar
```
