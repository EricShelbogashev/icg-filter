package view.filters.gaussian;

import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.darya.GaussianBlurFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GaussianFilterViewInit extends FilterViewUnit {
    private final GaussianSettings gaussianSettings = new GaussianSettings(
            OptionsFactory.settingInteger(
                    3,
                    "window",
                    "window",
                    3, 11
            )
    );

    public GaussianFilterViewInit(Consumer<Float> progressFilterListener) {
        super("Gauss Filter", "Blur image of gauss method.", "icons/gauss.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        int window = gaussianSettings.window().value();
        GaussianBlurFilter filter = new GaussianBlurFilter(window);
        return applyFilters(image, List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(gaussianSettings.window());
    }
}
