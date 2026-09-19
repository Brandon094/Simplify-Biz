# Esquema de Base de Datos — ERP+ Business (v2.0.0)

> **Definición de Arquitectura DDL, Relaciones y Estrategia de Migración**

---

## 1. Motor & Configuraciones JDBC

| Parámetro | Configuración |
| :--- | :--- |
| Motor DB | **SQLite 3.46+** (Modo Embebido) |
| Driver JDBC | `org.xerial:sqlite-jdbc:3.46.1.0` |
| Conexión | Singleton `GestorConexion` compartida |
| Journal Mode | `PRAGMA journal_mode=WAL;` |
| Busy Timeout | `PRAGMA busy_timeout=5000;` |
| Foreign Keys | `PRAGMA foreign_keys=ON;` |

---

## 2. Diagrama Entidad-Relación ASCII

```text
┌─────────────────┐     ┌──────────────────────┐     ┌──────────────────────┐
│    usuarios     │     │      productos       │     │      categorias      │
├─────────────────┤     ├──────────────────────┤     ├──────────────────────┤
│ id (PK)         │     │ id (PK)              │     │ id (PK)              │
│ nombre          │     │ producto             │     │ nombre (UNIQUE)      │
│ telefono        │     │ precio               │     └──────────────────────┘
│ email           │     │ precio_costo         │
│ rol             │     │ cantidad             │
│ contraseña      │     │ codigo (SKU)         │
│ no_cc           │     │ categoria (TEXT)     │
└─────────────────┘     └──────────────────────┘

┌─────────────────────┐      ┌─────────────────────────┐
│       ventas        │      │     detalles_venta      │
├─────────────────────┤      ├─────────────────────────┤
│ id (PK)             │◄─────┤ venta_id (FK)           │
│ cliente             │      │ id (PK)                 │
│ cc_cliente          │      │ producto                │
│ vendedor            │      │ cantidad                │
│ fecha               │      │ codigo                  │
│ total               │      │ precio                  │
│ metodo_pago         │      │ precio_costo            │
│ pago_confirmado     │      │ total                   │
└──────────┬──────────┘      └─────────────────────────┘
           │
           │                 ┌─────────────────────────┐
           │                 │     abonos_cartera      │
           │                 ├─────────────────────────┤
           └────────────────┤ venta_id (FK)           │
                             │ id (PK)                 │
                             │ monto                   │
                             │ fecha                   │
                             │ usuario_registro        │
                             │ metodo_pago             │
                             └─────────────────────────┘
```

---

## 3. Sentencias DDL Oficiales

### 3.1 `usuarios`
```sql
CREATE TABLE IF NOT EXISTS usuarios (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT    NOT NULL,
    telefono    TEXT    NOT NULL,
    email       TEXT    NOT NULL,
    rol         INTEGER NOT NULL,
    contraseña  TEXT    NOT NULL,
    no_cc       TEXT
);
```

### 3.2 `productos`
```sql
CREATE TABLE IF NOT EXISTS productos (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    producto     TEXT    NOT NULL,
    precio       REAL    NOT NULL,
    precio_costo REAL    DEFAULT 0.0,
    cantidad     INTEGER NOT NULL,
    codigo       TEXT    NOT NULL,
    categoria    TEXT
);
```

### 3.3 `ventas`
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

### 3.4 `detalles_venta`
```sql
CREATE TABLE IF NOT EXISTS detalles_venta (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id     INTEGER NOT NULL,
    producto     TEXT    NOT NULL,
    cantidad     INTEGER NOT NULL,
    codigo       TEXT    NOT NULL,
    precio       REAL    NOT NULL,
    precio_costo REAL    DEFAULT 0.0,
    total        REAL    NOT NULL,
    descuento    REAL    DEFAULT 0.0,
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE
);
```

### 3.5 `categorias`
```sql
CREATE TABLE IF NOT EXISTS categorias (
    id     INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT UNIQUE NOT NULL
);
```

### 3.6 `abonos_cartera`
```sql
CREATE TABLE IF NOT EXISTS abonos_cartera (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id         INTEGER NOT NULL,
    monto            REAL    NOT NULL,
    fecha            TEXT    NOT NULL,
    usuario_registro TEXT    NOT NULL,
    metodo_pago      TEXT    NOT NULL DEFAULT 'Efectivo',
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE
);
```

### 3.7 `proveedores`
```sql
CREATE TABLE IF NOT EXISTS proveedores (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre    TEXT    NOT NULL,
    nit       TEXT    UNIQUE NOT NULL,
    telefono  TEXT,
    email     TEXT,
    direccion TEXT
);
```

### 3.8 `compras`
```sql
CREATE TABLE IF NOT EXISTS compras (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    proveedor_id     INTEGER,
    proveedor_nombre TEXT    NOT NULL,
    proveedor_nit    TEXT    NOT NULL,
    num_factura      TEXT    NOT NULL,
    usuario_registro TEXT    NOT NULL,
    fecha            DATE    NOT NULL,
    total            REAL    NOT NULL,
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE SET NULL
);
```

### 3.9 `detalles_compra`
```sql
CREATE TABLE IF NOT EXISTS detalles_compra (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    compra_id    INTEGER NOT NULL,
    producto     TEXT    NOT NULL,
    codigo       TEXT    NOT NULL,
    cantidad     INTEGER NOT NULL,
    precio_costo REAL    NOT NULL,
    subtotal     REAL    NOT NULL,
    FOREIGN KEY (compra_id) REFERENCES compras(id) ON DELETE CASCADE
);
```

---

## 4. Migración Automática de Esquemas (`migrarColumnaSegura`)

`ConexionDB` incluye un mecanismo de auto-migración defensiva en tiempo de arranque:
```java
private static void migrarColumnaSegura(Connection conn, String tabla, String columna, String definicion) {
    if (!existeColumna(conn, tabla, columna)) {
        String sql = "ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + definicion;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}
```
Esto garantiza que bases de datos creadas en versiones previas reciban las tablas y columnas necesarias (`precio_costo`, `abonos_cartera`) automáticamente sin requerir intervención manual del usuario.

