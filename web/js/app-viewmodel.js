/**
 * ERP+ Business - Embudo Comercial MVVM (Model-View-ViewModel)
 * Manejo de estado dinámico de pestañas e interacción sin tecnicismos
 */

class MockupModel {
    constructor() {
        this.tabsData = {
            dashboard: {
                ventas: "$ 1,480,000",
                ganancia: "$ 420,000",
                stock: "3 ítems por agotarse",
                title: "Ventas y Ganancias del Día",
                bars: [45, 60, 85, 50, 95, 75]
            },
            pos: {
                ventas: "$ 2,150,000",
                ganancia: "$ 610,000",
                stock: "0 ítems",
                title: "Registro Rápido en Caja",
                bars: [65, 80, 45, 90, 100, 85]
            },
            inventario: {
                ventas: "$ 980,000",
                ganancia: "$ 290,000",
                stock: "5 ítems por agotar",
                title: "Control de Inventario y Bodega",
                bars: [30, 50, 70, 40, 60, 55]
            },
            cartera: {
                ventas: "$ 3,400,000",
                ganancia: "$ 890,000",
                stock: "1 ítem",
                title: "Control de Cuentas por Cobrar (Fiados)",
                bars: [80, 70, 90, 65, 85, 95]
            }
        };
    }
}

// --- Carousel ViewModel para Maqueta de PC con Auto-Rotación e Interacción Manual ---
class ScreenshotCarouselViewModel {
    constructor() {
        this.slides = [
            { src: 'assets/images/dashboard.png', caption: 'Panel Principal (Dashboard BI)' },
            { src: 'assets/images/punto_de_venta.png', caption: 'Punto de Venta (POS Rápido)' },
            { src: 'assets/images/inventario.png', caption: 'Gestión de Inventario & Stock' },
            { src: 'assets/images/abastecimiento.png', caption: 'Abastecimiento de Facturas' },
            { src: 'assets/images/cartera_clientes.png', caption: 'Cuentas por Cobrar (Cartera)' },
            { src: 'assets/images/directorio_clientes.png', caption: 'Directorio & Historial de Clientes' },
            { src: 'assets/images/gestion_empleados.png', caption: 'Gestión de Empleados & Roles' },
            { src: 'assets/images/centro_reportes.png', caption: 'Centro de Reportes BI & Excel' },
            { src: 'assets/images/configuracion.png', caption: 'Ajustes del Sistema & Licencia' }
        ];
        this.currentIndex = 0;
        this.intervalId = null;
        this.delayMs = 4500; // 4.5 segundos por diapositiva
        this.isPaused = false;
        
        this.init();
    }

    init() {
        this.bindEvents();
        this.startAutoPlay();
    }

    bindEvents() {
        const mockupContainer = document.getElementById('demostracion');
        if (mockupContainer) {
            // Pausar auto-rotación cuando el usuario pasa el mouse o toca la pantalla
            mockupContainer.addEventListener('mouseenter', () => this.pauseAutoPlay());
            mockupContainer.addEventListener('mouseleave', () => this.resumeAutoPlay());
            
            // Soporte Gestos Touch (Swipe Mobile Left / Right)
            let touchStartX = 0;
            let touchEndX = 0;

            mockupContainer.addEventListener('touchstart', (e) => {
                touchStartX = e.changedTouches[0].screenX;
                this.pauseAutoPlay();
            }, { passive: true });

            mockupContainer.addEventListener('touchend', (e) => {
                touchEndX = e.changedTouches[0].screenX;
                this.handleSwipe(touchStartX, touchEndX);
                this.resumeAutoPlay();
            }, { passive: true });
        }

        const tabs = document.querySelectorAll('.preview-tab');
        tabs.forEach((tab, idx) => {
            tab.addEventListener('click', () => {
                this.goToSlide(idx);
            });
        });
    }

    handleSwipe(startX, endX) {
        const threshold = 40; // Desplazamiento mínimo en px para cambiar slide
        if (startX - endX > threshold) {
            // Swipe a la izquierda -> Siguiente imagen
            this.nextSlide();
        } else if (endX - startX > threshold) {
            // Swipe a la derecha -> Imagen anterior
            this.prevSlide();
        }
    }

    prevSlide() {
        this.currentIndex = (this.currentIndex - 1 + this.slides.length) % this.slides.length;
        this.updateUI();
    }

    goToSlide(index) {
        if (index < 0 || index >= this.slides.length) return;
        this.currentIndex = index;
        this.updateUI();
    }

    nextSlide() {
        this.currentIndex = (this.currentIndex + 1) % this.slides.length;
        this.updateUI();
    }

    updateUI() {
        const slideData = this.slides[this.currentIndex];
        const img = document.getElementById('main-preview-img');
        const cap = document.getElementById('preview-caption');
        const tabs = document.querySelectorAll('.preview-tab');

        if (img) {
            img.style.opacity = '0.2';
            setTimeout(() => {
                img.src = slideData.src;
                if (cap) cap.textContent = slideData.caption + ' — Captura real completa de la aplicación en PC';
                img.style.opacity = '1';
            }, 180);
        }

        tabs.forEach((tab, idx) => {
            if (idx === this.currentIndex) {
                tab.className = "preview-tab active px-3 py-1.5 rounded-lg bg-purple-600/40 text-purple-200 border border-purple-400/50 font-semibold transition-all whitespace-nowrap shadow-lg shadow-purple-900/40 scale-105";
                // Asegurar que la pestaña activa sea visible en scroll horizontal
                tab.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' });
            } else {
                tab.className = "preview-tab px-3 py-1.5 rounded-lg bg-slate-800/80 text-slate-400 hover:text-white border border-white/5 font-medium transition-all whitespace-nowrap opacity-80 hover:opacity-100";
            }
        });
    }

    startAutoPlay() {
        if (this.intervalId) clearInterval(this.intervalId);
        this.intervalId = setInterval(() => {
            if (!this.isPaused) {
                this.nextSlide();
            }
        }, this.delayMs);
    }

    pauseAutoPlay() {
        this.isPaused = true;
    }

    resumeAutoPlay() {
        this.isPaused = false;
    }
}

class AppViewModel {
    constructor() {
        this.mockModel = new MockupModel();
        this.init();
    }

    init() {
        this.bindMockupEvents();
        this.bindNavigationEvents();
        this.carouselVM = new ScreenshotCarouselViewModel();
    }

    // --- Enlace de la Maqueta Interactiva de PC ---
    bindMockupEvents() {}

    // --- Enlace de la Navegación Móvil (Drawer Toggle) ---
    bindNavigationEvents() {
        const menuToggle = document.getElementById('menuToggle');
        const navLinks = document.getElementById('navLinks');

        if (menuToggle && navLinks) {
            menuToggle.addEventListener('click', () => {
                const isOpen = navLinks.classList.contains('opacity-100');
                if (isOpen) {
                    navLinks.classList.add('-translate-y-[150%]', 'opacity-0');
                    navLinks.classList.remove('translate-y-0', 'opacity-100');
                } else {
                    navLinks.classList.remove('-translate-y-[150%]', 'opacity-0');
                    navLinks.classList.add('translate-y-0', 'opacity-100');
                }
            });

            navLinks.querySelectorAll('a').forEach(link => {
                link.addEventListener('click', () => {
                    navLinks.classList.add('-translate-y-[150%]', 'opacity-0');
                    navLinks.classList.remove('translate-y-0', 'opacity-100');
                });
            });
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new AppViewModel();
});
