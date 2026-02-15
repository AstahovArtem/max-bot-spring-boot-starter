package io.github.astahovtech.maxbot.core.handler.impl;

import java.util.function.Consumer;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.model.Update;

public final class CallbackHandler implements Handler {

    private final String prefix;
    private final Consumer<Ctx> action;

    public CallbackHandler(String prefix, Consumer<Ctx> action) {
        if (prefix == null || prefix.isBlank()) {
            throw new IllegalArgumentException("prefix must not be blank");
        }
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        this.prefix = prefix;
        this.action = action;
    }

    @Override
    public boolean supports(Update update) {
        if (update.callbackData() == null) {
            return false;
        }
        return update.callbackData().startsWith(prefix);
    }

    @Override
    public void handle(Ctx ctx) {
        action.accept(ctx);
    }

    public String prefix() {
        return prefix;
    }
}
