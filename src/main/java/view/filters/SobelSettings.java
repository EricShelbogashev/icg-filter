package view.filters;

import core.options.Setting;

public record SobelSettings(
        Setting<Integer> binarize
) {
}
