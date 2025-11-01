package io.github.duckysmacky.projectg.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.projectg.data.config.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.projectg.game.EquipmentManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class EquipGadgetPacket implements IMessage {
    private GadgetEntry gadgetEntry;

    public EquipGadgetPacket() {}

    public EquipGadgetPacket(GadgetEntry gadgetEntry) {
        this.gadgetEntry = gadgetEntry;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        Gson gson = new Gson();
        String json = ByteBufUtils.readUTF8String(buf);
        this.gadgetEntry = gson.fromJson(json, GadgetEntry.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        Gson gson = new Gson();
        String json = gson.toJson(this.gadgetEntry);
        ByteBufUtils.writeUTF8String(buf, json);
    }

    public static class Handler implements IMessageHandler<EquipGadgetPacket, IMessage> {
        @Override
        public IMessage onMessage(EquipGadgetPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;
                    GadgetEntry gadget = message.gadgetEntry;

                    EquipmentManager.PlayerEquipment equipment = EquipmentManager.instance().getEquipment(player);

                    equipment.getGadget().ifPresent(g -> removeGadget(player, g));
                    giveGadget(player, gadget);
                    equipment.setGadget(gadget);
                });
            }
            return null;
        }

        private void removeGadget(EntityPlayerMP player, GadgetEntry gadget) {
            InventoryPlayer inventory = player.inventory;
            int slots = 9 * 4;

            for (int i = 0; i < slots; i++) {
                ItemStack item = inventory.getStackInSlot(i);

                ResourceLocation registryName = item.getItem().getRegistryName();
                if (registryName == null) continue;

                String itemId = registryName.toString();
                if (itemId.equals(gadget.getItemId()) || gadget.getAdditionalItemIds().stream().anyMatch(itemId::equals))
                    inventory.setInventorySlotContents(i, new ItemStack(Items.AIR));
            }
        }

        private void giveGadget(EntityPlayerMP player, GadgetEntry gadget) {
            int mainSlot = 4;
            player.inventory.mainInventory.set(mainSlot, gadget.getItemStack());

            int extraSlot = mainSlot + 9 * 3;
            List<ItemStack> extraItems = gadget.getAdditionalItemStacks();
            for (int i = 0; i < extraItems.size() && i < 3; i++)
                player.inventory.mainInventory.set(extraSlot - 9 * i, extraItems.get(i));
        }
    }
}