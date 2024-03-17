package model;

import java.awt.*;



public class BloomFilter extends ICGFilter{

    private final double glowFactor;
    double threshold;

    static Pattern pattern = new Pattern(new Point(-1, -1), new Point(1, 1));

    public BloomFilter(double glowFactor, double threshold) {
        super(pattern);
        this.glowFactor = glowFactor;
        this.threshold = threshold;
    }

    private double getBrightness(int red, int green, int blue) {


        // Нормализуем значения каналов к диапазону [0, 1]
        double normalizedRed = red / 255.0;
        double normalizedGreen = green / 255.0;
        double normalizedBlue = blue / 255.0;

        // Вычисляем яркость (среднее арифметическое)
        return (normalizedRed + normalizedGreen + normalizedBlue) / 3.0;
    }


    @Override
    public int apply(MatrixView matrixView) {
        int pivotRGB =  matrixView.get(0, 0);
        int pivotRed = ColorRGB.getRed(pivotRGB);
        int pivotGreen = ColorRGB.getGreen(pivotRGB);
        int pivotBlue = ColorRGB.getBlue(pivotRGB);

        int resultRed = 0;
        int resultGreen = 0;
        int resultBlue = 0;



        if (getBrightness(pivotRed, pivotGreen, pivotBlue) >= threshold) {
            resultRed = Math.min((int)(pivotRed * glowFactor) ,255);
            resultGreen = Math.min((int)(pivotGreen * glowFactor), 255);
            resultBlue = Math.min((int)(pivotBlue * glowFactor), 255);
        }

        return (255 << 24) | (resultRed << 16) | (resultGreen << 8) | resultBlue;
    }
}
