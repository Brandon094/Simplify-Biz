#!/bin/bash
# ==============================================================================
# Script de Empaquetado y Distribución Oficial — ERP+ Business v2.0.0
# ==============================================================================

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIST_DIR="$PROJECT_DIR/dist"
BUNDLE_DIR="$DIST_DIR/ERP_Plus_Business_v2.0.0_Portable"

echo "🚀 Iniciando proceso de empaquetado de ERP+ Business v2.0.0..."

# 1. Limpieza y Creación de Carpetas
rm -rf "$BUNDLE_DIR"
mkdir -p "$BUNDLE_DIR"

# 2. Compilar y empaquetar con Maven
echo "📦 Compilando ejecutable Shaded JAR con Maven..."
cd "$PROJECT_DIR"
mvn clean package -DskipTests

# 3. Copiar ejecutable JAR
JAR_FILE=$(find "$DIST_DIR" target/ -name "ERP-Plus-Business-2.0.0.jar" -o -name "ERP-Plus-Business-*.jar" 2>/dev/null | head -n 1)
if [ -f "$JAR_FILE" ]; then
    cp "$JAR_FILE" "$BUNDLE_DIR/ERP-Plus-Business-2.0.0.jar"
    echo "✅ Executable JAR copiado a $BUNDLE_DIR/ERP-Plus-Business-2.0.0.jar"
else
    echo "❌ Error: No se encontró el JAR compilado."
    exit 1
fi

# 4. Generar Script Lanzador para Linux (run.sh)
cat << 'EOF' > "$BUNDLE_DIR/run.sh"
#!/bin/bash
# Lanzador ejecutable para Linux / macOS
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

if command -v java &> /dev/null; then
    echo "🚀 Iniciando ERP+ Business v2.0.0..."
    java -jar ERP-Plus-Business-2.0.0.jar
else
    echo "❌ Error: Java no está instalado o no se encuentra en el PATH del sistema."
    echo "Por favor instala Java OpenJDK 21+ o utiliza el instalador embebido con JRE."
    read -p "Presiona Enter para salir..."
fi
EOF
chmod +x "$BUNDLE_DIR/run.sh"

# 5. Generar Script Lanzador para Windows (run.bat)
cat << 'EOF' > "$BUNDLE_DIR/run.bat"
@echo off
title ERP+ Business v2.0.0
cd /d "%~dp0"
echo ---------------------------------------------------
echo  Iniciando ERP+ Business v2.0.0 - ERP Intelligent
echo ---------------------------------------------------
java -jar ERP-Plus-Business-2.0.0.jar
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error al iniciar la aplicación. Verifica que Java 21+ esté instalado.
    pause
)
EOF

# 6. Copiar Scripts SQL de Demostración y Recursos de Icono
if [ -f "$PROJECT_DIR/poblar_master_demo.sql" ]; then
    cp "$PROJECT_DIR/poblar_master_demo.sql" "$BUNDLE_DIR/poblar_master_demo.sql"
fi

# 7. Crear Guía de Distribución
cat << 'EOF' > "$BUNDLE_DIR/README_DISTRIBUCION.md"
# ERP+ Business v2.0.0 — Guía de Distribución e Instalación

Bienvenido al paquete ejecutable portable de **ERP+ Business v2.0.0**.

---

## 🚀 Requisitos de Ejecución

- **Java Runtime Environment (JRE):** OpenJDK 21 o superior instalado.
- **Sistemas Operativos Soportados:** Linux (Ubuntu, Debian, Fedora, Arch), Microsoft Windows (10/11), macOS.

---

## 💻 Instrucciones de Inicio Rápido

### En Linux / macOS:
```bash
./run.sh
```
*(O ejecuta directamente en terminal: `java -jar ERP-Plus-Business-2.0.0.jar`)*

### En Windows:
Doble clic sobre el archivo **`run.bat`** o ejecuta en CMD:
```cmd
run.bat
```

---

## 🛡️ Licenciamiento y Activación de Software

1. Al iniciar por primera vez, el sistema entra en **Modo Prueba (30 Días Demo)** con acceso a todas las funcionalidades.
2. Para activar la licencia **PRO Permanente**:
   - Ve a **Configuración** -> **Licencia & Activación de Software** -> **Administrar Licencia**.
   - Haz clic en **Copiar Hardware ID** (código único de 16 caracteres formato `XXXX-XXXX-XXXX-XXXX`).
   - Envía tu Hardware ID a soporte o al administrador emisor.
   - Pega el token de activación firmado y haz clic en **Activar Licencia PRO**.

---

## 🛠️ Empaquetado Nativo con JRE Incluido (`jpackage`)

Si deseas generar un instalador nativo (`.deb`, `.rpm`, `.exe`) con el JRE embebido (para que funcione en cualquier PC sin que el cliente instale Java previamente), ejecuta desde la consola del sistema operativo:

### Crear paquete ejecutable de Linux (.deb):
```bash
jpackage \
  --name "ERP-Plus-Business" \
  --app-version "2.0.0" \
  --input . \
  --main-jar ERP-Plus-Business-2.0.0.jar \
  --main-class com.mycompany.zl_solucion_integral.Main \
  --type deb \
  --vendor "ChopCode Solutions" \
  --description "ERP+ Business Intelligent POS & Management"
```

### Crear instalador de Windows (.exe / .msi):
```cmd
jpackage ^
  --name "ERP-Plus-Business" ^
  --app-version "2.0.0" ^
  --input . ^
  --main-jar ERP-Plus-Business-2.0.0.jar ^
  --main-class com.mycompany.zl_solucion_integral.Main ^
  --type msi ^
  --win-shortcut ^
  --win-menu ^
  --vendor "ChopCode Solutions"
```
EOF

echo "🎉 Empaquetado portable completado exitosamente en $BUNDLE_DIR"
