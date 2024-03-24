package model.bochkarev;

import misc.ICGFilter;
import misc.MatrixView;
import misc.Pattern;

import java.awt.*;

public class MotionBlurFilter  extends ICGFilter
{
    public MotionBlurFilter(Pattern pattern)
    {
        super(new Pattern(new Point(-2, -2), new Point(2, 2)));
    }
    @Override
    public int apply(MatrixView matrixView)
    {
        int[] one = new int[] {3, 0, 0, 0, 0};
        int[] two = new int[] {0, 2, 0, 0, 0};
        int[] three = new int[] {0, 0, 1, 0, 0};
        int[] four = new int[] {0, 0, 0, 2, 0};
        int[] five = new int[] {0, 0, 0, 0, 3};
        int[][] matrix = new int[][] {one, two, three, four, five};


        int redResult = 0;
        int greenResult = 0;
        int blueResult = 0;
        int alphaResult = 255;

        for(int i = -2; i < 3; i++)
        {
            for(int j = -2; j < 3; j++)
            {
                int rgb = matrixView.get(i, j);
                redResult += ((rgb >> 16) & 0xFF) * matrix[i+2][j+2];
                greenResult += ((rgb >> 8) & 0xFF) * matrix[i+2][j+2];
                blueResult += ((rgb) & 0xFF) * matrix[i+2][j+2];
            }
        }
        redResult /= 11;
        greenResult /= 11;
        blueResult /= 11;
        redResult = Math.min(Math.max(redResult, 0), 255);
        greenResult = Math.min(Math.max(greenResult, 0), 255);
        blueResult = Math.min(Math.max(blueResult, 0), 255);
        return ((int) alphaResult << 24) | ((int) redResult << 16) | ((int) greenResult << 8) | ((int) blueResult);
    }
}
