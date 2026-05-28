package com.arnav.chatoptimizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

@Environment(value=EnvType.CLIENT)
public final class ChatLogger {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ChatLogger() {}

    public static void log(Component message) {
        if (!ChatOptimizerConfig.chatLoggingEnabled) return;
        try {
            Path logFile = logFile();
            Files.createDirectories(logFile.getParent());
            String line = "[" + LocalTime.now().format(TIME_FMT) + "] " + message.getString()
                + System.lineSeparator();
            Files.writeString(logFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
        }
    }

    private static Path logFile() {
        String date = LocalDate.now().format(DATE_FMT);
        return FabricLoader.getInstance().getGameDir()
            .resolve("logs").resolve("chat").resolve("chat-" + date + ".log");
    }
}
