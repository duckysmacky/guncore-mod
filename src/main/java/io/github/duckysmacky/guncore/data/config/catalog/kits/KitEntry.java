package io.github.duckysmacky.guncore.data.config.catalog.kits;

import com.google.gson.annotations.SerializedName;
import io.github.duckysmacky.guncore.data.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.data.items.ItemFinder;
import io.github.duckysmacky.guncore.util.TextUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class KitEntry extends CatalogEntry {
    @SerializedName("class")
    private final KitClass class_;
    private final KitTier tier;
    private final String kitId;
    private final String iconItemId;
    private final List<String> variantIds;

    public KitEntry(
        boolean enabled,
        String name,
        KitClass class_,
        KitTier tier,
        String kitId,
        String iconItemId,
        List<String> variantIds,
        List<String> descriptionLines
    ) {
        super(enabled, name + " Kit", descriptionLines);
        this.class_ = Objects.requireNonNull(class_);
        this.tier = Objects.requireNonNull(tier);
        this.kitId = Objects.requireNonNull(kitId);
        this.iconItemId = Objects.requireNonNull(iconItemId);
        this.variantIds = Objects.requireNonNull(variantIds);
    }

    public static KitEntry createExample() {
        return new KitEntry(
          true,
          "Hunter",
            KitClass.ASSAULT,
            KitTier.BASIC,
            "hunter",
            "minecraft:iron_sword",
            Collections.emptyList(),
            Arrays.asList(
                "&7The Hunter Kit is perfect for players who want a balanced loadout for various combat situations.",
                "&7This is an example kit"
            )
        );
    }

    public KitClass getKitClass() {
        return class_;
    }

    public KitTier getTier() {
        return tier;
    }

    public String getKitId() {
        return kitId;
    }

    public ItemStack getIconItem() {
        ItemStack item = ItemFinder.findItemStack(iconItemId);
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

        if (!variantIds.isEmpty()) {
            loreList.appendTag(new NBTTagString(TextFormatting.GREEN + "Variants:"));
            for (String variantId : variantIds) {
                String line = TextFormatting.WHITE + "- " + variantId;
                loreList.appendTag(new NBTTagString(line));
            }
            loreList.appendTag(new NBTTagString(TextFormatting.GRAY + "Variant selection available via '/csg_kit give <variant_id>' command."));
            loreList.appendTag(new NBTTagString(""));
        }

        String tierLine = tier.color + "" + TextFormatting.BOLD + tier.display.toUpperCase() + " TIER";
        loreList.appendTag(new NBTTagString(tierLine));

        displayTag.setTag("Lore", loreList);
        item.setTagInfo("display", displayTag);

        return item;
    }
}
