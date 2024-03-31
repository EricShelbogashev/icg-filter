package view;

import javax.swing.*;
import java.util.List;

public class Menu {
    private final List<JMenu> menuList;
    private final JMenuBar menuBarView;

    public Menu(JMenuBar menuBarView, List<JMenu> menuList) {
        this.menuList = menuList;
        this.menuBarView = menuBarView;
    }

    public List<JMenu> getMenuList() {
        return menuList;
    }

    public void setEnabled(boolean isEnabled) {
        menuList.forEach(menuButton -> {
            menuButton.setEnabled(isEnabled);
        });
    }

    public JMenuBar getMenuBarView() {
        return menuBarView;
    }
}
