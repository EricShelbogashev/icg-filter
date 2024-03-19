package misc;

/*Effect logic:
 * 1. Define bloom pixels mask
 * 2. Apply gaussian blur
 * 3. Mix with original image to increase brightness */

import core.filter.FilterExecutor;
import model.filter.leonid.GaussianBlurFilter;

import java.awt.image.BufferedImage;

import static java.lang.Thread.sleep;

public class BloomEffect {
    BufferedImage originalImage;
    BufferedImage bloomMask;

    double glowFactor;
    double threshold;

    int radius;

    public BloomEffect(BufferedImage image, double glowFactor, double threshold, int radius) {
        originalImage = image;
        this.glowFactor = glowFactor;
        this.threshold = threshold;
        this.radius = radius;
    }

    private static int getResultRGB(int rgb1, int rgb2) {
        int red1 = ColorUtils.red(rgb1);
        int green1 = ColorUtils.green(rgb1);
        int blue1 = ColorUtils.blue(rgb1);

        int red2 = ColorUtils.red(rgb2);
        int green2 = ColorUtils.green(rgb2);
        int blue2 = ColorUtils.blue(rgb2);

        int red = Math.min(red1 + red2, 255);
        int green = Math.min(green1 + green2, 255);
        int blue = Math.min(blue1 + blue2, 255);

        return ColorUtils.rgb(red, green, blue);
    }

    private void mixImages(BufferedImage image1, BufferedImage image2) {
        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                int rgb1 = image1.getRGB(x, y);
                int rgb2 = image2.getRGB(x,y);

                int resultRGB = getResultRGB(rgb1, rgb2);


                image1.setRGB(x, y, resultRGB);
            }
        }
    }



    public BufferedImage applyEffect() throws InterruptedException {
        // Define bloom mask
        BloomFilter bloomFilter = new BloomFilter(glowFactor, threshold);
        GaussianBlurFilter blurFilter = new GaussianBlurFilter(radius);

        BufferedImage buffer = originalImage;
        ImageProcessor processor = new ImageProcessor(buffer);

        // Get bloom mask
        FilterExecutor.of(buffer)
                .with(bloomFilter)
                .with(blurFilter)
                // mixImages(originalImage, buffer);
                .threads(4);





        // Blur bloom mask
        // TODO
//        ;
//        processor = new ImageProcessor(bloomMask);
//        buffer = processor.apply(blurFilter, System.out::println);
//
//        sleep(2000);
//
//        mixImages(originalImage, buffer);
//


        // Mix blur mask with original image
        return originalImage;
    }
}
