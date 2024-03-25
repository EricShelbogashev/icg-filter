package model.filter.mikhail;

import core.filter.Image;
import core.filter.MatrixFilter;

import java.util.Arrays;

public class MedianFilter extends MatrixFilter {
    private final int w;

    public MedianFilter(int windowSize) {
        this.w = 2;
    }

    @Override
    protected int apply(Image image, int column, int row) {
        int[] redValues = new int[(2 * w + 1) * (2 * w + 1)];
        int[] greenValues = new int[(2 * w + 1) * (2 * w + 1)];
        int[] blueValues = new int[(2 * w + 1) * (2 * w + 1)];

        // Собираем значения пикселей из окна фильтра
        int counter = 0;
        for (int x = -w; x <= w; x++) {
            for (int y = -w; y <= w; y++) {
                int neighborX = column + x;
                int neighborY = row + y;
                if (isValidPixel(image, neighborX, neighborY)) {
                    redValues[counter] = (image.color(neighborX, neighborY) >> 16) & 0xFF;
                    greenValues[counter] = (image.color(neighborX, neighborY) >> 8) & 0xFF;
                    blueValues[counter] = (image.color(neighborX, neighborY)) & 0xFF;
                } else {
                    // Значение для пикселя за пределами изображения (например, обрезка)
                    redValues[counter] = 0;
                    greenValues[counter] = 0;
                    blueValues[counter] = 0;
                }
                counter++;
            }
        }

        // Сортируем значения для каждого канала (красный, зеленый, синий)
        Arrays.sort(redValues);
        Arrays.sort(greenValues);
        Arrays.sort(blueValues);

        // Медианное значение - центральный элемент отсортированного массива
        int medianRed = redValues[(2 * w + 1) * w];
        int medianGreen = greenValues[(2 * w + 1) * w];
        int medianBlue = blueValues[(2 * w + 1) * w];

        // Формируем итоговый пиксель
        return ((image.color(column, row) >> 24) & 0xFF) << 24 |  // Alpha остается неизменным
                (medianRed & 0xFF) << 16 |
                (medianGreen & 0xFF) << 8 |
                (medianBlue & 0xFF);
    }

    // Функция для проверки, находится ли пиксель внутри изображения
    private boolean isValidPixel(Image image, int x, int y) {
        return x >= 0 && x < image.width() && y >= 0 && y < image.height();
    }
}
