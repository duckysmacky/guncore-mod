package io.github.duckysmacky.guncore.common.config.catalog.kits;

import com.google.gson.annotations.SerializedName;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogEntry;
import io.github.duckysmacky.guncore.common.util.ItemFinder;
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
            List.of(
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
        if (item.isEmpty())
            item = new ItemStack(Blocks.DIRT);

        CompoundTag displayTag = item.getOrCreateTagElement("display");

        String coloredName = ChatFormatting.WHITE + "" + ChatFormatting.BOLD + name;
        displayTag.putString("Name", Component.Serializer.toJson(Component.literal(coloredName)));

        ListTag loreList = new ListTag();

        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines) {
                String coloredLine = TextUtils.translateColorCodes(line);
                loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(coloredLine))));
            }
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(""))));
        }

        if (!variantIds.isEmpty()) {
            String additionalItemsLine = ChatFormatting.GREEN + "Variants:";
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(additionalItemsLine))));
            for (String variantId : variantIds) {
                String coloredLine = ChatFormatting.WHITE + "- " + variantId;
                loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(coloredLine))));
            }
            String tipLine = ChatFormatting.GRAY + "Variant selection available via '/csg_kit give <variant_id>' command.";
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(tipLine))));
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(""))));
        }

        String rarityLine = tier.color + "" + ChatFormatting.BOLD + tier.display.toUpperCase();
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(rarityLine))));

        displayTag.put("Lore", loreList);
        item.addTagElement("display", displayTag);

        return item;
    }
}
