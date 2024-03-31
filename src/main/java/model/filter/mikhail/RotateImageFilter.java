package model.filter.mikhail;

import core.filter.CustomFilter;
import core.filter.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

//public class RotateImageFilter extends CustomFilter {
//
//    private final double angle;
//
//    public RotateImageFilter(double angle) {
//        this.angle = angle;
//    }
//
//    private static GraphicsConfiguration getDefaultConfiguration() {
//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice gd = ge.getDefaultScreenDevice();
//        return gd.getDefaultConfiguration();
//    }
//
//    @Override
//    protected BufferedImage apply(Image image) {
//        double sin = Math.sin(Math.toRadians(angle));
//        double cos = Math.cos(Math.toRadians(angle));
//        int w = image.width();
//        int h = image.height();
//        int newWidth = (int) Math.floor(w * cos + h * sin);
//        int newHeight = (int) Math.floor(h * cos + w * sin);
//        GraphicsConfiguration gc = getDefaultConfiguration();
//        BufferedImage result = gc.createCompatibleImage(newWidth, newHeight);
//        Graphics2D g = result.createGraphics();
//        g.setBackground(Color.WHITE);
////        g.setColor(Color.RED);
////        g.drawRect(0,0,newWidth, newHeight);
////        g.rotate(Math.toRadians(angle));
//        g.rotate(Math.toRadians(angle), (double) newWidth / 2, (double) newHeight / 2);
//        g.translate((newWidth - w) / 2, (newHeight - h) / 2);
//        g.drawRenderedImage(image.bufferedImage(), g.getTransform());
//        g.dispose();
//        return Image.of(result);
//    }
//}

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


public class RotateImageFilter extends CustomFilter {

    private final double angle;

    public RotateImageFilter(double angle) {
        this.angle = angle;
    }

    private static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

    @Override
    protected BufferedImage apply(Image image) {
        double angleInRadians = Math.toRadians(angle);
        double sin = Math.sin(angleInRadians);
        double cos = Math.cos(angleInRadians);

        int newH = (int) (image.width() * Math.abs(sin) + image.height() * Math.abs(cos));
        int newW = (int) (image.width() * Math.abs(cos) + image.height() * Math.abs(sin));

        BufferedImage newImage = new BufferedImage(newW, newH, image.bufferedImage().getType());
        Graphics2D g2d = newImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newW, newH);

        for (int x = 0; x < newW; x++) {
            for (int y = 0; y < newH; y++) {
                int newX = (int) ((x - newW / 2) * cos - (y - newH / 2) * sin) + image.bufferedImage().getWidth() / 2;
                int newY = (int) ((x - newW / 2) * sin + (y - newH / 2) * cos) + image.bufferedImage().getHeight() / 2;

                int color = 0;
                if (newX > 0 && newY > 0 && newX < image.bufferedImage().getWidth() && newY < image.bufferedImage().getHeight())
                    color = image.bufferedImage().getRGB(newX, newY);
                else
                    color = -1;

                newImage.setRGB(x, y, color);
            }
        }

        return newImage;
    }
}