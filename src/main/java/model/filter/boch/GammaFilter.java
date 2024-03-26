package model.filter.boch;

import core.filter.Image;
import core.filter.MatrixFilter;
import model.filter.leonid.ColorUtils;

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
        int redResult = (int) (255 * Math.pow(ColorUtils.red(rgb) / (float) 255, gamma));
        int greenResult = (int) (255 * Math.pow(ColorUtils.green(rgb) / (float) 255, gamma));
        int blueResult = (int) (255 * Math.pow(ColorUtils.blue(rgb) / (float) 255, gamma));

        redResult = Math.min(Math.max(redResult, 0), 255);
        greenResult = Math.min(Math.max(greenResult, 0), 255);
        blueResult = Math.min(Math.max(blueResult, 0), 255);
        return ColorUtils.rgb(redResult, greenResult, blueResult);
    }
}
