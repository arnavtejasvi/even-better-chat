package com.arnav.chatoptimizer;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public final class ChatOptimizerKeys {
    public static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath("chatoptimizer", "general"));

    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
        "key.chatoptimizer.open_config",
        GLFW.GLFW_KEY_O,
        CATEGORY
    );
    public static final KeyMapping OPEN_SEARCH = new KeyMapping(
        "key.chatoptimizer.open_search",
        GLFW.GLFW_KEY_J,
        CATEGORY
    );

    private ChatOptimizerKeys() {}

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(OPEN_CONFIG);
        event.register(OPEN_SEARCH);
    }
}
