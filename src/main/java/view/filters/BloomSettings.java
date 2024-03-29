package view.filters;

import core.options.Setting;

public record BloomSettings(
        Setting<Double> glowFactor,
        Setting<Double> threshold,
        Setting<Integer> radius

) {
}
