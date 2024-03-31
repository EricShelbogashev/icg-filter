package model.filter.boch;

import core.filter.CustomFilter;
import core.filter.Image;
import model.filter.leonid.ColorUtils;

import java.awt.image.BufferedImage;

public class EgorFloydDither extends CustomFilter {
    private final int qr, qg, qb;
    private double[][][] errors;

    public EgorFloydDither(int qr, int qg, int qb) {
        this.qr = qr;
        this.qb = qg;
        this.qg = qb;
    }

    @Override
    protected BufferedImage apply(Image image) {
        errors = new double[image.width()][image.height()][3];
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                int oldR = image.red(x, y) + (int) errors[x][y][0];
                int oldG = image.green(x, y) + (int) errors[x][y][1];
                int oldB = image.blue(x, y) + (int) errors[x][y][2];
                int newR = ColorUtils.findClosestColor(oldR, qr);
                int newG = ColorUtils.findClosestColor(oldG, qg);
                int newB = ColorUtils.findClosestColor(oldB, qb);
                double[] err = {oldR - newR, oldG - newG, oldB - newB};
                image.setColor(x, y, ColorUtils.rgb(newR, newG, newB));
                if (y < image.height() - 1) {
                    if (x > 0)
                        setError(x, y, -1, 1, err);
                    if (x < image.width() - 1)
                        setError(x, y, 1, 1, err);
                    setError(x, y, 0, 1, err);
                }
                if (x < image.width() - 1)
                    setError(x, y, 1, 0, err);
            }
        }
        return image.bufferedImage();
    }

    void setError(int x, int y, int xOffset, int yOffset, double[] quantError) {
        errors[x + xOffset][y + yOffset][0] += quantError[0] * getK(xOffset, yOffset);
        errors[x + xOffset][y + yOffset][1] += quantError[1] * getK(xOffset, yOffset);
        errors[x + xOffset][y + yOffset][2] += quantError[2] * getK(xOffset, yOffset);
    }

    float getK(int x, int y) {
        if (x == 1 && y == 0)
            return 7.0f / 16;
        else if (x == 0 && y == 1)
            return 5.0f / 16;
        else if (x == -1 && y == 1)
            return 1.0f / 16;
        else if (x == 1 && y == 1)
            return 3.0f / 16;
        return 0;
    }
}