package context;

import view.Menu;
import view.ProgressPanel;
import view.ToolBar;

import javax.swing.*;

public record ApplicationComponents(
        JLabel imageLabel,
        ProgressPanel progressPanel,
        JScrollPane scrollPane,
        ToolBar toolBar,
        Menu menuBar
) {
}
