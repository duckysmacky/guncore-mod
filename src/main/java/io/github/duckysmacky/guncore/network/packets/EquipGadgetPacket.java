package io.github.duckysmacky.guncore.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.data.config.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.guncore.game.EquipmentController;
import io.github.duckysmacky.guncore.game.EquipmentManager;
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

                    EquipmentController.equipGadget(player, gadget);
                });
            }
            return null;
        }
    }
}