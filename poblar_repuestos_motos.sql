-- ====================================================================
-- SCRIPT DE DATOS DE PRUEBA: ALMACÉN DE REPUESTOS DE MOTOS Y TORNILLERÍA
-- Compatible con DB Browser for SQLite y ERP+ Business
-- ====================================================================

-- 1. Insertar Categorías de Repuestos y Tornillería
INSERT OR IGNORE INTO categorias (nombre) VALUES 
('REPUESTOS MOTOR'),
('FRENOS Y SUSPENSIÓN'),
('KITS DE ARRASTRE'),
('LLANTAS Y NEUMÁTICOS'),
('LUBRICANTES Y FLUIDOS'),
('TORNILLERÍA Y FIJACIÓN'),
('ELÉCTRICO E ILUMINACIÓN'),
('ACCESORIOS Y CASCOS');

-- 2. Insertar Productos de Ejemplo
INSERT INTO productos (producto, precio, cantidad, codigo, categoria) VALUES

-- Categoría: REPUESTOS MOTOR
('Kit Cilindro y Pistón Yamaha FZ 160', 185000.00, 12, 'MOT-001', 'REPUESTOS MOTOR'),
('Bujía Iridium NGK CPR8EA-9', 28000.00, 45, 'MOT-002', 'REPUESTOS MOTOR'),
('Kit de Empaques de Motor Bajaj Pulsar 200', 35000.00, 20, 'MOT-003', 'REPUESTOS MOTOR'),
('Carburador Mikuni 28mm Universal', 145000.00, 8, 'MOT-004', 'REPUESTOS MOTOR'),
('Filtro de Aire Alto Flujo K&N', 65000.00, 15, 'MOT-005', 'REPUESTOS MOTOR'),
('Válvulas de Admisión y Escape Honda CB125', 42000.00, 18, 'MOT-006', 'REPUESTOS MOTOR'),

-- Categoría: FRENOS Y SUSPENSIÓN
('Pastillas de Freno Delanteras Brembo Sinterizadas', 48000.00, 30, 'FRE-001', 'FRENOS Y SUSPENSIÓN'),
('Banda de Freno Trasera Suzuki GN125', 25000.00, 25, 'FRE-002', 'FRENOS Y SUSPENSIÓN'),
('Disco de Freno Lobulado 260mm Pulsar 180', 110000.00, 10, 'FRE-003', 'FRENOS Y SUSPENSIÓN'),
('Amortiguadores Traseros Hidráulicos (Par)', 160000.00, 6, 'SUS-001', 'FRENOS Y SUSPENSIÓN'),
('Retenedores de Barras Delanteras 31mm (Par)', 18000.00, 40, 'SUS-002', 'FRENOS Y SUSPENSIÓN'),

-- Categoría: KITS DE ARRASTRE
('Kit de Arrastre Racing Cadena Reforzada 428H-136L', 135000.00, 14, 'ARR-001', 'KITS DE ARRASTRE'),
('Piñón de Salida 14T Acero 1045', 22000.00, 28, 'ARR-002', 'KITS DE ARRASTRE'),
('Catalina Trasera 42T Acero Reforzado', 45000.00, 16, 'ARR-003', 'KITS DE ARRASTRE'),
('Cadena Dorada con O-Ring 520H-120L', 120000.00, 9, 'ARR-004', 'KITS DE ARRASTRE'),

-- Categoría: LLANTAS Y NEUMÁTICOS
('Llanta Michelin Pilot Street 110/80-17', 240000.00, 10, 'LLA-001', 'LLANTAS Y NEUMÁTICOS'),
('Llanta Pirelli MT60 90/90-19 Doble Propósito', 285000.00, 7, 'LLA-002', 'LLANTAS Y NEUMÁTICOS'),
('Neumático / Cámara Reforzada Rinaldi 2.75-18', 32000.00, 35, 'LLA-003', 'LLANTAS Y NEUMÁTICOS'),

-- Categoría: LUBRICANTES Y FLUIDOS
('Aceite Motul 7100 4T 10W40 100% Sintético 1L', 68000.00, 50, 'LUB-001', 'LUBRICANTES Y FLUIDOS'),
('Aceite Mobil Super Moto 4T 20W50 Mineral 1L', 34000.00, 80, 'LUB-002', 'LUBRICANTES Y FLUIDOS'),
('Líquido de Frenos DOT 4 Motul 500ml', 26000.00, 30, 'LUB-003', 'LUBRICANTES Y FLUIDOS'),
('Grasa para Cadenas Motul C2 Chain Lube 400ml', 45000.00, 24, 'LUB-004', 'LUBRICANTES Y FLUIDOS'),

-- Categoría: TORNILLERÍA Y FIJACIÓN
('Tornillo Allen Bristol M6x20mm Inoxidable (Paquete x50)', 15000.00, 100, 'TOR-001', 'TORNILLERÍA Y FIJACIÓN'),
('Tornillo Hexagonal Grado 8 M8x30mm (Paquete x25)', 18000.00, 80, 'TOR-002', 'TORNILLERÍA Y FIJACIÓN'),
('Tuerca Autoblocante de Seguridad M6 (Paquete x50)', 12000.00, 120, 'TOR-003', 'TORNILLERÍA Y FIJACIÓN'),
('Arandela Presión Guasa 1/4" Zincada (Paquete x100)', 8500.00, 150, 'TOR-004', 'TORNILLERÍA Y FIJACIÓN'),
('Tornillo de Carenado / Lujo M5x16mm Neón (Paquete x10)', 14000.00, 60, 'TOR-005', 'TORNILLERÍA Y FIJACIÓN'),
('Abrazadera de Presión de Manguera 1/2" (Paquete x20)', 10500.00, 90, 'TOR-006', 'TORNILLERÍA Y FIJACIÓN'),

-- Categoría: ELÉCTRICO E ILUMINACIÓN
('Bombillo LED H4 10000 Lúmenes Ojo de Águila', 55000.00, 22, 'ELE-001', 'ELÉCTRICO E ILUMINACIÓN'),
('Batería YUASA YTX7A-BS Gel 12V 7Ah', 145000.00, 11, 'ELE-002', 'ELÉCTRICO E ILUMINACIÓN'),
('Direccionales LED Secuenciales de Lujo (Juego x4)', 42000.00, 18, 'ELE-003', 'ELÉCTRICO E ILUMINACIÓN'),
('Switch de Encendido / Switch Swiche Doble Llave', 28000.00, 15, 'ELE-004', 'ELÉCTRICO E ILUMINACIÓN');
