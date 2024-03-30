package view.filters.sharpness;

import core.options.Setting;
import model.filter.boch.SharpnessFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SharpnessViewUnit extends FilterViewUnit {
    private final SharpnessSettings options = new SharpnessSettings();

    public SharpnessViewUnit(Consumer<Float> progressFilterListener) {
        super("Sharpen", "Apply sharpness filter", "icons/sharpenIcon.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        SharpnessFilter filter = new SharpnessFilter();
        return applyFilters(image, List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return null;
    }
}