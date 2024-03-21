package model.filter.darya;

import core.filter.Image;
import core.filter.MatrixFilter;

public class WaterShedFilter extends MatrixFilter {
    int[] kv;

    public WaterShedFilter(int[] kv) {
        this.kv = kv;
    }

    @Override
    protected int apply(Image image, int x, int y) {
        int oldpix = image.color(x, y);
        int alpha = (oldpix >> 24) & 0xFF;
        int old_red = (oldpix >> 16) & 0xFF;
        int old_green = (oldpix >> 8) & 0xFF;
        int old_blue = (oldpix) & 0xFF;
        float err_red = 0;
        float err_blue = 0;
        float err_green = 0;
        int[] values = {image.color(-1, -1), image.color(-1, 0), image.color(-1, 1),
                image.color(1, -1), image.color(1, 0), image.color(1, 1)};
        float[] koef = {-1.0f / 9, -2.0f / 9, -1.0f / 9, 1.0f / 9, 2.0f / 9, 1.0f / 9};
        for (int i = 0; i < 6; i++) {
            err_red += (((values[i] >> 16) & 0xFF)) * koef[i];
            err_green += (((values[i] >> 8) & 0xFF)) * koef[i];
            err_blue += (((values[i]) & 0xFF)) * koef[i];
        }
        int r = ClosestPalette.find_closest_palette_color(old_red + (int) err_red, old_green + (int) err_green, old_blue + (int) err_blue, alpha, kv);
        return r;
    }
}
