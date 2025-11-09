package io.github.duckysmacky.guncore.common.network;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.network.packets.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1.0.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(GuncoreMod.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        // client packets
        CHANNEL.registerMessage(packetId++, SyncCatalogPacket.class, SyncCatalogPacket::encode, SyncCatalogPacket::decode, SyncCatalogPacket::handle);
        CHANNEL.registerMessage(packetId++, SyncGameInfoPacket.class, SyncGameInfoPacket::encode, SyncGameInfoPacket::decode, SyncGameInfoPacket::handle);
        CHANNEL.registerMessage(packetId++, OpenMainMenuPacket.class, OpenMainMenuPacket::encode, OpenMainMenuPacket::decode, OpenMainMenuPacket::handle);
        CHANNEL.registerMessage(packetId++, ReopenMenuPacket.class, ReopenMenuPacket::encode, ReopenMenuPacket::decode, ReopenMenuPacket::handle);
        CHANNEL.registerMessage(packetId++, RefreshMenuPacket.class, RefreshMenuPacket::encode, RefreshMenuPacket::decode, RefreshMenuPacket::handle);

        // server packets
        CHANNEL.registerMessage(packetId++, LoadConfigPacket.class, LoadConfigPacket::encode, LoadConfigPacket::decode, LoadConfigPacket::handle);
        CHANNEL.registerMessage(packetId++, EquipGunPacket.class, EquipGunPacket::encode, EquipGunPacket::decode, EquipGunPacket::handle);
        CHANNEL.registerMessage(packetId++, EquipKitPacket.class, EquipKitPacket::encode, EquipKitPacket::decode, EquipKitPacket::handle);
        CHANNEL.registerMessage(packetId++, EquipGadgetPacket.class, EquipGadgetPacket::encode, EquipGadgetPacket::decode, EquipGadgetPacket::handle);
        CHANNEL.registerMessage(packetId++, ControlRoundPacket.class, ControlRoundPacket::encode, ControlRoundPacket::decode, ControlRoundPacket::handle);
        CHANNEL.registerMessage(packetId++, SetGameModePacket.class, SetGameModePacket::encode, SetGameModePacket::decode, SetGameModePacket::handle);
        CHANNEL.registerMessage(packetId++, SetGameModeVariantPacket.class, SetGameModeVariantPacket::encode, SetGameModeVariantPacket::decode, SetGameModeVariantPacket::handle);
        CHANNEL.registerMessage(packetId++, JoinTeamPacket.class, JoinTeamPacket::encode, JoinTeamPacket::decode, JoinTeamPacket::handle);
        CHANNEL.registerMessage(packetId++, UpdatePlayerListPacket.class, UpdatePlayerListPacket::encode, UpdatePlayerListPacket::decode, UpdatePlayerListPacket::handle);
        CHANNEL.registerMessage(packetId++, ExecuteCommandPacket.class, ExecuteCommandPacket::encode, ExecuteCommandPacket::decode, ExecuteCommandPacket::handle);
        CHANNEL.registerMessage(packetId++, BroadcastMessagePacket.class, BroadcastMessagePacket::encode, BroadcastMessagePacket::decode, BroadcastMessagePacket::handle);
    }
}