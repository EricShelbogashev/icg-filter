package model.filter.darya;

import core.filter.Image;
import core.filter.MatrixFilter;

public class FillColorFilter extends MatrixFilter {
    public FillColorFilter(){

    }
    @Override
    protected int apply(Image image, int x, int y) {
        int alpha = (image.color(x, y) >> 24) & 0xFF;
        int r = ((alpha & 0xFF) << 24) |
                (((((image.color(x, y) & 0xFF) / 2) * 30 + 40) & 0xFF) << 16) |
                (((((image.color(x, y) & 0xFF) / 2) * 10 + 40) & 0xFF) << 8) |
                (((((image.color(x, y) & 0xFF) / 2) * 20 + 40) & 0xFF));
        return r;
    }
}
