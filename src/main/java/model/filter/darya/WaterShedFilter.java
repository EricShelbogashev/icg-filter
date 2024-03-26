package model.filter.darya;

import core.filter.Image;
import core.filter.MatrixFilter;
import model.filter.leonid.ColorUtils;

public class WaterShedFilter extends MatrixFilter {
    int[] kv;

    public WaterShedFilter(int[] kv) {
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
        int[] values = {image.color(-1, -1), image.color(-1, 0), image.color(-1, 1),
                image.color(1, -1), image.color(1, 0), image.color(1, 1)};
        float[] koef = {-1.0f / 9, -2.0f / 9, -1.0f / 9, 1.0f / 9, 2.0f / 9, 1.0f / 9};
        for (int i = 0; i < 6; i++) {
            err_red += ColorUtils.red(values[i]) * koef[i];
            err_green += ColorUtils.green(values[i]) * koef[i];
            err_blue += ColorUtils.blue(values[i]) * koef[i];
        }
        return ColorUtils.rgb(
                ColorUtils.findClosestColor(old_red + (int) err_red, kv[0]),
                ColorUtils.findClosestColor(old_green + (int) err_green, kv[1]),
                ColorUtils.findClosestColor(old_blue + (int) err_blue, kv[2]),
                alpha);
    }
}
