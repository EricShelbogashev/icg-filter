package model.filter.leonid;

import core.filter.Image;
import core.filter.MatrixFilter;

public class OrderedDithering extends MatrixFilter {

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

    static int[][] ditherMatrix16 = {
            {0, 128, 32, 160, 8, 136, 40, 168, 2, 130, 34, 162, 10, 138, 42, 170},
            {192, 64, 224, 96, 200, 72, 232, 104, 194, 66, 226, 98, 202, 74, 234, 106},
            {48, 176, 16, 144, 56, 184, 24, 152, 50, 178, 18, 146, 58, 186, 26, 154},
            {240, 112, 208, 80, 248, 120, 216, 88, 242, 114, 210, 82, 250, 122, 218, 90},
            {12, 140, 44, 172, 4, 132, 36, 164, 14, 142, 46, 174, 6, 134, 38, 166},
            {204, 76, 236, 108, 196, 68, 228, 100, 206, 78, 238, 110, 198, 70, 230, 102},
            {60, 188, 28, 156, 52, 180, 20, 148, 62, 190, 30, 158, 54, 182, 22, 150},
            {252, 124, 220, 92, 244, 116, 212, 84, 254, 126, 222, 94, 246, 118, 214, 86},
            {3, 131, 35, 163, 11, 139, 43, 171, 1, 129, 33, 161, 9, 137, 41, 169},
            {195, 67, 227, 99, 203, 75, 235, 107, 193, 65, 225, 97, 201, 73, 233, 105},
            {51, 179, 19, 147, 59, 187, 27, 155, 49, 177, 17, 145, 57, 185, 25, 153},
            {243, 115, 211, 83, 251, 123, 219, 91, 241, 113, 209, 81, 249, 121, 217, 89},
            {15, 143, 47, 175, 7, 135, 39, 167, 13, 141, 45, 173, 5, 133, 37, 165},
            {207, 79, 239, 111, 199, 71, 231, 103, 205, 77, 237, 109, 197, 69, 229, 101},
            {63, 191, 31, 159, 55, 183, 23, 151, 61, 189, 29, 157, 53, 181, 21, 149},
            {255, 127, 223, 95, 247, 119, 215, 87, 253, 125, 221, 93, 245, 117, 213, 85}
    };
    static int[][] ditherMatrix4 = {
            {0, 8, 2, 10},
            {12, 4, 14, 6},
            {3, 11, 1, 9},
            {15, 7, 13, 5}
    };
    static int[][] ditherMatrix2 = {
            {0, 2},
            {3, 1}
    };
    int redQuantizationRank, greenQuantizationRank, blueQuantizationRank;
    int matrixSize;

    int normalizer;
    int[][] ditherMatrix;

    public OrderedDithering(int redQuantizationRank, int greenQuantizationRank,
                            int blueQuantizationRank) {
        this.redQuantizationRank = redQuantizationRank;
        this.greenQuantizationRank = greenQuantizationRank;
        this.blueQuantizationRank = blueQuantizationRank;

        int minQuantizationRank = Math.min(redQuantizationRank, greenQuantizationRank);
        minQuantizationRank = Math.min(minQuantizationRank, blueQuantizationRank);

        if (minQuantizationRank == 2) {
            matrixSize = 16;
            ditherMatrix = ditherMatrix16;
            normalizer = 128;
        }
        else if (minQuantizationRank >= 64) {
            matrixSize = 2;
            ditherMatrix = ditherMatrix2;
            normalizer = 2;
        } else if (minQuantizationRank >= 16) {
            matrixSize = 4;
            ditherMatrix = ditherMatrix4;
            normalizer = 8;
        } else {
            // quantization rank = 4
            matrixSize = 8;
            ditherMatrix = ditherMatrix8;
            normalizer = 32;
        }
    }

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

    @Override
    protected int apply(Image image, int x, int y) {
        int pixelColor = image.color(x, y);
        int red = ColorUtils.red(pixelColor);
        int green = ColorUtils.green(pixelColor);
        int blue = ColorUtils.blue(pixelColor);

        // Применение дизеринга к каждому каналу цвета
        int newRed = ColorUtils.findClosestColor(red + ditherMatrix[x % matrixSize][y % matrixSize] - normalizer, redQuantizationRank);
        int newGreen = ColorUtils.findClosestColor(green + ditherMatrix[x % matrixSize][y % matrixSize] - normalizer, greenQuantizationRank);
        int newBlue = ColorUtils.findClosestColor(blue + ditherMatrix[x % matrixSize][y % matrixSize] - normalizer, blueQuantizationRank);
        pixelColor = ColorUtils.rgb(newRed, newGreen, newBlue);
        return pixelColor;
    }

    public enum MatrixOption {two, four, eight}
}
