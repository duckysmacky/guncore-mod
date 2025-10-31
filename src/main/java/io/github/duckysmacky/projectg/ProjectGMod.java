package io.github.duckysmacky.projectg;

import io.github.duckysmacky.projectg.commands.ConfigReloadCommand;
import io.github.duckysmacky.projectg.commands.MenuCommand;
import io.github.duckysmacky.projectg.data.ConfigLoader;
import io.github.duckysmacky.projectg.network.CommonProxy;
import io.github.duckysmacky.projectg.network.PacketHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = ProjectGMod.MODID,
    name = ProjectGMod.NAME,
    version = ProjectGMod.VERSION
)
public class ProjectGMod {
    public static final String MODID = "projectg";
    public static final String NAME = "Project-G Mod";
    public static final String VERSION = "1.0";
    @Mod.Instance(MODID)
    public static ProjectGMod INSTANCE;
    @SidedProxy(
        clientSide = "io.github.duckysmacky.projectg.network.ClientProxy",
        serverSide = "io.github.duckysmacky.projectg.network.CommonProxy"
    )
    public static CommonProxy PROXY;
    public static Logger LOGGER;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY.preInit(event);

        LOGGER = event.getModLog();
        LOGGER.info("Project G mod is starting...");

        PacketHandler.init();

        ConfigLoader configLoader = ConfigLoader.instance();
        configLoader.loadConfig();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init(event);
        LOGGER.info("Project G mod has started.");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new MenuCommand());
        event.registerServerCommand(new ConfigReloadCommand());
    }
}
