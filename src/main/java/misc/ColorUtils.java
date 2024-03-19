package misc;

public class ColorUtils {
    static int red(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    static int green(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    static int blue(int rgb) {
        return rgb & 0xFF;
    }

    static int rgb(int red, int green, int blue) {
        return (255 << 24) | (red << 16) | (green << 8) | (blue);
    }
    static int rgb(int red, int green, int blue, int alpha) {return (alpha << 24) | (red << 16) | (green << 8) | blue;}

    static int alpha(int rgb) {return (255 >> 24) & 0xFF;}


    // Returns brightness value [0, 1]
    static double brightness(int rgb) {

        // Normalize chanel values to [0, 1]
        double normalizedRed = red(rgb) / 255.0;
        double normalizedGreen = green(rgb) / 255.0;
        double normalizedBlue = blue(rgb) / 255.0;

        // Calculate brightness
        return (normalizedRed + normalizedGreen + normalizedBlue) / 3.0;
    }

    static int invert(int rgb) {
        return rgb(255 - red(rgb), 255 - green(rgb), 255 - blue(rgb), alpha(rgb));
    }

    static int getMonochrome(int rgb) {

        return rgb((int) (0.299 * red(rgb)), (int) (0.587 * green(rgb)), (int) (0.114 * blue(rgb)), alpha(rgb));
    }
}
