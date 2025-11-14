package io.github.duckysmacky.guncore.common.config.catalog.entries;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.util.ItemUtils;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemEntry extends CatalogEntry implements EquippableEntry {
    private final Rarity rarity;
    private final String itemId;
    private final int itemAmount;
    private final String nbtData;
    private final List<String> additionalItemIds;

    public ItemEntry(
        boolean enabled,
        String name,
        Rarity rarity,
        String itemId,
        int itemAmount,
        String nbtData,
        List<String> additionalItemIds,
        List<String> descriptionLines
    ) {
        super(enabled, name, descriptionLines);
        this.rarity = Objects.requireNonNull(rarity);
        this.itemId = Objects.requireNonNull(itemId);
        this.itemAmount = itemAmount;
        this.nbtData = nbtData;
        this.additionalItemIds = Objects.requireNonNull(additionalItemIds);
    }

    public static ItemEntry createExample() {
        return new ItemEntry(
            true,
            "Water Bucket",
            Rarity.COMMON,
            "minecraft:water_bucket",
            1,
            null,
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

        applyNBT(item, nbtData);

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
                String coloredLine = ChatFormatting.WHITE + "- " + additionalItem.getHoverName().getString();
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

    public static ItemStack applyNBT(ItemStack itemStack, String nbtString) {
        try {
            if (nbtString == null || nbtString.trim().isEmpty()) {
                return itemStack;
            }

            CompoundTag nbt = parseNBT(nbtString);
            itemStack.setTag(nbt);

            return itemStack;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid NBT string format: " + nbtString, e);
        }
    }

    private static CompoundTag parseNBT(String jsonString) {
        try {
            jsonString = jsonString.trim();
            if (jsonString.endsWith(";")) {
                jsonString = jsonString.substring(0, jsonString.length() - 1);
            }

            return TagParser.parseTag(jsonString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse NBT string: " + jsonString, e);
        }
    }
}
