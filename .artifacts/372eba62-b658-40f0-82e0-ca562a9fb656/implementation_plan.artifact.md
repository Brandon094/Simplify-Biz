# Plan de Rediseño Moderno - Simplify Biz

Este plan detalla la transformación de Simplify Biz de una interfaz Swing tradicional a una experiencia de usuario moderna "Neon Cyberpunk/Dark" inspirada en el diseño compartido.

## User Review Required

> [!IMPORTANT]
> Cambiaremos el flujo de ventanas múltiples (donde cada módulo abre una nueva ventana) por una **Ventana Única con Navegación Lateral**. Esto mejorará drásticamente la fluidez de la aplicación.

## Proposed Changes

### 1. Infraestructura de UI (Dependencias y Temas)

#### [MODIFY] [pom.xml](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo Desktop/pom.xml)
- Añadir dependencia de `FlatLaf` (Laf moderno).
- Añadir `FlatLaf Extras` para soporte de iconos SVG.
- Añadir `swing-toast-notifications` para feedback visual no intrusivo.

#### [MODIFY] [Main.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo Desktop/src/main/java/com/mycompany/zl_solucion_integral/Main.java)
- Configurar `FlatLaf DarkLaf` al inicio de la aplicación.
- Personalizar colores globales (Púrpura Neón, Azul Eléctrico) a través de `UIManager`.

---

### 2. Componentes Base Modernos

#### [NEW] `ModernCard.java`
- Panel con bordes redondeados, sombra suave y fondo degradado opcional para métricas.

#### [NEW] `Sidebar.java`
- Componente de navegación vertical con iconos y estados activos estilizados.

---

### 3. Rediseño del Dashboard Principal

#### [NEW] [DashboardFrame.java](file:///home/brandond/Datos_Proyectos/Documentos/Desarrollo/Desarrollo Desktop/src/main/java/com/mycompany/zl_solucion_integral/views/DashboardFrame.java)
- Sustituirá a `FormMainWindow`.
- Contendrá el Sidebar a la izquierda y un contenedor principal (Content Area) dinámico.
- Implementará el diseño de métricas (Ventas totales, Órdenes, etc.) usando `ModernCard`.

#### [MODIFY] Vistas Existentes (Productos, Usuarios, Ventas)
- Convertir de `JFrame` a `JPanel`.
- Estilizar tablas usando `FlatLaf` para que se vean limpias y modernas.
- Eliminar botones de "Cerrar" internos, ya que la navegación será centralizada.

---

### 4. Pulido de UX (Transiciones y Feedback)

- Implementar notificaciones flotantes para acciones exitosas (ej: "Producto Guardado").
- Mejorar el `FormLogIn` con un diseño minimalista y centrado.

## Verification Plan

### Automated Tests
- Compilación completa con `mvn clean package` para verificar dependencias.
- Pruebas unitarias de controladores para asegurar que la lógica de negocio no se rompió al mover la UI.

### Manual Verification
- Verificar el escalado de la ventana principal.
- Comprobar que el cambio entre módulos en la barra lateral es instantáneo y no deja rastros de memoria.
- Validar la legibilidad de los textos neón sobre fondo oscuro.
