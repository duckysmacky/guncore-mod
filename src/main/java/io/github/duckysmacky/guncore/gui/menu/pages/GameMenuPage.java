package io.github.duckysmacky.guncore.gui.menu.pages;

import io.github.duckysmacky.guncore.game.GameMode;
import io.github.duckysmacky.guncore.game.Team;
import io.github.duckysmacky.guncore.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.guncore.data.items.ItemStackCustomizer;
import io.github.duckysmacky.guncore.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.network.PacketHandler;
import io.github.duckysmacky.guncore.network.packets.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GameMenuPage extends StaticMenu {
    public GameMenuPage(BaseMenu parent) {
        super("Game", parent, 7, 9);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLDEN_SWORD))
                .setName("&f&lFree For All")
                .addLoreLine("&7Set the game mode to Free For All")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new SetGameModePacket(GameMode.FFA))
        ), 1, 1);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.SHIELD))
                .setName("&f&lTeam Deathmatch")
                .addLoreLine("&7Set the game mode to Team Deathmatch")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new SetGameModePacket(GameMode.TDM))
        ), 1, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLDEN_CHESTPLATE))
                .setName("&f&lHostage Rescue")
                .addLoreLine("&fWork In Progress")
                .addLoreLine("&7Set the game mode to Hostage Rescue")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new SetGameModePacket(GameMode.HOSTAGE))
        ), 1, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.CLOCK))
                .setName("&f&lTime-based")
                .addLoreLine("&fDefault time limit: 10 minutes")
                .addLoreLine("&7Make the game mode time-based")
                .addLoreLine("&7The player/team with the most kills is considered to be the winner")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new SetGameModeVariantPacket(GameMode.Variant.TIME))
        ), 1, 6);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.APPLE))
                .setName("&f&lLife-based")
                .addLoreLine("&fDefault lives: 5")
                .addLoreLine("&7Make the game mode life-based")
                .addLoreLine("&7The only player/team left alive is considered to be the winner")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new SetGameModeVariantPacket(GameMode.Variant.LIVES))
        ), 1, 7);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 14))
                .setName("&c&lRed team")
                .addLoreLine("&7Join the Red team")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new JoinTeamPacket(Team.RED))
        ), 3, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 11))
                .setName("&9&lBlue team")
                .addLoreLine("&7Join the Blue team")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new JoinTeamPacket(Team.BLUE))
        ), 3, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 4))
                .setName("&e&lYellow team")
                .addLoreLine("&7Join the Yellow team")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new JoinTeamPacket(Team.YELLOW))
        ), 3, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 13))
                .setName("&a&lGreen team")
                .addLoreLine("&7Join the Green team")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new JoinTeamPacket(Team.GREEN))
        ), 3, 5);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 10))
                .setName("&d&lPurple team")
                .addLoreLine("&7Join the Purple team")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new JoinTeamPacket(Team.PURPLE))
        ), 3, 6);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.SLIME_BALL))
                .setName("&a&lStart")
                .addLoreLine("&7Start a new round")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new ControlRoundPacket(ControlRoundPacket.RoundAction.START))
        ), 5, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLD_NUGGET))
                .setName("&e&lPause")
                .addLoreLine("&7Toggle round pause")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new ControlRoundPacket(ControlRoundPacket.RoundAction.PAUSE))
        ), 5, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.BRICK))
                .setName("&8&lEnd")
                .addLoreLine("&7End (finish) the current round")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new ControlRoundPacket(ControlRoundPacket.RoundAction.END))
        ), 5, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.BARRIER))
                .setName("&c&lReset")
                .addLoreLine("&7Reset the round")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new ControlRoundPacket(ControlRoundPacket.RoundAction.RESET))
        ), 5, 5);
    }
}