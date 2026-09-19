#!/bin/bash
# ==============================================================================
# Script de Empaquetado y Distribución Oficial — ERP+ Business v2.1.0
# ==============================================================================

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIST_DIR="$PROJECT_DIR/dist"
BUNDLE_DIR="$DIST_DIR/ERP_Plus_Business_v2.1.0_Portable"

echo "🚀 Iniciando proceso de empaquetado de ERP+ Business v2.1.0..."

# 1. Limpieza y Creación de Carpetas
rm -rf "$BUNDLE_DIR"
mkdir -p "$BUNDLE_DIR/Linux_macOS"
mkdir -p "$BUNDLE_DIR/Windows"

# 2. Compilar y empaquetar con Maven
echo "📦 Compilando ejecutable Shaded JAR con Maven..."
cd "$PROJECT_DIR"
mvn clean package -DskipTests

# 3. Copiar ejecutable JAR a ambas plataformas
JAR_FILE=$(find "$DIST_DIR" target/ -name "ERP-Plus-Business-2.1.0.jar" -o -name "ERP-Plus-Business-*.jar" 2>/dev/null | head -n 1)
if [ -f "$JAR_FILE" ]; then
    cp "$JAR_FILE" "$BUNDLE_DIR/Linux_macOS/ERP-Plus-Business-2.1.0.jar"
    cp "$JAR_FILE" "$BUNDLE_DIR/Windows/ERP-Plus-Business-2.1.0.jar"
    echo "✅ Executable JAR copiado a subcarpetas Linux_macOS y Windows"
else
    echo "❌ Error: No se encontró el JAR compilado."
    exit 1
fi

# 4. Generar Script Lanzador para Linux / macOS (Linux_macOS/run.sh)
cat << 'EOF' > "$BUNDLE_DIR/Linux_macOS/run.sh"
#!/bin/bash
# Lanzador ejecutable para Linux / macOS
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

if command -v java &> /dev/null; then
    echo "🚀 Iniciando ERP+ Business v2.1.0..."
    java -jar ERP-Plus-Business-2.1.0.jar
else
    echo "❌ Error: Java no está instalado o no se encuentra en el PATH del sistema."
    echo "Por favor instala Java OpenJDK 21+ o utiliza el instalador embebido con JRE."
    read -p "Presiona Enter para salir..."
fi
EOF
chmod +x "$BUNDLE_DIR/Linux_macOS/run.sh"

# 5. Generar Scripts Lanzadores para Windows (Windows/run.bat y ERP-Plus-Business.vbs)
cat << 'EOF' > "$BUNDLE_DIR/Windows/run.bat"
@echo off
title ERP+ Business v2.1.0
cd /d "%~dp0"
echo ---------------------------------------------------
echo  Iniciando ERP+ Business v2.1.0 - ChopCode Solutions
echo ---------------------------------------------------
start javaw -jar ERP-Plus-Business-2.1.0.jar
EOF

# Lanzador gráfico sin ventana negra de consola para Windows
cat << 'EOF' > "$BUNDLE_DIR/Windows/ERP-Plus-Business.vbs"
Set WshShell = CreateObject("WScript.Shell")
WshShell.Run "javaw -jar ERP-Plus-Business-2.1.0.jar", 0, False
EOF

# 6. Copiar Scripts SQL de Demostración
if [ -f "$PROJECT_DIR/poblar_master_demo.sql" ]; then
    cp "$PROJECT_DIR/poblar_master_demo.sql" "$BUNDLE_DIR/poblar_master_demo.sql"
fi

# 7. Crear Guía de Distribución
cat << 'EOF' > "$BUNDLE_DIR/README_DISTRIBUCION.md"
# ERP+ Business v2.1.0 — Guía de Distribución e Instalación

Bienvenido al paquete ejecutable portable de **ERP+ Business v2.1.0**.

---

## 🚀 Requisitos de Ejecución

- **Java Runtime Environment (JRE):** OpenJDK 21 o superior instalado.
- **Sistemas Operativos Soportados:** Linux (Ubuntu, Debian, Fedora, Arch), macOS (macOS 11+), Microsoft Windows (10/11).

---

## 💻 Instrucciones de Inicio Rápido por Sistema Operativo

### 🐧 🍎 En Linux / macOS:
1. Abre la carpeta `Linux_macOS/`.
2. Ejecuta el script lanzador:
   ```bash
   ./run.sh
   ```
   *(O directamente en consola: `java -jar ERP-Plus-Business-2.1.0.jar`)*

### 🪟 En Microsoft Windows:
1. Abre la carpeta `Windows/`.
2. Haz doble clic sobre **`run.bat`** o sobre **`ERP-Plus-Business.vbs`** (para iniciar sin consola negra).

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

Si deseas generar un instalador nativo (`.deb`, `.rpm`, `.exe`) con el JRE embebido (para que funcione en cualquier PC sin que el cliente instale Java previamente), ejecuta desde la consola del sistema operativo dentro de la carpeta correspondiente (`Linux_macOS/` o `Windows/`):

### Crear paquete ejecutable de Linux (.deb):
```bash
jpackage \
  --name "ERP-Plus-Business" \
  --app-version "2.1.0" \
  --input . \
  --main-jar ERP-Plus-Business-2.1.0.jar \
  --main-class com.mycompany.zl_solucion_integral.Main \
  --type deb \
  --vendor "ChopCode Solutions" \
  --description "ERP+ Business Intelligent POS & Management"
```

### Crear instalador de Windows (.exe / .msi):
```cmd
jpackage ^
  --name "ERP-Plus-Business" ^
  --app-version "2.1.0" ^
  --input . ^
  --main-jar ERP-Plus-Business-2.1.0.jar ^
  --main-class com.mycompany.zl_solucion_integral.Main ^
  --type msi ^
  --win-shortcut ^
  --win-menu ^
  --vendor "ChopCode Solutions"
```
EOF

echo "🎉 Empaquetado portable completado exitosamente en $BUNDLE_DIR"

