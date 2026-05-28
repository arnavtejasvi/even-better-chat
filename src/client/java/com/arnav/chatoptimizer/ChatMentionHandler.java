package com.arnav.chatoptimizer;

import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

public final class ChatMentionHandler {
    private ChatMentionHandler() {}

    public static boolean containsMention(String raw, String playerName) {
        return raw.toLowerCase().contains(playerName.toLowerCase());
    }

    public static Text highlight(Text source, String playerName, int color) {
        Style highlightStyle = Style.EMPTY.withColor(color).withBold(true);
        return rewrite(source, playerName.toLowerCase(), highlightStyle);
    }

    private static MutableText rewrite(Text node, String lowerName, Style highlightStyle) {
        TextContent content = node.getContent();
        MutableText result;
        if (content instanceof PlainTextContent.Literal literal) {
            result = splitAndHighlight(literal.string(), node.getStyle(), lowerName, highlightStyle);
        } else {
            result = MutableText.of(content).setStyle(node.getStyle());
        }
        for (Text sibling : node.getSiblings()) {
            result.append(rewrite(sibling, lowerName, highlightStyle));
        }
        return result;
    }

    private static MutableText splitAndHighlight(String str, Style baseStyle, String lowerName, Style highlightStyle) {
        String lower = str.toLowerCase();
        int idx = lower.indexOf(lowerName);
        if (idx < 0) return Text.literal(str).setStyle(baseStyle); // fast path: no mention
        MutableText wrapper = Text.empty();
        int start = 0;
        do {
            if (idx > start) wrapper.append(Text.literal(str.substring(start, idx)).setStyle(baseStyle));
            wrapper.append(Text.literal(str.substring(idx, idx + lowerName.length())).setStyle(highlightStyle));
            start = idx + lowerName.length();
        } while ((idx = lower.indexOf(lowerName, start)) >= 0);
        if (start < str.length()) wrapper.append(Text.literal(str.substring(start)).setStyle(baseStyle));
        return wrapper;
    }
}
