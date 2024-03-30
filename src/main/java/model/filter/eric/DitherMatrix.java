package model.filter.eric;

class DitherMatrix {
    int[][] matrix;
    int size;
    int normalizer;

    public DitherMatrix(int quantizationRank) {
        QuantizationRank rank = QuantizationRank.fromRank(quantizationRank);
        this.size = rank.getSize();
        this.matrix = rank.getMatrix();
        this.normalizer = rank.getNormalizer();
    }
}