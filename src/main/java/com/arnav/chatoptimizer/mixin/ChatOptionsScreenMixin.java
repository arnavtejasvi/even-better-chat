package com.arnav.chatoptimizer.mixin;

import com.arnav.chatoptimizer.ChatOptimizerConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.ChatOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatOptionsScreen.class)
public abstract class ChatOptionsScreenMixin extends Screen {
    protected ChatOptionsScreenMixin(Component title) { super(title); }

    @Inject(method = "init", at = @At("RETURN"))
    private void chatoptimizer$addConfigButton(CallbackInfo ci) {
        int cx = this.width / 2;
        // Add our button above the standard Done button
        this.addRenderableWidget(
            Button.builder(
                Component.translatable("screen.chatoptimizer.open_settings"),
                btn -> Minecraft.getInstance().setScreen(new ChatOptimizerConfigScreen(this))
            ).pos(cx - 100, this.height - 51).size(200, 20).build()
        );
    }
}
