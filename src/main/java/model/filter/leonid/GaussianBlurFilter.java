package model.filter.leonid;

import core.filter.Image;
import core.filter.MatrixFilter;

public class GaussianBlurFilter extends MatrixFilter {
    private final int w;

    public GaussianBlurFilter(int w) {
        this.w = w;
    }

    @Override
    protected int apply(Image image, int column, int row) {
        float res_r = 0;
        float res_g = 0;
        float res_b = 0;
        int alpha = (image.color(column, row) >> 24) & 0xFF;
        float sigma = w / 2.1f;
        for (int x = -1 * w; x <= w; x++)
            for (int y = -1 * w; y <= w; y++) {
                float k = (float) (1.0f / (2.0f * Math.PI * sigma * sigma) * Math.pow(2.7f, -1.0f * (x * x + y * y) / (2.0f * sigma * sigma)));
                res_r += ((image.color(column + x, row + y) >> 16) & 0xFF) * k;
                res_g += ((image.color(column + x, row + y) >> 8) & 0xFF) * k;
                res_b += ((image.color(column + x, row + y)) & 0xFF) * k;
            }
        if (Math.round(res_r) > 255 || Math.round(res_r) < 0)
            res_r = 255;
        if (Math.round(res_b) > 255 || Math.round(res_b) < 0)
            res_b = 255;
        if (Math.round(res_g) > 255 || Math.round(res_g) < 0)
            res_g = 255;
        return ((alpha & 0xFF) << 24) |
                ((Math.round(res_r) & 0xFF) << 16) |
                ((Math.round(res_g) & 0xFF) << 8) |
                ((Math.round(res_b) & 0xFF));
    }
}
