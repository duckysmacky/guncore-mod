package io.github.duckysmacky.guncore.client.gui.menu.pages;

import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.client.gui.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.DisplayEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.client.gui.menu.entries.GameruleToggleEntry;
import io.github.duckysmacky.guncore.client.gui.menu.entries.GameSettingToggleEntry;
import io.github.duckysmacky.guncore.client.game.ClientGameInfo;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class WorldSettingsMenuPage extends StaticMenu {
    public WorldSettingsMenuPage(BaseMenu parent) {
        super("World Settings", parent, 6, 9);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.WATER_BUCKET))
                .setName("&f&lWeather Cycle")
                .addLoreLine("&fToggle the weather cycle")
                .getItemStack()
        ), 1, 1);

        addEntry(new GameruleToggleEntry(this, "doWeatherCycle"), 2, 1);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.CLOCK))
                .setName("&f&lDaylight Cycle")
                .addLoreLine("&fToggle the daylight cycle")
                .getItemStack()
        ), 1, 2);

        addEntry(new GameruleToggleEntry(this, "doDaylightCycle"), 2, 2);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.FIRE_CHARGE))
                .setName("&f&lFire Spreading")
                .addLoreLine("&fToggle fire spreading")
                .getItemStack()
        ), 1, 3);

        addEntry(new GameruleToggleEntry(this, "doFireTick"), 2, 3);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.STONE_BUTTON))
                .setName("&f&lItem Drops")
                .addLoreLine("&fToggle item dropping from destruction")
                .getItemStack()
        ), 1, 4);

        addEntry(new GameruleToggleEntry(this, "doTileDrops"), 2, 4);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLDEN_APPLE))
                .setName("&f&lNatural Regeneration")
                .addLoreLine("&fToggle natural regeneration")
                .addLoreLine("&fIf disabled, you can heal only using healing items")
                .getItemStack()
        ), 1, 5);

        addEntry(new GameruleToggleEntry(this, "naturalRegeneration"), 2, 5);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.HOPPER))
                .setName("&f&lItem Pickup")
                .addLoreLine("&fToggle whether players can pick up items")
                .addLoreLine("&7Useful when other mods still drop items")
                .getItemStack()
        ), 1, 6);

        addEntry(new GameSettingToggleEntry(
            this, () -> ClientGameInfo.instance().isItemPickup(), "item_pickup"
        ), 2, 6);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.LAVA_BUCKET))
                .setName("&f&lAutomatic Item Cleanup")
                .addLoreLine("&fPeriodically remove all dropped items and falling blocks")
                .addLoreLine("&7Interval is set in settings.json (default 30s)")
                .getItemStack()
        ), 1, 7);

        addEntry(new GameSettingToggleEntry(
            this, () -> ClientGameInfo.instance().isItemCleanup(), "item_cleanup"
        ), 2, 7);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.REDSTONE_TORCH))
                .setName("&f&lSetup world settings")
                .addLoreLine("&7Automatically setup world settings")
                .addLoreLine("&7Sets up teams, game rules and other")
                .getItemStack(),
            p -> CommandExecutor.execute("game world setup")
        ), 4, 1);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.COAL_BLOCK))
                .setName("&f&lSet Midnight")
                .addLoreLine("&7Set the time to the darkest one (midnight)")
                .getItemStack(),
            p -> CommandExecutor.execute("time set 18000")
        ), 4, 2);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.GLOWSTONE))
                .setName("&f&lSet Noon")
                .addLoreLine("&7Set the time to the brightest one (noon)")
                .getItemStack(),
            p -> CommandExecutor.execute("time set 6000")
        ), 4, 3);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.IRON_TRAPDOOR))
                .setName("&f&lClear item drops")
                .addLoreLine("&7Remove all of the dropped items and falling blocks")
                .getItemStack(),
            p -> CommandExecutor.execute("game clear_items")
        ), 4, 4);
    }
}