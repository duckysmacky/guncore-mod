package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.DynamicMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionEntry;
import io.github.duckysmacky.guncore.common.config.menus.MenuSectionType;

import java.util.ArrayList;
import java.util.List;

public class KitCategoryMenuPage extends DynamicMenu {
    /** Kits of the shown classes, the random button picks from these */
    private final List<KitEntry> shownKits = new ArrayList<>();

    public KitCategoryMenuPage(BaseMenu parent) {
        super("Kits", parent, 4, 9, true);

        List<KitEntry> kits = ConfigManager.instance().getCatalogManager().getCatalog(CatalogType.KITS);

        for (MenuSectionEntry kitClass : ConfigManager.instance().getMenuSectionManager().getSections(MenuSectionType.KIT_CLASSES)) {
            int before = shownKits.size();
            kits.stream()
                .filter(kit -> kit.getKitClass().equals(kitClass.getId()))
                .filter(kit -> ClientGameInfo.instance().isAllowed(kit.getRarity()))
                .forEach(shownKits::add);
            if (shownKits.size() == before) continue;

            addEntry(new SubpageEntry(kitClass.getIcon(), new KitsMenuPage(this, kitClass)));
        }

        setRandomEntry("&7Select a random kit from any class", shownKits, KitsMenuPage::equip);
    }
}
