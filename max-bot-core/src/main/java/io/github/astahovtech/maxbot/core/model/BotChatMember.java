package io.github.astahovtech.maxbot.core.model;

public record BotChatMember(
        long userId,
        String firstName,
        String lastName,
        String username,
        boolean isBot,
        boolean isOwner,
        boolean isAdmin,
        long joinTime
) {
}
