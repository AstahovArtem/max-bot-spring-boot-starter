package io.github.astahovtech.maxbot.core.impl;

import java.util.ArrayList;
import java.util.List;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.exception.MaxBotException;
import io.github.astahovtech.maxbot.core.keyboard.Button;
import io.github.astahovtech.maxbot.core.keyboard.InlineKeyboard;
import io.github.astahovtech.maxbot.core.model.BotUser;
import io.github.astahovtech.maxbot.core.outgoing.OutgoingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.max.botapi.MaxBotAPI;
import ru.max.botapi.exceptions.APIException;
import ru.max.botapi.exceptions.ClientException;
import ru.max.botapi.model.AttachmentRequest;
import ru.max.botapi.model.BotInfo;
import ru.max.botapi.model.CallbackAnswer;
import ru.max.botapi.model.CallbackButton;
import ru.max.botapi.model.InlineKeyboardAttachmentRequest;
import ru.max.botapi.model.InlineKeyboardAttachmentRequestPayload;
import ru.max.botapi.model.LinkButton;
import ru.max.botapi.model.MessageLinkType;
import ru.max.botapi.model.NewMessageBody;
import ru.max.botapi.model.NewMessageLink;
import ru.max.botapi.model.TextFormat;

public final class RuMaxApi implements MaxApi {

    private static final Logger log = LoggerFactory.getLogger(RuMaxApi.class);

    private final MaxBotAPI botApi;

    public RuMaxApi(MaxBotAPI botApi) {
        this.botApi = botApi;
    }

    @Override
    public void sendMessage(long chatId, String text) {
        sendMessage(chatId, OutgoingMessage.text(text).build());
    }

    @Override
    public void sendMessage(long chatId, OutgoingMessage message) {
        try {
            NewMessageBody body = toNewMessageBody(message);
            botApi.sendMessage(body).chatId(chatId).execute();
        } catch (ClientException | APIException e) {
            throw new MaxBotException("sendMessage failed for chatId=" + chatId, e);
        }
    }

    @Override
    public void editMessage(String messageId, String text) {
        editMessage(messageId, OutgoingMessage.text(text).build());
    }

    @Override
    public void editMessage(String messageId, OutgoingMessage message) {
        try {
            NewMessageBody body = toNewMessageBody(message);
            botApi.editMessage(body, messageId).execute();
        } catch (ClientException | APIException e) {
            throw new MaxBotException("editMessage failed for messageId=" + messageId, e);
        }
    }

    @Override
    public void deleteMessage(String messageId) {
        try {
            botApi.deleteMessage(messageId).execute();
        } catch (ClientException | APIException e) {
            throw new MaxBotException("deleteMessage failed for messageId=" + messageId, e);
        }
    }

    @Override
    public void answerCallback(String callbackId, String notification) {
        try {
            CallbackAnswer answer = new CallbackAnswer().notification(notification);
            botApi.answerOnCallback(answer, callbackId).execute();
        } catch (ClientException | APIException e) {
            throw new MaxBotException("answerCallback failed for callbackId=" + callbackId, e);
        }
    }

    @Override
    public void answerCallback(String callbackId, OutgoingMessage message) {
        try {
            NewMessageBody body = toNewMessageBody(message);
            CallbackAnswer answer = new CallbackAnswer().message(body);
            botApi.answerOnCallback(answer, callbackId).execute();
        } catch (ClientException | APIException e) {
            throw new MaxBotException("answerCallback failed for callbackId=" + callbackId, e);
        }
    }

    @Override
    public BotUser getMe() {
        try {
            BotInfo info = botApi.getMyInfo().execute();
            return new BotUser(
                    info.getUserId(),
                    info.getFirstName(),
                    info.getLastName(),
                    info.getUsername(),
                    true
            );
        } catch (ClientException | APIException e) {
            throw new MaxBotException("getMe failed", e);
        }
    }

    private NewMessageBody toNewMessageBody(OutgoingMessage message) {
        List<AttachmentRequest> attachments = null;

        if (message.keyboard() != null) {
            attachments = new ArrayList<>();
            attachments.add(toKeyboardAttachment(message.keyboard()));
        }

        NewMessageLink link = null;
        if (message.replyToMessageId() != null) {
            link = new NewMessageLink(MessageLinkType.REPLY, message.replyToMessageId());
        }

        NewMessageBody body = new NewMessageBody(message.text(), attachments, link);

        if (!message.shouldNotify()) {
            body.notify(false);
        }
        if (message.format() != null) {
            body.format(switch (message.format()) {
                case MARKDOWN -> TextFormat.MARKDOWN;
                case HTML -> TextFormat.HTML;
            });
        }

        return body;
    }

    private InlineKeyboardAttachmentRequest toKeyboardAttachment(InlineKeyboard keyboard) {
        List<List<ru.max.botapi.model.Button>> apiButtons = new ArrayList<>();
        for (List<Button> row : keyboard.rows()) {
            List<ru.max.botapi.model.Button> apiRow = new ArrayList<>();
            for (Button btn : row) {
                apiRow.add(toApiButton(btn));
            }
            apiButtons.add(apiRow);
        }
        InlineKeyboardAttachmentRequestPayload payload =
                new InlineKeyboardAttachmentRequestPayload(apiButtons);
        return new InlineKeyboardAttachmentRequest(payload);
    }

    private ru.max.botapi.model.Button toApiButton(Button btn) {
        return switch (btn) {
            case Button.Callback cb -> new CallbackButton(cb.text(), cb.payload());
            case Button.Link lnk -> new LinkButton(lnk.text(), lnk.url());
        };
    }
}
