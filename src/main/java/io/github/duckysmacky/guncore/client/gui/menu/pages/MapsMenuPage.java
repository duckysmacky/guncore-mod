package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.locations.LocationEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionType;

import java.util.List;

public class MapsMenuPage extends DynamicMenu {
    public MapsMenuPage(BaseMenu parent) {
        super("Locations", parent, 4, 9, true);

        List<LocationEntry> locations = ConfigManager.instance().getCatalogManager().getCatalog(CatalogType.LOCATIONS);

        for (MenuSectionEntry map : ConfigManager.instance().getMenuSectionManager().getSections(MenuSectionType.MAPS)) {
            if (locations.stream().noneMatch(location -> location.getMap().equals(map.getId()))) continue;

            addEntry(new SubpageEntry(map.getIcon(), new LocationsMenuPage(this, map)));
        }
    }
}
