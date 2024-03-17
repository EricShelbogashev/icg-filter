package model;

public class ColorRGB {
    static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    static int getBlue(int rgb) {
        return rgb & 0xFF;
    }

    static int getRGB(int red, int green, int blue) {
        return (255 << 24) | (red << 16) | (green << 8) | (blue);
    }

    static double getBrightness(int rgb) {
        int red = (rgb >> 16) & 0xFF; // Получаем значение красного канала
        int green = (rgb >> 8) & 0xFF; // Получаем значение зеленого канала
        int blue = rgb & 0xFF; // Получаем значение синего канала

        // Нормализуем значения каналов к диапазону [0, 1]
        double normalizedRed = red / 255.0;
        double normalizedGreen = green / 255.0;
        double normalizedBlue = blue / 255.0;

        // Вычисляем яркость (среднее арифметическое)
        return (normalizedRed + normalizedGreen + normalizedBlue) / 3.0;
    }
}
