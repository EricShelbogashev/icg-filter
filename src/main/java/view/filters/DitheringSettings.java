package view.filters;

import core.options.Setting;

// TODO: Add dithering enum

public record DitheringSettings(
        Setting<Integer> redRank,
        Setting<Integer> greenRank,
        Setting<Integer> blueRank
        ) {
}
