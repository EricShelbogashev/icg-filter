package misc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MatrixViewFactory {
    private final BufferedImage image;
    private final Pattern pattern;

    public MatrixViewFactory(BufferedImage image, Pattern pattern) {
        this.image = image;
        this.pattern = pattern;
    }

    public Stream<MatrixView> row(int y) {
        if (y < 0 || y >= image.getHeight()) {
            throw new IllegalArgumentException("Row index out of image bounds");
        }
        return IntStream.range(0, image.getWidth())
                .mapToObj(x -> new MatrixView(image, pattern, new Point(x, y)));
    }

    public Stream<Stream<MatrixView>> rows() {
        return IntStream.range(0, image.getHeight()).mapToObj(this::row);
    }

    public Stream<MatrixView> column(int x) {
        if (x < 0 || x >= image.getWidth()) {
            throw new IllegalArgumentException("Column index out of image bounds");
        }
        return IntStream.range(0, image.getHeight())
                .mapToObj(y -> new MatrixView(image, pattern, new Point(x, y)));
    }

    public Stream<Stream<MatrixView>> columns() {
        return IntStream.range(0, image.getWidth()).mapToObj(this::column);
    }
}
