package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.StaticMenuPage;
import io.github.duckysmacky.guncore.server.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class EquipmentMenuPage extends StaticMenuPage {
    public EquipmentMenuPage(BaseMenuPage parent) {
        super("Equipment", parent, 3, 9);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.CHEST))
                .setName("&f&lKits")
                .addLoreLine("&7Choose your kit which contains armor and items to use in battle")
                .addLoreLine("&c&lWARNING: &7Equipping any kit clears inventory, recommended to equip a kit first")
                .getItemStack(),
            new KitCategoryMenuPage(this)
        ), 1, 1);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.BOW))
                .setName("&f&lWeapons")
                .addLoreLine("&7Select a main and a secondary weapon to fight with")
                .getItemStack(),
            new WeaponsMenuPage(this)
        ), 1, 4);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.DIAMOND_PICKAXE))
                .setName("&f&lGadgets")
                .addLoreLine("&7Select any gadget to assist you in battle")
                .addLoreLine("&7Gadgets are unique items that provide additional abilities or utilities")
                .getItemStack(),
            new GadgetsMenuPage(this)
        ), 1, 7);
    }
}
