package model.filter.mikhail;

import core.filter.CustomFilter;
import core.filter.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RotateImageFilter extends CustomFilter {

    private double angle;

    public RotateImageFilter(double angle) {
        this.angle = angle;
    }

    @Override
    protected BufferedImage apply(Image image) {
        double sin = Math.abs(Math.sin(Math.toRadians(angle))), cos = Math.abs(Math.cos(Math.toRadians(angle)));
        int w = image.width();
        int h = image.height();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT);
        Graphics2D g = result.createGraphics();
        g.rotate(Math.toRadians(angle), (double) w / 2, (double) h / 2);
        g.translate((newWidth - w) / 2, (newHeight - h) / 2);
        g.drawRenderedImage(image.bufferedImage(), null);
        g.dispose();
        return result;
    }

    private static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }
}

//public class RotateImageFilter extends MatrixFilter {
//
//    private double angle;
//
//    public RotateImageFilter(double angle) {
//        this.angle = angle;
//    }
//
//    @Override
//    protected int apply(Image image, int x, int y) {
//
//        // Вычисление центра изображения
//        int centerX = image.width() / 2;
//        int centerY = image.height() / 2;
//
//        // Относительные координаты
//        int xPrime = x - centerX;
//        int yPrime = y - centerY;
//
//        // Матрица поворота
//        double cosTheta = Math.cos(Math.toRadians(angle));
//        double sinTheta = Math.sin(Math.toRadians(angle));
//
//        // Новые координаты
//        int xDoublePrime = (int) (xPrime * cosTheta - yPrime * sinTheta);
//        int yDoublePrime = (int) (xPrime * sinTheta + yPrime * cosTheta);
//
//        // Перенос координат обратно
//        int xNew = xDoublePrime + centerX;
//        int yNew = yDoublePrime + centerY;
//
//        // Проверка на выход за пределы изображения
//        if (xNew < 0 || xNew >= image.width() || yNew < 0 || yNew >= image.height()) {
//            return 0; // Цвет фона
//        }
//
//        return image.color(xNew, yNew);
//    }
//}