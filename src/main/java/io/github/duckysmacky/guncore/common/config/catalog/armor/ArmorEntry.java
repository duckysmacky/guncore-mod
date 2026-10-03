package io.github.duckysmacky.guncore.common.config.catalog.armor;

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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** A full armor set; any piece may be omitted from the JSON */
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
        this.helmetItemId = helmetItemId;
        this.chestplateItemId = chestplateItemId;
        this.leggingsItemId = leggingsItemId;
        this.bootsItemId = bootsItemId;
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
            Arrays.asList("&7A full set of iron armor.", "&7This is an example armor.")
        );
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    /** Pieces in the order of player's armor inventory: boots, leggings, chestplate, helmet (EMPTY when not set) */
    public List<ItemStack> getArmorStacks() {
        return Arrays.asList(
            findStack(bootsItemId),
            findStack(leggingsItemId),
            findStack(chestplateItemId),
            findStack(helmetItemId)
        );
    }

    @Override
    public ItemStack getIcon() {
        ItemStack icon = getArmorStacks().stream()
            .reduce(ItemStack.EMPTY, (best, piece) -> piece.isEmpty() ? best : piece); // helmet wins as it is last
        if (icon.isEmpty())
            icon = new ItemStack(Blocks.DIRT);

        NBTTagCompound displayTag = new NBTTagCompound();
        displayTag.setString("Name", rarity.color + "" + TextFormatting.BOLD + name);

        NBTTagList loreList = new NBTTagList();
        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines)
                loreList.appendTag(new NBTTagString(TextUtils.translateColorCodes(line)));
            loreList.appendTag(new NBTTagString(""));
        }
        loreList.appendTag(new NBTTagString(rarity.color + "" + TextFormatting.BOLD + rarity.display.toUpperCase()));

        displayTag.setTag("Lore", loreList);
        icon.setTagInfo("display", displayTag);
        return icon;
    }

    private static ItemStack findStack(String id) {
        return id == null ? ItemStack.EMPTY : ItemFinder.findItemStack(id);
    }
}
