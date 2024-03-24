package model.bochkarev;

import model.ICGFilter;
import model.MatrixView;
import model.Pattern;

import java.awt.*;

public class SharpnessFilter extends ICGFilter
{
    public SharpnessFilter(Pattern pattern)
    {
        super(new Pattern(new Point(-1, -1), new Point(1, 1)));
    }
    @Override
    public int apply(MatrixView matrixView)
    {
        int[] one = new int[] {0, -1, 0};
        int[] two = new int[] {-1, 5, -1};
        int[] three = new int[] {0, -1, 0};
        int[][] matrix = new int[][] {one, two, three};


        int redResult = 0;
        int greenResult = 0;
        int blueResult = 0;
        int alphaResult = 255;

        for(int i = -1; i < 2; i++)
        {
            for(int j = -1; j < 2; j++)
            {
                int rgb = matrixView.get(i, j);
                redResult += ((rgb >> 16) & 0xFF) * matrix[i+1][j+1];
                greenResult += ((rgb >> 8) & 0xFF) * matrix[i+1][j+1];
                blueResult += ((rgb) & 0xFF) * matrix[i+1][j+1];
            }
        }
        redResult = Math.min(Math.max(redResult, 0), 255);
        greenResult = Math.min(Math.max(greenResult, 0), 255);
        blueResult = Math.min(Math.max(blueResult, 0), 255);
        return ((int) alphaResult << 24) | ((int) redResult << 16) | ((int) greenResult << 8) | ((int) blueResult);
    }
}
