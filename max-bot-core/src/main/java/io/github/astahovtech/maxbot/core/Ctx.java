package io.github.astahovtech.maxbot.core;

import java.io.File;
import java.util.List;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.model.Attachment;
import io.github.astahovtech.maxbot.core.model.BotChat;
import io.github.astahovtech.maxbot.core.model.BotChatMember;
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

    public MaxApi api() {
        return api;
    }

    public Update update() {
        return update;
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

    public String callbackData() {
        return update.callbackData();
    }

    public String callbackId() {
        return update.callbackId();
    }

    public String payload() {
        return update.payload();
    }

    public void reply(String text) {
        api.sendMessage(update.chatId(), text);
    }

    public void reply(OutgoingMessage message) {
        api.sendMessage(update.chatId(), message);
    }

    public void editMessage(String text) {
        api.editMessage(requireMessageId(), text);
    }

    public void editMessage(OutgoingMessage message) {
        api.editMessage(requireMessageId(), message);
    }

    public void deleteMessage() {
        api.deleteMessage(requireMessageId());
    }

    public void answerCallback(String notification) {
        api.answerCallback(requireCallbackId(), notification);
    }

    public void answerCallbackWithMessage(OutgoingMessage message) {
        api.answerCallback(requireCallbackId(), message);
    }

    public BotChat getChat() {
        return api.getChat(update.chatId());
    }

    public List<BotChatMember> getChatMembers() {
        return api.getChatMembers(update.chatId());
    }

    public void leaveChat() {
        api.leaveChat(update.chatId());
    }

    public String uploadImage(File file) {
        return api.uploadImage(file);
    }

    public String uploadVideo(File file) {
        return api.uploadVideo(file);
    }

    public String uploadAudio(File file) {
        return api.uploadAudio(file);
    }

    public String uploadFile(File file) {
        return api.uploadFile(file);
    }

    public void replyWithImage(String text, File imageFile) {
        String token = api.uploadImage(imageFile);
        reply(OutgoingMessage.text(text)
                .attach(Attachment.photo(token))
                .build());
    }

    private String requireMessageId() {
        String mid = update.messageId();
        if (mid == null) {
            throw new IllegalStateException("messageId is null for update type=" + update.type());
        }
        return mid;
    }

    private String requireCallbackId() {
        String cbId = update.callbackId();
        if (cbId == null) {
            throw new IllegalStateException("callbackId is null for update type=" + update.type());
        }
        return cbId;
    }
}
