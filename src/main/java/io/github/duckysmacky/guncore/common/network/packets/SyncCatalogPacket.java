package io.github.duckysmacky.guncore.common.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.GuncoreMod;

public class SyncCatalogPacket {
    private final CatalogType catalogType;
    private final String catalogJson;

    public SyncCatalogPacket(CatalogType catalogType, String catalogJson) {
        this.catalogType = catalogType;
        this.catalogJson = catalogJson;
    }

    public static void encode(SyncCatalogPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.catalogType);
        buf.writeUtf(msg.catalogJson);
    }

    public static SyncCatalogPacket decode(FriendlyByteBuf buf) {
        CatalogType type = buf.readEnum(CatalogType.class);
        String json = buf.readUtf();
        return new SyncCatalogPacket(type, json);
    }

    public static void handle(SyncCatalogPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                ConfigManager.instance().getCatalogManager().cacheCatalog(msg.catalogType, msg.catalogJson);

                GuncoreMod.LOGGER.info("Synced {} catalog", msg.catalogType.name());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
