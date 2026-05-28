package com.arnav.chatoptimizer;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(value=EnvType.CLIENT)
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
            new EditBox(this.getFont(), this.width / 2 - 150, 22, 300, 20, Component.empty()));
        this.searchField.setMaxLength(100);
        this.searchField.setResponder(q -> { scrollOffset = 0; results = ChatSearch.search(q); });
        this.searchField.setFocused(true);
        results = ChatSearch.search("");

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), btn -> this.onClose())
            .pos(this.width / 2 - 50, this.height - 26).size(100, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xC0000000);
        super.extractRenderState(ctx, mouseX, mouseY, delta);
        ctx.text(this.getFont(), this.title,
            this.width / 2 - this.getFont().width(this.title) / 2, 7, 0xFFFFFF, true);

        int startY = 50;
        if (results.isEmpty()) {
            Component noResults = Component.translatable("screen.chatoptimizer.search.no_results");
            ctx.text(this.getFont(), noResults,
                this.width / 2 - this.getFont().width(noResults) / 2, startY, 0x888888, true);
        } else {
            int end = Math.min(scrollOffset + MAX_LINES, results.size());
            for (int i = scrollOffset; i < end; i++) {
                String msg = results.get(i);
                if (this.getFont().width(msg) > this.width - 20) {
                    while (msg.length() > 3 && this.getFont().width(msg + "...") > this.width - 20) {
                        msg = msg.substring(0, msg.length() - 1);
                    }
                    msg = msg + "...";
                }
                ctx.text(this.getFont(), Component.literal(msg), 10, startY + (i - scrollOffset) * LINE_H, 0xFFFFFF, true);
            }
            if (results.size() > MAX_LINES) {
                Component info = Component.literal(
                    (scrollOffset + 1) + "–" + Math.min(scrollOffset + MAX_LINES, results.size())
                    + " / " + results.size());
                ctx.text(this.getFont(), info,
                    this.width / 2 - this.getFont().width(info) / 2, this.height - 46, 0xAAAAAA, true);
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
