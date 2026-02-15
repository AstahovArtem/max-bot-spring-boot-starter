package io.github.astahovtech.maxbot.core;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.model.Update;

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

    public void reply(String text) {
        api.sendMessage(update.chatId(), text);
    }
}
