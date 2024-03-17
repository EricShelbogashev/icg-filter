package model;

import java.awt.*;

public class EmbossingFilter extends ICGFilter {
    // Matrix 4x4

    static int[][] embossingMatrix1 = {
            { -2, -1, 0},
            {-1, 1, 1},
            {0, 1, 2}
    };

    /*static int[][] embossingMatrix2 = {
            {2, 1, 0},
            {1, 1, -1},
            {0, -1, -2}
    };*/

    static Pattern pattern  = new Pattern(new Point(-1, -1), new Point(new Point(1, 1)));
    public EmbossingFilter() {
        super(pattern);
    }

    @Override
    public int apply(MatrixView matrixView) {
        int curColor;
        int resultColor = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                curColor = (matrixView.get(i, j) >> 16) & 0xFF;
                int koef = embossingMatrix1[i+1][j+1];
                curColor *= koef;
                resultColor += curColor;
            }
        }

        int alpha = (matrixView.get(0,0) >> 24) & 0xFF; // извлечение альфа-канала
        int grayColor = resultColor;
        grayColor = Math.max(grayColor, 0); // ограничение значения в диапазоне [0, 255]
        grayColor = Math.min(grayColor, 255);

        return (alpha << 24) | (grayColor << 16) | (grayColor << 8) | grayColor;
    }
}
