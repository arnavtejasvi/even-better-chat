package com.arnav.chatoptimizer.mixin;

import com.arnav.chatoptimizer.ChatOptimizerConfig;
import com.arnav.chatoptimizer.ChatTimestampCache;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiMessage.Line.class)
public class ChatHudLineVisibleMixin {
    @Unique private ChatTimestampCache.TimestampEntry chatoptimizer$timestampEntry;
    @Unique private long chatoptimizer$timestampMinute = -1L;
    @Unique private FormattedCharSequence chatoptimizer$baseContent = FormattedCharSequence.EMPTY;
    @Unique private FormattedCharSequence chatoptimizer$cachedContent = FormattedCharSequence.EMPTY;
    @Unique private long chatoptimizer$renderRevision = -1L;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void chatoptimizer$init(int addedTime, FormattedCharSequence content,
                                    GuiMessageTag tag, boolean endOfEntry, CallbackInfo ci) {
        this.chatoptimizer$baseContent = content;
        this.chatoptimizer$timestampMinute = System.currentTimeMillis() / 60000L;
        this.chatoptimizer$renderRevision = ChatOptimizerConfig.renderRevision;
        this.chatoptimizer$rebuildContent();
    }

    @Inject(method = "content", at = @At("RETURN"), cancellable = true)
    private void chatoptimizer$prefixContent(CallbackInfoReturnable<FormattedCharSequence> cir) {
        this.chatoptimizer$ensureContentUpToDate();
        cir.setReturnValue(this.chatoptimizer$cachedContent);
    }

    @Unique
    private void chatoptimizer$ensureContentUpToDate() {
        if (this.chatoptimizer$renderRevision == ChatOptimizerConfig.renderRevision) return;
        this.chatoptimizer$renderRevision = ChatOptimizerConfig.renderRevision;
        this.chatoptimizer$rebuildContent();
    }

    @Unique
    private void chatoptimizer$rebuildContent() {
        if (ChatOptimizerConfig.showTimestamps) {
            this.chatoptimizer$timestampEntry = ChatTimestampCache.get(this.chatoptimizer$timestampMinute);
            this.chatoptimizer$cachedContent = FormattedCharSequence.composite(
                this.chatoptimizer$timestampEntry.orderedText(), this.chatoptimizer$baseContent);
        } else {
            this.chatoptimizer$timestampEntry = null;
            this.chatoptimizer$cachedContent = this.chatoptimizer$baseContent;
        }
    }
}
