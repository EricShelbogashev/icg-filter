package model.filter.darya;

import core.filter.CustomFilter;
import core.filter.Image;
import model.filter.leonid.ColorUtils;

import java.awt.image.BufferedImage;

public class MyFloydDithering extends CustomFilter {
    private final int quantRed, quantGreen, quantBlue;
    private double[][][] errors;

    public MyFloydDithering(int[] kv) {
        this.quantRed = kv[0];
        this.quantGreen = kv[1];
        this.quantBlue = kv[2];
    }

    float chooseKoeff(int xStep, int yStep) {
        if (xStep == 1 && yStep == 0)
            return 7.0f / 16;
        else if (xStep == -1 && yStep == 1)
            return 1.0f / 16;
        else if (xStep == 0 && yStep == 1)
            return 5.0f / 16;
        else if (xStep == 1 && yStep == 1)
            return 3.0f / 16;
        return 0;
    }

    void setError(int x, int y, int xOffset, int yOffset, double[] quantError) {
        errors[x + xOffset][y + yOffset][0] += quantError[0] * chooseKoeff(xOffset, yOffset);
        errors[x + xOffset][y + yOffset][1] += quantError[1] * chooseKoeff(xOffset, yOffset);
        errors[x + xOffset][y + yOffset][2] += quantError[2] * chooseKoeff(xOffset, yOffset);
    }

    @Override
    protected BufferedImage apply(Image image) {
        errors = new double[image.width()][image.height()][3];
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                int oldRed = image.red(x, y) + (int) errors[x][y][0];
                int oldGreen = image.green(x, y) + (int) errors[x][y][1];
                int oldBlue = image.blue(x, y) + (int) errors[x][y][2];
                int newRed = ColorUtils.findClosestColor(oldRed, quantRed);
                int newGreen = ColorUtils.findClosestColor(oldGreen, quantGreen);
                int newBlue = ColorUtils.findClosestColor(oldBlue, quantBlue);
                double[] quantError = {oldRed - newRed, oldGreen - newGreen, oldBlue - newBlue};
                image.setColor(x, y, ColorUtils.rgb(newRed, newGreen, newBlue));
                if (y < image.height() - 1) {
                    if (x < image.width() - 1)
                        setError(x, y, 1, 1, quantError);
                    if (x > 0)
                        setError(x, y, -1, 1, quantError);
                    setError(x, y, 0, 1, quantError);
                }
                if (x < image.width() - 1)
                    setError(x, y, 1, 0, quantError);
            }
        }
        return image.bufferedImage();
    }
}
