/*
 * Decompiled with CFR 0.152.
 */
package com.arnav.chatoptimizer.mixin;

import com.arnav.chatoptimizer.ChatOptimizerConfig;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.client.gui.hud.MessageIndicator;
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
    @Shadow
    @Final
    private ArrayListDeque<String> messageHistory;
    @Shadow
    @Final
    private List<ChatHudLine> messages;
    @Unique
    private String chatoptimizer$lastCollapsedMessage;
    @Unique
    private Text chatoptimizer$lastCollapsedBaseText;
    @Unique
    private int chatoptimizer$duplicateCount = 1;

    @Shadow
    private void refresh() {
    }

    @Inject(method={"addToMessageHistory"}, at={@At(value="HEAD")}, cancellable=true)
    private void chatoptimizer$skipDuplicateMessageHistory(String message, CallbackInfo ci) {
        if (ChatOptimizerConfig.collapseDuplicateMessages && Objects.equals(message, this.messageHistory.peekLast())) {
            ci.cancel();
        }
    }

    @Inject(method={"addToMessageHistory"}, at={@At(value="TAIL")})
    private void chatoptimizer$trimMessageHistory(String message, CallbackInfo ci) {
        if (!ChatOptimizerConfig.trimChatHistory) {
            return;
        }
        while (this.messageHistory.size() > ChatOptimizerConfig.maxChatHistoryEntries) {
            this.messageHistory.removeFirst();
        }
    }

    @Inject(method={"addMessage"}, at={@At(value="HEAD")}, cancellable=true)
    private void chatoptimizer$collapseDuplicateMessages(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
        if (!ChatOptimizerConfig.collapseDuplicateMessages || this.messages.isEmpty()) {
            return;
        }
        ChatHudLine lastLine = this.messages.get(this.messages.size() - 1);
        String messageString = message.getString();
        if (Objects.equals(this.chatoptimizer$lastCollapsedMessage, messageString)) {
            ++this.chatoptimizer$duplicateCount;
            Text updatedContent = this.chatoptimizer$buildRepeatedContent(this.chatoptimizer$lastCollapsedBaseText, this.chatoptimizer$duplicateCount);
            this.messages.set(this.messages.size() - 1, new ChatHudLine(lastLine.creationTick(), updatedContent, lastLine.signature(), lastLine.indicator()));
            this.refresh();
            ci.cancel();
        }
    }

    @Inject(method={"addMessage"}, at={@At(value="TAIL")})
    private void chatoptimizer$trackLastMessage(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
        this.chatoptimizer$lastCollapsedMessage = message.getString();
        this.chatoptimizer$lastCollapsedBaseText = message;
        this.chatoptimizer$duplicateCount = 1;
    }

    @Unique
    private Text chatoptimizer$buildRepeatedContent(Text baseText, int count) {
        if (count <= 1) {
            return baseText;
        }
        MutableText repeatedSuffix = Text.translatable((String)"chatoptimizer.repeat_count", (Object[])new Object[]{count}).formatted(Formatting.DARK_GRAY);
        return baseText.copy().append((Text)Text.literal((String)" ")).append((Text)repeatedSuffix);
    }
}

