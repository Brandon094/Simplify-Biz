# Changelog — ERP+ Business

Todas las modificaciones, mejoras, nuevas funcionalidades y correcciones de seguridad de **ERP+ Business** están documentadas en este archivo siguiendo el estándar [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/) y la especificación [Semantic Versioning (SemVer)](https://semver.org/).

---

## [2.1.0] - 2026-09-17 — BI Analytics, Ergonomics & Autocomplete Release

### 🛠️ Correcciones & Mejoras de Ergonomía (POS Autocomplete & Client DB Refactoring)
- **Hover Aislado y Respuesta Inmediata en Botones del Carrito (`SalesPage.java`):** Incorporación de un listener `MouseMotionAdapter` en la tabla del carrito (`tbCart`) que activa automáticamente la edición de celda al deslizar el mouse sobre la columna de acciones. Se aislaron los efectos hover de los botones **+**, **-** y **Trash** para que cada uno responda de forma independiente con su color neón correspondiente (`NEON_GREEN`, `NEON_PURPLE`, `NEON_RED`) y se restablezca su estado neutro al cambiar de fila.
- **Definición de Columna Dedicada `no_cc` en Tabla `usuarios`:** Migración DDL y actualización del controlador para almacenar el número de Cédula/NIT en un campo nativo en texto plano para clientes (`rol = 2`), desacoplando la cédula del campo `contraseña`.
- **Eliminación Definitiva de Filtración de Hashes SHA-256:** Refactorización de la consulta `buscarClientesSugeridos()` para suprimir cualquier fuga de hashes de contraseñas de administradores/empleados en el popup de sugerencias flotantes del POS, garantizando sugerencias limpias y carga completa de datos de contacto (Cédula, Nombre, Teléfono y Correo).
- **Autocompletado Dual de Clientes (`AutocompletePopup<Usuario>`):** Integración del componente desacoplado de autocompletado en tiempo real en los campos de **Cédula/NIT** y **Nombre del cliente** en el POS (`SalesPage.java`), desplegando sugerencias flotantes al escribir sin requerir `Enter` o cambio de foco.
- **Eliminación de Interferencia por `focusLost`:** Remoción del listener `focusLost` en `txtClientCC` que provocaba cierres prematuros del popup desplegable al hacer clic en las sugerencias de la lista.
- **Reset Defensivo de Formulario de Clientes (`syncClientState`):** Garantía de limpieza atómica de los campos de teléfono, correo y restauración del color de texto por defecto (`ThemeConstants.TEXT_PRIMARY`) al alternar entre *Consumidor Final* y *Cliente Registrado* o al modificar el método de pago.
- **Mejora de UX & Guía Contextual en Registro de Administrador (`ModernAdminRegistrationPage.java`):** Incorporación de microcopy permanente y helper labels descriptivos debajo de todos los campos (Nombre, Teléfono, Correo y Contraseña), orientando al usuario durante la creación de la cuenta principal del negocio.
- **Autenticación Estandarizada por Primer Nombre o Correo (`UsuarioController.java` & `ModernLoginPage.java`):** Flexibilización del inicio de sesión permitiendo autenticarse ingresando únicamente el **primer nombre** del usuario (ej. `"Brandon"` para `"Brandon Daza"`), su **correo electrónico** o su **nombre completo**, preservando intacto el cifrado SHA-256 y almacenando el nombre real en la sesión.

### 🚀 Añadido (BI Analytics & Business Intelligence)
- **Filtro Anual Completo ("Este Año"):** Extensión del selector temporal del Dashboard a 4 dimensiones (Hoy, Esta Semana, Este Mes y Este Año).
- **Gráfico Comparativo Dual & Tasa de Crecimiento (`NeonLineChart`):** Renderizado vectorial 2D en tiempo real con línea continua neón para la serie actual y línea discontinua punteada para el periodo anterior (ej. *Este Mes vs Mes Anterior* o *Este Año vs Año Anterior*).
- **Badge Neón Dinámico (% vs Per. Anterior):** Cálculo automático del porcentaje de crecimiento o decrecimiento $$\Delta \%$$ desplegado en una etiqueta neón verde o rosa (`+18.5% vs per. anterior`).
- **Etiquetado Adaptativo de Eje X (`VentasController`):** Formateador inteligente que alterna horas (`08:00`), días/meses (`15 Jul`), meses (`Ene`, `Feb`) y años (`2024`, `2025`).
- **Formateo Compacto Inteligente de Moneda (`UIUtils.formatCompactCurrency`):** Estandarización de cifras grandes en tarjetas KPI (`$100K`, `$5.4M`, `$1.2B`) en Dashboard, Cartera y Reportes para evitar desbordamiento de texto, conservando la precisión contable exacta (`$ 128.450.000,00`) en tooltips y punto de venta (POS).
- **Rediseño Compacto de Tarjetas KPI (`MetricCard`, `CarteraPage`, `ReportsPage`):** Optimización de padding interno (insets reducidos a `10, 14`) y tipografía, otorgando mayor respiro visual y espacio para los gráficos y tablas centrales.
- **Dataset Maestro Demo Multiaño (3 Años - `poblar_master_demo.sql`):** Script SQL realista con 1,225+ ventas y 400+ abonos distribuidos entre 2024 y 2026 para pruebas exhaustivas de comparativas interanuales.

---

## [2.0.0] - 2026-09-16 — Enterprise Major Release

### 🚀 Añadido (Enterprise Release)
- **Endurecimiento de Licenciamiento Criptográfico RSA-2048 (`LicenciaManager` & `GeneradorLicenciaAdmin`):**
  - **Firma Criptográfica Estricta:** Eliminación de tokens simulados derivados (`ERPPRO-`) para evitar cualquier intento de falsificación o bypass. Las licencias ahora requieren una firma digital SHA256withRSA válida codificada en Base64.
  - **Generador de Licencias Comercial (`GeneradorLicenciaAdmin.java`):** Nueva herramienta ejecutable para el Administrador que genera y firma tokens con la clave privada de 2048 bits para licencias comerciales vitalicias ($800.000 COP / $1.000.000 COP) vinculadas al Hardware ID único del cliente.
- **Mejora Integral de UX en Punto de Venta (POS - SalesPage):**
  - **StockBadge Siempre Visible (`StockBadge.java`):** Componente atómico con dos estados (Neutro/Guía y Activo con semáforo verde/ámbar/rojo de disponibilidad) que elimina saltos de layout y orienta al usuario permanentemente.
  - **Labels y Textos Conversacionales:** Redacción amigable de etiquetas y helpers contextuales en cada campo (`Buscar producto`, `¿Cuántas unidades va a llevar?`, `Descuento (%)`), eliminando términos técnicos en mayúsculas para permitir un uso intuitivo sin consultar manuales.
  - **Placeholder Dinámico de Cantidad:** Muestra dinámicamente el límite permitido (`Max: N`) al seleccionar un producto para evitar ventas fallidas por falta de stock.
- **Flujo Dual de Importación Excel/CSV (`ExcelSQLiteManager`):**
  - Introducción del enum `ModoImportacion` (`CATALOGO` y `ABASTECIMIENTO`).
  - **Modo Catálogo:** Importación masiva e inteligente directamente a la base de datos de productos desde la pantalla de Inventario con estrategia Upsert por SKU (crea o actualiza existencias).
  - **Modo Abastecimiento:** Integración en `RegistrarCompraDialog`. Valida obligatoriamente Proveedor y Número de Factura antes de procesar el archivo Excel. Carga los productos directamente en la tabla de compras para su revisión antes de confirmar formalmente el ingreso a bodega.
- **Refactorización de Diálogos & Atomic Design:**
  - Consolidación y reubicación de todos los diálogos modales en la estructura de paquetes `views/components/dialogs/` (`ImportarProductosDialog`, `RegistrarCompraDialog`, `ManualUsuarioDialog`, `LicenciaDialog`, `RegistrarAbonoDialog`, `HistorialAbonosDialog`, `HistorialComprasClienteDialog`).
  - Eliminación de carpetas duplicadas e hiper-modularización del diseño UI/UX.
  - Diseño de footer fijo responsivo con barra de botones de acción (`Cargar`, `Confirmar`, `Cancelar`) y panel central con desplazamiento (`JScrollPane`) para evitar truncado en resoluciones bajas.
- **Refactorización Completa del Sistema de Color (DRY & ThemeConstants):**
  - Reemplazo de colores hardcodeados por tokens centralizados en `ThemeConstants` en todas las vistas (`ClientsPage`, `ProductPage`, `SellersPage`, `AutocompletePopup`, `UIUtils`, `CarteraPage`, `ReportsPage`, `ProvidersPage`, `SalesPage`, `ModernLoginPage`, `ModernAdminRegistrationPage`).
  - Renderizado vectorial dinamizado mediante `UIUtils.createIcon(path, color)`.
- **Modificación Dinámica de Cantidades en el Carrito (POS):**
  - Columna de **Acciones** por fila en la tabla del carrito con botones vectoriales SVG (`plus.svg`, `minus.svg`, `products.svg`, `trash.svg`) para incrementar `+1`, mermar `-1`, editar cantidad exacta o eliminar el ítem.
  - Reorganización en dos niveles dentro del footer del carrito para evitar superposiciones visuales.
- **Centro de Ayuda & Lector de Manual (`ManualUsuarioDialog`):** Lector e instructivo navegable con inicio automático en Sección 0 (Introducción), buscador por palabra clave y barra inferior fija de paginación.
- **Gestión de Licencias & Evaluación Demo 30 Días (`LicenciaDialog`):** Sistema criptográfico con cálculo de Hardware ID, botón de copia en 1 clic y token de activación `ERPPRO-XXXX-XXXX-XXXX`.
- **Ecosistema de Tooltips Inteligentes en Dashboard (4 Tarjetas KPI):**
  - **Ventas Totales:** Radiografía flotante con el desglose por métodos de pago (Efectivo en Caja, Transferencias digitales Nequi/Banco y Crédito Comercial / Fiado con montos y porcentajes).
  - **Utilidad Neta:** Radiografía ejecutiva de rentabilidad (Total Facturado, Costo COGS de Mercancía Vendida, Utilidad Neta Ganada y Retorno limpio por cada $1.000 vendidos).
  - **Inversión Inventario:** Radiografía de patrimonio en bodega (Abastecimiento por Facturas de Proveedores vs. Carga Directa / Catálogo Propietario).
  - **Stock Crítico:** Listado interactivo de los productos con existencias $\le 5$ unidades (destacando agotados en 0 unds).
  - **Permanencia Extendida (20s):** Configuración global de `ToolTipManager` con visibilidad activa de 20 segundos para lectura sin afanes.
- **Suite Completa de 45 Pruebas Automatizadas:** Cobertura integral de controladores, motor de base de datos, parsers y gestor de Excel con JUnit 5 pasando al 100%.

---

## [1.3.0] - 2026-09-14

### 🚀 Añadido (Added)
- **Licenciamiento Criptográfico Hardware-Bound (Fase 6 - Punto 1):** Sistema de verificación de licencias con cifrado asimétrico RSA-2048 y huella digital de hardware (`Hardware ID` de 16 caracteres formato `XXXX-XXXX-XXXX-XXXX`). Evaluación automática de período de prueba (30 días Demo) y activación PRO instantánea.
- **Diálogo Modal de Activación de Licencia (`LicenciaDialog`):** Modal de estética Cyberpunk Neón con visualización de Hardware ID, botón de copia al portapapeles en 1-clic, validación de token en tiempo real y feedback interactivo.
- **Visor de Manual de Usuario Integrado (`ManualUsuarioDialog`):** Visor interactivo dentro del software Swing con árbol navegable de capítulos, búsqueda en tiempo real, formato enriquecido e instrucciones paso a paso.
- **Módulo de Cartera y Cuentas por Cobrar (CxC):** Panel completo con KPIs de Cartera Pendiente, Recaudado Mes y Deudores Activos. Soporte para abonos parciales y totales vía `RegistrarAbonoDialog` e `HistorialAbonosDialog`.
- **Módulo de Compras y Entrada de Bodega (`ComprasPage`):** Gestión formal de abastecimiento con facturas de proveedor, incremento de stock atómico y actualización de costo de adquisición (`precio_costo`).
- **Autocompletado Genérico DRY (`AutocompletePopup<T>`):** Sugerencias emergentes en tiempo real e insensibles a mayúsculas/minúsculas para productos y clientes en POS e Inventario.
- **Exportación Ejecutiva a Excel Real (`.xlsx`):** Generación nativa con Apache POI incluyendo banner corporativo, encabezados `#1E293B`, formato de moneda `$#,##0.00` y fila de Gran Total.
- **Generación e Impresión Oficial a PDF:** Vista previa e impresión vectorial mediante `JTable.print(...)`.
- **Acordeón Inteligente de Navegación (`ModernSidebar`):** Menú lateral organizado por secciones temáticas colapsables con persistencia de preferencias de usuario (`Preferences`).

### 🎨 Cambiado (Changed)
- **Inteligencia de Negocio en Dashboard:** Incorporación de KPIs en tiempo real para **Utilidad Neta ($)** (ganancia real descontando costo de inventario vendido) e **Inversión en Bodega ($)**.
- **Gráficos Neón Avanzados (`NeonPieChart`, `NeonBarChart`):** Soporte para interacciones hover (*offset* de rebanadas, líneas conectoras e información flotante).
- **Estándar DRY en Tablas (`UIUtils.applyTableStyling`):** Ocultamiento de IDs, alineación derecha para moneda y centrado de códigos/fechas en todas las tablas del sistema.

### 🛡️ Fijado / Correcciones (Fixed)
- **Conexiones Concurrentes en SQLite:** Patrón Singleton implementado en `GestorConexion` eliminando bloqueos `database is locked`.
- **Sensibilidad de Búsqueda:** Búsquedas e insensibilidad a mayúsculas/minúsculas estandarizadas mediante `String.toLowerCase()` y `COLLATE NOCASE` en SQLite.

---

## [1.2.0] - 2026-09-10

### 🚀 Añadido (Added)
- **Gestión Dinámica de Categorías en SQLite:** Tabla `categorias` con actualización en tiempo real y selector con capacidad de aprendizaje de nuevas categorías.
- **Historial de Compras por Cliente (`HistorialComprasClienteDialog`):** Consulta master-detail de facturas e ítems adquiridos por cliente con resumen de ticket promedio.
- **Diseño Adaptativo / Layout Responsive (`LayoutResponsive`):** Breakpoints para Escritorio, Tablet y Móvil con navegación drawer hamburguesa.

### 🎨 Cambiado (Changed)
- **Tema Claro Alternable (☀️/🌙):** Selector de modo oscuro/claro centralizado en `ThemeConstants` con persistencia de preferencia.

---

## [1.1.0] - 2026-08-25

### 🚀 Añadido (Added)
- **Punto de Venta Simétrico (POS):** Carrito de compras, descuentos y selección rápida de métodos de pago (`Efectivo`, `Transferencia`, `Crédito`).
- **Control de Acceso por Roles (RBAC):** Separación de permisos entre Administrador (`1`) y Vendedor (`0`).

---

## [1.0.0] - 2026-08-01

### 🚀 Añadido (Added)
- Lanzamiento inicial de **ERP+ Business**.
- Arquitectura Swing MVC con almacenamiento local en SQLite.
- Dashboard de métricas principales, catálogo de productos y módulo de clientes.
