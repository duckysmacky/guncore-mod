package io.github.duckysmacky.guncore.client.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ClientKeyBindings {
    public static final String CATEGORY = "Guncore";

    public static final KeyMapping OPEN_MENU_KEY = new KeyMapping(
        "Open the menu",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_M,
        CATEGORY
    );
}