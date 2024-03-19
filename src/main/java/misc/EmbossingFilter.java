package misc;

import java.awt.*;

public class EmbossingFilter extends ICGFilter {

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
        }
        else if (lightPosition == Light.RIGHT_BOTTOM) {
            return rightBottomLight[x][y];
        }
        else if(lightPosition == Light.RIGHT_TOP) {
            return rightTopLight[x][y];
        }
        else {
            return leftBottomLight[x][y];
        }
    }


    static Pattern pattern  = new Pattern(new Point(-1, -1), new Point(new Point(1, 1)));
    public EmbossingFilter(Light lightPosition) {
        super(pattern);
        this.lightPosition = lightPosition;
    }

    @Override
    public int apply(MatrixView matrixView) {
        int curColor;
        int resultColor = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                curColor = (matrixView.get(i, j) >> 16) & 0xFF;
                int koef = getKoef(i+1, j+1);
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
