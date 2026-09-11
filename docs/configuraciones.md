# Configuraciones del Sistema - Simplify Biz

## Archivo `config.properties`
El sistema utiliza un archivo de propiedades ubicado en la raíz del proyecto para persistir configuraciones locales del entorno de ejecución.

### Propiedades:
- `db.path`: Almacena la ruta absoluta de la carpeta donde se encuentra el archivo `db.db`.

**Ejemplo de contenido:**
```properties
db.path=/home/usuario/SimplifyBiz/data
```

## Inicialización de la Base de Datos
El proceso de inicialización se realiza en la clase `ConexionDB` a través del método `inicializarBaseDeDatos()`.
- Si el archivo de base de datos no existe en la ruta especificada, SQLite lo crea automáticamente.
- Se ejecutan sentencias `CREATE TABLE IF NOT EXISTS` para asegurar que la estructura esté presente sin borrar datos existentes.
- Se inserta un registro inicial en la tabla `configuracion` con el ID 1 si no existe.

## Requisitos de Entorno
- **Java Runtime Environment (JRE):** Version 17 o superior.
- **Permisos de Escritura:** La aplicación debe tener permisos para escribir en la carpeta seleccionada para la base de datos y en la raíz del proyecto para el archivo `.properties`.
