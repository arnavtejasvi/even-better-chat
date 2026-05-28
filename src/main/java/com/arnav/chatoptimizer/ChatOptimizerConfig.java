package com.arnav.chatoptimizer;

import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

public final class ChatOptimizerConfig {
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("chatoptimizer.properties");

    // General
    public static volatile boolean showTimestamps = true;
    public static volatile boolean collapseDuplicateMessages = true;
    public static volatile boolean trimChatHistory = true;
    public static volatile int maxChatHistoryEntries = 32;
    public static volatile boolean chatLoggingEnabled = false;

    // Appearance
    public static volatile TimestampFormat timestampFormat = TimestampFormat.H24;
    public static volatile int timestampColor = 0x555555;
    public static volatile BracketStyle bracketStyle = BracketStyle.SQUARE;

    // Filtering
    public static volatile boolean filterEnabled = false;
    public static Set<String> blockedPlayers = new HashSet<>();
    public static List<String> blockedKeywords = new ArrayList<>();

    public static volatile long renderRevision = 0L;

    public enum TimestampFormat { H24, H12 }
    public enum BracketStyle { SQUARE, ROUND, NONE }

    public static final int[] COLOR_VALUES = { 0x555555, 0xAAAAAA, 0xFFFFFF, 0xFFFF55, 0x55FF55, 0x55FFFF };
    public static final String[] COLOR_KEYS  = { "dark_gray", "gray", "white", "yellow", "green", "aqua" };

    private ChatOptimizerConfig() {}

    public static void load() {
        Properties p = new Properties();
        if (Files.isRegularFile(CONFIG_PATH, new LinkOption[0])) {
            try (InputStream in = Files.newInputStream(CONFIG_PATH, new OpenOption[0])) {
                p.load(in);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load Chat Optimizer config", e);
            }
        }
        showTimestamps             = getBool(p, "showTimestamps", true);
        collapseDuplicateMessages  = getBool(p, "collapseDuplicateMessages", true);
        trimChatHistory            = getBool(p, "trimChatHistory", true);
        maxChatHistoryEntries      = getInt(p,  "maxChatHistoryEntries", 32, 8, 256);
        chatLoggingEnabled         = getBool(p, "chatLoggingEnabled", false);
        timestampFormat            = getEnum(p, "timestampFormat", TimestampFormat.class, TimestampFormat.H24);
        timestampColor             = getHex(p,  "timestampColor", 0x555555);
        bracketStyle               = getEnum(p, "bracketStyle", BracketStyle.class, BracketStyle.SQUARE);
        filterEnabled              = getBool(p, "filterEnabled", false);
        blockedPlayers             = new HashSet<>(parseList(p.getProperty("blockedPlayers", "")));
        blockedKeywords            = new ArrayList<>(parseList(p.getProperty("blockedKeywords", "")));
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("showTimestamps",            Boolean.toString(showTimestamps));
        p.setProperty("collapseDuplicateMessages", Boolean.toString(collapseDuplicateMessages));
        p.setProperty("trimChatHistory",           Boolean.toString(trimChatHistory));
        p.setProperty("maxChatHistoryEntries",     Integer.toString(maxChatHistoryEntries));
        p.setProperty("chatLoggingEnabled",        Boolean.toString(chatLoggingEnabled));
        p.setProperty("timestampFormat",           timestampFormat.name());
        p.setProperty("timestampColor",            String.format("%06X", timestampColor));
        p.setProperty("bracketStyle",              bracketStyle.name());
        p.setProperty("filterEnabled",             Boolean.toString(filterEnabled));
        p.setProperty("blockedPlayers",            String.join(",", blockedPlayers));
        p.setProperty("blockedKeywords",           String.join(",", blockedKeywords));
        try {
            Files.createDirectories(Objects.requireNonNull(CONFIG_PATH.getParent()), new FileAttribute[0]);
            try (OutputStream out = Files.newOutputStream(CONFIG_PATH, new OpenOption[0])) {
                p.store(out, "Chat Optimizer configuration");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save Chat Optimizer config", e);
        }
    }

    public static void setShowTimestamps(boolean v)            { showTimestamps = v;            markRenderDirty(); }
    public static void setCollapseDuplicateMessages(boolean v) { collapseDuplicateMessages = v; markRenderDirty(); }
    public static void setTrimChatHistory(boolean v)           { trimChatHistory = v;           markRenderDirty(); }
    public static void setChatLoggingEnabled(boolean v)        { chatLoggingEnabled = v; }
    public static void setFilterEnabled(boolean v)             { filterEnabled = v; }

    public static void setMaxChatHistoryEntries(int v) {
        maxChatHistoryEntries = Math.max(8, Math.min(256, v));
        markRenderDirty();
    }

    public static void setTimestampFormat(TimestampFormat v) { timestampFormat = v; markRenderDirty(); }
    public static void setTimestampColor(int v)              { timestampColor = v;  markRenderDirty(); }
    public static void setBracketStyle(BracketStyle v)       { bracketStyle = v;    markRenderDirty(); }

    public static void markRenderDirty() { ++renderRevision; }

    private static boolean getBool(Properties p, String key, boolean def) {
        String v = p.getProperty(key);
        return v == null ? def : Boolean.parseBoolean(v);
    }

    private static int getInt(Properties p, String key, int def, int min, int max) {
        String v = p.getProperty(key);
        if (v == null) return def;
        try { return Math.max(min, Math.min(max, Integer.parseInt(v))); }
        catch (NumberFormatException e) { return def; }
    }

    private static <E extends Enum<E>> E getEnum(Properties p, String key, Class<E> cls, E def) {
        String v = p.getProperty(key);
        if (v == null) return def;
        try { return Enum.valueOf(cls, v); }
        catch (IllegalArgumentException e) { return def; }
    }

    private static int getHex(Properties p, String key, int def) {
        String v = p.getProperty(key);
        if (v == null) return def;
        try { return (int) Long.parseLong(v, 16); }
        catch (NumberFormatException e) { return def; }
    }

    private static List<String> parseList(String value) {
        List<String> result = new ArrayList<>();
        if (value == null || value.isBlank()) return result;
        for (String item : value.split(",")) {
            String t = item.trim();
            if (!t.isEmpty()) result.add(t);
        }
        return result;
    }
}
