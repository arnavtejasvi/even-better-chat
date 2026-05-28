/*
 * Decompiled with CFR 0.152.
 */
package com.arnav.chatoptimizer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public final class ChatOptimizerKeys {
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("chatoptimizer", "general"));
    public static final KeyBinding OPEN_CONFIG = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.chatoptimizer.open_config", InputUtil.Type.KEYSYM, 79, CATEGORY));
    public static final KeyBinding OPEN_SEARCH = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.chatoptimizer.open_search", InputUtil.Type.KEYSYM, 74, CATEGORY));

    private ChatOptimizerKeys() {
    }
}

