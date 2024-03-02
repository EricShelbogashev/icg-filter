package model;

public abstract class ICGFilter {
    private final Pattern pattern;

    public ICGFilter(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public abstract int apply(MatrixView matrixView);
}
