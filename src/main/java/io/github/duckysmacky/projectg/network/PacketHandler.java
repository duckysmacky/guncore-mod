package io.github.duckysmacky.projectg.network;

import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.network.packets.*;
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
        networkWrapperInstance = NetworkRegistry.INSTANCE.newSimpleChannel(ProjectGMod.MODID);

        networkWrapperInstance.registerMessage(OpenMainMenuPacket.Handler.class, OpenMainMenuPacket.class, packetId++, Side.CLIENT);

        networkWrapperInstance.registerMessage(ExecuteCommandPacket.Handler.class, ExecuteCommandPacket.class, packetId++, Side.SERVER);
        networkWrapperInstance.registerMessage(BroadcastMessagePacket.Handler.class, BroadcastMessagePacket.class, packetId++, Side.SERVER);

        networkWrapperInstance.registerMessage(UpdatePlayerListPacket.Handler.class, UpdatePlayerListPacket.class, packetId++, Side.SERVER);

        networkWrapperInstance.registerMessage(LoadConfigPacket.Handler.class, LoadConfigPacket.class, packetId++, Side.SERVER);
        networkWrapperInstance.registerMessage(SyncConfigPacket.Handler.class, SyncConfigPacket.class, packetId++, Side.CLIENT);
    }
}