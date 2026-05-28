package com.arnav.chatoptimizer;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ChatOptimizerMod.MOD_ID)
public class ChatOptimizerMod {
    public static final String MOD_ID = "chatoptimizer";

    public ChatOptimizerMod(IEventBus modEventBus) {
        ChatOptimizerConfig.load();
        modEventBus.addListener(this::onRegisterKeyMappings);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ChatOptimizerKeys.onRegisterKeyMappings(event);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        while (ChatOptimizerKeys.OPEN_CONFIG.consumeClick()) {
            minecraft.setScreen(new ChatOptimizerConfigScreen(minecraft.screen));
        }
        while (ChatOptimizerKeys.OPEN_SEARCH.consumeClick()) {
            minecraft.setScreen(new ChatSearchScreen());
        }
    }
}
