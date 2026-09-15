/**
 * ERP+ Business - Arquitectura Frontend MVVM (Model-View-ViewModel) & DRY
 */

// ==========================================================================
// 1. MODEL: Definición de datos de estado y fórmulas de cálculo de ROI
// ==========================================================================
class CalculatorModel {
    constructor() {
        this.ventasMensuales = 5000000;
        this.horasPerdidasSemana = 10;
        this.tasaHoraCOP = 15000; // Valor hora estándar comerciante
        this.porcentajeAhorroTiempo = 0.70; // 70% de reducción en cuadres
        this.porcentajePrevencionFugas = 0.045; // 4.5% de rescate en pérdidas de stock
    }

    calcularResultados() {
        const ahorroFugas = this.ventasMensuales * this.porcentajePrevencionFugas;
        const horasAhorradasMes = Math.round(this.horasPerdidasSemana * 4.33 * this.porcentajeAhorroTiempo);
        const valorTiempo = horasAhorradasMes * this.tasaHoraCOP;
        const ahorroTotalMes = Math.round(ahorroFugas + valorTiempo);

        return {
            ahorroTotalMes: ahorroTotalMes,
            horasAhorradasMes: horasAhorradasMes
        };
    }
}

class MockupModel {
    constructor() {
        this.tabsData = {
            dashboard: {
                ventas: "$ 1,480,000",
                ganancia: "$ 420,000",
                stock: "3 ítems",
                title: "Rendimiento Semanal de Ventas",
                bars: [45, 60, 85, 50, 95, 75]
            },
            pos: {
                ventas: "$ 2,150,000",
                ganancia: "$ 610,000",
                stock: "0 ítems",
                title: "Ventas POS en Tiempo Real",
                bars: [65, 80, 45, 90, 100, 85]
            },
            inventario: {
                ventas: "$ 980,000",
                ganancia: "$ 290,000",
                stock: "5 ítems",
                title: "Valoración de Stock & Kardex",
                bars: [30, 50, 70, 40, 60, 55]
            },
            cartera: {
                ventas: "$ 3,400,000",
                ganancia: "$ 890,000",
                stock: "1 ítem",
                title: "Recaudación de Abonos & Deudas",
                bars: [80, 70, 90, 65, 85, 95]
            }
        };
    }
}

// ==========================================================================
// 2. VIEWMODEL: Enlace reactivo entre eventos de la vista y los modelos
// ==========================================================================
class AppViewModel {
    constructor() {
        this.calcModel = new CalculatorModel();
        this.mockModel = new MockupModel();
        this.init();
    }

    init() {
        this.bindCalculatorEvents();
        this.bindMockupEvents();
        this.bindNavigationEvents();
        
        // Render inicial
        this.updateCalculatorUI();
    }

    // --- Enlace de la Calculadora ---
    bindCalculatorEvents() {
        const inputVentas = document.getElementById('inputVentas');
        const inputHoras = document.getElementById('inputHoras');

        if (inputVentas && inputHoras) {
            inputVentas.addEventListener('input', (e) => {
                this.calcModel.ventasMensuales = parseFloat(e.target.value);
                this.updateCalculatorUI();
            });

            inputHoras.addEventListener('input', (e) => {
                this.calcModel.horasPerdidasSemana = parseFloat(e.target.value);
                this.updateCalculatorUI();
            });
        }
    }

    updateCalculatorUI() {
        const txtValVentas = document.getElementById('txtValVentas');
        const txtValHoras = document.getElementById('txtValHoras');
        const outDinero = document.getElementById('outDinero');
        const outHoras = document.getElementById('outHoras');

        if (!txtValVentas || !outDinero) return;

        const res = this.calcModel.calcularResultados();

        txtValVentas.textContent = '$' + this.calcModel.ventasMensuales.toLocaleString('es-CO') + ' COP';
        txtValHoras.textContent = this.calcModel.horasPerdidasSemana + ' hrs / semana';

        outDinero.textContent = '$' + res.ahorroTotalMes.toLocaleString('es-CO') + ' COP / mes';
        outHoras.textContent = 'Recuperas aprox. ' + res.horasAhorradasMes + ' horas libres al mes';
    }

    // --- Enlace de la Maqueta Interactiva (Mockup MVVM) ---
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

        // Actualizar estados visuales de los tabs
        document.querySelectorAll('.mock-tab').forEach(t => {
            t.className = "mock-tab flex-1 md:flex-initial px-4 py-2.5 rounded-lg text-xs md:text-sm font-medium text-slate-400 hover:text-white hover:bg-white/5 flex items-center gap-2 transition-all";
        });
        activeTabElement.className = "mock-tab flex-1 md:flex-initial px-4 py-2.5 rounded-lg text-xs md:text-sm font-semibold flex items-center gap-2 transition-all bg-gradient-to-r from-blue-600 to-purple-600 text-white shadow-md";

        // Actualizar Vista Reactiva (Bindings)
        document.getElementById('vmVentas').textContent = data.ventas;
        document.getElementById('vmGanancia').textContent = data.ganancia;
        document.getElementById('vmStock').textContent = data.stock;
        document.getElementById('vmChartTitle').textContent = data.title;

        // Animar barras
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

// Instanciación del ViewModel al cargar el DOM
document.addEventListener('DOMContentLoaded', () => {
    new AppViewModel();
});
