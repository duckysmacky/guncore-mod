package io.github.duckysmacky.guncore.client.gui.menu.entries;

import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ActionEntry extends MenuEntry {
    private final Consumer<EntityPlayer> action;
    private final BooleanSupplier selected;

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
    }

    @Override
    public ItemStack getIcon() {
        return selected != null && selected.getAsBoolean() ? ItemStackCustomizer.withGlint(icon) : icon;
    }

    @Override
    public void onClick(EntityPlayer player) {
        player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
        action.accept(player);
    }
}
