package model.options;

import core.options.Setting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class SettingsDialogGenerator {

    public static void generateAndShowDialog(List<Setting<?>> settings, Runnable onClose) {
        JFrame frame = new JFrame("Настройки");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (Setting<?> setting : settings) {
            final var component = setting.createComponent();
            JPanel settingPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(setting.label());
            settingPanel.add(label, BorderLayout.NORTH);
            settingPanel.add(component, BorderLayout.CENTER);
            panel.add(settingPanel);
        }

        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> frame.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);

        frame.add(new JScrollPane(panel), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                onClose.run();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                onClose.run();
            }
        });
    }
}
