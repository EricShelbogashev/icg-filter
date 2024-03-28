package view.filters;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.eric.FitAlgorithm;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FitImageToScreenFilterViewUnit extends FilterViewUnit {
    private final FitSettings fitOptions = new FitSettings(
            OptionsFactory.settingEnum(
                    FitAlgorithm.BILINEAR,
                    "Choose fit to image algorithm",
                    "",
                    FitAlgorithm.class
            ));
    private final Supplier<Dimension> getSize;

    public FitImageToScreenFilterViewUnit(Supplier<Dimension> getSize, Consumer<List<Filter>> applyFilters) {
        super("Fit image to screen", "Selects the image size so that it fits completely into the work area.", "icons/fit.png", applyFilters);
        this.getSize = getSize;
    }

    @Override
    public void applyFilter(BufferedImage image) {
        Dimension screenSize = getSize.get();
        double widthRatio = (screenSize.getWidth() - 13) / image.getWidth();
        double heightRatio = (screenSize.getHeight() - 13) / image.getHeight();
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) Math.floor(image.getWidth() * ratio);
        int newHeight = (int) Math.floor(image.getHeight() * ratio);

        final var algorithmType = fitOptions.algorithmType();
        FitAlgorithm algorithm = algorithmType.value();
        applyFilters.accept(List.of(algorithm.filter(newWidth, newHeight)));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(fitOptions.algorithmType());
    }
}
