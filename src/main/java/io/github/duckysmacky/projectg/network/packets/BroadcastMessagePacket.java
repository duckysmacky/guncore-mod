package io.github.duckysmacky.projectg.network.packets;

import io.github.duckysmacky.projectg.game.CommandExecutor;
import io.github.duckysmacky.projectg.game.ServerBroadcaster;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class BroadcastMessagePacket implements IMessage {
    private String message;

    public BroadcastMessagePacket() {}

    public BroadcastMessagePacket(String message) {
        this.message = message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        this.message = new String(bytes);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = message.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static class Handler implements IMessageHandler<BroadcastMessagePacket, IMessage> {
        @Override
        public IMessage onMessage(BroadcastMessagePacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;
                    MinecraftServer server = player.getServer();

                    ServerBroadcaster.broadcastAsServer(server, message.message);
                });
            }
            return null;
        }
    }
}