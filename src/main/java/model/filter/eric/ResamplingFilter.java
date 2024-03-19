package model.filter.eric;

import core.filter.CustomFilter;

public abstract class ResamplingFilter extends CustomFilter {
    protected final int targetWidth;
    protected final int targetHeight;

    public ResamplingFilter(int targetWidth, int targetHeight) {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }
}
