package io.github.duckysmacky.guncore.server.menu;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkHooks;

public abstract class BaseMenuPage {
    protected final String title;
    protected final BaseMenuPage parent;
    protected final int rows;
    protected final int cols;

    public BaseMenuPage(String title, BaseMenuPage parent, int rows, int cols) {
        this.title = title;
        this.parent = parent;
        this.rows = rows;
        // TODO: make this dynamic
        this.cols = 9;
    }

    public void open(ServerPlayer player) {
        SimpleContainer inventory = new SimpleContainer(rows * cols);
        fillInventory(inventory);

        NetworkHooks.openScreen(player, new SimpleMenuProvider(
            (windowId, playerInv, p) -> new MenuPageContainer(windowId, inventory, this),
            Component.literal(title)
        ), buf -> {
            buf.writeUtf(title);
            buf.writeInt(rows);
            buf.writeInt(cols);
        });
    }

    public void openParent(ServerPlayer player) {
        if (parent != null) {
            parent.open(player);
        }
    }

    public String getTitle() {
        return title;
    }

    public BaseMenuPage getParent() {
        return parent;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    /**
     * Returns the reserved slot for the back button
     */
    public int getBackButtonSlot() {
        return (rows - 1) * cols + cols / 2;
    }

    /**
     * Returns the reserved icon for the back button
     */
    public ItemStack getBackButtonItem() {
        return new ItemStackCustomizer(new ItemStack(Blocks.BARRIER))
            .setName("&c&lBack")
            .addLoreLine("&7Return to the previous menu")
            .getItemStack();
    }

    /**
     * Fill the inventory with the page's items
     */
    public abstract void fillInventory(Container inventory);

    /**
     * Handle click on a slot
     */
    public abstract void handleClick(int slot, ServerPlayer player);
}