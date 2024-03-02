package model;

import java.awt.Point;

/**
 * Pattern of the filter matrix.
 */
public class Pattern {
    private final Point start;
    private final Point end;

    /**
     * Constructs a Pattern with specified start and end points.
     *
     * @param start The start point of the submatrix. Should not be greater than (0,0).
     * @param end   The end point of the submatrix. Should not be less than (0,0).
     */
    public Pattern(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }
}
