package view.filters.dithering;

import core.options.Setting;

public record DitheringSettings(
        Setting<Integer> redRank,
        Setting<Integer> greenRank,
        Setting<Integer> blueRank,
        Setting<DitheringMethods> ditheringMethods,
        Setting<DitheringPerson> ditheringPerson
        ) {
}
