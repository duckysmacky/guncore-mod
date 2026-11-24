package io.github.duckysmacky.guncore.client.gui.menu;

import io.github.duckysmacky.guncore.client.gui.menu.entries.MenuEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public abstract class StaticMenu extends BaseMenu {
    private final Map<Integer, MenuEntry> entries = new HashMap<>();
    private final Map<Integer, BooleanSupplier> visibility = new HashMap<>();

    public StaticMenu(String title, BaseMenu parent, int rows, int cols) {
        super(title, parent, rows, cols);
    }

    public void addEntry(MenuEntry entry, int row, int col) {
        int slot = row * cols + col;
        entries.put(slot, entry);
    }

    /**
     * Adds an entry which is only shown and clickable while {@code visible} is true
     */
    public void addEntry(MenuEntry entry, int row, int col, BooleanSupplier visible) {
        addEntry(entry, row, col);
        visibility.put(row * cols + col, visible);
    }

    private boolean isShown(int slot) {
        BooleanSupplier visible = visibility.get(slot);
        return visible == null || visible.getAsBoolean();
    }

    @Override
    public void fillInventory(net.minecraft.inventory.IInventory inventory) {
        for (Map.Entry<Integer, MenuEntry> e : entries.entrySet()) {
            inventory.setInventorySlotContents(e.getKey(), isShown(e.getKey()) ? e.getValue().getIcon() : ItemStack.EMPTY);
        }

        // back button
        if (parent != null) {
            inventory.setInventorySlotContents(getBackButtonSlot(), getBackButtonItem());
        }
    }

    @Override
    public void handleClick(int slot, EntityPlayer player) {
        if (entries.containsKey(slot) && isShown(slot)) {
            entries.get(slot).onClick(player);
        } else if (parent != null && slot == getBackButtonSlot()) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
            openParent(player);
        }
    }
}