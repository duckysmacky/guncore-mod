package io.github.duckysmacky.projectg.gui.menu.pages;

import io.github.duckysmacky.projectg.game.CommandExecutor;
import io.github.duckysmacky.projectg.game.GameManager;
import io.github.duckysmacky.projectg.gui.menu.BaseMenu;
import io.github.duckysmacky.projectg.gui.menu.StaticMenu;
import io.github.duckysmacky.projectg.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.projectg.gui.menu.entry.DisplayEntry;
import io.github.duckysmacky.projectg.data.ItemStackCustomizer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Arrays;

public class SettingsMenuPage extends StaticMenu {
    public SettingsMenuPage(BaseMenu parent) {
        super("Settings", parent, 6, 9);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.WATER_BUCKET))
                .setName("&f&lWeather Cycle")
                .getItemStack()
        ), 1, 1);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.DYE))
                .setName("&f&lToggle Weather Cycle")
                .addLoreLine("&fClick to toggle the weather cycle")
                .getItemStack(),
            (player) -> player.sendMessage(new TextComponentString("Weather cycle toggled!"))
        ), 2, 1);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.REDSTONE_TORCH))
                .setName("&fSetup teams")
                .addLoreLine("&7Automatically sets up scoreboard teams and the death counter")
                .getItemStack(),
            p -> GameManager.instance().setupScoreboardTeams()
        ), 4, 1);
    }
}