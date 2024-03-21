package model.filter.leonid;

import core.filter.Image;
import core.filter.MatrixFilter;


public class BloomFilter extends MatrixFilter {

    private final double glowFactor;
    double threshold;

    public BloomFilter(double glowFactor, double threshold) {
        this.glowFactor = glowFactor;
        this.threshold = threshold;
    }

    public BloomFilter() {
        this.glowFactor = 0.3;
        this.threshold = 0.7;
    }


    @Override
    public int apply(Image image, int x, int y) {
        int resultRed = 0;
        int resultGreen = 0;
        int resultBlue = 0;

        if (ColorUtils.getNormalizeBrightness(image.color(x, y)) >= threshold) {
            resultRed = Math.min((int) (image.red(x, y) * glowFactor), 255);
            resultGreen = Math.min((int) (image.green(x, y) * glowFactor), 255);
            resultBlue = Math.min((int) (image.blue(x, y) * glowFactor), 255);
        }

        return ColorUtils.rgb(resultRed, resultGreen, resultBlue);
    }
}
