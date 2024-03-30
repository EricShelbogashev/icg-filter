package model.filter.eric;

import core.filter.Image;
import core.filter.MatrixFilter;
import model.filter.leonid.ColorUtils;

public class EricOrderedDither extends MatrixFilter {
    private final DitherMatrix redDitherMatrix;
    private final DitherMatrix greenDitherMatrix;
    private final DitherMatrix blueDitherMatrix;

    public EricOrderedDither(int redQuantizationRank, int greenQuantizationRank, int blueQuantizationRank) {
        this.redDitherMatrix = new DitherMatrix(redQuantizationRank);
        this.greenDitherMatrix = new DitherMatrix(greenQuantizationRank);
        this.blueDitherMatrix = new DitherMatrix(blueQuantizationRank);
    }

    protected int apply(Image image, int x, int y) {
        int pixelColor = image.color(x, y);

        int newRed = ditherColor(ColorUtils.red(pixelColor), redDitherMatrix, x, y);
        int newGreen = ditherColor(ColorUtils.green(pixelColor), greenDitherMatrix, x, y);
        int newBlue = ditherColor(ColorUtils.blue(pixelColor), blueDitherMatrix, x, y);

        return ColorUtils.rgb(newRed, newGreen, newBlue);
    }

    private int ditherColor(int colorValue, DitherMatrix ditherMatrix, int x, int y) {
        int ditherValue = ditherMatrix.matrix[x % ditherMatrix.size][y % ditherMatrix.size] - ditherMatrix.normalizer;
        return ColorUtils.findClosestColor(colorValue + ditherValue, ditherMatrix.size);
    }
}