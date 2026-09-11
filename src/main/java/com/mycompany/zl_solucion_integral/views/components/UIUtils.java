package com.mycompany.zl_solucion_integral.views.components;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
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
