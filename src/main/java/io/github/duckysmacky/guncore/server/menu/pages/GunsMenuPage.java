package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunCategory;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.server.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.server.menu.DynamicMenuPage;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.common.config.catalog.guns.GunEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public class GunsMenuPage extends DynamicMenuPage {
    public GunsMenuPage(BaseMenuPage parent, GunCategory gunCategory) {
        super(gunCategory.display + "s", parent, 6, 9);

        ConfigManager.instance().getCatalogManager().<GunEntry>getCatalog(CatalogType.GUNS).stream()
            .filter(gun -> gun.getCategory() == gunCategory)
            .sorted(Comparator.comparingInt(gun -> gun.getRarity().sortOrder))
            .forEach(gun -> {
                ItemStack gunItem = gun.getGunItemStack();

                addEntry(new ActionEntry(gunItem, player -> {
                    EquipmentController.equipGun(player, gun);

                    player.sendSystemMessage(Component.literal("Selected gun: " + gunItem.getHoverName()));
                    player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f);

                    open(player);
                }));
            });
    }
}
