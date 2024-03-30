package view.filters.gamma;

import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.boch.GammaFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

    public GammaFilterViewUnit(Consumer<Float> progressFilterListener) {
        super("Gamma filter", "Apply gamma-correction.", "icons/blurIcon.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        final int gamma = options.gamma().value();
        GammaFilter gammaFilter = new GammaFilter(gamma);
        return applyFilters(image, List.of(gammaFilter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(options.gamma());
    }
}
