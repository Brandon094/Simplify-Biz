# Documentación técnica - ERP+ Business

## Alcance

Aplicación Java Swing de escritorio con SQLite local, autenticación por roles y una ventana principal que reemplaza el contenido según el módulo seleccionado.

## Capas del proyecto

- `config`: `ConexionDB`, `DatabaseInitializer`, `SelecionRuta`, `Seguridad`, correo y utilidades de archivos.
- `controllers`: acceso a datos y operaciones de usuarios, productos y ventas.
- `models`: `Producto`, `Venta`, `Usuario`, `Sesion` e `Informe`.
- `views`: login, registro inicial, shell principal y páginas funcionales.
- `views/components`: constantes visuales y componentes reutilizables.

## Patrón de presentación

La aplicación usa MVC y una organización inspirada en Atomic Design:

- Atoms: `NeonButton`, `RoundedPanel`, `NeonLineChart`, `NeonPieChart`.
- Molecules: `SidebarItem`.
- Organisms: `ModernSidebar`, `MetricCard`.
- Shell: `MainTemplate`.
- Páginas: dashboard, ventas, productos, clientes, empleados, reportes, configuración y proveedores.

## Arranque y navegación

`Main.main()` configura `FlatDarkLaf`, aplica estilos globales, inicializa la base de datos y decide entre registro de administrador o login. `MainTemplate` recibe el rol, crea el sidebar y muestra:

- Administrador (`1`): Resumen, Ventas, Productos, Clientes, Empleados, Reportes y Configuración.
- Vendedor (`0`): Ventas y Productos.

Proveedores permanece implementado, pero no se registra actualmente en el sidebar.

## Dependencias principales

- Java/JDK 17.
- Maven.
- FlatLaf y FlatLaf Extras 3.5.1.
- SQLite JDBC 3.46.1.0.
- Apache POI 5.2.3 para Excel.
- JavaMail 1.6.2.
- Lombok como dependencia provided.

## Recursos visuales

Los SVG se encuentran en `src/main/resources/icons` y se cargan con `FlatSVGIcon`. La aplicación aplica filtros de color para estados y acciones. Los componentes de gráficos usan `Graphics2D`, con guías, escalas, leyendas y estados vacíos.

## Flujos importantes

### Productos

`ProductPage` valida campos, crea `Producto` y llama a `ProductoController.agregarOActualizarProductoSiExiste()`. Si el código existe, el controlador suma stock; si no, inserta el producto.

### Ventas

`SalesPage` busca por código o nombre, construye el carrito, valida cantidades y crea una `Venta`. `VentasController.guardarVenta()` ejecuta una transacción que inserta encabezado y detalles, y descuenta stock.

Para venta mostrador se utilizan `CONSUMIDOR FINAL` y `N/A`. No se agrega un usuario genérico a `usuarios`.

### Clientes y empleados

Clientes se muestran mediante `mostrarUsuariosPorRol(..., "2")` y la vista es de consulta. Empleados se registran y actualizan mediante `UsuarioController` con rol `0`.

### Reportes

La tabla se carga con `VentasController.MostrarVentas()` o `mostrarFechasDefinidas()`. El feedback de lista vacía se muestra en la vista. La exportación a Excel y PDF debe considerarse pendiente de completar en la lógica.

## Construcción y verificación

```bash
mvn compile
mvn clean package
java -jar target/Simplify-Biz-1.2.0.jar
```

El proyecto no contiene actualmente fuentes de pruebas automatizadas. Toda modificación visual debe verificarse compilando y abriendo el JAR.

## Documentos relacionados

- [Manual de usuario](manual_usuario.md)
- [Configuraciones](configuraciones.md)
- [Esquema](esquema_bd.md)
- [Diccionario](diccionario_datos.md)
- [Roadmap](roadmap/roadmap_erpsimplify.md)
