# ERP+ BUSINESS — Next-Gen Enterprise POS & Inventory Platform

> **Plataforma ERP de Escritorio de Alto Rendimiento para Pequeñas y Medianas Empresas**  
> Solución integral de gestión comercial desarrollada bajo estándar de ingeniería de Silicon Valley: arquitectura desacoplada MVC, interfaz responsiva Atomic Design con estética Cyberpunk/Neon (FlatLaf), motor de persistencia relacional transaccional SQLite WAL, motor de importación masiva inteligente Excel/CSV y analítica financiera en tiempo real.

![Java 25](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk)
![SQLite WAL Mode](https://img.shields.io/badge/SQLite_3.46-WAL_Mode-blue?style=for-the-badge&logo=sqlite)
![FlatLaf Cyberpunk](https://img.shields.io/badge/FlatLaf-3.5.1_Cyberpunk-purple?style=for-the-badge)
![Maven 3.8+](https://img.shields.io/badge/Maven-3.8+-red?style=for-the-badge&logo=apachemaven)
![JUnit 5 Passed](https://img.shields.io/badge/Tests-41%2F41_Passed-brightgreen?style=for-the-badge&logo=junit5)
![Architecture](https://img.shields.io/badge/Architecture-MVC_%7C_Atomic_Design-informational?style=for-the-badge)

---

## 🏗️ Arquitectura del Sistema & Matriz de Fases

**ERP+ BUSINESS** elimina la complejidad técnica de los sistemas empresariales tradicionales mediante un ejecutable liviano de ventana única con persistencia local ACID de latencia cero y renderizado acelerado por hardware 2D en Swing.

### Fases Consolidadas de Ingeniería (100% Completadas)

- **[X] Fase 1 — UI/UX Cyberpunk & Design System Dynamic Tokens:** Alternancia de tema Oscuro Neón / Claro en caliente mediante `ThemeConstants` e integración de FlatLaf 3.5.1.
- **[X] Fase 2 — Engine Responsive & Adaptabilidad Fluida:** Gestor de breakpoints `LayoutResponsive` (móvil `< 480px`, tablet `480–960px`, escritorio `> 960px`) con drawer colapsable.
- **[X] Fase 3 — Core MVC & Persistencia Transaccional ACID:** Capa de controladores desacoplada (`VentasController`, `ComprasController`, `CarteraController`) con rollback automático en JDBC y patrón Singleton `GestorConexion`.
- **[X] Fase 4 — Control de Accesos, Seguridad & Cartera (CxC):** Autenticación por roles (`1` Admin, `0` Empleado, `2` Cliente), hashing criptográfico SHA-256 de claves y módulo de gestión de abonos parciales/totales.
- **[X] Fase 5 — Analítica Financiera & Abastecimiento:** Dashboard ejecutivo con widgets gráficos vectoriales 2D (`NeonPieChart`, `NeonLineChart`, `NeonBarChart`), módulo de Compras a Proveedores con incremento atómico de stock e historial 360° por cliente.
- **[X] Fase 6 — Ecosistema SVG, Top Header & Sidebar Ergónomico:** Implementación de 51+ iconos vectoriales FlatSVG, Top Header dinámico contextual y menú lateral colapsable horizontalmente de 260px a 64px (+170px de espacio útil).
- **[X] Fase 7 — Asistente de Importación Excel/CSV & UX POS Ergonométrica:** Engine `ExcelSQLiteManager` con lectura de cabeceras en caliente, mapeo dinámico visual de columnas, estrategia Upsert por SKU (creación o actualización con suma de existencias) y botones de acción del POS amplias a `115px` con `HAND_CURSOR` y hover fluido.

---

## 🛠️ Stack Tecnológico & Dependencias

| Componente | Tecnología | Versión / Especificación |
| :--- | :--- | :--- |
| **Lenguaje Base** | Java Development Kit (JDK) | **Java 25+** (Soporte en tiempo de ejecución) |
| **GUI Framework** | Java Swing + FlatLaf | **FlatLaf 3.5.1** (Themes MacDark / MacLight) |
| **Vectorial SVG** | FlatSVG / JSVG | **JSVG 1.4.0** / **FlatLaf Extras 3.5.1** (51 SVG Icons) |
| **Motor de Base de Datos** | SQLite JDBC | **SQLite 3.46.1.0** (Embebido, WAL Mode, Foreign Keys) |
| **Procesamiento Hojas de Cálculo**| Apache POI | **Apache POI 5.2.3** (OOXML `.xlsx` export/import) |
| **Testing Automatizado** | JUnit 5 | **JUnit Jupiter 5.10.0** (41 Tests unitarios/integración) |
| **Build Manager** | Apache Maven | **Maven 3.8+** (Plugins Shader, Compiler, Antrun) |

---

## 📐 Estructura del Repositorio (Atomic Design & MVC)

```text
com.mycompany.zl_solucion_integral/
├── Main.java                          # Punto de entrada de la aplicación JVM
├── config/                            # Infraestructura, Singleton DB, Seguridad y Parsers
│   ├── ConexionDB.java                # DDl, auto-migraciones de esquemas (`migrarColumnaSegura`)
│   ├── GestorConexion.java            # Singleton de conexión JDBC con PRAGMA WAL & Busy Timeout
│   ├── SelecionRuta.java              # Gestión de paths por SO y persistencia en `config.properties`
│   ├── Seguridad.java                 # Cifrado defensivo SHA-256
│   ├── Validaciones.java              # Parsers numéricos y limpiador regex de divisas
│   ├── ResultadoOperacion.java        # DTO inmutable de transporte de respuestas
│   └── ExcelSQLiteManager.java        # Motor de procesamiento masivo Excel/CSV con Upsert
├── controllers/                       # Controladores de Lógica de Negocio (MVC Puros)
│   ├── ProductoController.java        # Reglas de inventario, stock crítico y valuación a costo
│   ├── VentasController.java          # Transacciones atómicas POS, snapshots y utilidad neta
│   ├── UsuarioController.java         # Autenticación, roles y autocompletado de clientes
│   ├── CarteraController.java         # Gestión de deudas, abonos parciales y liquidación
│   ├── ComprasController.java         # Registro de facturas de proveedores e incremento de stock
│   └── ProveedorController.java       # Maestro de proveedores y búsquedas reactivas
├── models/                            # Entidades del Dominio (POJOs)
│   ├── Producto.java | Usuario.java | Venta.java | DetalleVenta.java
│   ├── Compra.java   | DetalleCompra.java | Proveedor.java | Sesion.java
└── views/                             # Capa de Presentación (Atomic Design Swing)
    ├── components/
    │   ├── atoms/                     # NeonButton, RoundedPanel, NeonLineChart, NeonBarChart, NeonPieChart
    │   ├── AutocompletePopup.java     # Componente genérico flotante de autocompletado desacoplado
    │   ├── dialogs/                   # ImportarProductosDialog, ManualUsuarioDialog, LicenciaDialog...
    │   ├── molecules/                 # SidebarItem, SidebarSection (Acordeón neumórfico con Preferences)
    │   └── organisms/                 # ModernSidebar, MetricCard, UIUtils
    └── [Pages]                        # DashboardPage, SalesPage, ProductPage, ComprasPage, CarteraPage...
```

---

## 🚀 Compilación, Pruebas y Despliegue

### Requisitos Previos
- JDK 25 o superior instalado y configurado en el `PATH`.
- Apache Maven 3.8+.

### Pipeline de Comandos Maven

```bash
# 1. Compilar fuentes Java
mvn clean compile

# 2. Ejecutar la suite completa de 41 pruebas automatizadas de integración/unidad
mvn test

# 3. Construir el paquete Shaded Fat-JAR de producción (Genera ejecutable en dist/)
mvn clean package -DskipTests=false

# 4. Ejecución del artefacto generado
java -jar dist/ERP-Plus-Business-2.0.0.jar

# 5. Generar paquete ejecutable portable oficial (Linux / Windows)
bash scripts/package-app.sh
```

---

## 📄 Mapa de Documentación Técnica

- 💻 [**Documentación Técnica General & Guía de Ingeniería**](docs/documentacion_tecnica.md) — Arquitectura detailed, patrones de diseño, pipeline de datos y suite de pruebas.
- 🗄️ [**Esquema de Base de Datos & Estrategia SQL**](docs/esquema_bd.md) — DDL oficial, llaves foráneas, índices, concurrencia WAL e hilos de Shutdown Hook.
- 📖 [**Diccionario de Datos**](docs/diccionario_datos.md) — Definición detallada campo por campo de las 9 tablas relacionales y reglas de snapshot.
- 🔌 [**API de Controladores & Contrato de Negocio**](docs/api_controladores.md) — Métodos de lectura/escritura, firmas, DTOs y manejo defensivo de excepciones.
- 📐 [**Arquitectura UI/UX & Design System**](docs/arquitectura_ui.md) — Desglose Atomic Design, tokens neón, breakpoints y componentes 2D.
- ⚙️ [**Manual de Mantenimiento & Onboarding para Desarrolladores**](docs/manual_mantenimiento.md) — Guía de extensión del sistema, ciclo de vida JVM y empaquetado nativo `jpackage`.
- 📘 [**Manual de Usuario de la App**](docs/manual_usuario.md) — Instructivo de operación comercial para el usuario final.
- 🗺️ [**Roadmap del Proyecto**](docs/roadmap/roadmap.md) — Historial de fases y planificación futura (Fase 8).

---

## 👨‍💻 Autoría & Propiedad Intelectual

Desarrollado para **ERP+ BUSINESS** por **Brandon Daza** — **ChopCode Solutions** ([Portafolio Oficial](https://portafolio-brandon-daza.web.app/)). Todos los derechos reservados.
