# Roadmap de ERP+ Business

Este documento define el orden recomendado para continuar el desarrollo. La regla principal es consolidar la experiencia antes de ampliar la lógica de negocio.

## Estado actual

### Completado en la iteración UI/UX

* [x] Identidad visual oscura y branding ERP+ Business.
* [x] **Tema claro alternable** mediante toggle ☀️/🌙 en el sidebar, con paletas centralizadas en `ThemeConstants` (principio DRY).
* [x] Login con panel moderno, SVG y flujo de acceso.
* [x] Ventana única con navegación lateral por rol.
* [x] Dashboard con KPIs, actividad operativa, gráficos, leyendas y estados vacíos.
* [x] Productos con formulario, iconos SVG, tabla y feedback sin registros.
* [x] Ventas con carrito, descuento, checkout, cliente genérico e iconos SVG.
* [x] Clientes convertidos en consulta de usuarios con rol `2`.
* [x] Empleados con registro y actualización para el administrador.
* [x] Reportes con filtros, acciones visuales, iconos SVG y feedback sin resultados.
* [x] Configuración con información técnica, SVG y enlace a `[https://portafolio-brandon-daza.web.app/](https://portafolio-brandon-daza.web.app/)`.
* [x] Proveedores con UI preparada, pero ocultos temporalmente del sidebar.
* [x] **Categorías Dinámicas en SQLite (v1.3.0):** Gestión de categorías en tabla dedicada `categorias` con combo editable que aprende nuevas categorías automáticamente sin código duro.
* [x] **Estándar DRY en Tablas (v1.3.0):** Implementación de `UIUtils.applyTableStyling` y `formatDate` en todas las tablas del sistema (Id oculto, alineación a derecha de moneda `$ 15.000,00`, fechas y códigos centrados).
* [x] **Autocompletado de Clientes y Venta Ágil (v1.3.0):** Rediseño del POS con venta rápida en Efectivo por defecto (2 clics a `CONSUMIDOR FINAL`) y autocompletado inteligente por Cédula / NIT en Transferencias y Crédito.
* [x] **Autocompletado Genérico DRY (`AutocompletePopup<T>`):** Componente reutilizable para campos `JTextField` implementado en **Punto de Venta** (búsqueda de productos y clientes) y en **Gestión de Inventario** (búsqueda y auto-rellenado de formulario de productos por Código o Nombre en tiempo real e insensible a mayúsculas/minúsculas).
* [x] **Flujo Estandarizado de Métodos de Pago:** Limpieza de métodos de pago en POS (`Efectivo`, `Transferencia` y `Crédito`). Asignación automática de estado en base de datos (`pago_confirmado = 'pagado'` para Efectivo/Transferencia y `'deudor'` para Crédito).
* [x] **Inteligencia de Negocio y Utilidad Neta (Fase 5 - v1.3.0):** Registro de `precio_costo` en productos y ventas. Tarjetas KPI dinámicas en el Dashboard con **Utilidad Neta Ganada ($)** (con % de margen real) e **Inversión Total en Inventario ($)**.
* [x] **Ecosistema de Iconografía SVG Completo (Fase 6 - v1.3.0):** Implementación integral de 46 iconos vectoriales FlatSVG (sin emojis) en la totalidad de diálogos, barras, métricas, botones y gráficos (`chart-line.svg`, `chart-pie.svg`, `info.svg`, `boxes-stacked.svg`, `triangle-exclamation.svg`, `shield-heart.svg`, etc.).
* [x] **Lector Interactivo de Manual de Usuario (`ManualUsuarioDialog`):** Diálogo navegable con inicio automático en Sección 0 (Introducción), buscador por palabra clave y **barral inferior fija de paginación** (`◄ Tema Anterior`, `Tema Siguiente ►`, `Cerrar`).
* [x] **Modal de Licenciamiento ERP+ (`LicenciaDialog`):** Diálogo interactivo con estado de suscripción (PRO/Demo), botón de copia de Hardware ID en 1 clic y campo de entrada de token de activación.
* [x] **Formulario de Login Robusto & Informativo (`ModernLoginPage`):** Rediseño en `GridBagLayout` con ancho garantizado de 420px anti-aplastamiento, 8 insignias de módulos con iconos SVG y telemetría de motor SQLite WAL.
* [x] **Top Header Dinámico con SVG (v2.0.1):** Barra superior centralizada que actualiza en tiempo real el título, subtítulo e icono SVG temático con color neón dedicado por módulo, maximizando el espacio de trabajo vertical.
* [x] **Menú Lateral Colapsable Horizontalmente (v2.0.1):** Contracción del `ModernSidebar` de 260px a 64px (+170px de espacio útil) con botón toggle `arrow-left.svg`/`arrow-right.svg`, centrado automático de iconos, tooltips y preferencia de acordeones respetada.
* [x] **Persistencia de Descuentos POS & Layout Ampliado (v2.0.1):** Migración DDL de la columna `descuento` en `detalles_venta`, persistencia atómica en `VentasController`, carrito ampliado al 44% de peso horizontal, 3 botones por fila en 1 sola línea (`+`, `-`, `🗑`) y feedback transparente en el footer (subtotal bruto, ahorro/descuento en `-$` y `%`, y total neto final).
* [x] **Motor Dual de Importación Excel/CSV & Consolidación Atomic Design (v2.0.0):**
  - Motor `ExcelSQLiteManager` parametrizado con enum `ModoImportacion` (`CATALOGO` y `ABASTECIMIENTO`).
  - Importación en Modo Catálogo desde Productos (Upsert directo en SQLite por SKU).
  - Importación en Modo Abastecimiento desde `RegistrarCompraDialog` (requiere Proveedor y Factura previa, precargando la tabla de compras para revisión visual antes de la confirmación).
  - Consolidación y reubicación de todos los diálogos en `views/components/dialogs/`.
  - Refactorización de todos los colores hardcodeados a tokens de `ThemeConstants`.
  - Suite de pruebas JUnit 5 en **42/42 pruebas automatizadas pasando al 100% (BUILD SUCCESS)**.
* [x] **Calculadora de Vueltas Exactas en POS (v2.0.3):** Integración de campo interactivo "Paga con ($)" con icono `wallet.svg` en la parte inferior del carrito de compras (`SalesPage`), realizando cálculo automático de vueltas exactas en verde neón o dinero faltante en rojo en tiempo real sin calculadoras externas.
* [x] Documentación técnica, funcional, de arquitectura y de base de datos totalmente actualizada para la **versión 2.0.3**.

---

## Fase 7: Asistente Inteligente de Importación Excel/CSV & Ergonomía POS

* [x] **Motor de Importación Flexible (`ExcelSQLiteManager.java`):**
  - Mapeo dinámico visual de columnas para adaptarse a cualquier formato de Excel o CSV de proveedores.
  - Vista previa en vivo de las primeras 5 filas antes de importar.
  - Estrategia inteligente de creación / actualización (Upsert por SKU).
  - Limpiador y formateador de divisas defensivo.
* [x] **Integración Multi-Módulo:**
  - Importación directa al catálogo en `ProductPage.java`.
  - Importación masiva de facturas de compra en `RegistrarCompraDialog.java`.
  - Generador de plantilla modelo descargable `.xlsx`.
* [x] **Rediseño Ergonométrico de Inventario y POS:**
  - Eliminación de la gráfica de pastel redundante en Productos para maximizar el espacio de la tabla.
  - Reubicación de la barra de botones bajo el título en una sola fila (`FlowLayout.LEFT`).
  - Botones de acción del carrito POS ampliados a 115px con puntero de mano y efectos hover.
* [x] **Suite de Pruebas Unitarias:**
  - `ExcelSQLiteManagerTest.java` para validar lectura, mapeo y limpieza de moneda.
  - Cobertura de 41 pruebas automatizadas al 100%.

---

## Fase 8: Funcionalidades Empresariales Avanzadas (Planificación Futura)

* [ ] **Integración con Lector de Código de Barras (Barcode Scanner HID / USB):**
  - Soporte de entrada rápida plug-and-play en el POS (`SalesPage`) e Inventarios mediante captura de eventos de teclado de escáner USB estándar (carácter terminador `Enter` / EAN-13 / Code128).
  - Búsqueda y adición instantánea al carrito al escanear el código de barras sin necesidad de tocar el teclado o mouse.
  - Generador visual de etiquetas de código de barras imprimibles en PDF para productos nuevos.
* [ ] **Facturación Electrónica & Integración Fiscal (DIAN / API Externa):**
  - Envío automático de facturas en formato XML firmado y código QR / CUFE.
  - Proveedor tecnológico mediante API REST síncrona.
* [ ] **Impresión Térmica Directa (Tickets POS / ESC POS):**
  - Driver nativo para impresoras de recibos de 80mm y 58mm por puerto USB / Bluetooth sin cuadro de diálogo del sistema operativo.
* [ ] **Cierre y Arqueo Diario de Caja (Corte Z / Arqueo):**
  - Registro de dinero inicial de apertura de caja.
  - Conteo físico de efectivo al cierre de turno con conciliación de diferencias y reporte en recibo.
* [ ] **Respaldos Automáticos en la Nube (Cloud Backup):**
  - Copia de seguridad cifrada del archivo `ventas.db` en Google Drive / Dropbox desde la pestaña de Configuración.

---

## Punto de continuidad

Antes de iniciar una nueva fase, ejecutar `mvn clean package`, abrir el JAR y revisar las pantallas afectadas. No cambiar el esquema SQLite sin respaldar la base de datos y actualizar el esquema y diccionario en la misma tarea.



* [x] Soporte de tema oscuro y claro con paletas centralizadas.
* [x] **Implementar Microcopy de contexto:** Añadir subtítulos descriptivos debajo del título principal de cada módulo (ej. "Administra tu catálogo y mantén el control de tu stock").
* [x] **Añadir Helper Texts (Textos de ayuda):** Incorporar textos pequeños y permanentes debajo de campos clave en los formularios (ej. debajo de contraseñas, códigos SKU o descuentos).
* [x] **Integrar Tooltips:** Configurar globos de ayuda al pasar el cursor sobre botones de acción con iconos SVG (ej. "Generar PDF", "Cambiar ruta").
* [x] **Hacer accionables los Estados Vacíos:** Cambiar los mensajes pasivos por textos conversacionales que guíen al usuario y agregar botones de "Llamado a la Acción" (CTA) donde aplique.
* [x] Unificar validaciones visuales y mensajes de error (controladores migrados a `UIUtils`/`UIMessages`).
* [x] Añadir feedback de carga, éxito y error donde falte (ej. Skeletons o Shimmers para ocultar tiempos de carga).
* [x] Revisar tamaños mínimos, paddings y comportamiento en distintas resoluciones para dar "respiro visual" (mínimo de ventana ajustado a 1120x700).
* [x] Eliminar emojis restantes de las vistas (verificado: sin emojis en `src/`).
* [x] Probar accesibilidad básica: foco, contraste y navegación por teclado (anillos de foco y navegación por teclado en sidebar y botones).
* [x] Persistir la preferencia de tema del usuario (recordar oscuro/claro entre reinicios).

## Fase 2: Diseño Responsive (Experiencia adaptable tipo móvil)

> **Objetivo:** que la aplicación se adapte con elegancia a ventanas pequeñas y pantallas
> reducidas (hasta ~360px de ancho lógico), con el mismo lenguaje visual. Como es una app
> de escritorio Java Swing y no una app web, "responsive" aquí significa **layout fluido y
> adaptativo**: reflow de paneles según el ancho, componentes que se apilan en vertical,
> navegación colapsable y tipografía/paddings escalables — la misma sensación que una app
> de celular, dentro del escritorio.

* [x] **Añadir un gestor de breakpoints:** clase `LayoutResponsive` con umbrales (móvil `< 480px`,
  tablet `480–960px`, escritorio `> 960px`) y listener de redimensionado.
* [x] **Sidebar colapsable:** en móvil/tablet la navegación lateral (260px) se oculta y se controla
  con un botón hamburguesa en el header; al navegar el drawer se cierra solo.
* [x] **Formularios a ancho fluido:** paneles de formulario de Productos, Empleados y Proveedores
  pasan de ancho fijo (350–380px) a 100% del ancho en móvil (se apilan arriba).
* [x] **Reflow vertical en móvil:** Ventas (`1x3`) y Dashboard (`1x4` y `1x2`) se apilan en una
  sola columna cuando el ancho no alcanza.
* [x] **Look & feel táctil:** `NeonButton` con altura mínima de 44px y filas de tabla a 36px
  (`ThemeConstants.TOUCH_TARGET_MIN` / `TABLE_ROW_HEIGHT`).
* [x] **Tipografía y paddings escalables:** clase `UIUtils.WrappingLabel` (basada en `JTextArea`
  con wrap nativo) que envuelve títulos y subtítulos sin recortarse, y paddings adaptativos en
  login/registro y header de cada módulo.
* [x] **Ajuste de formularios que se cortaban:** reflow de filtros y botones en Reportes; filas
  "etiqueta/valor" apiladas y campo+botón reordenados en Configuración; estado vacío con
  `UIUtils.ScrollablePanel` para ajustarse al viewport (elimina scroll horizontal y cortes de
  texto). Verificado en Productos, Clientes, Reportes, Configuración, Empleados y Proveedores.
* [x] **Tablas adaptables:** permitir scroll horizontal y/o vista de tarjetas (card list) en
  pantallas estrechas para las tablas de productos, ventas y reportes (pendiente: las tablas ya
  tienen scroll, falta la vista de tarjetas).
* [x] **Login y registro adaptables:** `ModernLoginPage` y `ModernAdminRegistrationPage` diseñados en `GridBagLayout` con panel de marca fluido y tarjeta de inicio de sesión fija de 420px.
* [x] **Probar en resoluciones objetivo:** verificado con capturas offscreen a 380px (móvil) y
  1366/1400px (escritorio) en todas las vistas principales.

## Fase 3: Correcciones técnicas prioritarias (Core & Estabilidad)

* [x] **Implementar Patrón Singleton en Base de Datos:** Crear clase `GestorConexion` para evitar la apertura múltiple de conexiones a SQLite y eliminar el spam de logs.
* [x] Refactorizar DAOs para usar la conexión única y prevenir el error `database is locked`.
* [x] Revisar la composición de `db.path` y el nombre real del archivo SQLite (`db.db`).
* [x] Separar mensajes de UI de los controladores para facilitar pruebas (`ResultadoOperacion`).
* [x] Añadir pruebas automatizadas a usuarios, productos y ventas (JUnit 5 en `src/test/java`).
* [x] Revisar validaciones de números, fechas, cantidades y duplicados (evitar cierres inesperados por `NumberFormatException`).
* [x] Evitar mostrar información sensible como contraseñas en las tablas de UI (columna eliminada de los modelos).

## Fase 4: Módulos pendientes

* [x] **Crear persistencia real para proveedores (v1.3.0):** Tabla `proveedores` en SQLite con operaciones CRUD y autocompletado en compras.
* [x] **Implementar compras y entrada de inventario (v1.3.0):** Módulo `ComprasPage.java` con modal `RegistrarCompraDialog`, incremento atómico de stock en bodega (`cantidad = cantidad + ?`), actualización del costo de adquisición (`precio_costo`) y resumen KPI.
* [x] **Completar exportación Excel y PDF (v1.3.0):** Exportación Excel ejecutiva nativa con Apache POI (`.xlsx`) y exportación PDF/Impresión oficial vectorial vía `JTable.print`.
* [x] **Añadir historial de compras por cliente (v1.3.0):** Consulta de facturas y desglose de ítems comprados mediante el modal `HistorialComprasClienteDialog` con resumen KPI (Total Comprado, Compras Realizadas, Ticket Promedio) y master-detail interactivo.
* [x] **Módulo de Cartera y Cuentas por Cobrar (CxC - v1.3.0):**
  - Panel `CarteraPage.java` con KPIs de Cartera Pendiente, Recaudado Mes y Deudores Activos.
  - Tabla de ventas a crédito en estado `'deudor'`.
  - Nueva tabla SQLite `abonos_cartera` para registro de abonos parciales o totales.
  - Actualización automática de estado a `'pagado'` al saldar el 100% de la deuda.
  - Diálogos modales `RegistrarAbonoDialog` e `HistorialAbonosDialog` para abonos e historial transaccional.
* [x] **Búsqueda y sugerencias en tiempo real (v1.3.0):** Autocompletado genérico (`AutocompletePopup<T>`) en POS e Inventario.

## Fase 5: Inteligencia de negocio

* [x] **Utilidades reales (venta menos costo):** Cálculo de Utilidad Neta acumulada y porcentaje de margen real sobre ventas en el Dashboard y en el Centro de Reportes.
* [x] **Inversión en inventario:** Sumatoria en tiempo real del valor del stock en bodega a precio de costo.
* [x] **Alertas de stock crítico:** Indicador destacado en el Dashboard para ítems con 5 unidades o menos.
* [x] **Distribución por categorías:** Gráfico de torta/anillo responsivo `NeonPieChart` con datos dinámicos de SQLite.
* [x] **Comparación de períodos en dashboard:** Selector dinámico (`Histórico Total`, `Hoy`, `Últimos 7 Días`, `Este Mes`) que recalculan métricas y gráficos en tiempo real.
* [x] **Tarjetas de Resumen Ejecutivo en Centro de Reportes:** Métricas dinámicas en tiempo real en `ReportsPage.java` para Total Facturado, Costo COGS, Utilidad Neta & Margen %, y Desglose por Métodos de Pago (Efectivo / Transferencia / Crédito).
* [x] **Reportes con exportación estilizada a Excel (.xlsx):** Generación de archivos Microsoft Excel reales mediante Apache POI con formato ejecutivo, banner corporativo, encabezados en azul oscuro `#1E293B`, formato `$#,##0.00` y fila final de Gran Total.
* [x] **Generación e Impresión Oficial a PDF:** Impresión nativa y exportación PDF vectorial mediante `JTable.print(...)`.
* [x] **Script de Poblamiento Maestro de Demostración (`poblar_master_demo.sql`):** Script SQL autocontenido con esquema, usuarios, categorías, 34 productos con precios de costo/venta y 8 ventas distribuidas en 7 días para demostración comercial.


## Fase 6: Distribución e Iconografía Completa

* [ ] **Endurecimiento de Licenciamiento RSA-2048 & Script Admin:**
  - Desactivar validación simple `ERPPRO-` para evitar derivación inversa del algoritmo.
  - Exigir firma criptográfica RSA-2048 estricta vinculada al `HardwareId` del cliente.
  - Crear herramienta/script externo de administración para firma de licencias comerciales vitalicias de $800.000 COP / $1.000.000 COP con datos de cliente, HWID y fecha de vencimiento (`2099-12-31`).
* [x] **Licenciamiento Criptográfico Hardware-Bound:** Generación de Hardware ID, firma RSA-2048, evaluación de período Demo (30 días) y diálogo modal `LicenciaDialog`.
* [x] **Versionado y Notas de Publicación:** Actualización SemVer a v1.3.0 y creación del archivo oficial `CHANGELOG.md`.
* [x] **Manual de Usuario Integrado:** Visor e interactivo de manual operativo dentro de la app (`ManualUsuarioDialog`) con búsqueda en tiempo real, arranque automático en Sección 0 y **paginador inferior fijo** sin necesidad de desplazamientos laterales.
* [x] **Ecosistema SVG Unificado sin Emojis:** Todos los módulos, diálogos, gráficos y tarjetas del Dashboard utilizan iconos vectoriales SVG con acento neón.
* [x] **Empaquetado y Pruebas 100% Exitosas:** Suite completa de 36 pruebas automatizadas en JUnit 5 pasando al 100% y empaquetado JAR ejecutable en `dist/Simplify-Biz-1.3.0.jar`.

## Punto de continuidad

Antes de iniciar una nueva fase, ejecutar `mvn clean package`, abrir el JAR y revisar las pantallas afectadas. No cambiar el esquema SQLite sin respaldar la base de datos y actualizar el esquema y diccionario en la misma tarea.