package io.github.duckysmacky.guncore.gui.menu.pages;

import io.github.duckysmacky.guncore.data.config.ConfigManager;
import io.github.duckysmacky.guncore.data.config.catalog.kits.KitClass;
import io.github.duckysmacky.guncore.game.CommandExecutor;
import io.github.duckysmacky.guncore.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.guncore.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.gui.menu.DynamicMenu;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.Comparator;

public class KitsMenuPage extends DynamicMenu {
    public KitsMenuPage(BaseMenu parent, KitClass kitClass) {
        super(kitClass.display + " Kits", parent, 5, 9);

        ConfigManager configManager = ConfigManager.instance();

        configManager.getCachedKits().stream()
            .filter(kit -> kit.getKitClass() == kitClass)
            .sorted(Comparator.comparingInt(kit -> kit.getTier().sortOrder))
            .forEach(kit -> {
                ItemStack kitIcon = kit.getIconItem();

                addEntry(new ActionEntry(kitIcon, (player) -> {
                    player.sendMessage(new TextComponentString("Selected kit: " + kitIcon.getDisplayName()));

                    CommandExecutor.execute(kit.getCommand(player));

                    player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                }));
            });
    }
}
