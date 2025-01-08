# ZL Solución Integral

ZL Solución Integral es un software de gestión empresarial que facilita la administración de usuarios, productos, ventas e informes. Desarrollado en **Java 17** con interfaz gráfica **Swing** y una base de datos **SQLite**, ofrece un sistema de roles y permite un manejo eficiente de datos a través de módulos especializados.

## Requisitos para ejecutar la aplicación

La aplicación incluye el **Java Development Kit (JDK) 17** necesario para su ejecución, el cual se encuentra en la carpeta `jdk17` en la raíz del repositorio.

### Pasos para configurar Java Development Kit (JDK 17)

1. **Acceder a la carpeta del JDK:**
   - Navega a la carpeta `jdk17` en la raíz del repositorio.

2. **Instalar o configurar el JDK:**
   - Si tu sistema operativo requiere una instalación, ejecuta el instalador correspondiente desde la carpeta `jdk17` y sigue las instrucciones proporcionadas.

3. **Verificar la configuración del JDK:**
   - Abre una terminal o símbolo del sistema (cmd en Windows).
   - Escribe el siguiente comando para verificar que el JDK está configurado correctamente:
     ```bash
     java -version
     ```
   - Deberías ver una salida que muestre la versión de Java instalada, algo como:
     ```bash
     java version "17.0.13"
     ```

### Base de datos

La base de datos de **ZL Solución Integral** está almacenada en el archivo `DB.db`, ubicado en la raíz del proyecto. Este archivo contiene los datos iniciales necesarios para el funcionamiento de la aplicación. En la base de datos se ha creado un usuario genérico `admin` para poder iniciar la aplicación.

### Ejecutar la aplicación

1. **Ejecutar la aplicación desde el archivo `.jar`**
   - Asegúrate de que el JDK 17 esté configurado correctamente.
   - Haz doble clic en el archivo `.jar` de la aplicación, `ZL_Solución_Integral-17.0.jar`.

2. **Ejecutar la aplicación desde la terminal:**
   - Si tu sistema no abre el archivo `.jar` directamente, puedes ejecutarlo desde la terminal o símbolo del sistema con el siguiente comando:
     ```bash
     java -jar ZL_Solución_Integral-17.0.jar
     ```

3. **Problemas comunes al ejecutar:**
   - Si la aplicación no se ejecuta, verifica que:
     - El JDK incluido en la carpeta `jdk17` está configurado correctamente.
     - El archivo `.jar` no está dañado.
     - No hay bloqueos del sistema operativo o antivirus.

Una vez seguidos estos pasos, la aplicación debería ejecutarse correctamente en tu equipo.
