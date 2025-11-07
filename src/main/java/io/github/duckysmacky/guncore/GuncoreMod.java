package io.github.duckysmacky.guncore;

import io.github.duckysmacky.guncore.common.config.ConfigManager;
import io.github.duckysmacky.guncore.server.commands.GuncoreConfigCommand;
import io.github.duckysmacky.guncore.server.commands.GameCommand;
import io.github.duckysmacky.guncore.server.commands.MenuCommand;
import io.github.duckysmacky.guncore.common.network.CommonProxy;
import io.github.duckysmacky.guncore.common.network.PacketHandler;
import io.github.duckysmacky.guncore.server.events.ServerEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = GuncoreMod.MODID,
    name = GuncoreMod.NAME,
    version = GuncoreMod.VERSION
)
public class GuncoreMod {
    public static final String MODID = "guncore";
    public static final String NAME = "Guncore";
    public static final String VERSION = "1.0";
    @Mod.Instance(MODID)
    public static GuncoreMod INSTANCE;
    @SidedProxy(
        clientSide = "io.github.duckysmacky.guncore.client.network.ClientProxy",
        serverSide = "io.github.duckysmacky.guncore.common.network.CommonProxy"
    )
    public static CommonProxy PROXY;
    public static Logger LOGGER;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY.preInit(event);

        LOGGER = event.getModLog();
        LOGGER.info("Guncore mod is starting...");

        PacketHandler.init();

        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init(event);
        LOGGER.info("Guncore mod has started.");

        ConfigManager.instance().load();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new MenuCommand());
        event.registerServerCommand(new GuncoreConfigCommand());
        event.registerServerCommand(new GameCommand());
    }
}
