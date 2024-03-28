package model.filter.eric;

import core.filter.Image;
import core.filter.MatrixFilter;

//TODO: (e.bochkarev) реализовать.

public class EricOrderedDither extends MatrixFilter
{
    int r;
    int g;
    int b;

    public EricOrderedDither(int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    protected int apply(Image image, int x, int y) {
        return 0;
    }
}
