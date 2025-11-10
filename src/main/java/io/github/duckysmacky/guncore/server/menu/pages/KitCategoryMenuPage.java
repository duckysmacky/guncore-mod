package io.github.duckysmacky.guncore.server.menu.pages;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitClass;
import io.github.duckysmacky.guncore.common.config.catalog.kits.KitEntry;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.server.menu.BaseMenuPage;
import io.github.duckysmacky.guncore.server.menu.StaticMenuPage;
import io.github.duckysmacky.guncore.server.menu.entries.ActionEntry;
import io.github.duckysmacky.guncore.server.menu.entries.SubpageEntry;
import io.github.duckysmacky.guncore.common.util.ItemStackCustomizer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Random;

public class KitCategoryMenuPage extends StaticMenuPage {
    private final Random random;
    public KitCategoryMenuPage(BaseMenuPage parent) {
        super("Kits", parent, 4, 9);
        this.random = new Random();

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.IRON_CHESTPLATE))
                .setName("&f&lAssault Class")
                .addLoreLine("&7Excellent all-rounder kits")
                .addLoreLine("&7Utility for better survivability and easier combat")
                .getItemStack(),
            new KitsMenuPage(this, KitClass.ASSAULT)
        ), 1, 2);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.ENDER_EYE))
                .setName("&f&lSkirmisher Class")
                .addLoreLine("&7Fast-paced kits for aggressive playstyles")
                .addLoreLine("&7Utility to help initiate a fight or move around")
                .getItemStack(),
            new KitsMenuPage(this, KitClass.SKIRMISHER)
        ), 1, 3);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Items.IRON_SWORD))
                .setName("&f&lAssassin Class")
                .addLoreLine("&7Kits focused on destruction and high damage")
                .addLoreLine("&7Utility helps eliminate opponents or destroy areas")
                .getItemStack(),
            new KitsMenuPage(this, KitClass.ASSASSIN)
        ), 1, 4);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.PISTON))
                .setName("&f&lSentinel Class")
                .addLoreLine("&7Defensive kits for holding positions")
                .addLoreLine("&7Utility to fortify areas and support teammates")
                .getItemStack(),
            new KitsMenuPage(this, KitClass.SENTINEL)
        ), 1, 5);

        addEntry(new SubpageEntry(
            new ItemStackCustomizer(new ItemStack(Blocks.BEDROCK))
                .setName("&f&lSpecial Kits")
                .addLoreLine("&7Unique kits that don't fit into other categories")
                .getItemStack(),
            new KitsMenuPage(this, KitClass.SPECIAL)
        ), 1, 6);

        addEntry(new ActionEntry(
            new ItemStackCustomizer(new ItemStack(Items.DIAMOND))
                .setName("&f&lRandom Kit")
                .addLoreLine("&7Select a random kit")
                .getItemStack(),
            this::selectRandomKit
        ), 2, 4);
    }

    private void selectRandomKit(ServerPlayer player) {
        List<KitEntry> kits = ConfigManager.instance().getCatalogManager().getCatalog(CatalogType.KITS);

        int randomIndex = random.nextInt(kits.size());
        KitEntry kit = kits.get(randomIndex);

        EquipmentController.equipKit(player, kit);

        player.sendSystemMessage(Component.literal("Random kit selected: " + kit.getName()));
        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }
}