# Roadmap de ERP+ Business

Este documento define el orden recomendado para continuar el desarrollo. La regla principal es consolidar la experiencia antes de ampliar la lógica de negocio.

## Estado actual

### Completado en la iteración UI/UX

* [x] Identidad visual oscura y branding ERP+ Business.
* [x] **Tema claro alternable** mediante toggle ☀️/🌙 en el sidebar, con paletas centralizadas en `ThemeConstants` (principio DRY).
* [x] Login con panel moderno, SVG y flujo de acceso.
* [x] Ventana única con navegación lateral por rol.
* [x] Dashboard con KPIs, actividad operativa, gráficos, leyendas y estados vacíos.
* [x] Productos con formulario, iconos SVG, tabla y feedback sin registros.
* [x] Ventas con carrito, descuento, checkout, cliente genérico e iconos SVG.
* [x] Clientes convertidos en consulta de usuarios con rol `2`.
* [x] Empleados con registro y actualización para el administrador.
* [x] Reportes con filtros, acciones visuales, iconos SVG y feedback sin resultados.
* [x] Configuración con información técnica, SVG y enlace a `[https://portafolio-brandon-daza.web.app/](https://portafolio-brandon-daza.web.app/)`.
* [x] Proveedores con UI preparada, pero ocultos temporalmente del sidebar.
* [x] Documentación técnica, funcional y de base de datos actualizada.

## Fase 1: Cierre de UI/UX (Pulido y Microcopy)

* [x] Soporte de tema oscuro y claro con paletas centralizadas.
* [x] **Implementar Microcopy de contexto:** Añadir subtítulos descriptivos debajo del título principal de cada módulo (ej. "Administra tu catálogo y mantén el control de tu stock").
* [x] **Añadir Helper Texts (Textos de ayuda):** Incorporar textos pequeños y permanentes debajo de campos clave en los formularios (ej. debajo de contraseñas, códigos SKU o descuentos).
* [x] **Integrar Tooltips:** Configurar globos de ayuda al pasar el cursor sobre botones de acción con iconos SVG (ej. "Generar PDF", "Cambiar ruta").
* [x] **Hacer accionables los Estados Vacíos:** Cambiar los mensajes pasivos por textos conversacionales que guíen al usuario y agregar botones de "Llamado a la Acción" (CTA) donde aplique.
* [ ] Unificar validaciones visuales y mensajes de error.
* [ ] Añadir feedback de carga, éxito y error donde falte (ej. Skeletons o Shimmers para ocultar tiempos de carga).
* [ ] Revisar tamaños mínimos, paddings y comportamiento en distintas resoluciones para dar "respiro visual".
* [ ] Eliminar emojis restantes de las vistas.
* [ ] Probar accesibilidad básica: foco, contraste y navegación por teclado.
* [ ] Persistir la preferencia de tema del usuario (recordar oscuro/claro entre reinicios).

## Fase 2: Correcciones técnicas prioritarias (Core & Estabilidad)

* [ ] **Implementar Patrón Singleton en Base de Datos:** Crear clase `GestorConexion` para evitar la apertura múltiple de conexiones a SQLite y eliminar el spam de logs.
* [ ] Refactorizar DAOs para usar la conexión única y prevenir el error `database is locked`.
* [ ] Revisar la composición de `db.path` y el nombre real del archivo SQLite.
* [ ] Separar mensajes de UI de los controladores para facilitar pruebas.
* [ ] Añadir pruebas automatizadas a usuarios, productos y ventas.
* [ ] Revisar validaciones de números, fechas, cantidades y duplicados (evitar cierres inesperados por `NumberFormatException`).
* [ ] Evitar mostrar información sensible como contraseñas en las tablas de UI (ocultar columna o usar `****`).

## Fase 3: Módulos pendientes

* [ ] Crear persistencia real para proveedores.
* [ ] Implementar compras y entrada de inventario.
* [ ] Completar exportación Excel y PDF.
* [ ] Añadir historial de compras por cliente.
* [ ] Añadir cartera y control de ventas a crédito.
* [ ] Agregar filtros y búsqueda rápida al inventario.

## Fase 4: Inteligencia de negocio

* [ ] Utilidades reales: venta menos costo.
* [ ] Productos más vendidos.
* [ ] Alertas de stock crítico.
* [ ] Comparación de periodos en dashboard.
* [ ] Reportes con filtros persistentes y resumen ejecutivo.

## Fase 5: Escalabilidad

* [ ] Soporte para múltiples cajas o sucursales.
* [ ] Estrategia de sincronización o backend remoto.
* [ ] Módulo de gastos operativos.
* [ ] Políticas de respaldo y recuperación asistida.

## Fase 6: Distribución

* [ ] Instalador para Linux y Windows con JRE incluido.
* [ ] Versionado y notas de publicación.
* [ ] Licenciamiento.
* [ ] Manual de usuario integrado.

## Punto de continuidad

Antes de iniciar una nueva fase, ejecutar `mvn clean package`, abrir el JAR y revisar las pantallas afectadas. No cambiar el esquema SQLite sin respaldar la base de datos y actualizar el esquema y diccionario en la misma tarea.