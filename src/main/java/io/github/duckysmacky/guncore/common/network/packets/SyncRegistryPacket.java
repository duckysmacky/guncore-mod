package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/** Sends one file of a config registry (catalog, menu sections) from the server to clients */
public class SyncRegistryPacket implements IMessage {
    private String registryId;
    private int typeOrdinal;
    private String json;

    public SyncRegistryPacket() {}

    public SyncRegistryPacket(String registryId, int typeOrdinal, String json) {
        this.registryId = registryId;
        this.typeOrdinal = typeOrdinal;
        this.json = json;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, registryId);
        buf.writeInt(typeOrdinal);
        byte[] bytes = json.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.registryId = ByteBufUtils.readUTF8String(buf);
        this.typeOrdinal = buf.readInt();
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        this.json = new String(bytes);
    }

    public static class Handler implements IMessageHandler<SyncRegistryPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncRegistryPacket message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() ->
                    ConfigManager.instance().getRegistry(message.registryId).cache(message.typeOrdinal, message.json)
                );
            }
            return null;
        }
    }
}
