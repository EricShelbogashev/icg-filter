package model.filter.darya;

import core.filter.Image;
import core.filter.MatrixFilter;
import model.filter.leonid.ColorUtils;

public class FillColorFilter extends MatrixFilter {
    public FillColorFilter() {

    }

    @Override
    protected int apply(Image image, int x, int y) {
        int alpha = image.alpha(x, y);
        return ColorUtils.rgb(
                (image.red(x, y) / 8) * 10 + 40,
                (image.green(x, y) / 8) * 20 + 40,
                (image.blue(x, y) / 8) * 40 + 40,
                alpha);
    }
}
