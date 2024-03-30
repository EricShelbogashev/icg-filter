package model.filter.eric;

import core.filter.CustomFilter;
import core.filter.Image;
import model.filter.leonid.ColorUtils;

import java.awt.image.BufferedImage;

public class EricFloydSteinbergFilter extends CustomFilter {
    private final int quantizationRed;
    private final int quantizationGreen;
    private final int quantizationBlue;

    public EricFloydSteinbergFilter(int quantizationRed, int quantizationGreen, int quantizationBlue) {
        this.quantizationRed = quantizationRed;
        this.quantizationGreen = quantizationGreen;
        this.quantizationBlue = quantizationBlue;
    }

    private float errorDiffusionFactor(int xOffset, int yOffset) {
        return switch (xOffset + "," + yOffset) {
            case "1,0" -> 7.0f / 16;
            case "-1,1" -> 1.0f / 16;
            case "0,1" -> 5.0f / 16;
            case "1,1" -> 3.0f / 16;
            default -> 0;
        };
    }

    @Override
    protected BufferedImage apply(Image image) {
        double[][][] errors = new double[image.width()][image.height()][3];
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                int[] originalColors = {image.red(x, y), image.green(x, y), image.blue(x, y)};
                int[] newColors = new int[3];
                int[] colorErrors = new int[3];

                for (int i = 0; i < 3; i++) {
                    originalColors[i] += (int) errors[x][y][i];
                    newColors[i] = ColorUtils.findClosestColor(originalColors[i],
                            i == 0 ? quantizationRed
                                    : i == 1 ? quantizationGreen
                                    : quantizationBlue
                    );
                    colorErrors[i] = originalColors[i] - newColors[i];
                }

                image.setColor(x, y, ColorUtils.rgb(newColors[0], newColors[1], newColors[2]));
                distributeError(errors, x, y, image.width(), image.height(), colorErrors);
            }
        }
        return image.bufferedImage();
    }

    private void distributeError(double[][][] errors, int x, int y, int width, int height, int[] colorErrors) {
        int[][] offsets = {{1, 0}, {-1, 1}, {0, 1}, {1, 1}};
        for (int[] offset : offsets) {
            int newX = x + offset[0];
            int newY = y + offset[1];

            if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                float factor = errorDiffusionFactor(offset[0], offset[1]);
                for (int i = 0; i < 3; i++) {
                    errors[newX][newY][i] += colorErrors[i] * factor;
                }
            }
        }
    }
}
