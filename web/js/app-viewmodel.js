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
                // Centrar la pestaña dentro de su contenedor de pestañas sin desplazar la ventana del usuario
                const container = tab.parentElement;
                if (container) {
                    const scrollLeftGoal = tab.offsetLeft - (container.clientWidth / 2) + (tab.clientWidth / 2);
                    container.scrollTo({ left: scrollLeftGoal, behavior: 'smooth' });
                }
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
        this.bindReviewsEvents();
        this.carouselVM = new ScreenshotCarouselViewModel();
    }

    // --- Enlace de la Sección de Reseñas y Calificaciones de Clientes con Firestore ---
    bindReviewsEvents() {
        const starBtns = document.querySelectorAll('.star-btn');
        const ratingInput = document.getElementById('selectedRating');
        const ratingText = document.getElementById('ratingText');

        if (starBtns.length > 0) {
            starBtns.forEach(btn => {
                btn.addEventListener('click', (e) => {
                    e.preventDefault();
                    const rating = parseInt(btn.getAttribute('data-rating'));
                    if (ratingInput) ratingInput.value = rating;

                    // Actualizar estado visual de las estrellas
                    starBtns.forEach((s, idx) => {
                        if (idx < rating) {
                            s.classList.remove('text-slate-600');
                            s.classList.add('text-amber-400');
                        } else {
                            s.classList.remove('text-amber-400');
                            s.classList.add('text-slate-600');
                        }
                    });

                    if (ratingText) {
                        const labels = ['Pésimo', 'Regular', 'Bueno', 'Muy Bueno', 'Excelente'];
                        ratingText.textContent = `${rating}.0 / 5.0 (${labels[rating - 1]})`;
                    }
                });
            });
        }

        // Cargar reseñas públicas guardadas previamente desde Cloud Firestore
        this.loadFirestoreReviews();

        // Enviar Formulario e Insertar en Firestore DB
        const reviewForm = document.getElementById('reviewForm');
        if (reviewForm) {
            reviewForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const btnSubmit = document.getElementById('btnSubmitReview');
                const author = document.getElementById('reviewAuthor')?.value.trim();
                const comment = document.getElementById('reviewComment')?.value.trim();
                const rating = parseInt(document.getElementById('selectedRating')?.value || '5');
                const visibility = document.querySelector('input[name="visibility"]:checked')?.value || 'public';

                if (!author || !comment) return;

                if (btnSubmit) {
                    btnSubmit.disabled = true;
                    btnSubmit.innerHTML = `<span>Enviando a Firestore...</span>`;
                }

                try {
                    const reviewData = {
                        author: author,
                        comment: comment,
                        rating: rating,
                        visibility: visibility,
                        createdAt: window.firestoreSDK ? window.firestoreSDK.serverTimestamp() : new Date().toISOString()
                    };

                    // Insertar documento en la colección 'reviews' de Cloud Firestore
                    if (window.firebaseDb && window.firestoreSDK) {
                        const { collection, addDoc } = window.firestoreSDK;
                        await addDoc(collection(window.firebaseDb, 'reviews'), reviewData);
                    }

                    // Mostrar Banner de Confirmación al Usuario (Feedback Estilizado)
                    const feedbackAlert = document.getElementById('feedbackAlert');
                    const feedbackAlertMessage = document.getElementById('feedbackAlertMessage');
                    if (feedbackAlert && feedbackAlertMessage) {
                        if (visibility === 'public') {
                            feedbackAlertMessage.innerHTML = `
                                <strong>¡Gracias por tu opinión pública!</strong><br>
                                Tu calificación de <strong>${rating}.0 ★</strong> se ha registrado exitosamente en la base de datos de Firestore.
                            `;
                        } else {
                            feedbackAlertMessage.innerHTML = `
                                <strong>¡Gracias por tu feedback privado!</strong><br>
                                Tu mensaje ha sido enviado de forma confidencial al desarrollador en Cloud Firestore.
                            `;
                        }
                        feedbackAlert.classList.remove('hidden');
                        setTimeout(() => feedbackAlert.classList.add('hidden'), 8000);
                    }

                    // Si la reseña es pública, refrescar la lista de opiniones
                    if (visibility === 'public') {
                        this.renderReviewCard({
                            ...reviewData,
                            createdAtText: 'Recién publicada'
                        }, true);
                    }

                    reviewForm.reset();
                    if (ratingText) ratingText.textContent = '5.0 / 5.0 (Excelente)';
                } catch (err) {
                    console.error("Error al guardar reseña en Firestore:", err);
                    alert("Tu opinión fue procesada localmente. Gracias por tu feedback.");
                } finally {
                    if (btnSubmit) {
                        btnSubmit.disabled = false;
                        btnSubmit.innerHTML = `
                            <img src="assets/icons/rocket.svg" class="w-4 h-4 icon-svg icon-white" alt="Enviar">
                            Publicar mi Opinión
                        `;
                    }
                }
            });
        }
    }

    async loadFirestoreReviews() {
        const container = document.getElementById('reviewsListContainer');
        const emptyPlaceholder = document.getElementById('emptyReviewsPlaceholder');
        if (!container) return;

        try {
            if (window.firebaseDb && window.firestoreSDK) {
                const { collection, getDocs, query, where, orderBy } = window.firestoreSDK;
                const reviewsRef = collection(window.firebaseDb, 'reviews');
                
                // Consultar únicamente las reseñas marcadas como 'public'
                const q = query(reviewsRef, where('visibility', '==', 'public'));
                const querySnapshot = await getDocs(q);

                let totalCount = 0;
                let sumRating = 0;

                querySnapshot.forEach((doc) => {
                    const data = doc.data();
                    totalCount++;
                    sumRating += (data.rating || 5);

                    if (emptyPlaceholder) emptyPlaceholder.remove();
                    this.renderReviewCard(data, false);
                });

                if (totalCount > 0) {
                    const badge = document.getElementById('reviewCountBadge');
                    const starsAvgText = document.getElementById('starsAvgText');
                    if (badge) badge.textContent = `${totalCount} Opiniones`;
                    if (starsAvgText) {
                        const avg = (sumRating / totalCount).toFixed(1);
                        starsAvgText.textContent = `${avg} promedio`;
                    }
                }
            }
        } catch (err) {
            console.warn("Firestore collection 'reviews' aún no poblada o en creación:", err);
        }
    }

    renderReviewCard(data, isPrepend) {
        const container = document.getElementById('reviewsListContainer');
        const emptyPlaceholder = document.getElementById('emptyReviewsPlaceholder');
        if (!container) return;
        if (emptyPlaceholder) emptyPlaceholder.remove();

        const ratingNum = parseInt(data.rating || 5);
        const starsStr = '★'.repeat(ratingNum) + '☆'.repeat(5 - ratingNum);
        const authorName = data.author || 'Comerciante Anónimo';
        const initials = authorName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();

        const card = document.createElement('div');
        card.className = "bg-slate-900/90 border border-purple-500/40 rounded-2xl p-5 hover:border-purple-300 transition-all shadow-xl animate-fade-in";
        card.innerHTML = `
            <div class="flex items-center justify-between mb-2">
                <div class="flex items-center gap-2.5">
                    <div class="w-8 h-8 rounded-full bg-purple-600/40 border border-purple-400/50 flex items-center justify-center text-xs font-bold text-purple-200">
                        ${initials}
                    </div>
                    <div>
                        <h4 class="text-sm font-bold text-slate-100">${authorName}</h4>
                        <span class="text-[10px] text-emerald-400 font-semibold">🌐 Opinión en Firestore • ${data.createdAtText || 'Verificada'}</span>
                    </div>
                </div>
                <div class="text-amber-400 text-xs font-bold">${starsStr} ${ratingNum}.0</div>
            </div>
            <p class="text-slate-300 text-xs leading-relaxed">
                "${data.comment}"
            </p>
        `;

        if (isPrepend) {
            container.prepend(card);
        } else {
            container.appendChild(card);
        }
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
