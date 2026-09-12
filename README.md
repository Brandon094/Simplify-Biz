# ERP+ Business

> **Sistema ERP de escritorio para pequeñas y medianas empresas** — Gestión integral de inventario, ventas, clientes, empleados, reportes y configuración en una interfaz moderna de ventana única.

![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square)
![SQLite](https://img.shields.io/badge/SQLite-3.46-blue?style=flat-square)
![FlatLaf](https://img.shields.io/badge/FlatLaf-3.5.1-purple?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=flat-square)
![Tests](https://img.shields.io/badge/Tests-JUnit%205-green?style=flat-square)
![License](https://img.shields.io/badge/License-Propietaria-lightgrey?style=flat-square)

---

## 🏗️ Estado del Proyecto

Actualmente el sistema cuenta con las **Fases 1 (UI/UX), 2 (Responsive) y 3 (Core & Estabilidad)** cerradas y consolidadas. Consulta el [Roadmap](docs/roadmap/roadmap.md) para ver las fases futuras.

---

## ✨ Características Principales

| Característica | Descripción |
| :--- | :--- |
| **Conexión Singleton a SQLite** | Patrón Singleton (`GestorConexion`) que mantiene una única conexión compartida con modo WAL, `busy_timeout` y `foreign_keys`, previniendo `database is locked`. |
| **Almacenamiento Seguro** | Ubicación protegida automática por el SO (`%APPDATA%\ERPPlusBusiness` en Windows, `~/.config/ERPPlusBusiness` en Linux) para resguardar la base de datos de borrados accidentales. |
| **Tema Oscuro y Claro** | Alternancia en caliente mediante toggle (☀️/🌙) en el sidebar sin reiniciar la app. Paleta centralizada en `ThemeConstants`. La preferencia se persiste en `config.properties`. |
| **Diseño Adaptativo** | Breakpoints fluidos (`LayoutResponsive`) para pantallas desde 360px hasta escritorio completo, con sidebar colapsable y formularios reflow. |
| **Arquitectura MVC** | Controladores independientes de Swing que retornan `ResultadoOperacion` y usan constantes de `UIMessages`. Cero acoplamientos a `JOptionPane` en la capa de negocio. |
| **Seguridad** | Contraseñas encriptadas (`Seguridad`). Columna de contraseña excluida de todas las consultas de listado. |
| **Validación Defensiva** | Parsers seguros en `Validaciones` (enteros, decimales, fechas `dd/MM/yyyy`) que previenen `NumberFormatException` y cierres inesperados. |
| **Pruebas Automatizadas** | Suite de 30 pruebas unitarias e integración en JUnit 5 ejecutables con `mvn test`. |
| **Atomic Design** | Componentes organizados en átomos, moléculas y organismos reutilizables. |

---

## 🚀 Inicio Rápido

### Requisitos

| Requisito | Versión Mínima |
| :--- | :--- |
| JDK | 25 |
| Maven | 3.8 |
| Sistema Operativo | Linux / Windows / macOS |

Se requieren permisos de lectura/escritura en la carpeta de la aplicación y en la ruta de datos elegida.

### Compilar y Ejecutar

```bash
# Compilar fuentes
mvn compile

# Ejecutar suite de pruebas automatizadas
mvn clean test

# Empaquetar JAR ejecutable
mvn clean package

# Ejecutar aplicación
java -jar dist/Simplify-Biz-1.2.0.jar
```

> **Nota sobre NTFS:** El `target` de compilación está redirigido a `~/.m2-tmp/` para evitar fallos de `mvn clean` en particiones NTFS montadas vía FUSE. El JAR final se copia automáticamente a `dist/`.

---

## 🏁 Primer Inicio

1. `Main` configura el tema FlatLaf (oscuro por defecto vía preferencia guardada) y aplica estilos globales.
2. Carga `config.properties`. Si no existe `db.path`, asigna automáticamente la **carpeta protegida de datos de aplicación del usuario por SO** (`%APPDATA%\ERPPlusBusiness` en Windows, `~/.config/ERPPlusBusiness` en Linux).
3. `GestorConexion` resuelve la ruta efectiva (carpeta → `db.db`, o archivo `.db`/`.sqlite` explícito) y abre la conexión SQLite compartida con PRAGMAs WAL.
4. `DatabaseInitializer` → `ConexionDB` crea las tablas e inserta valores por defecto si no existen.
5. Si no hay un administrador registrado (`rol = 1`), muestra el formulario de **Registro Inicial**; de lo contrario despliega el **Login**.

---

## 📦 Módulos

### Dashboard
KPIs en tiempo real (ventas acumuladas, órdenes, productos, stock crítico), gráfico lineal neon de ventas de los últimos 7 días, indicador de actividad operativa y tabla de últimas ventas.

### Productos
Catálogo con SKU, precio, categoría y stock. Gráfico circular de distribución por categoría. Operaciones: crear (si el código existe, suma stock), modificar, eliminar con confirmación. Filtro por categoría.

### Ventas
Carrito interactivo con descuento porcentual, búsqueda por código o nombre, soporte para venta mostrador (`CONSUMIDOR FINAL / N/A`). Transacción SQLite atómica que inserta encabezado + detalles y descuenta stock con rollback automático.

### Clientes
Consulta de usuarios con rol `2`. Vista de solo lectura. Las contraseñas están excluidas de la consulta SQL.

### Empleados
Gestión de personal (rol `0`) para administradores. Registro, actualización (contraseña opcional) y eliminación con diálogo de confirmación. Contraseñas no visibles en tabla.

### Reportes
Historial de ventas con filtro por rango de fechas (`dd/MM/yyyy`) y exportación a Excel (`.xlsx`). Generación de cotizaciones desde plantilla Excel.

### Configuración
Consulta y cambio de ruta de base de datos, información del sistema (versión, motor SQLite, licencia) y enlace al portafolio del desarrollador ([ChopCode Solutions](https://portafolio-brandon-daza.web.app/)).

### Proveedores *(prototipo)*
Pantalla UI preparada. No persiste datos en SQLite. Oculta del sidebar hasta que se implemente la persistencia.

---

## 🗂️ Estructura del Proyecto

```text
Desarrollo Desktop/
├── pom.xml                         Configuración Maven (dependencias, plugins, target externo)
├── config.properties               Ruta de BD y preferencia de tema (generado en ejecución)
├── dist/                           JAR ejecutable final (copiado tras mvn package)
├── docs/                           Documentación técnica, manuales y roadmap
│
└── src/
    ├── main/
    │   ├── java/com/mycompany/zl_solucion_integral/
    │   │   ├── Main.java                          Punto de entrada: tema, BD, flujo inicial
    │   │   ├── config/
    │   │   │   ├── GestorConexion.java             Singleton de conexión SQLite (WAL, pragmas)
    │   │   │   ├── ConexionDB.java                 Creación de tablas (DDL)
    │   │   │   ├── DatabaseInitializer.java        Orquestador de inicialización de tablas
    │   │   │   ├── ResultadoOperacion.java         DTO resultado ok/error sin Swing
    │   │   │   ├── SelecionRuta.java               Gestión de config.properties y rutas
    │   │   │   ├── Seguridad.java                  Encriptación y validación de contraseñas
    │   │   │   ├── Validaciones.java               Parsers seguros (enteros, decimales, fechas)
    │   │   │   ├── ExcelSQLiteManager.java         Importación de datos desde Excel
    │   │   │   ├── EnvioCotizacion.java            Envío de cotizaciones por correo (JavaMail)
    │   │   │   ├── Listener.java                   Interfaz de listener para eventos
    │   │   │   ├── PantallaCarga.java              Splash screen de carga inicial
    │   │   │   └── UtilVentanas.java               Utilidades de ventanas
    │   │   │
    │   │   ├── controllers/
    │   │   │   ├── ProductoController.java         CRUD productos, búsqueda, stock crítico
    │   │   │   ├── UsuarioController.java          CRUD usuarios, autenticación, roles
    │   │   │   └── VentasController.java           Transacciones de venta, reportes, Excel
    │   │   │
    │   │   ├── models/
    │   │   │   ├── Producto.java                   Modelo de producto con Lombok
    │   │   │   ├── Usuario.java                    Modelo de usuario/cliente
    │   │   │   ├── Venta.java                      Modelo de venta con detalles
    │   │   │   ├── Sesion.java                     Datos de sesión del usuario logueado
    │   │   │   └── Informe.java                    Placeholder para informes futuros
    │   │   │
    │   │   └── views/
    │   │       ├── MainTemplate.java               Shell principal con sidebar y contenido
    │   │       ├── ModernLoginPage.java             Formulario de login moderno
    │   │       ├── ModernAdminRegistrationPage.java Registro del primer administrador
    │   │       ├── DashboardPage.java               Panel de KPIs y gráficos
    │   │       ├── ProductPage.java                 Gestión de productos
    │   │       ├── SalesPage.java                   Carrito y punto de venta
    │   │       ├── ClientsPage.java                 Consulta de clientes (rol 2)
    │   │       ├── SellersPage.java                 Gestión de empleados (rol 0)
    │   │       ├── ReportsPage.java                 Reportes y filtros de ventas
    │   │       ├── ConfigPage.java                  Configuración del sistema
    │   │       ├── ProvidersPage.java               Prototipo de proveedores
    │   │       └── components/
    │   │           ├── ThemeConstants.java           Paletas de colores (oscura/clara)
    │   │           ├── LayoutResponsive.java        Breakpoints y reflow adaptativo
    │   │           ├── UIUtils.java                 Utilidades de UI, estilos, componentes
    │   │           ├── UIMessages.java              Constantes de mensajes de usuario
    │   │           ├── atoms/
    │   │           │   ├── NeonButton.java          Botón neumórfico con brillo
    │   │           │   ├── RoundedPanel.java        Panel con esquinas redondeadas
    │   │           │   ├── NeonLineChart.java       Gráfico de líneas neon (Graphics2D)
    │   │           │   ├── NeonPieChart.java        Gráfico de torta por categorías
    │   │           │   └── ThemeToggleButton.java   Toggle de tema con SVG (sol/luna)
    │   │           ├── molecules/
    │   │           │   └── SidebarItem.java         Ítem de navegación lateral
    │   │           └── organisms/
    │   │               ├── ModernSidebar.java       Menú lateral colapsable
    │   │               └── MetricCard.java          Tarjeta KPI con tendencia
    │   │
    │   └── resources/
    │       └── icons/                              Iconos SVG (sun, moon, dashboard, etc.)
    │
    └── test/
        └── java/com/mycompany/zl_solucion_integral/
            ├── config/
            │   ├── GestorConexionTest.java          Tests del singleton de conexión
            │   └── ValidacionesTest.java            Tests de parsers y validaciones
            └── controllers/
                ├── ProductoControllerTest.java      Tests de CRUD de productos
                ├── UsuarioControllerTest.java       Tests de CRUD de usuarios
                └── VentasControllerTest.java        Tests de transacciones de venta
```

---

## 🧪 Pruebas

La suite de pruebas se ejecuta con JUnit 5 sobre una base de datos SQLite en memoria (`:memory:`), sin necesidad de entorno gráfico:

```bash
mvn clean test
```

| Clase de Test | Cobertura |
| :--- | :--- |
| `GestorConexionTest` | Singleton, resolución de rutas, PRAGMAs, conexión en memoria |
| `ValidacionesTest` | Parsers de enteros, decimales, fechas, emails, teléfonos |
| `ProductoControllerTest` | Crear, actualizar stock, modificar, eliminar, buscar |
| `UsuarioControllerTest` | Registrar, modificar, eliminar, validar credenciales, roles |
| `VentasControllerTest` | Transacción de venta, descuento de stock, rollback |

---

## 🔧 Dependencias

| Dependencia | Versión | Propósito |
| :--- | :--- | :--- |
| Java/JDK | 25 | Runtime y compilación |
| FlatLaf + Extras | 3.5.1 | Look & Feel moderno (`FlatMacDarkLaf` / `FlatMacLightLaf`) |
| SQLite JDBC | 3.46.1.0 | Motor de base de datos local |
| Apache POI | 5.2.3 | Lectura/escritura de archivos Excel (.xlsx) |
| JavaMail | 1.6.2 | Envío de cotizaciones por correo electrónico |
| Lombok | 1.18.30 | Reducción de boilerplate (getters, setters, constructors) |
| SLF4J Simple | 1.7.36 | Binding de logging para SQLite JDBC |
| JUnit Jupiter | 5.10.2 | Framework de pruebas unitarias e integración |
| Maven Surefire | 3.2.5 | Ejecución de pruebas automatizadas |
| Maven Shade | 3.4.1 | Generación de JAR ejecutable con dependencias |

---

## 📚 Documentación

| Documento | Descripción |
| :--- | :--- |
| [Documentación Técnica](docs/documentacion_tecnica.md) | Arquitectura, capas, patrones, flujos y construcción |
| [Arquitectura de UI/UX](docs/arquitectura_ui.md) | Sistema de diseño, Atomic Design, temas y responsive |
| [Configuraciones](docs/configuraciones.md) | `config.properties`, contrato de rutas y copias de seguridad |
| [Manual de Operación](docs/manual_usuario.md) | Guía paso a paso para cada módulo de la aplicación |
| [Esquema de Base de Datos](docs/esquema_bd.md) | DDL de todas las tablas SQLite |
| [Diccionario de Datos](docs/diccionario_datos.md) | Descripción funcional de cada campo |
| [Roadmap de Desarrollo](docs/roadmap/roadmap.md) | Fases completadas y planificadas |
| [API de Controladores](docs/api_controladores.md) | Referencia de métodos públicos de los controladores |

---

## 👨‍💻 Autor

**Brandon Daza** — [ChopCode Solutions](https://portafolio-brandon-daza.web.app/)

---

## 📄 Licencia

Software propietario. Todos los derechos reservados © ChopCode Solutions.
