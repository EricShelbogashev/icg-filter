package view.filters;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.mikhail.WindFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
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
                    "Higher values restrict the effect to fewer areas of the image.",
                    0, 50
            )
    );

    public WindFilterViewUnit(Consumer<List<Filter>> filtersConsumer) {
        super("Wind Filter", "Wind-like bleed effect.", "icons/wind.png", filtersConsumer);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        WindFilter.Direction direction = options.direction().value();

        int strength = options.strength().value();

        int threshold = options.threshold().value();

        WindFilter filter = new WindFilter(direction, strength, threshold);
        this.applyFilters.accept(List.of(filter));
    }

    @Override
    @Nullable
    public List<Setting<?>> getSettings() {
        return List.of(options.direction(), options.strength(), options.threshold());
    }
}
