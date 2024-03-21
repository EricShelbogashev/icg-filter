package model.filter.darya;

import core.filter.CustomFilter;
import core.filter.Image;

import java.awt.image.BufferedImage;

public class MyOrderedDithering extends CustomFilter {
    private final int[][] M1 = {{0, 2}, {3, 1}};
    private final int[][] M2 = {{0, 8, 2, 10}, {12, 4, 14, 6}, {3, 11, 1, 9}, {15, 7, 13, 5}};
    private final int[][] M3 = {{0, 32, 8, 40, 2, 34, 10, 42}, {48, 16, 56, 24, 50, 18, 58, 26}, {12, 44, 4, 36, 14, 46, 6, 38},
            {60, 28, 52, 20, 62, 30, 54, 22}, {3, 35, 11, 43, 1, 33, 9, 41}, {51, 19, 59, 27, 49, 17, 57, 25},
            {15, 47, 7, 39, 13, 45, 5, 37}, {63, 31, 55, 23, 61, 29, 53, 21}};
    int[] kv;

    public MyOrderedDithering(int[] kv) {
        this.kv = kv;
    }

    @Override
    protected BufferedImage apply(Image image) {
        int red = -1000, green = -1000, blue = -1000;
        for (int i = 0; i < image.width(); i++)
            for (int j = 0; j < image.height(); j++) {
                int oldpix = image.color(i, j);
                if (kv[0] <= 4)
                    red = image.red(i, j) + 32 - M3[j % 8][i % 8];
                else if (kv[0] <= 16)
                    red = image.red(i, j) + 8 - M2[j % 4][i % 4];
                else if (kv[0] <= 64)
                    red = image.red(i, j) + 2 - M1[j % 2][i % 2];
                if (kv[1] <= 4)
                    green = image.green(i, j) + 32 - M3[j % 8][i % 8];
                else if (kv[1] <= 16)
                    green = image.green(i, j) + 8 - M2[j % 4][i % 4];
                else if (kv[1] <= 64)
                    green = image.green(i, j) + 2 - M1[j % 2][i % 2];
                if (kv[2] <= 4)
                    blue = image.blue(i, j) + 32 - M3[j % 8][i % 8];
                else if (kv[2] <= 16)
                    blue = image.blue(i, j) + 8 - M2[j % 4][i % 4];
                else if (kv[2] <= 64)
                    blue = image.blue(i, j) + 2 - M1[j % 2][i % 2];
                int alpha = (oldpix >> 24) & 0xFF;
                if (red == -1000 || green == -1000 || blue == -1000)
                    return image.bufferedImage();
                image.bufferedImage().setRGB(i, j, ClosestPalette.find_closest_palette_color(red, green, blue, alpha, kv));
            }
        return image.bufferedImage();
    }
}
