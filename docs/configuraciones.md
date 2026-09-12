# Configuración del Sistema — ERP+ Business

> Detalle completo sobre la configuración de la aplicación, el contrato de rutas de la base de datos, opciones de tema y procedimientos de respaldo.

---

## 1. Archivo `config.properties`

El archivo se encuentra en el **directorio de ejecución** de la aplicación (junto al JAR) y se gestiona mediante `SelecionRuta`.

### 1.1 Propiedades Disponibles

```properties
# Ruta de la carpeta o archivo de base de datos SQLite (si se omite, se usa la ubicación protegida del SO)
db.path=%APPDATA%\ERPPlusBusiness\db.db

# Preferencia de tema (true = oscuro, false = claro)
theme.dark=true

# Usuario recordado para autocompletar en inicio de sesión (opcional)
remember.user=admin
```

### 1.2 Carga y Escritura

| Operación | Método | Descripción |
| :--- | :--- | :--- |
| Leer ruta BD | `SelecionRuta.cargarRutaBaseDatos()` | Lee `db.path`. Si no existe o está vacía, devuelve la **ruta protegida automática por SO**. |
| Ruta Protegida SO | `SelecionRuta.obtenerRutaProtegidaPredeterminada()` | Genera la ruta resguardada (`%APPDATA%\ERPPlusBusiness` en Windows / `~/.config/ERPPlusBusiness` en Linux). |
| Guardar ruta BD | `Main.guardarRutaEnConfig(ruta)` | Escribe `db.path` en el archivo de propiedades. |
| Leer tema | `SelecionRuta.cargarPreferenciaTema()` | Lee `theme.dark`. Devuelve `null` si no existe (se interpreta como oscuro). |
| Guardar tema | `SelecionRuta.guardarPreferenciaTema(isDark)` | Escribe `theme.dark` como `true` o `false`. |
| Leer usuario recordado | `SelecionRuta.cargarUsuarioRecordado()` | Lee `remember.user`. Devuelve `null` si no existe. |
| Guardar/limpiar usuario | `SelecionRuta.guardarUsuarioRecordado(usuario)` | Guarda o borra la propiedad `remember.user`. |

---

## 2. Contrato de Ruta y Archivo Efectivo

`GestorConexion.resolverArchivo(dbPath)` define un contrato único de almacenamiento que mantiene compatibilidad total con instalaciones previas y resguarda la base de datos de borrados accidentales:

### 2.1 Ubicación Protegida por Defecto (Protección contra Borrado)

Si el usuario no especifica una ruta manual en `config.properties`, el sistema almacena la base de datos en un directorio privado y oculto del sistema operativo:

- **Windows**: `%APPDATA%\ERPPlusBusiness\db.db` (ej: `C:\Users\<Usuario>\AppData\Roaming\ERPPlusBusiness\db.db`)
- **Linux**: `~/.config/ERPPlusBusiness/db.db`
- **macOS**: `~/Library/Application Support/ERPPlusBusiness/db.db`

### 2.2 Reglas de Resolución

| Prioridad | Condición | Archivo Efectivo | Ejemplo |
| :--- | :--- | :--- | :--- |
| 1 | `db.path` no configurado / nulo | **Ruta Protegida del SO** | `%APPDATA%\ERPPlusBusiness\db.db` |
| 2 | `db.path` existe y es un **directorio** | `<db.path>/db.db` | `/home/user/data` → `/home/user/data/db.db` |
| 3 | `db.path` existe y es un **archivo** | Se usa tal cual | `/home/user/mi_bd.db` → `/home/user/mi_bd.db` |
| 4 | No existe, pero termina en `.db` o `.sqlite` | Se trata como archivo (crea carpeta padre) | `/home/user/nueva.sqlite` → `/home/user/nueva.sqlite` |
| 5 | No existe y no parece archivo | Se crea como directorio + `/db.db` | `/home/user/nueva` → `/home/user/nueva/db.db` |

### 2.3 URL JDBC Resultante

```
jdbc:sqlite:<archivo_efectivo_absoluto>
```

Ejemplo: `jdbc:sqlite:/home/user/.config/ERPPlusBusiness/db.db`

---

## 3. Inicialización de la Base de Datos

El flujo de inicialización al arrancar la aplicación:

```
Main.inicializarBaseDatos()
  ├─ SelecionRuta.cargarRutaBaseDatos()  → lee db.path o carpeta protegida SO
  ├─ Main.guardarRutaEnConfig(ruta)      → resguarda en config.properties
  ├─ GestorConexion.inicializar(ruta)
  │     ├─ resolverArchivo(ruta) → File efectivo
  │     ├─ DriverManager.getConnection(jdbcUrl)
  │     └─ aplicarPragmas(conn)
  │           ├─ PRAGMA journal_mode=WAL
  │           ├─ PRAGMA busy_timeout=5000
  │           └─ PRAGMA foreign_keys=ON
  ├─ ShutdownHook → GestorConexion.cerrar()
  └─ DatabaseInitializer.inicializarTablas()
        └─ ConexionDB.inicializarBaseDeDatos()
              ├─ CREATE TABLE IF NOT EXISTS usuarios
              ├─ CREATE TABLE IF NOT EXISTS productos
              ├─ CREATE TABLE IF NOT EXISTS ventas
              ├─ CREATE TABLE IF NOT EXISTS detalles_venta
              ├─ CREATE TABLE IF NOT EXISTS configuracion
              └─ INSERT OR IGNORE INTO configuracion (id=1)
```

**Reglas:**

- SQLite crea el archivo automáticamente si no existe.
- Las tablas se crean con `CREATE TABLE IF NOT EXISTS` — no se borran datos existentes.
- La tabla `configuracion` recibe el registro `id = 1` solo si aún no existe (`INSERT OR IGNORE`).
- No se realizan migraciones de columnas. Todo cambio futuro del esquema requiere una migración explícita.

---

## 4. Cambiar la Ruta desde la Aplicación

1. Abre la sección **Configuración**.
2. Pulsa **Cambiar ruta** y selecciona la nueva carpeta.
3. El cambio se escribe en `config.properties`.
4. **Reinicia la aplicación** para que las conexiones utilicen la nueva ruta.

> **Nota:** La base de datos anterior no se copia automáticamente. Si deseas migrar los datos, copia manualmente el archivo SQLite a la nueva ubicación antes de cambiar la ruta.

---

## 5. Configuración de Tema

La preferencia de tema se gestiona de forma transparente:

| Acción | Resultado |
| :--- | :--- |
| Pulsar toggle ☀️/🌙 en sidebar | Cambia el tema inmediatamente y guarda la preferencia |
| Iniciar la aplicación | Lee `theme.dark` de `config.properties`. Oscuro por defecto si no existe. |
| Cambiar `theme.dark` manualmente | Surte efecto en el próximo arranque |

---

## 6. Requisitos y Permisos

| Requisito | Detalle |
| :--- | :--- |
| JRE/JDK | 25 o superior |
| Permiso de escritura | Carpeta de datos (donde reside `db.db`) |
| Permiso de escritura | Directorio del archivo `config.properties` |
| Red | No requerida (todo es local) |

**Recomendaciones:**

- Evitar rutas dentro de carpetas sincronizadas (Dropbox, Google Drive, OneDrive) mientras se realizan pruebas con SQLite.
- En Linux, evitar particiones NTFS montadas vía FUSE para la carpeta `target` de Maven (ya configurado en `pom.xml`).

---

## 7. Copias de Seguridad

### 7.1 Respaldo Manual

1. Cierra la aplicación completamente.
2. Copia el archivo efectivo (`db.db` o la ruta especificada) a una carpeta de respaldo.
3. Guarda copias con fecha (ej: `db_2026-09-12.db`).
4. Prueba periódicamente la restauración en una carpeta separada.

### 7.2 Restauración

1. Cierra la aplicación.
2. Reemplaza el archivo `db.db` en la carpeta de datos con el respaldo.
3. Inicia la aplicación normalmente.

> **Precaución:** No borrar ni reemplazar `config.properties` sin conservar una copia de la ruta utilizada. Si lo pierdes, deberás volver a seleccionar la carpeta de datos.
