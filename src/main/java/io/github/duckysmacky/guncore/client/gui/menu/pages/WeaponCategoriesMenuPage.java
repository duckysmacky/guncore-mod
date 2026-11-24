package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionType;

import java.util.List;

public class WeaponCategoriesMenuPage extends DynamicMenu {
    public WeaponCategoriesMenuPage(BaseMenu parent, boolean secondary) {
        super(secondary ? "Secondary Weapons" : "Primary Weapons", parent, 4, 9, true);

        CatalogType type = secondary ? CatalogType.SECONDARY_WEAPONS : CatalogType.PRIMARY_WEAPONS;
        List<GunEntry> guns = ConfigManager.instance().getCatalogManager().getCatalog(type);

        for (MenuSectionEntry category : ConfigManager.instance().getMenuSectionManager().getSections(MenuSectionType.GUN_CATEGORIES)) {
            if (guns.stream().noneMatch(gun -> gun.getCategory().equals(category.getId()))) continue;

            addEntry(new SubpageEntry(category.getIcon(), new GunsMenuPage(this, secondary, category)));
        }
    }
}
