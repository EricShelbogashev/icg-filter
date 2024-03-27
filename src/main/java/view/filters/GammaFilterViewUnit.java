package view.filters;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.boch.GammaFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class GammaFilterViewUnit extends FilterViewUnit {
    private final GammaSettings options = new GammaSettings(
            OptionsFactory.settingInteger(
                    500,
                    "factor",
                    "",
                    1, 1000
            )
    );

    public GammaFilterViewUnit(Consumer<List<Filter>> applyFilters) {
        super("Gamma filter", "Apply gamma-correction.", "icons/blurIcon.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        final int gamma = options.gamma().value();
        GammaFilter gammaFilter = new GammaFilter(gamma);
        applyFilters.accept(List.of(gammaFilter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(options.gamma());
    }
}
