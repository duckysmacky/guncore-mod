package io.github.duckysmacky.projectg.data.catalog.kits;

import com.google.gson.annotations.SerializedName;
import io.github.duckysmacky.projectg.data.catalog.CatalogEntry;
import io.github.duckysmacky.projectg.data.ItemFinder;
import io.github.duckysmacky.projectg.data.ItemStackCustomizer;
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
        this.class_ = class_;
        this.tier = tier;
        this.kitId = kitId;
        this.iconItemId = iconItemId;
        this.variantIds = variantIds;
    }

    public static KitEntry getExample() {
        return new KitEntry(
          true,
          "Soldier",
            KitClass.ASSAULT,
            KitTier.BASIC,
            "assault",
            "minecraft:iron_helmet",
            Collections.emptyList(),
            Arrays.asList(
                "&7A well-rounded kit for frontline combat.",
                "&7Everything you need to fight, survive and win in the same place"
            )
        );
    }

    public String getCommand(EntityPlayer player) {
        return String.format("csg_kit give %s %s", kitId, player.getName());
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

        String coloredName = TextFormatting.WHITE + name;
        displayTag.setString("Name", coloredName);

        NBTTagList loreList = new NBTTagList();

        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines) {
                String coloredLine = ItemStackCustomizer.translateColorCodes(line);
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
