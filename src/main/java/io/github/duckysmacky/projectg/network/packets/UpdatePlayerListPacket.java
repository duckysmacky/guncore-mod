package io.github.duckysmacky.projectg.network.packets;

import io.github.duckysmacky.projectg.game.GameManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class UpdatePlayerListPacket implements IMessage {

    public UpdatePlayerListPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<UpdatePlayerListPacket, IMessage> {
        @Override
        public IMessage onMessage(UpdatePlayerListPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;
                    MinecraftServer server = player.getServer();

                    GameManager.instance().updatePlayerListAsServer(server);
                });
            }
            return null;
        }
    }
}