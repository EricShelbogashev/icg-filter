package view.filters;

import core.filter.Filter;
import core.options.Setting;
import model.filter.leonid.NegativeFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class NegativeFilterViewUnit extends FilterViewUnit{
    public NegativeFilterViewUnit(Consumer<List<Filter>> applyFilters) {
        super("Negative filter", "Inverts colors", "icons/invertIcon.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        applyFilters.accept(List.of(new NegativeFilter()));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return null;
    }
}
