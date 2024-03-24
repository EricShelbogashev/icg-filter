package model.filter.darya;

import core.filter.CustomFilter;
import core.filter.Image;
import model.filter.leonid.ColorUtils;

import java.awt.image.BufferedImage;

public class FDither extends CustomFilter {
    int[] kv;
    public FDither(int[]kv){
        this.kv = kv;
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

    void setError(Image image, int x, int y, int xOffset, int yOffset, int[] error) {
        int red = image.red(x + xOffset, y + yOffset);
        red += (int) (error[0] * chooseFactor(xOffset, yOffset));
        int green = image.green(x + xOffset, y + yOffset);
        green += (int) (error[1] * chooseFactor(xOffset, yOffset));
        int blue = image.blue(x + xOffset, y + yOffset);
        blue += (int) (error[2] * chooseFactor(xOffset, yOffset));
        image.setColor(x + xOffset, y + yOffset, ColorUtils.rgb(red, green, blue));
    }
    @Override
    protected BufferedImage apply(Image image) {
        int oldRed, oldGreen, oldBlue, newRed, newGreen, newBlue;
        for (int y = 0; y < image.bufferedImage().getHeight(); y++) {
            for (int x = 0; x < image.bufferedImage().getWidth(); x++) {
                oldRed = image.red(x, y);
                oldGreen = image.green(x, y);
                oldBlue = image.blue(x, y);
                newRed = ColorUtils.findClosestColor(oldRed, kv[0]);
                newGreen = ColorUtils.findClosestColor(oldGreen, kv[1]);
                newBlue = ColorUtils.findClosestColor(oldBlue, kv[2]);
                int[] error = {oldRed - newRed, oldGreen - newGreen, oldBlue - newBlue};
                image.setColor(x, y, ColorUtils.rgb(newRed, newGreen, newBlue));
                setError(image, x, y, 0, 1, error);
                setError(image, x, y, 1, 0, error);
                setError(image, x, y, -1, 1, error);
                setError(image, x, y, 1, 1, error);

            }
        }
        return image.bufferedImage();
    }
}
