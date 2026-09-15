/**
 * ERP+ Business - Main JS Controller (Mobile First Menu Drawer & Interactive Mockup)
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Mobile Menu Drawer Toggle
    const menuToggle = document.getElementById('menuToggle');
    const navLinks = document.getElementById('navLinks');

    if (menuToggle && navLinks) {
        menuToggle.addEventListener('click', () => {
            navLinks.classList.toggle('active');
            const isActive = navLinks.classList.contains('active');
            menuToggle.setAttribute('aria-expanded', isActive);
        });

        // Cierra el menú móvil al hacer clic en un enlace
        navLinks.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', () => {
                navLinks.classList.remove('active');
            });
        });
    }

    // 2. Mockup Tab Switcher (Simulación interactiva de la App Desktop)
    const mockItems = document.querySelectorAll('.mock-item');
    const mockBars = document.querySelectorAll('.bar');

    mockItems.forEach(item => {
        item.addEventListener('click', () => {
            mockItems.forEach(i => i.classList.remove('active'));
            item.classList.add('active');

            // Simular cambio dinámico de datos del gráfico en la maqueta
            mockBars.forEach(bar => {
                const randomHeight = Math.floor(Math.random() * 65) + 30;
                bar.style.height = randomHeight + '%';
            });
        });
    });

    // 3. Smooth Scroll para enlaces internos de navegación
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            if (targetId === '#') return;
            const targetElement = document.querySelector(targetId);
            if (targetElement) {
                targetElement.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
});
