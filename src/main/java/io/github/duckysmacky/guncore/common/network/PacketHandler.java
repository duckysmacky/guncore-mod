package io.github.duckysmacky.guncore.common.network;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.common.network.packets.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketHandler {
    private static SimpleNetworkWrapper networkWrapperInstance;
    private static int packetId = 0;

    private PacketHandler() {}

    public static SimpleNetworkWrapper instance() {
        return networkWrapperInstance;
    }

    public static void init() {
        networkWrapperInstance = NetworkRegistry.INSTANCE.newSimpleChannel(GuncoreMod.MODID);

        networkWrapperInstance.registerMessage(OpenMainMenuPacket.Handler.class, OpenMainMenuPacket.class, packetId++, Side.CLIENT);
        networkWrapperInstance.registerMessage(ReopenMenuPacket.Handler.class, ReopenMenuPacket.class, packetId++, Side.CLIENT);
        networkWrapperInstance.registerMessage(SyncGameInfoPacket.Handler.class, SyncGameInfoPacket.class, packetId++, Side.CLIENT);

        networkWrapperInstance.registerMessage(ExecuteCommandPacket.Handler.class, ExecuteCommandPacket.class, packetId++, Side.SERVER);
        networkWrapperInstance.registerMessage(BroadcastMessagePacket.Handler.class, BroadcastMessagePacket.class, packetId++, Side.SERVER);
        networkWrapperInstance.registerMessage(EquipGunPacket.Handler.class, EquipGunPacket.class, packetId++, Side.SERVER);
        networkWrapperInstance.registerMessage(EquipGadgetPacket.Handler.class, EquipGadgetPacket.class, packetId++, Side.SERVER);
        networkWrapperInstance.registerMessage(EquipKitPacket.Handler.class, EquipKitPacket.class, packetId++, Side.SERVER);
        networkWrapperInstance.registerMessage(UpdatePlayerListPacket.Handler.class, UpdatePlayerListPacket.class, packetId++, Side.SERVER);
        networkWrapperInstance.registerMessage(SetGameModePacket.Handler.class, SetGameModePacket.class, packetId++, Side.SERVER);
        networkWrapperInstance.registerMessage(SetGameModeVariantPacket.Handler.class, SetGameModeVariantPacket.class, packetId++, Side.SERVER);
        networkWrapperInstance.registerMessage(JoinTeamPacket.Handler.class, JoinTeamPacket.class, packetId++, Side.SERVER);
        networkWrapperInstance.registerMessage(ControlRoundPacket.Handler.class, ControlRoundPacket.class, packetId++, Side.SERVER);

        networkWrapperInstance.registerMessage(SyncCatalogPacket.Handler.class, SyncCatalogPacket.class, packetId++, Side.CLIENT);
        networkWrapperInstance.registerMessage(LoadConfigPacket.Handler.class, LoadConfigPacket.class, packetId++, Side.SERVER);
    }
}