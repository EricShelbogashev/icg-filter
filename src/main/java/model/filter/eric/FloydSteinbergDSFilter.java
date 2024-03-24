package model.filter.eric;

import core.filter.CustomFilter;
import core.filter.Image;
import model.filter.leonid.ColorUtils;

import java.awt.image.BufferedImage;

public class FloydSteinbergDSFilter extends CustomFilter {
    private final int quantizationRed, quantizationGreen, quantizationBlue;

    private static final int[][] ditherMatrix = {
            {1, 0, 7},
            {-1, 1, 3},
            {0, 1, 5},
            {1, 1, 1}
    };

    public FloydSteinbergDSFilter(int quantizationRed, int quantizationGreen, int quantizationBlue) {
        this.quantizationRed = quantizationRed;
        this.quantizationBlue = quantizationBlue;
        this.quantizationGreen = quantizationGreen;
    }

    private void applyError(Image image, int x, int y, int[] quantumError) {
        for (int[] offset : ditherMatrix) {
            int nx = x + offset[0], ny = y + offset[1];
            float factor = offset[2] / 16.0f;

            int[] colors = image.components(nx, ny);
            colors[0] += (int) (quantumError[0] * factor);
            colors[1] += (int) (quantumError[1] * factor);
            colors[2] += (int) (quantumError[2] * factor);

            image.setColor(nx, ny, ColorUtils.componentsToRgb(colors));
        }
    }

    @Override
    protected BufferedImage apply(Image image) {
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                final var color = image.color(x, y);
                final var red = ColorUtils.red(color);
                final var green = ColorUtils.green(color);
                final var blue = ColorUtils.blue(color);

                int[] newColors = {
                        ColorUtils.findClosestColor(red, quantizationRed),
                        ColorUtils.findClosestColor(green, quantizationGreen),
                        ColorUtils.findClosestColor(blue, quantizationBlue)
                };

                int[] quantumError = {
                        red - newColors[0],
                        green - newColors[1],
                        blue - newColors[2]
                };

                image.setColor(x, y, ColorUtils.componentsToRgb(newColors));
                applyError(image, x, y, quantumError);
            }
        }
        return image.bufferedImage();
    }
}
