package view.filters.sobel;

import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.darya.SobelFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SobelFilterViewInit extends FilterViewUnit {
    private final SobelSettings sobelSettings = new SobelSettings(
            OptionsFactory.settingInteger(
                    128,
                    "binarize",
                    "binarize",
                    0, 254
            )
    );

    public SobelFilterViewInit(Consumer<Float> progressFilterListener) {
        super("Sobel Filter", "Get edges on the image.", "icons/sobel.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        int binarize = sobelSettings.binarize().value();
        SobelFilter filter = new SobelFilter(binarize);
        return applyFilters(image, List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(sobelSettings.binarize());
    }
}
