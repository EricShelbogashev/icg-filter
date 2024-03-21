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

    /*@Override
    protected int apply(Image image, int x, int y) {
        int oldRed = image.red(x, y);
        int oldGreen = image.green(x, y);
        int oldBlue = image.blue(x, y);
        float errRed = 0;
        float errGreen = 0;
        float errBlue = 0;
        int[] neighbours = {image.color(x - 1, y), image.color(x + 1, y -1), image.color(x, y-1), image.color(x-1, y-1)};
        for (int i = 0; i < 4; i++){
            errRed += (oldRed - ColorUtils.red(neighbours[i])) * koef[i];
            errGreen += (oldGreen - ColorUtils.green(neighbours[i])) * koef[i];
            errBlue += (oldBlue- ColorUtils.blue(neighbours[i])) * koef[i];
        }
        int resultRed = ColorUtils.findClosestColor(oldRed + (int)errRed, quantizationRed);
        int resultGreen = ColorUtils.findClosestColor(oldGreen + (int)errGreen, quantizationGreen);
        int resultBlue = ColorUtils.findClosestColor(oldBlue + (int)errBlue, quantizationBlue);
        return ColorUtils.rgb(resultRed, resultGreen, resultBlue, image.alpha(x,y));
    }*/

    /*for each y from top to bottom do
            for each x from left to right do
    oldpixel := pixels[x][y]
    newpixel := find_closest_palette_color(oldpixel)
    pixels[x][y] := newpixel
    quant_error := oldpixel - newpixel
    pixels[x + 1][y    ] := pixels[x + 1][y    ] + quant_error × 7 / 16
    pixels[x - 1][y + 1] := pixels[x - 1][y + 1] + quant_error × 3 / 16
    pixels[x    ][y + 1] := pixels[x    ][y + 1] + quant_error × 5 / 16
    pixels[x + 1][y + 1] := pixels[x + 1][y + 1] + quant_error × 1 / 16*/


    float chooseFactor(int xOffset, int yOffset) {
        if (xOffset == 1 && yOffset == 0) {
            return 7.0f / 16;
        }
        else if (xOffset == -1 && yOffset == 1) {
            return 3.0f / 16;
        }
        else if (xOffset == 0 && yOffset == 1) {
            return 5.0f / 16;
        }

        else if (xOffset == 1 && yOffset == 1) {
            return 1.0f / 16;
        }

        return 0;

    }
    void setError(Image image, int x, int y, int xOffset, int yOffset, int[] quantError) {
        int red = image.red(x + xOffset, y + yOffset);
        red += (int) (quantError[0] * chooseFactor(xOffset, yOffset));

        int green = image.green(x + xOffset, y + yOffset);
        green += (int) (quantError[1] * chooseFactor(xOffset, yOffset));

        int blue = image.blue(x + xOffset, y + yOffset);
        blue += (int) (quantError[1] * chooseFactor(xOffset, yOffset));

        image.setColor(x + xOffset, y + yOffset, ColorUtils.rgb(red, green, blue));
    }

    @Override
    protected BufferedImage apply(Image image) {
        for (int x = 0; x < image.bufferedImage().getWidth(); x++) {
            for (int y = 0; y < image.bufferedImage().getHeight(); y++) {
                int oldRed = image.red(x,y);
                int oldGreen = image.green(x, y);
                int oldBlue = image.blue(x, y);

                int newRed = ColorUtils.findClosestColor(oldRed, quantizationRed);
                int newGreen = ColorUtils.findClosestColor(oldGreen, quantizationGreen);
                int newBlue = ColorUtils.findClosestColor(oldBlue, quantizationBlue);

                int[] quantError = {oldRed - newRed, oldGreen - newGreen, oldBlue - newBlue};

                image.setColor(newRed, newGreen, newBlue);

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
