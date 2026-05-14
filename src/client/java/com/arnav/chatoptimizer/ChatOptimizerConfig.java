/*
 * Decompiled with CFR 0.152.
 */
package com.arnav.chatoptimizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import java.util.Properties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(value=EnvType.CLIENT)
public final class ChatOptimizerConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("chatoptimizer.properties");
    private static final int DEFAULT_MAX_CHAT_HISTORY_ENTRIES = 32;
    public static volatile boolean showTimestamps = true;
    public static volatile boolean collapseDuplicateMessages = true;
    public static volatile boolean trimChatHistory = true;
    public static volatile int maxChatHistoryEntries = 32;
    public static volatile long renderRevision = 0L;

    private ChatOptimizerConfig() {
    }

    public static void load() {
        Properties properties = new Properties();
        if (Files.isRegularFile(CONFIG_PATH, new LinkOption[0])) {
            try (InputStream inputStream = Files.newInputStream(CONFIG_PATH, new OpenOption[0]);){
                properties.load(inputStream);
            }
            catch (IOException exception) {
                throw new IllegalStateException("Failed to load Chat Optimizer config", exception);
            }
        }
        showTimestamps = ChatOptimizerConfig.getBoolean(properties, "showTimestamps", true);
        collapseDuplicateMessages = ChatOptimizerConfig.getBoolean(properties, "collapseDuplicateMessages", true);
        trimChatHistory = ChatOptimizerConfig.getBoolean(properties, "trimChatHistory", true);
        maxChatHistoryEntries = ChatOptimizerConfig.getInt(properties, "maxChatHistoryEntries", 32, 8, 256);
    }

    public static void save() {
        Properties properties = new Properties();
        properties.setProperty("showTimestamps", Boolean.toString(showTimestamps));
        properties.setProperty("collapseDuplicateMessages", Boolean.toString(collapseDuplicateMessages));
        properties.setProperty("trimChatHistory", Boolean.toString(trimChatHistory));
        properties.setProperty("maxChatHistoryEntries", Integer.toString(maxChatHistoryEntries));
        try {
            Files.createDirectories(Objects.requireNonNull(CONFIG_PATH.getParent()), new FileAttribute[0]);
            try (OutputStream outputStream = Files.newOutputStream(CONFIG_PATH, new OpenOption[0]);){
                properties.store(outputStream, "Chat Optimizer configuration");
            }
        }
        catch (IOException exception) {
            throw new IllegalStateException("Failed to save Chat Optimizer config", exception);
        }
    }

    public static void setMaxChatHistoryEntries(int value) {
        maxChatHistoryEntries = Math.max(8, Math.min(256, value));
        ChatOptimizerConfig.markRenderDirty();
    }

    public static void setShowTimestamps(boolean value) {
        showTimestamps = value;
        ChatOptimizerConfig.markRenderDirty();
    }

    public static void setCollapseDuplicateMessages(boolean value) {
        collapseDuplicateMessages = value;
        ChatOptimizerConfig.markRenderDirty();
    }

    public static void setTrimChatHistory(boolean value) {
        trimChatHistory = value;
        ChatOptimizerConfig.markRenderDirty();
    }

    public static void markRenderDirty() {
        ++renderRevision;
    }

    private static boolean getBoolean(Properties properties, String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    private static int getInt(Properties properties, String key, int defaultValue, int min, int max) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Math.max(min, Math.min(max, Integer.parseInt(value)));
        }
        catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }
}

