package view.filters;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.leonid.EmbossingFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class EmbossingFilterViewUnit extends FilterViewUnit{
    private final EmbossingSettings options = new EmbossingSettings(
            OptionsFactory.settingEnum(
                    EmbossingFilter.Light.LEFT_TOP,
                    "Light source",
                    "",
                    EmbossingFilter.Light.class
            ),
            OptionsFactory.settingInteger(
                    64,
                    "Brightness increase",
                    "",
                    0, 255
            )
    );
    public EmbossingFilterViewUnit(Consumer<List<Filter>> applyFilters) {
        super("Embossing filter", "Emboss image like on metal", "icons/embossingIcon.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        EmbossingFilter.Light lightSource = options.lightSource().value();
        int brightnessIncrease = options.brightnessIncrease().value();

        EmbossingFilter filter = new EmbossingFilter(lightSource, brightnessIncrease);
        applyFilters.accept(List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(options.lightSource(), options.brightnessIncrease());
    }
}
