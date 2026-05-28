package com.arnav.chatoptimizer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;

public final class ChatMentionHandler {
    private ChatMentionHandler() {}

    public static boolean containsMention(String raw, String playerName) {
        return raw.toLowerCase().contains(playerName.toLowerCase());
    }

    public static Component highlight(Component source, String playerName, int color) {
        Style highlightStyle = Style.EMPTY.withColor(color).withBold(true);
        return rewrite(source, playerName.toLowerCase(), highlightStyle);
    }

    private static MutableComponent rewrite(Component node, String lowerName, Style highlightStyle) {
        ComponentContents content = node.getContents();
        MutableComponent result;
        if (content instanceof PlainTextContents.LiteralContents literal) {
            result = splitAndHighlight(literal.text(), node.getStyle(), lowerName, highlightStyle);
        } else {
            result = MutableComponent.create(content).setStyle(node.getStyle());
        }
        for (Component sibling : node.getSiblings()) {
            result.append(rewrite(sibling, lowerName, highlightStyle));
        }
        return result;
    }

    private static MutableComponent splitAndHighlight(String str, Style baseStyle, String lowerName, Style highlightStyle) {
        String lower = str.toLowerCase();
        int idx = lower.indexOf(lowerName);
        if (idx < 0) return Component.literal(str).setStyle(baseStyle);
        MutableComponent wrapper = Component.empty();
        int start = 0;
        do {
            if (idx > start) wrapper.append(Component.literal(str.substring(start, idx)).setStyle(baseStyle));
            wrapper.append(Component.literal(str.substring(idx, idx + lowerName.length())).setStyle(highlightStyle));
            start = idx + lowerName.length();
        } while ((idx = lower.indexOf(lowerName, start)) >= 0);
        if (start < str.length()) wrapper.append(Component.literal(str.substring(start)).setStyle(baseStyle));
        return wrapper;
    }
}
