package io.github.duckysmacky.guncore.client;

import io.github.duckysmacky.guncore.GuncoreMod;
import io.github.duckysmacky.guncore.client.gui.menu.BaseMenu;
import io.github.duckysmacky.guncore.client.gui.menu.MenuManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber
public class KeybindHandler {
    public static final KeyBinding OPEN_MENU = new KeyBinding(
            "Open the Guncore menu",
            Keyboard.KEY_M,
            "key.categories.guncore"
    );

    public static void init() {
        ClientRegistry.registerKeyBinding(OPEN_MENU);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (OPEN_MENU.isPressed()) {
            BaseMenu menu = MenuManager.instance().getMainMenu();

            GuncoreMod.PROXY.openMenuPage(menu);
        }
    }
}