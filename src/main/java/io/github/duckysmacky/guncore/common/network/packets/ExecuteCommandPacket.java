package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.game.CommandExecutor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ExecuteCommandPacket implements IMessage {
    private String command;

    public ExecuteCommandPacket() {}

    public ExecuteCommandPacket(String command) {
        this.command = command;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        this.command = new String(bytes);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = command.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static class Handler implements IMessageHandler<ExecuteCommandPacket, IMessage> {
        @Override
        public IMessage onMessage(ExecuteCommandPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    EntityPlayerMP player = context.getServerHandler().player;
                    MinecraftServer server = player.getServer();

                    CommandExecutor.executeAsServer(server, message.command);
                });
            }
            return null;
        }
    }
}