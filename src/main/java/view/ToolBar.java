package view;

import javax.swing.*;
import java.util.List;

public class ToolBar {
    private final List<JButton> buttons;
    private final JToggleButton showOriginalImageButton;
    private final JToolBar toolbarComponent;

    public ToolBar(JToolBar toolbarComponent, List<JButton> buttons, JToggleButton showOriginalImageButton) {
        this.buttons = buttons;
        this.showOriginalImageButton = showOriginalImageButton;
        this.toolbarComponent = toolbarComponent;
    }

    public List<JButton> getButtons() {
        return buttons;
    }

    public JToggleButton getShowOriginalImageButton() {
        return showOriginalImageButton;
    }

    public JToolBar getToolbarComponent() {
        return toolbarComponent;
    }

    public void setEnabledAllButtons(boolean isEnable) {
        buttons.forEach(button -> {
            button.setEnabled(isEnable);
        });
        showOriginalImageButton.setEnabled(isEnable);
    }
}
