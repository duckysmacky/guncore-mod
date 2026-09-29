package io.github.duckysmacky.guncore.server.menu.entries;

import io.github.duckysmacky.guncore.common.game.ServerBroadcaster;
import io.github.duckysmacky.guncore.common.game.ServerSoundPlayer;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.server.ServerLifecycleHooks;

public class GameruleToggleEntry extends ToggleButtonEntry {
    public GameruleToggleEntry(
        BaseMenuPage menu,
        GameRules.Key<GameRules.BooleanValue> gamerule,
        String gameruleName
    ) {
        super(menu,
            () -> ServerLifecycleHooks.getCurrentServer().getGameRules().getBoolean(gamerule),
            (player, state) -> {
            player.server.getGameRules().getRule(gamerule).set(state, player.server);

            ChatFormatting color = state ? ChatFormatting.GREEN : ChatFormatting.GRAY;
            String stateChat = state ? "enabled" : "disabled";
            String message = String.format("&f%s %s%s", gameruleName, color, stateChat);

            ServerBroadcaster.message(message);
            ServerSoundPlayer.playForAll(SoundEvents.NOTE_BLOCK_HARP.get(), 1f, 1f);
        });
    }
}
