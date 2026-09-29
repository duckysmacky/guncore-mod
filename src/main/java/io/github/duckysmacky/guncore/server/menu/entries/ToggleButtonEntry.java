package io.github.duckysmacky.guncore.server.menu.entries;

import io.github.duckysmacky.guncore.common.game.ServerSoundPlayer;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Menu pages are shared between players, so the state is read from the supplier every time instead of being cached.
 */
public class ToggleButtonEntry extends MenuEntry {
    private final BaseMenuPage menu;
    private final Supplier<Boolean> stateSupplier;
    private final BiConsumer<ServerPlayer, Boolean> onToggle;

    public ToggleButtonEntry(
        BaseMenuPage menu,
        Supplier<Boolean> stateSupplier,
        BiConsumer<ServerPlayer, Boolean> onToggle
    ) {
        super(ItemStack.EMPTY);
        this.menu = menu;
        this.stateSupplier = stateSupplier;
        this.onToggle = onToggle;
    }

    private static ItemStack getIcon(boolean state) {
        ItemStack iconItem = new ItemStack(state ? Items.LIME_DYE : Items.GRAY_DYE);

        return new ItemStackCustomizer(iconItem)
            .setName(state ? "&a&lEnabled" : "&7&lDisabled")
            .addLoreLine("&7Click to toggle")
            .getItemStack();
    }

    @Override
    public ItemStack getIcon() {
        return getIcon(stateSupplier.get());
    }

    @Override
    public void onClick(ServerPlayer player) {
        boolean newState = !stateSupplier.get();

        ServerSoundPlayer.playFor(player, SoundEvents.UI_BUTTON_CLICK.get(), 1f, 1f);
        onToggle.accept(player, newState);
        menu.open(player);
    }
}
