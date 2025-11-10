package io.github.duckysmacky.guncore.server.menu;

import java.util.ArrayList;
import java.util.List;

import io.github.duckysmacky.guncore.server.menu.entries.MenuEntry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;

public abstract class DynamicMenuPage extends BaseMenuPage {
    private final List<MenuEntry> entries = new ArrayList<>();

    public DynamicMenuPage(String title, BaseMenuPage parent, int rows, int cols) {
        super(title, parent, rows, cols);
    }

    public void addEntry(MenuEntry entry) {
        entries.add(entry);
    }

    @Override
    public void fillInventory(Container inventory) {
        int startRow = 1; // offset
        int startCol = 1;
        int maxCols = cols - 2;

        for (int i = 0; i < entries.size(); i++) {
            int row = startRow + i / maxCols;
            int col = startCol + i % maxCols;
            int slot = row * cols + col;
            inventory.setItem(slot, entries.get(i).getIcon());
        }

        // back button
        if (parent != null) {
            inventory.setItem(getBackButtonSlot(), getBackButtonItem());
        }
    }

    @Override
    public void handleClick(int slot, ServerPlayer player) {
        int startRow = 1;
        int startCol = 1;
        int maxCols = cols - 2;

        for (int i = 0; i < entries.size(); i++) {
            int row = startRow + i / maxCols;
            int col = startCol + i % maxCols;
            int entrySlot = row * cols + col;

            if (slot == entrySlot) {
                entries.get(i).onClick(player);
                return;
            }
        }

        // back button
        if (parent != null && slot == getBackButtonSlot()) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 1.0f, 1.0f);
            openParent(player);
        }
    }
}