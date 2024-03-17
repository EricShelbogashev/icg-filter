package model;

import java.awt.*;

public class MonochromeFilter extends ICGFilter{

    static final private Pattern pattern = new Pattern(new Point(0, 0), new Point(0, 0));
    public MonochromeFilter() {
        super(pattern);
    }

    private int getMonochrome(int rgb) {
        int alpha = (rgb >> 24) & 0xFF;
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        // Вычисляем новое значение для оттенка серого
        int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);


        return  (alpha << 24) | (gray << 16) | (gray << 8) | gray;
    }
    @Override
    public int apply(MatrixView matrixView) {
        return getMonochrome(matrixView.get(0,0));
    }
}
