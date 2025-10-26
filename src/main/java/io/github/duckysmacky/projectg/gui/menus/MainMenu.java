package io.github.duckysmacky.projectg.gui.menus;

import io.github.duckysmacky.projectg.gui.StaticMenuPage;
import io.github.duckysmacky.projectg.gui.SubpageEntry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MainMenu extends StaticMenuPage {
    public MainMenu() {
        super("Menu", null, 3, 9);

        // Equipment
        addEntry(new SubpageEntry(new ItemStack(Items.IRON_CHESTPLATE), new EquipmentMenu(this)), 1, 1);
        // Locations
        addEntry(new SubpageEntry(new ItemStack(Items.COMPASS), new LocationsMenu(this)), 1, 3);
        // Settings
        addEntry(new SubpageEntry(new ItemStack(Items.COMPARATOR), new SettingsMenu(this)), 1, 5);
    }
}
