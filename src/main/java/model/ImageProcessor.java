package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class ImageProcessor {
    private final BufferedImage image;

    public ImageProcessor(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage apply(ICGFilter filter) {
        BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        MatrixViewFactory factory = new MatrixViewFactory(image, filter.getPattern());
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            factory.rows().forEach(row ->
                    executor.submit(() -> job(resultImage, filter, row))
            );
            executor.shutdown();
        }

        return resultImage;
    }

    private void job(BufferedImage resultImage, ICGFilter filter, Stream<MatrixView> row) {
        row.forEach(cell -> applyFilter(resultImage, filter.apply(cell), cell.getPivot()));
    }

    private void applyFilter(BufferedImage resultImage, int color, Point pivot) {
        resultImage.setRGB(pivot.x, pivot.y, color);
    }
}
