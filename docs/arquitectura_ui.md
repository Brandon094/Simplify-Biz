# Arquitectura de UI/UX - ERP+ Business

Este documento detalla el sistema de diseño, la estructura de componentes Swing y la arquitectura visual implementada en la aplicación ERP+ Business.

---

## 1. Filosofía de Diseño

ERP+ Business adopta una estética **Neon/Glassmorphism Cyberpunk**, priorizando la jerarquía visual, la claridad operacional y la velocidad de trabajo diaria.

### Principios Fundamentales
1. **Consistencia Total (DRY Visual):** Todos los colores, fuentes y dimensiones derivan centralizadamente de `ThemeConstants`.
2. **Respiro Visual y Accesibilidad:**
   - Blancos y paddings adaptativos de mínimo 15–30px.
   - Objetivos táctiles/clicables mínimos de `44px` de altura (`ThemeConstants.TOUCH_TARGET_MIN`).
   - Filas de tabla de `36px` (`ThemeConstants.TABLE_ROW_HEIGHT`).
   - Anillos de foco explícitos para navegación por teclado.
3. **Alternancia de Tema sin Reinicio:** Soporte nativo para paleta oscura (predeterminada) y clara.

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
  │   │   ├── NeonLineChart.java        Gráfico de líneas en Graphics2D con curva suave y grid
  │   │   ├── NeonPieChart.java         Gráfico de torta/anillo responsivo con paleta neón de 10 colores, métricas centrales y leyenda dinámica.
  │   │   └── ThemeToggleButton.java    Botón toggle (☀️/🌙) que conmuta la paleta en caliente
  │   ├── molecules/
  │   │   └── SidebarItem.java          Fila de navegación con icono SVG, etiqueta y estado activo
  │   └── organisms/
  │       ├── ModernSidebar.java        Menú lateral colapsable con perfil de usuario y toggle de tema
  │       └── MetricCard.java           Tarjeta KPI con icono, valor numérico y tendencia
  └── [Páginas] (DashboardPage, ProductPage, SalesPage, SellersPage, ClientsPage, ReportsPage, ConfigPage)
```

---

## 4. Diseño Responsive y Adaptativo (`LayoutResponsive`)

`LayoutResponsive` registra listeners sobre las pantallas para reflow automático de componentes según el ancho disponible de la ventana:

### Breakpoints Definidos

- **Móvil (`< 480px`):** Ancho fluido 100%. Formularios se apilan verticalmente sobre las tablas. Menú lateral colapsable controlado por botón de hamburguesa.
- **Tablet (`480px – 960px`):** Layout mixto. Formularios e indicadores se reordenan.
- **Escritorio (`> 960px`):** Panel en 2 o 3 columnas laterales (formulario a la izquierda, tabla/gráficos a la derecha).

---

## 5. Iconografía y Microcopy

### Iconografía SVG
Todos los iconos son archivos SVG alojados en `src/main/resources/icons/`. Se cargan mediante `FlatSVGIcon` usando rutas relativas directas (ej: `"icons/sun.svg"`, `"icons/moon.svg"`), aplicando un `ColorFilter` dinámico según el estado y el tema.

### Guiado de Usuario y Microcopy
- **Subtítulos de Contexto:** Cada módulo incluye un encabezado descriptivo creado con `UIUtils.createHeader`.
- **Textos de Ayuda (Helper Texts):** Campos clave incluyen descripciones de formato (ej: `"Ej: SKU-001 — código único"`).
- **Placeholders Visuales:** Cajas de texto con ejemplos tenues mediante `FlatClientProperties.PLACEHOLDER_TEXT` que desaparecen al enfocar.
- **Sistema de Tablas y Formatos DRY (`UIUtils`):** Métodos centralizados que estandarizan el diseño y datos de todas las tablas de la aplicación (**Productos**, **Clientes**, **Empleados**, **Reportes** y **Dashboard**):
  - **`UIUtils.formatDate(Object)`**: Convierte cualquier formato de fecha (`Date`, `LocalDate`, `Timestamp` o cadenas ISO `yyyy-MM-dd`) al estándar unificado **`dd/MM/yyyy`** (ej: `12/09/2026`).
  - **`UIUtils.formatCurrency(double)`**: Formato de moneda contable (`$ 15.000,00`) alineado a la derecha para precios y totales.
  - Ocultamiento automático de la columna `Id` técnica.
  - Centrado automático para cantidades, stock, códigos, números de documento, fechas y estados.
  - Alineación a la izquierda para nombres, categorías, correos y direcciones.
  - Filas de zebra alternadas y selección neumórfica en tono `NEON_PURPLE`.
