package view.filters.rotate;

import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.mikhail.RotateImageFilter;
import org.jetbrains.annotations.Nullable;
import view.filters.FilterViewUnit;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

    public RotateImageViewUnit(Consumer<Float> progressFilterListener) {
        super("Rotate image", "Rotate an image with an angle.", "icons/rotate.png", progressFilterListener);
    }

    @Override
    public CompletableFuture<BufferedImage> applyFilter(BufferedImage image) {
        int angle = rotateImageSettings.angle().value();
        RotateImageFilter filter = new RotateImageFilter(angle);
        return applyFilters(image, List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(rotateImageSettings.angle());
    }
}
