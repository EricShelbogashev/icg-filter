package view.filters;

import core.options.Setting;

public record GaussianSettings(
        Setting<Integer> window
) {
}
