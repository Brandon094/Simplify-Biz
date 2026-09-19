# Diccionario de Datos — ERP+ Business (v2.0.0)

> **Especificación de Dominio de Datos & Diccionario Funcional SQL**  
> Definición detallada de estructuras de datos, tipos, claves, snapshots y reglas de cálculo contable.

---

## 1. Tabla `usuarios`

Entidad principal para autenticación multirol (Administradores, Vendedores/Empleados) y referencia de Clientes.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | PK autoincremental única. |
| `nombre` | `TEXT` | No | Nombre completo o razón social. Case-insensitive para autenticación. |
| `telefono` | `TEXT` | No | Teléfono de contacto (7–10 dígitos). Usado en recuperación de cuenta. |
| `email` | `TEXT` | No | Correo electrónico con regex validado. |
| `rol` | `INTEGER` | No | Roles: `1` = Administrador, `0` = Vendedor/Empleado, `2` = Cliente. |
| `contraseña` | `TEXT` | No | Hash criptográfico SHA-256 de 64 caracteres Hex para usuarios con acceso a sistema (Administradores/Empleados). |
| `no_cc` | `TEXT` | Sí | Número de Cédula / NIT del cliente en texto plano (ej: `83249810`). Usado para sugerencias y autocompletado en POS. |

---

## 2. Tabla `productos`

Catálogo maestro de inventario con costeo y categorías dinámicas.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | PK autoincremental única. |
| `producto` | `TEXT` | No | Nombre o descripción del artículo. |
| `precio` | `REAL` | No | Precio público de venta unitario ($). |
| `precio_costo` | `REAL` | Sí | Costo de adquisición unitario ($). Usado para Inversión y Utilidad. Default: `0.0`. |
| `cantidad` | `INTEGER` | No | Existencias disponibles en bodega. |
| `codigo` | `TEXT` | No | Código SKU o de barras (Único). |
| `categoria` | `TEXT` | Sí | Categoría vinculada a la tabla `categorias`. |

---

## 3. Tabla `ventas`

Encabezados transaccionales de venta (POS) y estado de cartera.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | PK autoincremental única de venta. |
| `cliente` | `TEXT` | No | Snapshot del nombre del comprador. En mostrador: `CONSUMIDOR FINAL`. |
| `cc_cliente` | `TEXT` | No | Snapshot de Cédula/NIT del cliente. En mostrador: `N/A`. |
| `vendedor` | `TEXT` | No | Nombre del usuario activo en la sesión. |
| `fecha` | `DATE` | No | Fecha de transacción (`yyyy-MM-dd`). |
| `total` | `REAL` | No | Valor neto facturado con descuentos aplicados ($). |
| `metodo_pago` | `TEXT` | No | `Efectivo`, `Transferencia` o `Crédito`. |
| `pago_confirmado` | `TEXT` | Sí | Estado del cobro: `'pagado'` (inmediato) o `'deudor'` (venta a plazo en cartera). |

---

## 4. Tabla `detalles_venta`

Líneas de transacción con snapshot histórico de costo. Relación N:1 con `ventas`.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | PK autoincremental de la línea. |
| `venta_id` | `INTEGER` | No | **FK** -> `ventas.id` (ON DELETE CASCADE). |
| `producto` | `TEXT` | No | Snapshot textual del artículo. |
| `cantidad` | `INTEGER` | No | Unidades facturadas. |
| `codigo` | `TEXT` | No | SKU del artículo vendido. |
| `precio` | `REAL` | No | Precio unitario de lista del artículo. |
| `precio_costo` | `REAL` | Sí | Snapshot del costo de adquisición unitario al momento de la venta ($). |
| `total` | `REAL` | No | Subtotal neto final de la línea con descuento aplicable ($). |
| `descuento` | `REAL` | Sí | Porcentaje de descuento (0 a 100%) otorgado a la línea en la venta. |

---

## 5. Tabla `categorias`

Maestro dinámico de categorías de inventario.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | PK autoincremental. |
| `nombre` | `TEXT` | No | Nombre único de la categoría (`UNIQUE NOT NULL`). |

---

## 6. Tabla `abonos_cartera`

Bitácora de pagos parciales realizados por clientes a ventas con estado `pago_confirmado = 'deudor'`.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | PK autoincremental del registro de abono. |
| `venta_id` | `INTEGER` | No | **FK** -> `ventas.id` (ON DELETE CASCADE). |
| `monto` | `REAL` | No | Cantidad de dinero abonada a la deuda ($). Debe ser > 0. |
| `fecha` | `TEXT` | No | Timestamp de registro (`yyyy-MM-dd HH:mm:ss`). |
| `usuario_registro` | `TEXT` | No | Nombre del usuario/empleado que recibió y registró el dinero. |
| `metodo_pago` | `TEXT` | No | Método utilizado para el abono (`Efectivo`, `Transferencia`). Default: `'Efectivo'`. |

---

## 7. Tabla `proveedores`

Maestro de proveedores de mercancía e insumos.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | PK autoincremental del proveedor. |
| `nombre` | `TEXT` | No | Nombre o Razón Social del proveedor. |
| `nit` | `TEXT` | No | Cédula o NIT único del proveedor (`UNIQUE NOT NULL`). |
| `telefono` | `TEXT` | Sí | Teléfono de contacto. |
| `email` | `TEXT` | Sí | Correo electrónico de contacto. |
| `direccion` | `TEXT` | Sí | Dirección física o fiscal. |

---

## 8. Tabla `compras`

Encabezados de órdenes de compra e ingresos de almacén.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | PK autoincremental de la orden de compra. |
| `proveedor_id` | `INTEGER` | Sí | **FK** -> `proveedores.id` (ON DELETE SET NULL). |
| `proveedor_nombre` | `TEXT` | No | Snapshot del nombre del proveedor. |
| `proveedor_nit` | `TEXT` | No | Snapshot del NIT/Cédula del proveedor. |
| `num_factura` | `TEXT` | No | Número de factura o remisión del proveedor. |
| `usuario_registro` | `TEXT` | No | Nombre del usuario que registró la compra. |
| `fecha` | `DATE` | No | Fecha de recepción (`yyyy-MM-dd`). |
| `total` | `REAL` | No | Total neto facturado por el proveedor ($). |

---

## 9. Tabla `detalles_compra`

Lineas de productos recibidos en una compra. Relación N:1 con `compras`.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | PK autoincremental del renglón. |
| `compra_id` | `INTEGER` | No | **FK** -> `compras.id` (ON DELETE CASCADE). |
| `producto` | `TEXT` | No | Nombre del producto ingresado. |
| `codigo` | `TEXT` | No | Código SKU del producto. |
| `cantidad` | `INTEGER` | No | Unidades recibidas e ingresadas a bodega. |
| `precio_costo` | `REAL` | No | Costo unitario de compra acordado ($). |
| `subtotal` | `REAL` | No | Subtotal del renglón (`cantidad * precio_costo`). |

---

## 10. Fórmulas Financieras & Reglas de Snapshot

- **Utilidad Neta Real ($)**:
  $$\text{Utilidad} = \sum_{\text{detalles}} \left( \text{total} - (\text{precio\_costo} \times \text{cantidad}) \right)$$
- **Inversión en Bodega ($)**:
  $$\text{Inversión} = \sum_{\text{productos}} \left( \text{precio\_costo} \times \text{cantidad} \right)$$
- **Margen Real (%)**:
  $$\text{Margen} = \left( \frac{\text{Utilidad Neta}}{\text{Ventas Totales}} \right) \times 100$$
- **Saldo Pendiente por Venta ($)**:
  $$\text{Saldo Pendiente} = \text{total\_venta} - \sum \text{abonos\_cartera.monto}$$
- **Cartera Pendiente Total ($)**:
  $$\text{Cartera Pendiente} = \sum_{\text{deudores}} \text{Saldo Pendiente}$$
- **Recaudado Mes ($)**:
  $$\text{Recaudado Mes} = \sum_{\text{abonos del mes}} \text{abonos\_cartera.monto}$$

