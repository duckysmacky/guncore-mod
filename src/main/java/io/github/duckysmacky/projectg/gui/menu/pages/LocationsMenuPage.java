package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.config.ConfigLoader;
import io.github.duckysmacky.projectg.config.catalog.CityMap;
import io.github.duckysmacky.projectg.gui.ActionEntry;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.DynamicMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class LocationsMenuPage extends DynamicMenu {
    public LocationsMenuPage(BaseMenu parent, CityMap map) {
        super("Locations", parent, 7, 9);

        ConfigLoader configLoader = ConfigLoader.instance();

        configLoader.getCachedLocations().stream()
            .filter(location -> location.getMap() == map)
            .forEach(location -> {
                ItemStack locationIcon = location.getIconItem();

                addEntry(new ActionEntry(
                    locationIcon,
                    (player) -> player.sendMessage(new TextComponentString("Selected location: " + locationIcon.getDisplayName()))
                ));
            });
    }
}
