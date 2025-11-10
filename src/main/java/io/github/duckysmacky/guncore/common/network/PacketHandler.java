package io.github.duckysmacky.guncore.common.network;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.network.packets.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1.0.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(GuncoreMod.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        // client packets
        CHANNEL.registerMessage(packetId++, CacheCatalogPacket.class, CacheCatalogPacket::encode, CacheCatalogPacket::decode, CacheCatalogPacket::handle);
        CHANNEL.registerMessage(packetId++, SyncGameInfoPacket.class, SyncGameInfoPacket::encode, SyncGameInfoPacket::decode, SyncGameInfoPacket::handle);

        // server packets
        CHANNEL.registerMessage(packetId++, OpenMainMenuPacket.class, OpenMainMenuPacket::encode, OpenMainMenuPacket::decode, OpenMainMenuPacket::handle);
        CHANNEL.registerMessage(packetId++, ReopenMenuPagePacket.class, ReopenMenuPagePacket::encode, ReopenMenuPagePacket::decode, ReopenMenuPagePacket::handle);
        CHANNEL.registerMessage(packetId++, LoadConfigPacket.class, LoadConfigPacket::encode, LoadConfigPacket::decode, LoadConfigPacket::handle);
        CHANNEL.registerMessage(packetId++, ExecuteCommandPacket.class, ExecuteCommandPacket::encode, ExecuteCommandPacket::decode, ExecuteCommandPacket::handle);
        CHANNEL.registerMessage(packetId++, BroadcastMessagePacket.class, BroadcastMessagePacket::encode, BroadcastMessagePacket::decode, BroadcastMessagePacket::handle);
    }
}