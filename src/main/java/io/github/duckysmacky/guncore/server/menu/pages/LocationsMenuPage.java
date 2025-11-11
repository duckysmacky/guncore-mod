package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.entries.CityMap;
import io.github.duckysmacky.guncore.common.config.catalog.entries.LocationEntry;
import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.server.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.DynamicMenuPage;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class LocationsMenuPage extends DynamicMenuPage {
    public LocationsMenuPage(BaseMenuPage parent, CityMap map) {
        super("Locations", parent, 9, 9);

        ConfigManager.instance().getCatalogManager().<LocationEntry>getCatalog(CatalogType.LOCATIONS).stream()
            .filter(location -> location.getMap() == map)
            .forEach(location -> {
                ItemStack locationIcon = location.getIconItem();

                addEntry(new ActionEntry(locationIcon, (player) -> {
                    LocationEntry.LocationCoordinates coords = location.getCoordinates();
                    String command = String.format("tp %s %d %d %d", player.getScoreboardName(), coords.x(), coords.y(), coords.z());
                    CommandExecutor.execute(command);

                    player.sendSystemMessage(Component.literal("Teleporting to '" + locationIcon.getHoverName().getString() + "'"));
                    player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1f, 1f);
                }));
            });
    }
}
