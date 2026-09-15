-- ====================================================================
-- SCRIPT MAESTRO DE POBLADO DEMO — ERP+ BUSINESS / SIMPLIFY-BIZ (v1.3.0)
-- Almacén de Repuestos de Motos, Lubricantes, Lujos, Compras y Cartera
-- Diseñado para Demostración Comercial Completa a Clientes Potenciales
-- Incluye: DDL Completo, Usuarios Multirol, Proveedores, Categorías, 38 Productos,
--          Histórico de Ventas, Abonos a Cartera, Compras e Ingresos de Almacén.
-- ====================================================================

PRAGMA foreign_keys = ON;
PRAGMA journal_mode = WAL;

-- 1. CREACIÓN DE TABLAS (DDL Defensivo)

CREATE TABLE IF NOT EXISTS usuarios (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT    NOT NULL,
    telefono    TEXT    NOT NULL,
    email       TEXT    NOT NULL,
    rol         INTEGER NOT NULL,
    contraseña  TEXT    NOT NULL
);

CREATE TABLE IF NOT EXISTS categorias (
    id     INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS productos (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    producto     TEXT    NOT NULL,
    precio       REAL    NOT NULL,
    precio_costo REAL    DEFAULT 0.0,
    cantidad     INTEGER NOT NULL,
    codigo       TEXT    NOT NULL,
    categoria    TEXT
);

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

CREATE TABLE IF NOT EXISTS detalles_venta (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id     INTEGER NOT NULL,
    producto     TEXT    NOT NULL,
    cantidad     INTEGER NOT NULL,
    codigo       TEXT    NOT NULL,
    precio       REAL    NOT NULL,
    precio_costo REAL    DEFAULT 0.0,
    total        REAL    NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS abonos_cartera (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    venta_id    INTEGER NOT NULL,
    monto       REAL    NOT NULL,
    fecha       DATE    NOT NULL,
    metodo_pago TEXT    NOT NULL,
    observacion TEXT,
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS proveedores (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre    TEXT    NOT NULL,
    nit       TEXT    UNIQUE NOT NULL,
    telefono  TEXT,
    email     TEXT,
    direccion TEXT
);

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

CREATE TABLE IF NOT EXISTS configuracion (
    id                      INTEGER PRIMARY KEY,
    ultimoNumeroCotizacion  TEXT
);

-- 2. LIMPIEZA DE REGISTROS PREVIOS (Restablecimiento Completo)
DELETE FROM detalles_compra;
DELETE FROM compras;
DELETE FROM proveedores;
DELETE FROM abonos_cartera;
DELETE FROM detalles_venta;
DELETE FROM ventas;
DELETE FROM productos;
DELETE FROM categorias;
DELETE FROM usuarios;
DELETE FROM configuracion;

-- 3. REGISTRO DE USUARIOS
INSERT INTO usuarios (id, nombre, telefono, email, rol, contraseña) VALUES
(1, 'brandon', '3001234567', 'admin@erpplus.com', 1, '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5'),
(2, 'admin', '3000000000', 'gerencia@erpplus.com', 1, '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918'),
(3, 'Carlos Pérez', '3109876543', 'carlos.vendedor@erpplus.com', 0, '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5'),
(4, 'Ana Gómez', '3204567890', 'ana.vendedor@erpplus.com', 0, '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5'),
(5, 'Taller MotoSpeed S.A.S.', '3157778899', 'contacto@motospeed.com', 2, 'N/A'),
(6, 'MotoRepuestos El Paisa', '800456789', 'ventas@elpaisa.com', 2, 'N/A'),
(7, 'Juan Carlos Rodríguez', '1098765432', 'juan.rodriguez@gmail.com', 2, 'N/A'),
(8, 'Distribuidora Lujomotos', '900111222', 'lujomotos@hotmail.com', 2, 'N/A'),
(9, 'Taller Central de Motos', '900888777', 'centralmotos@gmail.com', 2, 'N/A');

-- 4. REGISTRO DE PROVEEDORES
INSERT INTO proveedores (id, nombre, nit, telefono, email, direccion) VALUES
(1, 'Importadora Japonesa de Repuestos S.A.', '890123456-7', '6013334455', 'ventas@impojapon.com.co', 'Calle 13 # 32-45, Bogotá'),
(2, 'Distribuidora Motos del Valle Ltda.', '900456789-1', '6024445566', 'pedidos@motosdelvalle.com', 'Carrera 10 # 15-20, Cali'),
(3, 'Lubricantes y Químicos Colombia S.A.S.', '800987654-2', '6042223344', 'contacto@lubriquim.com.co', 'Av. Industriales # 45-67, Medellín'),
(4, 'Lujos y Accesorios Racing Parts', '901234567-8', '6076667788', 'ventas@racingparts.co', 'Calle 45 # 23-11, Bucaramanga');

-- 5. REGISTRO DE CATEGORÍAS
INSERT INTO categorias (id, nombre) VALUES 
(1, 'REPUESTOS MOTOR'),
(2, 'FRENOS Y SUSPENSIÓN'),
(3, 'LUBRICANTES Y FLUIDOS'),
(4, 'ELÉCTRICO Y ILUMINACIÓN'),
(5, 'TRANSMISIÓN Y ARRASTRE'),
(6, 'LLANTAS Y NEUMÁTICOS'),
(7, 'LUJOS Y ACCESORIOS'),
(8, 'HERRAMIENTAS Y TALLER'),
(9, 'INDUMENTARIA Y PROTECCIÓN');

-- 6. REGISTRO DE PRODUCTOS (38 ítems con Costos y Stock)
INSERT INTO productos (id, producto, precio, precio_costo, cantidad, codigo, categoria) VALUES
(1, 'Kit Cilindro NKD 125 AKT', 185000, 115000, 18, 'MOT-001', 'REPUESTOS MOTOR'),
(2, 'Kit Piston y Anillos NS 200 Standard', 95000, 58000, 14, 'MOT-002', 'REPUESTOS MOTOR'),
(3, 'Válvulas Admisión y Escape FZ 16', 42000, 24000, 22, 'MOT-003', 'REPUESTOS MOTOR'),
(4, 'Carburador Completo CG 125 / GN 125', 125000, 78000, 10, 'MOT-004', 'REPUESTOS MOTOR'),
(5, 'Cárter Derecha Pulsar 180 UG', 210000, 135000, 3, 'MOT-005', 'REPUESTOS MOTOR'),

(6, 'Pastillas de Freno Delanteras NMAX 155 Sinterizadas', 38000, 21000, 35, 'FRE-001', 'FRENOS Y SUSPENSIÓN'),
(7, 'Banda de Freno Trasera Boxer CT 100 Tecno', 22000, 12000, 40, 'FRE-002', 'FRENOS Y SUSPENSIÓN'),
(8, 'Disco de Freno Lobulado Delantero Duke 200/390', 145000, 88000, 6, 'FRE-003', 'FRENOS Y SUSPENSIÓN'),
(9, 'Amortiguadores Traseros Hidráulicos NKD 125 (Par)', 110000, 68000, 8, 'FRE-004', 'FRENOS Y SUSPENSIÓN'),
(10, 'Cuna de Dirección Rodamientos Cónicos GIXER 150', 35000, 19000, 15, 'FRE-005', 'FRENOS Y SUSPENSIÓN'),

(11, 'Aceite Motul 7100 4T 10W40 100% Sintético 1L', 68000, 46000, 48, 'LUB-001', 'LUBRICANTES Y FLUIDOS'),
(12, 'Aceite Yamalube 4T 20W50 Mineral 1L', 32000, 21000, 60, 'LUB-002', 'LUBRICANTES Y FLUIDOS'),
(13, 'Aceite Mobil Super Moto 4T 15W50 Semi-Sintético 1L', 38000, 24000, 32, 'LUB-003', 'LUBRICANTES Y FLUIDOS'),
(14, 'Líquido de Frenos DOT 4 Motul 500ml', 25000, 14500, 25, 'LUB-004', 'LUBRICANTES Y FLUIDOS'),
(15, 'Grasa Sintética para Cadenas Motul C2 Chain Lube 400ml', 45000, 28000, 20, 'LUB-005', 'LUBRICANTES Y FLUIDOS'),

(16, 'Batería de Gel YTZ7S 12V 6Ah Libre de Mantenimiento', 115000, 72000, 12, 'ELE-001', 'ELÉCTRICO Y ILUMINACIÓN'),
(17, 'Faro Principal LED H4 8000 LM Ojo de Ángel Redondo', 55000, 31000, 18, 'ELE-002', 'ELÉCTRICO Y ILUMINACIÓN'),
(18, 'Bujía Iridium IX NGK CR8EIX', 38000, 22000, 30, 'ELE-003', 'ELÉCTRICO Y ILUMINACIÓN'),
(19, 'Regulador de Voltaje Pulsar 200 NS HD Original', 88000, 54000, 7, 'ELE-004', 'ELÉCTRICO Y ILUMINACIÓN'),
(20, 'Stop Trasero LED Integrado con Direccionales Universal', 48000, 26000, 11, 'ELE-005', 'ELÉCTRICO Y ILUMINACIÓN'),

(21, 'Kit de Arrastre Cassarella Reforzado O-Ring NKD 125', 135000, 82000, 16, 'TRA-001', 'TRANSMISIÓN Y ARRASTRE'),
(22, 'Cadena Choho 428H-132 Llabes Reforzada Dorada', 62000, 37000, 25, 'TRA-002', 'TRANSMISIÓN Y ARRASTRE'),
(23, 'Piñón de Ataque 14T FZ 16 / FZ 2.0 Steel Racing', 18000, 9500, 30, 'TRA-003', 'TRANSMISIÓN Y ARRASTRE'),
(24, 'Catalina Trasera 43T Pulsar 180 Acerada', 45000, 26000, 14, 'TRA-004', 'TRANSMISIÓN Y ARRASTRE'),

(25, 'Llanta Delantera Michelin Pilot Street 2 90/90-17 Sellomatic', 175000, 118000, 10, 'LLA-001', 'LLANTAS Y NEUMÁTICOS'),
(26, 'Llanta Trasera Pirelli Diablo Rosso III 140/70-17', 38000, 248000, 5, 'LLA-002', 'LLANTAS Y NEUMÁTICOS'),
(27, 'Neumático Kenda 2.75/3.00-18 Válvula TR4', 24000, 13500, 40, 'LLA-003', 'LLANTAS Y NEUMÁTICOS'),

(28, 'Espejos Tipo Rizoma Tomok Aluminio CNC Negros', 68000, 39000, 15, 'LUJ-001', 'LUJOS Y ACCESORIOS'),
(29, 'Manubrio Protaper EVO Carbono 28mm con Almohadilla', 120000, 71000, 8, 'LUJ-002', 'LUJOS Y ACCESORIOS'),
(30, 'Manijas Abatibles CNC Regulables Pulsar NS 200 (Par)', 58000, 33000, 12, 'LUJ-003', 'LUJOS Y ACCESORIOS'),
(31, 'Sliders de Motor Protector Anti-Caída Titanium NKD 125', 85000, 49000, 9, 'LUJ-004', 'LUJOS Y ACCESORIOS'),

(32, 'Copa Extractora Filtro de Aceite Universal 14 Flautas', 28000, 15000, 6, 'HER-001', 'HERRAMIENTAS Y TALLER'),
(33, 'Prensa Cadenas Profesional Heavy Duty para Motos 420-530', 48000, 27000, 4, 'HER-002', 'HERRAMIENTAS Y TALLER'),
(34, 'Multímetro Digital Automotriz con Probador de Bujías', 75000, 42000, 5, 'HER-003', 'HERRAMIENTAS Y TALLER'),

(35, 'Casco Integral Shaft Pro 526 Solid Certificado ECE 2206 M', 240000, 155000, 6, 'IND-001', 'INDUMENTARIA Y PROTECCIÓN'),
(36, 'Guantes de Cuero Térmicos Impermeables con Nudillos de Carbono L', 85000, 48000, 12, 'IND-002', 'INDUMENTARIA Y PROTECCIÓN'),
(37, 'Impermeable Tipo Pantaloneta y Chaqueta 100% Vulcalizado XL', 65000, 38000, 15, 'IND-003', 'INDUMENTARIA Y PROTECCIÓN'),
(38, 'Chaqueta de Protección Racing con Certificación CE Nivel 2 L', 290000, 185000, 4, 'IND-004', 'INDUMENTARIA Y PROTECCIÓN');

-- 7. REGISTRO DE HISTÓRICO DE COMPRAS (Entradas a Bodega)
INSERT INTO compras (id, proveedor_id, proveedor_nombre, proveedor_nit, num_factura, usuario_registro, fecha, total) VALUES
(1, 1, 'Importadora Japonesa de Repuestos S.A.', '890123456-7', 'FAC-JPN-8849', 'admin', '2026-09-01', 3450000),
(2, 3, 'Lubricantes y Químicos Colombia S.A.S.', '800987654-2', 'FACT-LUB-2026', 'admin', '2026-09-03', 2840000),
(3, 4, 'Lujos y Accesorios Racing Parts', '901234567-8', 'INV-RACING-405', 'Carlos Pérez', '2026-09-07', 1250000);

INSERT INTO detalles_compra (id, compra_id, producto, codigo, cantidad, precio_costo, subtotal) VALUES
(1, 1, 'Kit Cilindro NKD 125 AKT', 'MOT-001', 20, 115000, 2300000),
(2, 1, 'Kit Piston y Anillos NS 200 Standard', 'MOT-002', 15, 58000, 870000),
(3, 1, 'Válvulas Admisión y Escape FZ 16', 'MOT-003', 10, 24000, 240000),
(4, 2, 'Aceite Motul 7100 4T 10W40 100% Sintético 1L', 'LUB-001', 50, 46000, 2300000),
(5, 2, 'Aceite Yamalube 4T 20W50 Mineral 1L', 'LUB-002', 20, 27000, 540000),
(6, 3, 'Espejos Tipo Rizoma Tomok Aluminio CNC Negros', 'LUJ-001', 15, 39000, 585000),
(7, 3, 'Manubrio Protaper EVO Carbono 28mm con Almohadilla', 'LUJ-002', 8, 71000, 568000),
(8, 3, 'Sliders de Motor Protector Anti-Caída Titanium NKD 125', 'LUJ-004', 2, 48500, 97000);

-- 8. REGISTRO DE HISTÓRICO DE VENTAS (Muestra variada para Dashboard, Cartera y Reportes)
INSERT INTO ventas (id, cliente, cc_cliente, vendedor, fecha, total, metodo_pago, pago_confirmado) VALUES
(1, 'Taller MotoSpeed S.A.S.', '3157778899', 'Carlos Pérez', '2026-09-08', 545000, 'Efectivo', 'pagado'),
(2, 'MotoRepuestos El Paisa', '800456789', 'Ana Gómez', '2026-09-09', 420000, 'Transferencia', 'pagado'),
(3, 'Juan Carlos Rodríguez', '1098765432', 'Carlos Pérez', '2026-09-10', 136000, 'Efectivo', 'pagado'),
(4, 'Distribuidora Lujomotos', '900111222', 'brandon', '2026-09-11', 890000, 'Crédito', 'deudor'),
(5, 'Taller Central de Motos', '900888777', 'Ana Gómez', '2026-09-12', 680000, 'Crédito', 'deudor'),
(6, 'CONSUMIDOR FINAL', 'N/A', 'Carlos Pérez', '2026-09-12', 68000, 'Efectivo', 'pagado'),
(7, 'CONSUMIDOR FINAL', 'N/A', 'Ana Gómez', '2026-09-13', 175000, 'Efectivo', 'pagado'),
(8, 'Taller MotoSpeed S.A.S.', '3157778899', 'Carlos Pérez', '2026-09-13', 290000, 'Transferencia', 'pagado'),
(9, 'Juan Carlos Rodríguez', '1098765432', 'Ana Gómez', '2026-09-14', 240000, 'Efectivo', 'pagado'),
(10, 'MotoRepuestos El Paisa', '800456789', 'Carlos Pérez', '2026-09-14', 450000, 'Crédito', 'deudor');

-- 9. DETALLES DE VENTAS
INSERT INTO detalles_venta (id, venta_id, producto, cantidad, codigo, precio, precio_costo, total) VALUES
(1, 1, 'Kit Cilindro NKD 125 AKT', 2, 'MOT-001', 185000, 115000, 370000),
(2, 1, 'Michelin Pilot Street 2 90/90-17', 1, 'LLA-001', 175000, 118000, 175000),
(3, 2, 'Kit de Arrastre Cassarella NKD 125', 2, 'TRA-001', 135000, 82000, 270000),
(4, 2, 'Aceite Motul 7100 4T 10W40 1L', 2, 'LUB-001', 68000, 46000, 136000),
(5, 3, 'Aceite Motul 7100 4T 10W40 1L', 2, 'LUB-001', 68000, 46000, 136000),
(6, 4, 'Casco Integral Shaft Pro 526', 2, 'IND-001', 240000, 155000, 480000),
(7, 4, 'Chaqueta de Protección Racing CE L', 1, 'IND-004', 290000, 185000, 290000),
(8, 4, 'Guantes de Cuero Térmicos L', 1, 'IND-002', 120000, 71000, 120000),
(9, 5, 'Disco de Freno Lobulado Duke 200', 2, 'FRE-003', 145000, 88000, 290000),
(10, 5, 'Batería de Gel YTZ7S 12V 6Ah', 2, 'ELE-001', 115000, 72000, 230000),
(11, 5, 'Amortiguadores Traseros NKD 125', 1, 'FRE-004', 160000, 95000, 160000),
(12, 6, 'Aceite Motul 7100 4T 10W40 1L', 1, 'LUB-001', 68000, 46000, 68000),
(13, 7, 'Llanta Delantera Michelin Pilot Street 2', 1, 'LLA-001', 175000, 118000, 175000),
(14, 8, 'Chaqueta de Protección Racing CE L', 1, 'IND-004', 290000, 185000, 290000),
(15, 9, 'Casco Integral Shaft Pro 526', 1, 'IND-001', 240000, 155000, 240000),
(16, 10, 'Kit Cilindro NKD 125 AKT', 1, 'MOT-001', 185000, 115000, 185000),
(17, 10, 'Kit de Arrastre Cassarella NKD 125', 1, 'TRA-001', 135000, 82000, 135000),
(18, 10, 'Espejos Tipo Rizoma Tomok Aluminio', 1, 'LUJ-001', 130000, 78000, 130000);

-- 10. ABONOS DE CARTERA
INSERT INTO abonos_cartera (id, venta_id, monto, fecha, metodo_pago, observacion) VALUES
(1, 4, 300000, '2026-09-12', 'Transferencia', 'Abono inicial 35% recibido vía Nequi'),
(2, 5, 200000, '2026-09-13', 'Efectivo', 'Pago parcial en caja principal por cliente');

-- 11. VALORES INICIALES DE CONFIGURACIÓN
INSERT OR IGNORE INTO configuracion (id, ultimoNumeroCotizacion) VALUES (1, '20260914-001');

PRAGMA foreign_keys = ON;
