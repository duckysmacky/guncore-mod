package io.github.duckysmacky.guncore.server.menu.pages.dynamic;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.game.ServerSoundPlayer;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.server.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.DynamicMenuPage;
import io.github.duckysmacky.guncore.common.config.catalog.entries.ItemEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public class ItemSelectionMenuPage extends DynamicMenuPage {
    public ItemSelectionMenuPage(BaseMenuPage parent, EquipmentType equipmentType) {
        super(equipmentType.display, parent, 9, 9);

        ConfigManager.instance().getCatalogManager().<ItemEntry>getCatalog(equipmentType.catalog).stream()
            .sorted(Comparator.comparingInt(item -> item.getRarity().sortOrder))
            .forEach(item -> {
                ItemStack icon = item.getIcon();

                addEntry(new ActionEntry(icon, player -> {
                    EquipmentController.equip(player, equipmentType, item);

                    player.sendSystemMessage(Component.literal(TextUtils.translateColorCodes(String.format(
                        "&a%s selected:&r %s", equipmentType.display, icon.getDisplayName().getString()
                    ))));
                    ServerSoundPlayer.playFor(player, SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f);

                    open(player);
                }));
            });
    }
}
