package io.github.duckysmacky.guncore.client.gui.menu;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.server.menu.MenuPageContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MenuPageScreen extends AbstractContainerScreen<MenuPageContainer> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");
    private final String title;
    private final int rows, cols;

    public MenuPageScreen(MenuPageContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.title = container.getTitle();
        this.rows = container.getRows();
        this.cols = container.getCols();
        this.imageWidth = 176;
        this.imageHeight = 114 + rows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int startX = (width - imageWidth) / 2;
        int startY = (height - imageHeight) / 2;

        final int TITLE_HEIGHT = 16; // top bar for title
        final int SLOT_HEIGHT = 18; // each row of slots
        final int FOOTER_HEIGHT = 7; // just the decorative border

        // top part of the GUI
        graphics.blit(
            BACKGROUND_TEXTURE,
            startX,
            startY,
            0,
            0,
            imageWidth,
            TITLE_HEIGHT
        );

        // middle part for each row
        for (int row = 0; row < rows; row++) {
            graphics.blit(
                BACKGROUND_TEXTURE,
                startX,
                startY + TITLE_HEIGHT + row * SLOT_HEIGHT,
                0,
                17,
                imageWidth,
                18
            );
        }

        // bottom part
        int footerY = startY + TITLE_HEIGHT + rows * SLOT_HEIGHT;
        graphics.blit(
            BACKGROUND_TEXTURE,
            startX,
            footerY,
            0,
            126,
            imageWidth,
            FOOTER_HEIGHT
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }
}