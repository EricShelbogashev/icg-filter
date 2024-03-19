package model.filter.leonid;

import core.filter.Image;
import core.filter.MatrixFilter;

public class NegativeFilter extends MatrixFilter {
    @Override
    protected int apply(Image image, int x, int y) {
        int invRed = 255 - image.red(x, y);
        int invGreen = 255 - image.green(x, y);
        int invBlue = 255 - image.blue(x, y);

        return Image.buildColor(invRed, invGreen, invBlue);
    }
}
