package model.filter.mikhail;

import core.filter.CustomFilter;
import core.filter.Image;
import model.filter.leonid.ColorUtils;

import java.awt.image.BufferedImage;

public class WindFilter extends CustomFilter {
    private final int windStrength; // Сила ветра
    private final int threshold; // Сила ветра
    private final Direction windDirection;
    private int directionCoeff = 0;

    public WindFilter(Direction windDirection, int windStrength, int threshold) {

        this.threshold = threshold;
        this.windStrength = windStrength;
        this.windDirection = windDirection;
    }

    @Override
    protected BufferedImage apply(Image image) {
        switch (windDirection) {
            case TOP -> {
                directionCoeff = 1;
                return verticalWind(image);
            }
            case BOTTOM -> {
                directionCoeff = -1;
                return verticalWind(image);
            }
            case RIGHT -> {
                directionCoeff = 1;
                return horizontalWind(image);
            }
            case LEFT -> {
                directionCoeff = -1;
                return horizontalWind(image);
            }
            default -> {
                return image.bufferedImage();
            }
        }
    }

    private BufferedImage verticalWind(Image image) {
        int width = image.width();
        int height = image.height();
        Image result = Image.copyOf(image);
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {
                if (Math.random() < 0.3) {
                    continue;
                }
                int randomStrength = windStrength + (int) (windStrength * (width / 100) * Math.random());
                int firstPix = result.color(u, v);
                int lastPix = firstPix;
                int pix = firstPix;

                int i;
                for (i = 1; i <= randomStrength; i++) {
                    if (v + i * directionCoeff < 0) {
                        pix = result.color(u, 0);
                    } else if (v + i * directionCoeff >= height) {
                        pix = result.color(u, height - 1);
                    } else {
                        pix = result.color(u, v + i * directionCoeff);
                    }
                    int lumaDif = lumaDifference(pix, lastPix);
                    lastPix = pix;
                    if (lumaDif > threshold) {
                        break;
                    }
                }
                int lumaDif = lumaDifference(firstPix, lastPix);
                if (lumaDif <= threshold) {
                    continue;
                }
                for (int j = 0; j <= i; j++) {
                    int blendedPixel = lerp(pix, firstPix, randomStrength, j);
                    if (v + j * directionCoeff >= 0 && v + j * directionCoeff < height) {
                        result.setColor(u, v + j * directionCoeff, getAverageColor(blendedPixel, image.color(u, v + j * directionCoeff)));
                    }
                }

            }
        }
        return result.bufferedImage();
    }

    private BufferedImage horizontalWind(Image image) {
        int width = image.width();
        int height = image.height();
        Image result = Image.copyOf(image);
        for (int u = 0; u < width; u++) {
            for (int v = 0; v < height; v++) {

                if (Math.random() < 0.3) {
                    continue;
                }
                int randomStrength = windStrength + (int) (windStrength * (width / 100) * Math.random());
                int firstPix = result.color(u, v);
                int lastPix = firstPix;
                int pix = firstPix;

                int i;
                for (i = 1; i <= randomStrength; i++) {
                    if (u + i * directionCoeff < 0) {
                        pix = result.color(0, v);
                    } else if (u + i * directionCoeff >= width) {
                        pix = result.color(width - 1, v);
                    } else {
                        pix = result.color(u + i * directionCoeff, v);
                    }
                    int lumaDif = lumaDifference(pix, lastPix);
                    lastPix = pix;
                    if (lumaDif > threshold) {
                        break;
                    }
                }
                int lumaDif = lumaDifference(firstPix, lastPix);
                if (lumaDif <= threshold) {
                    continue;
                }
                for (int j = 0; j <= i; j++) {
                    int blendedPixel = lerp(pix, firstPix, randomStrength, j);
                    if (u + j * directionCoeff >= 0 && u + j * directionCoeff < width) {
                        result.setColor(u + j * directionCoeff, v, blendedPixel);
                    }
                }

            }
        }
        return result.bufferedImage();
    }

    public static int getAverageColor(int color1, int color2) {
        int alpha = ColorUtils.alpha(color1);
        int red1 = ColorUtils.red(color1);
        int green1 = ColorUtils.green(color1);
        int blue1 = ColorUtils.blue(color1);
        int red2 = ColorUtils.red(color2);
        int green2 = ColorUtils.green(color2);
        int blue2 = ColorUtils.blue(color2);
        int averageRed = (3 * red1 + red2) / 4;
        int averageGreen = (3 * green1 + green2) / 4;
        int averageBlue = (3 * blue1 + blue2) / 4;
        return ColorUtils.rgb(averageRed, averageGreen, averageBlue, alpha);
    }


    // Функция для вычисления luma
    private static int luma(int rgb) {
        int r = ColorUtils.red(rgb);
        int g = ColorUtils.green(rgb);
        int b = ColorUtils.blue(rgb);
        return (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
    }

    // Функция для вычисления разницы luma
    private static int lumaDifference(int pix1, int pix2) {
        return Math.abs(luma(pix1) - luma(pix2));
    }

    // Функция для линейной интерполяции
    private static int lerp(int pix1, int pix2, int distance, int step) {

        // Разница между цветами
        int alphaDiff = ColorUtils.alpha(pix2) - ColorUtils.alpha(pix1);
        int redDiff = ColorUtils.red(pix2) - ColorUtils.red(pix1);
        int greenDiff = ColorUtils.green(pix2) - ColorUtils.green(pix1);
        int blueDiff = ColorUtils.blue(pix2) - ColorUtils.blue(pix1);

        // Интерполяция каждого компонента цвета
        int alpha = ColorUtils.alpha(pix1) + (alphaDiff * step) / distance;
        int red = ColorUtils.red(pix1) + (redDiff * step) / distance;
        int green = ColorUtils.green(pix1) + (greenDiff * step) / distance;
        int blue = ColorUtils.blue(pix1) + (blueDiff * step) / distance;

        // Сборка int значения цвета
        return ColorUtils.rgb(red, green, blue, alpha);
    }

    public enum Direction {TOP, BOTTOM, LEFT, RIGHT}
}
