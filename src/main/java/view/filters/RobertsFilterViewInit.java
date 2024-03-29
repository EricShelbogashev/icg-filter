package view.filters;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.darya.RobertsFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class RobertsFilterViewInit extends FilterViewUnit{
    private final RobertsSettings robertsSettings = new RobertsSettings(
            OptionsFactory.settingInteger(
                    28,
                    "binarize",
                    "binarize",
                    0, 254
            )
    );

    public RobertsFilterViewInit(Consumer<List<Filter>> applyFilters) {
        super("Roberts Filter", "Get edges on the image.", "icons/roberts.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        int binarize = robertsSettings.binarize().value();
        RobertsFilter filter = new RobertsFilter(binarize);
        applyFilters.accept(List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(robertsSettings.binarize());
    }
}
