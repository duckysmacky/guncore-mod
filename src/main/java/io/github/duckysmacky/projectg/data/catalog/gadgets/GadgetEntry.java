package io.github.duckysmacky.projectg.data.catalog.gadgets;

import io.github.duckysmacky.projectg.data.catalog.CatalogEntry;
import io.github.duckysmacky.projectg.data.catalog.Rarity;
import io.github.duckysmacky.projectg.data.ItemFinder;
import io.github.duckysmacky.projectg.data.ItemStackCustomizer;
import io.github.duckysmacky.projectg.util.ColorTranslator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GadgetEntry extends CatalogEntry {
    private final Rarity rarity;
    private final String itemId;
    private final int itemAmount;
    private final List<String> additionalItemIds;

    public GadgetEntry(
        boolean enabled,
        String name,
        Rarity rarity,
        String itemId,
        int itemAmount,
        List<String> additionalItemIds,
        List<String> descriptionLines
    ) {
        super(enabled, name, descriptionLines);
        this.rarity = rarity;
        this.itemId = itemId;
        this.itemAmount = itemAmount;
        this.additionalItemIds = additionalItemIds;
    }

    public static GadgetEntry getExample() {
        return new GadgetEntry(
            true,
            "Water Bucket",
            Rarity.COMMON,
            "minecraft:water_bucket",
            1,
            Collections.emptyList(),
            Arrays.asList(
                "&7A bucket filled with water.",
                "&7Useful for putting out fires or landing safely from heights."
            )
        );
    }

    public Rarity getRarity() {
        return rarity;
    }

    public ItemStack getItemStack() {
        ItemStack item = ItemFinder.findItemStack(itemId);
        if (item == ItemStack.EMPTY)
            item = new ItemStack(Blocks.DIRT);

        item.setCount(itemAmount);

        NBTTagCompound displayTag = new NBTTagCompound();

        String coloredName = rarity.color + "" + TextFormatting.BOLD + name;
        displayTag.setString("Name", coloredName);

        NBTTagList loreList = new NBTTagList();

        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines) {
                String coloredLine = ColorTranslator.translateColorCodes(line);
                loreList.appendTag(new NBTTagString(coloredLine));
            }
            loreList.appendTag(new NBTTagString(""));
        }

        if (!additionalItemIds.isEmpty()) {
            String additionalItemsLine = TextFormatting.GREEN + "Additionally includes:";
            loreList.appendTag(new NBTTagString(additionalItemsLine));
            for (ItemStack additionalItem : getAdditionalItemStacks()) {
                String line = TextFormatting.WHITE + "- " + additionalItem.getDisplayName();
                loreList.appendTag(new NBTTagString(line));
            }
            loreList.appendTag(new NBTTagString(""));
        }

        String rarityLine = rarity.color + "" + TextFormatting.BOLD + rarity.display.toUpperCase();
        loreList.appendTag(new NBTTagString(rarityLine));

        displayTag.setTag("Lore", loreList);
        item.setTagInfo("display", displayTag);

        return item;
    }

    public List<ItemStack> getAdditionalItemStacks() {
        return additionalItemIds.stream()
            .map(id -> {
                ItemStack item = ItemFinder.findItemStack(id);
                if (item == ItemStack.EMPTY)
                    item = new ItemStack(Blocks.DIRT);
                return item;
            })
            .collect(Collectors.toList());
    }

    public String getItemId() {
        return itemId;
    }

    public List<String> getAdditionalItemIds() {
        return additionalItemIds;
    }
}
