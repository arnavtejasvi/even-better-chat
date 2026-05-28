package com.arnav.chatoptimizer;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

@Environment(value=EnvType.CLIENT)
public final class ChatOptimizerKeys {
    public static final KeyMapping OPEN_CONFIG = KeyMappingHelper.registerKeyMapping(
        new KeyMapping("key.chatoptimizer.open_config", InputConstants.Type.KEYSYM, 79, KeyMapping.Category.MISC));
    public static final KeyMapping OPEN_SEARCH = KeyMappingHelper.registerKeyMapping(
        new KeyMapping("key.chatoptimizer.open_search", InputConstants.Type.KEYSYM, 74, KeyMapping.Category.MISC));

    private ChatOptimizerKeys() {}
}
