package view.filters;

import core.options.Setting;
import model.filter.leonid.EmbossingFilter;

public record EmbossingSettings(Setting<EmbossingFilter.Light> lightSource, Setting<Integer> brightnessIncrease) {

}
