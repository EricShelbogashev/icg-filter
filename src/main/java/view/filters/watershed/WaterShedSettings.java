package view.filters.watershed;

import core.options.Setting;

public record WaterShedSettings(
        Setting<Integer> red,
        Setting<Integer> green,
        Setting<Integer> blue
) {
}
