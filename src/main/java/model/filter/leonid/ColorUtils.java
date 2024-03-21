package model.filter.leonid;

public class ColorUtils {
    static public int red(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    static public int green(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    static public int blue(int rgb) {
        return rgb & 0xFF;
    }

    static public int rgb(int red, int green, int blue) {
        return (255 << 24) | (red << 16) | (green << 8) | (blue);
    }

    static public int rgb(int red, int green, int blue, int alpha) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    static public int alpha(int rgb) {
        return (rgb >> 24) & 0xFF;
    }


    // Returns brightness value [0, 1]
    static public double getNormalizeBrightness(int rgb) {

        // Normalize chanel values to [0, 1]
        double normalizedRed = red(rgb) / 255.0;
        double normalizedGreen = green(rgb) / 255.0;
        double normalizedBlue = blue(rgb) / 255.0;

        // Calculate brightness
        return (normalizedRed + normalizedGreen + normalizedBlue) / 3.0;
    }

    static public int invert(int rgb) {
        return rgb(255 - red(rgb), 255 - green(rgb), 255 - blue(rgb), alpha(rgb));
    }

    static public int getMonochrome(int rgb) {

        return rgb((int) (0.299 * red(rgb)), (int) (0.587 * green(rgb)), (int) (0.114 * blue(rgb)), alpha(rgb));
    }

    static public int getBrightness(int rgb) {
        return (red(rgb) + green(rgb) + blue(rgb)) / 3;
    }

    public static int sum(int rgb1, int rgb2) {
        int red = Math.min(ColorUtils.red(rgb2) + ColorUtils.red(rgb1), 255);
        int green = Math.min(ColorUtils.green(rgb2) + ColorUtils.green(rgb1), 255);
        int blue = Math.min(ColorUtils.blue(rgb2) + ColorUtils.blue(rgb1), 255);

        return ColorUtils.rgb(red, green, blue);
    }


    public static int findClosestColor(int color, int quantizationDeg) {
        if (color < 0)
            return 0;
        if (color > 255)
            return 255;
        float del = (float) (quantizationDeg - 1);
        return (int) ((float) (Math.round((float) color / 255 * del)) / del * 255);
    }
}
