package misc;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Provides a wrapper for safe access to a part of an image defined by start and end points.
 * This allows working with a submatrix of the image as an independent object.
 * <p>
 * The pivot point is assumed to be in coordinates (0,0).
 */
public class MatrixView {
    private final BufferedImage image;
    private final Pattern pattern;

    private final Point pivot;

    /**
     * Constructor for MatrixView.
     *
     * @param image   The source image to access.
     * @param pattern The model.filter matrix pattern.
     * @param pivot   The pivot point of the view. Must be a valid point in the image.
     */
    public MatrixView(BufferedImage image, Pattern pattern, Point pivot) {
        this.image = image;
        this.pattern = pattern;
        this.pivot = pivot;
    }

    Point getPivot() {
        return pivot;
    }

    /**
     * Returns the RGB value of a pixel at given coordinates.
     *
     * @param x The X coordinate relative to the start point of the submatrix.
     * @param y The Y coordinate relative to the start point of the submatrix.
     * @return The RGB value of the pixel.
     */
    public int get(int x, int y) {
        return orElseDefault(x, y, 0);
    }

    /**
     * Throws IndexOutOfBoundsException if the specified coordinates are outside the submatrix bounds.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return RGB value of the pixel.
     */
    public int orElseThrow(int x, int y) {
        Integer result = safeAccess(x, y);
        if (result == null) {
            throw new IndexOutOfBoundsException(
                    "absolute x or y is out of the image bounds: x=" + x + ", y=" + y + ", width=" + image.getWidth() + ", height=" + image.getHeight() + ", pivot=(" + pivot.x + "," + pivot.y + ")"
            );
        }
        return result;
    }

    /**
     * Returns the RGB value of a pixel at given coordinates or a default value
     * if the specified coordinates are outside the submatrix bounds.
     *
     * @param x          The X coordinate relative to the start point of the submatrix.
     * @param y          The Y coordinate relative to the start point of the submatrix.
     * @param defaultVal The default value.
     * @return The RGB value of the pixel or the default value.
     */
    private int orElseDefault(int x, int y, int defaultVal) {
        Integer result = safeAccess(x, y);
        return result != null ? result : defaultVal;
    }

    private Integer safeAccess(int x, int y) {
        if (!isWithinBounds(x, y)) return null;

        int absoluteX = pivot.x + x;
        int absoluteY = pivot.y + y;

        if (absoluteX >= 0 && absoluteX < image.getWidth() && absoluteY >= 0 && absoluteY < image.getHeight()) {
            return image.getRGB(absoluteX, absoluteY);
        } else {
            return null;
        }
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= pattern.start().x && x <= pattern.end().x && y >= pattern.start().y && y <= pattern.end().y;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(pivot).append("\n");
        for (int y = pattern.start().y; y <= pattern.end().y; y++) {
            for (int x = pattern.start().x; x <= pattern.end().x; x++) {
                int rgb = orElseDefault(x, y, 0);
                builder.append(String.format("%08X ", rgb));
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
