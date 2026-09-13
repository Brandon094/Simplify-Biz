# Documentación Técnica & Guía de Ingeniería — ERP+ Business (v1.3.0)

> **Documentación Técnica Empresarial / Silicon Valley Startup Standard**  
> Especificación de arquitectura, patrones de diseño de software, pipeline de datos, motor de renderizado Swing 2D y protocolos de seguridad.

---

## 1. Visión General del Sistema & Arquitectura High-Level

**ERP+ Business** es una plataforma de software de escritorio desacoplada de alto rendimiento desarrollada en Java 25 y Swing, respaldada por un motor de persistencia relacional SQLite con registros WAL (*Write-Ahead Logging*). La arquitectura está diseñada bajo los principios **SOLID**, **DRY** (*Don't Repeat Yourself*) y **Atomic Design**.

### 1.1 Diagrama de Arquitectura de Componentes

```text
┌──────────────────────────────────────────────────────────────────────────────────┐
│                             PRESENTACIÓN & UI (Swing)                            │
│  ┌───────────────────────┐  ┌─────────────────────────┐  ┌────────────────────┐  │
│  │ MainTemplate (Shell)  │  │ DashboardPage           │  │ SalesPage (POS)    │  │
│  └───────────┬───────────┘  └────────────┬────────────┘  └─────────┬──────────┘  │
│              │                           │                         │             │
│              ▼                           ▼                         ▼             │
│  ┌────────────────────────────────────────────────────────────────────────────┐  │
│  │ ATOMIC DESIGN COMPONENTS: NeonButton | NeonBarChart | NeonPieChart | Popup │  │
│  └───────────────────────────────────────┬────────────────────────────────────┘  │
└──────────────────────────────────────────┼───────────────────────────────────────┘
                                           │ (ResultadoOperacion DTO)
                                           ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                           CAPA DE CONTROLADORES & LOGICA                         │
│  ┌────────────────────────┐  ┌────────────────────────┐  ┌─────────────────────┐ │
│  │ ProductoController     │  │ VentasController       │  │ UsuarioController   │ │
│  └───────────┬────────────┘  └───────────┬────────────┘  └──────────┬──────────┘ │
└──────────────┼───────────────────────────┼──────────────────────────┼────────────┘
               │                           │                          │
               └───────────────────────────┼──────────────────────────┘
                                           ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                         INFRAESTRUCTURA & PERSISTENCIA                           │
│  ┌────────────────────────┐  ┌────────────────────────┐  ┌─────────────────────┐ │
│  │ GestorConexion (WAL)   │  │ ConexionDB (Schema)    │  │ Seguridad (SHA-256) │ │
│  └───────────┬────────────┘  └───────────┬────────────┘  └──────────┬──────────┘ │
└──────────────┼───────────────────────────┼──────────────────────────┼────────────┘
               │                           │                          │
               ▼                           ▼                          ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                          ALMACENAMIENTO FISICO (SQLite)                          │
│               Windows: %APPDATA%/ERPPlusBusiness/db.db                           │
│               Linux: ~/.config/ERPPlusBusiness/db.db                            │
└──────────────────────────────────────────────────────────────────────────────────┘
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
├── controllers/         # Reglas de negocio y transacciones (DAOs embebidos)
│   ├── ProductoController.java    # Gestión de inventario, stock crítico e inversión
│   ├── VentasController.java      # Transacciones ACID de POS y utilidad neta
│   └── UsuarioController.java     # Autenticación, clientes y empleados
├── models/              # Modelos de dominio POJO (Lombok)
│   ├── Producto.java              # Atributos SKU, precios (venta/costo), stock y categoría
│   ├── Usuario.java               # Roles, credenciales e identificación
│   ├── Venta.java                 # Transacción compuesta con snapshot de precios
│   └── Sesion.java                # Contenedor de sesión activa en la JVM
└── views/               # Interfaz gráfica moderna (FlatLaf + Cyberpunk Neon)
    ├── components/
    │   ├── atoms/                 # NeonButton, NeonLineChart, NeonPieChart, NeonBarChart, RoundedPanel
    │   ├── AutocompletePopup.java # Motor genérico de autocompletado en tiempo real
    │   ├── molecules/             # SidebarItem
    │   └── organisms/             # ModernSidebar, MetricCard, PasswordRecoveryDialog
    └── [Pages]                    # DashboardPage, SalesPage, ProductPage, ClientsPage, ReportsPage, ConfigPage
```

---

## 3. Patrones de Diseño Avanzados

### 3.1 Conexión Compartida Singleton (`GestorConexion`)
Para evitar los bloqueos `SQLITE_BUSY` (`database is locked`), la aplicación mantiene una única conexión abierta en la memoria de la JVM. En el momento de la inicialización, se ejecutan las siguientes pragmas de rendimiento:
- `PRAGMA journal_mode=WAL;` (Write-Ahead Logging para lectura/escritura concurrente).
- `PRAGMA busy_timeout=5000;` (Espera defensiva de 5000 ms).
- `PRAGMA foreign_keys=ON;` (Enforza integridad referencial).

### 3.2 Motor Genérico de Autocompletado (`AutocompletePopup<T>`)
Implementado mediante un popup desacoplado que soporta interfaces funcionales:
- `SearchProvider<T>`: Consulta asíncrona de coincidencia (ej: `productoCtrl.buscarProductosSugeridos`).
- `DisplayFormatter<T>`: Formateo dinámico del ítem desplegado.
- `SelectionListener<T>`: Callback al seleccionar con clic o tecla `ENTER`.
- Interceptación de teclado (`VK_UP`, `VK_DOWN`, `VK_ESCAPE`, `VK_ENTER`) que transfiere el foco sin cerrar el popup.

### 3.3 Motor de Renderizado Gráfico 2D (`Graphics2D`)
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

-- 2. Insertar detalles reteniendo snapshot de costo
INSERT INTO detalles_venta (venta_id, producto, cantidad, codigo, precio, precio_costo, total)
VALUES (?, ?, ?, ?, ?, ?, ?);

-- 3. Descontar inventario de forma defensiva
UPDATE productos SET cantidad = cantidad - ? 
WHERE codigo = ? AND cantidad >= ?;

COMMIT; -- O ROLLBACK en caso de excepción
```

- Si `metodoPago` es `"Crédito"`, `pago_confirmado` se establece en `'deudor'`. En `"Efectivo"` o `"Transferencia"`, en `'pagado'`.

### 4.2 Métricas Financieras del Dashboard

1. **Ventas Totales ($)**: `SELECT SUM(total) FROM ventas;`
2. **Utilidad Neta ($)**: `SELECT SUM(total - (precio_costo * cantidad)) FROM detalles_venta;`
3. **Inversión en Bodega ($)**: `SELECT SUM(precio_costo * cantidad) FROM productos;`
4. **Margen Real (%)**: `(Utilidad Neta / Ventas Totales) * 100.0`

---

## 5. Construcción, Pruebas y Despliegue

```bash
# Compilar fuentes Java
mvn clean compile

# Ejecutar suite de pruebas unitarias (JUnit 5 en memoria)
mvn test

# Empaquetar artefacto JAR ejecutable
mvn clean package

# Ejecución en producción
java -jar dist/Simplify-Biz-1.3.0.jar
```
