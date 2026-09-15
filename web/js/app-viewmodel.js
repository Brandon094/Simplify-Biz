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

class AppViewModel {
    constructor() {
        this.mockModel = new MockupModel();
        this.init();
    }

    init() {
        this.bindMockupEvents();
        this.bindNavigationEvents();
    }

    // --- Enlace de la Maqueta Interactiva de PC ---
    bindMockupEvents() {
        const tabs = document.querySelectorAll('.mock-tab');
        tabs.forEach(tab => {
            tab.addEventListener('click', (e) => {
                const tabKey = tab.getAttribute('data-tab');
                this.switchMockupTab(tabKey, tab);
            });
        });
    }

    switchMockupTab(tabKey, activeTabElement) {
        const data = this.mockModel.tabsData[tabKey];
        if (!data) return;

        document.querySelectorAll('.mock-tab').forEach(t => {
            t.className = "mock-tab flex-1 md:flex-initial px-4 py-2.5 rounded-lg text-xs md:text-sm font-medium text-slate-400 hover:text-white hover:bg-white/5 flex items-center gap-2 transition-all";
        });
        activeTabElement.className = "mock-tab flex-1 md:flex-initial px-4 py-2.5 rounded-lg text-xs md:text-sm font-semibold flex items-center gap-2 transition-all bg-gradient-to-r from-blue-600 to-purple-600 text-white shadow-md";

        const elVentas = document.getElementById('vmVentas');
        const elGanancia = document.getElementById('vmGanancia');
        const elStock = document.getElementById('vmStock');
        const elTitle = document.getElementById('vmChartTitle');

        if (elVentas) elVentas.textContent = data.ventas;
        if (elGanancia) elGanancia.textContent = data.ganancia;
        if (elStock) elStock.textContent = data.stock;
        if (elTitle) elTitle.textContent = data.title;

        const bars = document.querySelectorAll('.vm-bar');
        bars.forEach((bar, index) => {
            if (data.bars[index] !== undefined) {
                bar.style.height = data.bars[index] + '%';
            }
        });
    }

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
