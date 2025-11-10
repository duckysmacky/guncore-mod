package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.common.game.GameMode;
import io.github.duckysmacky.guncore.common.game.Team;
import io.github.duckysmacky.guncore.server.game.GameManager;
import io.github.duckysmacky.guncore.server.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.StaticMenuPage;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class GameMenuPage extends StaticMenuPage {
    public GameMenuPage(BaseMenuPage parent) {
        super("Game", parent, 9, 9);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLDEN_SWORD))
                .setName("&f&lFree For All")
                .addLoreLine("&7Set the game mode to Free For All")
                .getItemStack(),
            p -> GameManager.instance().setGameMode(GameMode.FFA)
        ), 1, 1);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.SHIELD))
                .setName("&f&lTeam Deathmatch")
                .addLoreLine("&7Set the game mode to Team Deathmatch")
                .getItemStack(),
            p -> GameManager.instance().setGameMode(GameMode.TDM)
        ), 1, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLDEN_CHESTPLATE))
                .setName("&f&lHostage Rescue")
                .addLoreLine("&fWork In Progress")
                .addLoreLine("&7Set the game mode to Hostage Rescue")
                .getItemStack(),
            p -> GameManager.instance().setGameMode(GameMode.HOSTAGE)
        ), 1, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.CLOCK))
                .setName("&f&lTime-based")
                .addLoreLine("&fDefault time limit: 10 minutes")
                .addLoreLine("&7Make the game mode time-based")
                .addLoreLine("&7The player/team with the most kills is considered to be the winner")
                .getItemStack(),
            p -> GameManager.instance().setGameModeVariant(GameMode.Variant.TIME)
        ), 1, 6);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.APPLE))
                .setName("&f&lLife-based")
                .addLoreLine("&fDefault lives: 5")
                .addLoreLine("&7Make the game mode life-based")
                .addLoreLine("&7The only player/team left alive is considered to be the winner")
                .getItemStack(),
            p -> GameManager.instance().setGameModeVariant(GameMode.Variant.LIVES)
        ), 1, 7);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.RED_WOOL))
                .setName("&c&lRed team")
                .addLoreLine("&7Join the Red team")
                .getItemStack(),
            p -> GameManager.instance().joinTeam(p, Team.RED)
        ), 3, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.BLUE_WOOL))
                .setName("&9&lBlue team")
                .addLoreLine("&7Join the Blue team")
                .getItemStack(),
            p -> GameManager.instance().joinTeam(p, Team.BLUE)
        ), 3, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.YELLOW_WOOL))
                .setName("&e&lYellow team")
                .addLoreLine("&7Join the Yellow team")
                .getItemStack(),
            p -> GameManager.instance().joinTeam(p, Team.YELLOW)
        ), 3, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.GREEN_WOOL))
                .setName("&a&lGreen team")
                .addLoreLine("&7Join the Green team")
                .getItemStack(),
            p -> GameManager.instance().joinTeam(p, Team.GREEN)
        ), 3, 5);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.PURPLE_WOOL))
                .setName("&d&lPurple team")
                .addLoreLine("&7Join the Purple team")
                .getItemStack(),
            p -> GameManager.instance().joinTeam(p, Team.PURPLE)
        ), 3, 6);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.SLIME_BALL))
                .setName("&a&lStart")
                .addLoreLine("&7Start a new round")
                .getItemStack(),
            p -> GameManager.instance().startRound()
        ), 5, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLD_NUGGET))
                .setName("&e&lPause")
                .addLoreLine("&7Toggle round pause")
                .getItemStack(),
            p -> GameManager.instance().toggleRoundPause()
        ), 5, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.BRICK))
                .setName("&8&lEnd")
                .addLoreLine("&7End (finish) the current round")
                .getItemStack(),
            p -> GameManager.instance().endRound()
        ), 5, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.BARRIER))
                .setName("&c&lReset")
                .addLoreLine("&7Reset the round")
                .getItemStack(),
            p -> GameManager.instance().resetRound()
        ), 5, 5);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.CLAY_BALL))
                .setName("&9&lPrepare everyone")
                .addLoreLine("&7Clear everyone's inventory and put everyone in spectator mode")
                .getItemStack(),
            p -> {
                CommandExecutor.execute("clear @a");
                CommandExecutor.execute("gamemode 3 @a");
            }
        ), 5, 6);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.SPECTRAL_ARROW))
                .setName("&e&lGlowing Event (1 sec)")
                .addLoreLine("&7Give everyone glowing for 1 second")
                .getItemStack(),
            p -> CommandExecutor.execute("effect @a minecraft:glowing 1")
        ), 7, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.SPECTRAL_ARROW))
                .setName("&e&lGlowing Event (5 secs)")
                .addLoreLine("&7Give everyone glowing for 5 seconds")
                .getItemStack(),
            p -> CommandExecutor.execute("effect @a minecraft:glowing 5")
        ), 7, 4);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.ELYTRA))
                .setName("&e&lLevitation Event")
                .addLoreLine("&7Give everyone levitation for 10 seconds")
                .getItemStack(),
            p -> CommandExecutor.execute("effect @a minecraft:levitation 10")
        ), 7, 5);
    }
}