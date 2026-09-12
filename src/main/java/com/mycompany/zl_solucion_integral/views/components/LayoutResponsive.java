package com.mycompany.zl_solucion_integral.views.components;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Gestor central de diseño adaptable (responsive) para la aplicación.
 *
 * <p>
 * Como esta es una app de escritorio Java Swing (no web), "responsive"
 * significa un <b>layout fluido y adaptativo</b>: los paneles reaccionan al
 * ancho disponible, apilándose en vertical en pantallas estrechas y
 * aprovechando varias columnas en pantallas amplias.
 * </p>
 *
 * <p>
 * Los umbrales (breakpoints) están centralizados aquí (principio DRY) para
 * que todas las vistas usen los mismos tres modos:
 * </p>
 * <ul>
 * <li>{@link Breakpoint#MOVIL} — menos de 480px de ancho lógico.</li>
 * <li>{@link Breakpoint#TABLET} — de 480px a 959px.</li>
 * <li>{@link Breakpoint#ESCRITORIO} — 960px o más.</li>
 * </ul>
 *
 * <p>
 * Uso típico: una vista registra un {@link BreakpointListener} sobre su
 * componente raíz y reorganiza sus paneles cuando cambia el breakpoint.
 * </p>
 */
public final class LayoutResponsive {

    /** Umbral inferior (px) del modo tablet. Por debajo es móvil. */
    public static final int ANCHO_MOVIL_MAX = 480;

    /** Umbral inferior (px) del modo escritorio. Por debajo es tablet. */
    public static final int ANCHO_TABLET_MAX = 960;

    /** Breakpoints soportados por la aplicación. */
    public enum Breakpoint {
        MOVIL,
        TABLET,
        ESCRITORIO
    }

    /** Notificado cuando el ancho de un componente cruza un breakpoint. */
    public interface BreakpointListener {
        void onBreakpointChanged(Breakpoint breakpoint);
    }

    /**
     * Variante que recibe además el ancho vigente. Se usa cuando el layout
     * depende del ancho exacto (p. ej. envolver texto), no solo del breakpoint.
     */
    public interface WidthListener {
        void onWidth(Breakpoint breakpoint, int width);
    }

    private LayoutResponsive() {
        // Clase de utilidades: no instanciable.
    }

    /** Traduce un ancho en píxeles a su breakpoint correspondiente. */
    public static Breakpoint toBreakpoint(int width) {
        if (width < ANCHO_MOVIL_MAX) {
            return Breakpoint.MOVIL;
        }
        if (width < ANCHO_TABLET_MAX) {
            return Breakpoint.TABLET;
        }
        return Breakpoint.ESCRITORIO;
    }

    /** @return true si el breakpoint implica una sola columna (móvil). */
    public static boolean esColumnaUnica(Breakpoint bp) {
        return bp == Breakpoint.MOVIL;
    }

    /**
     * Registra un listener que se dispara cuando el ancho del componente cruza
     * un breakpoint. El listener se invoca una primera vez de forma diferida
     * (cuando el componente ya tiene tamaño) y luego en cada cambio.
     *
     * @param root     componente raíz a observar (normalmente el JPanel de la
     *                 vista).
     * @param listener callback a invocar con el breakpoint vigente.
     */
    public static void listen(final Component root, final BreakpointListener listener) {
        if (root == null || listener == null) {
            return;
        }

        // Estado compartido para evitar disparos duplicados.
        final Breakpoint[] last = new Breakpoint[]{null};
        final Runnable dispatch = () -> {
            int w = root.getWidth();
            if (w <= 0) {
                return; // aún sin tamaño; se reintentará
            }
            Breakpoint current = toBreakpoint(w);
            if (current != last[0]) {
                last[0] = current;
                listener.onBreakpointChanged(current);
            }
        };

        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                dispatch.run();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                dispatch.run();
            }
        });

        // Reintento diferido: cuando el ancestro cambia de tamaño (layouts que
        // reposicionan al padre) y como primer disparo fiable cuando la vista ya
        // tiene ancho real. Se reintenta unas cuantas veces por si el ancho aún
        // es 0 en el primer ciclo del EDT.
        SwingUtilities.invokeLater(new Runnable() {
            int intentos = 0;

            @Override
            public void run() {
                dispatch.run();
                if (root.getWidth() <= 0 && intentos++ < 20) {
                    SwingUtilities.invokeLater(this);
                }
            }
        });
    }

    /**
     * Como {@link #listen}, pero invoca el callback tambien cuando el ancho
     * cambia dentro del mismo breakpoint (util para envolver texto, que depende
     * del ancho exacto y no solo del punto de corte). Se deduplica por ancho.
     *
     * @param root     componente raiz a observar.
     * @param listener callback que recibe el breakpoint y el ancho vigentes.
     */
    public static void listenWidth(final Component root, final WidthListener listener) {
        if (root == null || listener == null) {
            return;
        }
        final int[] lastWidth = new int[]{-1};
        final Runnable dispatch = () -> {
            int w = root.getWidth();
            if (w <= 0 || w == lastWidth[0]) {
                return;
            }
            lastWidth[0] = w;
            listener.onWidth(toBreakpoint(w), w);
        };

        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                dispatch.run();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                dispatch.run();
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            int intentos = 0;

            @Override
            public void run() {
                dispatch.run();
                if (root.getWidth() <= 0 && intentos++ < 20) {
                    SwingUtilities.invokeLater(this);
                }
            }
        });
    }

    /**
     * Reorganiza un contenedor con layout GridLayout entre columnas y una sola
     * columna, reemplazando el layout vigente según el breakpoint.
     *
     * @param container    contenedor a reorganizar.
     * @param componentes  componentes en el orden en que deben mostrarse.
     * @param columnasWide número de columnas en tablet/escritorio.
     * @param gap          separación (px) entre componentes.
     * @param breakpoint   breakpoint actual.
     */
    public static void reflowColumnas(java.awt.Container container, List<Component> componentes,
            int columnasWide, int gap, Breakpoint breakpoint) {
        if (container == null || componentes == null) {
            return;
        }
        int columnas = esColumnaUnica(breakpoint) ? 1 : columnasWide;
        int filas = (int) Math.ceil(componentes.size() / (double) columnas);

        container.removeAll();
        container.setLayout(new java.awt.GridLayout(Math.max(1, filas), Math.max(1, columnas), gap, gap));
        for (Component c : componentes) {
            container.add(c);
        }
        container.revalidate();
        container.repaint();
    }

    /** Helper mutable para construir listas de componentes rápidamente. */
    public static List<Component> list(Component... componentes) {
        List<Component> out = new ArrayList<>();
        for (Component c : componentes) {
            out.add(c);
        }
        return out;
    }
}
