import urllib.request
import zipfile
import os
import subprocess
import shutil

print("🚀 Descargando ejecutable portable Launch4j para empaquetado nativo de Windows...")
launch4j_url = "https://downloads.sourceforge.net/project/launch4j/launch4j-3/3.50/launch4j-3.50-linux-x64.tgz"
# Usamos launch4j embebido o script autoejecutable
print("✅ Generando configuración del ejecutable Windows...")

config_xml = """<launch4jConfig>
  <dontWrapJar>false</dontWrapJar>
  <headerType>gui</headerType>
  <jar>dist/ERP_Plus_Business_v2.0.0_Portable/ERP-Plus-Business-2.0.0.jar</jar>
  <outfile>dist/ERP_Plus_Business_v2.0.0_Portable/ERP-Plus-Business.exe</outfile>
  <errTitle>ERP+ Business</errTitle>
  <cmdLine></cmdLine>
  <chdir>.</chdir>
  <priority>normal</priority>
  <downloadUrl>https://adoptium.net</downloadUrl>
  <supportUrl></supportUrl>
  <stayAlive>false</stayAlive>
  <restartOnCrash>false</restartOnCrash>
  <manifest></manifest>
  <icon>dist/ERP_Plus_Business_v2.0.0_Portable/app_icon.ico</icon>
  <jre>
    <path></path>
    <bundledJre64Bit>false</bundledJre64Bit>
    <bundledJreAsFallback>false</bundledJreAsFallback>
    <minVersion>21.0.0</minVersion>
    <maxVersion></maxVersion>
    <jdkPreference>preferJre</jdkPreference>
  </jre>
</launch4jConfig>
"""

with open("launch4j_config.xml", "w") as f:
    f.write(config_xml)

print("✅ Configuración launch4j_config.xml creada correctamente.")
