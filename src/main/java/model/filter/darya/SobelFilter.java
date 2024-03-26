package model.filter.darya;

import core.filter.Image;
import core.filter.MatrixFilter;
import model.filter.leonid.ColorUtils;

public class SobelFilter extends MatrixFilter {
    int binarize;
    public SobelFilter(int binarize) {
        this.binarize = binarize;
    }
    @Override
    protected int apply(Image image, int x, int y) {
        int oldpix = image.color(x, y);
        int alpha = ColorUtils.alpha(oldpix);
        float[][] koef = {{1.0f, 2.0f, 1.0f}, {0.0f, 0.0f, 0.0f}, {-1.0f, -2.0f, -1.0f}};
        float[][] koef2 = {{-1.0f, 0.0f, 1.0f}, {-2.0f, 0.0f, 2.0f}, {-1.0f, 0.0f, 1.0f}};
        float curColor;
        float resultColor1 = 0;
        float resultColor2 = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                curColor = ColorUtils.getBrightness(image.color(x + i, y + j));
                float k = koef[i + 1][j + 1];
                curColor *= k;
                resultColor1 += curColor;
            }
        }
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                curColor = ColorUtils.getBrightness(image.color(x + i, y + j));
                float k = koef2[i + 1][j + 1];
                curColor *= k;
                resultColor2 += curColor;
            }
        }
        int resColor = (int)Math.pow(resultColor1 * resultColor1 + resultColor2 * resultColor2, 0.5);
        resColor = Math.max(resColor, 0);
        resColor = Math.min(resColor, 255);
        if (resColor > binarize)
            resColor = 255;
        else
            resColor = 0;
        return ColorUtils.rgb(resColor, resColor, resColor, alpha);
    }
}
