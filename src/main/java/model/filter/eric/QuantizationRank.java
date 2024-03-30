package model.filter.eric;

import static model.filter.leonid.OrderedDithering.ditherMatrix16;
import static model.filter.leonid.OrderedDithering.ditherMatrix2;
import static model.filter.leonid.OrderedDithering.ditherMatrix4;
import static model.filter.leonid.OrderedDithering.ditherMatrix8;

public enum QuantizationRank {
    RANK_2(2, 16, ditherMatrix16, 128),
    RANK_4(4, 8, ditherMatrix8, 32),
    RANK_64(64, 4, ditherMatrix4, 8),
    RANK_128(128, 2, ditherMatrix2, 2);

    private final int rank;
    private final int size;
    private final int[][] matrix;
    private final int normalizer;

    QuantizationRank(int rank, int size, int[][] matrix, int normalizer) {
        this.rank = rank;
        this.size = size;
        this.matrix = matrix;
        this.normalizer = normalizer;
    }

    public static QuantizationRank fromRank(int rank) {
        if (rank >= RANK_128.rank) {
            return RANK_128;
        } else if (rank >= RANK_64.rank) {
            return RANK_64;
        } else if (rank >= RANK_4.rank) {
            return RANK_4;
        } else {
            return RANK_2;
        }
    }

    public int getSize() {
        return size;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public int getNormalizer() {
        return normalizer;
    }
}