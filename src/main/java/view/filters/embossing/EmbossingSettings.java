package view.filters.embossing;

import core.options.Setting;
import model.filter.leonid.EmbossingFilter;

public record EmbossingSettings(Setting<EmbossingFilter.Light> lightSource, Setting<Integer> brightnessIncrease) {

}
