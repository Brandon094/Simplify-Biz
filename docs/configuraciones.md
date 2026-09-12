# Configuración del sistema - ERP+ Business

## `config.properties`

El archivo se encuentra en la raíz de ejecución y se carga mediante `SelecionRuta.cargarRutaBaseDatos()`.

```properties
db.path=/home/usuario/ERPPlusBusiness/data
```

## Particularidad de la ruta actual

`ConexionDB` recibe el valor de `db.path`, crea el directorio si no existe y compone internamente la URL SQLite como:

```text
jdbc:sqlite:<db.path>/db.db
```

Por tanto, con la implementación actual `db.path` debe tratarse como una carpeta base y el archivo efectivo es `db.db`. La pantalla de configuración utiliza un selector que actualmente puede devolver una ruta con nombre `db.sqlite`; antes de corregir este comportamiento debe revisarse la compatibilidad con instalaciones existentes.

## Inicialización

`Main.inicializarBaseDatos()` lee la ruta, crea una `ConexionDB` y delega en `DatabaseInitializer`. La clase `ConexionDB` también contiene la rutina de creación de tablas y el valor inicial de `configuracion`.

- SQLite crea el archivo si no existe.
- Las tablas se crean con `CREATE TABLE IF NOT EXISTS`.
- No se borran datos existentes durante el arranque.
- La tabla `configuracion` recibe el registro `id = 1` si aún no existe.

## Cambio desde la aplicación

La pantalla Configuración permite seleccionar una nueva ubicación. El cambio se escribe en `config.properties`, pero requiere reiniciar la aplicación para que las nuevas conexiones utilicen la ruta.

## Requisitos y permisos

- JRE/JDK 17 o superior.
- Permiso de escritura en la carpeta de datos.
- Permiso de escritura en `config.properties`.
- Evitar rutas dentro de carpetas sincronizadas mientras se prueba SQLite.

## Copias de seguridad

1. Cerrar la aplicación.
2. Copiar el archivo efectivo `db.db` a una carpeta de respaldo.
3. Guardar copias con fecha.
4. Probar periódicamente la restauración en una carpeta separada.

No borrar ni reemplazar el archivo de configuración sin conservar una copia de la ruta utilizada.
