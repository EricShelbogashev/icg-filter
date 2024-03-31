package view.filters.watercolor;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.boch.SharpnessFilter;
import model.filter.mikhail.MedianFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class WatercolorFilterViewUnit extends FilterViewUnit {
    private WatercolorSettings options = new WatercolorSettings(
            OptionsFactory.settingInteger(
                    2,
                    "Mask radius",
                    "The radius of a square window of pixels that will be blurred by a single color.",
                    1, 10
            ),
            OptionsFactory.settingInteger(
                    2,
                    "Grain strength",
                    "The strength of the grain effect.",
                    0, 3
            )
    );

    public WatercolorFilterViewUnit(Consumer<Float> progressFilterListener) {
        super("Watercolor filter", "Adds a watercolor painting effect to your image.", "icons/paletteIcon.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        int maskRadius = options.maskRadius().value();
        int grainStrength = options.grainStrength().value();
        ArrayList<Filter> waterColorFilters = new ArrayList<>();
        MedianFilter medianFilter = new MedianFilter(maskRadius);
        waterColorFilters.add(medianFilter);
        for (int i = 0; i < grainStrength; i++) {
            waterColorFilters.add(new SharpnessFilter());
        }
        return applyFilters(image, waterColorFilters);
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(options.maskRadius(), options.grainStrength());
    }
}
