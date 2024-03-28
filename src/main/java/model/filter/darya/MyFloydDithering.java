package model.filter.darya;

import core.filter.Image;
import core.filter.MatrixFilter;
import model.filter.leonid.ColorUtils;

public class MyFloydDithering extends MatrixFilter {
    int[] kv;

    public MyFloydDithering(int[] kv) {
        this.kv = kv;
    }

    @Override
    protected int apply(Image image, int x, int y) {
        int oldpix = image.color(x, y);
        int alpha = ColorUtils.alpha(oldpix);
        int old_red = ColorUtils.red(oldpix);
        int old_green = ColorUtils.green(oldpix);
        int old_blue = ColorUtils.blue(oldpix);
        float err_red = 0;
        float err_blue = 0;
        float err_green = 0;
        int[] values = {image.color(x - 1, y), image.color(x + 1, y - 1), image.color(x, y - 1), image.color(x - 1, y - 1)};
        float[] koef = {7.0f / 16, 3.0f / 16, 5.0f / 16, 1 / 16.0f};
        for (int i = 0; i < 4; i++) {
            err_red += old_red - ColorUtils.red(values[i]) * koef[i];
            err_green += old_green - ColorUtils.green(values[i]) * koef[i];
            err_blue += old_blue - ColorUtils.blue(values[i]) * koef[i];
        }
        return ColorUtils.rgb(
                ColorUtils.findClosestColor(old_red + (int) err_red, kv[0]),
                ColorUtils.findClosestColor(old_green + (int) err_green, kv[1]),
                ColorUtils.findClosestColor(old_blue + (int) err_blue, kv[2]),
                alpha
        );
    }
}
