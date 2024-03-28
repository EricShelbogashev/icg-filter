package view.filters;

import core.filter.Filter;
import core.options.Setting;
import model.filter.boch.MotionBlurFilter;
import model.filter.boch.SharpnessFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SharpnessViewUnit extends FilterViewUnit
{
    private final SharpnessSettings options = new SharpnessSettings();

    public SharpnessViewUnit(Consumer<List<Filter>> applyFilters)
    {
        super("Sharpen", "Apply sharpness filter", "icons/sharpenIcon.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image)
    {
        SharpnessFilter filter = new SharpnessFilter();
        applyFilters.accept(List.of(filter));
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return null;
    }
}