package view.filters.negative;

import core.options.Setting;
import model.filter.leonid.NegativeFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NegativeFilterViewUnit extends FilterViewUnit {
    public NegativeFilterViewUnit(Consumer<Float> progressFilterListener) {
        super("Negative filter", "Inverts colors", "icons/invertIcon.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        return applyFilters(image, List.of(new NegativeFilter()));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return null;
    }
}
