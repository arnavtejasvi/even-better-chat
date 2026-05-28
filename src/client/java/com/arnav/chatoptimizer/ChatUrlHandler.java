package com.arnav.chatoptimizer;

import java.net.URI;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatUrlHandler {
    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\\s<>\"']+(?<![.,;:!?)])");

    private ChatUrlHandler() {}

    public static Text linkify(Text source) {
        return rewrite(source);
    }

    private static MutableText rewrite(Text node) {
        TextContent content = node.getContent();
        MutableText result;
        if (content instanceof PlainTextContent.Literal literal) {
            result = splitAndLinkify(literal.string(), node.getStyle());
        } else {
            result = MutableText.of(content).setStyle(node.getStyle());
        }
        for (Text sibling : node.getSiblings()) {
            result.append(rewrite(sibling));
        }
        return result;
    }

    private static MutableText splitAndLinkify(String str, Style baseStyle) {
        Matcher m = URL_PATTERN.matcher(str);
        if (!m.find()) return Text.literal(str).setStyle(baseStyle); // fast path: no URL
        MutableText wrapper = Text.empty();
        int last = 0;
        do {
            if (m.start() > last) wrapper.append(Text.literal(str.substring(last, m.start())).setStyle(baseStyle));
            Style linkStyle = baseStyle.withUnderline(true)
                .withClickEvent(new ClickEvent.OpenUrl(URI.create(m.group())));
            wrapper.append(Text.literal(m.group()).setStyle(linkStyle));
            last = m.end();
        } while (m.find());
        if (last < str.length()) wrapper.append(Text.literal(str.substring(last)).setStyle(baseStyle));
        return wrapper;
    }
}
