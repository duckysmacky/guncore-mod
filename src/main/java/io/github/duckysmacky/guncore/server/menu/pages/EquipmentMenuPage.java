package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.entries.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.entries.EquippableEntry;
import io.github.duckysmacky.guncore.common.game.EquipmentType;
import io.github.duckysmacky.guncore.common.game.ServerSoundPlayer;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.StaticMenuPage;
import io.github.duckysmacky.guncore.server.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.server.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.server.menu.pages.dynamic.ArmorSelectionMenuPage;
import io.github.duckysmacky.guncore.server.menu.pages.dynamic.ItemSelectionMenuPage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Random;

public class EquipmentMenuPage extends StaticMenuPage {
    private final Random random;

    public EquipmentMenuPage(BaseMenuPage parent) {
        super("Equipment", parent, 7, 9);
        this.random = new Random();

        int row = 1;
        int col = 3;
        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.CROSSBOW))
                .setName("&f&lMain weapon")
                .getItemStack(),
            new MainWeaponsMenuPage(this)
        ), row, col);
        addRandomButton(row, col, EquipmentType.MAIN_WEAPON);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.BOW))
                .setName("&f&lSecondary weapon")
                .getItemStack(),
            new SecondaryWeaponsMenuPage(this)
        ), row, ++col);
        addRandomButton(row, col, EquipmentType.SECONDARY_WEAPON);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.DIAMOND_CHESTPLATE))
                .setName("&f&lArmor")
                .getItemStack(),
            new ArmorSelectionMenuPage(this)
        ), row, ++col);
        addRandomButton(row, col, EquipmentType.ARMOR);

        row = 4;
        col = 2;
        addItemButton(row, col, EquipmentType.LETHAL, Items.TNT);
        addItemButton(row, ++col, EquipmentType.TACTICAL, Items.COBWEB);
        addItemButton(row, ++col, EquipmentType.GADGET, Items.BEACON);
        addItemButton(row, ++col, EquipmentType.UTILITY, Items.IRON_PICKAXE);
        addItemButton(row, ++col, EquipmentType.PERK, Items.EXPERIENCE_BOTTLE);
    }

    private void addItemButton(int row, int col, EquipmentType type, Item icon) {
        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(icon))
                .setName("&f&l" + type.display)
                .getItemStack(),
            new ItemSelectionMenuPage(this, type)
        ), row, col);
        addRandomButton(row, col, type);
    }

    private void addRandomButton(int row, int col, EquipmentType type) {
        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.RECOVERY_COMPASS))
                .setName("&e&lSelect random")
                .getItemStack(),
            p -> selectRandom(p, type)
        ), row + 1, col);
    }

    private void selectRandom(ServerPlayer player, EquipmentType equipmentType) {
        List<CatalogEntry> entries = ConfigManager.instance().getCatalogManager().getCatalog(equipmentType.catalog);

        int randomIndex = random.nextInt(entries.size());
        EquippableEntry item = (EquippableEntry) entries.get(randomIndex);

        EquipmentController.equip(player, equipmentType, item);

        player.sendSystemMessage(Component.literal(TextUtils.translateColorCodes(String.format(
            "&eRandom %s selected:&r %s", equipmentType.display, item.getItemStack().getDisplayName().getString()
        ))));
        ServerSoundPlayer.playFor(player, SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }
}
