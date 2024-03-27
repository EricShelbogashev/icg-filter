package view.filters;

import core.options.Setting;

public record BloomSettings(
        Setting<Float> glowFactor,
        Setting<Float> threshold,
        Setting<Integer> radius

) {
}
