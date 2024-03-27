package view.filters;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.mikhail.RotateImageFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class RotateImageViewUnit extends FilterViewUnit {
    private final RotateImageSettings rotateImageSettings = new RotateImageSettings(
            OptionsFactory.settingInteger(
                    90,
                    "Angle",
                    "Angle of rotate.",
                    -180, 180
            )
    );

    public RotateImageViewUnit(Consumer<List<Filter>> applyFilters) {
        super("Rotate image", "Rotate an image with an angle.", "icons/rotate.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        int angle = rotateImageSettings.angle().value();
        RotateImageFilter filter = new RotateImageFilter(angle);
        applyFilters.accept(List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(rotateImageSettings.angle());
    }
}
