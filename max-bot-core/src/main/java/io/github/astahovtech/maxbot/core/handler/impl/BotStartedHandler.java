package io.github.astahovtech.maxbot.core.handler.impl;

import java.util.function.Consumer;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.model.Update;
import io.github.astahovtech.maxbot.core.model.UpdateType;

public final class BotStartedHandler implements Handler {

    private final Consumer<Ctx> action;

    public BotStartedHandler(Consumer<Ctx> action) {
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        this.action = action;
    }

    @Override
    public boolean supports(Update update) {
        return update.type() == UpdateType.BOT_STARTED;
    }

    @Override
    public void handle(Ctx ctx) {
        action.accept(ctx);
    }
}
