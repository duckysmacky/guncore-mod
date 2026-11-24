package io.github.duckysmacky.guncore.client.gui.menu.entries;

import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ActionEntry extends MenuEntry {
    private final Consumer<EntityPlayer> action;
    private final BooleanSupplier selected;
    private final Supplier<ItemStack> iconSupplier;

    public ActionEntry(ItemStack icon, Consumer<EntityPlayer> action) {
        this(icon, action, null);
    }

    /**
     * An entry which is shown with an enchantment glint while it is the currently selected option
     */
    public ActionEntry(ItemStack icon, Consumer<EntityPlayer> action, BooleanSupplier selected) {
        super(icon);
        this.action = action;
        this.selected = selected;
        this.iconSupplier = null;
    }

    /**
     * An entry whose icon shows values that change while the menu page exists
     */
    public ActionEntry(Supplier<ItemStack> iconSupplier, Consumer<EntityPlayer> action) {
        super(null);
        this.action = action;
        this.selected = null;
        this.iconSupplier = iconSupplier;
    }

    @Override
    public ItemStack getIcon() {
        ItemStack current = iconSupplier != null ? iconSupplier.get() : icon;
        return selected != null && selected.getAsBoolean() ? ItemStackCustomizer.withGlint(current) : current;
    }

    @Override
    public void onClick(EntityPlayer player) {
        player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
        action.accept(player);
    }
}
