# ERP+ Business

ERP+ Business es una aplicación de escritorio para pequeñas y medianas empresas. Centraliza inventario, ventas, clientes, empleados, reportes y configuración en una ventana operativa con navegación lateral.

El proyecto está desarrollado con Java 17, Swing, FlatLaf y SQLite. La versión actual prioriza la modernización UI/UX sin cambiar las reglas de negocio existentes.

## Inicio rápido

### Requisitos

- JDK 17 o superior.
- Maven 3.8 o superior.
- Permisos de lectura y escritura para la raíz del proyecto, `config.properties` y la ubicación de SQLite.

### Compilar y ejecutar

Desde la raíz del proyecto:

```bash
mvn clean package
java -jar target/Simplify-Biz-1.2.0.jar
```

Durante el desarrollo puede usarse:

```bash
mvn compile
```

`mvn clean package` genera un JAR ejecutable con dependencias incluidas mediante Maven Shade.

## Primer inicio

1. `Main` configura FlatLaf Dark y los estilos globales.
2. Se lee `config.properties`.
3. Si falta la ruta, se solicita una carpeta mediante un selector.
4. Se crean o verifican las tablas SQLite.
5. Si no existe un administrador, se muestra el registro inicial; de lo contrario aparece el login.

## Módulos y alcance actual

- **Dashboard:** KPIs, últimas ventas, estado operativo y gráficos con estados vacíos.
- **Productos:** registro y actualización de producto, SKU, precio, categoría y stock.
- **Ventas:** búsqueda, cantidad, descuento, carrito, cliente, método de pago y confirmación.
- **Venta mostrador:** usa `CONSUMIDOR FINAL` y `N/A` si el comprador no desea entregar datos.
- **Clientes:** consulta de usuarios con rol `2`; no crea ni edita clientes manualmente.
- **Empleados:** el administrador registra y actualiza usuarios con rol `0`.
- **Reportes:** consulta por rango de fechas y acciones de exportación parcialmente conectadas.
- **Configuración:** cambio de ruta, información técnica y enlace al portafolio del desarrollador.
- **Proveedores:** vista preparada visualmente, actualmente oculta del sidebar y sin persistencia propia.

## Decisiones de UI/UX

- Ventana única con contenido dinámico y sidebar por rol.
- Componentes Swing reutilizables organizados por atoms, molecules y organisms.
- Iconos SVG locales en `src/main/resources/icons` mediante `FlatSVGIcon`.
- Estados vacíos explícitos para listas, ventas e inventario.
- Tablas con encabezados, filas alternadas, selección y columnas técnicas ocultas donde corresponde.

## Estructura principal

```text
src/main/java/com/mycompany/zl_solucion_integral/
  config/       Conexión, inicialización, seguridad, rutas y utilidades
  controllers/  Operaciones de negocio y acceso a datos
  models/       Entidades del dominio
  views/        Pantallas y componentes Swing
src/main/resources/icons/  Recursos SVG de la interfaz
docs/                         Documentación técnica, datos y roadmap
```

## Documentación

- [Documentación técnica](docs/documentacion_tecnica.md)
- [Manual de operación](docs/manual_usuario.md)
- [Configuración](docs/configuraciones.md)
- [Esquema de base de datos](docs/esquema_bd.md)
- [Diccionario de datos](docs/diccionario_datos.md)
- [Roadmap](docs/roadmap/roadmap_erpsimplify.md)

## Estado y continuidad

La prioridad vigente es cerrar la consistencia UI/UX y después abordar validaciones, persistencia de proveedores, exportación PDF/Excel completa, historial de clientes e inteligencia de negocio. El punto de continuidad está en el [roadmap](docs/roadmap/roadmap_erpsimplify.md).
