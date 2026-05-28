package com.arnav.chatoptimizer;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public final class ChatOptimizerConfigScreen extends Screen {
    private final Screen parent;
    private int activeTab = 0;

    // General tab refs
    private ButtonWidget decreaseHistoryButton;
    private ButtonWidget increaseHistoryButton;

    // Filters tab refs
    private TextFieldWidget playerInputField;
    private TextFieldWidget keywordInputField;

    public ChatOptimizerConfigScreen(Screen parent) {
        super(Text.translatable("screen.chatoptimizer.title"));
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
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), btn -> this.close())
            .position(cx - 100, this.height - 28).size(200, 20).build());
    }

    // ─── Tab bar ──────────────────────────────────────────────────────────────

    private void buildTabBar(int cx) {
        int w = 70, h = 16, gap = 4;
        int startX = cx - (w * 3 + gap * 2) / 2;

        ButtonWidget general    = tab(startX,              8, w, h, "general",    0);
        ButtonWidget appearance = tab(startX + w + gap,    8, w, h, "appearance", 1);
        ButtonWidget filters    = tab(startX + (w+gap) * 2,8, w, h, "filters",    2);

        general.active    = activeTab != 0;
        appearance.active = activeTab != 1;
        filters.active    = activeTab != 2;

        this.addDrawableChild(general);
        this.addDrawableChild(appearance);
        this.addDrawableChild(filters);
    }

    private ButtonWidget tab(int x, int y, int w, int h, String key, int index) {
        return ButtonWidget.builder(Text.translatable("screen.chatoptimizer.tab." + key),
            btn -> { activeTab = index; this.clearAndInit(); })
            .position(x, y).size(w, h).build();
    }

    // ─── General tab ──────────────────────────────────────────────────────────

    private void initGeneralTab(int cx) {
        int top = 36, row = 22, bw = 220, x = cx - bw / 2;

        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ChatOptimizerConfig.showTimestamps)
            .build(x, top, bw, 20, Text.translatable("screen.chatoptimizer.timestamps"), (btn, v) -> {
                ChatOptimizerConfig.setShowTimestamps(v); ChatOptimizerConfig.save();
            }));
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ChatOptimizerConfig.collapseDuplicateMessages)
            .build(x, top + row, bw, 20, Text.translatable("screen.chatoptimizer.duplicate_collapse"), (btn, v) -> {
                ChatOptimizerConfig.setCollapseDuplicateMessages(v); ChatOptimizerConfig.save();
            }));
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ChatOptimizerConfig.trimChatHistory)
            .build(x, top + row * 2, bw, 20, Text.translatable("screen.chatoptimizer.history_trim"), (btn, v) -> {
                ChatOptimizerConfig.setTrimChatHistory(v); ChatOptimizerConfig.save();
            }));

        // History cap: label rendered in render(), -/+ buttons centered
        this.decreaseHistoryButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("-"), btn -> {
            ChatOptimizerConfig.setMaxChatHistoryEntries(ChatOptimizerConfig.maxChatHistoryEntries - 4);
            refreshHistoryButtons(); ChatOptimizerConfig.save();
        }).position(cx - 46, top + row * 4).size(40, 20).build());
        this.increaseHistoryButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("+"), btn -> {
            ChatOptimizerConfig.setMaxChatHistoryEntries(ChatOptimizerConfig.maxChatHistoryEntries + 4);
            refreshHistoryButtons(); ChatOptimizerConfig.save();
        }).position(cx + 6, top + row * 4).size(40, 20).build());

        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ChatOptimizerConfig.chatLoggingEnabled)
            .build(x, top + row * 5, bw, 20, Text.translatable("screen.chatoptimizer.chat_logging"), (btn, v) -> {
                ChatOptimizerConfig.setChatLoggingEnabled(v); ChatOptimizerConfig.save();
            }));
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ChatOptimizerConfig.urlClickableEnabled)
            .build(x, top + row * 6, bw, 20, Text.translatable("screen.chatoptimizer.clickable_urls"), (btn, v) -> {
                ChatOptimizerConfig.setUrlClickableEnabled(v); ChatOptimizerConfig.save();
            }));

        refreshHistoryButtons();
    }

    private void refreshHistoryButtons() {
        if (decreaseHistoryButton != null) decreaseHistoryButton.active = ChatOptimizerConfig.maxChatHistoryEntries > 8;
        if (increaseHistoryButton != null) increaseHistoryButton.active = ChatOptimizerConfig.maxChatHistoryEntries < 256;
    }

    // ─── Appearance tab ───────────────────────────────────────────────────────

    private void initAppearanceTab(int cx) {
        int top = 36, row = 22, bw = 220, x = cx - bw / 2;

        this.addDrawableChild(CyclingButtonWidget.<ChatOptimizerConfig.TimestampFormat>builder(
            fmt -> Text.translatable("screen.chatoptimizer.format." + fmt.name().toLowerCase()),
            ChatOptimizerConfig.timestampFormat)
            .values(List.of(ChatOptimizerConfig.TimestampFormat.H24, ChatOptimizerConfig.TimestampFormat.H12))
            .build(x, top, bw, 20, Text.translatable("screen.chatoptimizer.timestamp_format"), (btn, v) -> {
                ChatOptimizerConfig.setTimestampFormat(v); ChatOptimizerConfig.save();
            }));

        this.addDrawableChild(CyclingButtonWidget.<ChatOptimizerConfig.BracketStyle>builder(
            s -> Text.translatable("screen.chatoptimizer.bracket." + s.name().toLowerCase()),
            ChatOptimizerConfig.bracketStyle)
            .values(List.of(ChatOptimizerConfig.BracketStyle.SQUARE,
                            ChatOptimizerConfig.BracketStyle.ROUND,
                            ChatOptimizerConfig.BracketStyle.NONE))
            .build(x, top + row, bw, 20, Text.translatable("screen.chatoptimizer.bracket_style"), (btn, v) -> {
                ChatOptimizerConfig.setBracketStyle(v); ChatOptimizerConfig.save();
            }));

        this.addDrawableChild(CyclingButtonWidget.<Integer>builder(
            i -> Text.translatable("screen.chatoptimizer.color." + ChatOptimizerConfig.COLOR_KEYS[i]),
            colorIndex())
            .values(List.of(0, 1, 2, 3, 4, 5))
            .build(x, top + row * 2, bw, 20, Text.translatable("screen.chatoptimizer.timestamp_color"), (btn, i) -> {
                ChatOptimizerConfig.setTimestampColor(ChatOptimizerConfig.COLOR_VALUES[i]);
                ChatOptimizerConfig.save();
            }));
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ChatOptimizerConfig.mentionHighlightEnabled)
            .build(x, top + row * 4, bw, 20, Text.translatable("screen.chatoptimizer.mention_highlight"), (btn, v) -> {
                ChatOptimizerConfig.setMentionHighlightEnabled(v); ChatOptimizerConfig.save();
            }));
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ChatOptimizerConfig.mentionSoundEnabled)
            .build(x, top + row * 5, bw, 20, Text.translatable("screen.chatoptimizer.mention_sound"), (btn, v) -> {
                ChatOptimizerConfig.setMentionSoundEnabled(v); ChatOptimizerConfig.save();
            }));
    }

    private int colorIndex() {
        for (int i = 0; i < ChatOptimizerConfig.COLOR_VALUES.length; i++) {
            if (ChatOptimizerConfig.COLOR_VALUES[i] == ChatOptimizerConfig.timestampColor) return i;
        }
        return 0;
    }

    // ─── Filters tab ──────────────────────────────────────────────────────────

    private void initFiltersTab(int cx) {
        int top = 36, row = 22, bw = 220, x = cx - bw / 2;
        int fw = 160, aw = 56;

        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ChatOptimizerConfig.filterEnabled)
            .build(x, top, bw, 20, Text.translatable("screen.chatoptimizer.filter.enabled"), (btn, v) -> {
                ChatOptimizerConfig.setFilterEnabled(v); ChatOptimizerConfig.save();
            }));

        // Players section (label drawn in render)
        int playersInputY = top + row + 14;
        this.playerInputField = this.addDrawableChild(
            new TextFieldWidget(this.textRenderer, x, playersInputY, fw, 20, Text.empty()));
        this.playerInputField.setMaxLength(64);
        this.playerInputField.setSuggestion("Player name...");
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.chatoptimizer.filter.add"), btn -> {
            String name = this.playerInputField.getText().trim();
            if (!name.isEmpty()) {
                ChatOptimizerConfig.blockedPlayers.add(name);
                ChatOptimizerConfig.save();
                this.playerInputField.setText("");
            }
        }).position(x + fw + 4, playersInputY).size(aw, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.chatoptimizer.filter.clear"), btn -> {
            ChatOptimizerConfig.blockedPlayers.clear();
            ChatOptimizerConfig.save();
        }).position(x, playersInputY + row).size(100, 20).build());

        // Keywords section (label drawn in render)
        int kwInputY = playersInputY + row * 3 + 4;
        this.keywordInputField = this.addDrawableChild(
            new TextFieldWidget(this.textRenderer, x, kwInputY, fw, 20, Text.empty()));
        this.keywordInputField.setMaxLength(64);
        this.keywordInputField.setSuggestion("Keyword...");
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.chatoptimizer.filter.add"), btn -> {
            String kw = this.keywordInputField.getText().trim();
            if (!kw.isEmpty()) {
                ChatOptimizerConfig.blockedKeywords.add(kw);
                ChatOptimizerConfig.save();
                this.keywordInputField.setText("");
            }
        }).position(x + fw + 4, kwInputY).size(aw, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.chatoptimizer.filter.clear"), btn -> {
            ChatOptimizerConfig.blockedKeywords.clear();
            ChatOptimizerConfig.save();
        }).position(x, kwInputY + row).size(100, 20).build());
    }

    // ─── Rendering ────────────────────────────────────────────────────────────

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xC0000000);
        super.render(ctx, mouseX, mouseY, delta);
        int cx = this.width / 2;
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, cx, 29, 0xFFFFFF);

        int top = 36, row = 22, x = cx - 110;

        if (activeTab == 0) {
            // History cap label between the -/+ buttons
            ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("screen.chatoptimizer.history_value", ChatOptimizerConfig.maxChatHistoryEntries),
                cx, top + row * 3 + 7, 0xE0E0E0);
        } else if (activeTab == 2) {
            int playersInputY = top + row + 14;
            int kwInputY = playersInputY + row * 3 + 4;

            ctx.drawTextWithShadow(this.textRenderer,
                Text.translatable("screen.chatoptimizer.filter.blocked_players",
                    ChatOptimizerConfig.blockedPlayers.size()),
                x, top + row + 2, 0xE0E0E0);

            String players = String.join(", ", ChatOptimizerConfig.blockedPlayers);
            if (!players.isEmpty()) {
                if (players.length() > 42) players = players.substring(0, 39) + "...";
                ctx.drawTextWithShadow(this.textRenderer, Text.literal(players),
                    x, playersInputY + row + row + 4, 0x888888);
            }

            ctx.drawTextWithShadow(this.textRenderer,
                Text.translatable("screen.chatoptimizer.filter.blocked_keywords",
                    ChatOptimizerConfig.blockedKeywords.size()),
                x, kwInputY - 12, 0xE0E0E0);

            String keywords = String.join(", ", ChatOptimizerConfig.blockedKeywords);
            if (!keywords.isEmpty()) {
                if (keywords.length() > 42) keywords = keywords.substring(0, 39) + "...";
                ctx.drawTextWithShadow(this.textRenderer, Text.literal(keywords),
                    x, kwInputY + row + row + 4, 0x888888);
            }
        }
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }
}
