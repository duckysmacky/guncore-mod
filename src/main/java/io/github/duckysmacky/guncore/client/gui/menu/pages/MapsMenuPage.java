package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.locations.LocationEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionType;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MapsMenuPage extends DynamicMenu {
    private final Random random = new Random();
    /** Locations of the shown maps, the random button picks from these */
    private final List<LocationEntry> shownLocations = new ArrayList<>();

    public MapsMenuPage(BaseMenu parent) {
        super("Locations", parent, 4, 9, true);

        List<LocationEntry> locations = ConfigManager.instance().getCatalogManager().getCatalog(CatalogType.LOCATIONS);

        for (MenuSectionEntry map : ConfigManager.instance().getMenuSectionManager().getSections(MenuSectionType.MAPS)) {
            List<LocationEntry> mapLocations = locations.stream()
                .filter(location -> location.getMap().equals(map.getId()))
                .collect(Collectors.toList());
            if (mapLocations.isEmpty()) continue;

            shownLocations.addAll(mapLocations);
            addEntry(new SubpageEntry(map.getIcon(), new LocationsMenuPage(this, map)));
        }

        setTopEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.DIAMOND))
                .setName("&f&lRandom Location")
                .addLoreLine("&7Teleport to a random location")
                .getItemStack(),
            player -> {
                if (shownLocations.isEmpty()) return;
                LocationsMenuPage.teleport(player, shownLocations.get(random.nextInt(shownLocations.size())));
            }
        ));
    }
}
