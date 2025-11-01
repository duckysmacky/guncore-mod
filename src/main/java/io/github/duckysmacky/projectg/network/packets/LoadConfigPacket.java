package io.github.duckysmacky.projectg.network.packets;

import io.github.duckysmacky.projectg.data.config.ConfigManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class LoadConfigPacket implements IMessage {
    public LoadConfigPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<LoadConfigPacket, IMessage> {
        @Override
        public IMessage onMessage(LoadConfigPacket message, MessageContext context) {
            if (context.side == Side.SERVER) {
                FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                    ConfigManager.instance().syncWithClients();
                });
            }
            return null;
        }
    }
}
