package com.arnav.chatoptimizer.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(value=EnvType.CLIENT)
@Mixin(value={Screen.class})
public interface ScreenAccessor {
    @Invoker(value="addRenderableWidget")
    public <T extends GuiEventListener & Renderable & NarratableEntry> T chatoptimizer$invokeAddRenderableWidget(T var1);
}
