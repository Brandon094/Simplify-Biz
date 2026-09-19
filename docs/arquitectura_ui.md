# Arquitectura de UI/UX - ERP+ Business

Este documento detalla el sistema de diseño, la estructura de componentes Swing y la arquitectura visual implementada en la aplicación ERP+ Business.

---

## 1. Filosofía de Diseño

ERP+ Business adopta una estética **Neon/Glassmorphism Cyberpunk**, priorizando la jerarquía visual, la claridad operacional y la velocidad de trabajo diaria.

### Principios Fundamentales
1. **Consistencia Total (DRY Visual):** Todos los colores, fuentes, dimensiones y formateos derivan centralizadamente de `ThemeConstants` y `UIUtils`.
2. **Respiro Visual y Accesibilidad:**
   - Blancos y paddings adaptativos de mínimo 15–30px.
   - Objetivos táctiles/clicables mínimos de `44px` de altura (`ThemeConstants.TOUCH_TARGET_MIN`).
   - Filas de tabla de `36px` (`ThemeConstants.TABLE_ROW_HEIGHT`).
   - Anillos de foco explícitos para navegación por teclado.
3. **Alternancia de Tema sin Reinicio:** Soporte nativo para paleta oscura (predeterminada) y clara.
4. **Formateo de Moneda Compacto Inteligente (`UIUtils.formatCompactCurrency`):** Estandarización visual en tarjetas KPI (`$100K`, `$5.4M`, `$1.2B`) evitando desbordamientos de texto, manteniendo la precisión contable completa en tooltips y tablas (`$ 128.450.000,00`).

---

## 2. Sistema de Temas (`ThemeConstants`)

`ThemeConstants` administra el estado global del tema gráfico mediante `isDark()` y `setDark(boolean)`.

### Paletas Comparativas

| Token | Constante | Modo Oscuro | Modo Claro | Uso |
| :--- | :--- | :--- | :--- | :--- |
| **BACKGROUND** | `BACKGROUND` | `#0B0E14` | `#EEF2F7` | Fondo principal del viewport |
| **SIDEBAR** | `SIDEBAR_BACKGROUND` | `#121620` | `#FFFFFF` | Fondo del menú lateral y encabezados de tabla |
| **CARD** | `CARD_BACKGROUND` | `#1E293B` | `#FFFFFF` | Fondo de contenedores redondeados |
| **INPUT BG** | `INPUT_BACKGROUND` | `#0F172A` | `#F8FAFC` | Fondo de campos de texto y combos |
| **INPUT BORDER** | `INPUT_BORDER` | `#334155` | `#CBD5E1` | Borde de cajas de texto |
| **ZEBRA TABLE** | `TABLE_ZEBRA` | `#162032` | `#F1F5F9` | Filas alternadas de las tablas |
| **TEXT PRIMARY** | `TEXT_PRIMARY` | `#FFFFFF` | `#0F172A` | Títulos y etiquetas principales |
| **TEXT SECONDARY** | `TEXT_SECONDARY` | `#94A3B8` | `#475569` | Subtítulos, helper texts y placeholders |
| **NEON PURPLE** | `NEON_PURPLE` | `#A855F7` | `#7C3AED` | Acento primario, marca y botones destacados |
| **NEON BLUE** | `NEON_BLUE` | `#3B82F6` | `#2563EB` | Acciones secundarias e información |
| **NEON GREEN** | `NEON_GREEN` | `#22C55E` | `#16A34A` | Éxito, confirmación de venta y guardado |
| **NEON CYAN** | `NEON_CYAN` | `#06B6D4` | `#0891B2` | Gráficos, métricas e inventarios |

---

## 3. Estructura Atomic Design

La capa de vistas (`com.mycompany.zl_solucion_integral.views`) organiza sus componentes Swing inspirándose en Atomic Design:

```text
views/
  ├── components/
  │   ├── atoms/
  │   │   ├── NeonButton.java           Botón neumórfico con bordes redondeados, brillo y filtro SVG
  │   │   ├── RoundedPanel.java         Contenedor con esquinas redondeadas ajustables (radius)
  │   │   ├── NeonLineChart.java        Gráfico de líneas en Graphics2D con curva suave, grid e icono SVG de encabezado alineado
  │   │   ├── NeonBarChart.java         Gráfico de barras neón comparativo (Ventas, Utilidad Neta e Inversión Bodega) con animación hover, icono SVG y valores contables
  │   │   ├── NeonPieChart.java         Gráfico de dona neón interactivo (Top 5 + "OTROS", hover offset, conector neón, leyenda e icono SVG de encabezado)
  │   │   └── ThemeToggleButton.java    Botón toggle (☀️/🌙) que conmuta la paleta en caliente
  │   ├── AutocompletePopup.java        Componente genérico DRY de autocompletado y sugerencias flotantes para JTextFields.
  │   ├── dialogs/
  │   │   ├── ImportarProductosDialog.java Asistente modal dual en 4 pasos para importación y mapeo dinámico de Excel/CSV a la base de datos (Catálogo y Abastecimiento).
  │   │   ├── RegistrarCompraDialog.java  Modal de registro de facturas de abastecimiento a proveedores con validación de factura, autocompletado de proveedor e importación masiva.
  │   │   ├── ManualUsuarioDialog.java  Centro de Ayuda e instructivo navegable con buscador, inicio en Sección 0 y paginador inferior fijo.
  │   │   ├── LicenciaDialog.java       Modal de licenciamiento con estatus (Demo/PRO), copia de Hardware ID y caja de token activable.
  │   │   ├── RegistrarAbonoDialog.java Modal para ingresar abonos a deudas de ventas a crédito con resumen financiero.
  │   │   ├── HistorialAbonosDialog.java Modal para visualizar la bitácora cronológica de pagos parciales realizados.
  │   │   └── HistorialComprasClienteDialog.java Modal master-detail con KPIs para visualizar compras por cliente.
  │   ├── molecules/
  │   │   ├── SidebarItem.java          Fila de navegación con icono SVG, etiqueta y colapso visual centrado.
  │   │   └── SidebarSection.java       Sección de acordeón colapsable/expandible con indicador visual (▲/▼), hover neón y persistencia automática del estado de expandido/comprimido.
  │   └── organisms/
  │       ├── ModernSidebar.java        Menú lateral colapsable horizontalmente (260px <-> 64px) con botón toggle `arrow-left.svg`/`arrow-right.svg`, secciones desplegables, perfil de usuario y toggle de tema.
  │       └── MetricCard.java           Tarjeta KPI con icono, valor numérico y tendencia
  └── [Páginas] (DashboardPage, ProductPage, SalesPage, ComprasPage, SellersPage, ClientsPage, ReportsPage, CarteraPage, ConfigPage)
```

---

## 4. Diseño Responsive y Adaptativo (`LayoutResponsive`)

`LayoutResponsive` registra listeners sobre las pantallas para reflow automático de componentes según el ancho disponible de la ventana:

### Breakpoints Definidos

- **Móvil (`< 480px`):** Ancho fluido 100%. Formularios se apilan verticalmente sobre las tablas. Menú lateral colapsable controlado por botón de hamburguesa.
- **Tablet (`480px – 960px`):** Layout mixto. Formularios e indicadores se reordenan.
- **Escritorio (`> 960px`):** Panel en 3 columnas laterales con proporciones optimizadas: Búsqueda (28%), Carrito Ampliado (44%) y Checkout (28%).

---

## 5. Iconografía y Top Header Dinámico

### Iconografía SVG Vectorial (51 iconos)
Todos los iconos son archivos SVG alojados en `src/main/resources/icons/` (incluyendo `arrow-left.svg`, `arrow-right.svg`, `dashboard.svg`, `cart-shopping.svg`, `wallet.svg`, `boxes-stacked.svg`, `suppliers.svg`, `clients.svg`, `staff.svg`, `reports.svg`, `settings.svg`). Se cargan mediante `FlatSVGIcon` aplicando un `ColorFilter` dinámico según el estado y la paleta de color neón temático (`NEON_PURPLE`, `NEON_CYAN`, `NEON_GREEN`, `NEON_BLUE`).

### Top Header Dinámico (`MainTemplate`)
Barra de encabezado superior unificada que elimina la duplicidad visual en cada página. Actualiza en tiempo real el título, subtítulo de contexto e icono SVG temático de 22x22px con filtro de color neón correspondiente al módulo activo, maximizando el espacio de trabajo vertical.

---

## 6. Módulo de Ventas (POS 2.0) — Layout Ampliado & Feedback de Descuento (`SalesPage`)

- **Proporción Ampliada del Carrito (44%):** El contenedor del carrito cuenta con mayor amplitud horizontal para albergar la tabla de productos y desplegar los 3 botones de acción por fila (`+`, `-`, `🗑`) alineados en una sola línea sin solapamientos.
- **Feedback Transparente de Descuentos:** El footer del carrito detalla en tiempo real:
  - **Subtotal sin descuento** (bruto acumulado).
  - **Ahorro / Descuento total** en neón cyan (`-$` ahorrado y `%` efectivo).
  - **TOTAL VENTA final** (neto cobrado).
- **Venta Ágil sin Fricción (`syncClientState` & `AutocompletePopup`):** Gestión centralizada del estado del cliente basada en el método de pago seleccionado.
  - En **Efectivo** u **Otro**, los campos de cliente se ocultan dinámicamente (`clientFieldsContainer.setVisible(false)`) permitiendo la venta inmediata en 2 clics a `CONSUMIDOR FINAL`.
  - En **Transferencia** o **Crédito**, se despliegan automáticamente los campos con autocompletado en tiempo real mediante `AutocompletePopup<Usuario>` integrado simultáneamente en los campos de **Cédula/NIT** (`txtClientCC`) y **Nombre del cliente** (`txtClientName`).
  - **Experiencia sin bloqueos por foco:** Selección fluida de sugerencias sin cierres prematuros al remover la dependencia del evento `focusLost`.
  - **Reset defensivo de datos:** Limpieza automática de datos y restauración del color de texto (`TEXT_PRIMARY`) al alternar entre *Consumidor Final* y cliente registrado.
  - En **Transferencia** o **Crédito**, el contenedor reaparece automáticamente (`setVisible(true)`), deshabilitando la opción de consumidor final para exigir los datos del titular o comprobante.
- **Calculadora de Vueltas Exactas en Tiempo Real (`updateChangeCalculation`):** El footer del carrito incluye un campo interactivo `Paga con ($)` con icono SVG `icons/wallet.svg` que parsea defensivamente cualquier importe digitado por el tendero y calcula instantáneamente las vueltas exactas a entregar (`Vueltas: $ XX.XXX,XX` en verde neón o `Falta: $ XX.XXX,XX` en rojo neón), recalculándose automáticamente ante cambios en las existencias o productos del carrito.
- **Resguardo contra Desbordamientos (`JScrollPane`):** El panel de checkout está envuelto en un `JScrollPane` silencioso (sin bordes y con viewport transparente), garantizando que en pantallas pequeñas o al desplegar campos el botón **Confirmar venta** nunca se corte.

---

## 7. Registro Inicial del Administrador — Microcopy & Guía Contextual (`ModernAdminRegistrationPage`)

- **Diseño Neumórfico Responsivo:** Tarjeta central redondeada (`RoundedPanel`) de 500px de ancho en escritorio con reflow fluido al 100% en pantallas móviles (`< 480px`).
- **Guía Contextual Permanente:** Cada campo incluye una etiqueta de ayuda (`createHelperLabel`) con color secundario neón semitransparente que orienta al usuario sin interferir visualmente:
  - *Nombre Completo:* "Ingresa tu nombre y apellido para identificarte".
  - *Teléfono:* "Número móvil de 10 dígitos para recuperación de cuenta".
  - *Correo:* "Servirá para recibir notificaciones y recuperar tu acceso".
  - *Contraseña:* "Utiliza al menos 6 caracteres con letras y números".
- **Navegación por Teclado:** Avance fluido con `Enter` entre campos y registro por defecto con la tecla `Enter` en el campo de contraseña.
