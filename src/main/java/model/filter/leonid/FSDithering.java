package model.filter.leonid;

import core.filter.Image;
import core.filter.MatrixFilter;


public class FSDithering extends MatrixFilter {

    int quantizationRed, quantizationGreen, quantizationBlue;
    public FSDithering(int quantizationRed, int quantizationGreen, int quantizationBlue) {
        this.quantizationRed = quantizationRed;
        this.quantizationBlue = quantizationBlue;
        this.quantizationGreen = quantizationGreen;
    }

    @Override
    protected int apply(Image image, int x, int y) {
        return 0;
    }
}
