# Roadmap de ERP+ Business

Este documento define el orden recomendado para continuar el desarrollo. La regla principal es consolidar la experiencia antes de ampliar la lógica de negocio.

## Estado actual

### Completado en la iteración UI/UX

- Identidad visual oscura y branding ERP+ Business.
- Login con panel moderno, SVG y flujo de acceso.
- Ventana única con navegación lateral por rol.
- Dashboard con KPIs, actividad operativa, gráficos, leyendas y estados vacíos.
- Productos con formulario, iconos SVG, tabla y feedback sin registros.
- Ventas con carrito, descuento, checkout, cliente genérico e iconos SVG.
- Clientes convertidos en consulta de usuarios con rol `2`.
- Empleados con registro y actualización para el administrador.
- Reportes con filtros, acciones visuales, iconos SVG y feedback sin resultados.
- Configuración con información técnica, SVG y enlace a `https://portafolio-brandon-daza.web.app/`.
- Proveedores con UI preparada, pero ocultos temporalmente del sidebar.
- Documentación técnica, funcional y de base de datos actualizada.

## Fase 1: Cierre de UI/UX

- [ ] Unificar validaciones y mensajes de error.
- [ ] Revisar tamaños mínimos y comportamiento en distintas resoluciones.
- [ ] Eliminar emojis restantes de las vistas.
- [ ] Añadir feedback de carga, éxito y error donde falte.
- [ ] Probar accesibilidad básica: foco, contraste y navegación por teclado.

## Fase 2: Correcciones técnicas prioritarias

- [ ] Revisar la composición de `db.path` y el nombre real del archivo SQLite.
- [ ] Separar mensajes de UI de los controladores para facilitar pruebas.
- [ ] Añadir pruebas automatizadas a usuarios, productos y ventas.
- [ ] Revisar validaciones de números, fechas, cantidades y duplicados.
- [ ] Evitar mostrar información sensible como contraseñas.

## Fase 3: Módulos pendientes

- [ ] Crear persistencia real para proveedores.
- [ ] Implementar compras y entrada de inventario.
- [ ] Completar exportación Excel y PDF.
- [ ] Añadir historial de compras por cliente.
- [ ] Añadir cartera y control de ventas a crédito.
- [ ] Agregar filtros y búsqueda rápida al inventario.

## Fase 4: Inteligencia de negocio

- [ ] Utilidades reales: venta menos costo.
- [ ] Productos más vendidos.
- [ ] Alertas de stock crítico.
- [ ] Comparación de periodos en dashboard.
- [ ] Reportes con filtros persistentes y resumen ejecutivo.

## Fase 5: Escalabilidad

- [ ] Soporte para múltiples cajas o sucursales.
- [ ] Estrategia de sincronización o backend remoto.
- [ ] Módulo de gastos operativos.
- [ ] Políticas de respaldo y recuperación asistida.

## Fase 6: Distribución

- [ ] Instalador para Linux y Windows con JRE incluido.
- [ ] Versionado y notas de publicación.
- [ ] Licenciamiento.
- [ ] Manual de usuario integrado.

## Punto de continuidad

Antes de iniciar una nueva fase, ejecutar `mvn clean package`, abrir el JAR y revisar las pantallas afectadas. No cambiar el esquema SQLite sin respaldar la base de datos y actualizar el esquema y diccionario en la misma tarea.
