package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.gui.*;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.StaticMenu;
import io.github.duckysmacky.projectg.util.ItemStackCustomizer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class EquipmentMenuPage extends StaticMenu {
    public EquipmentMenuPage(BaseMenu parent) {
        super("Equipment", parent, 3, 9);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Blocks.CHEST))
                .setName("&f&lKits")
                .addLoreLine("&7Choose your kit which contains armor and items to use in battle")
                .addLoreLine("&c&lWARNING: &7Equipping any kit clears inventory, recommended to equip a kit first")
                .getItemStack(),
            new KitCategoryMenuPage(this)
        ), 1, 1);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.BOW))
                .setName("&f&lWeapons")
                .addLoreLine("&7Select a main and a secondary weapon to fight with")
                .getItemStack(),
            new WeaponsMenuPage(this)
        ), 1, 4);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.DIAMOND_PICKAXE))
                .setName("&f&lGadgets")
                .addLoreLine("&7Select any gadget to assist you in battle")
                .addLoreLine("&7Gadgets are unique items that provide additional abilities or utilities")
                .getItemStack(),
            new GadgetsMenuPage(this)
        ), 1, 7);
    }
}
