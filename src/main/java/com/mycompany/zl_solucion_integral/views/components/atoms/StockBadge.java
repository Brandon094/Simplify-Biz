package com.mycompany.zl_solucion_integral.views.components.atoms;

import com.mycompany.zl_solucion_integral.models.Producto;
import com.mycompany.zl_solucion_integral.views.components.ThemeConstants;
import javax.swing.*;
import java.awt.*;

/**
 * Átomo visual reutilizable que muestra información de stock de un producto
 * con semáforo de colores según el nivel de inventario disponible.
 *
 * <p>Siempre visible con dos estados:</p>
 * <ul>
 *   <li><b>Espera</b>: mensaje guía para el vendedor</li>
 *   <li><b>Activo</b>: semáforo de colores con precio, stock y código</li>
 * </ul>
 *
 * Diseñado para integrarse en cualquier formulario que necesite feedback
 * visual de disponibilidad (POS, compras, traslados).
 */
public class StockBadge extends RoundedPanel {

    private static final String IDLE_TEXT = "Selecciona un producto para ver su precio y disponibilidad";
    private final JLabel lblInfo;

    public StockBadge() {
        super(12, ThemeConstants.SIDEBAR_BACKGROUND);
        setLayout(new BorderLayout(8, 0));
        applyIdleState();

        lblInfo = new JLabel();
        lblInfo.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.PLAIN, 11.5f));
        lblInfo.setForeground(ThemeConstants.TEXT_SECONDARY);
        lblInfo.setText(IDLE_TEXT);
        add(lblInfo, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Actualiza el badge con los datos del producto seleccionado.
     * Aplica semáforo de colores según nivel de stock y muestra
     * código, precio unitario y disponibilidad.
     *
     * @param p el producto seleccionado; si es {@code null}, vuelve al estado de espera
     */
    public void update(Producto p) {
        if (p == null) {
            clear();
            return;
        }

        int stock = p.getCantidad();
        String codigo = p.getCodigo() != null ? p.getCodigo() : "S/C";
        String precio = String.format("$%,.2f", p.getPrecio());

        String stockText;
        Color accentColor;

        if (stock <= 0) {
            stockText = "Sin stock disponible";
            accentColor = ThemeConstants.NEON_RED;
        } else if (stock <= 4) {
            stockText = "Quedan solo " + stock + " unidades";
            accentColor = ThemeConstants.NEON_RED;
        } else if (stock <= 10) {
            stockText = "Quedan " + stock + " unidades";
            accentColor = ThemeConstants.NEON_AMBER;
        } else {
            stockText = stock + " unidades disponibles";
            accentColor = ThemeConstants.NEON_GREEN;
        }

        String html = String.format(
            "<html><span style='color:%s; font-weight:bold;'>%s</span>"
            + "  <span style='color:%s;'>|</span>  "
            + "Precio unitario: <b>%s</b>"
            + "  <span style='color:%s;'>|</span>  "
            + "Codigo: %s</html>",
            colorToHex(accentColor), stockText,
            colorToHex(ThemeConstants.TEXT_SECONDARY),
            precio,
            colorToHex(ThemeConstants.TEXT_SECONDARY),
            codigo
        );

        lblInfo.setText(html);
        lblInfo.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.BOLD, 11.5f));

        // Borde sutil con el color semáforo
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(
                    accentColor.getRed(), accentColor.getGreen(),
                    accentColor.getBlue(), 100), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        revalidate();
        repaint();
    }

    /**
     * Vuelve al estado de espera con el mensaje guía para el vendedor.
     */
    public void clear() {
        lblInfo.setText(IDLE_TEXT);
        lblInfo.setFont(ThemeConstants.FONT_SMALL.deriveFont(Font.PLAIN, 11.5f));
        lblInfo.setForeground(ThemeConstants.TEXT_SECONDARY);
        applyIdleState();
    }

    private void applyIdleState() {
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConstants.CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private static String colorToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
