package io.github.duckysmacky.projectg.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public abstract class DynamicMenuPage extends MenuPage {
    private final List<MenuEntry> entries = new ArrayList<>();

    public DynamicMenuPage(String title, MenuPage parent, int rows, int cols) {
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
            inventory.setInventorySlotContents(slot, entries.get(i).getItemIcon());
        }

        // back button
        if (parent != null) {
            inventory.setInventorySlotContents(getBackButtonSlot(), getBackButtonIcon());
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
            openParent(player);
        }
    }
}