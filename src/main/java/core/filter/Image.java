package core.filter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image {
    private final BufferedImage image;
    private int defaultColor;
    public static final int DEFAULT_ALPHA = 0xFF;

    public Image(BufferedImage image, int defaultColor) {
        this.image = image;
        this.defaultColor = defaultColor;
    }

    public Image(BufferedImage image) {
        this(image, Color.BLACK.getRGB());
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

    public int defaultColor() {
        return defaultColor;
    }

    public void defaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public BufferedImage bufferedImage() {
        return image;
    }

    public int color(int x, int y) {
        return orElseDefaultColor(x, y, defaultColor);
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

    private int orElseDefaultColor(int x, int y, int defaultVal) {
        Integer result = safeAccess(x, y);
        return result != null ? result : defaultVal;
    }

    private Integer safeAccess(int x, int y) {
        if (!isWithinBounds(x, y)) return null;
        return image.getRGB(x, y);
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight();
    }
}
