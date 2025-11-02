package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.data.config.ConfigManager;
import io.github.duckysmacky.projectg.data.config.catalog.locations.CityMap;
import io.github.duckysmacky.projectg.data.config.catalog.locations.LocationEntry;
import io.github.duckysmacky.projectg.game.CommandExecutor;
import io.github.duckysmacky.projectg.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.DynamicMenu;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class LocationsMenuPage extends DynamicMenu {
    public LocationsMenuPage(BaseMenu parent, CityMap map) {
        super("Locations", parent, 9, 9);

        ConfigManager configManager = ConfigManager.instance();

        configManager.getCachedLocations().stream()
            .filter(location -> location.getMap() == map)
            .forEach(location -> {
                ItemStack locationIcon = location.getIconItem();

                addEntry(new ActionEntry(locationIcon, (player) -> {
                    LocationEntry.LocationCoordinates coords = location.getCoordinates();
                    String command = String.format("tp %s %d %d %d", player.getName(), coords.x, coords.y, coords.z);
                    CommandExecutor.execute(command);

                    player.sendMessage(new TextComponentString("Teleporting to '" + locationIcon.getDisplayName() + "'"));
                    player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1f, 1f);
                }));
            });
    }
}
