package model.filter.darya;

public class ClosestPalette {
    public static int find_closest_palette_color(int red, int green, int blue, int alpha, int[]kv){
        int []result = {red, green, blue};
        for (int i = 0; i < 3; i++) {
            if (result[i] < 0)
                result[i] = 0;
            if (result[i] > 255)
                result[i] = 255;
            float del = (float)(kv[i] - 1);
            result[i] = (int)((float)((int)((float)result[i] / 255 * del)) / del * 255);
        }
        return ((alpha & 0xFF) << 24) |
                ((result[0] & 0xFF) << 16) |
                ((result[1] & 0xFF) << 8) |
                ((result[2] & 0xFF));
    }
}
