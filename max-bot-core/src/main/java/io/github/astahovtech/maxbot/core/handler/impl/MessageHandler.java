package io.github.astahovtech.maxbot.core.handler.impl;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.model.Update;

public final class MessageHandler implements Handler {

    private final Pattern pattern;
    private final Consumer<Ctx> action;

    public MessageHandler(Pattern pattern, Consumer<Ctx> action) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern must not be null");
        }
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        this.pattern = pattern;
        this.action = action;
    }

    public MessageHandler(String regex, Consumer<Ctx> action) {
        this(Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), action);
    }

    @Override
    public boolean supports(Update update) {
        if (update.text() == null) {
            return false;
        }
        return pattern.matcher(update.text()).find();
    }

    @Override
    public void handle(Ctx ctx) {
        action.accept(ctx);
    }
}
