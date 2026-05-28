package com.arnav.chatoptimizer.mixin;

import com.arnav.chatoptimizer.ChatOptimizerConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.ChatOptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={OptionsSubScreen.class})
public abstract class ChatOptionsScreenMixin {
    @Inject(method={"addFooter"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void chatoptimizer$addConfigButton(CallbackInfo ci) {
        if (!((Object)this instanceof ChatOptionsScreen)) return;
        OptionsSubScreen self = (OptionsSubScreen)(Object)this;
        ci.cancel();
        self.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE,
            btn -> self.onClose()).size(98, 20).build());
        self.layout.addToFooter(Button.builder(
            Component.translatable("screen.chatoptimizer.open_settings"),
            btn -> Minecraft.getInstance().setScreen(
                new ChatOptimizerConfigScreen((Screen)(Object)this))
        ).size(98, 20).build());
    }
}
