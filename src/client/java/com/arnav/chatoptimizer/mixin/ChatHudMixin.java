package com.arnav.chatoptimizer.mixin;

import com.arnav.chatoptimizer.ChatLogger;
import com.arnav.chatoptimizer.ChatOptimizerConfig;
import com.arnav.chatoptimizer.ChatSearch;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ArrayListDeque;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={ChatComponent.class})
public class ChatHudMixin {
    @Shadow @Final private ArrayListDeque<String> recentChat;
    @Shadow @Final private List<GuiMessage> allMessages;

    @Unique private String chatoptimizer$lastCollapsedMessage;
    @Unique private Component chatoptimizer$lastCollapsedBaseText;
    @Unique private int chatoptimizer$duplicateCount = 1;

    @Shadow public void rescaleChat() {}

    // ─── Message history (T-arrow-up) ────────────────────────────────────────

    @Inject(method={"addRecentChat"}, at={@At(value="HEAD")}, cancellable=true)
    private void chatoptimizer$skipDuplicateHistory(String message, CallbackInfo ci) {
        if (ChatOptimizerConfig.collapseDuplicateMessages
                && Objects.equals(message, this.recentChat.peekLast())) {
            ci.cancel();
        }
    }

    @Inject(method={"addRecentChat"}, at={@At(value="TAIL")})
    private void chatoptimizer$trimHistory(String message, CallbackInfo ci) {
        if (!ChatOptimizerConfig.trimChatHistory) return;
        while (this.recentChat.size() > ChatOptimizerConfig.maxChatHistoryEntries) {
            this.recentChat.removeFirst();
        }
    }

    // ─── Incoming chat messages ───────────────────────────────────────────────

    @Inject(method={"addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V"},
            at={@At(value="HEAD")}, cancellable=true)
    private void chatoptimizer$filterMessage(Component message, MessageSignature sig,
                                             GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
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

    @Inject(method={"addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V"},
            at={@At(value="HEAD")}, cancellable=true)
    private void chatoptimizer$collapseDuplicates(Component message, MessageSignature sig,
                                                  GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        if (ci.isCancelled()) return;
        if (!ChatOptimizerConfig.collapseDuplicateMessages || this.allMessages.isEmpty()) return;
        GuiMessage lastLine = this.allMessages.get(this.allMessages.size() - 1);
        String msgStr = message.getString();
        if (Objects.equals(this.chatoptimizer$lastCollapsedMessage, msgStr)) {
            ++this.chatoptimizer$duplicateCount;
            Component updated = chatoptimizer$buildRepeated(
                this.chatoptimizer$lastCollapsedBaseText, this.chatoptimizer$duplicateCount);
            this.allMessages.set(this.allMessages.size() - 1,
                new GuiMessage(lastLine.addedTime(), updated, lastLine.signature(), lastLine.source(), lastLine.tag()));
            this.rescaleChat();
            ci.cancel();
        }
    }

    @Inject(method={"addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V"},
            at={@At(value="TAIL")})
    private void chatoptimizer$trackLastMessage(Component message, MessageSignature sig,
                                                GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        this.chatoptimizer$lastCollapsedMessage  = message.getString();
        this.chatoptimizer$lastCollapsedBaseText = message;
        this.chatoptimizer$duplicateCount        = 1;
    }

    @Inject(method={"addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V"},
            at={@At(value="TAIL")})
    private void chatoptimizer$logMessage(Component message, MessageSignature sig,
                                          GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        ChatLogger.log(message);
    }

    @Inject(method={"addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V"},
            at={@At(value="TAIL")})
    private void chatoptimizer$bufferForSearch(Component message, MessageSignature sig,
                                               GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        ChatSearch.addMessage(message.getString());
    }

    @Unique
    private Component chatoptimizer$buildRepeated(Component base, int count) {
        if (count <= 1) return base;
        MutableComponent suffix = Component.translatable("chatoptimizer.repeat_count", count)
            .withStyle(ChatFormatting.DARK_GRAY);
        return base.copy().append(Component.literal(" ")).append(suffix);
    }
}
