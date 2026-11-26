package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionType;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.network.packets.EquipmentActionPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponCategoriesMenuPage extends DynamicMenu {
    /** Guns of the shown categories, the random button picks from these */
    private final List<GunEntry> shownGuns = new ArrayList<>();

    public WeaponCategoriesMenuPage(BaseMenu parent, boolean secondary) {
        super(secondary ? "Secondary Weapons" : "Primary Weapons", parent, 4, 9, true);

        CatalogType type = secondary ? CatalogType.SECONDARY_WEAPONS : CatalogType.PRIMARY_WEAPONS;
        List<GunEntry> guns = ConfigManager.instance().getCatalogManager().getCatalog(type);

        for (MenuSectionEntry category : ConfigManager.instance().getMenuSectionManager().getSections(MenuSectionType.GUN_CATEGORIES)) {
            List<GunEntry> categoryGuns = guns.stream()
                .filter(gun -> gun.getCategory().equals(category.getId()))
                .filter(gun -> ClientGameInfo.instance().isAllowed(gun.getRarity()))
                .collect(Collectors.toList());
            if (categoryGuns.isEmpty()) continue;

            shownGuns.addAll(categoryGuns);
            addEntry(new SubpageEntry(category.getIcon(), new GunsMenuPage(this, secondary, category)));
        }

        setDeselectEntry("&7Deselect your " + (secondary ? "secondary" : "main") + " weapon", player -> EquipmentActionPacket.send(EquipmentActionPacket.Action.DESELECT, secondary ? EquipmentType.SECONDARY_WEAPON : EquipmentType.MAIN_WEAPON));
        setRandomEntry("&7Select a random weapon from any category", shownGuns, (player, gun) -> GunsMenuPage.equip(player, secondary, gun));
    }
}
