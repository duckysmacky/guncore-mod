package io.github.duckysmacky.guncore.server.menu.pages.dynamic;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.entries.GunCategory;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.game.ServerSoundPlayer;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.server.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.server.menu.DynamicMenuPage;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.common.config.catalog.entries.GunEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public class WeaponSelectionMenuPage extends DynamicMenuPage {
    public WeaponSelectionMenuPage(BaseMenuPage parent, EquipmentType equipmentType, GunCategory gunCategory) {
        super(gunCategory.display + "s", parent, 6, 9);

        ConfigManager.instance().getCatalogManager().<GunEntry>getCatalog(equipmentType.catalog).stream()
            .filter(gun -> gun.getCategory() == gunCategory)
            .sorted(Comparator.comparingInt(gun -> gun.getRarity().sortOrder))
            .forEach(gun -> {
                ItemStack icon = gun.getIcon();

                addEntry(new ActionEntry(icon, player -> {
                    EquipmentController.equip(player, equipmentType, gun);

                    player.sendSystemMessage(Component.literal(TextUtils.translateColorCodes(String.format(
                        "&a%s selected:&r %s", equipmentType.display, icon.getDisplayName().getString()
                    ))));
                    ServerSoundPlayer.playFor(player, SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f);

                    open(player);
                }));
            });
    }
}
