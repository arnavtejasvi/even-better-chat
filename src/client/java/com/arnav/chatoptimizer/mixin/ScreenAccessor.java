/*
 * Decompiled with CFR 0.152.
 */
package com.arnav.chatoptimizer.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(value=EnvType.CLIENT)
@Mixin(value={Screen.class})
public interface ScreenAccessor {
    @Invoker(value="addDrawableChild")
    public <T extends Element & Drawable> T chatoptimizer$invokeAddDrawableChild(T var1);
}

