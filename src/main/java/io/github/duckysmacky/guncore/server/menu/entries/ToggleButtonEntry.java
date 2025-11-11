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

public class ToggleButtonEntry extends MenuEntry {
    private final BaseMenuPage menu;
    private boolean state;
    private final BiConsumer<ServerPlayer, Boolean> onToggle;

    public ToggleButtonEntry(
        BaseMenuPage menu,
        Supplier<Boolean> stateSupplier,
        BiConsumer<ServerPlayer, Boolean> onToggle
    ) {
        super(getIcon(stateSupplier.get()));
        this.menu = menu;
        this.state = stateSupplier.get();
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
    public void onClick(ServerPlayer player) {
        this.state = !this.state;
        this.icon = getIcon(state);

        ServerSoundPlayer.playFor(player, SoundEvents.UI_BUTTON_CLICK.get(), 1f, 1f);
        onToggle.accept(player, state);
        menu.open(player);
    }
}
