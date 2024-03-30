package view.filters.embossing;

import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.leonid.EmbossingFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class EmbossingFilterViewUnit extends FilterViewUnit {
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

    public EmbossingFilterViewUnit(Consumer<Float> progressFilterListener) {
        super("Embossing filter", "Emboss image like on metal", "icons/embossingIcon.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        EmbossingFilter.Light lightSource = options.lightSource().value();
        int brightnessIncrease = options.brightnessIncrease().value();

        EmbossingFilter filter = new EmbossingFilter(lightSource, brightnessIncrease);
        return applyFilters(image, List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(options.lightSource(), options.brightnessIncrease());
    }
}
