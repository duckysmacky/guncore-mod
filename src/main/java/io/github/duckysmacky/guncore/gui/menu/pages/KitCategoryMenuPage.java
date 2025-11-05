package io.github.duckysmacky.guncore.gui.menu.pages;

import io.github.duckysmacky.guncore.data.config.ConfigManager;
import io.github.duckysmacky.guncore.data.config.catalog.kits.KitClass;
import io.github.duckysmacky.guncore.data.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.game.CommandExecutor;
import io.github.duckysmacky.guncore.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.gui.menu.StaticMenu;
import io.github.duckysmacky.guncore.gui.menu.entry.ActionEntry;
import io.github.duckysmacky.guncore.gui.menu.entry.SubpageEntry;
import io.github.duckysmacky.guncore.data.items.ItemStackCustomizer;
import io.github.duckysmacky.guncore.network.PacketHandler;
import io.github.duckysmacky.guncore.network.packets.EquipKitPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.Random;

public class KitCategoryMenuPage extends StaticMenu {
    private final Random random;
    public KitCategoryMenuPage(BaseMenu parent) {
        super("Kits", parent, 4, 9);
        this.random = new Random();

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.IRON_CHESTPLATE))
                .setName("&f&lAssault Class")
                .addLoreLine("&7Excellent all-rounder kits")
                .addLoreLine("&7Utility for better survivability and easier combat")
                .getItemStack(),
            new KitsMenuPage(this, KitClass.ASSAULT)
        ), 1, 2);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.ENDER_EYE))
                .setName("&f&lSkirmisher Class")
                .addLoreLine("&7Fast-paced kits for aggressive playstyles")
                .addLoreLine("&7Utility to help initiate a fight or move around")
                .getItemStack(),
            new KitsMenuPage(this, KitClass.SKIRMISHER)
        ), 1, 3);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.IRON_SWORD))
                .setName("&f&lAssassin Class")
                .addLoreLine("&7Kits focused on destruction and high damage")
                .addLoreLine("&7Utility helps eliminate opponents or destroy areas")
                .getItemStack(),
            new KitsMenuPage(this, KitClass.ASSASSIN)
        ), 1, 4);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Blocks.PISTON))
                .setName("&f&lSentinel Class")
                .addLoreLine("&7Defensive kits for holding positions")
                .addLoreLine("&7Utility to fortify areas and support teammates")
                .getItemStack(),
            new KitsMenuPage(this, KitClass.SENTINEL)
        ), 1, 5);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Blocks.BEDROCK))
                .setName("&f&lSpecial Kits")
                .addLoreLine("&7Unique kits that don't fit into other categories")
                .getItemStack(),
            new KitsMenuPage(this, KitClass.SPECIAL)
        ), 1, 6);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new net.minecraft.item.ItemStack(Items.DIAMOND))
                .setName("&f&lRandom Kit")
                .addLoreLine("&7Select a random kit")
                .getItemStack(),
            this::selectRandomKit
        ), 2, 4);
    }

    private void selectRandomKit(EntityPlayer player) {
        ConfigManager configManager = ConfigManager.instance();
        List<KitEntry> kits = configManager.getCachedKits();

        int randomIndex = random.nextInt(kits.size());
        KitEntry kit = kits.get(randomIndex);

        PacketHandler.instance().sendToServer(new EquipKitPacket(kit));

        player.sendMessage(new TextComponentString("Random kit selected: " + kit.getName()));
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }
}