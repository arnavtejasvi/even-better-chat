package com.arnav.chatoptimizer;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatUrlHandler {
    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\\s<>\"']+(?<![.,;:!?)])");

    private ChatUrlHandler() {}

    public static Component linkify(Component source) {
        return rewrite(source);
    }

    private static MutableComponent rewrite(Component node) {
        ComponentContents content = node.getContents();
        MutableComponent result;
        if (content instanceof PlainTextContents.LiteralContents literal) {
            result = splitAndLinkify(literal.text(), node.getStyle());
        } else {
            result = MutableComponent.create(content).setStyle(node.getStyle());
        }
        for (Component sibling : node.getSiblings()) {
            result.append(rewrite(sibling));
        }
        return result;
    }

    private static MutableComponent splitAndLinkify(String str, Style baseStyle) {
        Matcher m = URL_PATTERN.matcher(str);
        if (!m.find()) return Component.literal(str).setStyle(baseStyle);
        MutableComponent wrapper = Component.empty();
        int last = 0;
        do {
            if (m.start() > last) wrapper.append(Component.literal(str.substring(last, m.start())).setStyle(baseStyle));
            Style linkStyle = baseStyle.withUnderlined(true)
                .withClickEvent(new ClickEvent.OpenUrl(URI.create(m.group())));
            wrapper.append(Component.literal(m.group()).setStyle(linkStyle));
            last = m.end();
        } while (m.find());
        if (last < str.length()) wrapper.append(Component.literal(str.substring(last)).setStyle(baseStyle));
        return wrapper;
    }
}
