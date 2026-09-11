# Diccionario de Datos - Simplify Biz

Este documento detalla cada campo de las tablas de la base de datos.

## Tabla: `usuarios`
| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | INTEGER | Identificador único (Auto-incremental). |
| nombre | TEXT | Nombre completo del usuario. |
| telefono | TEXT | Número de contacto. |
| email | TEXT | Correo electrónico (usado para login/notificaciones). |
| rol | INTEGER | Nivel de permisos (ej: 1 para Admin, 2 para Vendedor). |
| contraseña | TEXT | Contraseña encriptada o plana (según implementación). |

## Tabla: `productos`
| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | INTEGER | Identificador único del producto. |
| producto | TEXT | Nombre del producto. |
| precio | REAL | Precio unitario de venta. |
| cantidad | INTEGER | Stock disponible. |
| codigo | TEXT | Código de barras o SKU del producto. |
| categoria | TEXT | Categoría a la que pertenece el producto. |

## Tabla: `ventas`
| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | INTEGER | Número de factura/venta. |
| cliente | TEXT | Nombre del cliente. |
| cc_cliente | TEXT | Cédula o NIT del cliente. |
| vendedor | TEXT | Nombre del usuario que realizó la venta. |
| fecha | DATE | Fecha de la transacción. |
| total | REAL | Valor total de la venta. |
| metodo_pago | TEXT | Efectivo, Transferencia, etc. |
| pago_confirmado | TEXT | Estado del pago (Si/No). |

## Tabla: `detalles_venta`
| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | INTEGER | ID del registro de detalle. |
| venta_id | INTEGER | Referencia a la tabla `ventas`. |
| producto | TEXT | Nombre del producto al momento de la venta. |
| cantidad | INTEGER | Cantidad vendida. |
| codigo | TEXT | Código del producto. |
| precio | REAL | Precio unitario al que se vendió. |
| total | REAL | Subtotal (cantidad * precio). |

## Tabla: `configuracion`
| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | INTEGER | ID único (usualmente 1). |
| ultimoNumeroCotizacion | TEXT | Formato `YYYYMMDD-XXX` para el control de documentos. |
