package io.github.astahovtech.maxbot.core.api;

import java.io.File;
import java.util.List;

import io.github.astahovtech.maxbot.core.model.BotChat;
import io.github.astahovtech.maxbot.core.model.BotChatMember;
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

    String uploadImage(File file);

    String uploadVideo(File file);

    String uploadAudio(File file);

    String uploadFile(File file);

    BotChat getChat(long chatId);

    List<BotChatMember> getChatMembers(long chatId);

    void leaveChat(long chatId);
}
