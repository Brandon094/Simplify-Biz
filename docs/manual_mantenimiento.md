# Manual de Mantenimiento & Onboarding — ERP+ BUSINESS (v2.0.0)

> **Guía Técnica de Operaciones, Extensión de Código, Ciclo de Vida JVM y Empaquetado Nativo**  
> Destinado a ingenieros de software, mantenedores y nuevos desarrolladores del proyecto.

---

## 1. Ciclo de Vida de Ejecución desde `Main.java`

El flujo de inicio de la aplicación en la Máquina Virtual de Java (JVM) sigue una secuencia defensiva de inicialización:

```text
[Main.main()] ──► 1. Cargar Preferencia de Tema (`SelecionRuta.cargarPreferenciaTema()`)
              ──► 2. Aplicar Look and Feel FlatLaf (`Main.aplicarTema()`)
              ──► 3. Inicializar DB Singleton (`GestorConexion.getInstancia().inicializar(ruta)`)
              ──► 4. Registrar Shutdown Hook en JVM para cierre limpio de SQLite
              ──► 5. Verificar tablas y auto-migrar esquemas (`DatabaseInitializer`)
              ──► 6. Evaluar existencia de Admin (`UsuarioController.existeAdministrador()`)
                      ├─► NO EXISTE ──► Abrir `ModernAdminRegistrationPage`
                      └─► SI EXISTE ──► Abrir `ModernLoginPage`
```

### 1.1 Apagado Limpio y Shutdown Hooks
Para garantizar que la base de datos SQLite no quede con archivos temporales sin volcar en el journal WAL (`db.db-wal` / `db.db-shm`), `Main.java` registra un gancho de apagado en el `Runtime` de Java:

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    GestorConexion.getInstancia().cerrar();
}));
```

---

## 2. Protocolo de Extensión de la Aplicación

Si necesitas agregar un nuevo módulo operacional a ERP+ BUSINESS (ejemplo: **Cotizaciones** o **Garantías**), sigue estrictamente la arquitectura modular preexistente:

### Paso 1: Definir el Modelo POJO (`models/Cotizacion.java`)
Crea la clase de dominio inmutable con sus getters y setters correspondientes.

### Paso 2: Crear el DDL & Auto-Migración (`config/ConexionDB.java`)
Define la sentencia `CREATE TABLE IF NOT EXISTS cotizaciones (...)` e inclúyela en `inicializarTablas()`. Si agregas columnas en versiones futuras, registra la migración con `migrarColumnaSegura`.

### Paso 3: Crear el Controlador MVC Desacoplado (`controllers/CotizacionesController.java`)
Implementa las operaciones CRUD desacopladas de la interfaz Swing. Todos los métodos de escritura deben retornar un `ResultadoOperacion`:

```java
public ResultadoOperacion guardarCotizacion(Cotizacion cot, List<DetalleCotizacion> detalles) {
    Connection conn = GestorConexion.getInstancia().getConexion();
    try {
        conn.setAutoCommit(false);
        // ... Lógica de inserción JDBC
        conn.commit();
        return ResultadoOperacion.ok("Cotización guardada exitosamente.");
    } catch (SQLException e) {
        try { conn.rollback(); } catch (SQLException ignored) {}
        return ResultadoOperacion.error("Error al guardar la cotización: " + e.getMessage());
    } finally {
        try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
    }
}
```

### Paso 4: Añadir Iconografía SVG
Copia el archivo SVG vectorial correspondiente a la carpeta `src/main/resources/icons/cotizacion.svg`.

### Paso 5: Implementar la Capa de Vista (`views/CotizacionesPage.java`)
Crea el panel UI y regístralo dentro del menú lateral `ModernSidebar.java` y en el contenedor `MainTemplate.java`.

### Paso 6: Implementar las Pruebas Unitarias (`src/test/java/.../CotizacionesControllerTest.java`)
Escribe la suite de pruebas unitarias en JUnit 5 utilizando `@TempDir` o bases de datos SQLite temporales.

---

## 3. Guía de Construcción y Empaquetado Nativo (`jpackage`)

### 3.1 Construcción del Fat-JAR Ejecutable
```bash
mvn clean package
```
Esto generará el artefacto ejecutable `dist/ERP-Plus-Business-2.0.0.jar` conteniendo todas las dependencias (FlatLaf, SQLite, Apache POI).

### 3.2 Generación del Paquete Portable Automático
```bash
bash scripts/package-app.sh
```
Crea la carpeta lista para distribuir `dist/ERP_Plus_Business_v2.0.0_Portable/` con scripts `run.sh` para Linux/macOS y `run.bat` para Windows.

### 3.3 Distribución Nativa con Icono Oficial y Acceso Directo (.ico / VBScript / .deb)

ERP+ BUSINESS incluye un motor de empaquetado multi-plataforma listo para ser distribuido a clientes finales en Windows, Linux y macOS:

#### 1. Generación de Recursos e Icono Nativo
El sistema convierte automáticamente el isotipo vectorizado de la aplicación (`app_icon.png`) a un icono nativo multi-resolución de Windows (`app_icon.ico` conteniendo capas de 256x256 hasta 16x16 píxeles) dentro de `dist/ERP_Plus_Business_v2.0.0_Portable/`.

#### 2. Lanzadores y Acceso Directo de 1 Clic para Windows
Para evitar consolas de comandos visibles al usuario en Windows:
- Se incluye `Crear_Acceso_Directo.vbs` dentro del paquete portable.
- Al ejecutar el script VBScript en el PC del cliente, este crea automáticamente un **Acceso Directo oficial en el Escritorio** apuntando a `run.bat` y asignando la imagen de icono neón `app_icon.ico`.

#### 3. Instalador Debian/Ubuntu para Linux (`.deb`):
```bash
jpackage \
  --name "ERP-Plus-Business" \
  --app-version "2.0.0" \
  --input dist/ERP_Plus_Business_v2.0.0_Portable \
  --main-jar ERP-Plus-Business-2.0.0.jar \
  --main-class com.mycompany.zl_solucion_integral.Main \
  --type deb \
  --icon src/main/resources/icons/app_icon.png \
  --vendor "ChopCode Solutions" \
  --description "ERP+ Business Intelligent POS & Management"
```

#### 4. Instalador MSI/EXE para Windows (Desde consola Windows):
```cmd
jpackage ^
  --name "ERP-Plus-Business" ^
  --app-version "2.0.0" ^
  --input dist\ERP_Plus_Business_v2.0.0_Portable ^
  --main-jar ERP-Plus-Business-2.0.0.jar ^
  --main-class com.mycompany.zl_solucion_integral.Main ^
  --type msi ^
  --win-shortcut ^
  --win-menu ^
  --icon src\main\resources\icons\app_icon.ico ^
  --vendor "ChopCode Solutions"
```
