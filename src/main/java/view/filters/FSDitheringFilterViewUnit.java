package view.filters;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.leonid.FSDithering;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class FSDitheringFilterViewUnit extends FilterViewUnit {

    private final DitheringSettings options = new DitheringSettings(
            OptionsFactory.settingInteger(
                    2,
                    "red quantization",
                    "red quantization rank",
                    2, 128
            ),
            OptionsFactory.settingInteger(
                    2,
                    "green quantization",
                    "green quantization rank",
                    2, 128
            ),
            OptionsFactory.settingInteger(
                    2,
                    "blue quantization",
                    "blue quantization rank",
                    2, 128
            )
    );

    public FSDitheringFilterViewUnit(Consumer<List<Filter>> applyFilters) {
        super("Floyd-Steinberg Dithering", "Apply Floyd-Steinberg dithering", "icons/ditherIcon.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        int redQuantizationRank = options.redRank().value();
        int greenQuantizationRank = options.greenRank().value();
        int blueQuantizationRank = options.blueRank().value();

        FSDithering filter = new FSDithering(redQuantizationRank, greenQuantizationRank, blueQuantizationRank);

        applyFilters.accept(List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(options.redRank(), options.greenRank(), options.blueRank());
    }
}
