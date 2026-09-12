# Manual de operación - ERP+ Business

## Inicio

Ejecuta la aplicación con:

```bash
java -jar target/Simplify-Biz-1.2.0.jar
```

En el primer uso configura la ubicación de la base de datos y crea el administrador principal. En usos posteriores inicia sesión con las credenciales registradas.

## Roles

- **Administrador:** acceso al dashboard, ventas, productos, clientes, empleados, reportes y configuración.
- **Vendedor:** acceso a ventas y productos.
- **Cliente:** se almacena como usuario de rol `2`, pero no tiene una sección de gestión manual en la interfaz administrativa.

## Dashboard

El dashboard muestra ventas acumuladas, órdenes, productos, stock crítico, últimas ventas, estado operativo y gráficos. Los mensajes como `Aún no hay ventas registradas` indican que todavía no existen datos, no un error de carga.

## Registrar productos

1. Abre `Productos`.
2. Escribe nombre y código/SKU.
3. Selecciona categoría.
4. Introduce precio y stock.
5. Pulsa `Guardar producto`.

Si el código ya existe, el flujo actual actualiza el stock del producto existente.

## Cambiar entre tema oscuro y claro

En la parte inferior del menú lateral hay un botón que permite alternar el aspecto de la aplicación:

- En **tema oscuro** (predeterminado) muestra **☀️ Modo claro**; al pulsarlo la interfaz pasa a la paleta clara.
- En **tema claro** muestra **🌙 Modo oscuro**; al pulsarlo vuelve a la paleta oscura.

El cambio se aplica al instante a toda la aplicación (menú, tablas, formularios, gráficos y ventanas) y no modifica tus datos ni la configuración del negocio.

## Registrar una venta

1. Abre `Ventas`.
2. Busca el producto por código o nombre.
3. Define cantidad y descuento.
4. Pulsa `Agregar al carrito`.
5. Completa nombre y documento del cliente, o activa `Cliente no desea suministrar datos`.
6. Selecciona el método de pago.
7. Pulsa `Confirmar venta`.

En venta mostrador se guardan `CONSUMIDOR FINAL` y `N/A`. El sistema no crea un usuario ficticio.

## Consultar clientes

La sección `Clientes` lista los usuarios con rol `2`. Los clientes se crean desde el flujo de venta cuando entregan sus datos. La pantalla no permite crear ni editar clientes manualmente.

## Gestionar empleados

El administrador abre `Empleados`, completa nombre, teléfono, correo y contraseña, y utiliza `Registrar empleado`. Para editar, selecciona una fila y utiliza `Actualizar datos`.

## Reportes

En `Reportes` puedes consultar las ventas y filtrar por fecha. Los botones de Excel y PDF forman parte de la interfaz; la implementación de exportación debe revisarse antes de considerarla final.

## Configuración

La sección permite revisar la ruta de datos, cambiarla y consultar versión, motor de base de datos, licencia y desarrollador. El texto `ChopCode Solutions` abre el portafolio web.

## Proveedores

La opción está oculta temporalmente del sidebar. La pantalla existe como prototipo visual, pero todavía no guarda proveedores en SQLite.

## Copias de seguridad

Cierra la aplicación y respalda el archivo SQLite efectivo antes de moverlo o actualizar el proyecto. Consulta [configuraciones.md](configuraciones.md) para la particularidad actual de la ruta.
