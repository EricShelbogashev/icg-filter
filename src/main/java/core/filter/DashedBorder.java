package core.filter;

import javax.swing.border.Border;
import java.awt.*;

public class DashedBorder implements Border {
    private final Color color;
    private final int thickness;

    public DashedBorder(Color color, int thickness) {
        this.color = color;
        this.thickness = thickness;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
        g2d.setColor(color);
        g2d.drawRect(x, y, width - 1, height - 1);
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
