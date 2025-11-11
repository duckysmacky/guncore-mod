package io.github.duckysmacky.guncore.server.menu.pages.dynamic;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.entries.ArmorEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.ItemEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.game.ServerSoundPlayer;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.DynamicMenuPage;
import io.github.duckysmacky.guncore.server.menu.entries.ActionEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public class ArmorSelectionMenuPage extends DynamicMenuPage {
    private static final EquipmentType equipmentType = EquipmentType.ARMOR;

    public ArmorSelectionMenuPage(BaseMenuPage parent) {
        super(equipmentType.display, parent, 5, 9);

        ConfigManager.instance().getCatalogManager().<ArmorEntry>getCatalog(equipmentType.catalog).stream()
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
