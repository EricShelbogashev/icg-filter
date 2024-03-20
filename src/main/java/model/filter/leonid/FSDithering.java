package model.filter.leonid;

import core.filter.Image;
import core.filter.MatrixFilter;


public class FSDithering extends MatrixFilter {

    int quantizationRed, quantizationGreen, quantizationBlue;
    public FSDithering(int quantizationRed, int quantizationGreen, int quantizationBlue) {
        this.quantizationRed = quantizationRed;
        this.quantizationBlue = quantizationBlue;
        this.quantizationGreen = quantizationGreen;
    }
    
    static float[] koef = {7.0f / 16, 3.0f / 16, 5.0f / 16, 1 / 16.0f};

    @Override
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
    }
}
