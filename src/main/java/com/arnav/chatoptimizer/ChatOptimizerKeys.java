package com.arnav.chatoptimizer;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public final class ChatOptimizerKeys {
    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
        "key.chatoptimizer.open_config",
        InputConstants.Type.KEYSYM,
        79, // O
        "key.category.chatoptimizer.general"
    );
    public static final KeyMapping OPEN_SEARCH = new KeyMapping(
        "key.chatoptimizer.open_search",
        InputConstants.Type.KEYSYM,
        74, // J
        "key.category.chatoptimizer.general"
    );

    private ChatOptimizerKeys() {}
}
