package view.filters.wind;

import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.mikhail.WindFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class WindFilterViewUnit extends FilterViewUnit {
    private final WindSettings options = new WindSettings(
            OptionsFactory.settingEnum(
                    WindFilter.Direction.RIGHT,
                    "Wind direction",
                    "",
                    WindFilter.Direction.class
            ),
            OptionsFactory.settingInteger(
                    5,
                    "Strength",
                    "Higher values increase the magnitude of the effect.",
                    0, 100
            ),
            OptionsFactory.settingInteger(
                    3,
                    "Threshold",
                    "Lower values restrict the effect to fewer areas of the image.",
                    0, 50
            )
    );

    public WindFilterViewUnit(Consumer<Float> progressFilterListener) {
        super("Wind Filter", "Wind-like bleed effect.", "icons/wind.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        WindFilter.Direction direction = options.direction().value();

        int strength = options.strength().value();

        int threshold = options.threshold().value();

        WindFilter filter = new WindFilter(direction, strength, threshold);
        return applyFilters(image, List.of(filter));
    }

    @Override
    @Nullable
    public List<Setting<?>> getSettings() {
        return List.of(options.direction(), options.strength(), options.threshold());
    }
}
