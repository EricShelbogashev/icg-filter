package view.filters.monochrome;

import core.options.Setting;
import model.filter.leonid.MonochromeFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MonochromeFilterViewUnit extends FilterViewUnit {

    public MonochromeFilterViewUnit(Consumer<Float> progressFilterListener) {
        super("Monochrome filter", "Converts image to monochrome", "icons/monochromeIcon.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        return applyFilters(image, List.of(new MonochromeFilter()));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return null;
    }
}
