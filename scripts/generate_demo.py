import datetime
import random

lines = []
lines.append("-- ================================================= Requirement ====================")
lines.append("-- SCRIPT DE POBLADO MASIVO Y ULTRA-REALISTA (2024 - 2026) — SIMPLIFY-BIZ")
lines.append("-- Genera cientos de ventas distribuidas por días, meses y años")
lines.append("-- ====================================================================")
lines.append("")
lines.append("CREATE TABLE IF NOT EXISTS usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, telefono TEXT NOT NULL, email TEXT NOT NULL, rol INTEGER NOT NULL, contraseña TEXT NOT NULL);")
lines.append("CREATE TABLE IF NOT EXISTS productos (id INTEGER PRIMARY KEY AUTOINCREMENT, producto TEXT NOT NULL, precio REAL NOT NULL, precio_costo REAL DEFAULT 0.0, cantidad INTEGER NOT NULL, codigo TEXT NOT NULL, categoria TEXT);")
lines.append("CREATE TABLE IF NOT EXISTS ventas (id INTEGER PRIMARY KEY AUTOINCREMENT, cliente TEXT NOT NULL, cc_cliente TEXT NOT NULL, vendedor TEXT NOT NULL, fecha DATE NOT NULL, total REAL NOT NULL, metodo_pago TEXT NOT NULL, pago_confirmado TEXT);")
lines.append("CREATE TABLE IF NOT EXISTS detalles_venta (id INTEGER PRIMARY KEY AUTOINCREMENT, venta_id INTEGER NOT NULL, producto TEXT NOT NULL, cantidad INTEGER NOT NULL, codigo TEXT NOT NULL, precio REAL NOT NULL, precio_costo REAL DEFAULT 0.0, descuento REAL DEFAULT 0.0, total REAL NOT NULL, FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE);")
lines.append("CREATE TABLE IF NOT EXISTS configuracion (id INTEGER PRIMARY KEY, ultimoNumeroCotizacion TEXT);")
lines.append("CREATE TABLE IF NOT EXISTS categorias (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT UNIQUE NOT NULL);")
lines.append("CREATE TABLE IF NOT EXISTS abonos_cartera (id INTEGER PRIMARY KEY AUTOINCREMENT, venta_id INTEGER NOT NULL, monto REAL NOT NULL, fecha DATE NOT NULL, metodo_pago TEXT NOT NULL, observacion TEXT, FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE);")
lines.append("CREATE TABLE IF NOT EXISTS proveedores (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, nit TEXT UNIQUE NOT NULL, telefono TEXT, email TEXT, direccion TEXT);")
lines.append("CREATE TABLE IF NOT EXISTS compras (id INTEGER PRIMARY KEY AUTOINCREMENT, proveedor_id INTEGER, proveedor_nombre TEXT NOT NULL, proveedor_nit TEXT NOT NULL, num_factura TEXT NOT NULL, usuario_registro TEXT NOT NULL, fecha DATE NOT NULL, total REAL NOT NULL, FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE SET NULL);")
lines.append("CREATE TABLE IF NOT EXISTS detalles_compra (id INTEGER PRIMARY KEY AUTOINCREMENT, compra_id INTEGER NOT NULL, producto TEXT NOT NULL, codigo TEXT NOT NULL, cantidad INTEGER NOT NULL, precio_costo REAL NOT NULL, subtotal REAL NOT NULL, FOREIGN KEY (compra_id) REFERENCES compras(id) ON DELETE CASCADE);")

lines.append("")
lines.append("DELETE FROM abonos_cartera;")
lines.append("DELETE FROM detalles_compra;")
lines.append("DELETE FROM compras;")
lines.append("DELETE FROM detalles_venta;")
lines.append("DELETE FROM ventas;")
lines.append("DELETE FROM productos;")
lines.append("DELETE FROM proveedores;")
lines.append("DELETE FROM categorias;")
lines.append("DELETE FROM usuarios;")

lines.append("")
lines.append("INSERT INTO usuarios (id, nombre, telefono, email, rol, contraseña) VALUES (1, 'Brandon Dario (CEO Admin)', '3001234567', 'admin@simplifybiz.com', 1, '1234'), (2, 'Carlos Vendedor Mostrador', '3119876543', 'carlos@simplifybiz.com', 2, '1234'), (3, 'Mariana Auxiliar Bodega', '3205554433', 'mariana@simplifybiz.com', 2, '1234');")

lines.append("")
lines.append("INSERT INTO categorias (nombre) VALUES ('REPUESTOS MOTOR'), ('LUBRICANTES Y FLUIDOS'), ('FRENOS Y SUSPENSIÓN'), ('ELÉCTRICO Y ILUMINACIÓN'), ('INDUMENTARIA Y PROTECCIÓN'), ('HERRAMIENTAS'), ('ACCESORIOS Y LUJOS');")

lines.append("")
lines.append("INSERT INTO proveedores (id, nombre, nit, telefono, email, direccion) VALUES (1, 'Distribuidora Repuestos del Valle S.A.S.', '900456789-1', '3104567890', 'ventas@repuestosdelvalle.com', 'Calle 15 # 22-45'), (2, 'Lubricantes & Sintéticos Colombia Ltda.', '800123987-4', '3159876543', 'contacto@lubrisinteticos.co', 'Carrera 40 # 10-12'), (3, 'Importaciones Indumentaria & Lujos SAS', '901777888-5', '3182223344', 'importaciones@lujosymotos.com', 'Avenida de las Américas # 50-10');")

prods = [
    (101, 'Kit de Arrastre Cassarella Pulsar 200 NS', 145000.0, 95000.0, 18, 'SKU-ARR01', 'REPUESTOS MOTOR'),
    (102, 'Aceite Motul 7100 10W40 Sintético (1L)', 68000.0, 42000.0, 45, 'SKU-LUB01', 'LUBRICANTES Y FLUIDOS'),
    (103, 'Pastillas de Freno Brembo Delanteras XT', 52000.0, 31000.0, 3, 'SKU-FRN01', 'FRENOS Y SUSPENSIÓN'),
    (104, 'Batería Gel Yuasa 12V 9Ah YTX9-BS', 185000.0, 125000.0, 12, 'SKU-ELE01', 'ELÉCTRICO Y ILUMINACIÓN'),
    (105, 'Casco Certificado Shaft Pro Series Talla M', 240000.0, 155000.0, 4, 'SKU-IND01', 'INDUMENTARIA Y PROTECCIÓN'),
    (106, 'Bombillo LED H4 10000LM Canbus', 48000.0, 25000.0, 30, 'SKU-ELE02', 'ELÉCTRICO Y ILUMINACIÓN'),
    (107, 'Líquido de Frenos Motul DOT 5.1 (500ml)', 38000.0, 22000.0, 2, 'SKU-LUB02', 'LUBRICANTES Y FLUIDOS'),
    (108, 'Llanta Pirelli Diablo Rosso III 140/70-17', 420000.0, 290000.0, 8, 'SKU-ACC01', 'ACCESORIOS Y LUJOS'),
    (109, 'Chaqueta de Protección Impermeable Tour', 320000.0, 210000.0, 15, 'SKU-IND02', 'INDUMENTARIA Y PROTECCIÓN'),
    (110, 'Juego de Herramientas Husky 50 Piezas', 290000.0, 180000.0, 6, 'SKU-HER01', 'HERRAMIENTAS')
]

lines.append("")
p_values = [f"({p[0]}, '{p[1]}', {p[2]}, {p[3]}, {p[4]}, '{p[5]}', '{p[6]}')" for p in prods]
lines.append("INSERT INTO productos (id, producto, precio, precio_costo, cantidad, codigo, categoria) VALUES " + ", ".join(p_values) + ";")

vendedores = ['branddon', 'carlos_ventas', 'mariana_bodega']
metodos = ['Efectivo', 'Transferencia', 'Crédito']
clientes = [
    ('CONSUMIDOR FINAL', 'N/A'),
    ('Empresa Transporte Rápido SAS', '900111222'),
    ('Taller El Pique Rápido', '800999888'),
    ('Motoclub Los Halcones', '901555666'),
    ('Carlos Mario Restrepo', '1035444555'),
    ('Distribución & Logística S.A.S', '901222333'),
    ('Inversiones Obras & Vías SAS', '900888777'),
    ('Taller Los Mecánicos del Sur', '800444333')
]

current_date = datetime.date(2026, 9, 17)
start_date = datetime.date(2024, 1, 1)

venta_id = 1001
detail_id = 5001
abono_id = 101

today_str = current_date.isoformat()

# Generar ventas para 2024, 2025 y 2026
curr = start_date
while curr <= current_date:
    # Simular días con más o menos ventas (días de semana vs fin de semana)
    # 2024: 1 a 3 ventas por semana
    # 2025: 2 a 5 ventas por semana
    # 2026: 1 a 4 ventas por día o por cada par de días
    
    # Decidir si hay venta este día
    prob = 0.45 if curr.year == 2024 else (0.65 if curr.year == 2025 else 0.85)
    if curr == current_date:
        num_sales_today = 5
    elif (current_date - curr).days <= 7:
        num_sales_today = random.randint(2, 4)
    elif random.random() < prob:
        num_sales_today = random.randint(1, 3)
    else:
        num_sales_today = 0
        
    for _ in range(num_sales_today):
        cli, cc = random.choice(clientes)
        vend = random.choice(vendedores)
        metodo = random.choice(metodos)
        pago_conf = 'deudor' if metodo == 'Crédito' else 'pagado'
        
        # Generar de 1 a 3 ítems por venta
        num_items = random.randint(1, 3)
        items_chosen = random.sample(prods, num_items)
        
        venta_total = 0.0
        detalles_sql = []
        
        for item in items_chosen:
            cant = random.randint(1, 4)
            precio = item[2]
            costo = item[3]
            tot_item = cant * precio
            venta_total += tot_item
            detalles_sql.append(f"({venta_id}, '{item[1]}', {cant}, '{item[5]}', {precio}, {costo}, 0.0, {tot_item})")
            
        fecha_str = curr.isoformat()
        lines.append(f"INSERT INTO ventas (id, cliente, cc_cliente, vendedor, fecha, total, metodo_pago, pago_confirmado) VALUES ({venta_id}, '{cli}', '{cc}', '{vend}', '{fecha_str}', {venta_total}, '{metodo}', '{pago_conf}');")
        lines.append("INSERT INTO detalles_venta (venta_id, producto, cantidad, codigo, precio, precio_costo, descuento, total) VALUES " + ", ".join(detalles_sql) + ";")
        
        if metodo == 'Crédito':
            abono = round(venta_total * random.choice([0.3, 0.4, 0.5]), -3)
            if abono > 0:
                lines.append(f"INSERT INTO abonos_cartera (id, venta_id, monto, fecha, metodo_pago, observacion) VALUES ({abono_id}, {venta_id}, {abono}, '{fecha_str}', 'Transferencia', 'Abono inicial registrado');")
                abono_id += 1
                
        venta_id += 1
        
    curr += datetime.timedelta(days=1)

with open('/home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo Desktop/db/poblar_master_demo.sql', 'w', encoding='utf-8') as f:
    f.write("\n".join(lines))

print(f"Generadas {venta_id - 1001} ventas desde 2024 hasta {today_str}.")
