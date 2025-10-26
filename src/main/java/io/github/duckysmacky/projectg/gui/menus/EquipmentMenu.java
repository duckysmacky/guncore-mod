package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.StaticMenuPage;
import io.github.duckysmacky.projectg.gui.SubpageEntry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import io.github.duckysmacky.projectg.gui.MenuPage;

public class EquipmentMenu extends StaticMenuPage {
    public EquipmentMenu(MenuPage parent) {
        super("Equipment", parent, 5, 9);

        addEntry(new SubpageEntry(new ItemStack(Items.DIAMOND_SWORD), new KitsMenu(this)), 1, 1);
        addEntry(new SubpageEntry(new ItemStack(Items.BOW), new WeaponsMenu(this)), 1, 3);
        addEntry(new SubpageEntry(new ItemStack(Items.ELYTRA), new GadgetsMenu(this)), 1, 5);
    }
}
