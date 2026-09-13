# Diccionario de Datos — ERP+ Business (v1.3.0)

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
| `contraseña` | `TEXT` | No | Hash criptográfico SHA-256 de 64 caracteres Hex. Jamás expuesto en SELECTs de listado. |

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
| `precio` | `REAL` | No | Precio unitario cobrado al cliente. |
| `precio_costo` | `REAL` | Sí | Snapshot del costo de adquisición unitario al momento de la venta ($). |
| `total` | `REAL` | No | Subtotal de la línea (`precio * cantidad`). |

---

## 5. Tabla `categorias`

Maestro dinámico de categorías de inventario.

| Campo | Tipo SQL | Nullable | Descripción Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | No | PK autoincremental. |
| `nombre` | `TEXT` | No | Nombre único de la categoría (`UNIQUE NOT NULL`). |

---

## 6. Fórmulas Financieras & Reglas de Snapshot

- **Utilidad Neta Real ($)**:
  $$\text{Utilidad} = \sum_{\text{detalles}} \left( \text{total} - (\text{precio\_costo} \times \text{cantidad}) \right)$$
- **Inversión en Bodega ($)**:
  $$\text{Inversión} = \sum_{\text{productos}} \left( \text{precio\_costo} \times \text{cantidad} \right)$$
- **Margen Real (%)**:
  $$\text{Margen} = \left( \frac{\text{Utilidad Neta}}{\text{Ventas Totales}} \right) \times 100$$
