package model.filter.boch;

import core.filter.Image;
import core.filter.MatrixFilter;
import model.filter.leonid.ColorUtils;

public class SharpnessFilter extends MatrixFilter {

    @Override
    protected int apply(Image image, int x, int y) {
        int[] one = new int[]{0, -1, 0};
        int[] two = new int[]{-1, 5, -1};
        int[] three = new int[]{0, -1, 0};
        int[][] matrix = new int[][]{one, two, three};


        int redResult = 0;
        int greenResult = 0;
        int blueResult = 0;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                int rgb = image.color(i + x, j + y);
                redResult += ColorUtils.red(rgb) * matrix[i + 1][j + 1];
                greenResult += ColorUtils.green(rgb) * matrix[i + 1][j + 1];
                blueResult += ColorUtils.blue(rgb) * matrix[i + 1][j + 1];
            }
        }
        redResult = Math.min(Math.max(redResult, 0), 255);
        greenResult = Math.min(Math.max(greenResult, 0), 255);
        blueResult = Math.min(Math.max(blueResult, 0), 255);
        return ColorUtils.rgb(redResult, greenResult, blueResult);
    }
}
