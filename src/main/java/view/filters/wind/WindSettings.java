package view.filters.wind;

import core.options.Setting;
import model.filter.mikhail.WindFilter;

public record WindSettings(
        Setting<WindFilter.Direction> direction,
        Setting<Integer> strength,
        Setting<Integer> threshold
) {
}
