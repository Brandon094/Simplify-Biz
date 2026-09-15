/**
 * ERP+ Business - Main JS Controller & Dynamic Mockup Switcher
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Mockup Tab Switcher (Simulación interactiva de la App Desktop)
    const mockItems = document.querySelectorAll('.mock-item');
    const mockCards = document.querySelectorAll('.mock-card');
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

    // 2. Smooth Scroll para enlaces internos de navegación
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

    // 3. Navbar Sticky Glassmorphism Blur Effect en scroll
    const navbar = document.querySelector('.navbar');
    window.addEventListener('scroll', () => {
        if (window.scrollY > 40) {
            navbar.style.background = 'rgba(11, 15, 25, 0.95)';
            navbar.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.5)';
        } else {
            navbar.style.background = 'rgba(11, 15, 25, 0.8)';
            navbar.style.boxShadow = 'none';
        }
    });
});
