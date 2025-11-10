package io.github.duckysmacky.guncore.server.menu;

import io.github.duckysmacky.guncore.server.menu.pages.MainMenuPage;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager {
    private static MenuManager instance;
    private final Map<UUID, BaseMenuPage> lastOpenedMenus;
    private BaseMenuPage mainMenu;

    private MenuManager() {
        this.lastOpenedMenus = new HashMap<>();
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
    }

    public void setLastOpenedMenu(Player player, BaseMenuPage lastOpenedMenu) {
        lastOpenedMenus.put(player.getUUID(), lastOpenedMenu);
    }

    public BaseMenuPage getLastOpenedMenu(Player player) {
        return lastOpenedMenus.get(player.getUUID());
    }

    public BaseMenuPage getMainMenu() {
        return this.mainMenu;
    }
}
