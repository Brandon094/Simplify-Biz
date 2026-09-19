# Arquitectura de UI/UX - ERP+ Business (v2.1.0)

> **Especificación de Sistema de Diseño, Componentes Swing y Renderizado Gráfico 2D**  
> Implementación desacoplada basada en **Atomic Design**, paleta **Cyberpunk Neon/Glassmorphism** y reflow responsive adaptativo.

---

## 1. Filosofía de Diseño & Sistema de Tokens

ERP+ Business adopta una estética **Neon/Glassmorphism Cyberpunk**, priorizando la jerarquía visual, la velocidad operacional en caja y la ergonomía del usuario.

### 1.1 Principios Fundamentales
1. **Consistencia Total (DRY Visual):** Todos los colores, tipografías, dimensiones y formateos de moneda derivan centralizadamente de `ThemeConstants` y `UIUtils`.
2. **Ergonomía & Accesibilidad:**
   - Blancos y paddings adaptativos de mínimo `15px` a `30px`.
   - Objetivos táctiles/clicables mínimos de `44px` de altura (`ThemeConstants.TOUCH_TARGET_MIN`).
   - Filas de tabla de `36px` (`ThemeConstants.TABLE_ROW_HEIGHT`).
   - Anillos de foco explícitos para navegación por teclado.
3. **Alternancia de Tema sin Reinicio:** Soporte nativo en caliente para paleta oscura (predeterminada) y clara mediante `ThemeConstants.setDark(boolean)` y notificación de eventos repaint.
4. **Formateo de Moneda Compacto Inteligente (`UIUtils.formatCompactCurrency`):** Estandarización visual en tarjetas KPI (`$100K`, `$5.4M`, `$1.2B`) evitando desbordamientos de texto, manteniendo la precisión contable completa en tooltips y tablas (`$ 128.450.000,00`).

---

## 2. Paleta de Colores & Sistema de Temas (`ThemeConstants`)

`ThemeConstants` administra el estado global del tema gráfico mediante `isDark()` y `setDark(boolean)`.

| Token Visual | Constante | Modo Oscuro | Modo Claro | Propósito y Aplicación |
| :--- | :--- | :--- | :--- | :--- |
| **BACKGROUND** | `BACKGROUND` | `#0B0E14` | `#EEF2F7` | Fondo principal del viewport y ventanas |
| **SIDEBAR** | `SIDEBAR_BACKGROUND` | `#121620` | `#FFFFFF` | Fondo del menú lateral y encabezados de tabla |
| **CARD** | `CARD_BACKGROUND` | `#1E293B` | `#FFFFFF` | Contenedores redondeados neumórficos (`RoundedPanel`) |
| **INPUT BG** | `INPUT_BACKGROUND` | `#0F172A` | `#F8FAFC` | Fondo de cajas de texto (`JTextField`) y combos |
| **INPUT BORDER** | `INPUT_BORDER` | `#334155` | `#CBD5E1` | Borde perimetral de cajas de texto |
| **ZEBRA TABLE** | `TABLE_ZEBRA` | `#162032` | `#F1F5F9` | Filas alternadas de las tablas (`JTable`) |
| **TEXT PRIMARY** | `TEXT_PRIMARY` | `#FFFFFF` | `#0F172A` | Títulos, valores contables y etiquetas principales |
| **TEXT SECONDARY** | `TEXT_SECONDARY` | `#94A3B8` | `#475569` | Subtítulos, helper texts y placeholders |
| **NEON PURPLE** | `NEON_PURPLE` | `#A855F7` | `#7C3AED` | Acento primario, marca y botones destacados |
| **NEON BLUE** | `NEON_BLUE` | `#3B82F6` | `#2563EB` | Acciones secundarias e información de sistema |
| **NEON GREEN** | `NEON_GREEN` | `#22C55E` | `#16A34A` | Éxito, confirmación de venta, abonos y guardado |
| **NEON CYAN** | `NEON_CYAN` | `#06B6D4` | `#0891B2` | Gráficos BI, métricas, atajos e inventarios |

---

## 3. Especificación Atómica de Componentes (`views/components`)

La interfaz organiza sus componentes Swing siguiendo estrictamente **Atomic Design**:

### 3.1 Átomos (`components/atoms`)

- **[`NeonButton.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/NeonButton.java)**:
  - Botón interactivo neumórfico con soporte para iconos SVG vectoriales (`FlatSVGIcon`).
  - Aplica efectos hover con cambio de opacidad y sombra neón perimetral. Soporta variantes de color (`NEON_PURPLE`, `NEON_CYAN`, `NEON_GREEN`, `NEON_BLUE`).

- **[`RoundedPanel.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/RoundedPanel.java)**:
  - Contenedor Swing base con esquinas redondeadas ajustables (radio por defecto `16px`). Renderiza su fondo con Anti-Aliasing activado en `Graphics2D`.

- **[`NeonLineChart.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/NeonLineChart.java)**:
  - Motor de renderizado en `Graphics2D` para curvas continuas y punteadas.
  - Trazado de serie actual (línea sólida neón violeta + degradado inferior semitransparente) y serie del periodo anterior (línea discontinua `BasicStroke.JOIN_ROUND`).
  - Renderizado automático del **Badge de Variación Interperiodo ($\Delta \%$)** en la esquina superior derecha:
    $$\mathbf{\Delta \%} = \left( \frac{\mathbf{V_{actual}} - \mathbf{V_{anterior}}}{\mathbf{V_{anterior}}} \right) \times 100$$

- **[`NeonBarChart.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/NeonBarChart.java)**:
  - Gráfico de barras vectoriales para comparativa macro (**Ventas Totales**, **Utilidad Neta**, **Inversión en Bodega**). Normalización de escala dinámica `maxVal * 1.15` y formateo compacto en Eje Y (`$4.3M`, `$500K`).

- **[`NeonPieChart.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/NeonPieChart.java)**:
  - Gráfico de dona polar interactivo para el Top 5 de categorías + `"OTROS"`.
  - **Matemática de Coordenadas Polares & Hover Offset:** Mapea el ángulo cartesiano $\theta = \text{atan2}(y - cy, x - cx)$ respecto al centro $(cx, cy)$. Si el cursor colisiona con el sector $i$, desplaza la rebanada $7\text{px}$ en la dirección del radio medio $\theta_m$:
    $$x_{\text{offset}} = 7 \cdot \cos(\theta_m), \quad y_{\text{offset}} = 7 \cdot \sin(\theta_m)$$
    Y dibuja una curva de Bézier neón (`Path2D`) apuntando a la leyenda activa.

- **[`ThemeToggleButton.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/atoms/ThemeToggleButton.java)**:
  - Botón toggle (☀️/🌙) que conmuta la paleta visual en caliente evaluando `ThemeConstants.setDark(...)` y re-pintando el árbol de componentes sin reiniciar la aplicación.

---

### 3.2 Moléculas (`components/molecules` & `components`)

- **[`SidebarItem.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/molecules/SidebarItem.java)**:
  - Componente de fila de menú. Encapsula un icono SVG vectorial y la etiqueta del módulo. Renderiza estados: normal, hover e interactivo activo con un indicador neón vertical a la izquierda.

- **[`SidebarSection.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/molecules/SidebarSection.java)**:
  - Acordeón colapsable/expandible con indicador visual (▲/▼). Mantiene la persistencia automática del estado desplegado/colapsado entre sesiones mediante `java.util.prefs.Preferences`.

- **[`AutocompletePopup.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/AutocompletePopup.java)**:
  - Motor desacoplado de sugerencias emergentes en tiempo real basado en genéricos `<T>`. Permite asociar listeners de búsqueda reactiva a `JTextFields` sin bloquear el foco de entrada del teclado.

---

### 3.3 Organismos (`components/organisms`)

- **[`ModernSidebar.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/organisms/ModernSidebar.java)**:
  - Menú lateral principal con colapso horizontal dinámico ($260\text{px} \leftrightarrow 64\text{px}$) mediante el botón toggle `arrow-left.svg`/`arrow-right.svg`. Incluye avatar del usuario, perfil de sesión y toggle de tema visual.

- **[`MetricCard.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/organisms/MetricCard.java)**:
  - Tarjeta KPI ejecutiva compuesta por contenedor neumórfico (`RoundedPanel`), icono SVG temático con filtro de color neón, título secundario, valor numérico formateado compacto (`$100K`, `$5.4M`) y tendencia de porcentaje.

---

### 3.4 Asistentes Modales & Diálogos (`components/dialogs`)

- **[`ImportarProductosDialog.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/dialogs/ImportarProductosDialog.java)**: Asistente modal en 4 pasos para la importación masiva y mapeo dinámico de columnas Excel/CSV al catálogo o abastecimiento.
- **[`RegistrarCompraDialog.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/dialogs/RegistrarCompraDialog.java)**: Modal de ingreso de facturas de abastecimiento con autocompletado de proveedor e importación previa.
- **[`RegistrarAbonoDialog.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/dialogs/RegistrarAbonoDialog.java)**: Modal para el recaudo de cuotas parciales en ventas a crédito con resumen financiero.
- **[`HistorialAbonosDialog.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/dialogs/HistorialAbonosDialog.java)**: Bitácora cronológica de pagos recibidos por factura de cartera.
- **[`ManualUsuarioDialog.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/components/dialogs/ManualUsuarioDialog.java)**: Centro de Ayuda e instructivo navegable con buscador interactivo y paginador.

---

### 3.5 Vistas Principales / Páginas (`views/`)

- **[`DashboardPage.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/DashboardPage.java)**: Centro BI con 4 tarjetas KPI, selector de ventana temporal, gráfico de líneas comparativo interperiodo, gráfico donut de categorías y gráfico de barras macro.
- **[`SalesPage.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/SalesPage.java)**: Punto de venta POS 2.0 en 3 columnas (28% Catálogo, 44% Carrito Ampliado, 28% Checkout), autocompletado en tiempo real de clientes y calculadora de vueltas exactas.
- **[`ProductPage.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/ProductPage.java)**: Gestión de catálogo e inventario, alertas de stock crítico e importación Excel.
- **[`ComprasPage.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/ComprasPage.java)**: Registro de facturas de proveedores e incremento automático de stock.
- **[`CarteraPage.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/CarteraPage.java)**: Módulo de gestión de cuentas por cobrar, seguimiento a deudores y recaudo de abonos.
- **[`ReportsPage.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/ReportsPage.java)**: Inteligencia financiera con filtrado por fechas y exportación a Excel/PDF.
- **[`ConfigPage.java`](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo%20Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/ConfigPage.java)**: Ajustes del sistema, copia de seguridad SQLite y licenciamiento.

---

## 4. Diseño Responsive & Adaptativo (`LayoutResponsive`)

`LayoutResponsive` analiza en tiempo real las dimensiones de la ventana para realizar reflow de componentes:

### Breakpoints Definidos
- **Móvil (`< 480px`):** Formularios se apilan verticalmente. Tablas activan scroll horizontal silencioso. Menú lateral pasa a modo flotante colapsado.
- **Tablet (`480px – 960px`):** Layout mixto a 2 columnas. Indicadores KPI se reorganizan en cuadrícula $2 \times 2$.
- **Escritorio (`> 960px`):** Distributivo optimizado de 3 columnas para POS: Catálogo (28%), Carrito Ampliado (44%) y Checkout (28%).

---

## 5. Encabezado Superior Dinámico (`MainTemplate`)

La clase `MainTemplate` actúa como el Shell Frame contenedor de la aplicación. Proporciona una barra de encabezado superior (*Top Header*) unificada que elimina duplicidades visuales. Actualiza en tiempo real el título de la vista, subtítulo descriptivo e icono SVG temático de $22 \times 22\text{px}$ con filtro de color neón correspondiente al módulo activo, maximizando el espacio de trabajo vertical.
