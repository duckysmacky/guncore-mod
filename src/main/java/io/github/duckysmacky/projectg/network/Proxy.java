package io.github.duckysmacky.projectg.network;

import io.github.duckysmacky.projectg.gui.MenuPage;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface Proxy {
    public void preInit(FMLPreInitializationEvent event);
    public void init(FMLInitializationEvent event);
    public void postInit(FMLPostInitializationEvent event);

    public void openMenuPage(MenuPage menuPage);
}