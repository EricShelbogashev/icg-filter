package model.filter.eric;

import core.filter.Filter;

import java.util.function.BiFunction;

public enum FitAlgorithm {
    LANCZOS(LanczosResampling::new),
    BILINEAR(BilinearInterpolation::new),
    NEAREST(NearestNeighborInterpolation::new);

    private final BiFunction<Integer, Integer, Filter> filterFactory;

    FitAlgorithm(BiFunction<Integer, Integer, Filter> filterFactory) {
        this.filterFactory = filterFactory;
    }

    public Filter filter(int targetWidth, int targetHeight) {
        return filterFactory.apply(targetWidth, targetHeight);
    }
}
