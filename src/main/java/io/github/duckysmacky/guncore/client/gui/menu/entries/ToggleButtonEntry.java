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
    // how long a clicked state is shown before falling back to the synced server state
    private static final long PENDING_MS = 2000;
    private final BaseMenu menu;
    private final Supplier<Boolean> stateSupplier;
    private final BiConsumer<EntityPlayer, Boolean> onToggle;
    private Boolean pendingState;
    private long pendingUntil;

    public ToggleButtonEntry(
        BaseMenu menu,
        Supplier<Boolean> stateSupplier,
        BiConsumer<EntityPlayer, Boolean> onToggle
    ) {
        super(null);
        this.menu = menu;
        this.stateSupplier = stateSupplier;
        this.onToggle = onToggle;
    }

    private boolean getState() {
        boolean synced = stateSupplier.get();
        if (pendingState != null && (pendingState == synced || System.currentTimeMillis() > pendingUntil))
            pendingState = null;

        return pendingState != null ? pendingState : synced;
    }

    @Override
    public ItemStack getIcon() {
        return getIcon(getState());
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
        boolean newState = !getState();
        pendingState = newState;
        pendingUntil = System.currentTimeMillis() + PENDING_MS;

        player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
        onToggle.accept(player, newState);
        menu.open(player);
    }

}
