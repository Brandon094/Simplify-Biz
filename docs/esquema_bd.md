# Esquema de base de datos - ERP+ Business

ERP+ Business utiliza SQLite como motor local. No requiere un servidor de base de datos y sus archivos pueden respaldarse y trasladarse con cuidado.

## Tablas

### `usuarios`

```sql
CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    telefono TEXT NOT NULL,
    email TEXT NOT NULL,
    rol INTEGER NOT NULL,
    contraseña TEXT NOT NULL
);
```

### `productos`

```sql
CREATE TABLE IF NOT EXISTS productos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    producto TEXT NOT NULL,
    precio REAL NOT NULL,
    cantidad INTEGER NOT NULL,
    codigo TEXT NOT NULL,
    categoria TEXT
);
```

### `ventas`

```sql
CREATE TABLE IF NOT EXISTS ventas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente TEXT NOT NULL,
    cc_cliente TEXT NOT NULL,
    vendedor TEXT NOT NULL,
    fecha DATE NOT NULL,
    total REAL NOT NULL,
    metodo_pago TEXT NOT NULL,
    pago_confirmado TEXT
);
```

### `detalles_venta`

```sql
CREATE TABLE IF NOT EXISTS detalles_venta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id INTEGER NOT NULL,
    producto TEXT NOT NULL,
    cantidad INTEGER NOT NULL,
    codigo TEXT NOT NULL,
    precio REAL NOT NULL,
    total REAL NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES ventas(id)
);
```

### `configuracion`

```sql
CREATE TABLE IF NOT EXISTS configuracion (
    id INTEGER PRIMARY KEY,
    ultimoNumeroCotizacion TEXT
);
```

## Reglas de operación

- `ventas` y `detalles_venta` se relacionan mediante `detalles_venta.venta_id`.
- Una venta puede usar `CONSUMIDOR FINAL` cuando no se suministran datos personales.
- El cliente genérico no requiere un registro en `usuarios`.
- El stock se descuenta al confirmar la venta y la transacción se revierte si falla la actualización.
- Actualmente no existe una tabla propia de proveedores; `ProvidersPage` usa datos de demostración y no representa persistencia real.

## Inicialización y evolución

Las tablas se crean con `CREATE TABLE IF NOT EXISTS` durante el arranque. Este mecanismo no realiza migraciones de columnas para bases existentes. Todo cambio futuro del esquema debe incluir una migración explícita y una copia de seguridad previa.
