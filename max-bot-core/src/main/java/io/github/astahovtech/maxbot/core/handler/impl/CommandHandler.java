package io.github.astahovtech.maxbot.core.handler.impl;

import java.util.function.Consumer;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.model.Update;

public final class CommandHandler implements Handler {

    private final String command;
    private final Consumer<Ctx> action;

    public CommandHandler(String command, Consumer<Ctx> action) {
        if (command == null || command.isBlank()) {
            throw new IllegalArgumentException("command must not be blank");
        }
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        String normalized = command.strip();
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("command must not be blank");
        }
        this.command = normalized;
        this.action = action;
    }

    @Override
    public boolean supports(Update update) {
        if (update.text() == null) {
            return false;
        }
        String text = update.text().trim();
        String slash = "/" + command;
        return text.equals(slash) || text.startsWith(slash + " ");
    }

    @Override
    public void handle(Ctx ctx) {
        action.accept(ctx);
    }
}
