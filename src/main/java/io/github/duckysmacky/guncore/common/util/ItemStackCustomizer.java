package io.github.duckysmacky.guncore.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class ItemStackCustomizer {
    private ItemStack item;
    private CompoundTag displayTag;
    private ListTag loreList;

    public ItemStackCustomizer(ItemStack item) {
        if (item == null || item.isEmpty()) {
            this.item = new ItemStack(Blocks.DIRT);
        } else {
            this.item = item;
        }
        this.displayTag = item.getOrCreateTagElement("display");
        this.loreList = new ListTag();
    }

    public static ItemStackCustomizer from(String registryName) {
        return new ItemStackCustomizer(ItemFinder.findItemStack(registryName));
    }

    public ItemStackCustomizer setName(String name) {
        String translated = TextUtils.translateColorCodes(name);
        displayTag.putString("Name", Component.Serializer.toJson(Component.literal(translated)));
        return this;
    }

    public ItemStackCustomizer addLoreLine(String line) {
        String translated = TextUtils.translateColorCodes(line);
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(translated))));
        return this;
    }

    public ItemStackCustomizer resetLore() {
        loreList = new ListTag();
        return this;
    }

    public ItemStack getItemStack() {
        displayTag.put("Lore", loreList);
        item.addTagElement("display", displayTag);
        return item;
    }
}
