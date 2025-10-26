package io.github.duckysmacky.projectg.network;

import io.github.duckysmacky.projectg.ProjectGMod;
import io.github.duckysmacky.projectg.network.packets.OpenMenuPacket;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static SimpleNetworkWrapper instance;
    private static int id = 0;

    public static void init() {
        instance = NetworkRegistry.INSTANCE.newSimpleChannel(ProjectGMod.MODID);

        instance.registerMessage(OpenMenuPacket.Handler.class, OpenMenuPacket.class, id++, Side.CLIENT);
    }
}