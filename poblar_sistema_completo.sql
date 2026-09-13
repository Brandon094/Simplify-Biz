-- ====================================================================
-- SCRIPT DE POBLADO COMPLETO DEL SISTEMA ERP+ BUSINESS
-- Almacén de Repuestos de Motos y Tornillería
-- Incluye: Usuarios (Vendedores/Clientes), Productos, Ventas e Histórico de 7 días
-- ====================================================================

-- 1. REGISTRAR USUARIOS (Administradores, Vendedores y Clientes)
-- Nota: La contraseña '12345' o por defecto está encriptada mediante la clase Seguridad
INSERT OR IGNORE INTO usuarios (id, nombre, telefono, email, rol, contraseña) VALUES
(1, 'brandon', '3001234567', 'admin@erpplus.com', 1, 'U2FsdGVkX1+v8Z1A...'), -- Admin principal
(2, 'Carlos Pérez', '3109876543', 'carlos.vendedor@erpplus.com', 0, '12345'), -- Empleado / Vendedor 1
(3, 'Ana Gómez', '3204567890', 'ana.vendedor@erpplus.com', 0, '12345'), -- Empleado / Vendedor 2
(4, 'Taller MotoSpeed S.A.S.', '3157778899', 'contacto@motospeed.com', 2, 'N/A'), -- Cliente 1
(5, 'MotoRepuestos El Paisa', '3012223344', 'ventas@elpaisa.com', 2, 'N/A'), -- Cliente 2
(6, 'Juan Carlos Rodríguez', '3189990011', 'juan.rodriguez@gmail.com', 2, 'N/A'), -- Cliente 3
(7, 'Distribuidora Lujomotos', '3114445566', 'lujomotos@hotmail.com', 2, 'N/A'); -- Cliente 4

-- 2. REGISTRAR CATEGORÍAS
INSERT OR IGNORE INTO categorias (nombre) VALUES 
('REPUESTOS MOTOR'),
('FRENOS Y SUSPENSIÓN'),
('KITS DE ARRASTRE'),
('LLANTAS Y NEUMÁTICOS'),
('LUBRICANTES Y FLUIDOS'),
('TORNILLERÍA Y FIJACIÓN'),
('ELÉCTRICO E ILUMINACIÓN'),
('ACCESORIOS Y CASCOS');

-- 3. REGISTRAR PRODUCTOS DE INVENTARIO
INSERT OR IGNORE INTO productos (id, producto, precio, cantidad, codigo, categoria) VALUES
(1, 'Kit Cilindro y Pistón Yamaha FZ 160', 185000.00, 12, 'MOT-001', 'REPUESTOS MOTOR'),
(2, 'Bujía Iridium NGK CPR8EA-9', 28000.00, 45, 'MOT-002', 'REPUESTOS MOTOR'),
(3, 'Kit de Empaques de Motor Bajaj Pulsar 200', 35000.00, 20, 'MOT-003', 'REPUESTOS MOTOR'),
(4, 'Carburador Mikuni 28mm Universal', 145000.00, 8, 'MOT-004', 'REPUESTOS MOTOR'),
(5, 'Filtro de Aire Alto Flujo K&N', 65000.00, 15, 'MOT-005', 'REPUESTOS MOTOR'),
(6, 'Válvulas de Admisión y Escape Honda CB125', 42000.00, 18, 'MOT-006', 'REPUESTOS MOTOR'),
(7, 'Pastillas de Freno Delanteras Brembo Sinterizadas', 48000.00, 30, 'FRE-001', 'FRENOS Y SUSPENSIÓN'),
(8, 'Banda de Freno Trasera Suzuki GN125', 25000.00, 25, 'FRE-002', 'FRENOS Y SUSPENSIÓN'),
(9, 'Disco de Freno Lobulado 260mm Pulsar 180', 110000.00, 10, 'FRE-003', 'FRENOS Y SUSPENSIÓN'),
(10, 'Amortiguadores Traseros Hidráulicos (Par)', 160000.00, 6, 'SUS-001', 'FRENOS Y SUSPENSIÓN'),
(11, 'Retenedores de Barras Delanteras 31mm (Par)', 18000.00, 40, 'SUS-002', 'FRENOS Y SUSPENSIÓN'),
(12, 'Kit de Arrastre Racing Cadena Reforzada 428H-136L', 135000.00, 14, 'ARR-001', 'KITS DE ARRASTRE'),
(13, 'Piñón de Salida 14T Acero 1045', 22000.00, 28, 'ARR-002', 'KITS DE ARRASTRE'),
(14, 'Catalina Trasera 42T Acero Reforzado', 45000.00, 16, 'ARR-003', 'KITS DE ARRASTRE'),
(15, 'Cadena Dorada con O-Ring 520H-120L', 120000.00, 9, 'ARR-004', 'KITS DE ARRASTRE'),
(16, 'Llanta Michelin Pilot Street 110/80-17', 240000.00, 10, 'LLA-001', 'LLANTAS Y NEUMÁTICOS'),
(17, 'Llanta Pirelli MT60 90/90-19 Doble Propósito', 285000.00, 7, 'LLA-002', 'LLANTAS Y NEUMÁTICOS'),
(18, 'Neumático / Cámara Reforzada Rinaldi 2.75-18', 32000.00, 35, 'LLA-003', 'LLANTAS Y NEUMÁTICOS'),
(19, 'Aceite Motul 7100 4T 10W40 100% Sintético 1L', 68000.00, 50, 'LUB-001', 'LUBRICANTES Y FLUIDOS'),
(20, 'Aceite Mobil Super Moto 4T 20W50 Mineral 1L', 34000.00, 80, 'LUB-002', 'LUBRICANTES Y FLUIDOS'),
(21, 'Líquido de Frenos DOT 4 Motul 500ml', 26000.00, 30, 'LUB-003', 'LUBRICANTES Y FLUIDOS'),
(22, 'Grasa para Cadenas Motul C2 Chain Lube 400ml', 45000.00, 24, 'LUB-004', 'LUBRICANTES Y FLUIDOS'),
(23, 'Tornillo Allen Bristol M6x20mm Inoxidable (Paquete x50)', 15000.00, 100, 'TOR-001', 'TORNILLERÍA Y FIJACIÓN'),
(24, 'Tornillo Hexagonal Grado 8 M8x30mm (Paquete x25)', 18000.00, 80, 'TOR-002', 'TORNILLERÍA Y FIJACIÓN'),
(25, 'Tuerca Autoblocante de Seguridad M6 (Paquete x50)', 12000.00, 120, 'TOR-003', 'TORNILLERÍA Y FIJACIÓN'),
(26, 'Arandela Presión Guasa 1/4" Zincada (Paquete x100)', 8500.00, 150, 'TOR-004', 'TORNILLERÍA Y FIJACIÓN'),
(27, 'Tornillo de Carenado / Lujo M5x16mm Neón (Paquete x10)', 14000.00, 60, 'TOR-005', 'TORNILLERÍA Y FIJACIÓN'),
(28, 'Abrazadera de Presión de Manguera 1/2" (Paquete x20)', 10500.00, 90, 'TOR-006', 'TORNILLERÍA Y FIJACIÓN'),
(29, 'Bombillo LED H4 10000 Lúmenes Ojo de Águila', 55000.00, 22, 'ELE-001', 'ELÉCTRICO E ILUMINACIÓN'),
(30, 'Batería YUASA YTX7A-BS Gel 12V 7Ah', 145000.00, 11, 'ELE-002', 'ELÉCTRICO E ILUMINACIÓN'),
(31, 'Direccionales LED Secuenciales de Lujo (Juego x4)', 42000.00, 18, 'ELE-003', 'ELÉCTRICO E ILUMINACIÓN'),
(32, 'Switch de Encendido / Switch Swiche Doble Llave', 28000.00, 15, 'ELE-004', 'ELÉCTRICO E ILUMINACIÓN');

-- 4. HISTÓRICO DE VENTAS (Últimos 7 Días)
-- Genera movimientos continuos para dinamizar el gráfico de líneas del Dashboard y la tabla de reportes

INSERT OR IGNORE INTO ventas (id, cliente, cc_cliente, vendedor, fecha, total, metodo_pago, pago_confirmado) VALUES
(1, 'Taller MotoSpeed S.A.S.', '901234567-1', 'Carlos Pérez', date('now', '-6 days'), 325000.00, 'Efectivo', ''),
(2, 'Juan Carlos Rodríguez', '1098765432', 'Ana Gómez', date('now', '-5 days'), 136000.00, 'Nequi', ''),
(3, 'MotoRepuestos El Paisa', '800456789-3', 'Carlos Pérez', date('now', '-4 days'), 525000.00, 'Crédito', 'deudor'),
(4, 'Distribuidora Lujomotos', '900111222-5', 'brandon', date('now', '-3 days'), 240000.00, 'Bancolombia', ''),
(5, 'CONSUMIDOR FINAL', 'N/A', 'Ana Gómez', date('now', '-2 days'), 96000.00, 'Efectivo', ''),
(6, 'Taller MotoSpeed S.A.S.', '901234567-1', 'Carlos Pérez', date('now', '-1 day'), 415000.00, 'Transferencia', ''),
(7, 'Juan Carlos Rodríguez', '1098765432', 'brandon', date('now'), 173000.00, 'Efectivo', '');

-- 5. DETALLES DE VENTAS (Items de cada venta)
INSERT OR IGNORE INTO detalles_venta (id, venta_id, producto, cantidad, codigo, precio, total) VALUES
-- Venta 1
(1, 1, 'Kit Cilindro y Pistón Yamaha FZ 160', 1, 'MOT-001', 185000.00, 185000.00),
(2, 1, 'Carburador Mikuni 28mm Universal', 1, 'MOT-004', 140000.00, 140000.00),

-- Venta 2
(3, 2, 'Aceite Motul 7100 4T 10W40 100% Sintético 1L', 2, 'LUB-001', 68000.00, 136000.00),

-- Venta 3 (Venta a Crédito / Deudor)
(4, 3, 'Kit de Arrastre Racing Cadena Reforzada 428H-136L', 3, 'ARR-001', 135000.00, 405000.00),
(5, 3, 'Cadena Dorada con O-Ring 520H-120L', 1, 'ARR-004', 120000.00, 120000.00),

-- Venta 4
(6, 4, 'Llanta Michelin Pilot Street 110/80-17', 1, 'LLA-001', 240000.00, 240000.00),

-- Venta 5
(7, 5, 'Pastillas de Freno Delanteras Brembo Sinterizadas', 2, 'FRE-001', 48000.00, 96000.00),

-- Venta 6
(8, 6, 'Llanta Pirelli MT60 90/90-19 Doble Propósito', 1, 'LLA-002', 285000.00, 285000.00),
(9, 6, 'Batería YUASA YTX7A-BS Gel 12V 7Ah', 1, 'ELE-002', 130000.00, 130000.00),

-- Venta 7 (Hoy)
(10, 7, 'Bujía Iridium NGK CPR8EA-9', 2, 'MOT-002', 28000.00, 56000.00),
(11, 7, 'Aceite Mobil Super Moto 4T 20W50 Mineral 1L', 3, 'LUB-002', 34000.00, 102000.00),
(12, 7, 'Tornillo Allen Bristol M6x20mm Inoxidable (Paquete x50)', 1, 'TOR-001', 15000.00, 15000.00);
