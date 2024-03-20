package model.filter.leonid;

import core.filter.Image;
import core.filter.MatrixFilter;


public class EmbossingFilter extends MatrixFilter {

    @Override
    protected int apply(Image image, int x, int y) {
        int curColor;
        int resultColor = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                curColor = image.color(x + i, y + j);
                int koef = getKoef(i + 1, j + 1);
                curColor *= koef;
                resultColor += curColor;
            }
        }

        int grayColor = resultColor;
        grayColor = Math.max(grayColor, 0); // ограничение значения в диапазоне [0, 255]
        grayColor = Math.min(grayColor, 255);

        return grayColor;
    }

    public enum Light {LEFT_TOP, RIGHT_BOTTOM, RIGHT_TOP}

    static int[][] rightTopLight = {
            {0, 1, 2},
            {-1, 1, 1},
            {-2, -1, 0}
    };

    static int[][] leftTopLight = {
            {2, 1, 0},
            {1, 1, -1},
            {0, -1, -2}
    };

    static int[][] leftBottomLight = {
            {0, -1, -2},
            {1, 1, -1},
            {2, 1, 0}
    };

    static int[][] rightBottomLight = {
            {-2, -1, 0},
            {-1, 1, 1},
            {0, 1, 2}
    };

    Light lightPosition;

    private int getKoef(int x, int y) {
        if (lightPosition == Light.LEFT_TOP) {
            return leftTopLight[x][y];
        } else if (lightPosition == Light.RIGHT_BOTTOM) {
            return rightBottomLight[x][y];
        } else if (lightPosition == Light.RIGHT_TOP) {
            return rightTopLight[x][y];
        } else {
            return leftBottomLight[x][y];
        }
    }

    public EmbossingFilter(Light lightPosition) {
        this.lightPosition = lightPosition;
    }

}
