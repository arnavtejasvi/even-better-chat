/*
 * Decompiled with CFR 0.152.
 */
package com.arnav.chatoptimizer;

import com.arnav.chatoptimizer.ChatOptimizerConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;

@Environment(value=EnvType.CLIENT)
public final class ChatOptimizerConfigScreen
extends Screen {
    private final Screen parent;
    private CyclingButtonWidget<Boolean> timestampsButton;
    private CyclingButtonWidget<Boolean> duplicateButton;
    private CyclingButtonWidget<Boolean> trimButton;
    private ButtonWidget decreaseHistoryButton;
    private ButtonWidget increaseHistoryButton;

    public ChatOptimizerConfigScreen(Screen parent) {
        super((Text)Text.translatable((String)"screen.chatoptimizer.title"));
        this.parent = parent;
    }

    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int top = 48;
        int rowSpacing = 24;
        int buttonWidth = 220;
        this.timestampsButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((boolean)ChatOptimizerConfig.showTimestamps).build(centerX - buttonWidth / 2, top, buttonWidth, 20, (Text)Text.translatable((String)"screen.chatoptimizer.timestamps"), (button, value) -> {
            ChatOptimizerConfig.setShowTimestamps(value);
            ChatOptimizerConfig.save();
        }));
        this.duplicateButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((boolean)ChatOptimizerConfig.collapseDuplicateMessages).build(centerX - buttonWidth / 2, top + rowSpacing, buttonWidth, 20, (Text)Text.translatable((String)"screen.chatoptimizer.duplicate_collapse"), (button, value) -> {
            ChatOptimizerConfig.setCollapseDuplicateMessages(value);
            ChatOptimizerConfig.save();
        }));
        this.trimButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((boolean)ChatOptimizerConfig.trimChatHistory).build(centerX - buttonWidth / 2, top + rowSpacing * 2, buttonWidth, 20, (Text)Text.translatable((String)"screen.chatoptimizer.history_trim"), (button, value) -> {
            ChatOptimizerConfig.setTrimChatHistory(value);
            ChatOptimizerConfig.save();
        }));
        this.decreaseHistoryButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.literal((String)"-"), button -> {
            ChatOptimizerConfig.setMaxChatHistoryEntries(ChatOptimizerConfig.maxChatHistoryEntries - 4);
            this.refreshHistoryButtons();
            ChatOptimizerConfig.save();
        }).position(centerX - 110, top + rowSpacing * 3).size(40, 20).build());
        this.increaseHistoryButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.literal((String)"+"), button -> {
            ChatOptimizerConfig.setMaxChatHistoryEntries(ChatOptimizerConfig.maxChatHistoryEntries + 4);
            this.refreshHistoryButtons();
            ChatOptimizerConfig.save();
        }).position(centerX + 70, top + rowSpacing * 3).size(40, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"gui.done"), button -> this.close()).position(centerX - 100, this.height - 30).size(200, 20).build());
        this.refreshHistoryButtons();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.fill(0, 0, this.width, this.height, -1072689136);
        super.render(context, mouseX, mouseY, deltaTicks);
        int centerX = this.width / 2;
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, (Text)Text.translatable((String)"screen.chatoptimizer.history_value", (Object[])new Object[]{ChatOptimizerConfig.maxChatHistoryEntries}), centerX, this.height / 2 - 18, 0xE0E0E0);
    }

    public void close() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }

    private void refreshHistoryButtons() {
        if (this.decreaseHistoryButton != null) {
            boolean bl = this.decreaseHistoryButton.active = ChatOptimizerConfig.maxChatHistoryEntries > 8;
        }
        if (this.increaseHistoryButton != null) {
            this.increaseHistoryButton.active = ChatOptimizerConfig.maxChatHistoryEntries < 256;
        }
    }
}

