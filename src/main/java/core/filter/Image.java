package core.filter;

import java.awt.image.BufferedImage;

public class Image {
    private final BufferedImage image;
    public static final int DEFAULT_ALPHA = 0xFF;

    public Image(BufferedImage image) {
        this.image = image;
    }

    public static int buildColor(int red, int green, int blue) {
        return Image.DEFAULT_ALPHA << 24 | (red << 16) | (green << 8) | blue;
    }

    public int width() {
        return image.getWidth();
    }

    public int height() {
        return image.getHeight();
    }

    public int type() {
        return image.getType();
    }

    public BufferedImage bufferedImage() {
        return image;
    }

    public int color(int x, int y) {
        int clampedX = Math.max(0, Math.min(x, image.getWidth() - 1));
        int clampedY = Math.max(0, Math.min(y, image.getHeight() - 1));
        return image.getRGB(clampedX, clampedY);
    }

    public int red(int x, int y) {
        return color(x, y) >> 16 & 0xFF;
    }

    public int green(int x, int y) {
        return color(x, y) >> 8 & 0xFF;
    }

    public int blue(int x, int y) {
        return color(x, y) & 0xFF;
    }

    public int alpha(int x, int y) {return (color(x, y) >> 24) & 0xFF;}
}
