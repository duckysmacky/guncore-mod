package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionType;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WeaponCategoriesMenuPage extends DynamicMenu {
    private final Random random = new Random();
    /** Guns of the shown categories, the random button picks from these */
    private final List<GunEntry> shownGuns = new ArrayList<>();

    public WeaponCategoriesMenuPage(BaseMenu parent, boolean secondary) {
        super(secondary ? "Secondary Weapons" : "Primary Weapons", parent, 4, 9, true);

        CatalogType type = secondary ? CatalogType.SECONDARY_WEAPONS : CatalogType.PRIMARY_WEAPONS;
        List<GunEntry> guns = ConfigManager.instance().getCatalogManager().getCatalog(type);

        for (MenuSectionEntry category : ConfigManager.instance().getMenuSectionManager().getSections(MenuSectionType.GUN_CATEGORIES)) {
            List<GunEntry> categoryGuns = guns.stream()
                .filter(gun -> gun.getCategory().equals(category.getId()))
                .collect(Collectors.toList());
            if (categoryGuns.isEmpty()) continue;

            shownGuns.addAll(categoryGuns);
            addEntry(new SubpageEntry(category.getIcon(), new GunsMenuPage(this, secondary, category)));
        }

        setTopEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.DIAMOND))
                .setName("&f&lRandom Weapon")
                .addLoreLine("&7Select a random weapon")
                .getItemStack(),
            player -> {
                if (shownGuns.isEmpty()) return;
                GunsMenuPage.equip(player, secondary, shownGuns.get(random.nextInt(shownGuns.size())));
            }
        ));
    }
}
