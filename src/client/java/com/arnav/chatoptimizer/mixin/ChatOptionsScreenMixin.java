/*
 * Decompiled with CFR 0.152.
 */
package com.arnav.chatoptimizer.mixin;

import com.arnav.chatoptimizer.ChatOptimizerConfigScreen;
import com.arnav.chatoptimizer.mixin.ScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ChatOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={GameOptionsScreen.class})
public abstract class ChatOptionsScreenMixin {
    @Inject(method={"initFooter"}, at={@At(value="TAIL")})
    private void chatoptimizer$addConfigButton(CallbackInfo ci) {
        if (!(this instanceof ChatOptionsScreen)) {
            return;
        }
        Screen screen = (Screen)this;
        int x = screen.width / 2 - 100;
        int y = screen.height - 52;
        ButtonWidget button = ButtonWidget.builder((Text)Text.translatable((String)"screen.chatoptimizer.open_settings"), widget -> MinecraftClient.getInstance().setScreen((Screen)new ChatOptimizerConfigScreen(screen))).position(x, y).size(200, 20).build();
        ((ScreenAccessor)((Object)this)).chatoptimizer$invokeAddDrawableChild(button);
    }
}

