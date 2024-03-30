package view.filters;

import core.filter.Filter;
import core.options.Setting;
import model.filter.eric.VHSFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class VHSFilterViewUnit extends FilterViewUnit {

    public VHSFilterViewUnit(Consumer<List<Filter>> applyFilters) {
        super("VHS filter", "VHS filter.", "icons/theaters.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        final var filter = new VHSFilter();
        applyFilters.accept(List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return null;
    }
}
