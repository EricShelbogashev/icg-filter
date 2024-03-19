package misc;

import java.awt.*;

/**
 * Pattern of the model.filter matrix.
 */
public record Pattern(Point start, Point end) {
    /**
     * Constructs a Pattern with specified start and end points.
     *
     * @param start The start point of the submatrix. Should not be greater than (0,0).
     * @param end   The end point of the submatrix. Should not be less than (0,0).
     */
    public Pattern {
    }
}
