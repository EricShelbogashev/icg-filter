package view.filters.roberts;

import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.darya.RobertsFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RobertsFilterViewInit extends FilterViewUnit {
    private final RobertsSettings robertsSettings = new RobertsSettings(
            OptionsFactory.settingInteger(
                    28,
                    "binarize",
                    "binarize",
                    0, 254
            )
    );

    public RobertsFilterViewInit(Consumer<Float> progressFilterListener) {
        super("Roberts Filter", "Get edges on the image. (Roberts)", "icons/roberts.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        int binarize = robertsSettings.binarize().value();
        RobertsFilter filter = new RobertsFilter(binarize);
        return applyFilters(image, List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(robertsSettings.binarize());
    }
}
