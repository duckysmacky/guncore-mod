package io.github.duckysmacky.projectg.gui.menu;

import io.github.duckysmacky.projectg.gui.MenuEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;

import java.util.HashMap;
import java.util.Map;

public abstract class StaticMenu extends BaseMenu {
    private final Map<Integer, MenuEntry> entries = new HashMap<>();

    public StaticMenu(String title, BaseMenu parent, int rows, int cols) {
        super(title, parent, rows, cols);
    }

    public void addEntry(MenuEntry entry, int row, int col) {
        int slot = row * cols + col;
        entries.put(slot, entry);
    }

    @Override
    public void fillInventory(net.minecraft.inventory.IInventory inventory) {
        for (Map.Entry<Integer, MenuEntry> e : entries.entrySet()) {
            inventory.setInventorySlotContents(e.getKey(), e.getValue().getIcon());
        }

        // back button
        if (parent != null) {
            inventory.setInventorySlotContents(getBackButtonSlot(), getBackButtonItem());
        }
    }

    @Override
    public void handleClick(int slot, EntityPlayer player) {
        if (entries.containsKey(slot)) {
            entries.get(slot).onClick(player);
        } else if (parent != null && slot == getBackButtonSlot()) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
            openParent(player);
        }
    }
}