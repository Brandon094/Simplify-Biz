# Changelog — ERP+ Business

Todas las modificaciones, mejoras, nuevas funcionalidades y correcciones de seguridad de **ERP+ Business** están documentadas en este archivo siguiendo el estándar [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/) y la especificación [Semantic Versioning (SemVer)](https://semver.org/).

---

## [2.0.0] - 2026-09-14 — Enterprise Major Release

### 🚀 Añadido (Enterprise Release)
- **Modificación Dinámica de Cantidades en el Carrito (POS):**
  - Columna de **Acciones** por fila en la tabla del carrito con botones vectoriales SVG (`plus.svg`, `minus.svg`, `products.svg`, `trash.svg`) para incrementar `+1`, mermar `-1`, editar cantidad exacta o eliminar el ítem.
  - Reorganización en dos niveles dentro del footer del carrito (Línea divisoria `JSeparator`, barra de acciones globales y fila independiente de `TOTAL VENTA`) para evitar superposiciones.
  - Validación dinámica contra el stock real de inventario en tiempo real.

### Added / Añadido
- **Versión Major 2.0.0 (Enterprise Ready):** Liberación oficial de arquitectura empresarial con suite completa de 9 módulos de negocio.
- **Ecosistema de Iconografía Vectorial SVG (46 Iconos):** Sustitución total de emojis por iconos vectoriales FlatSVG con filtrado dinámico neón (incluyendo `info.svg`, `barcode.svg`, `boxes-stacked.svg`, `wallet.svg`, `ticket.svg`, `xmark.svg`, etc.).
- **Módulo Completo de Proveedores (`ProvidersPage.java`):** Gestión integral CRUD de casas comerciales con autocompletado en tiempo real en la entrada de facturas de compra.
- **Centro de Ayuda & Lector de Manual (`ManualUsuarioDialog`):** Lector e instructivo navegable con inicio automático en Sección 0 (Introducción), buscador por palabra clave y barra inferior fija de paginación.
- **Gestión de Licencias & Evaluación Demo 30 Días (`LicenciaDialog`):** Sistema criptográfico con cálculo de Hardware ID, botón de copia en 1 clic y token de activación `ERPPRO-XXXX-XXXX-XXXX`.
- **Navegación con Acordeón Inteligente (`SidebarSection`):** Grupos de opciones colapsables con tarjetas neumórficas, indicador de dirección (`▲`/`▼`) y persistencia automática del estado mediante `Preferences`. Swing con árbol navegable de capítulos, búsqueda en tiempo real, formato enriquecido e instrucciones paso a paso.
- **Módulo de Cartera y Cuentas por Cobrar (CxC):** Panel completo con KPIs de Cartera Pendiente, Recaudado Mes y Deudores Activos. Soporte para abonos parciales y totales vía `RegistrarAbonoDialog` e `HistorialAbonosDialog`.
- **Módulo de Compras y Entrada de Bodega (`ComprasPage`):** Gestión formal de abastecimiento con facturas de proveedor, incremento de stock atómico y actualización de costo de adquisición (`precio_costo`).
- **Autocompletado Genérico DRY (`AutocompletePopup<T>`):** Sugerencias emergentes en tiempo real e insensibles a mayúsculas/minúsculas para productos y clientes en POS e Inventario.
- **Exportación Ejecutiva a Excel Real (`.xlsx`):** Generación nativa con Apache POI incluyendo banner corporativo, encabezados `#1E293B`, formato de moneda `$#,##0.00` y fila de Gran Total.
- **Generación e Impresión Oficial a PDF:** Vista previa e impresión vectorial mediante `JTable.print(...)`.

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
