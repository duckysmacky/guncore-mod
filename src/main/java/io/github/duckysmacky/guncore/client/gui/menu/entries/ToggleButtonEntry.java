package io.github.duckysmacky.guncore.client.gui.menu.entries;

import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ToggleButtonEntry extends MenuEntry {
    private final BaseMenu menu;
    private boolean state;
    private final BiConsumer<EntityPlayer, Boolean> onToggle;

    public ToggleButtonEntry(
        BaseMenu menu,
        Supplier<Boolean> stateSupplier,
        BiConsumer<EntityPlayer, Boolean> onToggle
    ) {
        super(getIcon(stateSupplier.get()));
        this.menu = menu;
        this.state = stateSupplier.get();
        this.onToggle = onToggle;
    }

    private static ItemStack getIcon(boolean state) {
        ItemStack iconItem = state
            ? new ItemStack(Items.DYE, 1, 10) // lime green dye
            : new ItemStack(Items.DYE, 1, 8); // light gray dye

        return new ItemStackCustomizer(iconItem)
            .setName(state ? "&a&lEnabled" : "&7&lDisabled")
            .addLoreLine("&7Click to toggle")
            .getItemStack();
    }

    @Override
    public void onClick(EntityPlayer player) {
        this.state = !this.state; // toggle state
        this.icon = getIcon(state); // update icon

        player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
        onToggle.accept(player, state);
        menu.open(player);
    }

}
