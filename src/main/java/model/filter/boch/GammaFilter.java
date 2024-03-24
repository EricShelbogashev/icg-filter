package model.bochkarev;

import core.filter.Image;
import core.filter.MatrixFilter;
import misc.ICGFilter;
import misc.MatrixView;
import misc.Pattern;

import java.awt.*;

public class GammaFilter extends MatrixFilter
{
    int gamm = 300;

    public GammaFilter(int gamma)
    {
        this.gamm = gamma;
    }

    @Override
    protected int apply(Image image, int x, int y)
    {
        float gamma = (float) gamm / 100;
        int rgb = image.color(x, y);
        int redResult = (int) (255 * Math.pow(((rgb >> 16) & 0xFF) / (float) 255, gamma));
        int greenResult = (int) (255 * Math.pow(((rgb >> 8) & 0xFF) / (float) 255, gamma));
        int blueResult = (int) (255 * Math.pow(((rgb) & 0xFF) / (float)255, gamma));
        int alphaResult = 255;

        redResult = Math.min(Math.max(redResult, 0), 255);
        greenResult = Math.min(Math.max(greenResult, 0), 255);
        blueResult = Math.min(Math.max(blueResult, 0), 255);
        return ((int) alphaResult << 24) | ((int) redResult << 16) | ((int) greenResult << 8) | ((int) blueResult);
    }
}
