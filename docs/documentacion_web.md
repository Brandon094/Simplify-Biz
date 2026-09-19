# Documentación del Sitio Web Landing & Ecosistema Firebase — ERP+ BUSINESS (v2.1.0)

> **Documentación del Portal Web Comercial & Servicios Cloud**  
> Especificación de la arquitectura web landing, patrón Atomic Design HTML5, integración con Firebase Cloud Services (Hosting & Cloud Firestore) y suite de analítica SEO.

---

## 1. Visión General & Dominio en Producción

El portal web de **ERP+ BUSINESS** es una landing page de alta conversión optimizada para dispositivos móviles (Mobile-First) desplegada sobre la infraestructura global de **Firebase Hosting**.

- **URL Oficial en Producción:** [https://erp-plus-business.web.app](https://erp-plus-business.web.app)
- **Dominio Alternativo:** [https://erp-plus-business.firebaseapp.com](https://erp-plus-business.firebaseapp.com)
- **Proyecto de Firebase:** `erp-plus-business`
- **Librería UI:** Vanilla HTML5 / JavaScript (ES6+ Modules) + TailwindCSS v3 (CDN)

---

## 2. Arquitectura Frontend (Atomic Design & MVVM)

El portal web está estructurado bajo la metodología **Atomic Design**, manteniendo `index.html` inmutable e inyectando asíncronamente cada componente desacoplado mediante el cargador dinámico `js/component-loader.js`.

```text
web/
├── index.html                           # Shell HTML principal, metadatos SEO y SDK de Firebase
├── firebase.json                        # Configuración de despliegue de Hosting & Rules de Firestore
├── firestore.rules                      # Reglas de seguridad estrictas en producción para Firestore
├── robots.txt                           # Instructivo para arañas de búsqueda Googlebot
├── sitemap.xml                          # Mapa del sitio indexable para Google Search Console
├── js/
│   ├── app-viewmodel.js                 # ViewModel MVVM: Carrusel de 9 capturas realistas y opiniones
│   └── component-loader.js              # Cargador dinámico de componentes Atomic Design (`data-include`)
├── components/
│   ├── atoms/
│   │   ├── hero-badge.html              # Badge Neón animado de valor comercial
│   │   └── nav-logo.html                # Logotipo vectorizado ERP+ Business
│   ├── molecules/
│   │   ├── app-preview-mockup.html      # Frame del monitor de PC con pestañas e interactividad táctil
│   │   └── nav-links.html               # Enlaces de navegación con drawer responsivo móvil
│   └── organisms/
│       ├── navbar.html                  # Header superior fijo con desenfoque glassmorphism
│       ├── hero.html                    # Sección principal con llamadas a la acción (CTAs)
│       ├── modules.html                 # Rejilla de beneficios reales y módulos principales
│       ├── calculator.html              # Cuadro comparativo de ahorro (Pago Único vs Arriendo)
│       ├── reviews.html                 # Sección de Opiniones y Reseñas en tiempo real (Cloud Firestore)
│       ├── downloads.html               # Tarjetas de descarga directa de paquetes (.deb / .zip)
│       └── footer.html                  # Pie de página sobrio con enlaces de soporte
├── assets/
│   ├── icons/                           # Ecosistema de 58+ iconos vectoriales SVG
│   └── images/                          # Capturas de pantalla reales de la aplicación Swing
└── downloads/                           # Instaladores binarios de producción
    ├── erp-plus-business_2.1.0_amd64.deb # Paquete nativo Debian / Ubuntu para Linux (67 MB)
    └── ERP-Plus-Business-Windows-Portable.zip # Paquete ejecutable portable para Windows
```

---

## 3. Integración con Firebase Cloud Services

### 3.1 Firebase Hosting
- **Directorio público:** `web/`
- **Despliegue rápido:** `npx -y firebase-tools@latest deploy --only hosting --project erp-plus-business`

### 3.2 Cloud Firestore Database (Módulo de Opiniones)
Las reseñas enviadas por los usuarios en la web se almacenan en tiempo real en la colección `reviews` de Cloud Firestore.

#### Esquema del Documento (`reviews/{reviewId}`):
```json
{
  "author": "Carlos Mendoza — Ferretería El Carmen",
  "comment": "Excelente programa. Lo compré para mi ferretería porque no quería seguir pagando mensualidades.",
  "rating": 5,
  "visibility": "public",
  "createdAt": "2026-09-19T04:39:00.000Z"
}
```

### 3.3 Reglas de Seguridad en Producción (`firestore.rules`)
Las reglas garantizan inmutabilidad y evitan inyecciones de datos:

```javascript
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /reviews/{reviewId} {
      // Lectura pública de opiniones aprobadas
      allow read: if resource.data.visibility == 'public';

      // Creación con validación estricta de esquema y tipos
      allow create: if 
        request.resource.data.keys().hasOnly(['author', 'comment', 'rating', 'visibility', 'createdAt']) &&
        request.resource.data.author is string && request.resource.data.author.size() <= 100 &&
        request.resource.data.comment is string && request.resource.data.comment.size() <= 1000 &&
        request.resource.data.rating is int && request.resource.data.rating >= 1 && request.resource.data.rating <= 5 &&
        request.resource.data.visibility in ['public', 'private'];

      // Bloqueo total de modificaciones y eliminaciones públicas
      allow update, delete: if false;
    }
  }
}
```

---

## 4. Funcionalidades de Experiencia de Usuario (UX)

1. **Carrusel de Capturas con Auto-Rotación e Interacción Táctil (Touch Swipe)**:
   - Rotación automática cada **4.5 segundos** recorriendo los 9 módulos reales del sistema.
   - **Pausa en Hover / Touch**: Frena la rotación al pasar el mouse o tocar la imagen.
   - **Gesto Swipe Móvil**: Soporta deslizamiento táctil izquierda/derecha en teléfonos móviles.
   - **Sin Salto de Scroll**: El cambio de imágenes no desplaza la pantalla ni interrumpe la lectura del usuario.

2. **Formulario de Calificación por Estrellas & Feedback**:
   - Selección interactiva de 1 a 5 estrellas con indicador cualitativo (*Excelente, Muy Bueno*, etc.).
   - Alerta visual neón verde de confirmación tras publicar la opinión.

3. **Tematización Neón DRY de Iconos SVG**:
   - Sistema de clases centralizadas (`.icon-svg`, `.icon-white`, `.icon-purple`, `.icon-emerald`, `.icon-blue`, `.icon-cyan`, `.icon-amber`) para colorear dinámicamente los vectores SVG sin duplicar CSS.

---

## 5. Optimización SEO & Google Search Console

- **`sitemap.xml`:** Lista todas las secciones principales (`#beneficios`, `#demostracion`, `#comparativa`, `#opiniones`, `#descargar`) para rastreo prioritario.
- **`robots.txt`:** Define directivas de acceso para arañas de motores de búsqueda (`Googlebot`).
- **Etiquetas OpenGraph y Meta Tags:** Configurados para previsualización enriquecida al compartir el enlace por WhatsApp y redes sociales.
