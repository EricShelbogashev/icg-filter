package model.filter.boch;

import core.filter.CustomFilter;
import core.filter.Image;
import model.filter.darya.ClosestPalette;

import java.awt.image.BufferedImage;

public class Ordered extends CustomFilter
{
    private final int[][] M1 = {{0, 2}, {3, 1}};
    private final int[][] M2 = {{0, 8, 2, 10}, {12, 4, 14, 6}, {3, 11, 1, 9}, {15, 7, 13, 5}};
    private final int[][] M3 = {{0, 32, 8, 40, 2, 34, 10, 42}, {48, 16, 56, 24, 50, 18, 58, 26}, {12, 44, 4, 36, 14, 46, 6, 38}, {60, 28, 52, 20, 62, 30, 54, 22}, {3, 35, 11, 43, 1, 33, 9, 41}, {51, 19, 59, 27, 49, 17, 57, 25}, {15, 47, 7, 39, 13, 45, 5, 37}, {63, 31, 55, 23, 61, 29, 53, 21}};
    int[] k;

    public Ordered(int[] k)
    {
        this.k = k;
    }

    @Override
    protected BufferedImage apply(Image image)
    {
        int r = -1000, g = -1000, b = -1000;
        for (int i = 0; i < image.width(); i++)
        {
            for (int j = 0; j < image.height(); j++)
            {
                int old = image.color(i, j);
                if (k[0] <= 4) r = image.red(i, j) + 32 - M3[j % 8][i % 8];
                else if (k[0] <= 16) r = image.red(i, j) + 8 - M2[j % 4][i % 4];
                else if (k[0] <= 64) r = image.red(i, j) + 2 - M1[j % 2][i % 2];

                if (k[1] <= 4) g = image.green(i, j) + 32 - M3[j % 8][i % 8];
                else if (k[1] <= 16) g = image.green(i, j) + 8 - M2[j % 4][i % 4];
                else if (k[1] <= 64) g = image.green(i, j) + 2 - M1[j % 2][i % 2];

                if (k[2] <= 4) b = image.blue(i, j) + 32 - M3[j % 8][i % 8];
                else if (k[2] <= 16) b = image.blue(i, j) + 8 - M2[j % 4][i % 4];
                else if (k[2] <= 64) b = image.blue(i, j) + 2 - M1[j % 2][i % 2];

                int alpha = (old >> 24) & 0xFF;
                if (r == -1000 || g == -1000 || b == -1000) return image.bufferedImage();
                image.bufferedImage().setRGB(i, j, find_closest(r, g, b, alpha, k));
            }
        }
        return image.bufferedImage();
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
