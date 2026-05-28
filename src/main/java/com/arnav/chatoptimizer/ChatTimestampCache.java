package com.arnav.chatoptimizer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public final class ChatTimestampCache {
    private static final ConcurrentHashMap<Long, TimestampEntry> CACHE = new ConcurrentHashMap<>();
    private static volatile long cacheRevision = -1L;

    private ChatTimestampCache() {}

    public static TimestampEntry get(long timestampMinute) {
        long rev = ChatOptimizerConfig.renderRevision;
        if (cacheRevision != rev) {
            CACHE.clear();
            cacheRevision = rev;
        }
        return CACHE.computeIfAbsent(timestampMinute, ChatTimestampCache::createEntry);
    }

    private static TimestampEntry createEntry(long timestampMinute) {
        long millis = timestampMinute * 60_000L;
        String pattern = ChatOptimizerConfig.timestampFormat == ChatOptimizerConfig.TimestampFormat.H12
            ? "hh:mm a" : "HH:mm";
        String time = DateTimeFormatter.ofPattern(pattern)
            .format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()));

        String formatted = switch (ChatOptimizerConfig.bracketStyle) {
            case ROUND -> "(" + time + ") ";
            case NONE  -> time + " ";
            default    -> "[" + time + "] ";
        };

        MutableComponent text = Component.literal(formatted)
            .withStyle(style -> style.withColor(ChatOptimizerConfig.timestampColor));
        return new TimestampEntry(text.getVisualOrderText(), text);
    }

    public record TimestampEntry(FormattedCharSequence orderedText, Component text) {}
}
