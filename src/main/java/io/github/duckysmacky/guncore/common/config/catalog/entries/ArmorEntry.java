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

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ArmorEntry extends CatalogEntry implements EquippableEntry {
    private final Rarity rarity;
    private final String helmetItemId;
    private final String chestplateItemId;
    private final String leggingsItemId;
    private final String bootsItemId;

    public ArmorEntry(
        boolean enabled,
        String name,
        Rarity rarity,
        String helmetItemId,
        String chestplateItemId,
        String leggingsItemId,
        String bootsItemId,
        List<String> descriptionLines
    ) {
        super(enabled, name, descriptionLines);
        this.rarity = Objects.requireNonNull(rarity);
        this.helmetItemId = Objects.requireNonNull(helmetItemId);
        this.chestplateItemId = Objects.requireNonNull(chestplateItemId);
        this.leggingsItemId = Objects.requireNonNull(leggingsItemId);
        this.bootsItemId = Objects.requireNonNull(bootsItemId);
    }

    public static ArmorEntry createExample() {
        return new ArmorEntry(
            true,
            "Iron Armor",
            Rarity.COMMON,
            "minecraft:iron_helmet",
            "minecraft:iron_chestplate",
            "minecraft:iron_leggings",
            "minecraft:iron_boots",
            List.of(
                "&7The most basic armor"
            )
        );
    }

    private ItemStack createItemStack(String itemId) {
        ItemStack item = ItemUtils.findItemStack(itemId);
        if (item.isEmpty()) item = ItemUtils.placeholderItem();

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

        String rarityLine = rarity.color + "" + ChatFormatting.BOLD + rarity.display.toUpperCase();
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(rarityLine))));

        displayTag.put("Lore", loreList);
        item.addTagElement("display", displayTag);

        return item;
    }

    public ItemStack getHelmetItemStack() {
        return createItemStack(helmetItemId);
    }

    public ItemStack getChestplateItemStack() {
        return createItemStack(chestplateItemId);
    }

    public ItemStack getLeggingsItemStack() {
        return createItemStack(leggingsItemId);
    }

    public ItemStack getBootsItemStack() {
        return createItemStack(bootsItemId);
    }

    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public ItemStack getIcon() {
        return getHelmetItemStack();
    }

    @Override
    public ItemStack getItemStack() {
        return getHelmetItemStack();
    }

    @Override
    public List<ItemStack> getAdditionalItemStacks() {
        return Stream.of(helmetItemId, chestplateItemId, leggingsItemId, bootsItemId)
            .map(this::createItemStack)
            .toList();
    }
}
