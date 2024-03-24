package model.bochkarev;

import model.ICGFilter;
import model.MatrixView;
import model.Pattern;

import java.awt.*;

public class GammaFilter extends ICGFilter
{
    double gamma = 3;

    public GammaFilter(Pattern pattern)
    {
        super(new Pattern(new Point(0, 0), new Point(0, 0)));
    }
    @Override
    public int apply(MatrixView matrixView)
    {
        int rgb = matrixView.get(0, 0);
        int redResult = (int) (255 * Math.pow(((rgb >> 16) & 0xFF) / (double)255, gamma));
        int greenResult = (int) (255 * Math.pow(((rgb >> 8) & 0xFF) / (double)255, gamma));
        int blueResult = (int) (255 * Math.pow(((rgb) & 0xFF) / (double)255, gamma));
        int alphaResult = 255;

        redResult = Math.min(Math.max(redResult, 0), 255);
        greenResult = Math.min(Math.max(greenResult, 0), 255);
        blueResult = Math.min(Math.max(blueResult, 0), 255);
        return ((int) alphaResult << 24) | ((int) redResult << 16) | ((int) greenResult << 8) | ((int) blueResult);
    }
}
