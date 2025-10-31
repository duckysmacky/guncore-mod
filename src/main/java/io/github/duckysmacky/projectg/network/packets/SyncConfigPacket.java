package io.github.duckysmacky.projectg.network.packets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.data.config.ConfigManager;
import io.github.duckysmacky.projectg.data.config.GameConfig;
import io.github.duckysmacky.projectg.data.config.catalog.gadgets.GadgetEntry;
import io.github.duckysmacky.projectg.data.config.catalog.guns.GunEntry;
import io.github.duckysmacky.projectg.data.config.catalog.kits.KitEntry;
import io.github.duckysmacky.projectg.data.config.catalog.locations.LocationEntry;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.lang.reflect.Type;
import java.util.List;

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

                ProjectGMod.LOGGER.info("[ConfigLoader] Synced configs from server successfully");
            });
            return null;
        }
    }
}
