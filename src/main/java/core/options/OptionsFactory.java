package core.options;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OptionsFactory {

    public static Setting<Integer> settingInteger(int defaultValue, String label, String hint, int min, int max, String id) {
        return new Setting<>(defaultValue, label, hint, List.of(
                value -> value < min ? "should be grater or equal to " + min : null,
                value -> value > max ? "should be less or equal to " + max : null
        ), Integer.class, id) {
            @Override
            public JComponent createComponent() {
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(value.intValue(), min, max, 1));
                spinner.addChangeListener(e -> value(spinner.getValue()));
                return spinner;
            }
        };
    }

    public static <T extends Enum<T>> Setting<T> settingEnum(T defaultValue, String label, String description, Class<T> type, String id) {
        return new EnumSetting<>(defaultValue, label, description, type, id);
    }

    public static Setting<Float> settingFloat(float defaultValue, String label, String hint, float min, float max, String id) {
        return new Setting<>(defaultValue, label, hint, List.of(
                value -> value < min ? "should be grater or equal to " + min : null,
                value -> value > max ? "should be less or equal to " + max : null
        ), Float.class, id) {
            @Override
            public JComponent createComponent() {
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(value.floatValue(), min, max, 0.1));
                spinner.addChangeListener(e -> value(spinner.getValue()));
                return spinner;
            }
        };
    }

    public static Setting<Range> settingRange(Range defaultValue, String label, String hint, Range bounds, String id) {
        return new Setting<>(defaultValue, label, hint, List.of(
                value -> bounds.start() > value.start() ? "The range should start with " + bounds.start() : null,
                value -> bounds.end() < value.end() ? "The range should end with " + bounds.end() : null
        ), Range.class, id) {
            @Override
            public JComponent createComponent() {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JSpinner startSpinner = new JSpinner(new SpinnerNumberModel(defaultValue.start(), bounds.start(), bounds.end(), 1));
                JSpinner endSpinner = new JSpinner(new SpinnerNumberModel(defaultValue.end(), bounds.start(), bounds.end(), 1));
                startSpinner.addChangeListener(e -> {
                    Range newValue = new Range((Integer) startSpinner.getValue(), value.end());
                    value(newValue);
                });
                endSpinner.addChangeListener(e -> {
                    Range newValue = new Range(value.start(), (Integer) endSpinner.getValue());
                    value(newValue);
                });

                panel.add(new JLabel("Start:"));
                panel.add(startSpinner);
                panel.add(new JLabel("End:"));
                panel.add(endSpinner);

                return panel;
            }
        };
    }
}