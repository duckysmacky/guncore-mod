package io.github.duckysmacky.projectg.data.catalog.guns;

import io.github.duckysmacky.projectg.data.catalog.CatalogEntry;
import io.github.duckysmacky.projectg.data.catalog.Rarity;
import io.github.duckysmacky.projectg.data.ItemFinder;
import io.github.duckysmacky.projectg.util.TextUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;

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
        this.category = category;
        this.rarity = rarity;
        this.gunItemId = gunItemId;
        this.ammoItemId = ammoItemId;
        this.ammoItemAmount = ammoItemAmount;
    }

    public static GunEntry getExample() {
        return new GunEntry(
            true,
            "SOCOM M4A1",
            GunCategory.ASSAULT_RIFLE,
            Rarity.COMMON,
            "mw:socom_m4a1",
            "mw:socom_mag",
            12,
            Arrays.asList(
                "&7A versatile and reliable assault rifle favored by special operations forces.",
                "&7Known for its accuracy and adaptability in various combat scenarios."
            )
        );
    }

    public ItemStack getGunItemStack() {
        ItemStack item = ItemFinder.findItemStack(gunItemId);
        if (item == ItemStack.EMPTY)
            item = new ItemStack(Blocks.DIRT);

        NBTTagCompound displayTag = new NBTTagCompound();

        String coloredName = rarity.color + "" + TextFormatting.BOLD + name;
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
        return category == GunCategory.SIDEARM || category == GunCategory.MELEE;
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
