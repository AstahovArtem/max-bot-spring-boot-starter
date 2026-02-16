package io.github.astahovtech.maxbot.core.mapper;

import io.github.astahovtech.maxbot.core.model.BotUser;
import io.github.astahovtech.maxbot.core.model.Update;
import io.github.astahovtech.maxbot.core.model.UpdateType;
import ru.max.botapi.model.BotAddedToChatUpdate;
import ru.max.botapi.model.BotRemovedFromChatUpdate;
import ru.max.botapi.model.BotStartedUpdate;
import ru.max.botapi.model.Callback;
import ru.max.botapi.model.ChatTitleChangedUpdate;
import ru.max.botapi.model.Message;
import ru.max.botapi.model.MessageCallbackUpdate;
import ru.max.botapi.model.MessageChatCreatedUpdate;
import ru.max.botapi.model.MessageCreatedUpdate;
import ru.max.botapi.model.MessageEditedUpdate;
import ru.max.botapi.model.MessageRemovedUpdate;
import ru.max.botapi.model.Recipient;
import ru.max.botapi.model.User;
import ru.max.botapi.model.UserAddedToChatUpdate;
import ru.max.botapi.model.UserRemovedFromChatUpdate;

public final class UpdateMapper {

    public Update map(ru.max.botapi.model.Update apiUpdate) {
        return apiUpdate.map(new ru.max.botapi.model.Update.Mapper<>() {
            @Override
            public Update map(MessageCreatedUpdate u) {
                Message msg = u.getMessage();
                return Update.ofMessage(
                        extractChatId(msg),
                        extractMid(msg),
                        extractText(msg),
                        mapUser(msg.getSender()),
                        u.getTimestamp(),
                        u.getUserLocale()
                );
            }

            @Override
            public Update map(MessageCallbackUpdate u) {
                Callback cb = u.getCallback();
                Message msg = u.getMessage();
                return Update.ofCallback(
                        msg != null ? extractChatId(msg) : 0L,
                        msg != null ? extractMid(msg) : null,
                        msg != null ? extractText(msg) : null,
                        cb.getPayload(),
                        cb.getCallbackId(),
                        mapUser(cb.getUser()),
                        u.getTimestamp(),
                        u.getUserLocale()
                );
            }

            @Override
            public Update map(BotStartedUpdate u) {
                return Update.ofBotStarted(
                        u.getChatId(),
                        mapUser(u.getUser()),
                        u.getTimestamp(),
                        u.getPayload(),
                        u.getUserLocale()
                );
            }

            @Override
            public Update map(MessageEditedUpdate u) {
                return new Update(UpdateType.MESSAGE_EDITED, 0L, null, null,
                        null, null, null, u.getTimestamp(), null, null);
            }

            @Override
            public Update map(MessageRemovedUpdate u) {
                return new Update(UpdateType.MESSAGE_REMOVED, 0L, null, null,
                        null, null, null, u.getTimestamp(), null, null);
            }

            @Override
            public Update map(BotAddedToChatUpdate u) {
                return new Update(UpdateType.BOT_ADDED, u.getChatId(), null, null,
                        null, null, mapUser(u.getUser()), u.getTimestamp(), null, null);
            }

            @Override
            public Update map(BotRemovedFromChatUpdate u) {
                return new Update(UpdateType.BOT_REMOVED, u.getChatId(), null, null,
                        null, null, mapUser(u.getUser()), u.getTimestamp(), null, null);
            }

            @Override
            public Update map(UserAddedToChatUpdate u) {
                return new Update(UpdateType.USER_ADDED, u.getChatId(), null, null,
                        null, null, mapUser(u.getUser()), u.getTimestamp(), null, null);
            }

            @Override
            public Update map(UserRemovedFromChatUpdate u) {
                return new Update(UpdateType.USER_REMOVED, u.getChatId(), null, null,
                        null, null, mapUser(u.getUser()), u.getTimestamp(), null, null);
            }

            @Override
            public Update map(ChatTitleChangedUpdate u) {
                return new Update(UpdateType.CHAT_TITLE_CHANGED, u.getChatId(), null, null,
                        null, null, mapUser(u.getUser()), u.getTimestamp(), null, null);
            }

            @Override
            public Update map(MessageChatCreatedUpdate u) {
                return new Update(UpdateType.MESSAGE_CHAT_CREATED, 0L, null, null,
                        null, null, null, u.getTimestamp(), null, null);
            }

            @Override
            public Update mapDefault(ru.max.botapi.model.Update u) {
                return new Update(UpdateType.UNKNOWN, 0L, null, null,
                        null, null, null, u.getTimestamp(), null, null);
            }
        });
    }

    private static long extractChatId(Message msg) {
        if (msg == null || msg.getRecipient() == null) return 0L;
        Recipient r = msg.getRecipient();
        if (r.getChatId() != null) return r.getChatId();
        if (r.getUserId() != null) return r.getUserId();
        return 0L;
    }

    private static String extractMid(Message msg) {
        if (msg == null || msg.getBody() == null) return null;
        return msg.getBody().getMid();
    }

    private static String extractText(Message msg) {
        if (msg == null || msg.getBody() == null) return null;
        return msg.getBody().getText();
    }

    private static BotUser mapUser(User user) {
        if (user == null) return null;
        return new BotUser(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                Boolean.TRUE.equals(user.isBot())
        );
    }
}
