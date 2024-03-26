package model.filter.leonid;

import core.filter.Image;
import core.filter.MatrixFilter;

public class EmbossingFilter extends MatrixFilter {

    static int[][] leftTopLight = {
            {0, 1, 2},
            {-1, 1, 1},
            {-2, -1, 0}
    };
    static int[][] rightTopLight = {
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
    int brightnessIncrease;
    Light lightPosition;

    public EmbossingFilter(Light lightPosition, int brightnessIncrease) {
        this.lightPosition = lightPosition;
        this.brightnessIncrease = brightnessIncrease;
    }

    @Override
    protected int apply(Image image, int x, int y) {
        int curColor;
        int resultColor = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                curColor = ColorUtils.getBrightness(image.color(x + i, y + j));
                int koef = getKoef(i + 1, j + 1);
                curColor *= koef;
                resultColor += curColor;
            }
        }

        int alpha = image.alpha(x, y);
        int grayColor = resultColor + brightnessIncrease;
        grayColor = Math.max(grayColor, 0); // ограничение значения в диапазоне [0, 255]
        grayColor = Math.min(grayColor, 255);

        return ColorUtils.rgb(grayColor, grayColor, grayColor, alpha);
    }

    private int getKoef(int x, int y) {
        switch (lightPosition) {
            case LEFT_TOP -> {
                return leftTopLight[x][y];
            }
            case RIGHT_BOTTOM -> {
                return rightBottomLight[x][y];
            }
            case RIGHT_TOP -> {
                return rightTopLight[x][y];
            }
            case LEFT_BOTTOM -> {
                return leftBottomLight[x][y];
            }
        }
        return 0;
    }

    public enum Light {LEFT_TOP, RIGHT_BOTTOM, RIGHT_TOP, LEFT_BOTTOM}

}
