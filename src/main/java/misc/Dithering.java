package misc;

import java.awt.image.BufferedImage;

public class Dithering {

    public enum MatrixOption {two, four, eight}

    // Матрица упорядоченного дизеринга 8x8
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

    private static int findClosestColor(int color, int quantizationRank) {
        int quantum = 255/quantizationRank;
        int quantCount;
        if (color != 0) {
            quantCount = color/quantum;
        }
        else {
            quantCount = 0;
        }

        return Math.min(quantCount * quantum, 255);
    }

    private static int getMatrixSize(MatrixOption matrixOption) {
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

    public static BufferedImage applyDithering(BufferedImage image, int redQuantizationRank, int greenQuantizationRank,
                                               int blueQuantizationRank, MatrixOption matrixOption) {

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[][] ditherMatrix = getDitherMatrix(matrixOption);
        int matrixSize = getMatrixSize(matrixOption);

        // Применение дизеринга к каждому пикселю изображения
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelColor = image.getRGB(x, y);
                int red = ColorRGB.getRed(pixelColor);
                int green = ColorRGB.getGreen(pixelColor);
                int blue = ColorRGB.getBlue(pixelColor);

                // Применение дизеринга к каждому каналу цвета
                int newRed = findClosestColor(red + ditherMatrix[x % matrixSize][y % matrixSize], redQuantizationRank);
                int newGreen = findClosestColor(green + ditherMatrix[x % matrixSize][y % matrixSize], greenQuantizationRank);
                int newBlue = findClosestColor(blue + ditherMatrix[x % matrixSize][y % matrixSize], blueQuantizationRank);
                pixelColor = ColorRGB.getRGB(newRed, newGreen, newBlue);

                // Установка нового цвета пикселя
                resultImage.setRGB(x, y, pixelColor);
            }
        }

        return resultImage;
    }

}