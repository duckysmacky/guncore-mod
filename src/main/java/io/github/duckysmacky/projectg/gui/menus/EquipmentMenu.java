package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class EquipmentMenu extends StaticMenuPage {
    public EquipmentMenu(MenuPage parent) {
        super("Equipment", parent, 3, 9);

        addEntry(new SubpageEntry(
            new MenuIcon(new ItemStack(Blocks.CHEST))
                .setName("&f&lKits")
                .addLoreLine("&7Choose your kit for battle")
                .addLoreLine("&c&lWARNING: &7Equipping any kit clears inventory, recommended to equip a kit first"),
            new KitsMenu(this)
        ), 1, 1);

        addEntry(new SubpageEntry(
            new MenuIcon(new ItemStack(Items.BOW))
                .setName("&f&lWeapons")
                .addLoreLine("&7Select a main weapon and a sidearm"),
            new WeaponsMenu(this)
        ), 1, 4);

        addEntry(new ActionEntry(
            new MenuIcon(new ItemStack(Items.DIAMOND_PICKAXE))
                .setName("&f&lGadgets")
                .addLoreLine("&7Select a gadget to use in battle"),
            (player) -> player.sendMessage(new TextComponentString("Gadgets clicked!"))
        ), 1, 7);
    }
}
