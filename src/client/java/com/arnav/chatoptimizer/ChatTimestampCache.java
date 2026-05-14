/*
 * Decompiled with CFR 0.152.
 */
package com.arnav.chatoptimizer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.OrderedText;

@Environment(value=EnvType.CLIENT)
public final class ChatTimestampCache {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final ConcurrentHashMap<Long, TimestampEntry> CACHE = new ConcurrentHashMap();

    private ChatTimestampCache() {
    }

    public static TimestampEntry get(long timestampMinute) {
        return CACHE.computeIfAbsent(timestampMinute, ChatTimestampCache::createEntry);
    }

    private static TimestampEntry createEntry(long timestampMinute) {
        long timestampMillis = timestampMinute * 60000L;
        String formattedTimestamp = "[" + FORMATTER.format(Instant.ofEpochMilli(timestampMillis).atZone(ZoneId.systemDefault())) + "] ";
        MutableText text = Text.literal((String)formattedTimestamp).formatted(Formatting.DARK_GRAY);
        OrderedText orderedText = text.asOrderedText();
        return new TimestampEntry(orderedText, (Text)text);
    }

    @Environment(value=EnvType.CLIENT)
    public record TimestampEntry(OrderedText orderedText, Text text) {
        public int width(TextRenderer textRenderer) {
            return textRenderer.getWidth((StringVisitable)this.text);
        }
    }
}

