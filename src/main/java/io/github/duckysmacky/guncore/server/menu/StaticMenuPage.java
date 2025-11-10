package io.github.duckysmacky.guncore.server.menu;

import io.github.duckysmacky.guncore.server.menu.entries.MenuEntry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;

import java.util.HashMap;
import java.util.Map;

public abstract class StaticMenuPage extends BaseMenuPage {
    private final Map<Integer, MenuEntry> entries = new HashMap<>();

    public StaticMenuPage(String title, BaseMenuPage parent, int rows, int cols) {
        super(title, parent, rows, cols);
    }

    public void addEntry(MenuEntry entry, int row, int col) {
        int slot = row * cols + col;
        entries.put(slot, entry);
    }

    @Override
    public void fillInventory(Container inventory) {
        for (Map.Entry<Integer, MenuEntry> e : entries.entrySet()) {
            inventory.setItem(e.getKey(), e.getValue().getIcon());
        }

        // back button
        if (parent != null) {
            inventory.setItem(getBackButtonSlot(), getBackButtonItem());
        }
    }

    @Override
    public void handleClick(int slot, ServerPlayer player) {
        if (entries.containsKey(slot)) {
            entries.get(slot).onClick(player);
        } else if (parent != null && slot == getBackButtonSlot()) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 1.0f, 1.0f);
            openParent(player);
        }
    }
}