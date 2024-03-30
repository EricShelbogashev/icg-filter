package view.filters.bloom;

import core.options.Setting;

public record BloomSettings(
        Setting<Double> glowFactor,
        Setting<Double> threshold,
        Setting<Integer> radius

) {
}
