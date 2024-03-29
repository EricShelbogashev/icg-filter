package view.filters;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.darya.GaussianBlurFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class GaussianFilterViewInit extends FilterViewUnit{
    private final GaussianSettings gaussianSettings = new GaussianSettings(
            OptionsFactory.settingInteger(
                    3,
                    "window",
                    "window",
                    3, 11
            )
    );

    public GaussianFilterViewInit(Consumer<List<Filter>> applyFilters) {
        super("Gauss Filter", "Blur image of gauss method.", "icons/gauss.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        int window = gaussianSettings.window().value();
        GaussianBlurFilter filter = new GaussianBlurFilter(window);
        applyFilters.accept(List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(gaussianSettings.window());
    }
}
