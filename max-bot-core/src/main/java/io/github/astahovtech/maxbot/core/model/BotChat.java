package io.github.astahovtech.maxbot.core.model;

public record BotChat(
        long chatId,
        String type,
        String status,
        String title,
        String description,
        int participantsCount,
        boolean isPublic,
        Long ownerId,
        String link
) {
}
