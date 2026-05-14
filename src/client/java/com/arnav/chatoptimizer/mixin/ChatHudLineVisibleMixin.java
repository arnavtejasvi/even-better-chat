/*
 * Decompiled with CFR 0.152.
 */
package com.arnav.chatoptimizer.mixin;

import com.arnav.chatoptimizer.ChatOptimizerConfig;
import com.arnav.chatoptimizer.ChatTimestampCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.client.gui.hud.MessageIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={ChatHudLine.Visible.class})
public class ChatHudLineVisibleMixin {
    @Unique
    private ChatTimestampCache.TimestampEntry chatoptimizer$timestampEntry;
    @Unique
    private long chatoptimizer$timestampMinute = -1L;
    @Unique
    private OrderedText chatoptimizer$baseContent = OrderedText.empty();
    @Unique
    private OrderedText chatoptimizer$cachedContent = OrderedText.empty();
    @Unique
    private int chatoptimizer$cachedTimestampWidth = -1;
    @Unique
    private long chatoptimizer$renderRevision = -1L;

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void chatoptimizer$init(int addedTime, OrderedText content, MessageIndicator indicator, boolean endOfEntry, CallbackInfo ci) {
        this.chatoptimizer$baseContent = content;
        this.chatoptimizer$timestampMinute = System.currentTimeMillis() / 60000L;
        this.chatoptimizer$renderRevision = ChatOptimizerConfig.renderRevision;
        this.chatoptimizer$rebuildContent();
    }

    @Inject(method={"method_75758"}, at={@At(value="RETURN")}, cancellable=true)
    private void chatoptimizer$extendWidth(TextRenderer textRenderer, CallbackInfoReturnable<Integer> cir) {
        if (!ChatOptimizerConfig.showTimestamps) {
            return;
        }
        this.chatoptimizer$ensureContentUpToDate();
        if (this.chatoptimizer$cachedTimestampWidth < 0) {
            this.chatoptimizer$cachedTimestampWidth = this.chatoptimizer$timestampEntry.width(textRenderer);
        }
        cir.setReturnValue((Object)((Integer)cir.getReturnValue() + this.chatoptimizer$cachedTimestampWidth));
    }

    @Inject(method={"content"}, at={@At(value="RETURN")}, cancellable=true)
    private void chatoptimizer$prefixContent(CallbackInfoReturnable<OrderedText> cir) {
        this.chatoptimizer$ensureContentUpToDate();
        cir.setReturnValue((Object)this.chatoptimizer$cachedContent);
    }

    @Unique
    private void chatoptimizer$ensureContentUpToDate() {
        if (this.chatoptimizer$renderRevision == ChatOptimizerConfig.renderRevision) {
            return;
        }
        this.chatoptimizer$renderRevision = ChatOptimizerConfig.renderRevision;
        this.chatoptimizer$rebuildContent();
    }

    @Unique
    private void chatoptimizer$rebuildContent() {
        this.chatoptimizer$cachedTimestampWidth = -1;
        if (ChatOptimizerConfig.showTimestamps) {
            this.chatoptimizer$timestampEntry = ChatTimestampCache.get(this.chatoptimizer$timestampMinute);
            this.chatoptimizer$cachedContent = OrderedText.concat((OrderedText)this.chatoptimizer$timestampEntry.orderedText(), (OrderedText)this.chatoptimizer$baseContent);
        } else {
            this.chatoptimizer$timestampEntry = null;
            this.chatoptimizer$cachedContent = this.chatoptimizer$baseContent;
        }
    }
}

