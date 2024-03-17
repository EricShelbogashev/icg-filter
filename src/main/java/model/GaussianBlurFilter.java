package model;

import java.awt.*;

public class GaussianBlurFilter extends ICGFilter {

    private final int w;


    public GaussianBlurFilter(int w) {
        super(new Pattern(new Point(-1 * w, -1 * w), new Point(w, w)));
        this.w = w;
    }

    /*static double convolutionKoef5 = 0.00390625;
    static int[][] convolutionMatrix5 = {
            {1, 4, 6, 4, 1},
            {4, 16, 24, 16, 4},
            {6, 24, 36, 24, 6},
            {4, 16, 24, 16, 4},
            {1, 4, 6, 4, 1}
    };*/



    /*@Override
    public int apply(MatrixView matrixView) {
        int resultRed = 0;
        int resultGreen = 0;
        int resultBlue = 0;

        //Apply gaussian blur
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {

                int curColor = matrixView.get(i, j);

                int red = (curColor >> 16) & 0xFF;
                int green = (curColor >> 8) & 0xFF;
                int blue = curColor & 0xFF;

                resultRed += (int)(red * convolutionMatrix5[i + 2][j + 2] * convolutionKoef5);
                resultGreen += (int)(green * convolutionMatrix5[i + 2][j + 2] * convolutionKoef5);
                resultBlue += (int)(blue * convolutionMatrix5[i + 2][j + 2] * convolutionKoef5);
            }
        }


        return (255 << 24) | (resultRed << 16) | (resultGreen << 8) | resultBlue;
    }*/

    @Override
    public int apply(MatrixView matrixView) {
        float res_r = 0;
        float res_g = 0;
        float res_b = 0;
        int alpha = (matrixView.get(0, 0) >> 24) & 0xFF;
        float sigma = w / 2.1f;
        for (int x = -1 * w; x <= w; x++)
            for (int y = -1 * w; y <= w; y++){
                float k = (float) (1.0f / (2.0f * Math.PI * sigma * sigma) * Math.pow(2.7f,  -1.0f * (x * x + y * y) / (2.0f * sigma * sigma)));
                res_r += ((matrixView.get(x, y) >> 16) & 0xFF) * k;
                res_g += ((matrixView.get(x, y) >> 8) & 0xFF) * k;
                res_b += ((matrixView.get(x, y)) & 0xFF) * k;
            }
        if (Math.round(res_r) > 255 || Math.round(res_r) < 0)
            res_r = 255;
        if (Math.round(res_b) > 255 || Math.round(res_b) < 0)
            res_b = 255;
        if (Math.round(res_g) > 255 || Math.round(res_g) < 0)
            res_g = 255;
        return ((alpha & 0xFF) << 24) |
                ((Math.round(res_r) & 0xFF) << 16) |
                ((Math.round(res_g) & 0xFF) << 8) |
                ((Math.round(res_b) & 0xFF));
    }
}
