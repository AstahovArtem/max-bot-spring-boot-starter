package io.github.astahovtech.maxbot.core.impl;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import ru.max.botapi.MaxBotAPI;
import ru.max.botapi.exceptions.APIException;
import ru.max.botapi.exceptions.ClientException;
import ru.max.botapi.model.NewMessageBody;

public final class RuMaxApi implements MaxApi {

    private final MaxBotAPI botApi;

    public RuMaxApi(MaxBotAPI botApi) {
        this.botApi = botApi;
    }

    @Override
    public void sendMessage(long chatId, String text) {
        try {
            var body = new NewMessageBody(text, null, null);
            botApi.sendMessage(body)
                    .chatId(chatId)
                    .execute();
        } catch (ClientException | APIException e) {
            throw new RuntimeException("MAX API sendMessage failed", e);
        }
    }
}
