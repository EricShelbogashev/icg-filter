package model.filter.leonid;

import core.filter.CustomFilter;
import core.filter.Image;

import java.awt.image.BufferedImage;


public class FSDithering extends CustomFilter {

    int quantizationRed, quantizationGreen, quantizationBlue;

    public FSDithering(int quantizationRed, int quantizationGreen, int quantizationBlue) {
        this.quantizationRed = quantizationRed;
        this.quantizationBlue = quantizationBlue;
        this.quantizationGreen = quantizationGreen;
    }


    float chooseFactor(int xOffset, int yOffset) {
        if (xOffset == 1 && yOffset == 0) {
            return 7.0f / 16;
        }
        else if (xOffset == -1 && yOffset == 1) {
            return 1.0f / 16;
        }
        else if (xOffset == 0 && yOffset == 1) {
            return 5.0f / 16;
        }

        else if (xOffset == 1 && yOffset == 1) {
            return 3.0f / 16;
        }

        return 0;

    }
    void setError(Image image, int x, int y, int xOffset, int yOffset, int[] quantError) {
        int red = image.red(x + xOffset, y + yOffset);
        red += (int) (quantError[0] * chooseFactor(xOffset, yOffset));

        int green = image.green(x + xOffset, y + yOffset);
        green += (int) (quantError[1] * chooseFactor(xOffset, yOffset));

        int blue = image.blue(x + xOffset, y + yOffset);
        blue += (int) (quantError[2] * chooseFactor(xOffset, yOffset));

        image.setColor(x + xOffset, y + yOffset, ColorUtils.rgb(red, green, blue));
    }

    @Override
    protected BufferedImage apply(Image image) {
        for (int y = 0; y < image.bufferedImage().getHeight(); y++) {
            for (int x = 0; x < image.bufferedImage().getWidth(); x++) {
                int oldRed = image.red(x,y);
                int oldGreen = image.green(x, y);
                int oldBlue = image.blue(x, y);

                int newRed = ColorUtils.findClosestColor(oldRed, quantizationRed);
                int newGreen = ColorUtils.findClosestColor(oldGreen, quantizationGreen);
                int newBlue = ColorUtils.findClosestColor(oldBlue, quantizationBlue);

                int[] quantError = {oldRed - newRed, oldGreen - newGreen, oldBlue - newBlue};

                image.setColor(x, y, ColorUtils.rgb(newRed, newGreen, newBlue));

                // Spread error
                setError(image, x, y, 0, 1, quantError);
                setError(image, x, y, 1, 0, quantError);
                setError(image, x, y, -1, 1, quantError);
                setError(image, x, y, 1, 1, quantError);

            }

        }
        return image.bufferedImage();
    }
}
