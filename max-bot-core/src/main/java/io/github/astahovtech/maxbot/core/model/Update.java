package io.github.astahovtech.maxbot.core.model;

/**
 * Incoming update from the messenger.
 *
 * @param chatId       chat identifier
 * @param text         message text (may be {@code null})
 * @param callbackData callback payload from inline-button press (may be {@code null})
 */
public record Update(long chatId, String text, String callbackData) {}
