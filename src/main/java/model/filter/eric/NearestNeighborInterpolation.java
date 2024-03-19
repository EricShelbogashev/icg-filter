package model.filter.eric;

import core.filter.Image;

import java.awt.image.BufferedImage;

public class NearestNeighborInterpolation extends ResamplingFilter {

    public NearestNeighborInterpolation(int targetWidth, int targetHeight) {
        super(targetWidth, targetHeight);
    }

    public static BufferedImage resizeImage(Image originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.type());

        double xRatio = (double) originalImage.width() / targetWidth;
        double yRatio = (double) originalImage.height() / targetHeight;

        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                int xNearest = (int) (x * xRatio);
                int yNearest = (int) (y * yRatio);

                int rgb = originalImage.color(xNearest, yNearest);
                resizedImage.setRGB(x, y, rgb);
            }
        }
        return resizedImage;
    }

    @Override
    protected BufferedImage apply(Image image) {
        return resizeImage(image, targetWidth, targetHeight);
    }
}