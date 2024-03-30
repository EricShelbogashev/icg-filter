package model.filter.leonid;

import core.filter.CustomFilter;
import core.filter.Image;

import java.awt.image.BufferedImage;

public class FSDithering extends CustomFilter {

    int quantizationRed, quantizationGreen, quantizationBlue;

    private double[][][] errors;

    public FSDithering(int quantizationRed, int quantizationGreen, int quantizationBlue) {
        this.quantizationRed = quantizationRed;
        this.quantizationBlue = quantizationBlue;
        this.quantizationGreen = quantizationGreen;
    }


    float chooseFactor(int xOffset, int yOffset) {
        if (xOffset == 1 && yOffset == 0) {
            return 7.0f / 16;
        } else if (xOffset == -1 && yOffset == 1) {
            return 1.0f / 16;
        } else if (xOffset == 0 && yOffset == 1) {
            return 5.0f / 16;
        } else if (xOffset == 1 && yOffset == 1) {
            return 3.0f / 16;
        }

        return 0;

    }

    void setError(int x, int y, int xOffset, int yOffset, double[] quantError) {
        // red error
        errors[x + xOffset][y + yOffset][0] += quantError[0] * chooseFactor(xOffset, yOffset);
        // green error
        errors[x + xOffset][y + yOffset][1] += quantError[1] * chooseFactor(xOffset, yOffset);
        // blue error
        errors[x + xOffset][y + yOffset][2] += quantError[2] * chooseFactor(xOffset, yOffset);

    }

    @Override
    protected BufferedImage apply(Image image) {
        errors = new double[image.width()][image.height()][3];
        for (int y = 0; y < image.bufferedImage().getHeight(); y++) {
            for (int x = 0; x < image.bufferedImage().getWidth(); x++) {
                int oldRed = image.red(x, y);
                int oldGreen = image.green(x, y);
                int oldBlue = image.blue(x, y);

                int newRed = ColorUtils.findClosestColor(oldRed += (int) errors[x][y][0], quantizationRed);
                int newGreen = ColorUtils.findClosestColor(oldGreen += (int) errors[x][y][1], quantizationGreen);
                int newBlue = ColorUtils.findClosestColor(oldBlue += (int) errors[x][y][2], quantizationBlue);

                double[] quantError = {oldRed - newRed, oldGreen - newGreen, oldBlue - newBlue};

                image.setColor(x, y, ColorUtils.rgb(newRed, newGreen, newBlue));

                // Spread error
                if (y + 1 < image.height()) {
                    if (x + 1 < image.width()) {
                        setError(x, y, 1, 1, quantError);
                    }
                    if (x > 0) {
                        setError(x, y, -1, 1, quantError);
                    }
                    setError(x, y, 0, 1, quantError);
                }

                if (x + 1 < image.width()) {
                    setError(x, y, 1, 0, quantError);
                }

            }

        }
        return image.bufferedImage();
    }
}

