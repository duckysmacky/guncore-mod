package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.catalog.items.ItemEntry;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class EquipGadgetPacket implements IMessage {
    private String gadgetName;

    public EquipGadgetPacket() {}

    public EquipGadgetPacket(ItemEntry gadget) {
        this.gadgetName = gadget.getName();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.gadgetName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, gadgetName);
    }

    public static class Handler implements IMessageHandler<EquipGadgetPacket, IMessage> {
        @Override
        public IMessage onMessage(EquipGadgetPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;

                    ConfigManager.instance().getCatalogManager().<ItemEntry>findEntry(CatalogType.KIT_GADGETS, message.gadgetName)
                        .ifPresent(gadget -> EquipmentController.equipGadget(player, gadget));

                    PacketHandler.instance().sendTo(new ReopenMenuPacket(), player);
                });
            }
            return null;
        }
    }
}
