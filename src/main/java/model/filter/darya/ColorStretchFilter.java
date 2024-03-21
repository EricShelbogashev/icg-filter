package model.filter.darya;

import core.filter.Image;
import core.filter.MatrixFilter;

public class ColorStretchFilter extends MatrixFilter {
    private final int[] kv;

    public ColorStretchFilter(int[] kv) {
        this.kv = kv;
    }

    @Override
    protected int apply(Image image, int x, int y) {
        int oldpix = image.color(x, y);
        int alpha = (oldpix >> 24) & 0xFF;
        float err_red = 0;
        float err_blue = 0;
        float err_green = 0;
        float koef = 1.0f / 25;
        for (int i = -2; i < 2; i++)
            for (int j = -2; j < 2; j++) {
                err_red += (image.red(x + i, y + j) * koef);
                err_green += (image.green(x + i, y + j) * koef);
                err_blue += (image.blue(x + i, y + j) * koef);
            }
        int r = ClosestPalette.find_closest_palette_color((int) err_red, (int) err_green, (int) err_blue, alpha, kv);
        return r;
    }
}
