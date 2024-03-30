package view.filters.sobel;

import core.options.Setting;

public record SobelSettings(
        Setting<Integer> binarize
) {
}
