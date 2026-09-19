# ERP+ BUSINESS — Next-Gen Enterprise POS & Inventory Platform

> **Plataforma ERP de Escritorio de Alto Rendimiento para Pequeñas y Medianas Empresas**  
> Solución integral de gestión comercial desarrollada bajo estándar de ingeniería de Silicon Valley: arquitectura desacoplada MVC, interfaz responsiva Atomic Design con estética Cyberpunk/Neon (FlatLaf), motor de persistencia relacional transaccional SQLite WAL, motor de importación masiva inteligente dual Excel/CSV (Inventario y Abastecimiento) y analítica financiera en tiempo real.

![Java 25](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk)
![SQLite WAL Mode](https://img.shields.io/badge/SQLite_3.46-WAL_Mode-blue?style=for-the-badge&logo=sqlite)
![FlatLaf Cyberpunk](https://img.shields.io/badge/FlatLaf-3.5.1_Cyberpunk-purple?style=for-the-badge)
![Maven 3.8+](https://img.shields.io/badge/Maven-3.8+-red?style=for-the-badge&logo=apachemaven)
![JUnit 5 Passed](https://img.shields.io/badge/Tests-46%2F46_Passed-brightgreen?style=for-the-badge&logo=junit5)
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
- **[X] Fase 7 — Motor Dual de Importación Excel/CSV & UX POS Ergonométrica:** Engine `ExcelSQLiteManager` desacoplado que soporta dos modos de operación (`ModoImportacion.CATALOGO` y `ModoImportacion.ABASTECIMIENTO`). En Abastecimiento valida previamente Proveedor y Factura, precargando la orden para revisión visual antes del ingreso formal a bodega.
- **[X] Fase 8 — BI Analítico Avanzado, Ergonomía POS & Formateo Compacto:**
  - **Microcopy & Guía Contextual en Registro de Administrador (`ModernAdminRegistrationPage.java`):** Implementación de etiquetas de ayuda permanentes y explicativas bajo todos los campos del formulario de onboarding inicial.
  - **Autocompletado Dual de Clientes (`AutocompletePopup<Usuario>`):** Integración del componente flotante de autocompletado en los campos de **Cédula/NIT** y **Nombre del cliente** en el POS (`SalesPage.java`), desplegando sugerencias reactivas al escribir sin requerir `Enter` o cambio de foco.
  - **Desacoplamiento de Eventos por Foco:** Remoción de la dependencia de `focusLost` directo en `txtClientCC`, garantizando una selección de sugerencias fluida y sin bloqueos de interfaz.
  - **Reset Defensivo de Formulario:** Limpieza atómica de datos y restauración del color de texto por defecto (`TEXT_PRIMARY`) al alternar entre *Consumidor Final* y *Cliente Registrado*.
  - **Tooltips Radiográficos Adaptativos:** Radiografías flotantes en tarjetas KPI adaptadas dinámicamente al tema claro/oscuro.
  - **Formateo Compacto Inteligente de Moneda (`UIUtils.formatCompactCurrency`):** Estandarización de cifras grandes en tarjetas KPI de Dashboard, Cartera y Reportes (`$100K`, `$5.4M`, `$1.2B`) para evitar desbordamientos visuales, conservando la precisión contable completa (`$ 128.450.000,00`) en tooltips y punto de venta (POS).
  - **Filtro Anual Completo ("Este Año"):** Extensión del selector temporal a 4 dimensiones (Hoy, Esta Semana, Este Mes, Este Año).
  - **Gráfico Comparativo Dual & Badge Neón (% vs Per. Anterior):** Renderizado vectorial en `NeonLineChart` con serie discontinua trazada punto a punto del periodo previo y badge neón reactivo con tasa de crecimiento/decrecimiento (`+18.5% vs per. anterior`).
  - **Etiquetado Inteligente de Eje X:** Formateador inteligente que alterna etiquetas de días (`15 Jul`), meses (`Ene`, `Feb`) y años (`2024`, `2025`).
  - **Dataset Maestro Demo Multiaño (3 Años):** Script SQL con 1,225+ ventas realistas y 400+ abonos distribuidos entre 2024 y 2026.
  - **Suite de Pruebas Automatizadas:** 46 tests unitarios e integrales en JUnit 5 pasando al 100%.

---

## 🛠️ Stack Tecnológico & Dependencias

| Componente | Tecnología | Versión / Especificación |
| :--- | :--- | :--- |
| **Lenguaje Base** | Java Development Kit (JDK) | **Java 25+** (Soporte en tiempo de ejecución) |
| **GUI Framework** | Java Swing + FlatLaf | **FlatLaf 3.5.1** (Themes MacDark / MacLight) |
| **Vectorial SVG** | FlatSVG / JSVG | **JSVG 1.4.0** / **FlatLaf Extras 3.5.1** (51 SVG Icons) |
| **Motor de Base de Datos** | SQLite JDBC | **SQLite 3.46.1.0** (Embebido, WAL Mode, Foreign Keys) |
| **Procesamiento Hojas de Cálculo**| Apache POI | **Apache POI 5.2.3** (OOXML `.xlsx` export/import) |
| **Testing Automatizado** | JUnit 5 | **JUnit Jupiter 5.10.0** (45 Tests unitarios/integración) |
| **Build Manager** | Apache Maven | **Maven 3.8+** (Plugins Shader, Compiler, Antrun) |

---

## 📐 Estructura del Repositorio (Atomic Design & MVC)

```text
com.mycompany.zl_solucion_integral/
├── Main.java                          # Punto de entrada de la aplicación JVM
├── config/                            # Infraestructura, Singleton DB, Seguridad y Parsers
│   ├── ConexionDB.java                # DDL, auto-migraciones de esquemas (`migrarColumnaSegura`)
│   ├── GestorConexion.java            # Singleton de conexión JDBC con PRAGMA WAL & Busy Timeout
│   ├── SelecionRuta.java              # Gestión de paths por SO y persistencia en `config.properties`
│   ├── Seguridad.java                 # Cifrado defensivo SHA-256
│   ├── Validaciones.java              # Parsers numéricos y limpiador regex de divisas
│   ├── ResultadoOperacion.java        # DTO inmutable de transporte de respuestas
│   └── ExcelSQLiteManager.java        # Motor dual de procesamiento masivo Excel/CSV (Catálogo y Abastecimiento)
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
│   └── AbonoCartera.java
└── views/                             # Capa de Presentación (Atomic Design Swing)
    ├── components/
    │   ├── atoms/                     # NeonButton, RoundedPanel, NeonLineChart, NeonBarChart, NeonPieChart
    │   ├── AutocompletePopup.java     # Componente genérico flotante de autocompletado desacoplado
    │   ├── dialogs/                   # ImportarProductosDialog, ManualUsuarioDialog, LicenciaDialog, RegistrarCompraDialog...
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

# 2. Ejecutar la suite completa de 46 pruebas automatizadas de integración/unidad
mvn test

# 3. Generar una Licencia Comercial RSA-2048 (Herramienta Admin / Privada)
mvn compile exec:java -Dexec.mainClass="com.mycompany.zl_solucion_integral.tools.GeneradorLicenciaAdmin"

# 4. Construir el paquete Shaded Fat-JAR de producción (Genera ejecutable en dist/)
mvn clean package -DskipTests=false

# 4. Ejecución del artefacto generado
java -jar dist/ERP-Plus-Business-2.0.0.jar

# 5. Generar paquete ejecutable portable oficial (Linux / Windows)
bash scripts/package-app.sh
```

---

## 📄 Mapa de Documentación Técnica

- 💻 [**Documentación Técnica General & Guía de Ingeniería**](docs/documentacion_tecnica.md) — Arquitectura detallada, patrones de diseño, pipeline de datos y suite de pruebas.
- 🗄️ [**Esquema de Base de Datos & Estrategia SQL**](docs/esquema_bd.md) — DDL oficial, llaves foráneas, índices, concurrencia WAL e hilos de Shutdown Hook.
- 📖 [**Diccionario de Datos**](docs/diccionario_datos.md) — Definición detallada campo por campo de las 9 tablas relacionales y reglas de snapshot.
- 🔌 [**API de Controladores & Contrato de Negocio**](docs/api_controladores.md) — Métodos de lectura/escritura, firmas, DTOs y manejo defensivo de excepciones.
- 📐 [**Arquitectura UI/UX & Design System**](docs/arquitectura_ui.md) — Desglose Atomic Design, tokens neón, breakpoints y componentes 2D.
- ⚙️ [**Manual de Mantenimiento & Onboarding para Desarrolladores**](docs/manual_mantenimiento.md) — Guía de extensión del sistema, ciclo de vida JVM y empaquetado nativo `jpackage`.
- 📘 [**Manual de Usuario de la App**](docs/manual_usuario.md) — Instructivo de operación comercial para el usuario final.
- 🗺️ [**Roadmap del Proyecto**](docs/roadmap/roadmap.md) — Historial de fases y planificación futura (Fase 8+).

---

## 👨‍💻 Autoría & Propiedad Intelectual

Desarrollado para **ERP+ BUSINESS** por **Brandon Daza** — **ChopCode Solutions** ([Portafolio Oficial](https://portafolio-brandon-daza.web.app/)). Todos los derechos reservados.
