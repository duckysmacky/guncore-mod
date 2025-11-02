package io.github.duckysmacky.guncore.data.config.catalog.guns;

import io.github.duckysmacky.guncore.data.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.data.config.catalog.Rarity;
import io.github.duckysmacky.guncore.data.items.ItemFinder;
import io.github.duckysmacky.guncore.util.TextUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
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
            Arrays.asList(
                "&7The classic ranged weapon.",
                "&7Reliable and effective for all situations.",
                "&7This is an example gun."
            )
        );
    }

    public ItemStack getGunItemStack() {
        ItemStack item = ItemFinder.findItemStack(gunItemId);
        if (item == ItemStack.EMPTY)
            item = new ItemStack(Blocks.DIRT);

        NBTTagCompound displayTag = new NBTTagCompound();

        String coloredName = TextFormatting.WHITE + "" + TextFormatting.BOLD + name;
        displayTag.setString("Name", coloredName);

        NBTTagList loreList = new NBTTagList();

        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines) {
                String coloredLine = TextUtils.translateColorCodes(line);
                loreList.appendTag(new NBTTagString(coloredLine));
            }
            loreList.appendTag(new NBTTagString(""));
        }

        String ammoLine = TextFormatting.GREEN + "Included ammo: " + TextFormatting.WHITE + ammoItemAmount + "x " + getAmmoItemStack().getDisplayName();
        loreList.appendTag(new NBTTagString(ammoLine));
        loreList.appendTag(new NBTTagString(""));

        String rarityLine = rarity.color + "" + TextFormatting.BOLD + rarity.display.toUpperCase();
        loreList.appendTag(new NBTTagString(rarityLine));

        displayTag.setTag("Lore", loreList);
        item.setTagInfo("display", displayTag);

        return item;
    }

    public ItemStack getAmmoItemStack() {
        ItemStack item = ItemFinder.findItemStack(ammoItemId);
        if (item == ItemStack.EMPTY)
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
