package model.filter.eric;

import core.filter.Image;

import java.awt.image.BufferedImage;

public class BilinearInterpolation extends ResamplingFilter {

    public BilinearInterpolation(int targetWidth, int targetHeight) {
        super(targetWidth, targetHeight);
    }

    public static BufferedImage resizeImage(Image originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.type());

        double xRatio = (double) originalImage.width() / targetWidth;
        double yRatio = (double) originalImage.height() / targetHeight;

        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                double xDiff = (x * xRatio) - (int) (x * xRatio);
                double yDiff = (y * yRatio) - (int) (y * yRatio);
                int xIndex = (int) (x * xRatio);
                int yIndex = (int) (y * yRatio);

                int a = originalImage.color(Math.min(xIndex, originalImage.width() - 1), Math.min(yIndex, originalImage.height() - 1));
                int b = originalImage.color(Math.min(xIndex + 1, originalImage.width() - 1), Math.min(yIndex, originalImage.height() - 1));
                int c = originalImage.color(Math.min(xIndex, originalImage.width() - 1), Math.min(yIndex + 1, originalImage.height() - 1));
                int d = originalImage.color(Math.min(xIndex + 1, originalImage.width() - 1), Math.min(yIndex + 1, originalImage.height() - 1));

                int pixel = bilinearInterpolate(a, b, c, d, xDiff, yDiff);
                resizedImage.setRGB(x, y, pixel);
            }
        }
        return resizedImage;
    }

    private static int bilinearInterpolate(int a, int b, int c, int d, double xDiff, double yDiff) {
        int red = (int) ((a >> 16 & 0xff) * (1 - xDiff) * (1 - yDiff) + (b >> 16 & 0xff) * xDiff * (1 - yDiff) +
                (c >> 16 & 0xff) * yDiff * (1 - xDiff) + (d >> 16 & 0xff) * (xDiff * yDiff));
        int green = (int) ((a >> 8 & 0xff) * (1 - xDiff) * (1 - yDiff) + (b >> 8 & 0xff) * xDiff * (1 - yDiff) +
                (c >> 8 & 0xff) * yDiff * (1 - xDiff) + (d >> 8 & 0xff) * (xDiff * yDiff));
        int blue = (int) ((a & 0xff) * (1 - xDiff) * (1 - yDiff) + (b & 0xff) * xDiff * (1 - yDiff) +
                (c & 0xff) * yDiff * (1 - xDiff) + (d & 0xff) * (xDiff * yDiff));

        return 0xff000000 | (clip(red) << 16) | (clip(green) << 8) | clip(blue);
    }

    private static int clip(int value) {
        return Math.min(Math.max(value, 0), 255);
    }

    @Override
    protected BufferedImage apply(Image image) {
        return resizeImage(image, targetWidth, targetHeight);
    }
}