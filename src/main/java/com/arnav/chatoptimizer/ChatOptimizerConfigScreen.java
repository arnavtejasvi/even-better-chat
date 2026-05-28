package com.arnav.chatoptimizer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class ChatOptimizerConfigScreen extends Screen {
    private final Screen parent;
    private int activeTab = 0;

    private Button decreaseHistoryButton;
    private Button increaseHistoryButton;

    private EditBox playerInputField;
    private EditBox keywordInputField;

    public ChatOptimizerConfigScreen(Screen parent) {
        super(Component.translatable("screen.chatoptimizer.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int cx = this.width / 2;
        buildTabBar(cx);
        switch (activeTab) {
            case 1  -> initAppearanceTab(cx);
            case 2  -> initFiltersTab(cx);
            default -> initGeneralTab(cx);
        }
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), btn -> this.onClose())
            .pos(cx - 100, this.height - 28).size(200, 20).build());
    }

    private void buildTabBar(int cx) {
        int w = 70, h = 16, gap = 4;
        int startX = cx - (w * 3 + gap * 2) / 2;

        Button general    = tab(startX,              8, w, h, "general",    0);
        Button appearance = tab(startX + w + gap,    8, w, h, "appearance", 1);
        Button filters    = tab(startX + (w+gap) * 2,8, w, h, "filters",    2);

        general.active    = activeTab != 0;
        appearance.active = activeTab != 1;
        filters.active    = activeTab != 2;

        this.addRenderableWidget(general);
        this.addRenderableWidget(appearance);
        this.addRenderableWidget(filters);
    }

    private Button tab(int x, int y, int w, int h, String key, int index) {
        return Button.builder(Component.translatable("screen.chatoptimizer.tab." + key),
            btn -> { activeTab = index; this.rebuildWidgets(); })
            .pos(x, y).size(w, h).build();
    }

    private void initGeneralTab(int cx) {
        int top = 36, row = 22, bw = 220, x = cx - bw / 2;

        this.addRenderableWidget(CycleButton.onOffBuilder(ChatOptimizerConfig.showTimestamps)
            .create(x, top, bw, 20, Component.translatable("screen.chatoptimizer.timestamps"), (btn, v) -> {
                ChatOptimizerConfig.setShowTimestamps(v); ChatOptimizerConfig.save();
            }));
        this.addRenderableWidget(CycleButton.onOffBuilder(ChatOptimizerConfig.collapseDuplicateMessages)
            .create(x, top + row, bw, 20, Component.translatable("screen.chatoptimizer.duplicate_collapse"), (btn, v) -> {
                ChatOptimizerConfig.setCollapseDuplicateMessages(v); ChatOptimizerConfig.save();
            }));
        this.addRenderableWidget(CycleButton.onOffBuilder(ChatOptimizerConfig.trimChatHistory)
            .create(x, top + row * 2, bw, 20, Component.translatable("screen.chatoptimizer.history_trim"), (btn, v) -> {
                ChatOptimizerConfig.setTrimChatHistory(v); ChatOptimizerConfig.save();
            }));

        this.decreaseHistoryButton = this.addRenderableWidget(Button.builder(Component.literal("-"), btn -> {
            ChatOptimizerConfig.setMaxChatHistoryEntries(ChatOptimizerConfig.maxChatHistoryEntries - 4);
            refreshHistoryButtons(); ChatOptimizerConfig.save();
        }).pos(cx - 46, top + row * 4).size(40, 20).build());
        this.increaseHistoryButton = this.addRenderableWidget(Button.builder(Component.literal("+"), btn -> {
            ChatOptimizerConfig.setMaxChatHistoryEntries(ChatOptimizerConfig.maxChatHistoryEntries + 4);
            refreshHistoryButtons(); ChatOptimizerConfig.save();
        }).pos(cx + 6, top + row * 4).size(40, 20).build());

        this.addRenderableWidget(CycleButton.onOffBuilder(ChatOptimizerConfig.chatLoggingEnabled)
            .create(x, top + row * 5, bw, 20, Component.translatable("screen.chatoptimizer.chat_logging"), (btn, v) -> {
                ChatOptimizerConfig.setChatLoggingEnabled(v); ChatOptimizerConfig.save();
            }));
        this.addRenderableWidget(CycleButton.onOffBuilder(ChatOptimizerConfig.urlClickableEnabled)
            .create(x, top + row * 6, bw, 20, Component.translatable("screen.chatoptimizer.clickable_urls"), (btn, v) -> {
                ChatOptimizerConfig.setUrlClickableEnabled(v); ChatOptimizerConfig.save();
            }));

        refreshHistoryButtons();
    }

    private void refreshHistoryButtons() {
        if (decreaseHistoryButton != null) decreaseHistoryButton.active = ChatOptimizerConfig.maxChatHistoryEntries > 8;
        if (increaseHistoryButton != null) increaseHistoryButton.active = ChatOptimizerConfig.maxChatHistoryEntries < 256;
    }

    private void initAppearanceTab(int cx) {
        int top = 36, row = 22, bw = 220, x = cx - bw / 2;

        this.addRenderableWidget(CycleButton.<ChatOptimizerConfig.TimestampFormat>builder(
            fmt -> Component.translatable("screen.chatoptimizer.format." + fmt.name().toLowerCase()),
            ChatOptimizerConfig.timestampFormat)
            .withValues(List.of(ChatOptimizerConfig.TimestampFormat.H24, ChatOptimizerConfig.TimestampFormat.H12))
            .create(x, top, bw, 20, Component.translatable("screen.chatoptimizer.timestamp_format"), (btn, v) -> {
                ChatOptimizerConfig.setTimestampFormat(v); ChatOptimizerConfig.save();
            }));

        this.addRenderableWidget(CycleButton.<ChatOptimizerConfig.BracketStyle>builder(
            s -> Component.translatable("screen.chatoptimizer.bracket." + s.name().toLowerCase()),
            ChatOptimizerConfig.bracketStyle)
            .withValues(List.of(ChatOptimizerConfig.BracketStyle.SQUARE,
                                ChatOptimizerConfig.BracketStyle.ROUND,
                                ChatOptimizerConfig.BracketStyle.NONE))
            .create(x, top + row, bw, 20, Component.translatable("screen.chatoptimizer.bracket_style"), (btn, v) -> {
                ChatOptimizerConfig.setBracketStyle(v); ChatOptimizerConfig.save();
            }));

        this.addRenderableWidget(CycleButton.<Integer>builder(
            i -> Component.translatable("screen.chatoptimizer.color." + ChatOptimizerConfig.COLOR_KEYS[i]),
            colorIndex())
            .withValues(List.of(0, 1, 2, 3, 4, 5))
            .create(x, top + row * 2, bw, 20, Component.translatable("screen.chatoptimizer.timestamp_color"), (btn, i) -> {
                ChatOptimizerConfig.setTimestampColor(ChatOptimizerConfig.COLOR_VALUES[i]);
                ChatOptimizerConfig.save();
            }));
        this.addRenderableWidget(CycleButton.onOffBuilder(ChatOptimizerConfig.mentionHighlightEnabled)
            .create(x, top + row * 4, bw, 20, Component.translatable("screen.chatoptimizer.mention_highlight"), (btn, v) -> {
                ChatOptimizerConfig.setMentionHighlightEnabled(v); ChatOptimizerConfig.save();
            }));
        this.addRenderableWidget(CycleButton.onOffBuilder(ChatOptimizerConfig.mentionSoundEnabled)
            .create(x, top + row * 5, bw, 20, Component.translatable("screen.chatoptimizer.mention_sound"), (btn, v) -> {
                ChatOptimizerConfig.setMentionSoundEnabled(v); ChatOptimizerConfig.save();
            }));
    }

    private int colorIndex() {
        for (int i = 0; i < ChatOptimizerConfig.COLOR_VALUES.length; i++) {
            if (ChatOptimizerConfig.COLOR_VALUES[i] == ChatOptimizerConfig.timestampColor) return i;
        }
        return 0;
    }

    private void initFiltersTab(int cx) {
        int top = 36, row = 22, bw = 220, x = cx - bw / 2;
        int fw = 160, aw = 56;

        this.addRenderableWidget(CycleButton.onOffBuilder(ChatOptimizerConfig.filterEnabled)
            .create(x, top, bw, 20, Component.translatable("screen.chatoptimizer.filter.enabled"), (btn, v) -> {
                ChatOptimizerConfig.setFilterEnabled(v); ChatOptimizerConfig.save();
            }));

        int playersInputY = top + row + 14;
        this.playerInputField = this.addRenderableWidget(
            new EditBox(this.font, x, playersInputY, fw, 20, Component.empty()));
        this.playerInputField.setMaxLength(64);
        this.playerInputField.setSuggestion("Player name...");
        this.addRenderableWidget(Button.builder(Component.translatable("screen.chatoptimizer.filter.add"), btn -> {
            String name = this.playerInputField.getValue().trim();
            if (!name.isEmpty()) {
                ChatOptimizerConfig.blockedPlayers.add(name);
                ChatOptimizerConfig.save();
                this.playerInputField.setValue("");
            }
        }).pos(x + fw + 4, playersInputY).size(aw, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("screen.chatoptimizer.filter.clear"), btn -> {
            ChatOptimizerConfig.blockedPlayers.clear();
            ChatOptimizerConfig.save();
        }).pos(x, playersInputY + row).size(100, 20).build());

        int kwInputY = playersInputY + row * 3 + 4;
        this.keywordInputField = this.addRenderableWidget(
            new EditBox(this.font, x, kwInputY, fw, 20, Component.empty()));
        this.keywordInputField.setMaxLength(64);
        this.keywordInputField.setSuggestion("Keyword...");
        this.addRenderableWidget(Button.builder(Component.translatable("screen.chatoptimizer.filter.add"), btn -> {
            String kw = this.keywordInputField.getValue().trim();
            if (!kw.isEmpty()) {
                ChatOptimizerConfig.blockedKeywords.add(kw);
                ChatOptimizerConfig.save();
                this.keywordInputField.setValue("");
            }
        }).pos(x + fw + 4, kwInputY).size(aw, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("screen.chatoptimizer.filter.clear"), btn -> {
            ChatOptimizerConfig.blockedKeywords.clear();
            ChatOptimizerConfig.save();
        }).pos(x, kwInputY + row).size(100, 20).build());
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xC0000000);
        super.render(ctx, mouseX, mouseY, delta);
        int cx = this.width / 2;
        ctx.drawCenteredString(this.font, this.title, cx, 29, 0xFFFFFF);

        int top = 36, row = 22, x = cx - 110;

        if (activeTab == 0) {
            ctx.drawCenteredString(this.font,
                Component.translatable("screen.chatoptimizer.history_value", ChatOptimizerConfig.maxChatHistoryEntries),
                cx, top + row * 3 + 7, 0xE0E0E0);
        } else if (activeTab == 2) {
            int playersInputY = top + row + 14;
            int kwInputY = playersInputY + row * 3 + 4;

            ctx.drawString(this.font,
                Component.translatable("screen.chatoptimizer.filter.blocked_players",
                    ChatOptimizerConfig.blockedPlayers.size()),
                x, top + row + 2, 0xE0E0E0);

            String players = String.join(", ", ChatOptimizerConfig.blockedPlayers);
            if (!players.isEmpty()) {
                if (players.length() > 42) players = players.substring(0, 39) + "...";
                ctx.drawString(this.font, Component.literal(players), x, playersInputY + row + row + 4, 0x888888);
            }

            ctx.drawString(this.font,
                Component.translatable("screen.chatoptimizer.filter.blocked_keywords",
                    ChatOptimizerConfig.blockedKeywords.size()),
                x, kwInputY - 12, 0xE0E0E0);

            String keywords = String.join(", ", ChatOptimizerConfig.blockedKeywords);
            if (!keywords.isEmpty()) {
                if (keywords.length() > 42) keywords = keywords.substring(0, 39) + "...";
                ctx.drawString(this.font, Component.literal(keywords), x, kwInputY + row + row + 4, 0x888888);
            }
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }
}
