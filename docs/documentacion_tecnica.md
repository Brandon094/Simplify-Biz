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

### 3.2 Estructura Atomic Design & Componentes de Interfaz (`views`)

La interfaz gráfica (`com.mycompany.zl_solucion_integral.views`) está construida siguiendo la metodología **Atomic Design**, garantizando el reuso de código (DRY) y desacoplamiento visual:

#### Átomos (`components/atoms`)
- **[NeonButton.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/NeonButton.java)**: Botón interactivo personalizable con soporte para iconos SVG vectoriales, variantes de color neón (`NEON_PURPLE`, `NEON_CYAN`, `NEON_GREEN`), bordes redondeados y efectos hover con sombras suaves.
- **[RoundedPanel.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/RoundedPanel.java)**: Contenedor neumórfico con radio de curva configurable, color de fondo dinámico (`ThemeConstants.CARD_BACKGROUND`) y sombra perimetral.
- **[NeonLineChart.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/NeonLineChart.java)**: Componente de renderizado de gráficos de línea continua y comparativa interperiodo mediante `Graphics2D` con suavizado Anti-Aliasing, curva de Bézier, grid adaptativo y badge de variación porcentual $\Delta \%$.
- **[NeonBarChart.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/NeonBarChart.java)**: Gráfico de barras vectoriales neón para la comparativa macro (Ventas, Utilidad e Inversión) con valores contables en tooltips y animación al pasar el cursor.
- **[NeonPieChart.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/NeonPieChart.java)**: Gráfico de dona polar interactivo para el Top 5 de categorías + "OTROS", con cálculo cartesiano `Math.atan2`, hover offset de 7px, conector Bézier y leyenda explícita.
- **[ThemeToggleButton.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/ThemeToggleButton.java)**: Conmutador de tema visual (Modo Oscuro / Modo Claro) que reevalúa la paleta de color en tiempo real sin reiniciar la JVM.

#### Moléculas (`components/molecules` & `components`)
- **[SidebarItem.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/molecules/SidebarItem.java)**: Fila de menú que combina icono SVG con texto, gestionando estados activo, normal e interactivo (hover).
- **[SidebarSection.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/molecules/SidebarSection.java)**: Acordeón colapsable para submenús con persistencia de estado mediante `java.util.prefs.Preferences` e indicador visual dinámico (▲/▼).
- **[AutocompletePopup.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/AutocompletePopup.java)**: Ventana flotante desacoplada basada en genéricos `<T>` para búsqueda y autocompletado reactivo en `JTextFields` sin bloquear el foco.

#### Organismos (`components/organisms`)
- **[ModernSidebar.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/organisms/ModernSidebar.java)**: Menú de navegación principal con colapso horizontal dinámico (260px $\leftrightarrow$ 64px), perfil de usuario y conmutador de tema.
- **[MetricCard.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/organisms/MetricCard.java)**: Tarjeta KPI ejecutiva compuesta por icono SVG con filtro neón, título, valor formateado compacto (`$100K`, `$5.4M`) y tendencia de variación porcentual.

#### Páginas y Vistas Principales (`views/`)
- **[DashboardPage.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/DashboardPage.java)**: Centro de mando BI con 4 tarjetas KPI, selector de periodo (`Hoy`, `Esta Semana`, `Este Mes`, `Este Año`, `Histórico`), gráfico de líneas comparativo interperiodo, gráfico de donas de categorías y gráfico de barras de presupuesto macro.
- **[SalesPage.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/SalesPage.java)**: Punto de venta (POS 2.0) en 3 columnas (28% Catalogo, 44% Carrito, 28% Checkout), autocompletado en tiempo real de clientes, calculadora de vueltas exactas y selección de método de pago.
- **[ProductPage.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/ProductPage.java)**: Gestión de catálogo e inventario, alertas de stock crítico, filtro por categorías e importación/exportación masiva Excel.
- **[ComprasPage.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/ComprasPage.java)**: Módulo de abastecimiento e ingreso de compras a proveedores con incremento automático de inventario y registro de factura.
- **[CarteraPage.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/CarteraPage.java)**: Gestión de créditos, cuentas por cobrar, registro de abonos parciales e historial de pagos por cliente.
- **[ReportsPage.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/ReportsPage.java)**: Centro de inteligencia financiera con filtrado por rango de fechas, resumen de utilidad neta, COGS y exportación a Excel y PDF.
- **[ConfigPage.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/ConfigPage.java)**: Ajustes del sistema, respaldo/restauración de base de datos SQLite, gestión de usuarios/roles y estado de licencia.

---

## 4. Modelos Matemáticos & Fórmulas del Dashboard BI

El motor analítico de **ERP+ Business** calcula en tiempo real los indicadores clave de rendimiento (KPIs) combinando consultas SQL optimizadas sobre SQLite con procesamiento numérico defensivo en Java (`VentasController.java` y `ProductoController.java`).

### 4.1 Ingreso Total Facturado ($\mathbf{V_{total}}$)
El ingreso bruto acumulado en un intervalo de tiempo parametrizado $[t_0, t_1]$ representa la suma de los valores totales de las ventas confirmadas:

$$\mathbf{V_{total}} = \sum_{i \in \text{Ventas}(t_0, t_1)} \text{total}_i$$

*Consulta SQL Equivalente (`VentasController.obtenerTotalVentasPorPeriodo`):*
```sql
SELECT COALESCE(SUM(total), 0.0) 
FROM ventas 
WHERE fecha >= ? AND fecha <= ?;
```

### 4.2 Costo de Mercancía Vendida (COGS $\mathbf{C_{total}}$)
El costo total de la mercancía comercializada retiene el *snapshot* del precio de costo unitario ($C_{i,j}$) al momento exacto en que se efectuó la transacción, previniendo distorsiones por cambios futuros en la lista de precios:

$$\mathbf{C_{total}} = \sum_{i=1}^{N} \sum_{j=1}^{M_i} (C_{i,j} \times Q_{i,j})$$

Donde $N$ es el número de ventas, $M_i$ el número de renglones/detalles de la venta $i$, $C_{i,j}$ el precio de costo del producto $j$ (`precio_costo`), y $Q_{i,j}$ la cantidad vendida (`cantidad`).

*Consulta SQL Equivalente (`VentasController.obtenerUtilidadPorPeriodo`):*
```sql
SELECT COALESCE(SUM(d.precio_costo * d.cantidad), 0.0)
FROM detalles_venta d
JOIN ventas v ON d.venta_id = v.id
WHERE v.fecha >= ? AND v.fecha <= ?;
```

### 4.3 Utilidad Neta ($\mathbf{P_{net}}$) y Margen de Ganancia ($\mathbf{M_{net}}$)
La ganancia líquida real del negocio descuenta el COGS del total facturado:

$$\mathbf{P_{net}} = \mathbf{V_{total}} - \mathbf{C_{total}} = \sum_{i=1}^{N} \sum_{j=1}^{M_i} \left[ T_{i,j} - (C_{i,j} \times Q_{i,j}) \right]$$

El margen de utilidad neta porcentual se define formalmente como:

$$\mathbf{M_{net}} = \begin{cases} 
\left( \frac{\mathbf{P_{net}}}{\mathbf{V_{total}}} \right) \times 100 & \text{si } \mathbf{V_{total}} > 0 \\ 
0 & \text{si } \mathbf{V_{total}} = 0 
\end{cases}$$

### 4.4 Variación Porcentual Interperiodo ($\mathbf{\Delta \%}$)
Para evaluar la tendencia de crecimiento o decrecimiento de ventas entre el periodo actual ($\mathbf{V_{actual}}$) y el periodo inmediatamente anterior de igual duración ($\mathbf{V_{anterior}}$), se utiliza la tasa de cambio relativa:

$$\mathbf{\Delta \%} = \begin{cases} 
\left( \frac{\mathbf{V_{actual}} - \mathbf{V_{anterior}}}{\mathbf{V_{anterior}}} \right) \times 100 & \text{si } \mathbf{V_{anterior}} > 0 \\
+100\% & \text{si } \mathbf{V_{actual}} > 0 \text{ y } \mathbf{V_{anterior}} = 0 \\
0\% & \text{en cualquier otro caso}
\end{cases}$$

Esta ecuación alimenta dinámicamente el badge neón del componente `NeonLineChart.java` y las tarjetas `MetricCard.java`.

### 4.5 Valor Total del Inventario en Bodega ($\mathbf{V_{inv}}$) e Inversión Total ($\mathbf{I_{total}}$)
El capital inmovilizado en el almacén representa la valoración a precio de costo de todos los productos en existencias:

$$\mathbf{V_{inv}} = \sum_{p=1}^{K} (C_p \times S_p)$$

Donde $K$ es el total de productos en el catálogo, $C_p$ es el precio de costo del producto $p$ y $S_p$ es el stock actual disponible (`cantidad`).

La **Inversión Total Histórica** $\mathbf{I_{total}}$ desglosa además los ingresos de mercancía realizados mediante facturas formales de abastecimiento a proveedores ($\mathbf{I_{abast}}$):

$$\mathbf{I_{total}} = \mathbf{I_{abast}} + \mathbf{V_{inv\_directo}}$$

$$\mathbf{I_{abast}} = \sum_{c=1}^{B} \sum_{r=1}^{L_c} (C_{c,r} \times Q_{c,r})$$

Donde $B$ es el número de compras a proveedores y $L_c$ el número de ítems de la compra $c$.

### 4.6 Cuotas de Cartera Pendiente ($\mathbf{R_{cartera}}$)
El saldo total por cobrar acumulado en cuentas de crédito a clientes se calcula como la diferencia entre la suma facturada a crédito y la suma de abonos registrados:

$$\mathbf{R_{cartera}} = \sum_{v \in \text{VentasCrédito}} \left( \text{total}_v - \sum_{a \in \text{Abonos}(v)} \text{monto}_a \right)$$

---

## 5. Flujos Transaccionales & Operaciones ACID

### 5.1 Transacción Atómica POS (`VentasController.guardarVenta`)

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

### 5.2 Módulo de Cartera & Recaudo de Abonos (`CarteraController.registrarAbono`)

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

### 5.3 Motor Dual de Importación Excel/CSV (`ExcelSQLiteManager`)

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
