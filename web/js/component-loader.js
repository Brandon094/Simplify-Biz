/**
 * ERP+ Business - Dynamic HTML Component Loader (Atomic Design Loader)
 * Carga de forma asíncrona todos los componentes HTML desacoplados (Atoms, Molecules, Organisms)
 * manteniendo el index.html limpio, semántico e inmutable.
 */

document.addEventListener('DOMContentLoaded', async () => {
    const includeElements = document.querySelectorAll('[data-include]');
    
    for (const el of includeElements) {
        const file = el.getAttribute('data-include');
        if (file) {
            try {
                const response = await fetch(file);
                if (response.ok) {
                    const htmlContent = await response.text();
                    el.outerHTML = htmlContent;
                } else {
                    console.error(`Error al cargar el componente: ${file}`);
                }
            } catch (err) {
                console.error(`Excepción al obtener el componente ${file}:`, err);
            }
        }
    }

    // Una vez cargados e inyectados todos los componentes en el DOM, inicializar el ViewModel
    if (typeof AppViewModel !== 'undefined') {
        new AppViewModel();
    }
});
