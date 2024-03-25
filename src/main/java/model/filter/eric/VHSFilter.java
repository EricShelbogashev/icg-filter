package model.filter.eric;

import core.filter.CustomFilter;
import core.filter.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class VHSFilter extends CustomFilter {

    public static Image applyAnaglyphEffect(Image image) {
        int width = image.width();
        int height = image.height();
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int shift = 10;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.color(x, y));
                int red = (color.getRed() << 16) & 0xFF0000;

                int blueGreen = 0;
                if (x + shift < width) {
                    Color shiftedColor = new Color(image.color(x + shift, y));
                    blueGreen = (shiftedColor.getGreen() << 8) & 0x00FF00 | shiftedColor.getBlue() & 0x0000FF;
                }

                int combinedColor = red | blueGreen;
                resultImage.setRGB(x, y, combinedColor);
            }
        }

        return new Image(resultImage);
    }

    public static Image applyStripedAndShiftEffect(Image image, int stripeHeight, int shiftValue) {
        final var result = Image.copyOf(image);
        final var random = new Random();
        final var height = image.height();
        final var width = image.width();
        for (int y = 0; y < height; y += stripeHeight * 2) {
            if (random.nextFloat() < 0.70) continue;
            for (int dy = 0; dy < stripeHeight && (y + dy) < height; dy++) {
                for (int x = 0; x < width; x++) {
                    int shiftedX = x + shiftValue;
                    if (shiftedX < 0) shiftedX = 0;
                    if (shiftedX >= width) shiftedX = width - 1;
                    result.setColor(shiftedX, y + dy, image.color(x, y + dy));
                }
            }
        }
        return result;
    }

    @Override
    protected BufferedImage apply(Image image) {
        var applied = image;
        applied = applyStripedAndShiftEffect(applied, 30, 8);
        applied = applyAnaglyphEffect(applied);
        return applied.bufferedImage();
    }
}
