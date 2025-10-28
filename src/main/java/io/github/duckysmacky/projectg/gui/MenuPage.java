package io.github.duckysmacky.projectg.gui;

import io.github.duckysmacky.projectg.util.ItemStackCustomizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;

public abstract class MenuPage {
    protected final String title;
    protected final MenuPage parent;
    protected final int rows;
    protected final int cols;

    public MenuPage(String title, MenuPage parent, int rows, int cols) {
        this.title = title;
        this.parent = parent;
        this.rows = rows;
        this.cols = cols;
    }

    public void open(EntityPlayer player) {
        InventoryBasic inventory = new InventoryBasic(title, true, rows * cols);
        fillInventory(inventory);

        FMLClientHandler.instance().displayGuiScreen(player, new MenuWrapperGui(inventory, this, player));
    }

    public void openParent(EntityPlayer player) {
        if (parent != null) {
            parent.open(player);
        }
    }

    public String getTitle() {
        return title;
    }

    public MenuPage getParent() {
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
    public abstract void fillInventory(IInventory inventory);

    /**
     * Handle click on a slot
     */
    public abstract void handleClick(int slot, EntityPlayer player);
}