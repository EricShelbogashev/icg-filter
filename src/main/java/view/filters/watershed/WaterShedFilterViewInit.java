package view.filters.watershed;

import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.darya.ColorStretchFilter;
import model.filter.darya.FillColorFilter;
import model.filter.darya.WaterShedFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class WaterShedFilterViewInit extends FilterViewUnit {
    private final WaterShedSettings options = new WaterShedSettings(
            OptionsFactory.settingInteger(
                    2,
                    "red kvant",
                    "",
                    2, 255
            ),
            OptionsFactory.settingInteger(
                    2,
                    "green kvant",
                    "",
                    2, 255
            ),
            OptionsFactory.settingInteger(
                    2,
                    "blue kvant",
                    "", 2, 255
            )
    );

    public WaterShedFilterViewInit(Consumer<Float> progressFilterListener) {
        super("Watershed filter", "Watershed filter.", "icons/waterIcon.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        int red = options.red().value();
        int green = options.green().value();
        int blue = options.blue().value();

        WaterShedFilter waterShedFilter = new WaterShedFilter(new int[]{red, green, blue});
        ColorStretchFilter blurFilter = new ColorStretchFilter(new int[]{red, green, blue});
        FillColorFilter fillFilter = new FillColorFilter();
        return applyFilters(image, List.of(waterShedFilter, blurFilter, fillFilter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(options.red(), options.green(), options.blue());
    }
}
