package com.arnav.chatoptimizer.mixin;

import com.arnav.chatoptimizer.ChatLogger;
import com.arnav.chatoptimizer.ChatOptimizerConfig;
import com.arnav.chatoptimizer.ChatSearch;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.ArrayListDeque;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={ChatHud.class})
public class ChatHudMixin {
    @Shadow @Final private ArrayListDeque<String> messageHistory;
    @Shadow @Final private List<ChatHudLine> messages;

    @Unique private String chatoptimizer$lastCollapsedMessage;
    @Unique private Text   chatoptimizer$lastCollapsedBaseText;
    @Unique private int    chatoptimizer$duplicateCount = 1;

    @Shadow private void refresh() {}

    // ─── Message history (T-arrow-up) ────────────────────────────────────────

    @Inject(method={"addToMessageHistory"}, at={@At(value="HEAD")}, cancellable=true)
    private void chatoptimizer$skipDuplicateHistory(String message, CallbackInfo ci) {
        if (ChatOptimizerConfig.collapseDuplicateMessages
                && Objects.equals(message, this.messageHistory.peekLast())) {
            ci.cancel();
        }
    }

    @Inject(method={"addToMessageHistory"}, at={@At(value="TAIL")})
    private void chatoptimizer$trimHistory(String message, CallbackInfo ci) {
        if (!ChatOptimizerConfig.trimChatHistory) return;
        while (this.messageHistory.size() > ChatOptimizerConfig.maxChatHistoryEntries) {
            this.messageHistory.removeFirst();
        }
    }

    // ─── Incoming chat messages ───────────────────────────────────────────────

    @Inject(method={"addMessage"}, at={@At(value="HEAD")}, cancellable=true)
    private void chatoptimizer$filterMessage(Text message, MessageSignatureData sig,
                                             MessageIndicator indicator, CallbackInfo ci) {
        if (!ChatOptimizerConfig.filterEnabled) return;
        String raw = message.getString();
        for (String player : ChatOptimizerConfig.blockedPlayers) {
            if (raw.contains("<" + player + ">")) { ci.cancel(); return; }
        }
        String lower = raw.toLowerCase();
        for (String keyword : ChatOptimizerConfig.blockedKeywords) {
            if (lower.contains(keyword.toLowerCase())) { ci.cancel(); return; }
        }
    }

    @Inject(method={"addMessage"}, at={@At(value="HEAD")}, cancellable=true)
    private void chatoptimizer$collapseDuplicates(Text message, MessageSignatureData sig,
                                                  MessageIndicator indicator, CallbackInfo ci) {
        if (ci.isCancelled()) return;
        if (!ChatOptimizerConfig.collapseDuplicateMessages || this.messages.isEmpty()) return;
        ChatHudLine lastLine = this.messages.get(this.messages.size() - 1);
        String msgStr = message.getString();
        if (Objects.equals(this.chatoptimizer$lastCollapsedMessage, msgStr)) {
            ++this.chatoptimizer$duplicateCount;
            Text updated = chatoptimizer$buildRepeated(
                this.chatoptimizer$lastCollapsedBaseText, this.chatoptimizer$duplicateCount);
            this.messages.set(this.messages.size() - 1,
                new ChatHudLine(lastLine.creationTick(), updated, lastLine.signature(), lastLine.indicator()));
            this.refresh();
            ci.cancel();
        }
    }

    @Inject(method={"addMessage"}, at={@At(value="TAIL")})
    private void chatoptimizer$trackLastMessage(Text message, MessageSignatureData sig,
                                                MessageIndicator indicator, CallbackInfo ci) {
        this.chatoptimizer$lastCollapsedMessage  = message.getString();
        this.chatoptimizer$lastCollapsedBaseText = message;
        this.chatoptimizer$duplicateCount        = 1;
    }

    @Inject(method={"addMessage"}, at={@At(value="TAIL")})
    private void chatoptimizer$logMessage(Text message, MessageSignatureData sig,
                                          MessageIndicator indicator, CallbackInfo ci) {
        ChatLogger.log(message);
    }

    @Inject(method={"addMessage"}, at={@At(value="TAIL")})
    private void chatoptimizer$bufferForSearch(Text message, MessageSignatureData sig,
                                               MessageIndicator indicator, CallbackInfo ci) {
        ChatSearch.addMessage(message.getString());
    }

    @Unique
    private Text chatoptimizer$buildRepeated(Text base, int count) {
        if (count <= 1) return base;
        MutableText suffix = Text.translatable("chatoptimizer.repeat_count", count)
            .formatted(Formatting.DARK_GRAY);
        return base.copy().append(Text.literal(" ")).append(suffix);
    }
}
