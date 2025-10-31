package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.data.catalog.CatalogLoader;
import io.github.duckysmacky.projectg.data.catalog.kits.KitClass;
import io.github.duckysmacky.projectg.game.CommandExecutor;
import io.github.duckysmacky.projectg.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.DynamicMenu;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;

public class KitsMenuPage extends DynamicMenu {
    public KitsMenuPage(BaseMenu parent, KitClass kitClass) {
        super(kitClass.display + " Kits", parent, 5, 9);

        CatalogLoader catalogLoader = CatalogLoader.instance();

        catalogLoader.getCachedKits().stream()
            .filter(kit -> kit.getKitClass() == kitClass)
            .sorted(Comparator.comparingInt(kit -> kit.getTier().sortOrder))
            .forEach(kit -> {
                ItemStack kitIcon = kit.getIconItem();

                addEntry(new ActionEntry(kitIcon, (player) -> {
                    player.sendMessage(new TextComponentString("Selected kit: " + kitIcon.getDisplayName()));

                    CommandExecutor commandExecutor = new CommandExecutor();
                    commandExecutor.execute(kit.getCommand(player));

                    player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                }));
            });
    }
}
