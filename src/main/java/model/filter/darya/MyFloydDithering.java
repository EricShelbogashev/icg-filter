package model.filter.darya;

import core.filter.Image;
import core.filter.MatrixFilter;

public class MyFloydDithering extends MatrixFilter {
    int[] kv;

    public MyFloydDithering(int[] kv) {
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
        int[] values = {image.color(x - 1, y), image.color(x + 1, y - 1), image.color(x, y - 1), image.color(x - 1, y - 1)};
        float[] koef = {7.0f / 16, 3.0f / 16, 5.0f / 16, 1 / 16.0f};
        for (int i = 0; i < 4; i++) {
            err_red += (old_red - ((values[i] >> 16) & 0xFF)) * koef[i];
            err_green += (old_green - ((values[i] >> 8) & 0xFF)) * koef[i];
            err_blue += (old_blue - ((values[i]) & 0xFF)) * koef[i];
        }
        int r = ClosestPalette.find_closest_palette_color(old_red + (int) err_red, old_green + (int) err_green, old_blue + (int) err_blue, alpha, kv);
        return r;
    }
}
