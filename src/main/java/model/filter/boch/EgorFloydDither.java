package model.filter.boch;

import core.filter.Image;
import core.filter.MatrixFilter;

//TODO: (e.bochkarev) реализовать.

public class EgorFloydDither extends MatrixFilter
{

    int r;
    int g;
    int b;

    public EgorFloydDither(int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    protected int apply(Image image, int x, int y)
    {
        return 0;
    }
}
