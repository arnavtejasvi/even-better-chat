package com.arnav.chatoptimizer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class ChatOptimizerClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("chatoptimizer");
    private static final KeyBinding OPEN_CONFIG = ChatOptimizerKeys.OPEN_CONFIG;
    private static final KeyBinding OPEN_SEARCH = ChatOptimizerKeys.OPEN_SEARCH;

    @Override
    public void onInitializeClient() {
        ChatOptimizerConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
        LOGGER.info("Chat Optimizer client initialized.");
        LOGGER.info("Chat history cap set to {} entries.", ChatOptimizerConfig.maxChatHistoryEntries);
    }

    private void onEndClientTick(MinecraftClient client) {
        while (OPEN_CONFIG.wasPressed()) {
            client.setScreen(new ChatOptimizerConfigScreen(client.currentScreen));
        }
        while (OPEN_SEARCH.wasPressed()) {
            client.setScreen(new ChatSearchScreen());
        }
    }
}
