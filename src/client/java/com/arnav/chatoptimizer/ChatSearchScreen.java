package com.arnav.chatoptimizer;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public final class ChatSearchScreen extends Screen {
    private static final int LINE_H = 10;
    private static final int MAX_LINES = 14;

    private TextFieldWidget searchField;
    private List<String> results = new ArrayList<>();
    private int scrollOffset = 0;

    public ChatSearchScreen() {
        super(Text.translatable("screen.chatoptimizer.search.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.searchField = this.addDrawableChild(
            new TextFieldWidget(this.textRenderer, this.width / 2 - 150, 22, 300, 20, Text.empty()));
        this.searchField.setMaxLength(100);
        this.searchField.setChangedListener(q -> { scrollOffset = 0; results = ChatSearch.search(q); });
        this.searchField.setFocused(true);
        results = ChatSearch.search("");

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), btn -> this.close())
            .position(this.width / 2 - 50, this.height - 26).size(100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xC0000000);
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 7, 0xFFFFFF);

        int startY = 50;
        if (results.isEmpty()) {
            ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("screen.chatoptimizer.search.no_results"),
                this.width / 2, startY, 0x888888);
        } else {
            int end = Math.min(scrollOffset + MAX_LINES, results.size());
            for (int i = scrollOffset; i < end; i++) {
                String msg = results.get(i);
                if (this.textRenderer.getWidth(msg) > this.width - 20) {
                    // truncate to fit
                    while (msg.length() > 3 && this.textRenderer.getWidth(msg + "...") > this.width - 20) {
                        msg = msg.substring(0, msg.length() - 1);
                    }
                    msg = msg + "...";
                }
                ctx.drawTextWithShadow(this.textRenderer, Text.literal(msg),
                    10, startY + (i - scrollOffset) * LINE_H, 0xFFFFFF);
            }
            if (results.size() > MAX_LINES) {
                String info = (scrollOffset + 1) + "–" + Math.min(scrollOffset + MAX_LINES, results.size())
                    + " / " + results.size();
                ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(info),
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
