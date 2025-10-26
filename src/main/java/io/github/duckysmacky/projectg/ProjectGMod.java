package io.github.duckysmacky.projectg;

import io.github.duckysmacky.projectg.commands.MenuCommand;
import io.github.duckysmacky.projectg.network.PacketHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ProjectGMod.MODID, name = ProjectGMod.NAME, version = ProjectGMod.VERSION)
public class ProjectGMod
{
    public static final String MODID = "projectg";
    public static final String NAME = "Project G Mod";
    public static final String VERSION = "1.0";
    @Mod.Instance(MODID)
    public static ProjectGMod instance;

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PacketHandler.init();

        logger = event.getModLog();
        logger.info("Project-G is starting...");

        // config will be loaded here
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Project-G mod initialized");
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new MenuCommand());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
