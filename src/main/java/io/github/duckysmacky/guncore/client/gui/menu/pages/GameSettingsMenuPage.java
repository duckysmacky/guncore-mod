package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.DisplayEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.GameSettingToggleEntry;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.common.network.packets.SetGameModePacket;
import io.github.duckysmacky.guncore.common.network.packets.SetGameModeVariantPacket;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GameSettingsMenuPage extends StaticMenu {
    public GameSettingsMenuPage(BaseMenu parent) {
        super("Game Settings", parent, 6, 9);

        // game modes
        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLDEN_SWORD))
                .setName("&f&lFree For All")
                .addLoreLine("&7Set the game mode to Free For All")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new SetGameModePacket(GameMode.FFA)),
            () -> ClientGameInfo.instance().getGameMode() == GameMode.FFA
        ), 1, 1);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.BANNER, 1, 1)) // red banner
                .setName("&f&lTeam Deathmatch")
                .addLoreLine("&7Set the game mode to Team Deathmatch")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new SetGameModePacket(GameMode.TDM)),
            () -> ClientGameInfo.instance().getGameMode() == GameMode.TDM
        ), 1, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLDEN_CHESTPLATE))
                .setName("&f&lHostage Rescue")
                .addLoreLine("&fWork In Progress")
                .addLoreLine("&7Set the game mode to Hostage Rescue")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new SetGameModePacket(GameMode.HOSTAGE)),
            () -> ClientGameInfo.instance().getGameMode() == GameMode.HOSTAGE
        ), 1, 3);

        // win conditions
        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.CLOCK))
                .setName("&f&lTime-based")
                .addLoreLine("&7Make the game mode time-based")
                .addLoreLine("&7The player/team with the most kills is considered to be the winner")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new SetGameModeVariantPacket(GameMode.Variant.TIME)),
            () -> ClientGameInfo.instance().getGameModeVariant() == GameMode.Variant.TIME
        ), 1, 5);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.APPLE))
                .setName("&f&lLife-based")
                .addLoreLine("&7Make the game mode life-based")
                .addLoreLine("&7The only player/team left alive is considered to be the winner")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new SetGameModeVariantPacket(GameMode.Variant.LIVES)),
            () -> ClientGameInfo.instance().getGameModeVariant() == GameMode.Variant.LIVES
        ), 1, 6);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.IRON_SWORD))
                .setName("&f&lKill-based")
                .addLoreLine("&7Make the game mode kill-based")
                .addLoreLine("&7The player/team who reaches the target kill amount is considered the winner")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new SetGameModeVariantPacket(GameMode.Variant.KILLS)),
            () -> ClientGameInfo.instance().getGameModeVariant() == GameMode.Variant.KILLS
        ), 1, 7);

        // values for the current game mode (display only)
        addEntry(new DisplayEntry(() -> valueIcon(
            new ItemStack(Items.CLOCK), "&f&lRound Time",
            TextUtils.formatTime(ClientGameInfo.instance().getRoundLengthSec()),
            "&7Used by the time-based win condition"
        )), 3, 5);

        addEntry(new DisplayEntry(() -> valueIcon(
            new ItemStack(Items.APPLE), "&f&lLives",
            String.valueOf(ClientGameInfo.instance().getStartingLives()),
            "&7Used by the life-based win condition"
        )), 3, 6);

        addEntry(new DisplayEntry(() -> valueIcon(
            new ItemStack(Items.IRON_SWORD), "&f&lKill Target",
            String.valueOf(ClientGameInfo.instance().getKillTarget()),
            "&7Used by the kill-based win condition"
        )), 3, 7);

        // life loss rule
        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.SKULL))
                .setName("&f&lKill-only Life Loss")
                .addLoreLine("&fIf enabled, only a death caused by another player costs a life")
                .addLoreLine("&fIf disabled, any death (fall damage, self-kill, etc.) costs a life too")
                .addLoreLine("&7Deaths are always counted either way")
                .getItemStack()
        ), 3, 1);

        addEntry(new GameSettingToggleEntry(
            this, () -> ClientGameInfo.instance().isKillOnlyLifeLoss(), "kill_only_lives"
        ), 4, 1);
    }

    private static ItemStack valueIcon(ItemStack item, String name, String value, String usage) {
        return new ItemStackCustomizer(item)
            .setName(name)
            .addLoreLine("&fValue: &a" + value + " &7(" + ClientGameInfo.instance().getGameMode().display + ")")
            .addLoreLine(usage)
            .addLoreLine("&7Display only, the actual values are set manually")
            .addLoreLine("&7in config/guncore/settings.json")
            .getItemStack();
    }
}
