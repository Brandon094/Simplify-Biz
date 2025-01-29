✨ Simplify Biz ✨
Simplify Biz es un software de gestión empresarial que facilita la administración de usuarios, productos, ventas e informes.
💻 Desarrollado en Java 17 con interfaz gráfica Swing y una base de datos SQLite, ofrece un sistema de roles y permite un manejo eficiente de datos a través de módulos especializados.

📋 Requisitos para ejecutar la aplicación
La aplicación está desarrollada en Java 17 y no requiere instalación. Simplemente necesitas tener configurado el JDK 17 en tu sistema. Si aún no lo tienes, puedes descargarlo desde el siguiente enlace:

👉 Descargar JDK 17

📦 Pasos para configurar el JDK 17 (si no lo tienes instalado)
🌐 Acceder al enlace de descarga:

Haz clic en el enlace proporcionado para acceder a la carpeta de Google Drive que contiene el JDK 17.
⬇️ Descargar el JDK:

Descarga el archivo correspondiente a tu sistema operativo y guárdalo en una ubicación conveniente.
⚙️ Instalar o configurar el JDK:

Si tu sistema operativo requiere instalación, ejecuta el archivo descargado y sigue las instrucciones.
🛠️ Verificar la configuración del JDK:

Abre una terminal o símbolo del sistema (cmd en Windows) y ejecuta el siguiente comando:
bash
Copiar
Editar
java -version
Deberías ver una salida con la versión de Java instalada, algo como:
bash
Copiar
Editar
java version "17.0.13"
🗄️ Base de datos
📂 La base de datos de Simplify Biz está almacenada en el archivo DB.db, que debe estar en la misma carpeta donde esté el archivo .jar. Este archivo contiene los datos iniciales necesarios para el funcionamiento de la aplicación.

🔑 En la base de datos se ha creado un usuario genérico:

👤 Usuario: admin
🔒 Contraseña: admin
🚀 Ejecutar la aplicación
1️⃣ Ejecutar la aplicación desde el archivo .jar
✅ Asegúrate de que el JDK 17 esté configurado correctamente.
📁 Haz doble clic en el archivo .jar de la aplicación:
Simplify_Biz-1.2.0.jar.
📂 La base de datos DB.db debe estar en la misma carpeta que el archivo .jar.
2️⃣ Ejecutar la aplicación desde la terminal
Si tu sistema no abre el archivo .jar directamente, puedes ejecutarlo desde la terminal o símbolo del sistema con el siguiente comando:

bash
Copiar
Editar
java -jar Simplify_Biz-1.2.0.jar
🛠️ Solución de problemas
Si encuentras problemas al ejecutar la aplicación, verifica lo siguiente:

✅ El JDK 17 está correctamente instalado y configurado
Puedes verificarlo ejecutando este comando en tu terminal:

bash
Copiar
Editar
java -version
Deberías ver algo como:

bash
Copiar
Editar
java version "17.0.13"
✅ El archivo .jar no está dañado
🧐 Asegúrate de que el archivo no se corrompió al descargarlo o copiarlo.

✅ Tu sistema operativo permite ejecutar archivos .jar
🔒 Revisa si tu antivirus o configuración de seguridad están bloqueando el archivo.

✨ ¡Eso es todo!
✅ Una vez realizados estos pasos, la aplicación estará lista para su uso.
💬 Si necesitas más ayuda, no dudes en abrir una issue en este repositorio. 😊
