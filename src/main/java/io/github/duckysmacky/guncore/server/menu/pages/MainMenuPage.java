package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.server.menu.StaticMenuPage;
import io.github.duckysmacky.guncore.server.menu.entries.SubpageEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MainMenuPage extends StaticMenuPage {
    public MainMenuPage() {
        super("Menu", null, 3, 9);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.IRON_CHESTPLATE))
                .setName("&f&lEquipment")
                .addLoreLine("&7Choose your equipment")
                .getItemStack(),
            new EquipmentMenuPage(this)
        ), 1, 1);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.COMPASS))
                .setName("&f&lLocations")
                .addLoreLine("&7Choose a location to fight in")
                .getItemStack(),
            new MapsMenuPage(this)
        ), 1, 3);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.CLOCK))
                .setName("&f&lGame")
                .addLoreLine("&7Set the game modes, join teams and other")
                .getItemStack(),
            new GameMenuPage(this)
        ), 1, 5);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.COMPARATOR))
                .setName("&f&lSettings")
                .addLoreLine("&7Adjust different settings")
                .getItemStack(),
            new SettingsMenuPage(this)
        ), 1, 7);
    }
}
