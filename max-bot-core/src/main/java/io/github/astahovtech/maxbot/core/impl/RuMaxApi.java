package io.github.astahovtech.maxbot.core.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.exception.MaxBotException;
import io.github.astahovtech.maxbot.core.keyboard.Button;
import io.github.astahovtech.maxbot.core.keyboard.InlineKeyboard;
import io.github.astahovtech.maxbot.core.model.Attachment;
import io.github.astahovtech.maxbot.core.model.BotChat;
import io.github.astahovtech.maxbot.core.model.BotChatMember;
import io.github.astahovtech.maxbot.core.model.BotUser;
import io.github.astahovtech.maxbot.core.outgoing.OutgoingMessage;
import io.github.astahovtech.maxbot.core.retry.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.max.botapi.MaxBotAPI;
import ru.max.botapi.MaxUploadAPI;
import ru.max.botapi.exceptions.APIException;
import ru.max.botapi.exceptions.ClientException;
import ru.max.botapi.model.AttachmentRequest;
import ru.max.botapi.model.AudioAttachmentRequest;
import ru.max.botapi.model.BotInfo;
import ru.max.botapi.model.CallbackAnswer;
import ru.max.botapi.model.CallbackButton;
import ru.max.botapi.model.Chat;
import ru.max.botapi.model.ChatMember;
import ru.max.botapi.model.ChatMembersList;
import ru.max.botapi.model.FileAttachmentRequest;
import ru.max.botapi.model.InlineKeyboardAttachmentRequest;
import ru.max.botapi.model.InlineKeyboardAttachmentRequestPayload;
import ru.max.botapi.model.LinkButton;
import ru.max.botapi.model.MessageLinkType;
import ru.max.botapi.model.NewMessageBody;
import ru.max.botapi.model.NewMessageLink;
import ru.max.botapi.model.PhotoAttachmentRequest;
import ru.max.botapi.model.PhotoAttachmentRequestPayload;
import ru.max.botapi.model.TextFormat;
import ru.max.botapi.model.UploadEndpoint;
import ru.max.botapi.model.UploadType;
import ru.max.botapi.model.UploadedInfo;
import ru.max.botapi.model.VideoAttachmentRequest;

public final class RuMaxApi implements MaxApi {

    private static final Logger log = LoggerFactory.getLogger(RuMaxApi.class);

    private final MaxBotAPI botApi;
    private final MaxUploadAPI uploadApi;
    private final RetryPolicy retryPolicy;

    public RuMaxApi(MaxBotAPI botApi) {
        this(botApi, null, RetryPolicy.noRetry());
    }

    public RuMaxApi(MaxBotAPI botApi, MaxUploadAPI uploadApi) {
        this(botApi, uploadApi, RetryPolicy.defaultPolicy());
    }

    public RuMaxApi(MaxBotAPI botApi, MaxUploadAPI uploadApi, RetryPolicy retryPolicy) {
        this.botApi = botApi;
        this.uploadApi = uploadApi;
        this.retryPolicy = retryPolicy != null ? retryPolicy : RetryPolicy.noRetry();
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

    @Override
    public String uploadImage(File file) {
        requireUploadApi();
        return retryPolicy.execute(() -> {
            try {
                UploadEndpoint endpoint = botApi.getUploadUrl(UploadType.IMAGE).execute();
                UploadedInfo info = uploadApi.uploadFile(endpoint.getUrl(), file).execute();
                return info.getToken();
            } catch (ClientException | APIException | FileNotFoundException e) {
                throw new MaxBotException("uploadImage failed", e);
            }
        });
    }

    @Override
    public String uploadVideo(File file) {
        requireUploadApi();
        return retryPolicy.execute(() -> {
            try {
                UploadEndpoint endpoint = botApi.getUploadUrl(UploadType.VIDEO).execute();
                uploadApi.uploadFile(endpoint.getUrl(), file).execute();
                return endpoint.getToken();
            } catch (ClientException | APIException | FileNotFoundException e) {
                throw new MaxBotException("uploadVideo failed", e);
            }
        });
    }

    @Override
    public String uploadAudio(File file) {
        requireUploadApi();
        return retryPolicy.execute(() -> {
            try {
                UploadEndpoint endpoint = botApi.getUploadUrl(UploadType.AUDIO).execute();
                uploadApi.uploadFile(endpoint.getUrl(), file).execute();
                return endpoint.getToken();
            } catch (ClientException | APIException | FileNotFoundException e) {
                throw new MaxBotException("uploadAudio failed", e);
            }
        });
    }

    @Override
    public String uploadFile(File file) {
        requireUploadApi();
        return retryPolicy.execute(() -> {
            try {
                UploadEndpoint endpoint = botApi.getUploadUrl(UploadType.FILE).execute();
                UploadedInfo info = uploadApi.uploadFile(endpoint.getUrl(), file).execute();
                return info.getToken();
            } catch (ClientException | APIException | FileNotFoundException e) {
                throw new MaxBotException("uploadFile failed", e);
            }
        });
    }

    @Override
    public BotChat getChat(long chatId) {
        try {
            Chat chat = botApi.getChat(chatId).execute();
            return new BotChat(
                    chat.getChatId(),
                    chat.getType() != null ? chat.getType().getValue() : null,
                    chat.getStatus() != null ? chat.getStatus().getValue() : null,
                    chat.getTitle(),
                    chat.getDescription(),
                    chat.getParticipantsCount(),
                    Boolean.TRUE.equals(chat.isPublic()),
                    chat.getOwnerId(),
                    chat.getLink()
            );
        } catch (ClientException | APIException e) {
            throw new MaxBotException("getChat failed for chatId=" + chatId, e);
        }
    }

    @Override
    public List<BotChatMember> getChatMembers(long chatId) {
        try {
            ChatMembersList membersList = botApi.getMembers(chatId).execute();
            List<BotChatMember> result = new ArrayList<>();
            if (membersList.getMembers() != null) {
                for (ChatMember m : membersList.getMembers()) {
                    result.add(new BotChatMember(
                            m.getUserId(),
                            m.getFirstName(),
                            m.getLastName(),
                            m.getUsername(),
                            Boolean.TRUE.equals(m.isBot()),
                            Boolean.TRUE.equals(m.isOwner()),
                            Boolean.TRUE.equals(m.isAdmin()),
                            m.getJoinTime()
                    ));
                }
            }
            return result;
        } catch (ClientException | APIException e) {
            throw new MaxBotException("getChatMembers failed for chatId=" + chatId, e);
        }
    }

    @Override
    public void leaveChat(long chatId) {
        try {
            botApi.leaveChat(chatId).execute();
        } catch (ClientException | APIException e) {
            throw new MaxBotException("leaveChat failed for chatId=" + chatId, e);
        }
    }

    private void requireUploadApi() {
        if (uploadApi == null) {
            throw new MaxBotException("MaxUploadAPI not configured. Upload operations are not available.");
        }
    }

    private NewMessageBody toNewMessageBody(OutgoingMessage message) {
        List<AttachmentRequest> attachments = null;

        if (message.keyboard() != null) {
            if (attachments == null) attachments = new ArrayList<>();
            attachments.add(toKeyboardAttachment(message.keyboard()));
        }

        if (message.attachments() != null) {
            if (attachments == null) attachments = new ArrayList<>();
            for (Attachment att : message.attachments()) {
                attachments.add(toAttachmentRequest(att));
            }
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

    private AttachmentRequest toAttachmentRequest(Attachment att) {
        return switch (att) {
            case Attachment.Photo p -> new PhotoAttachmentRequest(
                    new PhotoAttachmentRequestPayload().token(p.token()));
            case Attachment.Video v -> new VideoAttachmentRequest(new UploadedInfo().token(v.token()));
            case Attachment.Audio a -> new AudioAttachmentRequest(new UploadedInfo().token(a.token()));
            case Attachment.BotFile f -> new FileAttachmentRequest(new UploadedInfo().token(f.token()));
        };
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
