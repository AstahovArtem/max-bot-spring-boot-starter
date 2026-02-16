package io.github.astahovtech.maxbot.core;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.model.BotUser;
import io.github.astahovtech.maxbot.core.model.Update;
import io.github.astahovtech.maxbot.core.outgoing.OutgoingMessage;

public final class Ctx {

    private final MaxApi api;
    private final Update update;

    public Ctx(MaxApi api, Update update) {
        this.api = api;
        this.update = update;
    }

    public Update update() {
        return update;
    }

    public MaxApi api() {
        return api;
    }

    public long chatId() {
        return update.chatId();
    }

    public String messageId() {
        return update.messageId();
    }

    public String text() {
        return update.text();
    }

    public BotUser sender() {
        return update.sender();
    }

    public void reply(String text) {
        api.sendMessage(update.chatId(), text);
    }

    public void reply(OutgoingMessage message) {
        api.sendMessage(update.chatId(), message);
    }

    public void editMessage(String text) {
        requireMessageId();
        api.editMessage(update.messageId(), text);
    }

    public void editMessage(OutgoingMessage message) {
        requireMessageId();
        api.editMessage(update.messageId(), message);
    }

    public void deleteMessage() {
        requireMessageId();
        api.deleteMessage(update.messageId());
    }

    public void answerCallback(String notification) {
        requireCallbackId();
        api.answerCallback(update.callbackId(), notification);
    }

    public void answerCallbackWithMessage(OutgoingMessage message) {
        requireCallbackId();
        api.answerCallback(update.callbackId(), message);
    }

    private void requireMessageId() {
        if (update.messageId() == null) {
            throw new IllegalStateException("No messageId in update");
        }
    }

    private void requireCallbackId() {
        if (update.callbackId() == null) {
            throw new IllegalStateException("No callbackId in update");
        }
    }
}
