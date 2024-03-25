package model.filter.mikhail;

import core.filter.CustomFilter;
import core.filter.Image;

import java.awt.image.BufferedImage;

public class WindFilter extends CustomFilter {
    private final int windStrength; // Сила ветра
    private final int threshold; // Сила ветра
    private final Direction windDirection;

    public WindFilter(Direction windDirection, int windStrength, int threshold) {

        this.threshold = threshold;
        this.windStrength = windStrength;
        this.windDirection = windDirection;
    }

    @Override
    protected BufferedImage apply(Image image) {


        int width = image.width();
        int height = image.height();
//        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        Image result = Image.empty(width, height);
        Image result = Image.copyOf(image);
//        int randInt = new Random().nextInt(height / 2) + height / 2;
        System.out.println("THRESHOLD: " + threshold);
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
                    if (u + i < 0) {
                        pix = result.color(0, v);
                    } else if (u + i >= width) {
                        pix = result.color(width - 1, v);
                    } else {
                        pix = result.color(u + i, v);
                    }
                    int lumaDif = lumaDifference(pix, lastPix);
                    lastPix = pix;
                    System.out.println(lumaDif);
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
                    if (u + j >= 0 && u + j < width) {
                        result.setColor(u + j, v, getAverageColor(blendedPixel, image.color(u + j, v)));
//                        result.setColor(u + j, v, blendedPixel);
//                        image.setColor(u + j, v, blendedPixel);
//                        System.out.println(blendedPixel);
                    }
                }

            }
        }

//        return image.bufferedImage();
        return result.bufferedImage();
    }

    public static int getAverageColor(int color1, int color2) {
        int alpha = (color1 >> 24) & 0xFF;
        int red1 = getRed(color1);
        int green1 = getGreen(color1);
        int blue1 = getBlue(color1);
        int red2 = getRed(color2);
        int green2 = getGreen(color2);
        int blue2 = getBlue(color2);
        int averageRed = (3 * red1 + red2) / 4;
        int averageGreen = (3 * green1 + green2) / 4;
        int averageBlue = (3 * blue1 + blue2) / 4;
        return (alpha << 24) | (averageRed << 16) | (averageGreen << 8) | averageBlue;
    }


    // Функция для вычисления luma
    private static int luma(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
    }

    // Функция для вычисления разницы luma
    private static int lumaDifference(int pix1, int pix2) {
        int luma1 = luma(pix1);
        int luma2 = luma(pix2);
//        System.out.println(luma1 + " " + luma2);
        return Math.abs(luma(pix1) - luma(pix2));
    }

    // Функция для линейной интерполяции
    private static int lerp(int pix1, int pix2, int distance, int step) {

        // Разница между цветами
        int alphaDiff = (pix2 >> 24) & 0xFF - (pix1 >> 24) & 0xFF;
        int redDiff = getRed(pix2) - getRed(pix1);
        int greenDiff = getGreen(pix2) - getGreen(pix1);
        int blueDiff = getBlue(pix2) - getBlue(pix1);

        // Интерполяция каждого компонента цвета
        int alpha = (pix1 >> 24) & 0xFF + (alphaDiff * step) / distance;
        int red = getRed(pix1) + (redDiff * step) / distance;
        int green = getGreen(pix1) + (greenDiff * step) / distance;
        int blue = getBlue(pix1) + (blueDiff * step) / distance;

        // Сборка int значения цвета
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    // Вспомогательные методы для получения компонент цвета
    private static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    private static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    private static int getBlue(int color) {
        return color & 0xFF;
    }

    public enum Direction {TOP, BOTTOM, LEFT, RIGHT}
}
