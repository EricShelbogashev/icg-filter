package model.filter.leonid;

import core.filter.Image;
import core.filter.MatrixFilter;

public class MonochromeFilter extends MatrixFilter {
    @Override
    protected int apply(Image image, int x, int y) {
        // Вычисляем новое значение для оттенка серого
        int gray = (int) (
                0.299 * image.red(x, y)
                        + 0.587 * image.green(x, y)
                        + 0.114 * image.blue(x, y)
        );
        return Image.buildColor(gray, gray, gray);
    }
}
