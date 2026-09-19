# Configuración del Sistema & Despliegue — ERP+ Business (v2.1.0)

> **Guía de Infraestructura, Persistencia SQLite WAL, Rutas Protegidas por SO, Inyección Demo y SMTP**

---

## 1. Archivo de Propiedades `config.properties`

El archivo `config.properties` reside en la raíz de ejecución de la aplicación (o en el directorio de trabajo del proceso Java). Administra las preferencias locales y la ubicación física del motor de datos.

```properties
# Ruta absoluta de la carpeta de datos o archivo .db
# Si se omite o está vacío, el sistema resuelve automáticamente la ruta resguardada del SO
db.path=/home/usuario/.config/ERPPlusBusiness/db.db

# Preferencia de tema gráfico (true = Modo Oscuro Cyberpunk Neon, false = Modo Claro)
theme.dark=true

# Recordar usuario previamente autenticado en pantalla de Login
remember.user=admin
```

### Lectura y Carga Dinámica (`SelecionRuta.cargarRutaBaseDatos`)
El método `SelecionRuta.cargarRutaBaseDatos()` inspecciona `config.properties`. Si la clave `db.path` contiene un valor válido, la retorna; de lo contrario, delega la resolución a `obtenerRutaProtegidaPredeterminada()`.

---

## 2. Contrato de Ruta Segura por SO (`SelecionRuta`)

Para evitar la pérdida accidental de datos durante la compilación (`mvn clean`) o actualización del artefacto JAR ejecutable, **ERP+ Business** implementa un mecanismo de persistencia fuera del espacio de trabajo del proyecto:

| Sistema Operativo | Ruta Absoluta Predeterminada |
| :--- | :--- |
| **Windows** | `%APPDATA%\ERPPlusBusiness\db.db` |
| **Linux / Unix** | `~/.config/ERPPlusBusiness/db.db` |
| **macOS** | `~/Library/Application Support/ERPPlusBusiness/db.db` |

### Lógica de Resolución de Archivo (`GestorConexion.resolverArchivo`)
1. Si `dbPath` referencia a un archivo existente `.db` o `.sqlite`, se utiliza directamente.
2. Si `dbPath` apunta a un directorio existente o nuevo, crea la carpeta y utiliza `<directorio>/db.db`.
3. Crea automáticamente la estructura de carpetas padres (`mkdirs()`) si no existen previamente.

---

## 3. Infraestructura Singleton & PRAGMAs de Rendimiento (`GestorConexion`)

La clase `GestorConexion` mantiene **una única conexión JDBC abierta** en la memoria de la JVM durante toda la sesión para prevenir el error `SQLITE_BUSY` (*database is locked*).

### PRAGMAs SQLite Aplicados al Inicializar

```sql
-- 1. Habilitar Write-Ahead Logging (WAL) para lectura y escritura concurrente de alto rendimiento
PRAGMA journal_mode=WAL;

-- 2. Establecer tiempo máximo de espera defensiva en bloqueos concurrentes (5000 ms)
PRAGMA busy_timeout=5000;

-- 3. Enforzar integridad referencial y claves foráneas
PRAGMA foreign_keys=ON;

-- 4. Optimización de sincronización de disco
PRAGMA synchronous=NORMAL;
```

---

## 4. Scripts de Datos Demo & Pruebas en SQLite

Para fines de demostración ejecutiva, evaluación del Dashboard BI y pruebas de volumen, el proyecto incluye scripts SQL oficiales en la raíz y en el directorio de artefactos:

1. **`poblar_master_demo.sql`**:
   - Dataset maestro multiaño (2024–2026) con DDL oficial.
   - 9 usuarios de prueba (Admins, Vendedores, Clientes).
   - 4 proveedores corporativos y 9 categorías de repuestos.
   - 38 productos con precio de venta, precio de costo y stock actual.
   - 1,225+ ventas históricas distribuidas mes a mes (Efectivo, Transferencia, Crédito).
   - 400+ abonos a cartera y facturas de compras para verificar comparativas interperiodo, badges de crecimiento $\Delta \%$ y utilidades netas.
2. **`datos_demo_fase5.sql`**: Carga simplificada de categorías, productos y compras históricas para probar Utilidad Neta ($) e Inversión ($).
3. **`poblar_repuestos_motos.sql`**: Catálogo enfocado en repuestos y accesorios de motocicletas.
4. **`poblar_sistema_completo.sql`**: Carga masiva para pruebas de carga.

### Comando de Inyección Directa en SQLite CLI:
```bash
# Inyección del dataset maestro demo en Linux
sqlite3 ~/.config/ERPPlusBusiness/db.db < poblar_master_demo.sql

# Inyección en Windows (Command Prompt)
sqlite3 "%APPDATA%\ERPPlusBusiness\db.db" < poblar_master_demo.sql
```

---

## 5. Parámetros SMTP para Cotizaciones e Informes (`EnvioCotizacion`)

El módulo de exportación y envío de cotizaciones a clientes por correo electrónico utiliza el protocolo SMTP sobre TLS:

- **Servidor SMTP**: `smtp.gmail.com`
- **Puerto**: `587` (TLS / STARTTLS)
- **Autenticación**: Habilitada mediante *Contraseña de Aplicación de Google* (App Password de 16 caracteres).
- **Formato del Mensaje**: HTML enriquecido con resumen tabulado de productos, precios y archivo PDF adjunto.
