# Diccionario de datos - ERP+ Business

Este documento describe las tablas creadas por la aplicación y el uso funcional de cada campo.

## Tabla `usuarios`

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | INTEGER | Identificador único autoincremental. |
| nombre | TEXT | Nombre del usuario. |
| telefono | TEXT | Número de contacto. |
| email | TEXT | Correo del usuario. |
| rol | INTEGER | `1` administrador, `0` vendedor/empleado, `2` cliente. |
| contraseña | TEXT | Contraseña almacenada mediante el mecanismo de seguridad de la aplicación. |

## Tabla `productos`

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | INTEGER | Identificador único del producto. |
| producto | TEXT | Nombre del producto. |
| precio | REAL | Precio unitario de venta. |
| cantidad | INTEGER | Stock disponible. |
| codigo | TEXT | Código o SKU. |
| categoria | TEXT | Categoría del producto. |

## Tabla `ventas`

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | INTEGER | Identificador de la venta. |
| cliente | TEXT | Nombre histórico del cliente. |
| cc_cliente | TEXT | Cédula, NIT o `N/A` en venta mostrador. |
| vendedor | TEXT | Usuario que realizó la venta. |
| fecha | DATE | Fecha de la transacción. |
| total | REAL | Total de la venta. |
| metodo_pago | TEXT | Efectivo, crédito, transferencia u otro. |
| pago_confirmado | TEXT | Estado asociado al pago; crédito puede indicar `deudor`. |

## Tabla `detalles_venta`

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | INTEGER | Identificador del detalle. |
| venta_id | INTEGER | Referencia a `ventas.id`. |
| producto | TEXT | Producto al momento de la venta. |
| cantidad | INTEGER | Unidades vendidas. |
| codigo | TEXT | Código del producto. |
| precio | REAL | Precio unitario vendido. |
| total | REAL | Subtotal del detalle. |

## Tabla `configuracion`

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | INTEGER | Identificador, normalmente `1`. |
| ultimoNumeroCotizacion | TEXT | Formato `YYYYMMDD-XXX`. |

## Convenciones funcionales

- `rol = 1`: administrador.
- `rol = 0`: vendedor o empleado.
- `rol = 2`: cliente.
- Clientes se consultan desde la vista de clientes, pero se crean desde una venta cuando entregan sus datos.
- Las ventas mostrador usan `CONSUMIDOR FINAL` y `N/A` y no crean un usuario ficticio.
- `contraseña` no debe mostrarse en interfaces de consulta.
- `ventas` conserva datos históricos como texto y no depende de una clave foránea hacia `usuarios`.
