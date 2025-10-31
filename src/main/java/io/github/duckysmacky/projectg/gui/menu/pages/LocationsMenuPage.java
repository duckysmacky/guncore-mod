package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.data.config.ConfigManager;
import io.github.duckysmacky.projectg.data.config.catalog.locations.CityMap;
import io.github.duckysmacky.projectg.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.DynamicMenu;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class LocationsMenuPage extends DynamicMenu {
    public LocationsMenuPage(BaseMenu parent, CityMap map) {
        super("Locations", parent, 7, 9);

        ConfigManager configManager = ConfigManager.instance();

        configManager.getCachedLocations().stream()
            .filter(location -> location.getMap() == map)
            .forEach(location -> {
                ItemStack locationIcon = location.getIconItem();

                addEntry(new ActionEntry(locationIcon, (player) -> {
                    player.sendMessage(new TextComponentString("Teleporting to '" + locationIcon.getDisplayName() + "'"));

                    player.setPositionAndUpdate(
                        location.getCoordinates().x,
                        location.getCoordinates().y,
                        location.getCoordinates().z
                    );

                    player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1f, 1f);
                }));
            });
    }
}
