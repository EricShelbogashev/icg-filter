package misc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ImageProcessor {
    private final BufferedImage image;

    public ImageProcessor(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage apply(ICGFilter filter, Consumer<Double> progressListener) {
        BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        MatrixViewFactory factory = new MatrixViewFactory(image, filter.getPattern());
        Progress progress = new Progress(resultImage.getHeight(), progressListener);
        factory.rows().forEach(row ->
                Thread.ofVirtual().start(() -> job(progress, resultImage, filter, row))
        );

        return resultImage;
    }

    private void job(Progress progress, BufferedImage resultImage, ICGFilter filter, Stream<MatrixView> row) {
        row.forEach(cell -> applyFilter(resultImage, filter.apply(cell), cell.getPivot()));
        progress.submit();
    }

    private void applyFilter(BufferedImage resultImage, int color, Point pivot) {
        resultImage.setRGB(pivot.x, pivot.y, color);
    }

    static class Progress {
        private final double capacity;
        private final AtomicInteger accumulator;
        private final Consumer<Double> listener;

        public Progress(int capacity, Consumer<Double> listener) {
            this.capacity = capacity;
            this.accumulator = new AtomicInteger(0);
            this.listener = listener;
        }

        public void submit() {
            double progress = accumulator.incrementAndGet() / capacity;
            listener.accept(progress);
        }
    }
}
