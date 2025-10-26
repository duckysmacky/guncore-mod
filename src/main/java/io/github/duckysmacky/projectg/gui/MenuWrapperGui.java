package io.github.duckysmacky.projectg.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class MenuWrapperGui extends GuiContainer {
    private static final ResourceLocation CONTAINER_BACKGROUND =
        new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");
    private final InventoryBasic inventory;
    private final MenuPage page;
    private final int rows;
    private final int cols;

    public MenuWrapperGui(InventoryBasic inventory, MenuPage page, EntityPlayer player) {
        super(new MenuWrapperContainer(inventory, page));
        this.inventory = inventory;
        this.page = page;
        this.rows = page.getRows();
        this.cols = page.getCols();
        this.ySize = 114 + rows * 18;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(CONTAINER_BACKGROUND);

        // Compute a top-left corner for centering
        int startX = (width - xSize) / 2;
        int startY = (height - ySize) / 2;

        // Heights for different parts of the vanilla texture
        final int TITLE_HEIGHT = 16;    // top bar for title
        final int SLOT_HEIGHT = 18;     // each row of slots
        final int BOTTOM_HEIGHT = 10;   // bottom bar

        // Width is always full xSize
        final int WIDTH = xSize;

        // Draw top (title) part
        drawTexturedModalRect(startX, startY, 0, 0, WIDTH, TITLE_HEIGHT);

        // Draw middle (rows of slots)
        for (int row = 0; row < rows; row++) {
            drawTexturedModalRect(startX, startY + TITLE_HEIGHT + row * SLOT_HEIGHT, 0, 17, WIDTH, SLOT_HEIGHT);
        }

        // Draw bottom
        drawTexturedModalRect(startX, startY + TITLE_HEIGHT + rows * SLOT_HEIGHT, 0, 126, WIDTH, BOTTOM_HEIGHT);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(page.getTitle(), 8, 6, 0xFFFFFF);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        // Suppress vanilla slot interaction logic completely
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        Slot slot = getSlotUnderMouse();
        if (slot != null && slot.inventory == this.inventory) {
            int index = slot.getSlotIndex();

            if (page != null) {
                page.handleClick(index, mc.player);
            }
        }
    }
}