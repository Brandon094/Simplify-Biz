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
* [x] Unificar validaciones visuales y mensajes de error (controladores migrados a `UIUtils`/`UIMessages`).
* [x] Añadir feedback de carga, éxito y error donde falte (ej. Skeletons o Shimmers para ocultar tiempos de carga).
* [x] Revisar tamaños mínimos, paddings y comportamiento en distintas resoluciones para dar "respiro visual" (mínimo de ventana ajustado a 1120x700).
* [x] Eliminar emojis restantes de las vistas (verificado: sin emojis en `src/`).
* [x] Probar accesibilidad básica: foco, contraste y navegación por teclado (anillos de foco y navegación por teclado en sidebar y botones).
* [x] Persistir la preferencia de tema del usuario (recordar oscuro/claro entre reinicios).

## Fase 2: Diseño Responsive (Experiencia adaptable tipo móvil)

> **Objetivo:** que la aplicación se adapte con elegancia a ventanas pequeñas y pantallas
> reducidas (hasta ~360px de ancho lógico), con el mismo lenguaje visual. Como es una app
> de escritorio Java Swing y no una app web, "responsive" aquí significa **layout fluido y
> adaptativo**: reflow de paneles según el ancho, componentes que se apilan en vertical,
> navegación colapsable y tipografía/paddings escalables — la misma sensación que una app
> de celular, dentro del escritorio.

* [x] **Añadir un gestor de breakpoints:** clase `LayoutResponsive` con umbrales (móvil `< 480px`,
  tablet `480–960px`, escritorio `> 960px`) y listener de redimensionado.
* [x] **Sidebar colapsable:** en móvil/tablet la navegación lateral (260px) se oculta y se controla
  con un botón hamburguesa en el header; al navegar el drawer se cierra solo.
* [x] **Formularios a ancho fluido:** paneles de formulario de Productos, Empleados y Proveedores
  pasan de ancho fijo (350–380px) a 100% del ancho en móvil (se apilan arriba).
* [x] **Reflow vertical en móvil:** Ventas (`1x3`) y Dashboard (`1x4` y `1x2`) se apilan en una
  sola columna cuando el ancho no alcanza.
* [x] **Look & feel táctil:** `NeonButton` con altura mínima de 44px y filas de tabla a 36px
  (`ThemeConstants.TOUCH_TARGET_MIN` / `TABLE_ROW_HEIGHT`).
* [x] **Tipografía y paddings escalables:** clase `UIUtils.WrappingLabel` (basada en `JTextArea`
  con wrap nativo) que envuelve títulos y subtítulos sin recortarse, y paddings adaptativos en
  login/registro y header de cada módulo.
* [x] **Ajuste de formularios que se cortaban:** reflow de filtros y botones en Reportes; filas
  "etiqueta/valor" apiladas y campo+botón reordenados en Configuración; estado vacío con
  `UIUtils.ScrollablePanel` para ajustarse al viewport (elimina scroll horizontal y cortes de
  texto). Verificado en Productos, Clientes, Reportes, Configuración, Empleados y Proveedores.
* [x] **Tablas adaptables:** permitir scroll horizontal y/o vista de tarjetas (card list) en
  pantallas estrechas para las tablas de productos, ventas y reportes (pendiente: las tablas ya
  tienen scroll, falta la vista de tarjetas).
* [x] **Login y registro adaptables:** `ModernLoginPage` y `ModernAdminRegistrationPage` cambian a
  una sola columna (split vertical / tarjeta fluida) en pantallas pequeñas.
* [x] **Probar en resoluciones objetivo:** verificado con capturas offscreen a 380px (móvil) y
  1366/1400px (escritorio) en todas las vistas principales.

## Fase 3: Correcciones técnicas prioritarias (Core & Estabilidad)

* [x] **Implementar Patrón Singleton en Base de Datos:** Crear clase `GestorConexion` para evitar la apertura múltiple de conexiones a SQLite y eliminar el spam de logs.
* [x] Refactorizar DAOs para usar la conexión única y prevenir el error `database is locked`.
* [x] Revisar la composición de `db.path` y el nombre real del archivo SQLite (`db.db`).
* [x] Separar mensajes de UI de los controladores para facilitar pruebas (`ResultadoOperacion`).
* [x] Añadir pruebas automatizadas a usuarios, productos y ventas (JUnit 5 en `src/test/java`).
* [x] Revisar validaciones de números, fechas, cantidades y duplicados (evitar cierres inesperados por `NumberFormatException`).
* [x] Evitar mostrar información sensible como contraseñas en las tablas de UI (columna eliminada de los modelos).

## Fase 4: Módulos pendientes

* [ ] Crear persistencia real para proveedores.
* [ ] Implementar compras y entrada de inventario.
* [ ] Completar exportación Excel y PDF.
* [ ] Añadir historial de compras por cliente.
* [ ] Añadir cartera y control de ventas a crédito.
* [ ] Agregar filtros y búsqueda rápida al inventario.

## Fase 5: Inteligencia de negocio

* [ ] Utilidades reales: venta menos costo.
* [ ] Productos más vendidos.
* [ ] Alertas de stock crítico.
* [ ] Comparación de periodos en dashboard.
* [ ] Reportes con filtros persistentes y resumen ejecutivo.

## Fase 6: Escalabilidad

* [ ] Soporte para múltiples cajas o sucursales.
* [ ] Estrategia de sincronización o backend remoto.
* [ ] Módulo de gastos operativos.
* [ ] Políticas de respaldo y recuperación asistida.

## Fase 7: Distribución

* [ ] Instalador para Linux y Windows con JRE incluido.
* [ ] Versionado y notas de publicación.
* [ ] Licenciamiento.
* [ ] Manual de usuario integrado.

## Punto de continuidad

Antes de iniciar una nueva fase, ejecutar `mvn clean package`, abrir el JAR y revisar las pantallas afectadas. No cambiar el esquema SQLite sin respaldar la base de datos y actualizar el esquema y diccionario en la misma tarea.