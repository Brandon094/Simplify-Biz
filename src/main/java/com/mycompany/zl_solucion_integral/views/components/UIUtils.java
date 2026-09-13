package com.mycompany.zl_solucion_integral.views.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    /**
     * Crea un "label" multilínea que envuelve su texto automáticamente al
     * ancho disponible. Se implementa con un {@link JTextArea} transparente y
     * no editable, porque un {@code JLabel} nunca parte el texto y se corta con
     * puntos suspensivos en pantallas estrechas.
     *
     * @param texto a mostrar.
     * @param fuente a usar.
     * @param color  color del texto.
     * @return componente listo para usar en lugar de un JLabel de texto largo.
     */
    public static JTextArea createWrappingLabel(String texto, Font fuente, Color color) {
        return new WrappingLabel(texto, fuente, color);
    }

    /**
     * Etiqueta multilínea que ajusta su altura preferida al ancho disponible
     * usando HTML interno de Swing (estrategia determinista que no entra en
     * conflicto con los layout managers).
     *
     * <p>Se recalcula el ancho de wrap cada vez que la etiqueta cambia de
     * tama\u00f1o, de modo que el texto siempre envuelve sin recortarse.</p>
     */
    /**
     * Panel que se ajusta al ancho (y alto) del viewport cuando se usa como
     * vista de un {@link javax.swing.JScrollPane}. Necesario para que el estado
     * vacío no desborde horizontalmente y su texto pueda envolver.
     */
    public static class ScrollablePanel extends JPanel
            implements javax.swing.Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 64;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return true;
        }
    }

    public static class WrappingLabel extends JTextArea {
        private final String textoOriginal;

        public WrappingLabel(String texto, Font fuente, Color color) {
            super(texto);
            this.textoOriginal = texto == null ? "" : texto;
            setEditable(false);
            setFocusable(false);
            setOpaque(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setFont(fuente);
            setForeground(color);
            setBorder(null);
            setMargin(new Insets(0, 0, 0, 0));
            setAlignmentY(TOP_ALIGNMENT);
        }

        /**
         * Un JTextArea con lineWrap no calcula su alto para un ancho dado en
         * getPreferredSize (devuelve una sola linea). Lo calculamos contando
         * lineas con las metricas de la fuente, de modo que el layout reserve
         * el alto correcto y el texto nunca se recorte.
         */
        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            int anchoNatural = fm.stringWidth(textoOriginal)
                    + getInsets().left + getInsets().right;
            int anchoDisponible = getWidth() > 0 ? getWidth() : anchoNatural;
            int lineas = contarLineas(textoOriginal, Math.max(1,
                    anchoDisponible - getInsets().left - getInsets().right), fm);
            int alto = lineas * fm.getHeight();
            return new Dimension(anchoDisponible, alto);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(0, getFontMetrics(getFont()).getHeight());
        }

        /** Cuenta cuantas lineas ocupa el texto al ancho indicado. */
        private int contarLineas(String texto, int ancho, FontMetrics fm) {
            if (texto == null || texto.isEmpty()) {
                return 1;
            }
            int lineas = 1;
            int anchoLinea = 0;
            for (String palabra : texto.split(" ")) {
                int anchoPalabra = fm.stringWidth(palabra);
                int anchoEspacio = anchoLinea == 0 ? 0 : fm.stringWidth(" ");
                if (anchoLinea + anchoEspacio + anchoPalabra > ancho && anchoLinea > 0) {
                    lineas++;
                    anchoLinea = anchoPalabra;
                } else {
                    anchoLinea += anchoEspacio + anchoPalabra;
                }
            }
            return lineas;
        }
    }

    /**
     * Crea una fila de título compuesta por un icono SVG y un texto que
     * envuelve. Usa BorderLayout (icono al oeste, texto al centro) para que el
     * texto reciba el ancho restante y pueda envolver correctamente, algo que
     * FlowLayout no garantiza.
     *
     * @param iconPath ruta del icono SVG.
     * @param iconColor color del icono.
     * @param iconSize tamaño del icono en px (cuadrado).
     * @param texto del título (puede envolver).
     * @param fuente del título.
     * @param color del texto.
     * @return panel con la fila de título.
     */
    public static JPanel createWrappingTitleRow(String iconPath, Color iconColor, int iconSize,
                                                String texto, Font fuente, Color color) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        FlatSVGIcon icon = new FlatSVGIcon(iconPath, iconSize, iconSize);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, iconColor));
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setVerticalAlignment(SwingConstants.TOP);
        row.add(iconLabel, BorderLayout.WEST);

        JTextArea textLabel = createWrappingLabel(texto, fuente, color);
        row.add(textLabel, BorderLayout.CENTER);
        return row;
    }

    /** Fábrica de cabeceras de módulo: icono + título + microcopy de contexto (DRY). */
    public static JPanel createHeader(String iconPath, Color iconColor, String title, String subtitle) {
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);

        FlatSVGIcon icon = new FlatSVGIcon(iconPath, 24, 24);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, iconColor));

        JLabel lblTitle = new JLabel(title, icon, SwingConstants.LEFT);
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblTitle.setFont(ThemeConstants.FONT_TITLE);
        lblTitle.setIconTextGap(12);

        // El subtítulo usa un área de texto que envuelve por sí misma, así nunca
        // se corta con puntos suspensivos en pantallas estrechas.
        JTextArea lblSubtitle = createWrappingLabel(subtitle,
                ThemeConstants.FONT_SMALL, ThemeConstants.TEXT_SECONDARY);
        lblSubtitle.setBorder(BorderFactory.createEmptyBorder(4, 36, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        header.add(lblTitle, gbc);
        gbc.gridy = 1;
        header.add(lblSubtitle, gbc);

        return header;
    }

    /**
     * Ajusta el envoltura del texto de una cabecera creada con
     * {@link #createHeader}. En móvil el título y el subtítulo se envuelven en
     * varias líneas para no cortarse con puntos suspensivos.
     *
     * @param header  cabecera devuelta por {@code createHeader}.
     * @param ancho   ancho disponible (px) para el texto.
     * @param envolver true para envolver (móvil), false para una sola línea.
     */
    public static void ajustarHeaderResponsive(JPanel header, int ancho, boolean envolver) {
        if (header == null) {
            return;
        }
        Object tl = header.getClientProperty("header.titleLabel");
        Object tt = header.getClientProperty("header.titleText");
        Object sl = header.getClientProperty("header.subtitleLabel");
        Object st = header.getClientProperty("header.subtitleText");
        if (tl instanceof JLabel && tt instanceof String) {
            ajustarTextoResponsive((JLabel) tl, (String) tt, Math.max(120, ancho - 40), envolver);
        }
        if (sl instanceof JLabel && st instanceof String) {
            ajustarTextoResponsive((JLabel) sl, (String) st, Math.max(120, ancho - 60), envolver);
        }
    }

    /**
     * Prepara un componente de formulario (campo, combo, etc.) para que pueda
     * encogerse por debajo de su ancho preferido. Sin esto, los JComboBox y
     * algunos campos imponen un ancho mínimo (su texto más largo) que hace que
     * el formulario desborde y se corte en móvil.
     *
     * @param c componente a flexibilizar.
     * @return el mismo componente, para encadenar llamadas.
     */
    public static <T extends javax.swing.JComponent> T fluid(T c) {
        if (c != null) {
            c.setMinimumSize(new Dimension(0, ThemeConstants.TOUCH_TARGET_MIN));
            // Evita que una longitud de texto grande fije el preferredSize.
            if (c instanceof javax.swing.JComboBox && ((javax.swing.JComboBox<?>) c).isEditable()) {
                // Los editables ya se comportan bien; no hacemos nada extra.
            }
        }
        return c;
    }

    /**
     * Ajusta un JLabel para que su texto se envuelva (multilínea) cuando el
     * ancho disponible es estrecho (móvil) y vuelva a una sola línea en anchos
     * amplios. Usa HTML interno de Swing para el wrap de forma controlada.
     *
     * @param label          etiqueta a ajustar.
     * @param textoOriginal  texto plano original (sin HTML).
     * @param anchoDisponible ancho (px) a considerar; <=0 usa el ancho actual.
     * @param envolver       true para permitir varias líneas (móvil), false para una sola.
     */
    public static void ajustarTextoResponsive(JLabel label, String textoOriginal,
                                              int anchoDisponible, boolean envolver) {
        if (label == null || textoOriginal == null) {
            return;
        }
        if (envolver && anchoDisponible > 0) {
            label.setText("<html><body style='width:" + anchoDisponible + "px'>"
                    + escaparHtml(textoOriginal) + "</body></html>");
        } else {
            label.setText(textoOriginal);
        }
    }

    /** Escapa caracteres especiales de HTML para inserción segura en texto. */
    private static String escaparHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Fábrica de textos de ayuda (helper text) para campos clave. */
    public static JLabel createHelperLabel(String text) {
        JLabel helper = new JLabel(text);
        helper.setForeground(ThemeConstants.TEXT_SECONDARY);
        helper.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.ITALIC, 11f));
        return helper;
    }

    /**
     * Envuelve un campo de formulario con su texto de ayuda permanente
     * debajo (helper text). El contenedor se coloca en la misma fila del
     * GridBagLayout que ocupaba el campo, así que no altera la indexación.
     */
    public static JComponent createFieldWithHelper(JComponent field, String helperText) {
        JPanel wrap = new JPanel(new BorderLayout(0, 3));
        wrap.setOpaque(false);
        wrap.add(field, BorderLayout.CENTER);

        JLabel helper = new JLabel(helperText);
        helper.setForeground(ThemeConstants.TEXT_SECONDARY);
        helper.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.ITALIC, 11f));
        helper.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));

        wrap.add(helper, BorderLayout.SOUTH);
        return wrap;
    }

    /**
     * Fábrica de estados vacíos accionables: título conversacional, mensaje de
     * guía y, opcionalmente, un botón de llamado a la acción (CTA). Si
     * {@code ctaText} es null, no se muestra botón.
     */
    public static JPanel createEmptyState(String iconPath, Color iconColor, String title,
                                          String message, String ctaText, Runnable ctaAction) {
        // ScrollablePanel contenedor principal
        ScrollablePanel wrapper = new ScrollablePanel();
        wrapper.setLayout(new GridBagLayout());
        wrapper.setOpaque(false);

        // Panel de contenido interno centrado vertical y horizontalmente con ancho controlado
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        FlatSVGIcon icon = new FlatSVGIcon(iconPath, 36, 36);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, iconColor));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Fila 0: Icono centrado con espacio amplio
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 14, 0);
        content.add(iconLabel, gbc);

        // Fila 1: Título centrado fluido (ancho máximo 320px)
        JLabel lblTitle = new JLabel("<html><div style='text-align: center; width: 320px; line-height: 1.3;'>" + escaparHtml(title) + "</div></html>", SwingConstants.CENTER);
        lblTitle.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD, 15f));
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        content.add(lblTitle, gbc);

        // Fila 2: Mensaje secundario centrado (ancho máximo 320px)
        JLabel lblMessage = new JLabel("<html><div style='text-align: center; width: 320px; line-height: 1.4;'>" + escaparHtml(message) + "</div></html>", SwingConstants.CENTER);
        lblMessage.setFont(ThemeConstants.FONT_SMALL);
        lblMessage.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(lblMessage, gbc);

        // Fila 3: Botón CTA opcional centrado
        if (ctaText != null && ctaAction != null) {
            NeonButton btnCta = new NeonButton(ctaText);
            btnCta.setNeonColor(iconColor);
            btnCta.setIconTextGap(8);
            btnCta.setPreferredSize(new Dimension(200, 40));
            btnCta.addActionListener(e -> ctaAction.run());

            JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            btnWrap.setOpaque(false);
            btnWrap.add(btnCta);

            gbc.gridy = 3;
            gbc.insets = new Insets(18, 0, 0, 0);
            content.add(btnWrap, gbc);
        }

        // Agregar el bloque content al wrapper con centrado absoluto (center anchor)
        GridBagConstraints wGbc = new GridBagConstraints();
        wGbc.gridx = 0;
        wGbc.gridy = 0;
        wGbc.anchor = GridBagConstraints.CENTER;
        wGbc.weightx = 1.0;
        wGbc.weighty = 1.0;
        wGbc.insets = new Insets(20, 20, 20, 20);
        wrapper.add(content, wGbc);

        return wrapper;
    }

    /** Sobrecarga conveniente para crear estados vacíos directos con un FlatSVGIcon ya configurado. */
    public static JPanel createEmptyState(String title, String message, FlatSVGIcon icon) {
        ScrollablePanel wrapper = new ScrollablePanel();
        wrapper.setLayout(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        if (icon != null) {
            JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 14, 0);
            content.add(iconLabel, gbc);
        }

        JLabel lblTitle = new JLabel("<html><div style='text-align: center; width: 320px; line-height: 1.3;'>" + escaparHtml(title) + "</div></html>", SwingConstants.CENTER);
        lblTitle.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD, 15f));
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        content.add(lblTitle, gbc);

        JLabel lblMessage = new JLabel("<html><div style='text-align: center; width: 320px; line-height: 1.4;'>" + escaparHtml(message) + "</div></html>", SwingConstants.CENTER);
        lblMessage.setFont(ThemeConstants.FONT_SMALL);
        lblMessage.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(lblMessage, gbc);

        GridBagConstraints wGbc = new GridBagConstraints();
        wGbc.gridx = 0;
        wGbc.gridy = 0;
        wGbc.anchor = GridBagConstraints.CENTER;
        wGbc.weightx = 1.0;
        wGbc.weighty = 1.0;
        wGbc.insets = new Insets(20, 20, 20, 20);
        wrapper.add(content, wGbc);

        return wrapper;
    }

    public static void configureGlobalStyles() {
        // Configuraciones de FlatLaf para Diálogos
        UIManager.put("OptionPane.background", ThemeConstants.SIDEBAR_BACKGROUND);
        UIManager.put("Panel.background", ThemeConstants.SIDEBAR_BACKGROUND);
        UIManager.put("OptionPane.messageForeground", ThemeConstants.TEXT_PRIMARY);
        UIManager.put("OptionPane.messageFont", ThemeConstants.FONT_BODY);

        // Estilo de Botones en Diálogos
        UIManager.put("Button.arc", 15);
        UIManager.put("Button.background", ThemeConstants.CARD_BACKGROUND);
        UIManager.put("Button.foreground", ThemeConstants.TEXT_PRIMARY);
        UIManager.put("Button.hoverBackground", ThemeConstants.NEON_BLUE);
        UIManager.put("Button.border", BorderFactory.createLineBorder(ThemeConstants.NEON_BLUE, 1));

        // Campos de texto
        UIManager.put("TextField.background", ThemeConstants.INPUT_BACKGROUND);
        UIManager.put("TextField.foreground", ThemeConstants.TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", ThemeConstants.NEON_BLUE);
        UIManager.put("PasswordField.background", ThemeConstants.INPUT_BACKGROUND);
        UIManager.put("PasswordField.foreground", ThemeConstants.TEXT_PRIMARY);
        UIManager.put("TextArea.background", ThemeConstants.INPUT_BACKGROUND);
        UIManager.put("TextArea.foreground", ThemeConstants.TEXT_PRIMARY);

        // Tablas
        UIManager.put("Table.background", ThemeConstants.CARD_BACKGROUND);
        UIManager.put("Table.foreground", ThemeConstants.TEXT_SECONDARY);
        UIManager.put("TableHeader.background", ThemeConstants.SIDEBAR_BACKGROUND);
        UIManager.put("TableHeader.foreground", ThemeConstants.TEXT_SECONDARY);

        // Bordes de Diálogos y Acentos
        UIManager.put("OptionPane.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeConstants.NEON_PURPLE, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Títulos de ventanas de diálogo
        UIManager.put("TitlePane.background", ThemeConstants.BACKGROUND);
        UIManager.put("TitlePane.foreground", ThemeConstants.TEXT_PRIMARY);
    }

    /** Muestra un diálogo de error consistente con el tema. */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, UIMessages.TITULO_ERROR, JOptionPane.ERROR_MESSAGE);
    }

    /** Muestra un diálogo de advertencia consistente con el tema. */
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    /** Muestra un diálogo de éxito consistente con el tema. */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, UIMessages.TITULO_EXITO, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Muestra éxito o error según {@link com.mycompany.zl_solucion_integral.config.ResultadoOperacion}. */
    public static void showResultado(Component parent, com.mycompany.zl_solucion_integral.config.ResultadoOperacion resultado) {
        if (resultado == null) {
            return;
        }
        if (resultado.esExito()) {
            showSuccess(parent, resultado.getMensaje());
        } else {
            showError(parent, resultado.getMensaje());
        }
    }

    /** Muestra un diálogo de información consistente con el tema. */
    public static void showInfo(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private static final java.text.NumberFormat CURRENCY_FORMAT = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("es", "CO"));
    private static final java.text.SimpleDateFormat DISPLAY_DATE_FMT = new java.text.SimpleDateFormat("dd/MM/yyyy");
    private static final java.text.SimpleDateFormat ISO_DATE_FMT = new java.text.SimpleDateFormat("yyyy-MM-dd");

    /** Formatea un valor numérico a moneda contable ($ 15.000,00 o $ 15.000). */
    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    /**
     * Formatea cualquier objeto de fecha (Date, LocalDate, Timestamp o cadena 'yyyy-MM-dd')
     * al formato estándar unificado 'dd/MM/yyyy'.
     */
    public static String formatDate(Object dateVal) {
        if (dateVal == null) return "";
        if (dateVal instanceof java.util.Date) {
            return DISPLAY_DATE_FMT.format((java.util.Date) dateVal);
        }
        if (dateVal instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) dateVal).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        String str = dateVal.toString().trim();
        if (str.matches("^\\d{4}-\\d{2}-\\d{2}.*")) {
            try {
                java.util.Date d = ISO_DATE_FMT.parse(str.substring(0, 10));
                return DISPLAY_DATE_FMT.format(d);
            } catch (java.text.ParseException ignored) {}
        }
        return str;
    }

    /** Oculta visualmente la columna indicada por su nombre en el TableHeader. */
    public static void hideColumn(JTable table, String columnName) {
        if (table == null || table.getColumnModel() == null) return;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            javax.swing.table.TableColumn col = table.getColumnModel().getColumn(i);
            if (col.getHeaderValue() != null && columnName.equalsIgnoreCase(col.getHeaderValue().toString().trim())) {
                table.removeColumn(col);
                break;
            }
        }
    }

    /**
     * Aplica el sistema de diseño DRY a cualquier JTable de la aplicación:
     * - Oculta la columna "Id" visualmente si existe.
     * - Configura colores de zebra, selección, fuentes y bordes.
     * - Alinea y aplica formato por tipo de columna (Moneda a la derecha, datos numéricos/códigos/fechas/estados centrados, textos a la izquierda).
     */
    public static void applyTableStyling(JTable table) {
        if (table == null) return;

        // 1. Ocultar la columna Id visualmente si está presente
        hideColumn(table, "Id");

        table.setBackground(ThemeConstants.CARD_BACKGROUND);
        table.setForeground(ThemeConstants.TEXT_PRIMARY);
        table.setRowHeight(ThemeConstants.TABLE_ROW_HEIGHT);
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        table.setFont(ThemeConstants.FONT_SMALL);
        table.setSelectionBackground(new Color(168, 85, 247, 70));
        table.setSelectionForeground(ThemeConstants.TEXT_PRIMARY);

        // Encabezado
        javax.swing.table.JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
            header.setForeground(ThemeConstants.TEXT_SECONDARY);
            header.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD));
            header.setPreferredSize(new Dimension(0, 32));
            header.setReorderingAllowed(false);
        }

        // Renderizadores por tipo de columna
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            javax.swing.table.TableColumn col = table.getColumnModel().getColumn(i);
            if (col.getHeaderValue() == null) continue;
            String headerText = col.getHeaderValue().toString().trim();

            if (isCurrencyColumn(headerText)) {
                col.setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Object formatted = value;
                        if (value instanceof Number) {
                            formatted = formatCurrency(((Number) value).doubleValue());
                        } else if (value != null && !value.toString().isEmpty()) {
                            try {
                                double val = Double.parseDouble(value.toString().replace("$", "").replace(".", "").replace(",", ".").trim());
                                formatted = formatCurrency(val);
                            } catch (NumberFormatException ignored) {}
                        }
                        Component c = super.getTableCellRendererComponent(tbl, formatted, isSelected, hasFocus, row, column);
                        if (isSelected) {
                            c.setBackground(new Color(168, 85, 247, 40));
                        } else {
                            c.setBackground(row % 2 == 0 ? ThemeConstants.CARD_BACKGROUND : ThemeConstants.TABLE_ZEBRA);
                            c.setForeground(ThemeConstants.TEXT_PRIMARY);
                        }
                        setHorizontalAlignment(SwingConstants.RIGHT);
                        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                        return c;
                    }
                });
                col.setPreferredWidth(headerText.equalsIgnoreCase("Precio Total") || headerText.equalsIgnoreCase("Total") ? 120 : 100);
            } else if (isCenterColumn(headerText)) {
                col.setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Object cellValue = value;
                        if (headerText.equalsIgnoreCase("Fecha") && value != null) {
                            cellValue = formatDate(value);
                        }
                        Component c = super.getTableCellRendererComponent(tbl, cellValue, isSelected, hasFocus, row, column);
                        if (isSelected) {
                            c.setBackground(new Color(168, 85, 247, 40));
                        } else {
                            c.setBackground(row % 2 == 0 ? ThemeConstants.CARD_BACKGROUND : ThemeConstants.TABLE_ZEBRA);
                            if (headerText.equalsIgnoreCase("Pago confirmado") && value != null && value.toString().equalsIgnoreCase("deudor")) {
                                c.setForeground(new Color(245, 158, 11)); // Tono ámbar para deudores
                            } else {
                                c.setForeground(ThemeConstants.TEXT_SECONDARY);
                            }
                        }
                        setHorizontalAlignment(SwingConstants.CENTER);
                        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                        return c;
                    }
                });
                if (headerText.equalsIgnoreCase("Cantidad") || headerText.equalsIgnoreCase("Stock")) col.setPreferredWidth(75);
                else if (headerText.equalsIgnoreCase("Código") || headerText.equalsIgnoreCase("SKU")) col.setPreferredWidth(90);
                else if (headerText.equalsIgnoreCase("Fecha")) col.setPreferredWidth(100);
                else col.setPreferredWidth(110);
            } else {
                col.setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                        if (isSelected) {
                            c.setBackground(new Color(168, 85, 247, 40));
                        } else {
                            c.setBackground(row % 2 == 0 ? ThemeConstants.CARD_BACKGROUND : ThemeConstants.TABLE_ZEBRA);
                            c.setForeground(ThemeConstants.TEXT_SECONDARY);
                        }
                        setHorizontalAlignment(SwingConstants.LEFT);
                        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                        return c;
                    }
                });
                if (headerText.equalsIgnoreCase("Producto") || headerText.equalsIgnoreCase("Nombre")) col.setPreferredWidth(180);
                else if (headerText.equalsIgnoreCase("Cliente") || headerText.equalsIgnoreCase("Categoría")) col.setPreferredWidth(140);
                else if (headerText.equalsIgnoreCase("Vendedor")) col.setPreferredWidth(120);
                else col.setPreferredWidth(110);
            }
        }
    }

    private static boolean isCurrencyColumn(String header) {
        if (header == null) return false;
        String h = header.toLowerCase();
        return h.contains("precio") || h.equals("total") || h.contains("monto") || h.contains("valor");
    }

    private static boolean isCenterColumn(String header) {
        if (header == null) return false;
        String h = header.toLowerCase();
        return h.contains("cantidad") || h.contains("stock") || h.contains("código") || h.contains("codigo") || h.contains("sku")
                || h.contains("fecha") || h.contains("teléfono") || h.contains("telefono") || h.contains("cc") || h.contains("nit")
                || h.contains("método") || h.contains("metodo") || h.contains("pago") || h.contains("rol");
    }
}
