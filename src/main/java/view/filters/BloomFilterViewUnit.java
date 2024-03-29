package view.filters;

import core.filter.Filter;
import core.filter.Image;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.darya.GaussianBlurFilter;
import model.filter.leonid.BloomFilter;
import model.filter.leonid.MixFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class BloomFilterViewUnit extends FilterViewUnit {

    private final BloomSettings options = new BloomSettings(
            OptionsFactory.settingDouble(
                    0.3,
                    "strength",
                    "",
                    0.0, 1.0
            ),
            OptionsFactory.settingDouble(
                    0.7,
                    "threshold",
                    "",
                    0.0, 1.0
            ),
            OptionsFactory.settingInteger(
                    5,
                    "radius",
                    "", 1, 50
            )
    );

    public BloomFilterViewUnit(Consumer<List<Filter>> applyFilters) {
        super("Bloom filter", "Bloom filter.", "icons/bloomIcon.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        double glowFactor = options.glowFactor().value();
        double threshold = options.threshold().value();
        int radius = options.radius().value();

        BloomFilter bloomFilter = new BloomFilter(glowFactor, threshold);
        GaussianBlurFilter blurFilter = new GaussianBlurFilter(radius);
        MixFilter mixFilter = new MixFilter(new Image(image));
        applyFilters.accept(List.of(bloomFilter, blurFilter, mixFilter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(options.glowFactor(), options.threshold(), options.radius());
    }
}
