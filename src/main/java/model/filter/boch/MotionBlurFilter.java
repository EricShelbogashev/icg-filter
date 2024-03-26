package model.bochkarev;

import core.filter.Image;
import core.filter.MatrixFilter;

// TODO Shikhutdinov Leonid

public class MotionBlurFilter  extends MatrixFilter
{
    int strength = 1;

    public MotionBlurFilter(int strength)
    {
        this.strength = strength;
    }

    @Override
    protected int apply(Image image, int x, int y)
    {
        int[] one = new int[] {3*strength, 0, 0, 0, 0};
        int[] two = new int[] {0, 2*strength, 0, 0, 0};
        int[] three = new int[] {0, 0, 1*strength, 0, 0};
        int[] four = new int[] {0, 0, 0, 2*strength, 0};
        int[] five = new int[] {0, 0, 0, 0, 3*strength};
        int[][] matrix = new int[][] {one, two, three, four, five};


        int redResult = 0;
        int greenResult = 0;
        int blueResult = 0;
        int alphaResult = 255;

        for(int i = -2; i < 3; i++)
        {
            for(int j = -2; j < 3; j++)
            {
                int rgb = image.color(i + x, j + y);
                redResult += ((rgb >> 16) & 0xFF) * matrix[i+2][j+2];
                greenResult += ((rgb >> 8) & 0xFF) * matrix[i+2][j+2];
                blueResult += ((rgb) & 0xFF) * matrix[i+2][j+2];
            }
        }
        redResult /= 11*strength;
        greenResult /= 11*strength;
        blueResult /= 11*strength;
        redResult = Math.min(Math.max(redResult, 0), 255);
        greenResult = Math.min(Math.max(greenResult, 0), 255);
        blueResult = Math.min(Math.max(blueResult, 0), 255);
        return ((int) alphaResult << 24) | ((int) redResult << 16) | ((int) greenResult << 8) | ((int) blueResult);
    }
}
