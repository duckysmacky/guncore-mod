package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.server.game.EquipmentController;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class EquipBaseKitPacket implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<EquipBaseKitPacket, IMessage> {
        @Override
        public IMessage onMessage(EquipBaseKitPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;

                    EquipmentController.giveBaseItems(player);

                    PacketHandler.instance().sendTo(new ReopenMenuPacket(), player);
                });
            }
            return null;
        }
    }
}
