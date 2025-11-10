package io.github.duckysmacky.guncore.server.menu;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.server.menu.pages.MainMenuPage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MenuPageContainer extends AbstractContainerMenu {
    private final Container inventory;
    private final BaseMenuPage menuPage;
    private final String title;
    private final int rows, cols;

    public MenuPageContainer(int containerId, Container inventory) {
        this(containerId, inventory, new MainMenuPage());
    }

    public MenuPageContainer(int containerId, Container inventory, BaseMenuPage menuPage) {
        this(containerId, inventory, menuPage, menuPage.title, menuPage.rows, menuPage.cols);
    }

    public MenuPageContainer(int containerId, Container inventory, BaseMenuPage menuPage, String title, int rows, int cols) {
        super(GuncoreMod.GAME_MENU.get(), containerId);
        this.inventory = inventory;
        this.menuPage = menuPage;
        this.title = title;
        this.rows = rows;
        this.cols = cols;

        checkContainerSize(inventory, rows * cols);

        int guiWidth = cols * 18;
        int startX = (guiWidth - cols * 18) / 2 + 8; // horizontal offset for centering
        int startY = 17;

        this.slots.clear();

        // Add menu slots
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int slotIndex = row * cols + col;
                int x = startX + col * 18;
                int y = startY + row * 18;
                addSlot(new Slot(inventory, slotIndex, x, y));
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    @Override
    public void setItem(int slotId, int stateId, ItemStack stack) {
        if (slotId >= 0 && slotId < slots.size()) {
            super.setItem(slotId, stateId, stack);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (player instanceof ServerPlayer splayer) {
            if (slotId >= 0 && slotId < inventory.getContainerSize()) {
                menuPage.handleClick(slotId, splayer);
            }
        }
    }
}