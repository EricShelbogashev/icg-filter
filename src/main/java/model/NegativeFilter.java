package model;

import java.awt.*;

public class NegativeFilter extends ICGFilter {
    static Pattern pattern  = new Pattern(new Point(0, 0), new Point(new Point(0, 0)));
    public NegativeFilter() {
        super(pattern);
    }

    @Override
    public int apply(MatrixView matrixView) {

        int rgb = matrixView.get(0, 0);

        int alpha = (rgb >> 24) & 0xFF;
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;


        int invRed = 255 - red;
        int invGreen = 255 - green;
        int invBlue = 255 - blue;
        return (alpha << 24) | (invRed << 16) | (invGreen << 8) | (invBlue);
    }
}
