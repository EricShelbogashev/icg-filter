package view.filters;

import core.options.Setting;
import model.filter.eric.FitAlgorithm;

public record FitSettings(Setting<FitAlgorithm> algorithmType, Setting<FitImageTurnOn> on) {
}
