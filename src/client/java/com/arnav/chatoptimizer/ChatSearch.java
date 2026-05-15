package com.arnav.chatoptimizer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ChatSearch {
    private static final int MAX_MESSAGES = 200;
    private static final ArrayDeque<String> MESSAGES = new ArrayDeque<>();

    private ChatSearch() {}

    public static void addMessage(String plainText) {
        MESSAGES.addLast(plainText);
        while (MESSAGES.size() > MAX_MESSAGES) MESSAGES.removeFirst();
    }

    /** Returns messages matching the query, newest first. */
    public static List<String> search(String query) {
        String[] arr = MESSAGES.toArray(new String[0]);
        List<String> result = new ArrayList<>(arr.length);
        String lower = query == null ? "" : query.toLowerCase();
        for (int i = arr.length - 1; i >= 0; i--) {
            if (lower.isEmpty() || arr[i].toLowerCase().contains(lower)) {
                result.add(arr[i]);
            }
        }
        return result;
    }
}
