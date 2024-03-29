package model.filter.eric;

import core.filter.CustomFilter;
import core.filter.Image;
import model.filter.leonid.ColorUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class VHSFilter extends CustomFilter {
    public static Image applyFadedColorEffect(Image image) {
        final var height = image.height();
        final var width = image.width();
        final var result = Image.empty(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = image.color(x, y);
                final var red = ColorUtils.red(color);
                final var green = ColorUtils.green(color);
                final var blue = ColorUtils.blue(color);
                int average = (red + green + blue) / 3;
                int redNew = (red + average) / 2;
                int greenNew = (green + average) / 2;
                int blueNew = (blue + average) / 2;
                result.setColor(x, y, ColorUtils.rgb(redNew, greenNew, blueNew));
            }
        }
        return result;
    }

    public static Image applyAnaglyphEffect(Image sourceImage) {
        int width = sourceImage.width();
        int height = sourceImage.height();
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int shift = 8;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(sourceImage.color(x, y));
                int red = (color.getRed() << 16) & 0xFF0000;
                int blueGreen = 0;
                if (x + shift < width) {
                    Color shiftedColor = new Color(sourceImage.color(x + shift, y));
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

    public static Image applyColorNoiseEffect(Image image, int noiseIntensity) {
        final var height = image.height();
        final var width = image.width();
        final var result = Image.empty(width, height);
        Random random = new Random();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int originalColor = image.color(x, y);
                int alpha = ColorUtils.alpha(originalColor);
                int red = ColorUtils.red(originalColor);
                int green = ColorUtils.green(originalColor);
                int blue = ColorUtils.blue(originalColor);

                red = Math.min(255, Math.max(0, red + random.nextInt(noiseIntensity * 2) - noiseIntensity));
                green = Math.min(255, Math.max(0, green + random.nextInt(noiseIntensity * 2) - noiseIntensity));
                blue = Math.min(255, Math.max(0, blue + random.nextInt(noiseIntensity * 2) - noiseIntensity));

                int noisyColor = ColorUtils.rgb(red, green, blue, alpha);
                result.setColor(x, y, noisyColor);
            }
        }

        return result;
    }

    @Override
    protected BufferedImage apply(Image image) {
        var applied = image;
        applied = applyFadedColorEffect(applied);
        applied = applyAnaglyphEffect(applied);
        applied = applyColorNoiseEffect(applied, 50);
        applied = applyStripedAndShiftEffect(applied, 30, 8);
        return applied.bufferedImage();
    }
}
