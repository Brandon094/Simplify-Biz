# Documentación Técnica — ERP+ Business

> Referencia técnica completa del proyecto: arquitectura, patrones de diseño, flujos de datos, componentes y proceso de construcción.

---

## 1. Alcance

ERP+ Business es una aplicación de escritorio Java Swing con persistencia SQLite local, autenticación por roles y una ventana principal de contenido intercambiable. No requiere servidor web ni base de datos externa.

---

## 2. Capas del Proyecto

El proyecto sigue una arquitectura **MVC (Model-View-Controller)** organizada en cuatro paquetes principales:

```text
com.mycompany.zl_solucion_integral/
├── config/          Infraestructura: conexión, seguridad, rutas, validaciones, utilidades
├── controllers/     Lógica de negocio y acceso a datos (DAO embebido)
├── models/          Modelos de dominio (POJOs con Lombok)
└── views/           Interfaz gráfica Java Swing
    └── components/  Sistema de diseño: constantes, utilidades, átomos, moléculas y organismos
```

### 2.1 Capa `config` — Infraestructura

| Clase | Responsabilidad |
| :--- | :--- |
| `GestorConexion` | Singleton de conexión SQLite. Gestiona apertura, PRAGMAs (WAL, busy_timeout, foreign_keys), resolución de archivo efectivo y cierre. |
| `ConexionDB` | Creación de tablas (`CREATE TABLE IF NOT EXISTS`) e inserción de valores iniciales. |
| `DatabaseInitializer` | Orquesta la inicialización delegando a `ConexionDB`. |
| `ResultadoOperacion` | DTO inmutable `(exito, mensaje)` que desacopla controladores de Swing. Factory methods: `ok(msg)`, `error(msg)`. |
| `SelecionRuta` | Lee/escribe `config.properties`, provee la ruta protegida por SO por defecto (`%APPDATA%`/`~/.config`), selección de carpeta de BD, preferencia de tema y persistencia de usuario recordado (`remember.user`). |
| `Seguridad` | Encriptación y validación de contraseñas. |
| `Validaciones` | Parsers seguros: `parseEntero`, `parseEnteroPositivo`, `parseDecimalNoNegativo`, `parseFecha`. Validaciones de email, teléfono, categoría, descuento, etc. |
| `ExcelSQLiteManager` | Importación masiva de datos desde archivos Excel a SQLite. |
| `EnvioCotizacion` | Envío de cotizaciones por correo electrónico vía JavaMail. |
| `PantallaCarga` | Splash screen de carga durante la inicialización. |
| `Listener` | Interfaz funcional para callbacks de eventos. |
| `UtilVentanas` | Utilidades de posicionamiento de ventanas. |

### 2.2 Capa `controllers` — Lógica de Negocio

Los controladores **no invocan** `JOptionPane`, `UIUtils.showSuccess` ni ningún componente Swing. Cada operación retorna un `ResultadoOperacion` que la vista interpreta para mostrar feedback al usuario.

| Controlador | Operaciones Principales |
| :--- | :--- |
| `ProductoController` | `agregarOActualizarProductoSiExiste`, `modificarProducto`, `eliminarProducto`, `eliminarCantidadProducto`, `mostrarProductos`, `buscarProductoPorCodigo/Nombre`, `obtenerDistribucionCategorias`, `obtenerCantidadStockCritico` |
| `UsuarioController` | `agregarUsuario`, `modificarUsuario` (contraseña opcional), `eliminarUsuario`, `mostrarUsuarios/PorRol`, `validarCredencialesAdmin/Regular`, `validarDatosRecuperacion`, `restablecerContraseña`, `existeAdministrador` |
| `VentasController` | `guardarVenta` (transacción atómica), `modificarVenta`, `MostrarVentas`, `mostrarFechasDefinidas`, `mostrarVentasPorDia`, `exportarDatosTablaAExcel`, `generarArchivoCotizacionConPlantilla`, `obtenerVentasUltimos7Dias`, `obtenerVentasTotales` |

Todos los controladores obtienen la conexión vía `GestorConexion.getInstancia().obtenerConexion()` y nunca la cierran (la conexión es compartida).

### 2.3 Capa `models` — Dominio

| Modelo | Campos Principales | Notas |
| :--- | :--- | :--- |
| `Producto` | id, producto, precio, cantidad, codigo, total, categoria, cantidadSolicitada | Lombok `@Data/@AllArgsConstructor` |
| `Usuario` | id, nombre, telefono, email, contraseña, rol, noCc, NIT, DIR | Multirol: admin(1), vendedor(0), cliente(2) |
| `Venta` | producto, cantidad, codigo, total, fecha, metodoPago, vendedor, cliente, descuento | Modelo compuesto con referencia a Producto y Usuario |
| `Sesion` | usuarioLogueado | Datos de sesión activa |
| `Informe` | *(vacío)* | Placeholder para funcionalidad futura |

### 2.4 Capa `views` — Interfaz Gráfica

Organización inspirada en **Atomic Design**:

| Nivel | Componentes | Descripción |
| :--- | :--- | :--- |
| **Atoms** | `NeonButton`, `RoundedPanel`, `NeonLineChart`, `NeonPieChart`, `ThemeToggleButton` | Elementos visuales primitivos reutilizables |
| **Molecules** | `SidebarItem` | Combinaciones funcionales de átomos |
| **Organisms** | `ModernSidebar`, `MetricCard`, `PasswordRecoveryDialog` | Secciones completas de interfaz y diálogos modales |
| **Shell** | `MainTemplate` | Contenedor principal con sidebar + área de contenido |
| **Páginas** | `DashboardPage`, `ProductPage`, `SalesPage`, `ClientsPage`, `SellersPage`, `ReportsPage`, `ConfigPage`, `ProvidersPage` | Pantallas funcionales completas (SalesPage incluye retícula simétrica de 3 columnas con cabeceras alineadas milimétricamente a 24px de padding superior; estados vacíos estandarizados con centrado absoluto horizontal y vertical a través de `UIUtils.createEmptyState`) |
| **Acceso** | `ModernLoginPage`, `ModernAdminRegistrationPage` | Formularios de autenticación con iconos SVG en inputs (user/lock), recuerdo de usuario, recuperación de contraseña e hipervínculo interactivo a ChopCode Solutions |
| **Utilidades** | `ThemeConstants`, `LayoutResponsive`, `UIUtils`, `UIMessages` | Sistema de temas, responsive, helpers de UI, mensajes y fábrica de estados vacíos centrados |

---

## 3. Sistema de Temas

`ThemeConstants` centraliza todos los colores y dimensiones de la UI (principio DRY). Define dos paletas completas —**oscura** (predeterminada) y **clara**— intercambiables en caliente:

- **Estado:** `ThemeConstants.isDark()` / `setDark(boolean)` / `toggleTheme()`
- **Persistencia:** `SelecionRuta.guardarPreferenciaTema()` escribe `theme.dark` en `config.properties`
- **Arranque:** `SelecionRuta.cargarPreferenciaTema()` lee la preferencia (oscuro si no existe)
- **Aplicación:** `Main.aplicarTema()` instala `FlatMacDarkLaf` o `FlatMacLightLaf` y re-aplica estilos globales
- **Reconstrucción:** `MainTemplate` reconstruye la ventana completa con la nueva paleta

El cambio de tema es puramente visual: no afecta datos, conexiones ni lógica de negocio.

Constantes semánticas disponibles: `BACKGROUND`, `SIDEBAR_BACKGROUND`, `CARD_BACKGROUND`, `INPUT_BACKGROUND`, `INPUT_BORDER`, `TABLE_ZEBRA`, `TEXT_PRIMARY`, `TEXT_SECONDARY`, `NEON_PURPLE`, `NEON_BLUE`, `NEON_GREEN`, `NEON_CYAN`, `CARD_BORDER`, `HOVER_BACKGROUND`, `ACTIVE_BACKGROUND`, `BRAND_BACKGROUND`, `GRID_LINE`.

---

## 4. Persistencia y Gestión de Conexiones

### 4.1 Patrón Singleton (`GestorConexion`)

```
Main.inicializarBaseDatos()
  └─► GestorConexion.getInstancia().inicializar(dbPath)
        ├─► resolverArchivo(dbPath)     → File efectivo
        ├─► DriverManager.getConnection(jdbcUrl)
        └─► aplicarPragmas()
              ├─ PRAGMA journal_mode=WAL
              ├─ PRAGMA busy_timeout=5000
              └─ PRAGMA foreign_keys=ON
```

- **Prevención de `database is locked`:** Una sola conexión compartida por toda la aplicación.
- **Logs silenciados:** Solo emite `INFO` en el arranque inicial; reconexiones van a `FINE`.
- **Pruebas:** `reiniciarParaPruebas(jdbcUrl)` permite inyectar `jdbc:sqlite::memory:`.

### 4.2 Contrato de Rutas

`GestorConexion.resolverArchivo(dbPath)` resuelve el archivo SQLite efectivo:

| Escenario | `db.path` | Archivo Efectivo |
| :--- | :--- | :--- |
| Carpeta (predeterminado) | `/home/user/data` | `/home/user/data/db.db` |
| Archivo `.db` explícito | `/home/user/mi_bd.db` | `/home/user/mi_bd.db` |
| Archivo `.sqlite` explícito | `/home/user/datos.sqlite` | `/home/user/datos.sqlite` |
| Carpeta inexistente | `/home/user/nueva` | `/home/user/nueva/db.db` (crea carpeta) |

### 4.3 Desacoplamiento Swing

Los controladores **nunca** invocan componentes de UI. Retornan `ResultadoOperacion`:

```java
// En el controlador:
return ResultadoOperacion.ok(UIMessages.MSG_PRODUCTO_GUARDADO);

// En la vista:
ResultadoOperacion r = controller.agregarProducto(producto);
if (r.esExito()) UIUtils.showSuccess(this, r.getMensaje());
else UIUtils.showError(this, r.getMensaje());
```

---

## 5. Arranque y Navegación

### 5.1 Flujo de Arranque

1. `Main.main()` carga la preferencia de tema y aplica FlatLaf.
2. Lee `config.properties` para obtener `db.path`.
3. Si no existe `db.path`, muestra selector de carpeta.
4. `GestorConexion.inicializar(ruta)` abre la conexión SQLite.
5. `DatabaseInitializer.inicializarTablas()` crea tablas si no existen.
6. Shutdown hook registrado para cerrar la conexión al salir.
7. `UsuarioController.existeAdministrador()`:
   - **No** → `ModernAdminRegistrationPage`
   - **Sí** → `ModernLoginPage`

### 5.2 Navegación por Rol

`MainTemplate` recibe el rol y configura el sidebar:

| Rol | Módulos Visibles |
| :--- | :--- |
| **Administrador** (`1`) | Dashboard, Ventas, Productos, Clientes, Empleados, Reportes, Configuración |
| **Vendedor** (`0`) | Ventas, Productos |

---

## 6. Flujos de Negocio

### 6.1 Productos

1. `ProductPage` valida campos con `Validaciones`.
2. Crea un `Producto` y llama a `ProductoController.agregarOActualizarProductoSiExiste()`.
3. Si el código existe → suma stock (`UPDATE cantidad`).
4. Si no existe → inserta nuevo producto (`INSERT`).
5. La vista muestra el `ResultadoOperacion` correspondiente.

### 6.2 Ventas

1. `SalesPage` busca productos por código o nombre.
2. Construye un carrito con cantidades y descuento porcentual.
3. Soporta `CONSUMIDOR FINAL` / `N/A` para venta mostrador.
4. `VentasController.guardarVenta()` ejecuta una **transacción atómica**:
   - Verifica stock suficiente para todos los productos.
   - `INSERT INTO ventas` → obtiene ID generado.
   - `INSERT INTO detalles_venta` por cada producto.
   - `UPDATE productos SET cantidad = cantidad - ?` por cada producto.
   - `commit()` si todo OK; `rollback()` si falla cualquier paso.

### 6.3 Usuarios (Clientes y Empleados)

- **Clientes** (`rol = 2`): Se listan con `mostrarUsuariosPorRol(..., "2")`. Vista de solo consulta.
- **Empleados** (`rol = 0`): CRUD completo. `modificarUsuario` conserva la contraseña existente si el campo queda vacío.
- **Administradores** (`rol = 1`): Se crean en el registro inicial o manualmente.
- **Seguridad:** La constante `COLUMNAS_LISTADO` excluye `contraseña` de todas las consultas de listado.

### 6.4 Reportes

- `MostrarVentas()` carga el historial completo (JOIN ventas + detalles_venta).
- `mostrarFechasDefinidas()` filtra por rango de fechas.
- `exportarDatosTablaAExcel()` genera archivos `.xlsx` con Apache POI.
- `generarArchivoCotizacionConPlantilla()` rellena una plantilla Excel con los datos de la cotización.

---

## 7. Recursos Visuales

### 7.1 Iconografía SVG

Los iconos están en `src/main/resources/icons/` y se cargan con `FlatSVGIcon`:

```java
new FlatSVGIcon("icons/sun.svg", 20, 20)
```

Se aplican `ColorFilter` dinámicos según el estado (hover, activo, tema).

### 7.2 Gráficos

- `NeonLineChart`: Gráfico de líneas con curva suave, grid, escala automática y leyenda.
- `NeonPieChart`: Gráfico de torta/anillo con categorías y leyenda lateral.
- Ambos usan `Graphics2D` con antialiasing y renderizado de alta calidad.

---

## 8. Construcción y Verificación

```bash
# Compilar fuentes
mvn compile

# Ejecutar pruebas automatizadas (JUnit 5 en memoria / headless)
mvn test

# Empaquetar JAR ejecutable
mvn clean package

# Ejecutar aplicación
java -jar dist/Simplify-Biz-1.3.0.jar
```

Las modificaciones de lógica de negocio y persistencia se verifican automáticamente con `mvn test`. Las modificaciones visuales deben validarse abriendo el JAR empaquetado.

---

## 9. Documentos Relacionados

- [Manual de Operación](manual_usuario.md)
- [Arquitectura de UI/UX](arquitectura_ui.md)
- [Configuraciones y Rutas](configuraciones.md)
- [Esquema de Base de Datos](esquema_bd.md)
- [Diccionario de Datos](diccionario_datos.md)
- [API de Controladores](api_controladores.md)
- [Roadmap de Desarrollo](roadmap/roadmap.md)
