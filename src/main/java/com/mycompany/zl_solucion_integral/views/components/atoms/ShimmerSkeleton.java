package com.mycompany.zl_solucion_integral.views.components.atoms;

import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Átomo de esqueleto de carga (shimmer). Bloques con pulso animado que
 * comunican que los datos están cargando, sin bloquear la interfaz.
 * Uso previsto como marcador de posición mientras un SwingWorker carga datos.
 */
public class ShimmerSkeleton extends JPanel {
    private final Timer timer;
    private float phase = 0f;

    public ShimmerSkeleton() {
        setOpaque(false);
        // Pulso suave a ~30 fps para no consumir CPU innecesariamente.
        timer = new Timer(33, e -> {
            phase += 0.06f;
            if (phase > 1f) {
                phase = 0f;
            }
            repaint();
        });
        timer.start();

        // Detener la animación cuando el componente ya no se muestra (DRY con la vida útil).
        addAncestorListener(new javax.swing.event.AncestorListener() {
            @Override public void ancestorAdded(javax.swing.event.AncestorEvent e) { timer.start(); }
            @Override public void ancestorRemoved(javax.swing.event.AncestorEvent e) { timer.stop(); }
            @Override public void ancestorMoved(javax.swing.event.AncestorEvent e) { }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pulso entre 40% y 75% de opacidad.
        float alpha = 0.4f + 0.35f * (float) Math.abs(Math.sin(phase * Math.PI));
        g2.setColor(new Color(
                ThemeConstants.TEXT_SECONDARY.getRed(),
                ThemeConstants.TEXT_SECONDARY.getGreen(),
                ThemeConstants.TEXT_SECONDARY.getBlue(),
                (int) (alpha * 255)
        ));

        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            g2.dispose();
            return;
        }

        // Bloque ancho tipo "cabecera".
        g2.fill(new RoundRectangle2D.Float(16, 16, Math.min(width - 32, 260), 18, 10, 10));

        // Fila de tarjetas tipo "métricas".
        int cardW = (width - 32 - 3 * 14) / 4;
        for (int i = 0; i < 4; i++) {
            int x = 16 + i * (cardW + 14);
            g2.fill(new RoundRectangle2D.Float(x, 58, cardW, 64, 12, 12));
        }

        // Dos bloques grandes tipo "tablas".
        int halfW = (width - 32 - 18) / 2;
        g2.fill(new RoundRectangle2D.Float(16, 140, halfW, Math.max(60, height - 190), 12, 12));
        g2.fill(new RoundRectangle2D.Float(16 + halfW + 18, 140, halfW, Math.max(60, height - 190), 12, 12));

        g2.dispose();
    }
}
