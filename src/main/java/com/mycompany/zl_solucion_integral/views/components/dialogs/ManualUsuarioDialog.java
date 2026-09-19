package com.mycompany.zl_solucion_integral.views.components.dialogs;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Visor e interactivo de Manual de Usuario integrado dentro de la aplicación.
 * Cuenta con paginador de temas permanente en la barra inferior (siempre visible sin necesidad de scroll)
 * para cambiar de capítulo de forma rápida e intuitiva.
 */
public class ManualUsuarioDialog extends JDialog {

    private static class ManualSection {
        final String title;
        final String htmlContent;
        final String iconPath;

        ManualSection(String title, String htmlContent, String iconPath) {
            this.title = title;
            this.htmlContent = htmlContent;
            this.iconPath = iconPath;
        }
    }

    private final List<ManualSection> sections = new ArrayList<>();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> topicList = new JList<>(listModel);
    private final JEditorPane contentPane = new JEditorPane();
    private final JTextField txtSearch = new JTextField();
    private final JLabel lblPaginatorInfo = new JLabel();
    private final NeonButton btnPrev = new NeonButton("◄ Tema Anterior");
    private final NeonButton btnNext = new NeonButton("Tema Siguiente ►");
    private final String userRole;
    private int currentSectionIndex = 0;
    
    // Control de Sidebar colapsable
    private JPanel pnlSidebarHost;
    private NeonButton btnToggleSidebar;
    private boolean sidebarVisible = false; // Oculto por defecto para cero fricción

    // Feedback dopaminérgico — progreso y hover
    private final Set<Integer> readSections = new HashSet<>();
    private int hoveredIndex = -1;
    private JLabel lblProgreso;
    private JProgressBar progressBar;

    public ManualUsuarioDialog(Window owner) {
        this(owner, com.mycompany.zl_solucion_integral.models.Sesion.getRolLogueado());
    }

    public ManualUsuarioDialog(Window owner, String role) {
        super(owner, "Manual de Operación " + ("1".equals(role) ? "(Administrador)" : "(Vendedor/Empleado)") + " — ERP+ Business", ModalityType.APPLICATION_MODAL);
        this.userRole = role != null ? role : com.mycompany.zl_solucion_integral.models.Sesion.getRolLogueado();

        setSize(1060, 700);
        setMinimumSize(new Dimension(860, 580));
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeConstants.BACKGROUND);

        initSectionsData();
        initUI();

        // Cargar primera sección (Introducción) inmediatamente al abrir
        if (!sections.isEmpty()) {
            topicList.setSelectedIndex(0);
            displaySection(sections.get(0).title);
        }
    }

    private void initUI() {
        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(18, 24, 14, 24));

        JLabel titleLabel = new JLabel("Centro de Ayuda & Manual de Usuario", createIcon("icons/manual.svg", ThemeConstants.NEON_PURPLE, 24, 24), SwingConstants.LEFT);
        titleLabel.setFont(ThemeConstants.FONT_TITLE);
        titleLabel.setForeground(ThemeConstants.TEXT_PRIMARY);
        titleLabel.setIconTextGap(12);

        JLabel subtitleLabel = new JLabel("Guía interactiva de operación, flujos y preguntas frecuentes de ERP+ Business.");
        subtitleLabel.setFont(ThemeConstants.FONT_SMALL);
        subtitleLabel.setForeground(ThemeConstants.TEXT_SECONDARY);

        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.add(titleLabel);
        titleStack.add(Box.createVerticalStrut(4));
        titleStack.add(subtitleLabel);

        // Botón Toggle para abrir/ocultar el índice de temas (sidebar)
        btnToggleSidebar = new NeonButton("Índice de Temas");
        btnToggleSidebar.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnToggleSidebar.setIcon(createIcon("icons/arrow-right.svg", ThemeConstants.NEON_PURPLE, 16, 16));
        btnToggleSidebar.setIconTextGap(8);
        btnToggleSidebar.setToolTipText("Mostrar u ocultar la lista completa de temas del manual");
        btnToggleSidebar.setPreferredSize(new Dimension(175, 38));
        btnToggleSidebar.addActionListener(e -> toggleSidebar());

        // Campo de búsqueda en tiempo real
        txtSearch.setPreferredSize(new Dimension(240, 38));
        txtSearch.setBackground(ThemeConstants.INPUT_BACKGROUND);
        txtSearch.setForeground(ThemeConstants.TEXT_PRIMARY);
        txtSearch.setCaretColor(ThemeConstants.NEON_PURPLE);
        txtSearch.setFont(ThemeConstants.FONT_BODY);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        txtSearch.setToolTipText("Buscar tema o palabra clave en el manual...");

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterTopics(); }
            @Override public void removeUpdate(DocumentEvent e) { filterTopics(); }
            @Override public void changedUpdate(DocumentEvent e) { filterTopics(); }
        });

        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchWrapper.setOpaque(false);
        searchWrapper.add(btnToggleSidebar);
        JLabel searchIconLabel = new JLabel(createIcon("icons/search.svg", ThemeConstants.NEON_PURPLE, 18, 18));
        searchWrapper.add(searchIconLabel);
        searchWrapper.add(txtSearch);

        headerPanel.add(titleStack, BorderLayout.CENTER);
        headerPanel.add(searchWrapper, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- BODY (SIDEBAR COLAPSABLE + VISOR HTML EN PANELES SEPARADOS) ---
        // Lista de Temas
        populateTopicList("");
        topicList.setBackground(ThemeConstants.CARD_BACKGROUND);
        topicList.setForeground(ThemeConstants.TEXT_PRIMARY);
        topicList.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD));
        topicList.setSelectionBackground(ThemeConstants.NEON_PURPLE);
        topicList.setSelectionForeground(Color.WHITE);
        // Altura dinámica calculada por el renderer multilínea
        topicList.setBorder(new EmptyBorder(6, 6, 6, 6));

        // Renderizador multilínea con feedback dopaminérgico
        topicList.setCellRenderer(new ListCellRenderer<String>() {
            @Override
            public Component getListCellRendererComponent(
                    JList<? extends String> list, String value,
                    int index, boolean isSelected, boolean cellHasFocus) {

                JPanel cell = new JPanel(new BorderLayout(10, 0));

                // --- Fondo: seleccionado > hover > normal ---
                Color bg;
                if (isSelected) {
                    bg = ThemeConstants.NEON_PURPLE;
                } else if (index == hoveredIndex) {
                    bg = ThemeConstants.HOVER_BACKGROUND;
                } else {
                    bg = ThemeConstants.CARD_BACKGROUND;
                }
                cell.setBackground(bg);

                // --- Barra de acento lateral púrpura en seleccionado ---
                if (isSelected) {
                    cell.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 3, 0, 0, ThemeConstants.NEON_PURPLE),
                            new EmptyBorder(8, 9, 8, 12)
                    ));
                } else {
                    cell.setBorder(new EmptyBorder(8, 12, 8, 12));
                }

                // --- Icono SVG del capítulo ---
                JLabel iconLabel = new JLabel();
                int sectionRealIndex = -1;
                if (value != null) {
                    for (int s = 0; s < sections.size(); s++) {
                        if (sections.get(s).title.equals(value)) {
                            sectionRealIndex = s;
                            Color iconColor = isSelected ? Color.WHITE : ThemeConstants.NEON_PURPLE;
                            iconLabel.setIcon(createIcon(sections.get(s).iconPath, iconColor, 18, 18));
                            break;
                        }
                    }
                }
                iconLabel.setVerticalAlignment(SwingConstants.TOP);
                cell.add(iconLabel, BorderLayout.WEST);

                // --- Texto multilínea con word-wrap ---
                JTextArea textArea = new JTextArea(value != null ? value : "");
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setOpaque(false);
                textArea.setEditable(false);
                textArea.setFocusable(false);
                textArea.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD));
                textArea.setForeground(isSelected ? Color.WHITE : ThemeConstants.TEXT_PRIMARY);
                textArea.setBorder(null);

                int availableWidth = 300 - 24 - 18 - 10;
                textArea.setSize(new Dimension(availableWidth, Short.MAX_VALUE));
                textArea.setPreferredSize(new Dimension(availableWidth,
                        textArea.getPreferredSize().height));
                cell.add(textArea, BorderLayout.CENTER);

                // --- Indicador de sección leída (color verde en el icono) ---
                if (sectionRealIndex >= 0 && readSections.contains(sectionRealIndex) && !isSelected) {
                    iconLabel.setIcon(createIcon(sections.get(sectionRealIndex).iconPath, ThemeConstants.NEON_GREEN, 18, 18));
                }

                return cell;
            }
        });

        topicList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && topicList.getSelectedIndex() != -1) {
                String selectedTitle = topicList.getSelectedValue();
                displaySection(selectedTitle);
            }
        });

        // Hover effect — feedback visual al pasar el mouse
        topicList.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int idx = topicList.locationToIndex(e.getPoint());
                if (idx != hoveredIndex) {
                    hoveredIndex = idx;
                    topicList.repaint();
                }
            }
        });
        topicList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredIndex = -1;
                topicList.repaint();
            }
        });

        JScrollPane leftScroll = new JScrollPane(topicList);
        leftScroll.setBorder(BorderFactory.createLineBorder(ThemeConstants.CARD_BORDER, 1));
        leftScroll.setPreferredSize(new Dimension(300, 0));
        leftScroll.getViewport().setBackground(ThemeConstants.CARD_BACKGROUND);

        // --- Header de progreso (SIEMPRE VISIBLE, fuera del sidebar) ---
        JPanel progressPanel = new JPanel(new BorderLayout(8, 4));
        progressPanel.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        progressPanel.setBorder(new EmptyBorder(8, 24, 8, 24));

        lblProgreso = new JLabel("Explorado: 0 de 0");
        lblProgreso.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD, 11f));
        lblProgreso.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblProgreso.setIcon(createIcon("icons/bolt.svg", ThemeConstants.NEON_PURPLE, 14, 14));
        lblProgreso.setIconTextGap(6);
        progressPanel.add(lblProgreso, BorderLayout.CENTER);

        progressBar = new JProgressBar(0, Math.max(sections.size(), 1));
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(0, 5));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(ThemeConstants.CARD_BORDER);
        progressBar.setForeground(ThemeConstants.NEON_PURPLE);
        progressPanel.add(progressBar, BorderLayout.SOUTH);

        pnlSidebarHost = new JPanel(new BorderLayout());
        pnlSidebarHost.setOpaque(false);
        pnlSidebarHost.add(leftScroll, BorderLayout.CENTER);
        pnlSidebarHost.setVisible(false); // Oculto por defecto para cero fricción

        // Derecha: Visor HTML
        contentPane.setContentType("text/html");
        contentPane.setEditable(false);
        contentPane.setBackground(ThemeConstants.CARD_BACKGROUND);
        contentPane.setMargin(new Insets(18, 22, 18, 22));

        JScrollPane rightScroll = new JScrollPane(contentPane);
        rightScroll.setBorder(BorderFactory.createLineBorder(ThemeConstants.CARD_BORDER, 1));
        rightScroll.getViewport().setBackground(ThemeConstants.CARD_BACKGROUND);
        rightScroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel bodyPanel = new JPanel(new BorderLayout(14, 0));
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(new EmptyBorder(0, 24, 0, 24));
        bodyPanel.add(pnlSidebarHost, BorderLayout.WEST);
        bodyPanel.add(rightScroll, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 6));
        centerPanel.setOpaque(false);
        centerPanel.add(progressPanel, BorderLayout.NORTH);
        centerPanel.add(bodyPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- FOOTER CON PAGINADOR PERMANENTE EN LA PARTE INFERIOR NEGRA ---
        JPanel footerPanel = new JPanel(new BorderLayout(15, 0));
        footerPanel.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConstants.CARD_BORDER),
                new EmptyBorder(12, 24, 16, 24)
        ));

        lblPaginatorInfo.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD));
        lblPaginatorInfo.setForeground(ThemeConstants.TEXT_PRIMARY);

        JPanel pnlLeftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlLeftInfo.setOpaque(false);
        pnlLeftInfo.add(lblPaginatorInfo);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnPrev.setNeonColor(ThemeConstants.NEON_BLUE);
        btnPrev.setPreferredSize(new Dimension(150, 38));
        btnPrev.addActionListener(e -> navigateRelative(-1));

        btnNext.setNeonColor(ThemeConstants.NEON_PURPLE);
        btnNext.setPreferredSize(new Dimension(150, 38));
        btnNext.addActionListener(e -> navigateRelative(1));

        NeonButton btnClose = new NeonButton("Cerrar");
        btnClose.setNeonColor(ThemeConstants.TEXT_SECONDARY);
        btnClose.setIcon(createIcon("icons/xmark.svg", ThemeConstants.TEXT_SECONDARY, 16, 16));
        btnClose.setIconTextGap(6);
        btnClose.setPreferredSize(new Dimension(110, 38));
        btnClose.addActionListener(e -> dispose());

        pnlButtons.add(btnPrev);
        pnlButtons.add(btnNext);
        pnlButtons.add(btnClose);

        footerPanel.add(pnlLeftInfo, BorderLayout.CENTER);
        footerPanel.add(pnlButtons, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        pnlSidebarHost.setVisible(sidebarVisible);
        String iconPath = sidebarVisible ? "icons/arrow-left.svg" : "icons/arrow-right.svg";
        btnToggleSidebar.setIcon(createIcon(iconPath, ThemeConstants.NEON_PURPLE, 16, 16));
        btnToggleSidebar.setText(sidebarVisible ? "Ocultar Índice" : "Índice de Temas");
        revalidate();
        repaint();
    }

    private void filterTopics() {
        String query = txtSearch.getText().trim().toLowerCase();
        populateTopicList(query);
    }

    private void populateTopicList(String filter) {
        listModel.clear();
        for (ManualSection sec : sections) {
            if (filter.isEmpty() || sec.title.toLowerCase().contains(filter) || sec.htmlContent.toLowerCase().contains(filter)) {
                listModel.addElement(sec.title);
            }
        }
        if (listModel.getSize() > 0 && topicList.getSelectedIndex() == -1) {
            topicList.setSelectedIndex(0);
        }
    }

    private void displaySection(String title) {
        for (int i = 0; i < sections.size(); i++) {
            ManualSection sec = sections.get(i);
            if (sec.title.equals(title)) {
                currentSectionIndex = i;
                contentPane.setText(buildHtmlDocument(sec));
                contentPane.setCaretPosition(0);
                contentPane.revalidate();
                contentPane.repaint();

                // Feedback dopaminérgico: marcar como leída
                readSections.add(i);
                actualizarProgreso();
                topicList.repaint();

                actualizarPaginador(i);
                break;
            }
        }
    }

    private void actualizarPaginador(int index) {
        int total = sections.size();
        lblPaginatorInfo.setText("Capítulo " + (index + 1) + " de " + total + " — " + sections.get(index).title);

        boolean hasPrev = index > 0;
        boolean hasNext = index < total - 1;

        btnPrev.setEnabled(hasPrev);
        btnNext.setEnabled(hasNext);

        if (hasPrev) {
            btnPrev.setToolTipText("Ir al capítulo anterior: " + sections.get(index - 1).title);
        } else {
            btnPrev.setToolTipText("Estás en el primer capítulo.");
        }

        if (hasNext) {
            btnNext.setToolTipText("Ir al capítulo siguiente: " + sections.get(index + 1).title);
        } else {
            btnNext.setToolTipText("Estás en el último capítulo.");
        }
    }

    private void navigateRelative(int delta) {
        int target = currentSectionIndex + delta;
        if (target >= 0 && target < sections.size()) {
            String title = sections.get(target).title;
            topicList.setSelectedValue(title, true);
        }
    }

    private void actualizarProgreso() {
        int total = sections.size();
        int leidos = readSections.size();
        lblProgreso.setText("Explorado: " + leidos + " de " + total);
        progressBar.setMaximum(total);
        progressBar.setValue(leidos);
    }

    private String buildHtmlDocument(ManualSection section) {
        boolean dark = ThemeConstants.isDark();
        String bgHex = dark ? "#1E293B" : "#FFFFFF";
        String textHex = dark ? "#F8FAFC" : "#0F172A";
        String mutedHex = dark ? "#94A3B8" : "#64748B";
        String accentHex = dark ? "#A855F7" : "#7C3AED";
        String cardBgHex = dark ? "#0F172A" : "#F1F5F9";
        String borderHex = dark ? "#334155" : "#CBD5E1";
        
        String tipBgHex = dark ? "#0F172A" : "#F8FAFC";
        String tipBorderHex = dark ? "#22C55E" : "#16A34A";
        
        String noteBgHex = dark ? "#0F172A" : "#F8FAFC";
        String noteBorderHex = dark ? "#A855F7" : "#7C3AED";

        String infoBorderHex = dark ? "#06B6D4" : "#0891B2";

        return "<html><head><style>" +
                "body { font-family: 'Segoe UI', sans-serif; background-color: " + bgHex + "; color: " + textHex + "; margin: 18px; font-size: 13px; line-height: 1.6; }" +
                "h1 { color: " + accentHex + "; font-size: 20px; border-bottom: 2px solid " + accentHex + "; padding-bottom: 6px; margin-top: 0; }" +
                "h2 { color: " + textHex + "; font-size: 15px; margin-top: 18px; border-left: 3px solid " + accentHex + "; padding-left: 8px; }" +
                "p { margin: 8px 0; }" +
                "code { background-color: " + cardBgHex + "; color: " + accentHex + "; padding: 2px 6px; border-radius: 4px; font-family: monospace; border: 1px solid " + borderHex + "; }" +
                "ul, ol { margin: 6px 0; padding-left: 22px; }" +
                "li { margin-bottom: 6px; }" +
                ".tip { background-color: " + tipBgHex + "; border-left: 4px solid " + tipBorderHex + "; padding: 12px 16px; margin: 14px 0; border-radius: 6px; font-size: 13px; color: " + textHex + "; border: 1px solid " + borderHex + "; border-left-width: 4px; }" +
                ".note { background-color: " + noteBgHex + "; border-left: 4px solid " + noteBorderHex + "; padding: 12px 16px; margin: 14px 0; border-radius: 6px; font-size: 13px; color: " + textHex + "; border: 1px solid " + borderHex + "; border-left-width: 4px; }" +
                ".info { background-color: " + tipBgHex + "; border-left: 4px solid " + infoBorderHex + "; padding: 12px 16px; margin: 14px 0; border-radius: 6px; font-size: 13px; color: " + textHex + "; border: 1px solid " + borderHex + "; border-left-width: 4px; }" +
                ".tag-tip { color: " + tipBorderHex + "; font-weight: bold; font-size: 13px; margin-right: 4px; }" +
                ".tag-note { color: " + noteBorderHex + "; font-weight: bold; font-size: 13px; margin-right: 4px; }" +
                ".tag-info { color: " + infoBorderHex + "; font-weight: bold; font-size: 13px; margin-right: 4px; }" +
                "table { width: 100%; border-collapse: collapse; margin: 14px 0; }" +
                "th { background-color: " + cardBgHex + "; color: " + accentHex + "; padding: 8px; text-align: left; border: 1px solid " + borderHex + "; }" +
                "td { padding: 8px; border: 1px solid " + borderHex + "; }" +
                "</style></head><body>" +
                "<h1>" + section.title + "</h1>" +
                cleanHtmlIcons(section.htmlContent) +
                "</body></html>";
    }

    private String cleanHtmlIcons(String html) {
        html = html.replace("<span class='tag-tip'>✔ ", "<span class='tag-tip'>");
        html = html.replace("<span class='tag-note'>🛡 ", "<span class='tag-note'>");
        html = html.replace("<span class='tag-note'>🔒 ", "<span class='tag-note'>");
        html = html.replace("<span class='tag-note'>📌 ", "<span class='tag-note'>");
        html = html.replace("<span class='tag-note'>📦 ", "<span class='tag-note'>");
        html = html.replace("<span class='tag-note'>📊 ", "<span class='tag-note'>");
        html = html.replace("<span class='tag-info'>💡 ", "<span class='tag-info'>");
        html = html.replace("<span class='tag-info'>⚡ ", "<span class='tag-info'>");
        html = html.replace("<span class='tag-tip'>🚀 ", "<span class='tag-tip'>");
        html = html.replace("<span class='tag-tip'>⭐ ", "<span class='tag-tip'>");
        html = html.replace("<span class='tag-tip'>🤝 ", "<span class='tag-tip'>");
        html = html.replace("<span class='tag-tip'>🛡 ", "<span class='tag-tip'>");

        return html;
    }

    private FlatSVGIcon createIcon(String path, Color color, int width, int height) {
        FlatSVGIcon icon = new FlatSVGIcon(path, width, height);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, color));
        return icon;
    }

    private void initSectionsData() {
        boolean esAdmin = "1".equals(userRole);

        sections.add(new ManualSection(
                "¡Tu Máquina de Ventas y Ganancias RÁPIDAS!",
                "<p>¡Felicitaciones! Has tomado la decisión de transformar tu negocio en una verdadera <b>máquina comercial de altas ganancias</b> con ERP+ Business (v2.1.0).</p>" +
                "<p>Dile adiós a los desvelos haciendo cuentas a mano, a las dudas sobre si estás ganando dinero y al temor de mercancía perdida. Esta herramienta fue diseñada pensando en la psicología del comerciante exitoso: te ahorra tiempo, protege cada billete que entra a tu local y te regala la paz mental de ver tu <b>Ganancia Limpia y Real</b> al instante.</p>" +
                "<div class='tip'><span class='tag-tip'>✔ CONSEJO DE ÉXITO:</span> <b>Sensación de Control Total —</b> Imagina cerrar tu jornada con la tranquilidad absoluta de saber exactamente cuánto vendiste, cuánto ganaste de utilidad directa y qué productos le encantan a tus clientes. ¡Prepárate para llevar tu negocio al siguiente nivel!</div>" +
                "<div class='note'><span class='tag-note'>🛡 SEGURIDAD DE DATOS:</span> <b>Protección Total —</b> Tu información no viaja a servidores externos desconocidos; se almacena de forma blindada en tu propio computador.</div>",
                "icons/manual.svg"
        ));

        sections.add(new ManualSection(
                "Despega tu Negocio en 4 Pasos Sencillos",
                "<p>Descubre la emoción de ver tu negocio funcionando solo, de manera rápida y sin complicaciones:</p>" +
                "<h2>1. Llena tus Estantes (Inventario & Abastecimiento):</h2>" +
                "<p>Ingresa la mercancía que vendes en tu negocio. En pocos segundos tendrás cada artículo registrado con su precio de costo y su precio de venta al público.</p>" +
                "<div class='info'><span class='tag-info'>💡 REGLA DE ORO:</span> <b>¿Inventario o Abastecimiento? —</b> Usa el módulo de <b>Inventario</b> para registrar productos que ya tienes en tu almacén o mercancía propia sin factura. Usa el módulo de <b>Abastecimiento</b> cuando ingreses compras respaldadas por una factura o remisión de tu proveedor.</div>" +
                "<h2>2. Pon a Tu Equipo a Vender con Confianza:</h2>" +
                "<p>Crea usuarios para tus colaboradores o cajeros con guía paso a paso y etiquetas de ayuda intuitivas. Ellos podrán cobrar a toda velocidad a tus clientes, mientras tus secretos de costos y tus ganancias privadas se mantienen 100% protegidos bajo tu control exclusivo.</p>" +
                "<h2>3. Vive la Magia del Cobro Ágil (Punto de Venta):</h2>" +
                "<p>Atiende a tus clientes en segundos. Cobra en Efectivo, Nequi, Bancolombia o Fiado. Tu cliente quedará descrestado con la rapidez de tu atención, el autocompletado flotante de clientes por cédula o nombre y el descuento automático de mercancía en bodega.</p>" +
                "<h2>4. Siente la Satisfacción de tus Resultados:</h2>" +
                "<p>Mira en tus pantallas ejecutivas cómo crecen tus ganancias en tiempo real con cada venta que realiza tu equipo, con vistas comparativas anuales y formato compacto de moneda ($100K, $5.4M).</p>",
                "icons/bolt.svg"
        ));

        sections.add(new ManualSection(
                "Tu Caja Fuerte Comercial Blindada",
                "<p>Tu información comercial y tu dinero son sagrados. Por eso, el sistema protege tu negocio como una caja fuerte:</p>" +
                "<h2>Comodidad y Tranquilidad Diario:</h2>" +
                "<ul>" +
                "<li><b>Ingreso Flexible Simplificado:</b> Puedes iniciar sesión ingresando tu <b>primer nombre</b> (ejemplo: <i>'Brandon'</i> si te llamaste Brandon Daza), tu <b>correo electrónico</b> o tu nombre completo registrado. ¡Cero complicaciones!</li>" +
                "<li><b>Entrada Rápida 'Recordarme':</b> Inicia tu jornada laboral de inmediato sin tener que escribir tus datos una y otra vez. Tu tiempo vale oro.</li>" +
                "<li><b>Recuperación Inteligente de Clave:</b> Si un día olvidas tu clave, no te preocupes ni pierdas la calma. Con solo escribir tu usuario y teléfono recuperas el acceso en segundos.</li>" +
                "<li><b>Cierre de Sesión Seguro al Terminar:</b> Al finalizar el día, cierra tu sesión con un solo clic para asegurar que nadie sin autorización pueda curiosear tus ganancias ni tus datos confidenciales.</li>" +
                "</ul>" +
                "<div class='note'><span class='tag-note'>🔒 PRIVACIDAD VIP:</span> <b>Control de Acceso —</b> Ningún empleado con perfil de Vendedor podrá acceder a este módulo ni modificar usuarios administradores.</div>",
                "icons/shield-heart.svg"
        ));

        if (esAdmin) {
            sections.add(new ManualSection(
                    "Tu Centro de Mando y Ganancias al Instante",
                    "<p>Imagina tener un consultor de negocios a tu lado 24/7 que te dice exactamente cómo va tu dinero sin que tengas que hacer una sola suma:</p>" +
                    "<h2>Tus Indicadores de Éxito Comercial (KPIs):</h2>" +
                    "<ul>" +
                    "<li><b>Ventas Totales (V):</b> Suma bruta facturada en el periodo seleccionado (ej: <b>$5.4M</b> o <b>$120K</b>).<br/><i>Fórmula: V = ∑ (Ventas Confirmadas)</i></li>" +
                    "<li><b>Ganancia Limpia y Real (P):</b> La utilidad neta que te queda en el bolsillo deduciendo lo que costó la mercancía (COGS).<br/><i>Fórmula: P = V - COGS, donde COGS = ∑ (Precio Costo Unitario × Cantidad Vendida)</i></li>" +
                    "<li><b>Margen de Ganancia (M):</b> Porcentaje de rentabilidad neta comercial.<br/><i>Fórmula: M = (Ganancia Limpia / Ventas Totales) × 100</i></li>" +
                    "<li><b>Porcentaje de Crecimiento Interperiodo (Δ%):</b> Comparativa de ventas contra el periodo inmediatamente anterior equivalente.<br/><i>Fórmula: Δ% = ((Ventas Actuales - Ventas Anteriores) / Ventas Anteriores) × 100</i></li>" +
                    "<li><b>Dinero Invertido en Bodega:</b> Valoración total de existencias en estantes a precio de costo.<br/><i>Fórmula: Inversión = ∑ (Precio Costo × Stock Disponible)</i></li>" +
                    "<li><b>Cuentas por Cobrar (Cartera):</b> Acumulado de saldo pendiente de cobro en ventas a crédito.<br/><i>Fórmula: Cartera = ∑ (Ventas Crédito - Abonos Recibidos)</i></li>" +
                    "</ul>" +
                    "<div class='info'><span class='tag-info'>⚡ INTELIGENCIA FINANCIERA:</span> <b>Poder Comercial —</b> Observa en gráficos dinámicos qué meses y a qué horas vendes más para aplicar promociones irresistibles y multiplicar tus ingresos.</div>",
                    "icons/dashboard.svg"
            ));
        }

        sections.add(new ManualSection(
                "Mercancía Organizada, Negocio Multiplicado (Inventario)",
                "<p>Evita el dolor de perder mercancía por desorden o extravíos. Utiliza <b>Inventario</b> para cargar productos existentes en tu bodega que no cuentan con factura de proveedor o para ajustar existencias manualmente:</p>" +
                "<h2>Crear un Producto de Impacto:</h2>" +
                "<ol>" +
                "<li>Escribe el <b>Nombre Atractivo</b> y el código SKU único del artículo.</li>" +
                "<li>Asígnalo a su <b>Familia o Categoría</b> para que lo encuentres al segundo durante la venta.</li>" +
                "<li>Define tu <b>Precio de Venta</b>, tu <b>Precio de Costo</b> y la cantidad que tienes en tu vitrina o bodega.</li>" +
                "<li>Guarda y listo: tu artículo queda listo para venderse de inmediato.</li>" +
                "</ol>" +
                "<div class='tip'><span class='tag-tip'>✔ CERO ERRORES:</span> <b>Suma Automática —</b> Si vuelves a registrar un producto que ya tenías, el sistema no crea duplicados molestos; simplemente suma las unidades nuevas a tu abundancia de stock.</div>" +
                "<div class='note'><span class='tag-note'>📌 MARGEN EXACTO:</span> <b>Sugerencia de Precios —</b> Define siempre tu precio de costo real para que el cálculo del margen de utilidad sea 100% exacto en tus reportes.</div>",
                "icons/products.svg"
        ));

        sections.add(new ManualSection(
                "Carga Masiva de Productos en Segundos",
                "<p>¿Tu proveedor te entregó una lista enorme de 500 o 1,000 productos? ¡No gastes días ni semanas digitando uno por uno!</p>" +
                "<h2>La Magia de la Carga Automática:</h2>" +
                "<ol>" +
                "<li><b>Descarga el Formato Sencillo:</b> Obtén una plantilla limpia lista para llenar.</li>" +
                "<li><b>Abre el Asistente Inteligente:</b> Elige tu archivo de Excel con un solo clic.</li>" +
                "<li><b>Conecta los Datos de Forma Visual:</b> Indica de manera súper fácil cuál columna es el nombre, cuál es el precio y cuál es la cantidad.</li>" +
                "<li><b>Confirma y Disfruta:</b> En solo 5 segundos verás cientos de productos cargados en tu pantalla, listos para generar ventas.</li>" +
                "</ol>" +
                "<div class='tip'><span class='tag-tip'>🚀 AHORRO DE TIEMPO:</span> <b>Cero Trabajo Repetido —</b> Si algunos artículos ya existían en tu tienda, el sistema actualiza sus precios y suma el stock de forma automática.</div>",
                "icons/excel.svg"
        ));

        sections.add(new ManualSection(
                "Ventas Ultrarrápidas que Enamoran a tus Clientes",
                "<p>Ofrécele a tus clientes una experiencia de compra rápida, moderna y sin filas molestas que los haga regresar siempre a tu negocio:</p>" +
                "<h2>El Arte del Cobro Ágil en 3 Pasos:</h2>" +
                "<ol>" +
                "<li><b>Búsqueda y Autocompletado de Clientes Inteligente:</b> En el selector de cliente, escribe los primeros dígitos de la Cédula/NIT o las primeras letras del Nombre. Aparecerá un menú desplegable flotante con las coincidencia exactas de tus clientes registrados. Al seleccionar uno, se cargará su cédula, nombre, teléfono y correo automáticamente.</li>" +
                "<li><b>Encuentra el Producto al Instante:</b> Escribe el código SKU o el nombre del artículo. El producto salta de inmediato al carrito de compras.</li>" +
                "<li><b>Adapta la Venta a la Medida:</b> Cambia cantidades o aplica promociones especiales con botones amplios y cómodos diseñados para vender a toda prisa.</li>" +
                "<li><b>Cierra el Cobro y Recibe el Dinero:</b>" +
                "<ul>" +
                "<li><b>Efectivo (Calculadora de Vueltas Exactas):</b> Escribe en el campo <b>'Paga con ($)'</b> el billete que te entrega tu cliente. El sistema te dice de inmediato las <b>Vueltas Exactas</b> en verde neón (o cuánto dinero falta), eliminando errores de cálculo manual. Entrega el cambio y despacha al cliente en 2 segundos.</li>" +
                "<li><b>Transferencia Digital (Nequi / Bancolombia):</b> Registra los datos del pago digital con total nitidez y sin dudas de dinero en el aire.</li>" +
                "<li><b>Crédito Comercial (Fiado de Confianza):</b> Ofrécele crédito a tus clientes VIP. La cuenta se guarda sola en su expediente sin necesidad de cuadernos.</li>" +
                "</ul>" +
                "</li>" +
                "</ol>" +
                "<h2>Atajos de Teclado para Cobro Exprés:</h2>" +
                "<table>" +
                "<tr><th>Tecla / Acción</th><th>Función</th></tr>" +
                "<tr><td><b>ENTER</b> en Búsqueda</td><td>Agrega el producto encontrado de inmediato al carrito.</td></tr>" +
                "<tr><td><b>TAB</b> / <b>SHIFT + TAB</b></td><td>Salta velozmente entre campos del formulario sin soltar el teclado.</td></tr>" +
                "<tr><td>Digitar en <b>Paga con ($)</b></td><td>Calcula automáticamente las vueltas en tiempo real.</td></tr>" +
                "</table>" +
                "<div class='tip'><span class='tag-tip'>💡 NEUROVENTAS:</span> <b>Recomendación de Venta Cruzada —</b> Aprovecha la velocidad del cobro para sugerir un producto complementario antes de cerrar la compra.</div>",
                "icons/cart-shopping.svg"
        ));

        sections.add(new ManualSection(
                "Recupera tus Cuentas por Cobrar sin Discusiones",
                "<p>Nunca vuelvas a perder dinero por cuentas anotadas en papeles sueltos que se traspapelan o se olvidan:</p>" +
                "<h2>Cobra de Forma Clara y Profesional:</h2>" +
                "<ol>" +
                "<li>Busca a tu cliente en la lista ordenada de <b>Cuentas por Cobrar</b>.</li>" +
                "<li>Presiona <b>Registrar Abono</b> e ingresa la cantidad de dinero que te está entregando.</li>" +
                "<li>Mira cómo el saldo pendiente se reduce en tiempo real y entrégale un comprobante elegante de pago a tu cliente.</li>" +
                "<li>Cuando termine de pagar la última cuota, la deuda se marca automáticamente como <b>PAGADA Y COMPLETADA</b>.</li>" +
                "</ol>" +
                "<div class='tip'><span class='tag-tip'>✔ COBRO AMIGABLE:</span> <b>Transparencia y Paz Mental —</b> Revisa en cualquier momento el historial exacto de qué día, a qué hora y con qué encargado se hizo cada abono de dinero.</div>",
                "icons/wallet.svg"
        ));

        sections.add(new ManualSection(
                "Abastecimiento Inteligente y Alianzas con Proveedores",
                "<p>Mantén tu local siempre lleno de los productos que más le gustan a tus clientes. Utiliza <b>Abastecimiento</b> cada vez que recibas mercancía respaldada por una factura o remisión de compra de tu proveedor:</p>" +
                "<h2>Entrada Inmediata de Mercancía con Factura:</h2>" +
                "<ol>" +
                "<li>Haz clic en <b>Registrar Entrada de Abastecimiento</b>.</li>" +
                "<li>Elige o agrega a tu <b>Proveedor</b> de confianza e ingresa el número de tu factura de compra.</li>" +
                "<li>Escribe el nombre o código de los productos que recibiste. <b>¿Es un producto completamente nuevo?</b> Digita su nombre y precio de costo directamente en la lista: el sistema lo registrará automáticamente en tu catálogo sin abrir ventanas extras.</li>" +
                "<li>Confirma la recepción: las existencias en tu bodega aumentarán solas, se guardará el registro de la factura del proveedor y el costo de tu mercancía se actualizará para cuidar tus márgenes de ganancia.</li>" +
                "</ol>" +
                "<div class='note'><span class='tag-note'>📦 CONTROL DE BODEGA:</span> <b>Tip de Inventario —</b> Registra siempre tu abastecimiento antes de poner la mercancía a exhibición para mantener el control exacto de stock.</div>",
                "icons/suppliers.svg"
        ));

        if (esAdmin) {
            sections.add(new ManualSection(
                    "Fideliza Clientes y Protege la Privacidad de tu Negocio",
                    "<p>Conoce los gustos de tus mejores compradores y delega la atención al público con total seguridad:</p>" +
                    "<h2>Conoce a tus Clientes Estrella:</h2>" +
                    "<ul>" +
                    "<li>Consulta el historial de compras de cualquier cliente para saber cuáles son sus productos favoritos, cuánto te ha comprado en total y con qué frecuencia te visita. ¡Premia su lealtad y haz que vuelva siempre!</li>" +
                    "</ul>" +
                    "<h2>Delegación Segura para tus Colaboradores:</h2>" +
                    "<ul>" +
                    "<li>Crea accesos individuales para tus empleados con <b>Perfil Vendedor</b>. El nuevo formulario cuenta con etiquetas de guía visuales para asegurar un registro sin errores. Ellos tendrán todas las facilidades para cobrar rápido a los clientes, pero tus ganancias netas, costos de compra y balances confidenciales permanecerán totalmente bloqueados bajo tu clave de Administrador.</li>" +
                    "</ul>" +
                    "<div class='tip'><span class='tag-tip'>⭐ CLIENTES VIP:</span> <b>Fidelización Efectiva —</b> Sorprende a tus clientes frecuentes ofreciéndoles pequeños descuentos en sus cumpleaños consultando su fecha de registro.</div>",
                    "icons/clients.svg"
            ));

            sections.add(new ManualSection(
                    "Toma Decisiones Estratégicas e Imprime tus Logros",
                    "<p>Descubre el poder de tomar decisiones basadas en números reales sobre la salud de tu empresa:</p>" +
                    "<h2>Tus Informes Claros y Listos para Usar:</h2>" +
                    "<ul>" +
                    "<li>Elige el rango de fechas que deseas evaluar (hoy, esta semana, este mes o el año entero).</li>" +
                    "<li>Revisa en un abrir y cerrar de ojos el total cobrado, tus costos reales y tu <b>Utilidad Neta Directa</b>.</li>" +
                    "<li><b>Descarga en Excel con 1 Clic:</b> Genera reportes elegantes listos para analizar o enviar a tu contador.</li>" +
                    "<li><b>Impresión y Guardado en PDF:</b> Exporta balances oficiales impecables para respaldar la contabilidad de tu empresa.</li>" +
                    "</ul>" +
                    "<div class='note'><span class='tag-note'>📊 AUDITORÍA EJECUTIVA:</span> <b>Revisión Semanal —</b> Genera este reporte al final de cada semana para ajustar precios en productos con bajo margen de ganancia.</div>",
                    "icons/reports.svg"
            ));

            sections.add(new ManualSection(
                    "Tecnología de Vanguardia y Respaldo Continuo",
                    "<p>Siéntete respaldado en todo momento por un equipo de tecnología enfocado en hacer crecer tu comercio:</p>" +
                    "<ul>" +
                    "<li><b>Prueba Sin Riesgos:</b> Disfruta de todas las capacidades del sistema y comprueba cómo agiliza las ventas de tu negocio.</li>" +
                    "<li><b>Activación de Licencia Definitiva:</b> Asegura la continuidad de tu empresa activando tu licencia permanente con el código único de tu equipo.</li>" +
                    "<li><b>Acompañamiento y Soporte Directo:</b> Cuenta con el respaldo de <b>ChopCode Solutions</b> y su desarrollador principal <b>Brandon Daza</b> para resolver tus dudas, capacitar a tu equipo o agregar nuevas funciones a la medida de tu negocio.</li>" +
                    "</ul>" +
                    "<div class='tip'><span class='tag-tip'>🤝 GARANTÍA Y SOPORTE:</span> <b>Evolución Continua —</b> Tu sistema está preparado para recibir actualizaciones continuas sin perder tu información comercial.</div>",
                    "icons/settings.svg"
            ));

            sections.add(new ManualSection(
                    "Preguntas Frecuentes & Respaldo de Tranquilidad",
                    "<p>Respuestas inmediatas a las dudas más comunes sobre la seguridad y operación de tu negocio:</p>" +
                    "<h2>¿Mis datos están seguros si se interrumpe la energía eléctrica en mi local?</h2>" +
                    "<p><b>Sí, 100% protegidos.</b> ERP+ Business funciona con un motor de base de datos local SQLite de grado industrial que guarda de manera atómica cada transacción en el instante preciso en que presionas confirmar. Nada se pierde.</p>" +
                    "<h2>¿Puedo utilizar la aplicación en una laptop o PC de escasos recursos?</h2>" +
                    "<p><b>Totalmente.</b> El sistema está optimizado con arquitectura ultraligera en Java Swing, consumiendo mínimo procesador y memoria RAM, lo que garantiza una velocidad de respuesta inmediata incluso en equipos de cómputo sencillos.</p>" +
                    "<h2>¿Necesito pagar mensualidades por el uso de la aplicación?</h2>" +
                    "<p><b>No.</b> Adquieres tu licencia definitiva de pago único con soporte directo de ChopCode Solutions. Tu software es tuyo para siempre.</p>" +
                    "<div class='tip'><span class='tag-tip'>🛡 TRANQUILIDAD TOTAL:</span> <b>Respaldo Garantizado —</b> Tu información pertenece a tu negocio y jamás viaja a servidores externos desconocidos.</div>",
                    "icons/shield-heart.svg"
            ));
        }
    }
}
