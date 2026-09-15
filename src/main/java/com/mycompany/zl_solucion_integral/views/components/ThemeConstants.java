package com.mycompany.zl_solucion_integral.views.components;

import java.awt.Color;
import java.awt.Font;

/**
 * Fuente única de verdad para el tema de la aplicación (principio DRY).
 *
 * Todos los colores de la UI se obtienen desde estas constantes estáticas.
 * Soporta dos paletas (oscura por defecto y clara) y permite cambiar entre
 * ellas en caliente mediante {@link #setDark(boolean)} sin reescribir
 * ninguna vista.
 *
 * Los colores "semánticos" (INPUT_BACKGROUND, CARD_BORDER, TABLE_ZEBRA, etc.)
 * centralizan los valores que antes estaban hardcodeados en las vistas, de
 * modo que el cambio de tema se ve correctamente en toda la aplicación.
 */
public class ThemeConstants {

    private static boolean isDark = true;

    // ---- Colores Base (re-asignables según el tema) ----
    public static Color BACKGROUND;
    public static Color SIDEBAR_BACKGROUND;
    public static Color CARD_BACKGROUND;

    // ---- Colores Neon (acentos) ----
    public static Color NEON_PURPLE;
    public static Color NEON_BLUE;
    public static Color NEON_CYAN;
    public static Color NEON_GREEN;
    public static Color NEON_RED;
    public static Color NEON_AMBER;

    // ---- Colores de Texto ----
    public static Color TEXT_PRIMARY;
    public static Color TEXT_SECONDARY;

    // ---- Colores semánticos (antes hardcodeados en las vistas) ----
    /** Fondo de los JTextField/JPasswordField. */
    public static Color INPUT_BACKGROUND;
    /** Borde de los campos de texto. */
    public static Color INPUT_BORDER;
    /** Filas alternas (zebra) de las tablas. */
    public static Color TABLE_ZEBRA;
    /** Borde de las tarjetas (MetricCard). */
    public static Color CARD_BORDER;
    /** Fondo al pasar el ratón por encima (hover). */
    public static Color HOVER_BACKGROUND;
    /** Fondo del ítem activo del sidebar. */
    public static Color ACTIVE_BACKGROUND;
    /** Panel de marca en la pantalla de login (se mantiene oscuro como acento). */
    public static Color BRAND_BACKGROUND;
    public static Color BRAND_TEXT_PRIMARY;
    public static Color BRAND_TEXT_SECONDARY;
    /** Líneas de la cuadrícula de las gráficas. */
    public static Color GRID_LINE;
    /** Fondo de selección en tablas (variantes con alfa por color de acento). */
    public static Color SELECTION_PURPLE;
    public static Color SELECTION_CYAN;
    public static Color SELECTION_GREEN;
    public static Color SELECTION_BLUE;

    // ---- Tamaños táctiles (accesibilidad/uso en pantallas pequeñas) ----
    /** Altura mínima recomendada para objetivos táctiles (botones, campos). */
    public static final int TOUCH_TARGET_MIN = 44;
    /** Altura mínima de las filas de tabla para lectura/uso cómodo. */
    public static final int TABLE_ROW_HEIGHT = 36;

    // ---- Fuentes (compartidas por ambos temas) ----
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    static {
        applyTheme(true);
    }

    /** @return true si el tema activo es el oscuro. */
    public static boolean isDark() {
        return isDark;
    }

    /** Alterna entre tema oscuro y claro. */
    public static void toggleTheme() {
        setDark(!isDark);
    }

    /**
     * Aplica el tema indicado, re-asignando todas las constantes de color.
     *
     * @param dark true para tema oscuro, false para tema claro.
     */
    public static void setDark(boolean dark) {
        isDark = dark;
        applyTheme(dark);
    }

    private static void applyTheme(boolean dark) {
        if (dark) {
            // ---- PALETA OSCURA (valor por defecto / actual) ----
            BACKGROUND = decode("#0B0E14");
            SIDEBAR_BACKGROUND = decode("#121620");
            CARD_BACKGROUND = decode("#1E293B");

            NEON_PURPLE = decode("#A855F7");
            NEON_BLUE = decode("#3B82F6");
            NEON_CYAN = decode("#06B6D4");
            NEON_GREEN = decode("#22C55E");
            NEON_RED = decode("#EF4444");
            NEON_AMBER = decode("#F59E0B");

            TEXT_PRIMARY = decode("#FFFFFF");
            TEXT_SECONDARY = decode("#94A3B8");

            INPUT_BACKGROUND = new Color(15, 23, 42);
            INPUT_BORDER = new Color(51, 65, 85);
            TABLE_ZEBRA = new Color(30, 41, 59, 150);
            CARD_BORDER = new Color(148, 163, 184, 45);
            HOVER_BACKGROUND = new Color(255, 255, 255, 10);
            ACTIVE_BACKGROUND = new Color(168, 85, 247, 30);
            BRAND_BACKGROUND = new Color(17, 24, 39);
            BRAND_TEXT_PRIMARY = decode("#FFFFFF");
            BRAND_TEXT_SECONDARY = decode("#94A3B8");
            GRID_LINE = new Color(148, 163, 184, 35);
            SELECTION_PURPLE = new Color(168, 85, 247, 70);
            SELECTION_CYAN = new Color(6, 182, 212, 70);
            SELECTION_GREEN = new Color(34, 197, 94, 70);
            SELECTION_BLUE = new Color(59, 130, 246, 70);
        } else {
            // ---- PALETA CLARA ----
            BACKGROUND = decode("#EEF2F7");
            SIDEBAR_BACKGROUND = decode("#FFFFFF");
            CARD_BACKGROUND = decode("#FFFFFF");

            NEON_PURPLE = decode("#7C3AED");
            NEON_BLUE = decode("#2563EB");
            NEON_CYAN = decode("#0891B2");
            NEON_GREEN = decode("#16A34A");
            NEON_RED = decode("#DC2626");
            NEON_AMBER = decode("#D97706");

            TEXT_PRIMARY = decode("#0F172A");
            TEXT_SECONDARY = decode("#475569");

            INPUT_BACKGROUND = decode("#FFFFFF");
            INPUT_BORDER = decode("#CBD5E1");
            TABLE_ZEBRA = decode("#F1F5F9");
            CARD_BORDER = new Color(148, 163, 184, 120);
            HOVER_BACKGROUND = new Color(226, 232, 240, 120);
            ACTIVE_BACKGROUND = new Color(168, 85, 247, 35);
            BRAND_BACKGROUND = new Color(17, 24, 39);
            BRAND_TEXT_PRIMARY = decode("#FFFFFF");
            BRAND_TEXT_SECONDARY = decode("#94A3B8");
            GRID_LINE = new Color(148, 163, 184, 70);
            SELECTION_PURPLE = new Color(124, 58, 237, 50);
            SELECTION_CYAN = new Color(8, 145, 178, 50);
            SELECTION_GREEN = new Color(22, 163, 74, 50);
            SELECTION_BLUE = new Color(37, 99, 235, 50);
        }
    }

    /** Devuelve el color con la transparencia indicada (0–255). */
    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    private static Color decode(String hex) {
        return Color.decode(hex);
    }
}
