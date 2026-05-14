/*
 * Decompiled with CFR 0.152.
 */
package com.arnav.chatoptimizer;

import com.arnav.chatoptimizer.ChatOptimizerConfig;
import com.arnav.chatoptimizer.ChatOptimizerConfigScreen;
import com.arnav.chatoptimizer.ChatOptimizerKeys;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class ChatOptimizerClient
implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"chatoptimizer");
    private static final KeyBinding OPEN_CONFIG = ChatOptimizerKeys.OPEN_CONFIG;

    public void onInitializeClient() {
        ChatOptimizerConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
        LOGGER.info("Chat Optimizer client initialized.");
        LOGGER.info("Chat history cap set to {} entries.", (Object)ChatOptimizerConfig.maxChatHistoryEntries);
        LOGGER.info("Open the config screen with the configured key binding.");
    }

    private void onEndClientTick(MinecraftClient client) {
        while (OPEN_CONFIG.wasPressed()) {
            client.setScreen((Screen)new ChatOptimizerConfigScreen(client.currentScreen));
        }
    }
}

