package io.github.duckysmacky.guncore.client.gui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.MenuEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Menu with a variable amount of entries. Entries fill the inner area (everything except the outer ring of slots);
 * when they do not fit, extra pages are added and navigated with buttons on both sides of the back button. Optionally
 * rows are centered horizontally and an extra entry sits in the middle of the top row
 */
public abstract class DynamicMenu extends BaseMenu {
    private final List<MenuEntry> entries = new ArrayList<>();
    private final Random random = new Random();
    private final boolean centered;
    private MenuEntry topEntry;
    private int page = 0;

    public DynamicMenu(String title, BaseMenu parent, int rows, int cols) {
        this(title, parent, rows, cols, false);
    }

    public DynamicMenu(String title, BaseMenu parent, int rows, int cols, boolean centered) {
        super(title, parent, rows, cols);
        this.centered = centered;
    }

    public void addEntry(MenuEntry entry) {
        entries.add(entry);
    }

    /** Entry shown on every page in the middle of the top row */
    public void setTopEntry(MenuEntry entry) {
        topEntry = entry;
    }

    /** Top entry which runs {@code action} on a random element of {@code pool} */
    public <E> void setRandomEntry(String lore, List<E> pool, BiConsumer<EntityPlayer, E> action) {
        setTopEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.NETHER_STAR))
                .setName("&f&lSelect random")
                .addLoreLine(lore)
                .getItemStack(),
            player -> {
                if (pool.isEmpty()) return;
                action.accept(player, pool.get(random.nextInt(pool.size())));
            }
        ));
    }

    private int topEntrySlot() {
        return cols / 2;
    }

    private int perPage() {
        return (rows - 2) * (cols - 2);
    }

    private int pageCount() {
        return Math.max(1, (entries.size() + perPage() - 1) / perPage());
    }

    /** Slot of the n-th of the current page's {@code pageSize} entries */
    private int innerSlot(int n, int pageSize) {
        int maxCols = cols - 2;
        int row = n / maxCols;
        int offset = centered ? (maxCols - Math.min(maxCols, pageSize - row * maxCols)) / 2 : 0;
        return (1 + row) * cols + 1 + offset + n % maxCols;
    }

    private int previousPageSlot() {
        return getBackButtonSlot() - 1;
    }

    private int nextPageSlot() {
        return getBackButtonSlot() + 1;
    }

    private ItemStack pageButtonItem(String name, String lore) {
        return new ItemStackCustomizer(new ItemStack(Items.ARROW))
            .setName(name)
            .addLoreLine(lore)
            .getItemStack();
    }

    /** Every fresh open starts on the first page; page buttons use {@link #changePage} instead */
    @Override
    public void open(EntityPlayer player) {
        page = 0;
        super.open(player);
    }

    @Override
    public void fillInventory(IInventory inventory) {
        inventory.clear();

        int first = page * perPage();
        int last = Math.min(entries.size(), first + perPage());
        for (int i = first; i < last; i++) {
            inventory.setInventorySlotContents(innerSlot(i - first, last - first), entries.get(i).getIcon());
        }

        if (topEntry != null) {
            inventory.setInventorySlotContents(topEntrySlot(), topEntry.getIcon());
        }

        // back button
        if (parent != null) {
            inventory.setInventorySlotContents(getBackButtonSlot(), getBackButtonItem());
        }

        String pageLore = "&7Page " + (page + 1) + "/" + pageCount();
        if (page > 0) {
            inventory.setInventorySlotContents(previousPageSlot(), pageButtonItem("&e&lPrevious Page", pageLore));
        }
        if (page < pageCount() - 1) {
            inventory.setInventorySlotContents(nextPageSlot(), pageButtonItem("&e&lNext Page", pageLore));
        }
    }

    @Override
    public void handleClick(int slot, EntityPlayer player) {
        int first = page * perPage();
        int last = Math.min(entries.size(), first + perPage());
        for (int i = first; i < last; i++) {
            if (slot == innerSlot(i - first, last - first)) {
                entries.get(i).onClick(player);
                return;
            }
        }

        if (topEntry != null && slot == topEntrySlot()) {
            topEntry.onClick(player);
            return;
        }

        // back button
        if (parent != null && slot == getBackButtonSlot()) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
            openParent(player);
        } else if (page > 0 && slot == previousPageSlot()) {
            changePage(-1, player);
        } else if (page < pageCount() - 1 && slot == nextPageSlot()) {
            changePage(1, player);
        }
    }

    private void changePage(int delta, EntityPlayer player) {
        player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
        page += delta;
        super.open(player);
    }
}
