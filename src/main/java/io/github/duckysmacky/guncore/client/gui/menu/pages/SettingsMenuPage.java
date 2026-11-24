package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SettingsMenuPage extends StaticMenu {
    public SettingsMenuPage(BaseMenu parent) {
        super("Settings", parent, 3, 9);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLDEN_SWORD))
                .setName("&f&lGame")
                .addLoreLine("&7Game mode, win condition and round rules")
                .getItemStack(),
            new GameSettingsMenuPage(this)
        ), 1, 2);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.GRASS))
                .setName("&f&lWorld")
                .addLoreLine("&7Gamerules, time and other world settings")
                .getItemStack(),
            new WorldSettingsMenuPage(this)
        ), 1, 4);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.IRON_CHESTPLATE))
                .setName("&f&lEquipment")
                .addLoreLine("&7Configure the equipment system, allowed items, etc.")
                .getItemStack(),
            new EquipmentSettingsMenuPage(this)
        ), 1, 6);
    }
}
