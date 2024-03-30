package context;

import view.ProgressPanel;

import javax.swing.*;

public record ApplicationComponents(
        JLabel imageLabel,
        ProgressPanel progressPanel,
        JScrollPane scrollPane,
        JToggleButton showOriginalImageButton
) {
}
