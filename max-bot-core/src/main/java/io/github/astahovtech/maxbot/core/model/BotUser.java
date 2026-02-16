package io.github.astahovtech.maxbot.core.model;

public record BotUser(
        long userId,
        String firstName,
        String lastName,
        String username,
        boolean isBot
) {
}
