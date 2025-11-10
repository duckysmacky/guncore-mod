package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitClass;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.server.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.DynamicMenuPage;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public class KitsMenuPage extends DynamicMenuPage {
    public KitsMenuPage(BaseMenuPage parent, KitClass kitClass) {
        super(kitClass.display + " Kits", parent, 5, 9);

        ConfigManager.instance().getCatalogManager().<KitEntry>getCatalog(CatalogType.KITS).stream()
            .filter(kit -> kit.getKitClass() == kitClass)
            .sorted(Comparator.comparingInt(kit -> kit.getTier().sortOrder))
            .forEach(kit -> {
                ItemStack kitIcon = kit.getIconItem();

                addEntry(new ActionEntry(kitIcon, (player) -> {
                    EquipmentController.equipKit(player, kit);

                    player.sendSystemMessage(Component.literal("Selected kit: " + kitIcon.getHoverName()));
                    player.playSound(SoundEvents.PLAYER_LEVELUP, 1f, 1f);

                    open(player);
                }));
            });
    }
}
