# Manual de Operación — ERP+ Business

> Guía paso a paso para usuarios finales de la aplicación. Cubre todos los módulos, flujos de trabajo y preguntas frecuentes.

---

## 1. Iniciar la Aplicación

```bash
java -jar dist/Simplify-Biz-1.3.0.jar
```

### 1.1 Primer Uso

En el primer inicio, la aplicación:

1. **Crea la carpeta de datos protegida automáticamente:** El sistema inicializa de forma transparente y resguardada la base de datos SQLite en tu carpeta de usuario de aplicación (`AppData` en Windows / `.config` en Linux) para prevenir borrados accidentales.
2. **Crear el administrador principal:** Completa nombre, teléfono, correo electrónico y contraseña. Este será el usuario principal con acceso completo.
3. El sistema confirma la creación y muestra la pantalla de login.

### 1.2 Inicios Posteriores

La aplicación muestra directamente el formulario de **inicio de sesión**:

- **Recordarme:** Si activas la casilla *"Recordarme"*, tu nombre de usuario se guardará de forma segura para autocompletarse en próximos inicios de sesión.
- **¿Olvidaste tu contraseña?:** Haz clic sobre este enlace si necesitas restablecer tu clave. Se abrirá un diálogo modal que te solicitará:
  1. Tu nombre de usuario o correo electrónico registrado.
  2. Tu número de teléfono registrado.
  3. Si la verificación es exitosa, podrás definir una nueva contraseña al instante.

---

## 2. Roles de Usuario

| Rol | Código | Acceso |
| :--- | :--- | :--- |
| **Administrador** | `1` | Dashboard, Ventas, Productos, Clientes, Empleados, Reportes, Configuración |
| **Vendedor/Empleado** | `0` | Ventas, Productos |
| **Cliente** | `2` | Sin acceso directo. Se almacena como dato de referencia para las ventas. |

---

## 3. Módulos

### 3.1 Dashboard

El panel principal ofrece una visión ejecutiva e inteligente del rendimiento del negocio:

- **KPIs Financieros y Operativos:**
  - **Ventas Totales ($):** Acumulado histórico total facturado.
  - **Utilidad Neta ($):** Ganancia real neta descontando los costos de compra del inventario vendido, acompañada del % de margen real.
  - **Inversión Inventario ($):** Valor total a precio costo de la mercancía disponible en bodega.
  - **Stock Crítico:** Conteo dinámico de productos con 5 o menos unidades disponibles.
- **Gráficos Ejecutivos e Interactivos:**
  - **Ventas Últimos 7 Días:** Gráfico de línea neón con tendencia diaria acumulada.
  - **Distribución de Inventario:** Gráfico de donut neón interactivo que agrupa automáticamente las 5 categorías con mayor stock y consolida el resto en `"OTROS"`. Al pasar el cursor sobre cualquier categoría, la rebanada se desplaza hacia afuera (*hover offset*) y se dibuja una línea conectora neón que apunta a su texto en la leyenda lateral.
  - **Balance Financiero Comparativo:** Gráfico de barras neón que compara macroscópicamente las **Ventas Totales ($)**, la **Utilidad Neta ($)** y la **Inversión en Bodega ($)** con animación interactiva al pasar el cursor.
- **Salud del Sistema & Alertas:** Widget con 3 indicadores en tiempo real: estado de conexión a la BD SQLite (Modo WAL), alerta de reposición urgente de stock, y sesión/turno activo del usuario.

> **Nota:** Los mensajes como *"Aún no hay ventas registradas"* indican que no existen datos aún, no un error del sistema.

### 3.2 Productos

#### Registrar o Buscar un producto (Autocompletado en Tiempo Real)

1. Abre la sección **Productos** desde el menú lateral.
2. Al empezar a escribir en el campo **Nombre del producto** o **Código/SKU**, el sistema mostrará sugerencias desplegables en tiempo real e insensibles a mayúsculas/minúsculas.
3. Si seleccionas una sugerencia (con clic o la tecla `Enter`), se rellenarán automáticamente todos los campos del producto (Nombre, Código/SKU, Precio, Stock y Categoría) para consulta o reabastecimiento rápido.
4. Si registras un producto nuevo, completa los campos:
   - **Nombre del producto** — Nombre descriptivo.
   - **Código/SKU** — Código único de identificación (ej: `SKU-001`).
   - **Categoría** — Selecciona una categoría existente de la lista desplegable **o escribe una categoría nueva** directamente en la casilla. El sistema la aprenderá y guardará automáticamente para tu negocio.
   - **Precio** — Precio unitario de venta.
   - **Stock** — Cantidad disponible.
5. Pulsa **Crear producto**.

> **Comportamiento:** Si el código ya existe, el sistema **suma** la cantidad al stock existente en lugar de crear un duplicado.

#### Modificar un producto

1. Selecciona un producto en la tabla haciendo clic sobre la fila o búscalo mediante el autocompletado en los campos de entrada.
2. Los datos se cargan automáticamente en el formulario.
3. Modifica los campos deseados.
4. Pulsa **Actualizar**.

#### Eliminar un producto

1. Selecciona un producto en la tabla.
2. Pulsa el botón **Eliminar** (icono de papelera).
3. Confirma la acción en el diálogo de confirmación.

#### Filtrar por categoría

Utiliza el selector de categoría sobre la tabla para ver solo los productos de una categoría específica. Selecciona "Todas" para ver el catálogo completo.

### 3.3 Ventas (Punto de Venta - POS)

El punto de venta (POS) cuenta con un diseño de tres tarjetas simétricas diseñadas para máxima eficiencia operativa:

1. **Agregar productos (Izquierda):** Campo de búsqueda ágil con **autocompletado en tiempo real e insensible a mayúsculas/minúsculas** (busca por código o nombre). Permite seleccionar sugerencias con las teclas $\uparrow$/$\downarrow$ o `Enter`.
2. **Carrito de compras (Centro):** Listado dinámico con subtotal por ítem y cálculo del gran total en tiempo real.
3. **Método de pago y cliente (Derecha):** Selector de método de pago prominente (`Efectivo`, `Transferencia` y `Crédito`), autocompletado inteligente de clientes por Cédula, Nombre, Teléfono o Correo, y gestión adaptada al flujo de caja.

#### Flujo de Venta Rápida (Cero Fricción)

- **Venta en Efectivo (Por Defecto):**
  1. Escribe en el buscador de producto y presiona `Enter` sobre la sugerencia.
  2. El método de pago está predeterminado en **Efectivo** y el cliente en **Venta a Consumidor Final (Sin datos)**.
  3. Pulsa **Confirmar venta**. ¡Procesado en 2 clics! Se guarda automáticamente con estado `pago_confirmado = 'pagado'`.

- **Venta por Transferencia (Nequi / Bancolombia / Daviplata):**
  1. Selecciona el método **Transferencia**.
  2. El sistema desactiva automáticamente la casilla de Consumidor Final y exige los datos del cliente.
  3. Al escribir cualquier dato en **Cédula / NIT** (Cédula, Nombre, Teléfono o Correo), la lista desplegable sugerirá los clientes registrados o historial de clientes. Al seleccionar uno, se autocompletarán los 4 campos del cliente.
  4. Pulsa **Confirmar venta**. Se registra con estado `pago_confirmado = 'pagado'`.

- **Venta a Crédito (Fiado / Cuentas por Cobrar):**
  1. Selecciona el método **Crédito**.
  2. El sistema desactiva automáticamente la opción de Consumidor Final y exige los datos del cliente (**Cédula/NIT** y **Nombre/Razón Social**).
  3. Puedes autocompletar los datos usando el buscador inteligente de clientes.
  4. Pulsa **Confirmar venta**. Se registra en la base de datos con estado `pago_confirmado = 'deudor'` para seguimiento en cartera.

> **Seguridad:** El descuento de existencias en el inventario se ejecuta de forma atómica. Si ocurre una interrupción o fallo de base de datos, la transacción completa se revierte automáticamente.

### 3.4 Clientes

La sección **Clientes** lista los usuarios registrados con rol `2` (cliente):

- La tabla muestra: ID, Nombre, Email, Teléfono y Rol.
- **No se muestran contraseñas** en la interfaz.
- Los clientes se crean automáticamente desde el flujo de venta cuando proporcionan sus datos.
- Esta pantalla es **de solo consulta**: no permite crear ni editar clientes manualmente.

### 3.5 Empleados

*(Solo para administradores)*

#### Registrar un empleado

1. Abre la sección **Empleados**.
2. Completa: nombre, teléfono, correo electrónico y contraseña.
3. Pulsa **Registrar empleado**.

#### Actualizar datos

1. Selecciona un empleado en la tabla.
2. Modifica los campos deseados.
3. Si dejas la contraseña en blanco, se conserva la contraseña existente.
4. Pulsa **Actualizar datos**.

#### Eliminar un empleado

1. Selecciona un empleado en la tabla.
2. Pulsa **Eliminar**.
3. Confirma la acción en el diálogo de confirmación.

### 3.6 Reportes

1. Abre la sección **Reportes**.
2. **Ver todas las ventas:** Se cargan automáticamente al entrar.
3. **Filtrar por fecha:**
   - Ingresa la fecha de inicio y fin en formato `dd/MM/yyyy`.
   - Pulsa **Filtrar**.
4. **Exportar a Excel:**
   - Pulsa el botón de exportación.
   - Selecciona la ubicación para guardar el archivo `.xlsx`.

### 3.7 Configuración

La sección de configuración permite:

- **Ver la ruta de la base de datos** actual.
- **Cambiar la ruta:** Selecciona una nueva carpeta. Requiere reiniciar la aplicación.
- **Información del sistema:** Versión de la aplicación, motor de base de datos, licencia.
- **Enlace al desarrollador:** El texto *"ChopCode Solutions"* abre el portafolio web.

### 3.8 Proveedores *(próximamente)*

La pantalla existe como prototipo visual, pero actualmente no guarda datos en la base de datos. Esta funcionalidad se implementará en la Fase 4.

---

## 4. Tema Oscuro y Claro

En la parte inferior del menú lateral hay un botón toggle que permite cambiar el aspecto visual:

| Tema Actual | Botón Muestra | Acción |
| :--- | :--- | :--- |
| Oscuro (predeterminado) | ☀️ Modo claro | Cambia a paleta clara |
| Claro | 🌙 Modo oscuro | Cambia a paleta oscura |

- El cambio se aplica **instantáneamente** a toda la aplicación (menú, tablas, formularios, gráficos).
- La preferencia se **guarda automáticamente** y se recuerda entre reinicios.
- **No modifica datos** ni la configuración del negocio.

---

## 5. Copias de Seguridad

### 5.1 Proceso de Respaldo

1. **Cierra la aplicación** completamente.
2. Navega a la carpeta de datos configurada.
3. Copia el archivo `db.db` (o el archivo SQLite configurado) a una carpeta de respaldo.
4. Nombra las copias con la fecha (ej: `db_20260912.db`).

### 5.2 Restaurar un Respaldo

1. Cierra la aplicación.
2. Reemplaza el archivo `db.db` con la copia de respaldo.
3. Inicia la aplicación normalmente.

> **Importante:** Prueba periódicamente la restauración en una carpeta separada para verificar la integridad. Consulta [configuraciones.md](configuraciones.md) para más información sobre el contrato de rutas.

---

## 6. Solución de Problemas

| Problema | Causa Probable | Solución |
| :--- | :--- | :--- |
| "Error al inicializar la base de datos" | Ruta de BD inválida o permisos insuficientes | Verifica que la carpeta existe y tienes permisos de escritura |
| "database is locked" | Múltiples instancias de la app o BD en carpeta sincronizada | Cierra otras instancias. Mueve la BD a una carpeta local |
| Los iconos del tema se ven como cuadros rojos | SVGs no encontrados en el classpath | Verifica que `src/main/resources/icons/` contiene los archivos SVG |
| La tabla no muestra datos | Base de datos vacía | Es normal en el primer uso. Registra productos y realiza ventas |
| Campos de texto truncados | Ventana muy pequeña | Redimensiona la ventana. El diseño adaptativo se ajustará automáticamente |
