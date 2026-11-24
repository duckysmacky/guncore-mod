package io.github.duckysmacky.guncore.client.gui.menu.entries;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class DisplayEntry extends MenuEntry {
    private final Supplier<ItemStack> iconSupplier;

    public DisplayEntry(ItemStack icon) {
        super(icon);
        this.iconSupplier = null;
    }

    /**
     * For icons showing values that change while the menu page exists
     */
    public DisplayEntry(Supplier<ItemStack> iconSupplier) {
        super(null);
        this.iconSupplier = iconSupplier;
    }

    @Override
    public ItemStack getIcon() {
        return iconSupplier != null ? iconSupplier.get() : super.getIcon();
    }

    @Override
    public void onClick(EntityPlayer player) {
        // do nothing
    }
}
