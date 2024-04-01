package model.filter.mikhail;

import core.filter.Image;
import core.filter.MatrixFilter;
import model.filter.leonid.ColorUtils;

import static model.filter.leonid.OrderedDithering.*;
import static model.filter.leonid.OrderedDithering.ditherMatrix2;

public class MikhailOrderedDither extends MatrixFilter {
    static class DitherMatrix {
        int[][] matrix;
        int size;
        int normalizer;

        public DitherMatrix(int quantizationRank) {
            QuantizationRank rank = QuantizationRank.fromRank(quantizationRank);
            this.size = rank.getSize();
            this.matrix = rank.getMatrix();
            this.normalizer = rank.getNormalizer();
        }

        public int findDitherColor(int colorValue, int x, int y) {
            int ditherValue = matrix[x % size][y % size] - normalizer;
            return ColorUtils.findClosestColor(colorValue + ditherValue, size + 1);
        }
    }

    static class QuantizationRank {

        private final int[][] matrix;
        private final int rank;
        private final int size;
        private final int normalizer;
        private QuantizationRank(int rank, int size, int[][] matrix, int normalizer) {
            this.matrix = matrix;
            this.rank = rank;
            this.size = size;
            this.normalizer = normalizer;
        }

        private final static QuantizationRank quantizationRank2 = new QuantizationRank(2, 16, ditherMatrix16, 128);
        private final static QuantizationRank quantizationRank4 = new QuantizationRank(4, 8, ditherMatrix8, 32);
        private final static QuantizationRank quantizationRank64 = new QuantizationRank(64, 4, ditherMatrix4, 8);
        private final static QuantizationRank quantizationRank128 = new QuantizationRank(128, 2, ditherMatrix2, 2);

        public static QuantizationRank fromRank(int rank) {
            if (rank >= quantizationRank128.rank) {
                return quantizationRank128;
            } else if (rank >= quantizationRank64.rank) {
                return quantizationRank64;
            } else if (rank >= quantizationRank4.rank) {
                return quantizationRank4;
            } else {
                return quantizationRank2;
            }
        }

        public int getSize() {
            return size;
        }

        public int[][] getMatrix() {
            return matrix;
        }

        public int getNormalizer() {
            return normalizer;
        }
    }


    private final DitherMatrix redDitherMatrix;
    private final DitherMatrix greenDitherMatrix;
    private final DitherMatrix blueDitherMatrix;

    public MikhailOrderedDither(int redQuantizationRank, int greenQuantizationRank, int blueQuantizationRank) {
        this.redDitherMatrix = new DitherMatrix(redQuantizationRank);
        this.greenDitherMatrix = new DitherMatrix(greenQuantizationRank);
        this.blueDitherMatrix = new DitherMatrix(blueQuantizationRank);
    }

    @Override
    protected int apply(Image image, int x, int y) {
        int pixelRGB = image.color(x, y);
        int red = ColorUtils.red(pixelRGB);
        int blue = ColorUtils.blue(pixelRGB);
        int green = ColorUtils.green(pixelRGB);

        int newRed = redDitherMatrix.findDitherColor(red, x, y);
        int newGreen = greenDitherMatrix.findDitherColor(green, x, y);
        int newBlue = blueDitherMatrix.findDitherColor(blue, x, y);

        return ColorUtils.rgb(newRed, newGreen, newBlue);
    }
}
