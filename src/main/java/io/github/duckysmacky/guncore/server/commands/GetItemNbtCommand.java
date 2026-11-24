package io.github.duckysmacky.guncore.server.commands;

import io.github.duckysmacky.guncore.GuncoreMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;

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
        String snbt = tag == null ? "none" : tag.toString();
        Object id = stack.getItem().getRegistryName();
        int damage = stack.getMetadata();
        int count = stack.getCount();

        TextComponentString nbtLine = new TextComponentString("§aNBT: §f" + snbt);
        // clicking puts the NBT into the chat box, ready to copy
        if (tag != null) nbtLine.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, snbt));

        sender.sendMessage(new TextComponentString(
            String.format("§aItem: §f%s §aDamage: §f%d §aCount: §f%d", id, damage, count)
        ));
        sender.sendMessage(nbtLine);
        GuncoreMod.LOGGER.info("[getitemnbt] {}: Item: {} Damage: {} Count: {} NBT: {}", sender.getName(), id, damage, count, snbt);
    }
}
