package io.github.duckysmacky.guncore.common.network.packets;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncConfigPacket implements IMessage {
    private String gameConfigJson;
    private String gunsJson;
    private String kitsJson;
    private String gadgetsJson;
    private String locationsJson;

    public SyncConfigPacket() {}

    public SyncConfigPacket(
        String gameConfigJson,
        String gunsJson,
        String kitsJson,
        String gadgetsJson,
        String locationsJson
    ) {
        this.gameConfigJson = gameConfigJson;
        this.gunsJson = gunsJson;
        this.kitsJson = kitsJson;
        this.gadgetsJson = gadgetsJson;
        this.locationsJson = locationsJson;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.gameConfigJson = readBytes(buf);
        this.gunsJson = readBytes(buf);
        this.kitsJson = readBytes(buf);
        this.gadgetsJson = readBytes(buf);
        this.locationsJson = readBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeBytes(buf, gameConfigJson);
        writeBytes(buf, gunsJson);
        writeBytes(buf, kitsJson);
        writeBytes(buf, gadgetsJson);
        writeBytes(buf, locationsJson);
    }

    private String readBytes(ByteBuf buf) {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes);
    }

    private void writeBytes(ByteBuf buf, String str) {
        byte[] bytes = str.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static class Handler implements IMessageHandler<SyncConfigPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncConfigPacket message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                ConfigManager configManager = ConfigManager.instance();

                configManager.cacheGameConfig(message.gameConfigJson);
                configManager.cacheGuns(message.gunsJson);
                configManager.cacheKits(message.kitsJson);
                configManager.cacheGadgets(message.gadgetsJson);
                configManager.cacheLocations(message.locationsJson);

                GuncoreMod.LOGGER.info("[ConfigLoader] Synced configs from server successfully");
            });
            return null;
        }
    }
}
