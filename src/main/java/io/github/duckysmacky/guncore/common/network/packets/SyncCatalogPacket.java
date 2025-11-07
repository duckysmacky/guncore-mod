package io.github.duckysmacky.guncore.common.network.packets;

import com.google.gson.Gson;
import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.common.config.catalog.CatalogType;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SyncCatalogPacket implements IMessage {
    private CatalogType catalogType;
    private String catalogJson;

    public SyncCatalogPacket() {}

    public SyncCatalogPacket(CatalogType catalogType, String catalogJson) {
        this.catalogType = catalogType;
        this.catalogJson = catalogJson;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(catalogType.ordinal());
        byte[] bytes = catalogJson.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.catalogType = CatalogType.values()[buf.readInt()];
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        this.catalogJson = new String(bytes);
    }

    public static class Handler implements IMessageHandler<SyncCatalogPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncCatalogPacket message, MessageContext context) {
            if (context.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    ConfigManager.instance().getCatalogManager().cacheCatalog(message.catalogType, message.catalogJson);

                    GuncoreMod.LOGGER.info("Synced " + message.catalogType.name() + " catalog");
                });
            }
            return null;
        }
    }
}