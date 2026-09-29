package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.github.duckysmacky.guncore.server.game.GameManager;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.StaticMenuPage;
import io.github.duckysmacky.guncore.server.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.server.menu.entries.DisplayEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import io.github.duckysmacky.guncore.server.menu.entries.GameruleToggleEntry;
import io.github.duckysmacky.guncore.server.menu.entries.ToggleButtonEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;

public class SettingsMenuPage extends StaticMenuPage {
    public SettingsMenuPage(BaseMenuPage parent) {
        super("Settings", parent, 6, 9);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.WATER_BUCKET))
                .setName("&f&lWeather Cycle")
                .addLoreLine("&fToggle the weather cycle")
                .getItemStack()
        ), 1, 1);

        addEntry(new GameruleToggleEntry(
            this, GameRules.RULE_WEATHER_CYCLE,
            "Weather cycle"
        ), 2, 1);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.CLOCK))
                .setName("&f&lDaylight Cycle")
                .addLoreLine("&fToggle the daylight cycle")
                .getItemStack()
        ), 1, 2);

        addEntry(new GameruleToggleEntry(
            this, GameRules.RULE_DAYLIGHT,
            "Daylight cycle"
        ), 2, 2);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.FIRE_CHARGE))
                .setName("&f&lFire Spreading")
                .addLoreLine("&fToggle fire spreading")
                .getItemStack()
        ), 1, 3);

        addEntry(new GameruleToggleEntry(
            this, GameRules.RULE_DOFIRETICK,
            "Fire spreading"
        ), 2, 3);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.STONE_BUTTON))
                .setName("&f&lItem Drops")
                .addLoreLine("&fToggle item dropping from destruction")
                .getItemStack()
        ), 1, 4);

        addEntry(new GameruleToggleEntry(
            this, GameRules.RULE_DOBLOCKDROPS,
            "Item drops"
        ), 2, 4);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.GOLDEN_APPLE))
                .setName("&f&lNatural Regeneration")
                .addLoreLine("&fToggle natural regeneration")
                .addLoreLine("&fIf disabled, you can heal only using healing items")
                .getItemStack()
        ), 1, 5);

        addEntry(new GameruleToggleEntry(
            this, GameRules.RULE_NATURAL_REGENERATION,
            "Natural regeneration"
        ), 2, 5);

        addEntry(new DisplayEntry(
            new ItemStackCustomizer(new ItemStack(Items.SKELETON_SKULL))
                .setName("&f&lKill-only Life Loss")
                .addLoreLine("&fIf enabled, only a death caused by another player costs a life")
                .addLoreLine("&fIf disabled, any death (fall damage, self-kill, etc.) costs a life too")
                .addLoreLine("&7Deaths are always counted either way")
                .getItemStack()
        ), 1, 6);

        addEntry(new ToggleButtonEntry(
            this,
            () -> GameManager.instance().isKillOnlyLifeLoss(),
            (player, state) -> GameManager.instance().setKillOnlyLifeLoss(state)
        ), 2, 6);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.REDSTONE_TORCH))
                .setName("&f&lSetup world settings")
                .addLoreLine("&7Automatically setup world settings")
                .addLoreLine("&7Sets up teams, game rules and other")
                .getItemStack(),
            p -> GameManager.setupWorldSettings()
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
                .addLoreLine("&7Remove all of the dropped items on the ground")
                .getItemStack(),
            p -> CommandExecutor.execute("kill @e[type=item]")
        ), 4, 4);
    }
}