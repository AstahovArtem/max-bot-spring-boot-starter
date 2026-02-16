package io.github.astahovtech.maxbot.core.api;

import io.github.astahovtech.maxbot.core.model.BotUser;
import io.github.astahovtech.maxbot.core.outgoing.OutgoingMessage;

public interface MaxApi {

    void sendMessage(long chatId, String text);

    void sendMessage(long chatId, OutgoingMessage message);

    void editMessage(String messageId, String text);

    void editMessage(String messageId, OutgoingMessage message);

    void deleteMessage(String messageId);

    void answerCallback(String callbackId, String notification);

    void answerCallback(String callbackId, OutgoingMessage message);

    BotUser getMe();
}
