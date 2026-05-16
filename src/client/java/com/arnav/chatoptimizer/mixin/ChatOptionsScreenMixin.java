package com.arnav.chatoptimizer.mixin;

import com.arnav.chatoptimizer.ChatOptimizerConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ChatOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={GameOptionsScreen.class})
public abstract class ChatOptionsScreenMixin {
    // Cancel the default footer (which adds a 200px Done button) and replace it
    // with a narrower Done + our Chat Optimizer button side by side.
    @Inject(method={"initFooter"}, at={@At(value="HEAD")}, cancellable=true)
    private void chatoptimizer$addConfigButton(CallbackInfo ci) {
        if (!((Object)this instanceof ChatOptionsScreen)) return;
        Screen screen = (Screen)(Object)this;
        int cx = screen.width / 2;
        int footerY = screen.height - 27;

        ((ScreenAccessor)(Object)this).chatoptimizer$invokeAddDrawableChild(
            ButtonWidget.builder(ScreenTexts.DONE, btn -> screen.close())
                .position(cx - 100, footerY).size(98, 20).build()
        );
        ((ScreenAccessor)(Object)this).chatoptimizer$invokeAddDrawableChild(
            ButtonWidget.builder(
                Text.translatable("screen.chatoptimizer.open_settings"),
                btn -> MinecraftClient.getInstance().setScreen(new ChatOptimizerConfigScreen(screen))
            ).position(cx + 2, footerY).size(98, 20).build()
        );
        ci.cancel();
    }
}

