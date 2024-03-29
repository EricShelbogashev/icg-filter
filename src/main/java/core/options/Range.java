package core.options;

public record Range(int start, int end) implements Comparable<Range> {
    public Range {
        if (start > end) {
            throw new IllegalArgumentException("The start of your range should be greater than it's end");
        }
    }

    public static Range of(int start, int end) {
        return new Range(start, end);
    }

    @Override
    public int compareTo(Range o) {
        if (this.start == o.start) {
            return Integer.compare(this.end, o.end);
        }
        return Integer.compare(this.start, o.start);
    }

    @Override
    public String toString() {
        return "Range{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}