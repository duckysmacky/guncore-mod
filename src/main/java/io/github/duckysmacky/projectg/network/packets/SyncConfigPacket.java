package io.github.duckysmacky.projectg.network.packets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.data.config.ConfigLoader;
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

    public SyncConfigPacket() {} // required empty constructor

    public SyncConfigPacket(
        GameConfig gameConfig,
        List<GunEntry> guns,
        List<KitEntry> kits,
        List<GadgetEntry> gadgets,
        List<LocationEntry> locations
    ) {
        Gson gson = new Gson();
        this.gameConfigJson = gson.toJson(gameConfig);
        this.gunsJson = gson.toJson(guns);
        this.kitsJson = gson.toJson(kits);
        this.gadgetsJson = gson.toJson(gadgets);
        this.locationsJson = gson.toJson(locations);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, gameConfigJson);
        ByteBufUtils.writeUTF8String(buf, gunsJson);
        ByteBufUtils.writeUTF8String(buf, kitsJson);
        ByteBufUtils.writeUTF8String(buf, gadgetsJson);
        ByteBufUtils.writeUTF8String(buf, locationsJson);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.gameConfigJson = ByteBufUtils.readUTF8String(buf);
        this.gunsJson = ByteBufUtils.readUTF8String(buf);
        this.kitsJson = ByteBufUtils.readUTF8String(buf);
        this.gadgetsJson = ByteBufUtils.readUTF8String(buf);
        this.locationsJson = ByteBufUtils.readUTF8String(buf);
    }

    public static class Handler implements IMessageHandler<SyncConfigPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncConfigPacket message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Gson gson = new Gson();
                ConfigLoader configLoader = ConfigLoader.instance();

                configLoader.setCachedGameConfig(gson.fromJson(message.gameConfigJson, GameConfig.class));

                Type gunListType = new TypeToken<List<GunEntry>>() {}.getType();
                Type kitListType = new TypeToken<List<KitEntry>>() {}.getType();
                Type gadgetListType = new TypeToken<List<GadgetEntry>>() {}.getType();
                Type locationListType = new TypeToken<List<LocationEntry>>() {}.getType();

                configLoader.setCachedGuns(gson.fromJson(message.gunsJson, gunListType));
                configLoader.setCachedKits(gson.fromJson(message.kitsJson, kitListType));
                configLoader.setCachedGadgets(gson.fromJson(message.gadgetsJson, gadgetListType));
                configLoader.setCachedLocations(gson.fromJson(message.locationsJson, locationListType));

                ProjectGMod.LOGGER.info("[ConfigLoader] Synced configs from server successfully");
            });
            return null;
        }
    }
}
