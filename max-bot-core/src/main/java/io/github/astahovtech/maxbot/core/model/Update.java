package io.github.astahovtech.maxbot.core.model;

public record Update(
        UpdateType type,
        long chatId,
        String messageId,
        String text,
        String callbackData,
        String callbackId,
        BotUser sender,
        long timestamp,
        String userLocale,
        String payload
) {

    public static Update ofMessage(long chatId, String messageId, String text,
                                   BotUser sender, long timestamp, String userLocale) {
        return new Update(UpdateType.MESSAGE_CREATED, chatId, messageId, text,
                null, null, sender, timestamp, userLocale, null);
    }

    public static Update ofCallback(long chatId, String messageId, String text,
                                    String callbackData, String callbackId,
                                    BotUser sender, long timestamp, String userLocale) {
        return new Update(UpdateType.MESSAGE_CALLBACK, chatId, messageId, text,
                callbackData, callbackId, sender, timestamp, userLocale, null);
    }

    public static Update ofBotStarted(long chatId, BotUser user, long timestamp,
                                      String payload, String userLocale) {
        return new Update(UpdateType.BOT_STARTED, chatId, null, null,
                null, null, user, timestamp, userLocale, payload);
    }

    public static Update of(long chatId, String text, String callbackData) {
        UpdateType type = callbackData != null
                ? UpdateType.MESSAGE_CALLBACK
                : UpdateType.MESSAGE_CREATED;
        return new Update(type, chatId, null, text, callbackData, null, null, 0L, null, null);
    }
}
