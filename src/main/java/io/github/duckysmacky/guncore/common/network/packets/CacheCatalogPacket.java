package io.github.duckysmacky.guncore.common.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.GuncoreMod;

public class CacheCatalogPacket {
    private final CatalogType catalogType;
    private final String catalogJson;

    public CacheCatalogPacket(CatalogType catalogType, String catalogJson) {
        this.catalogType = catalogType;
        this.catalogJson = catalogJson;
    }

    public static void encode(CacheCatalogPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.catalogType);
        buf.writeUtf(msg.catalogJson);
    }

    public static CacheCatalogPacket decode(FriendlyByteBuf buf) {
        CatalogType type = buf.readEnum(CatalogType.class);
        String json = buf.readUtf();
        return new CacheCatalogPacket(type, json);
    }

    public static void handle(CacheCatalogPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                GuncoreMod.LOGGER.info("Caching {} catalog", msg.catalogType.name());
                ConfigManager.instance().getCatalogManager().cacheCatalog(msg.catalogType, msg.catalogJson);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
