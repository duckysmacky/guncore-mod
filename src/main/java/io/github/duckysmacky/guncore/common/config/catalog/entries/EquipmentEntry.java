package io.github.duckysmacky.guncore.common.config.catalog.entries;

import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.util.ItemUtils;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EquipmentEntry extends CatalogEntry implements ItemEntry {
    private final Rarity rarity;
    private final String itemId;
    private final int itemAmount;
    private final List<String> additionalItemIds;

    public EquipmentEntry(
        boolean enabled,
        String name,
        Rarity rarity,
        String itemId,
        int itemAmount,
        List<String> additionalItemIds,
        List<String> descriptionLines
    ) {
        super(enabled, name, descriptionLines);
        this.rarity = Objects.requireNonNull(rarity);
        this.itemId = Objects.requireNonNull(itemId);
        this.itemAmount = itemAmount;
        this.additionalItemIds = Objects.requireNonNull(additionalItemIds);
    }

    public static EquipmentEntry createExample() {
        return new EquipmentEntry(
            true,
            "Water Bucket",
            Rarity.COMMON,
            "minecraft:water_bucket",
            1,
            Collections.emptyList(),
            List.of(
                "&7A bucket filled with water.",
                "&7Useful for putting out fires or landing safely from heights.",
                "&7This is an example gadget."
            )
        );
    }

    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public ItemStack getIcon() {
        return getItemStack();
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = ItemUtils.findItemStack(itemId);
        if (item.isEmpty()) item = ItemUtils.placeholderItem();

        item.setCount(itemAmount);

        CompoundTag displayTag = item.getOrCreateTagElement("display");

        String coloredName = rarity.color + "" + ChatFormatting.BOLD + name;
        displayTag.putString("Name", Component.Serializer.toJson(Component.literal(coloredName)));

        ListTag loreList = new ListTag();

        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines) {
                String coloredLine = TextUtils.translateColorCodes(line);
                loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(coloredLine))));
            }
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(""))));
        }

        if (!additionalItemIds.isEmpty()) {
            String additionalItemsLine = ChatFormatting.GREEN + "Additionally includes:";
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(additionalItemsLine))));
            for (ItemStack additionalItem : getAdditionalItemStacks()) {
                String coloredLine = ChatFormatting.WHITE + "- " + additionalItem.getDisplayName();
                loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(coloredLine))));
            }
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(""))));
        }

        String rarityLine = rarity.color + "" + ChatFormatting.BOLD + rarity.display.toUpperCase();
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(rarityLine))));

        displayTag.put("Lore", loreList);
        item.addTagElement("display", displayTag);

        return item;
    }

    @Override
    public List<ItemStack> getAdditionalItemStacks() {
        return additionalItemIds.stream()
            .map(id -> {
                ItemStack item = ItemUtils.findItemStack(id);
                return item.isEmpty() ? new ItemStack(Blocks.DIRT) : item;
            })
            .collect(Collectors.toList());
    }
}
