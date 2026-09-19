/**
 * ERP+ Business - Dynamic HTML Component Loader (Atomic Design Loader)
 * Carga de forma asíncrona todos los componentes HTML desacoplados (Atoms, Molecules, Organisms)
 * manteniendo el index.html limpio, semántico e inmutable.
 */

document.addEventListener('DOMContentLoaded', async () => {
    // Función para incluir elementos asíncronos
    async function loadIncludes() {
        const includeElements = document.querySelectorAll('[data-include]');
        for (const el of includeElements) {
            const file = el.getAttribute('data-include');
            if (file) {
                try {
                    const response = await fetch(file);
                    if (response.ok) {
                        const htmlContent = await response.text();
                        
                        // Crear un contenedor temporal para extraer e inyectar el HTML y ejecutar sus scripts
                        const temp = document.createElement('div');
                        temp.innerHTML = htmlContent;
                        
                        // Reemplazar el elemento objetivo por el contenido HTML
                        el.replaceWith(...temp.childNodes);
                        
                        // Ejecutar explícitamente cualquier etiqueta <script> interna del componente
                        temp.querySelectorAll('script').forEach(oldScript => {
                            const newScript = document.createElement('script');
                            Array.from(oldScript.attributes).forEach(attr => newScript.setAttribute(attr.name, attr.value));
                            newScript.appendChild(document.createTextNode(oldScript.innerHTML));
                            document.body.appendChild(newScript);
                        });
                    } else {
                        console.error(`Error al cargar el componente: ${file}`);
                    }
                } catch (err) {
                    console.error(`Excepción al obtener el componente ${file}:`, err);
                }
            }
        }
    }

    // Ejecución de carga en primer y segundo nivel (sub-componentes anidados)
    await loadIncludes();
    await loadIncludes();

    // Una vez cargados e inyectados todos los componentes en el DOM, inicializar el ViewModel
    if (typeof AppViewModel !== 'undefined') {
        new AppViewModel();
    }
});
