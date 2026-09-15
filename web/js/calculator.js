/**
 * ERP+ Business - Calculadora Interactiva de ROI & Ahorro Comercial
 * Basado en Psicología del Consumidor & Neuroventas
 */

document.addEventListener('DOMContentLoaded', () => {
    const rangeVentas = document.getElementById('rangeVentas');
    const rangeHoras = document.getElementById('rangeHoras');
    
    const valVentas = document.getElementById('valVentas');
    const valHoras = document.getElementById('valHoras');
    
    const resDinero = document.getElementById('resDinero');
    const resHoras = document.getElementById('resHoras');

    if (!rangeVentas || !rangeHoras) return;

    function calcularROI() {
        const ventasMes = parseFloat(rangeVentas.value);
        const horasPerdidasSemana = parseFloat(rangeHoras.value);

        // Formato COP / Moneda
        valVentas.textContent = '$' + ventasMes.toLocaleString('es-CO') + ' COP';
        valHoras.textContent = horasPerdidasSemana + ' hrs / semana';

        // Estimación Neuroventas:
        // 1. Reducción de fugas de dinero por stock descontrolado/errores en caja (aprox 4.5% de ventas mensuales)
        const ahorroFugas = ventasMes * 0.045;
        
        // 2. Valor del tiempo ahorrado (horas semanales * 4.33 semanas/mes * tasa estimada de hora $15,000 COP)
        const horasAhorradasMes = Math.round(horasPerdidasSemana * 4.33 * 0.70); // 70% de ahorro de tiempo
        const valorTiempo = horasAhorradasMes * 15000;

        const ahorroTotalMes = Math.round(ahorroFugas + valorTiempo);

        // Animación suave de los resultados
        resDinero.textContent = '$' + ahorroTotalMes.toLocaleString('es-CO') + ' COP / mes';
        resHoras.textContent = '⏱️ Recuperas aprox. ' + horasAhorradasMes + ' horas libres al mes';
    }

    rangeVentas.addEventListener('input', calcularROI);
    rangeHoras.addEventListener('input', calcularROI);

    // Ejecución inicial
    calcularROI();
});
