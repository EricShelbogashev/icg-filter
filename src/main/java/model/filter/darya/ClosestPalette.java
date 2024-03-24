package model.filter.darya;

import model.filter.leonid.ColorUtils;

public class ClosestPalette {
    public static int find_closest_palette_color(int red, int green, int blue, int alpha, int[] kv) {
        int[] result = {red, green, blue};
        for (int i = 0; i < 3; i++) {
            if (result[i] < 0)
                result[i] = 0;
            if (result[i] > 255)
                result[i] = 255;
            float del = (float) (kv[i] - 1);
            result[i] = (int) ((float) (Math.round((float) result[i] / 255 * del)) / del * 255);
        }
        return ColorUtils.rgb(result[0], result[1], result[2], alpha);
    }
}
