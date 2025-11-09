package io.github.duckysmacky.guncore.common.config.catalog.guns;

import io.github.duckysmacky.guncore.common.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.util.ItemFinder;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Objects;

public class GunEntry extends CatalogEntry {
    private final GunCategory category;
    private final Rarity rarity;
    private final String gunItemId;
    private final String ammoItemId;
    private final int ammoItemAmount;

    public GunEntry(
        boolean enabled,
        String name,
        GunCategory category,
        Rarity rarity,
        String gunItemId,
        String ammoItemId,
        int ammoItemAmount,
        List<String> descriptionLines
    ) {
        super(enabled, name, descriptionLines);
        this.category = Objects.requireNonNull(category);
        this.rarity = Objects.requireNonNull(rarity);
        this.gunItemId = Objects.requireNonNull(gunItemId);
        this.ammoItemId = Objects.requireNonNull(ammoItemId);
        this.ammoItemAmount = ammoItemAmount;
    }

    public static GunEntry createExample() {
        return new GunEntry(
            true,
            "Bow",
            GunCategory.ASSAULT_RIFLE,
            Rarity.COMMON,
            "minecraft:bow",
            "minecraft:arrow",
            64,
            List.of(
                "&7The classic ranged weapon.",
                "&7Reliable and effective for all situations.",
                "&7This is an example gun."
            )
        );
    }

    public ItemStack getGunItemStack() {
        ItemStack item = ItemFinder.findItemStack(gunItemId);
        if (item.isEmpty())
            item = new ItemStack(Blocks.DIRT);

        CompoundTag displayTag = item.getOrCreateTagElement("display");

        String coloredName = ChatFormatting.WHITE + "" + ChatFormatting.BOLD + name;
        displayTag.putString("Name", Component.Serializer.toJson(Component.literal(coloredName)));

        ListTag loreList = new ListTag();

        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines) {
                String coloredLine = TextUtils.translateColorCodes(line);
                loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(coloredLine))));
            }
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(""))));
        }

        ItemStack ammo = getAmmoItemStack();
        String ammoLine = TextUtils.translateColorCodes(String.format("&aIncluded ammo: &f%dx %s", ammoItemAmount, ammo.getDisplayName()));
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(ammoLine))));
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(""))));

        String rarityLine = rarity.color + "" + ChatFormatting.BOLD + rarity.display.toUpperCase();
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(rarityLine))));

        displayTag.put("Lore", loreList);
        item.addTagElement("display", displayTag);

        return item;
    }

    public ItemStack getAmmoItemStack() {
        ItemStack item = ItemFinder.findItemStack(ammoItemId);
        if (item.isEmpty())
            item = new ItemStack(Blocks.DIRT);

        item.setCount(ammoItemAmount);
        return item;
    }

    public boolean isSecondary() {
        return category == GunCategory.SIDEARM;
    }

    public String getGunItemId() {
        return gunItemId;
    }

    public String getAmmoItemId() {
        return ammoItemId;
    }

    public GunCategory getCategory() {
        return category;
    }

    public Rarity getRarity() {
        return rarity;
    }
}
