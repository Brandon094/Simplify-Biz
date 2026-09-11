# Documentación Técnica - Vortex ERP

**Desarrollado por: ChopCode Solutions**

## Visión General
Vortex ERP es una plataforma de escritorio robusta para la gestión de negocios.

## Arquitectura del Sistema
El sistema utiliza una arquitectura **MVC (Modelo-Vista-Controlador)** potenciada por **Atomic Design** en la capa de presentación.

### Capas:
- **Atoms & Molecules:** Componentes de UI base como `NeonButton`, `NeonLineChart` y `SidebarItem`.
- **Organisms & Templates:** Estructuras complejas como `ModernSidebar` y el contenedor central `MainTemplate`.
- **Controllers:** Manejan la lógica de negocio y la persistencia (e.g., `VentasController`, `ProductoController`).
- **Models:** Entidades de datos que representan `Producto`, `Venta`, `Usuario`, etc.

## Tecnologías y Librerías
- **JDK:** 17+ (Compilado con compatibilidad amplia).
- **UI Framework:** Java Swing + **FlatLaf** (Dark Theme).
- **Base de Datos:** SQLite (Transacciones locales rápidas y sin configuración).
- **Visualización:** Custom Graphics2D para gráficos neón dinámicos.

## Estructura de la Base de Datos
- `usuarios`: Gestión de roles y credenciales.
- `productos`: Inventario, costos y categorías.
- `ventas`: Encabezados de facturación.
- `detalles_venta`: Relación de ítems por factura.
- `configuracion`: Parámetros globales y contadores.

## Flujo de Navegación
La aplicación ha sido refactorizada para usar un modelo de **Single Frame / Dynamic Content**, eliminando la dispersión de ventanas y mejorando la fluidez operativa del usuario final.
