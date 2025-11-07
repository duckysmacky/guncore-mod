package io.github.duckysmacky.guncore.client.gui.menu;

import io.github.duckysmacky.guncore.client.gui.menu.pages.MainMenuPage;

public class MenuManager {
    private static MenuManager instance;
    private BaseMenu mainMenu;
    private BaseMenu lastOpenedMenu;

    private MenuManager() {
        this.mainMenu = new MainMenuPage();
    }

    public static MenuManager instance() {
        if (instance == null) {
            instance = new MenuManager();
        }

        return instance;
    }

    public void refreshMenu() {
        this.mainMenu = new MainMenuPage();
        lastOpenedMenu = null;
    }

    public void setLastOpenedMenu(BaseMenu lastOpenedMenu) {
        this.lastOpenedMenu = lastOpenedMenu;
    }

    public BaseMenu getLastOpenedMenu() {
        return lastOpenedMenu;
    }

    public BaseMenu getMainMenu() {
        return mainMenu;
    }
}
