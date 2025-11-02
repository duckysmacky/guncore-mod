package io.github.duckysmacky.guncore.gui;

import io.github.duckysmacky.guncore.gui.menu.BaseMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class MenuContainer extends Container {
    private final IInventory inventory;
    private final BaseMenu menuPage;
    private final int rows;
    private final int cols;

    public MenuContainer(IInventory inventory, BaseMenu menuPage) {
        this.inventory = inventory;
        this.menuPage = menuPage;
        this.rows = menuPage.getRows();
        this.cols = menuPage.getCols();

        int guiWidth = cols * 18;
        int startX = (guiWidth - cols * 18) / 2 + 8; // horizontal offset for centering
        int startY = 17;

        // Add menu slots
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int slotIndex = row * cols + col;
                int x = startX + col * 18;
                int y = startY + row * 18;
                addSlotToContainer(new Slot(inventory, slotIndex, x, y));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (slotId >= 0 && slotId < inventory.getSizeInventory()) {
            menuPage.handleClick(slotId, player);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY;
    }
}