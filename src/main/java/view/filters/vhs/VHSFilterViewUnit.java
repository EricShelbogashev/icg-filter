package view.filters.vhs;

import core.options.Setting;
import model.filter.eric.VHSFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VHSFilterViewUnit extends FilterViewUnit {

    public VHSFilterViewUnit(Consumer<Float> progressFilterListener) {
        super("VHS filter", "VHS filter.", "icons/theaters.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        final var filter = new VHSFilter();
        return applyFilters(image, List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return null;
    }
}
