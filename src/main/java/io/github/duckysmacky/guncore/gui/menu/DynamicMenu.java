package io.github.duckysmacky.guncore.gui.menu;

import java.util.ArrayList;
import java.util.List;

import io.github.duckysmacky.guncore.gui.menu.entry.MenuEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;

public abstract class DynamicMenu extends BaseMenu {
    private final List<MenuEntry> entries = new ArrayList<>();

    public DynamicMenu(String title, BaseMenu parent, int rows, int cols) {
        super(title, parent, rows, cols);
    }

    public void addEntry(MenuEntry entry) {
        entries.add(entry);
    }

    @Override
    public void fillInventory(IInventory inventory) {
        int startRow = 1; // offset
        int startCol = 1;
        int maxCols = cols - 2;

        for (int i = 0; i < entries.size(); i++) {
            int row = startRow + i / maxCols;
            int col = startCol + i % maxCols;
            int slot = row * cols + col;
            inventory.setInventorySlotContents(slot, entries.get(i).getIcon());
        }

        // back button
        if (parent != null) {
            inventory.setInventorySlotContents(getBackButtonSlot(), getBackButtonItem());
        }
    }

    @Override
    public void handleClick(int slot, EntityPlayer player) {
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
            player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
            openParent(player);
        }
    }
}