package core.filter;

public abstract non-sealed class MatrixFilter extends Filter {
    protected abstract int apply(Image image, int x, int y);
}
