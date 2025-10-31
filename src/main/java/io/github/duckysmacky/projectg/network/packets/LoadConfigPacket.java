package io.github.duckysmacky.projectg.network.packets;

import io.github.duckysmacky.projectg.data.config.ConfigLoader;
import io.github.duckysmacky.projectg.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class LoadConfigPacket implements IMessage {
    public LoadConfigPacket() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<LoadConfigPacket, IMessage> {
        @Override
        public IMessage onMessage(LoadConfigPacket message, MessageContext context) {
            context.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                MinecraftServer server = context.getServerHandler().player.getServer();

                ConfigLoader configLoader = ConfigLoader.instance();
                configLoader.loadConfigFromServer(server);

                SyncConfigPacket syncPacket = new SyncConfigPacket(
                    configLoader.getCachedGameConfig(),
                    configLoader.getCachedGuns(),
                    configLoader.getCachedKits(),
                    configLoader.getCachedGadgets(),
                    configLoader.getCachedLocations()
                );

                PacketHandler.instance().sendTo(syncPacket, context.getServerHandler().player);
            });
            return null;
        }
    }
}
