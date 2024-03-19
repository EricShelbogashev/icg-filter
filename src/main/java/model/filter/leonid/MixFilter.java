package model.filter.leonid;

import core.filter.CustomFilter;
import core.filter.Image;
import core.filter.MatrixFilter;
import misc.ColorUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MixFilter extends MatrixFilter {
    Image originalImage;
    public MixFilter(Image originalImage) {
        this.originalImage = originalImage;
    }

    @Override
    protected int apply(Image image, int x, int y) {
        return ColorUtils.sum(originalImage.color(x,y), image.color(x, y));
    }
}
