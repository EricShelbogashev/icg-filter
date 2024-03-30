package view.filters.gaussian;

import core.options.Setting;

public record GaussianSettings(
        Setting<Integer> window
) {
}
