package model.filter.mikhail;

import core.filter.CustomFilter;
import core.filter.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MikhailFloydDither extends CustomFilter {
    private final int redQuantizationNum;
    private final int greenQuantizationNum;
    private final int blueQuantizationNum;

    private int[] rPalette;
    private int[] gPalette;
    private int[] bPalette;

    public MikhailFloydDither(int redQuantizationNum, int greenQuantizationNum, int blueQuantizationNum) {
        this.redQuantizationNum = redQuantizationNum;
        this.greenQuantizationNum = greenQuantizationNum;
        this.blueQuantizationNum = blueQuantizationNum;
    }

    private int getColor(int neighbourR, int neighbourG, int neighbourB) {
        int color;
        neighbourR = Math.max(Math.min(neighbourR, 255), 0);
        neighbourG = Math.max(Math.min(neighbourG, 255), 0);
        neighbourB = Math.max(Math.min(neighbourB, 255), 0);

        color = 255 << 24 | neighbourR << 16 | neighbourG << 8 | neighbourB;
        return color;
    }

    private void fillPalette(int[] palette, int interval, int quantNum) {
        int color = 0;
        for (int i = 0; i < quantNum; i++) {
            palette[i] = color;
            color += interval;
            if (color > 255)
                color = 255;
        }
    }

    private void createPalette() {
        rPalette = new int[redQuantizationNum];
        gPalette = new int[greenQuantizationNum];
        bPalette = new int[blueQuantizationNum];

        /* This is the step between colors in palette.
         *  Interval depends on quantization number.
         *  Quantization number is the number of colors in palette.
         */
        int interval = (int) (256 / (redQuantizationNum - 1));
        fillPalette(rPalette, interval, redQuantizationNum);

        interval = (int) (256 / (greenQuantizationNum - 1));
        fillPalette(gPalette, interval, greenQuantizationNum);

        interval = (int) (256 / (blueQuantizationNum - 1));
        fillPalette(bPalette, interval, blueQuantizationNum);
    }

    private int findColorInPalette(int[] palette, int color) {
        int min = 1000;
        int pos = 0;
        for (int i = 0; i < palette.length; i++) {
            if (Math.abs(palette[i] - color) < min) {
                min = Math.abs(palette[i] - color);
                pos = i;
            }
        }

        return palette[pos];
    }

    @Override
    protected BufferedImage apply(Image image) {
        BufferedImage newImage = new BufferedImage(image.bufferedImage().getWidth(), image.bufferedImage().getHeight(), image.bufferedImage().getType());
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(image.bufferedImage(), null, 0, 0);

        createPalette();

        for (int x = 0; x < newImage.getWidth() - 1; x++) {
            for (int y = 0; y < newImage.getHeight() - 1; y++) {
                int curColor = newImage.getRGB(x, y);
                int red = (curColor >> 16) & 0xFF;
                int green = (curColor >> 8) & 0xFF;
                int blue = curColor & 0xFF;

                int newR = findColorInPalette(rPalette, red);
                int newG = findColorInPalette(gPalette, green);
                int newB = findColorInPalette(bPalette, blue);

                int newColor = 255 << 24 | newR << 16 | newG << 8 | newB;

                newImage.setRGB(x, y, newColor);

                int errR = red - newR;
                int errG = green - newG;
                int errB = blue - newB;

                int neighbour;
                int neighbourR;
                int neighbourG;
                int neighbourB;
                int color;

                if (x < newImage.getWidth() - 1) {
                    neighbour = newImage.getRGB(x + 1, y);
                    neighbourR = (neighbour >> 16) & 0xFF;
                    neighbourG = (neighbour >> 8) & 0xFF;
                    neighbourB = neighbour & 0xFF;

                    neighbourR = (int) (neighbourR + errR * 7.0 / 16);
                    neighbourG = (int) (neighbourG + errG * 7.0 / 16);
                    neighbourB = (int) (neighbourB + errB * 7.0 / 16);

                    color = getColor(neighbourR, neighbourG, neighbourB);

                    newImage.setRGB(x + 1, y, color);
                }
                if (x > 0 && y < newImage.getHeight() - 1) {
                    neighbour = newImage.getRGB(x - 1, y + 1);
                    neighbourR = (neighbour >> 16) & 0xFF;
                    neighbourG = (neighbour >> 8) & 0xFF;
                    neighbourB = neighbour & 0xFF;

                    neighbourR = (int) (neighbourR + errR * 3.0 / 16);
                    neighbourG = (int) (neighbourG + errG * 3.0 / 16);
                    neighbourB = (int) (neighbourB + errB * 3.0 / 16);

                    color = getColor(neighbourR, neighbourG, neighbourB);

                    newImage.setRGB(x - 1, y + 1, color);
                }
                if (y < newImage.getHeight() - 1) {
                    neighbour = newImage.getRGB(x, y + 1);
                    neighbourR = (neighbour >> 16) & 0xFF;
                    neighbourG = (neighbour >> 8) & 0xFF;
                    neighbourB = neighbour & 0xFF;

                    neighbourR = (int) (neighbourR + errR * 5.0 / 16);
                    neighbourG = (int) (neighbourG + errG * 5.0 / 16);
                    neighbourB = (int) (neighbourB + errB * 5.0 / 16);

                    color = getColor(neighbourR, neighbourG, neighbourB);

                    newImage.setRGB(x, y + 1, color);
                }
                if (x < newImage.getWidth() - 1 && y < newImage.getHeight() - 1) {
                    neighbour = newImage.getRGB(x + 1, y + 1);
                    neighbourR = (neighbour >> 16) & 0xFF;
                    neighbourG = (neighbour >> 8) & 0xFF;
                    neighbourB = neighbour & 0xFF;

                    neighbourR = (int) (neighbourR + errR * 1.0 / 16);
                    neighbourG = (int) (neighbourG + errG * 1.0 / 16);
                    neighbourB = (int) (neighbourB + errB * 1.0 / 16);

                    color = getColor(neighbourR, neighbourG, neighbourB);

                    newImage.setRGB(x + 1, y + 1, color);
                }
            }
        }

        return newImage;
    }
}