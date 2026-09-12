package com.mycompany.zl_solucion_integral.views.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.zl_solucion_integral.views.components.atoms.NeonButton;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
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

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblSubtitle.setFont(ThemeConstants.FONT_SMALL);
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
        JPanel state = new JPanel(new GridBagLayout());
        state.setOpaque(false);

        FlatSVGIcon icon = new FlatSVGIcon(iconPath, 28, 28);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter().add(Color.BLACK, iconColor));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;

        JLabel lblTitle = new JLabel(title, icon, SwingConstants.CENTER);
        lblTitle.setForeground(ThemeConstants.TEXT_PRIMARY);
        lblTitle.setFont(ThemeConstants.FONT_BODY.deriveFont(Font.BOLD, 15f));
        lblTitle.setIconTextGap(12);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        state.add(lblTitle, gbc);

        JLabel lblMessage = new JLabel(message, SwingConstants.CENTER);
        lblMessage.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblMessage.setFont(ThemeConstants.FONT_SMALL);
        gbc.gridy = 1;
        state.add(lblMessage, gbc);

        if (ctaText != null && ctaAction != null) {
            NeonButton btnCta = new NeonButton(ctaText);
            btnCta.setNeonColor(iconColor);
            btnCta.setIconTextGap(8);
            btnCta.addActionListener(e -> ctaAction.run());
            gbc.gridy = 2;
            gbc.insets = new Insets(14, 0, 0, 0);
            state.add(btnCta, gbc);
        }

        return state;
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
}
