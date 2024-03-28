package model.filter.mikhail;

import core.filter.Image;
import core.filter.MatrixFilter;
import model.filter.leonid.ColorUtils;

//TODO (e.bochkarev) реализовать.

public class MikhailFloydDither extends MatrixFilter
{
    int r;
    int g;
    int b;

    public MikhailFloydDither(int r, int g, int b)
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