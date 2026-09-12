# ERP+ Business

ERP+ Business es una aplicación de escritorio para pequeñas y medianas empresas. Centraliza inventario, ventas, clientes, empleados, reportes y configuración en una ventana operativa con navegación lateral.

El proyecto está desarrollado con Java 25, Swing, FlatLaf y SQLite. La versión actual prioriza la modernización UI/UX sin cambiar las reglas de negocio existentes. Incluye soporte de **tema oscuro y claro** con un toggle en el sidebar.

## Inicio rápido

### Requisitos

- JDK 25 o superior.
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

1. `Main` configura el tema FlatLaf (oscuro por defecto) y los estilos globales.
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

## Temas visuales

La aplicación soporta **tema oscuro** (predeterminado) y **tema claro**. Puedes alternar en cualquier momento desde el botón del **sidebar**:

- En oscuro aparece **Modo claro** (icono de sol); al pulsarlo la interfaz cambia a la paleta clara.
- En claro aparece **Modo oscuro** (icono de luna); al pulsarlo vuelves a la paleta oscura.

Todas los colores están centralizados en `ThemeConstants` (principio DRY), por lo que el cambio se aplica a toda la aplicación de forma consistente:

| Recurso | Oscuro | Claro |
| :--- | :--- | :--- |
| Fondo general | `#0B0E14` | `#EEF2F7` |
| Fondo del sidebar | `#121620` | `#FFFFFF` |
| Fondo de tarjetas | `#1E293B` | `#FFFFFF` |
| Texto principal | `#FFFFFF` | `#0F172A` |
| Texto secundario | `#94A3B8` | `#475569` |
| Acento (morado) | `#A855F7` | `#7C3AED` |

## Decisiones de UI/UX

- Ventana única con contenido dinámico y sidebar por rol.
- Componentes Swing reutilizables organizados por atoms, molecules y organisms.
- Iconos SVG locales en `src/main/resources/icons` mediante `FlatSVGIcon`.
- Estados vacíos explícitos para listas, ventas e inventario.
- Tablas con encabezados, filas alternadas, selección y columnas técnicas ocultas donde corresponde.
- Paletas de color únicas en `ThemeConstants` con alternancia oscura/clara.

## Estructura principal

```text
src/main/java/com/mycompany/zl_solucion_integral/
  config/       Conexión, inicialización, seguridad, rutas y utilidades
  controllers/  Operaciones de negocio y acceso a datos
  models/       Entidades del dominio
  views/        Pantallas y componentes Swing
    components/
      atoms/         Botones, paneles redondeados, gráficos y toggle de tema
      molecules/     Ítems del sidebar
      organisms/     Sidebar completo y tarjetas de métricas
src/main/resources/icons/  Recursos SVG de la interfaz
docs/                         Documentación técnica, manual, datos y roadmap
```

## Documentación

- [Documentación técnica](docs/documentacion_tecnica.md)
- [Manual de operación](docs/manual_usuario.md)
- [Configuración](docs/configuraciones.md)
- [Esquema de base de datos](docs/esquema_bd.md)
- [Diccionario de datos](docs/diccionario_datos.md)
- [Roadmap](docs/roadmap/roadmap.md)

## Estado y continuidad

La prioridad vigente es cerrar la consistencia UI/UX (incluida la alternancia de tema ya implementada) y después abordar validaciones, persistencia de proveedores, exportación PDF/Excel completa, historial de clientes e inteligencia de negocio. El punto de continuidad está en el [roadmap](docs/roadmap/roadmap.md).
