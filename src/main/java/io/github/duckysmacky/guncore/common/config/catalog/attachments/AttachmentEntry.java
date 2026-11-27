package io.github.duckysmacky.guncore.common.config.catalog.attachments;

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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Weapon attachment; several can be selected at once */
public class AttachmentEntry extends CatalogEntry implements EquippableEntry {
    public static final String DEFAULT_TYPE = "other";

    private final String type;
    private final Rarity rarity;
    private final String itemId;
    private final List<String> compatibleWith;

    public AttachmentEntry(
        boolean enabled,
        String name,
        String type,
        Rarity rarity,
        String itemId,
        List<String> compatibleWith,
        List<String> descriptionLines
    ) {
        super(enabled, name, descriptionLines);
        this.type = type;
        this.rarity = Objects.requireNonNull(rarity);
        this.itemId = Objects.requireNonNull(itemId);
        this.compatibleWith = compatibleWith;
    }

    public static AttachmentEntry createExample() {
        return new AttachmentEntry(
            true,
            "Spyglass",
            "sights",
            Rarity.UNCOMMON,
            "minecraft:ender_eye",
            Collections.singletonList("Bow"),
            Arrays.asList(
                "&7Lets you see further.",
                "&7This is an example attachment."
            )
        );
    }

    /** Id of an {@code ATTACHMENT_TYPES} menu section */
    public String getType() {
        return type != null ? type : DEFAULT_TYPE;
    }

    public String getItemId() {
        return itemId;
    }

    public List<String> getCompatibleWith() {
        // Gson skips the constructor, so an omitted field stays null
        return compatibleWith != null ? compatibleWith : Collections.emptyList();
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public ItemStack getIcon() {
        return getItemStack();
    }

    public ItemStack getItemStack() {
        ItemStack item = ItemFinder.findItemStack(itemId);
        if (item == ItemStack.EMPTY)
            item = new ItemStack(Blocks.DIRT);

        NBTTagCompound displayTag = new NBTTagCompound();
        displayTag.setString("Name", rarity.color + "" + TextFormatting.BOLD + name);

        NBTTagList loreList = new NBTTagList();

        if (!descriptionLines.isEmpty()) {
            for (String line : descriptionLines)
                loreList.appendTag(new NBTTagString(TextUtils.translateColorCodes(line)));
            loreList.appendTag(new NBTTagString(""));
        }

        if (!getCompatibleWith().isEmpty()) {
            loreList.appendTag(new NBTTagString(TextFormatting.GREEN + "Compatible with: " + TextFormatting.WHITE + String.join(", ", getCompatibleWith())));
            loreList.appendTag(new NBTTagString(""));
        }

        loreList.appendTag(new NBTTagString(rarity.color + "" + TextFormatting.BOLD + rarity.display.toUpperCase()));

        displayTag.setTag("Lore", loreList);
        item.setTagInfo("display", displayTag);

        return item;
    }
}
