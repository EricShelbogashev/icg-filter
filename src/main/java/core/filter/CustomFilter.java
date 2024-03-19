package core.filter;

import java.awt.image.BufferedImage;

public abstract non-sealed class CustomFilter extends Filter {
    protected abstract BufferedImage apply(Image image);
}
