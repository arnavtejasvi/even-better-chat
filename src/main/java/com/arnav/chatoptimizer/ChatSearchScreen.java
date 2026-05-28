package com.arnav.chatoptimizer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class ChatSearchScreen extends Screen {
    private static final int LINE_H = 10;
    private static final int MAX_LINES = 14;

    private EditBox searchField;
    private List<String> results = new ArrayList<>();
    private int scrollOffset = 0;

    public ChatSearchScreen() {
        super(Component.translatable("screen.chatoptimizer.search.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.searchField = this.addRenderableWidget(
            new EditBox(this.font, this.width / 2 - 150, 22, 300, 20, Component.empty()));
        this.searchField.setMaxLength(100);
        this.searchField.setResponder(q -> { scrollOffset = 0; results = ChatSearch.search(q); });
        this.searchField.setFocused(true);
        results = ChatSearch.search("");

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), btn -> this.onClose())
            .pos(this.width / 2 - 50, this.height - 26).size(100, 20).build());
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xC0000000);
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredString(this.font, this.title, this.width / 2, 7, 0xFFFFFF);

        int startY = 50;
        if (results.isEmpty()) {
            ctx.drawCenteredString(this.font,
                Component.translatable("screen.chatoptimizer.search.no_results"),
                this.width / 2, startY, 0x888888);
        } else {
            int end = Math.min(scrollOffset + MAX_LINES, results.size());
            for (int i = scrollOffset; i < end; i++) {
                String msg = results.get(i);
                if (this.font.width(msg) > this.width - 20) {
                    while (msg.length() > 3 && this.font.width(msg + "...") > this.width - 20) {
                        msg = msg.substring(0, msg.length() - 1);
                    }
                    msg = msg + "...";
                }
                ctx.drawString(this.font, Component.literal(msg),
                    10, startY + (i - scrollOffset) * LINE_H, 0xFFFFFF);
            }
            if (results.size() > MAX_LINES) {
                String info = (scrollOffset + 1) + "–" + Math.min(scrollOffset + MAX_LINES, results.size())
                    + " / " + results.size();
                ctx.drawCenteredString(this.font, Component.literal(info),
                    this.width / 2, this.height - 46, 0xAAAAAA);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizAmount, double vertAmount) {
        int maxScroll = Math.max(0, results.size() - MAX_LINES);
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - vertAmount));
        return true;
    }
}
