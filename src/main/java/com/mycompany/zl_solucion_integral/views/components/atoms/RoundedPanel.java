package com.mycompany.zl_solucion_integral.views.components.atoms;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int round = 20;
    private Color backgroundColor = ThemeConstants.CARD_BACKGROUND;

    public RoundedPanel() {
        setOpaque(false);
    }

    public RoundedPanel(int round, Color backgroundColor) {
        this.round = round;
        this.backgroundColor = backgroundColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), round, round);
        g2.dispose();
        super.paintComponent(g);
    }
}
