package view.filters;

import core.filter.Filter;
import core.options.Setting;
import model.filter.leonid.MonochromeFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class MonochromeFilterViewUnit extends FilterViewUnit {

    public MonochromeFilterViewUnit(Consumer<List<Filter>> applyFilters) {
        super("Monochrome filter", "Converts image to monochrome", "icons/monochromeIcon.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        applyFilters.accept(List.of(new MonochromeFilter()));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return null;
    }
}
