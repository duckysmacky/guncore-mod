package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.common.network.packets.ControlRoundPacket;
import io.github.duckysmacky.guncore.common.network.packets.JoinTeamPacket;
import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.common.game.Team;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GameMenuPage extends StaticMenu {
    public GameMenuPage(BaseMenu parent) {
        super("Game", parent, 6, 9);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 14))
                .setName("&c&lRed team")
                .addLoreLine("&7Join the Red team")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new JoinTeamPacket(Team.RED))
        ), 2, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 11))
                .setName("&9&lBlue team")
                .addLoreLine("&7Join the Blue team")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new JoinTeamPacket(Team.BLUE))
        ), 2, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 4))
                .setName("&e&lYellow team")
                .addLoreLine("&7Join the Yellow team")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new JoinTeamPacket(Team.YELLOW))
        ), 2, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 13))
                .setName("&a&lGreen team")
                .addLoreLine("&7Join the Green team")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new JoinTeamPacket(Team.GREEN))
        ), 2, 5);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 10))
                .setName("&d&lPurple team")
                .addLoreLine("&7Join the Purple team")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new JoinTeamPacket(Team.PURPLE))
        ), 2, 6);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.SLIME_BALL))
                .setName("&a&lStart")
                .addLoreLine("&7Start a new round")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new ControlRoundPacket(ControlRoundPacket.RoundAction.START))
        ), 1, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLD_NUGGET))
                .setName("&e&lPause")
                .addLoreLine("&7Toggle round pause")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new ControlRoundPacket(ControlRoundPacket.RoundAction.PAUSE))
        ), 1, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.BRICK))
                .setName("&8&lEnd")
                .addLoreLine("&7End (finish) the current round")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new ControlRoundPacket(ControlRoundPacket.RoundAction.END))
        ), 1, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.BARRIER))
                .setName("&c&lReset")
                .addLoreLine("&7Reset the round")
                .getItemStack(),
            p -> PacketHandler.instance().sendToServer(new ControlRoundPacket(ControlRoundPacket.RoundAction.RESET))
        ), 1, 5);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.CLAY_BALL))
                .setName("&9&lPrepare everyone")
                .addLoreLine("&7Clear everyone's inventory and put everyone in spectator mode")
                .getItemStack(),
            p -> {
                CommandExecutor.execute("clear @a");
                CommandExecutor.execute("gamemode 3 @a");
            }
        ), 3, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.BEDROCK))
                .setName("&f&lSet Spawnpoints (All)")
                .addLoreLine("&7Set every online player's spawnpoint to their current location")
                .addLoreLine("&7Puts them in survival mode and places bedrock underneath")
                .getItemStack(),
            p -> CommandExecutor.execute("game spawnpoint_all")
        ), 3, 5);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.SPECTRAL_ARROW))
                .setName("&e&lGlowing Event (1 sec)")
                .addLoreLine("&7Give everyone glowing for 1 second")
                .getItemStack(),
            p -> CommandExecutor.execute("effect @a minecraft:glowing 1")
        ), 4, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.SPECTRAL_ARROW))
                .setName("&e&lGlowing Event (5 secs)")
                .addLoreLine("&7Give everyone glowing for 5 seconds")
                .getItemStack(),
            p -> CommandExecutor.execute("effect @a minecraft:glowing 5")
        ), 4, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.ELYTRA))
                .setName("&e&lLevitation Event")
                .addLoreLine("&7Give everyone levitation for 10 seconds")
                .getItemStack(),
            p -> CommandExecutor.execute("effect @a minecraft:levitation 10")
        ), 4, 5);
    }
}