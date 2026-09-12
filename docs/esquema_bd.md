# Esquema de Base de Datos — ERP+ Business

> Definiciones DDL de todas las tablas SQLite, relaciones, reglas de operación y estrategia de evolución del esquema.

---

## 1. Motor y Configuración

| Propiedad | Valor |
| :--- | :--- |
| Motor | SQLite 3.46+ (embebido) |
| Driver JDBC | `org.xerial:sqlite-jdbc:3.46.1.0` |
| Archivo | `db.db` (dentro de la carpeta configurada en `db.path`) |
| Journal Mode | WAL (Write-Ahead Logging) |
| Busy Timeout | 5000 ms |
| Foreign Keys | Habilitadas (`PRAGMA foreign_keys=ON`) |

---

## 2. Diagrama de Relaciones

```
┌─────────────────┐     ┌──────────────────────┐
│    usuarios      │     │      productos        │
│─────────────────│     │──────────────────────│
│ id (PK)         │     │ id (PK)              │
│ nombre          │     │ producto              │
│ telefono        │     │ precio               │
│ email           │     │ cantidad             │
│ rol             │     │ codigo               │
│ contraseña      │     │ categoria            │
└─────────────────┘     └──────────────────────┘

┌─────────────────────┐      ┌─────────────────────────┐
│       ventas         │      │     detalles_venta       │
│─────────────────────│      │─────────────────────────│
│ id (PK)             │◄─────│ venta_id (FK)            │
│ cliente             │      │ id (PK)                  │
│ cc_cliente          │      │ producto                 │
│ vendedor            │      │ cantidad                 │
│ fecha               │      │ codigo                   │
│ total               │      │ precio                   │
│ metodo_pago         │      │ total                    │
│ pago_confirmado     │      └─────────────────────────┘
└─────────────────────┘

┌─────────────────────────────┐
│       configuracion          │
│─────────────────────────────│
│ id (PK, siempre = 1)        │
│ ultimoNumeroCotizacion       │
└─────────────────────────────┘
```

---

## 3. Definiciones DDL

### 3.1 Tabla `usuarios`

Almacena administradores (rol 1), vendedores/empleados (rol 0) y clientes (rol 2).

```sql
CREATE TABLE IF NOT EXISTS usuarios (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT    NOT NULL,
    telefono    TEXT    NOT NULL,
    email       TEXT    NOT NULL,
    rol         INTEGER NOT NULL,
    contraseña  TEXT    NOT NULL
);
```

### 3.2 Tabla `productos`

Catálogo de productos con stock y categorización.

```sql
CREATE TABLE IF NOT EXISTS productos (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    producto  TEXT    NOT NULL,
    precio    REAL    NOT NULL,
    cantidad  INTEGER NOT NULL,
    codigo    TEXT    NOT NULL,
    categoria TEXT
);
```

### 3.3 Tabla `ventas`

Encabezado de cada transacción de venta.

```sql
CREATE TABLE IF NOT EXISTS ventas (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente         TEXT    NOT NULL,
    cc_cliente      TEXT    NOT NULL,
    vendedor        TEXT    NOT NULL,
    fecha           DATE    NOT NULL,
    total           REAL    NOT NULL,
    metodo_pago     TEXT    NOT NULL,
    pago_confirmado TEXT
);
```

### 3.4 Tabla `detalles_venta`

Líneas de detalle asociadas a una venta (relación 1:N).

```sql
CREATE TABLE IF NOT EXISTS detalles_venta (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id  INTEGER NOT NULL,
    producto  TEXT    NOT NULL,
    cantidad  INTEGER NOT NULL,
    codigo    TEXT    NOT NULL,
    precio    REAL    NOT NULL,
    total     REAL    NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES ventas(id)
);
```

### 3.5 Tabla `configuracion`

Configuración interna de la aplicación (registro único `id = 1`).

```sql
CREATE TABLE IF NOT EXISTS configuracion (
    id                      INTEGER PRIMARY KEY,
    ultimoNumeroCotizacion  TEXT
);
```

**Valor inicial:**

```sql
INSERT OR IGNORE INTO configuracion (id, ultimoNumeroCotizacion)
VALUES (1, '20241212-000');
```

---

## 4. Reglas de Operación

### 4.1 Relaciones

- `ventas` ↔ `detalles_venta`: Relación 1:N mediante `detalles_venta.venta_id → ventas.id`.
- Las foreign keys están habilitadas vía `PRAGMA foreign_keys=ON`.

### 4.2 Venta Mostrador

- Cuando el cliente no proporciona datos personales, se registra como:
  - `cliente = 'CONSUMIDOR FINAL'`
  - `cc_cliente = 'N/A'`
- **No se crea** un usuario ficticio en la tabla `usuarios`.

### 4.3 Integridad de Stock

- El stock se descuenta atómicamente al confirmar una venta dentro de una transacción SQL.
- Si falla la actualización de stock para cualquier producto, toda la transacción se revierte (`rollback`).
- La verificación `cantidad >= ?` en el `UPDATE` previene stocks negativos.

### 4.4 Datos Históricos

- `ventas` almacena nombres de cliente y vendedor como **texto plano** (snapshot histórico).
- No existe clave foránea hacia `usuarios`, lo que permite que los datos de venta persistan incluso si se elimina un usuario.

### 4.5 Seguridad

- Las consultas de listado de usuarios **excluyen** la columna `contraseña` (constante `COLUMNAS_LISTADO`).
- Las contraseñas se almacenan encriptadas mediante `Seguridad.encriptarContraseña()`.

### 4.6 Proveedores

- Actualmente **no existe** una tabla de proveedores.
- `ProvidersPage` utiliza datos de demostración en memoria y no representa persistencia real.
- La tabla se creará en la Fase 4 del roadmap.

---

## 5. Inicialización y Evolución

### 5.1 Creación Automática

Las tablas se crean con `CREATE TABLE IF NOT EXISTS` durante el arranque en `ConexionDB.inicializarBaseDeDatos()`. Este mecanismo:

- ✅ Crea tablas nuevas si no existen.
- ✅ No borra ni modifica tablas existentes.
- ✅ Inserta valores iniciales solo si no existen (`INSERT OR IGNORE`).
- ❌ **No realiza migraciones** de columnas para bases existentes.

### 5.2 Cambios Futuros del Esquema

Todo cambio futuro del esquema **debe**:

1. Respaldar la base de datos antes de aplicar cambios.
2. Implementar una migración explícita (ej: `ALTER TABLE ADD COLUMN`).
3. Actualizar este documento y el [Diccionario de Datos](diccionario_datos.md) en la misma tarea.
4. Verificar con `mvn test` que las pruebas siguen pasando.
