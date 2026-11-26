package io.github.duckysmacky.guncore.common.config.catalog.items;

import io.github.duckysmacky.guncore.common.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.common.config.catalog.EquippableEntry;
import io.github.duckysmacky.guncore.common.config.catalog.Rarity;
import io.github.duckysmacky.guncore.common.util.ItemFinder;
import io.github.duckysmacky.guncore.common.util.TextUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Generic item with optional extra items; used by kit gadgets and all build-a-class item categories */
public class ItemEntry extends CatalogEntry implements EquippableEntry {
    private final Rarity rarity;
    private final String itemId;
    private final int itemAmount;
    private final int damage;
    private final String nbtData;
    private final List<AdditionalItem> additionalItems;

    public ItemEntry(
        boolean enabled,
        String name,
        Rarity rarity,
        String itemId,
        int itemAmount,
        int damage,
        String nbtData,
        List<AdditionalItem> additionalItems,
        List<String> descriptionLines
    ) {
        super(enabled, name, descriptionLines);
        this.rarity = Objects.requireNonNull(rarity);
        this.itemId = Objects.requireNonNull(itemId);
        this.itemAmount = itemAmount;
        this.damage = damage;
        this.nbtData = nbtData;
        this.additionalItems = Objects.requireNonNull(additionalItems);
    }

    public static ItemEntry createExample() {
        return new ItemEntry(
            true,
            "Water Bucket",
            Rarity.COMMON,
            "minecraft:water_bucket",
            1,
            0,
            null,
            Collections.emptyList(),
            Arrays.asList(
                "&7A bucket filled with water.",
                "&7Useful for putting out fires or landing safely from heights.",
                "&7This is an example item."
            )
        );
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public ItemStack getIcon() {
        return getItemStack();
    }

    public String getItemId() {
        return itemId;
    }

    /** Ids of the main item and all additional items */
    public List<String> getItemIds() {
        List<String> ids = new ArrayList<>();
        ids.add(itemId);
        getAdditionalItems().forEach(a -> ids.add(a.id));
        return ids;
    }

    public List<AdditionalItem> getAdditionalItems() {
        return additionalItems == null ? Collections.emptyList() : additionalItems;
    }

    public ItemStack getItemStack() {
        ItemStack item = createStack(itemId, itemAmount, damage, nbtData);

        NBTTagCompound displayTag = new NBTTagCompound();

        String coloredName = getRarity().color + "" + TextFormatting.BOLD + name;
        displayTag.setString("Name", coloredName);

        NBTTagList loreList = new NBTTagList();

        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines) {
                String coloredLine = TextUtils.translateColorCodes(line);
                loreList.appendTag(new NBTTagString(coloredLine));
            }
            loreList.appendTag(new NBTTagString(""));
        }

        if (!getAdditionalItems().isEmpty()) {
            String additionalItemsLine = TextFormatting.GREEN + "Additionally includes:";
            loreList.appendTag(new NBTTagString(additionalItemsLine));
            for (ItemStack additionalItem : getAdditionalItemStacks()) {
                String line = TextFormatting.WHITE + "- " + additionalItem.getCount() + "x " + additionalItem.getDisplayName();
                loreList.appendTag(new NBTTagString(line));
            }
            loreList.appendTag(new NBTTagString(""));
        }

        String rarityLine = getRarity().color + "" + TextFormatting.BOLD + getRarity().display.toUpperCase();
        loreList.appendTag(new NBTTagString(rarityLine));

        displayTag.setTag("Lore", loreList);
        item.setTagInfo("display", displayTag);

        return item;
    }

    public List<ItemStack> getAdditionalItemStacks() {
        return getAdditionalItems().stream()
            .map(a -> createStack(a.id, a.amount, a.damage, a.nbtData))
            .collect(Collectors.toList());
    }

    static ItemStack createStack(String id, int amount, int damage, String nbtData) {
        ItemStack item = ItemFinder.findItemStack(id);
        if (item == ItemStack.EMPTY)
            item = new ItemStack(Blocks.DIRT);

        item.setItemDamage(damage);
        item.setCount(Math.max(amount, 1));

        ItemFinder.applyNbt(item, id, nbtData);

        return item;
    }

    public static class AdditionalItem {
        private final String id;
        private final int amount;
        private final int damage;
        private final String nbtData;

        public AdditionalItem(String id, int amount, int damage, String nbtData) {
            this.id = id;
            this.amount = amount;
            this.damage = damage;
            this.nbtData = nbtData;
        }

        public String getId() {
            return id;
        }
    }
}
