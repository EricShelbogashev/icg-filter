package model.filter.mikhail;

import core.filter.CustomFilter;
import core.filter.Image;
import model.filter.darya.GaussianBlurFilter;

import java.awt.image.BufferedImage;

public class WatercolorFilter extends CustomFilter {

    @Override
    protected BufferedImage apply(Image image) {
//        applyFilters(new GaussianBlurFilter(window_size)
        return image.bufferedImage();
    }

//    @Override
//    protected BufferedImage apply(Image image) {
//        // Создаем новое изображение
//        BufferedImage watercolorImage = new BufferedImage(image.width(), image.height(), BufferedImage.TYPE_INT_ARGB);
//
//        // Получаем массив пикселей
//        int[] pixels = getPixels(image.bufferedImage());
//
//        // Применяем фильтр к каждому пикселю
//        for (int i = 0; i < pixels.length; i++) {
//            int pixel = pixels[i];
//
//            // Разбиваем пиксель на компоненты RGB
//            int alpha = (pixel >> 24) & 0xFF;
//            int red = (pixel >> 16) & 0xFF;
//            int green = (pixel >> 8) & 0xFF;
//            int blue = pixel & 0xFF;
//
//            // Добавляем шум к компонентам RGB
//            red += (int) (Math.random() * 20 - 10);
//            green += (int) (Math.random() * 20 - 10);
//            blue += (int) (Math.random() * 20 - 10);
//
//            // Ограничиваем значения RGB
//            red = Math.max(0, Math.min(255, red));
//            green = Math.max(0, Math.min(255, green));
//            blue = Math.max(0, Math.min(255, blue));
//
//            // Снова объединяем компоненты RGB в пиксель
//            pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
//        }
//
//        // Задаем новые пиксели в новое изображение
//        watercolorImage.setRGB(0, 0, image.width(), image.height(), pixels, 0, image.width());
//
//        return watercolorImage;
//    }
//
//    public int[] getPixels(BufferedImage image) {
//
//        int width = image.getWidth();
//        int height = image.getHeight();
//        int[] pixels = new int[width * height];
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                pixels[y * width + x] = image.getRGB(x, y);
//            }
//        }
//
//        return pixels;
//    }
}
