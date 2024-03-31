package view.filters.watercolor;

import core.options.Setting;

public record WatercolorSettings(
        Setting<Integer> maskRadius,
        Setting<Integer> grainStrength
) {
}
