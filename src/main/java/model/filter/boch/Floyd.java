package model.filter.boch;

import core.filter.Image;
import core.filter.MatrixFilter;
import model.filter.leonid.ColorUtils;

public class Floyd extends MatrixFilter
{
    int[] k;

    public Floyd(int[] k)
    {
        this.k = k;
    }

    @Override
    protected int apply(Image image, int x, int y)
    {
        int old = image.color(x, y);

        int alpha = (old >> 24) & 0xFF;
        int red = (old >> 16) & 0xFF;
        int green = (old >> 8) & 0xFF;
        int blue = (old) & 0xFF;

        float err_r = 0;
        float err_b = 0;
        float err_g = 0;

        int[] list = {image.color(x - 1, y), image.color(x + 1, y - 1), image.color(x, y - 1), image.color(x - 1, y - 1)};
        float[] coefs = {7.0f / 16, 3.0f / 16, 5.0f / 16, 1 / 16.0f};

        for (int i = 0; i < 4; i++)
        {
            err_r += (red - ((list[i] >> 16) & 0xFF)) * coefs[i];
            err_g += (green - ((list[i] >> 8) & 0xFF)) * coefs[i];
            err_b += (blue - ((list[i]) & 0xFF)) * coefs[i];
        }

        int r = find_closest(red + (int) err_r, green + (int) err_g, blue + (int) err_b, alpha, k);
        return r;
    }

    private static int find_closest(int r, int g, int b, int alpha, int[] k)
    {
        int[] res = {r, g, b};
        for (int i = 0; i < 3; i++)
        {
            if (res[i] < 0) res[i] = 0;
            if (res[i] > 255) res[i] = 255;
            float del = (float) (k[i] - 1);
            res[i] = (int) ((float) (Math.round((float) res[i] / 255 * del)) / del * 255);
        }
        return (alpha << 24) | (res[0] << 16) | (res[1] << 8) | (res[2]);
    }
}
