# Configuración del Sistema & Despliegue — ERP+ Business (v2.0.0)

> **Guía de Despliegue, Rutas Protegidas por SO, Inyección de Datos y SMTP**

---

## 1. Archivo de Propiedades `config.properties`

Reside en la raíz de ejecución de la aplicación y administra las variables de entorno locales.

```properties
# Ruta de la base de datos (Si se omite, usa la ruta resguardada del SO)
db.path=/home/user/.config/ERPPlusBusiness/db.db

# Preferencia de tema (true = Oscuro Cyberpunk, false = Claro)
theme.dark=true

# Recordar usuario en pantalla de login
remember.user=admin
```

---

## 2. Contrato de Ruta Segura por SO (`SelecionRuta`)

Si el usuario no especifica una ruta personalizada en `ConfigPage`, el sistema resuelve de forma automática y transparente un directorio protegido por el sistema operativo:

- **Windows**: `%APPDATA%\ERPPlusBusiness\db.db`
- **Linux**: `~/.config/ERPPlusBusiness/db.db`
- **macOS**: `~/Library/Application Support/ERPPlusBusiness/db.db`

Esto evita la pérdida de datos cuando el desarrollador o usuario ejecuta `mvn clean` o actualiza la versión del ejecutable JAR.

---

## 3. Scripts de Datos Demo & Pruebas en SQLite

Para poblar la base de datos SQLite con datos de prueba realistas para demostración de métricas financieras del Dashboard (Fase 5), se proveen los siguientes scripts SQL:

1. **`poblar_master_demo.sql`**: Script maestro de demostración comercial con el DDL completo, 9 usuarios multirol, 4 proveedores, 9 categorías, 38 productos con costo e inventario, 10 ventas históricas (Efectivo, Transferencia, Crédito), abonos a cartera y 3 facturas de compras con 8 detalles de recepción en almacén.
2. **`datos_demo_fase5.sql`**: Carga de categorías, productos con costo y compras históricas para probar Utilidad Neta ($) e Inversión ($).
3. **`poblar_repuestos_motos.sql`**: Catálogo de prueba enfocado en repuestos y accesorios de motocicletas.
4. **`poblar_sistema_completo.sql`**: Dataset masivo de clientes, empleados, catálogo y ventas.

### Ejecución Directa en SQLite CLI:
```bash
sqlite3 ~/.config/ERPPlusBusiness/db.db < poblar_master_demo.sql
```

---

## 4. Parámetros SMTP para Envío de Cotizaciones (`EnvioCotizacion`)

- **Host SMTP**: `smtp.gmail.com`
- **Puerto**: `587` (TLS)
- **Autenticación**: Habilitada vía Contraseña de Aplicación de Google.
