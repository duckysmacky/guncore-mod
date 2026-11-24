package io.github.duckysmacky.guncore.server.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/** Prints the held item's id, damage and NBT (SNBT, the same format as {@code nbtData} in the item catalogs) */
public class GetItemNbtCommand extends CommandBase {
    @Override
    public String getName() {
        return "getitemnbt";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/getitemnbt - Prints the NBT data of the held item";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP)) throw new CommandException("Only players can use this command");

        ItemStack stack = ((EntityPlayerMP) sender).getHeldItemMainhand();
        if (stack.isEmpty()) throw new CommandException("Not holding an item");

        NBTTagCompound tag = stack.getTagCompound();
        sender.sendMessage(new TextComponentString(String.format(
            "§aItem: §f%s §aDamage: §f%d §aCount: §f%d", stack.getItem().getRegistryName(), stack.getMetadata(), stack.getCount()
        )));
        sender.sendMessage(new TextComponentString("§aNBT: §f" + (tag == null ? "none" : tag.toString())));
    }
}
