# Esquema de Base de Datos - Simplify Biz

El sistema utiliza **SQLite** como motor de base de datos, lo que permite que sea portable y no requiera una instalación de servidor de base de datos compleja.

## Tablas del Sistema

### 1. usuarios
Almacena la información de los usuarios que acceden al sistema.
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

### 2. productos
Gestiona el inventario de productos disponibles para la venta.
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

### 3. ventas
Registra el encabezado de las transacciones de venta.
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

### 4. detalles_venta
Almacena el detalle de los productos incluidos en cada venta (relación muchos a uno con `ventas`).
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

### 5. configuracion
Almacena configuraciones generales del sistema, como el contador de cotizaciones.
```sql
CREATE TABLE IF NOT EXISTS configuracion (
    id INTEGER PRIMARY KEY,
    ultimoNumeroCotizacion TEXT
);
```
