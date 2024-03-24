package core.options;

import javax.swing.*;
import java.util.List;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {

    public EnumSetting(E defaultValue, String label, String description, Class<E> type, String id) {
        super(defaultValue, label, description, List.of(), type, id);
    }

    @Override
    public JComponent createComponent() {
        E[] constants = type.getEnumConstants();
        JComboBox<E> comboBox = new JComboBox<>(constants);
        comboBox.setSelectedItem(value());
        comboBox.addActionListener(e -> value(comboBox.getSelectedItem()));
        return comboBox;
    }
}