package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.game.CommandExecutor;
import io.github.duckysmacky.projectg.game.Team;
import io.github.duckysmacky.projectg.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.projectg.data.ItemStackCustomizer;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.StaticMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GameMenuPage extends StaticMenu {
    public GameMenuPage(BaseMenu parent) {
        super("Game", parent, 9, 9);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 8))
                .setName("&fFFA")
                .addLoreLine("&7Set the game mode to FFA")
                .getItemStack(),
            p -> setFFA()
        ), 1, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 14))
                .setName("&cRed team")
                .addLoreLine("&7Join the Red team")
                .getItemStack(),
            (player) -> joinTeam(player, Team.RED)
        ), 1, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 11))
                .setName("&cBlue team")
                .addLoreLine("&7Join the Blue team")
                .getItemStack(),
            (player) -> joinTeam(player, Team.BLUE)
        ), 1, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 4))
                .setName("&cYellow team")
                .addLoreLine("&7Join the Yellow team")
                .getItemStack(),
            (player) -> joinTeam(player, Team.YELLOW)
        ), 1, 5);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.WOOL, 1, 13))
                .setName("&cGreen team")
                .addLoreLine("&7Join the Green team")
                .getItemStack(),
            (player) -> joinTeam(player, Team.GREEN)
        ), 1, 6);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.SLIME_BALL))
                .setName("&fReset Deaths")
                .addLoreLine("&7Reset the death counter")
                .getItemStack(),
            p -> resetDeaths()
        ), 3, 6);
    }

    public void setFFA() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        CommandExecutor commandExecutor = new CommandExecutor();

        String command = "scoreboard teams join ffa @a";
        commandExecutor.execute(command);

        String message = TextFormatting.GREEN + "" + TextFormatting.BOLD + "Game mode switched to FFA";
        server.getPlayerList().getPlayers()
            .forEach(p -> {
                p.sendMessage(new TextComponentString(message));
                p.playSound(SoundEvents.BLOCK_NOTE_HARP, 1f, 1f);
            });
    }

    public void joinTeam(EntityPlayer player, Team team) {
        CommandExecutor commandExecutor = new CommandExecutor();

        String command = String.format("scoreboard teams join %s %s", team.display.toLowerCase(), player.getName());
        commandExecutor.execute(command);

        String message = TextFormatting.WHITE + "Joined the " + team.color + team.display + TextFormatting.WHITE + " team";
        player.sendMessage(new TextComponentString(message));
        player.playSound(SoundEvents.BLOCK_NOTE_HARP, 1f, 1f);
    }

    public void resetDeaths() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        CommandExecutor commandExecutor = new CommandExecutor();

        String command = "scoreboard players set @a Deaths 0";
        commandExecutor.execute(command);

        String message = TextFormatting.GREEN + "" + TextFormatting.BOLD + "Death counter was reset";
        server.getPlayerList().getPlayers()
            .forEach(p -> {
                p.sendMessage(new TextComponentString(message));
                p.playSound(SoundEvents.BLOCK_NOTE_HARP, 1f, 1f);
            });
    }
}