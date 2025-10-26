package io.github.duckysmacky.projectg;

import io.github.duckysmacky.projectg.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ProjectGMod.MODID, name = ProjectGMod.NAME, version = ProjectGMod.VERSION)
public class ProjectGMod
{
    public static final String MODID = "projectg";
    public static final String NAME = "Project G Mod";
    public static final String VERSION = "1.0";

    @Mod.Instance(MODID)
    public static ProjectGMod instance;
    @SidedProxy(clientSide = "io.github.duckysmacky.projectg.proxy.ClientProxy", serverSide = "io.github.duckysmacky.projectg.proxy.CommonProxy")
    public static CommonProxy proxy;

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        logger = event.getModLog();
        logger.info("Project-G is starting...");

        // config will be loaded here
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        logger.info("Project-G mod initialized");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
