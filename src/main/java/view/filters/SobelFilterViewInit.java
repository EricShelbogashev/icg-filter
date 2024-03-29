package view.filters;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.darya.RobertsFilter;
import model.filter.darya.SobelFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class SobelFilterViewInit extends FilterViewUnit{
    private final SobelSettings sobelSettings = new SobelSettings(
            OptionsFactory.settingInteger(
                    128,
                    "binarize",
                    "binarize",
                    0, 254
            )
    );

    public SobelFilterViewInit(Consumer<List<Filter>> applyFilters) {
        super("Sobel Filter", "Get edges on the image.", "icons/sobel.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        int binarize = sobelSettings.binarize().value();
        SobelFilter filter = new SobelFilter(binarize);
        applyFilters.accept(List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(sobelSettings.binarize());
    }
}
