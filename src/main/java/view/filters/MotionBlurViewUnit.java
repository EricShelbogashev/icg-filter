package view.filters;

import core.filter.Filter;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.boch.MotionBlurFilter;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MotionBlurViewUnit extends FilterViewUnit
{
    private final MotionBlurSettings options = new MotionBlurSettings(
            OptionsFactory.settingInteger(
                    1,
                    "strength",
                    "strength",
                    1, 10
            )
    );

    public MotionBlurViewUnit(Consumer<List<Filter>> applyFilters)
    {
        super("Motion blur", "Apply motion blur", "icons/motionBlurIcon.png", applyFilters);
    }

    @Override
    public void applyFilter(BufferedImage image) {
        int strength = options.strength().value();
        MotionBlurFilter filter = new MotionBlurFilter(strength);
        List<Filter> list = new ArrayList<>();
        for(int i = 0; i < strength; i++)
        {
            list.add(filter);
        }
        applyFilters.accept(list);
    }

    @Override
    public @Nullable List<Setting<?>> getSettings() {
        return List.of(options.strength());
    }
}