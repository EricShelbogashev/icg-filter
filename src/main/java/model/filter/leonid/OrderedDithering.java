package model.filter.leonid;

import core.filter.Image;
import core.filter.MatrixFilter;

public class OrderedDithering extends MatrixFilter {

    public enum MatrixOption {two, four, eight}

    static int[][] ditherMatrix8 = {
            {0, 32, 8, 40, 2, 34, 10, 42},
            {48, 16, 56, 24, 50, 18, 58, 26},
            {12, 44, 4, 36, 14, 46, 6, 38},
            {60, 28, 52, 20, 62, 30, 54, 22},
            {3, 35, 11, 43, 1, 33, 9, 41},
            {51, 19, 59, 27, 49, 17, 57, 25},
            {15, 47, 7, 39, 13, 45, 5, 37},
            {63, 31, 55, 23, 61, 29, 53, 21}
    };

    static int[][] ditherMatrix4 = {
            {0, 8, 2, 10},
            {12, 4, 14, 6},
            {3, 11, 1, 9},
            {15, 7, 13, 5}
    };

    static int[][] ditherMatrix2 = {
            {0,2},
            {3, 1}
    };

    int redQuantizationRank, greenQuantizationRank, blueQuantizationRank;

    int matrixSize;

    int[][] ditherMatrix;

    private static int getMatrixSize(OrderedDithering.MatrixOption matrixOption) {
        return switch (matrixOption) {
            case two -> 2;
            case four -> 4;
            case eight -> 8;
        };
    }

    private static int[][] getDitherMatrix(MatrixOption matrixOption) {
        return switch (matrixOption) {
            case two -> ditherMatrix2;
            case four -> ditherMatrix4;
            case eight -> ditherMatrix8;
        };
    }

    private static int[][] getDitherMatrix(int matrixSize) {
        return switch (matrixSize) {
            case 2 -> ditherMatrix2;
            case 4 -> ditherMatrix4;
            case 8 -> ditherMatrix8;
            default -> throw new IllegalStateException("Unexpected value: " + matrixSize);
        };
    }


    public OrderedDithering(int redQuantizationRank, int greenQuantizationRank,
                            int blueQuantizationRank) {
        this.redQuantizationRank = redQuantizationRank;
        this.greenQuantizationRank = greenQuantizationRank;
        this.blueQuantizationRank = blueQuantizationRank;

        int minQuantizationRank = Math.min(redQuantizationRank, greenQuantizationRank);
        minQuantizationRank = Math.min(minQuantizationRank, blueQuantizationRank);

        if (minQuantizationRank >= 64) {
            matrixSize = 2;
            ditherMatrix = ditherMatrix2;
        }
        else if (minQuantizationRank >= 16) {
            matrixSize = 4;
            ditherMatrix = ditherMatrix4;
        }
        else {
            matrixSize = 8;
            ditherMatrix = ditherMatrix8;
        }
    }

    @Override
    protected int apply(Image image, int x, int y) {
        int pixelColor = image.color(x, y);
        int red = ColorUtils.red(pixelColor);
        int green = ColorUtils.green(pixelColor);
        int blue = ColorUtils.blue(pixelColor);

        // Применение дизеринга к каждому каналу цвета
        int newRed = ColorUtils.findClosestColor(red + ditherMatrix[x % matrixSize][y % matrixSize], redQuantizationRank);
        int newGreen = ColorUtils.findClosestColor(green + ditherMatrix[x % matrixSize][y % matrixSize], greenQuantizationRank);
        int newBlue = ColorUtils.findClosestColor(blue + ditherMatrix[x % matrixSize][y % matrixSize], blueQuantizationRank);
        pixelColor = ColorUtils.rgb(newRed, newGreen, newBlue);
        return pixelColor;
    }
}
